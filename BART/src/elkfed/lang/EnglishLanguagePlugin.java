/*
 * Copyright 2008 Yannick Versley / Univ. Tuebingen
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package elkfed.lang;

import edu.stanford.nlp.trees.HeadFinder;
import edu.stanford.nlp.trees.Tree;
import elkfed.config.ConfigProperties;
import elkfed.knowledge.NameDataBase;
import elkfed.knowledge.SemanticClass;
import elkfed.knowledge.WNInterface;
import elkfed.mmax.MarkableLevels;
import elkfed.mmax.util.NPHeadFinder;
import elkfed.nlp.util.Gender;
import static elkfed.mmax.MarkableLevels.*;
import static elkfed.lang.EnglishLinguisticConstants.*;
import static elkfed.mmax.pipeline.MarkableCreator.LABEL_ATTRIBUTE;
import elkfed.mmax.minidisc.MiniDiscourse;
import elkfed.mmax.minidisc.Markable;
import elkfed.mmax.minidisc.MarkableLevel;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import elkfed.coref.mentions.Mention;

/**
 *
 * @author versley
 */
public class EnglishLanguagePlugin implements LanguagePlugin {

    private static final String IRRELEVANT_TOKEN_POS = "(cc|in)";

    protected final Map<LanguagePlugin.TableName,Map<String,String>> aliasTables =
            new EnumMap(LanguagePlugin.TableName.class);

    public EnglishLanguagePlugin() {
        readMapping(TableName.AdjMap, "adj_map_en.txt");
    }

     /** Gets whether the markable is a proper name or not
     * (based on) capitalization
     */
    private boolean calcIsProperName(Markable markable)  {
        //TODO: This gives wrong results for markables at the start of a
        //      sentence (which are always capitalised).
        //      Either (i) check for sentence start - this would mean
        //      that we can only run this after MarkableFactory has set the
        //      sentence offset information or (ii) ignore the capitalisation
        //      of the first token unless it is tagged as NNP
        final String[] tokens = markable.getDiscourseElements();
        final String[] pos = markable.getAttributeValue(DEFAULT_POS_LEVEL).split(" ");

        if (tokens.length==1)
        {
            String tok0=tokens[0];
            String pos0=pos[0];
            if (pos0.startsWith("nnp")) return true;
            if (!Character.isUpperCase(tok0.charAt(0)))
                return false;
            if (pos0.equals("jj")) return true;
            for (int i=1; i<tokens[0].length(); i++) {
                if (Character.isUpperCase(tok0.charAt(i))) {
                    return true;
                }
            }
            return false;
        }

        for (int token = 0; token < tokens.length; token++) {
            if (!Character.isUpperCase(tokens[token].charAt(0)))
            {
                if (!pos[token].matches(IRRELEVANT_TOKEN_POS)) 
                    return false;
                
            }
        }
        return true;
    }

    /**
     * Start Singular/Plural  Determination
     *  Gets the number of a markable as a boolean (is singular?)
     *
     **/
    private boolean calcIsSingular(final Markable markable) {
        // if pronoun, check sing/plural pronoun
        if (markable.getDiscourseElementIDs().length == 1 &&
                calcMarkableString(markable).toLowerCase().matches(PRONOUN)) {
            return calcMarkableString(markable).toLowerCase().matches(SINGULAR_PRONOUN_ADJ);
        } // else, look whether the morphological root of the head noun
        // matches the lemma --- we assume an unknown ("<unknown>") or
        // cardinal (@card@) generated by tree-tagger to make return true
        // TODO: maybe do something more intelligent here?
        // i.e., check for NNS/NNPS or CD (indicate plural)
        else {
            final NPHeadFinder hf=NPHeadFinder.getInstance();
            return hf.getHead(markable).toLowerCase().equals(
                    hf.getHeadLemma(markable));
        }
    }

    /**
     * Determine whether mention is a definite
     */
    public boolean calcIsDnewDeterminer(String markableString)
    {
       String lower=markableString.toLowerCase();
       if (lower==null) return false;
       if (lower.startsWith("the") && lower.length()>4) lower=lower.substring(4);
       if (lower.startsWith("another")) return true;
       if (lower.startsWith("other")) return true;
       if (lower.startsWith("each")) return true;
       if (lower.startsWith("every")) return true;
       if (lower.startsWith("no")) return true;
       if (lower.startsWith("any")) return true;
       if (lower.startsWith("many")) return true;
       if (lower.startsWith("some")) return true;
       if (lower.startsWith("most")) return true;
       return false;
    }
    
    public boolean calcIsDefinite(String markableString)
    {
        return markableString.toLowerCase().startsWith(DEF_ARTICLE+" ");
    }
    
    public boolean calcIsIndefinite(String markableString)
    {
        String lower=markableString.toLowerCase();
        return (lower.startsWith("a ") || lower.startsWith("an "));
    }

    private boolean calcSpeechPronoun(String markableString) {
        String lower=markableString.toLowerCase();
        return EnglishLinguisticConstants.FIRST_SECOND_PERSON_RE
                .matcher(lower).matches();
    }
    

    /** Checks whether the anaphora starts with a demonstrative */
    private boolean startsWithDemonstrative(String markableString) {
        String lstr=markableString.toLowerCase();
        for (String demonstrative : DEMONSTRATIVES) {
            if (lstr.startsWith(demonstrative+" ")) {
                return true;
            }
        }
        return false;
    }

    protected String calcMarkableString(final Markable markable) {
        return new StringBuffer(markable.toString()).
                deleteCharAt(markable.toString().length()-1).deleteCharAt(0).toString();
    }

    /** Determine whether mention is a pronoun
     */
    private boolean calcPronoun(Markable markable) {
        return (markable.getDiscourseElementIDs().length == 1 &&
                calcMarkableString(markable).toLowerCase().matches(PRONOUN) &&
                !calcMarkableString(markable).equals("US"));
    }

    /**  Distinct types of pronouns */
    /* Reflexives */
    private boolean calcReflPronoun(Markable markable) {
        return (markable.getDiscourseElementIDs().length == 1 &&
                calcMarkableString(markable).toLowerCase().matches(REFLEXIVE_PRONOUN));
    }

    /* Possessives */
    private boolean calcPossPronoun(Markable markable) {
        return (markable.getDiscourseElementIDs().length == 1 &&
                calcMarkableString(markable).toLowerCase().matches(POSSESSIVE_PRONOUN));
    }

    private boolean startsWithPossPronoun(String markableString) {
       String lstr=markableString.toLowerCase();
        for (String pro : POSSESSIVE_PRONOUNS) {
            if (lstr.startsWith(pro+" ")) {
                return true;
            }
        }
        return false;
    }

    /* Personal */
    private boolean calcPersPronoun(Markable markable) {
        return (markable.getDiscourseElementIDs().length == 1 &&
                (calcMarkableString(markable).toLowerCase().matches(PERSONAL_PRONOUN_NOM) ||
                calcMarkableString(markable).toLowerCase().matches(PERSONAL_PRONOUN_ACCUSATIVE)));
    }

    /**
     * Start of gender determination
     *
     * Determines the gender of a markable */
    private Gender calcGender(final Markable markable, final SemanticClass semClass) {
        Gender dbLookup = null;
        Gender semClassLookup = null;
        final String markableString = calcMarkableString(markable);
        final String[] markableTokens = markableString.split(" ");

        // 1. check first for pronouns
        if (markableString.toLowerCase().matches(MALE_PRONOUN_ADJ)) {
            return Gender.MALE;
        } else if (markableString.toLowerCase().matches(FEMALE_PRONOUN_ADJ)) {
            return Gender.FEMALE;
        } else if (markableString.toLowerCase().matches(NEUTRAL_PRONOUN_ADJ)) {
            return Gender.NEUTRAL;
        } else if (markableString.toLowerCase().matches(PLURAL_PRONOUN_ADJ)) {
            return Gender.PLURAL;
        } // 2. else check for designators
        else if (markableTokens[0].toLowerCase().matches(MALE_DESIGNATOR)) {
            return Gender.MALE;
        } else if (markableTokens[0].toLowerCase().matches(FEMALE_DESIGNATOR)) {
            return Gender.FEMALE;
        } /*
        // 3. else look at other mentions
        else if ((Gender otherMentions = getOtherMentions()) != null)
        { return otherMentions; }
         */ // 4. else do a database lookup
        else if ((dbLookup = getDBLookup(markable)) != null) {
            return dbLookup;
        } // 5. else decide based on semantic class
        else if ((semClassLookup = getSemClassLookup(semClass)) != null) {
            return semClassLookup;
        } // else return "unknown"
        else {
            return Gender.UNKNOWN;
        }
    }

    /** Looks up in the database of common first names for a
     *  name given in a Markable. Assumes the name comes before the
     *  surname and it's the first string in the markable text,
     */
    private Gender getDBLookup(final Markable markable) {
        Gender g=NameDataBase.getInstance().lookup(markable);
        return g;
    }

    /** Looks up for the gender based on semantic class */
    private Gender getSemClassLookup(final SemanticClass semClass) {

        Gender tempR = Gender.UNKNOWN;
        if (semClass.equals(SemanticClass.MALE)) {
            tempR = Gender.MALE;
        } else if (semClass.equals(SemanticClass.FEMALE)) {
            tempR = Gender.FEMALE;
        } else if (SemanticClass.isaObject(semClass)) {
            tempR = Gender.NEUTRAL;
        } else if (semClass.equals(SemanticClass.UNKNOWN)) {
            tempR = Gender.UNKNOWN;
        }
        return tempR;
    }

    /**************************/
    /**End Gender Determination**/
    /**Determine mentions semantic class
     */
    private SemanticClass calcSemClass(Markable markable) {
        SemanticClass semclass;
        if (markable.getAttributeValue("type").equals("enamex")) {
            semclass=SemanticClass.getFromString(
                    markable.getAttributeValue("label"));
        } else {
            semclass=WNInterface.getInstance().getSemanticClass(
                    NPHeadFinder.getInstance().getHeadLemma(markable));
//            if (semclass==SemanticClass.UNKNOWN &&
//                    markable.getAttributeValue("label")!=null) {
//                semclass=SemanticClass.getFromString(
//                    markable.getAttributeValue("label"));
//            }
        }
        if (semclass == null) {
            semclass = SemanticClass.UNKNOWN;
        }
        return semclass;
    }

    public MentionType calcMentionType(Markable markable) {
        MentionType result=new MentionType();
        String markable_string=calcMarkableString(markable);
        //Enumeration
        //isPropername

        boolean is_nominal=true;

        if(calcIsProperName(markable)) {
            result.features.add(MentionType.Features.isProperName);
            is_nominal=false;
        }
        if (markable.getAttributeValue("type").equals("enamex")) {
            result.features.add(MentionType.Features.isEnamex);
            is_nominal=false;
        } else if (findCoord(markable)) {
            /* we treat all NPs as coordinated that
             * (a) have a CC in them (e.g., Peter and Mary) but
             * (b) aren't Enamex (i.e., not something like AT & T)
             */
            result.features.add(MentionType.Features.isCoord);
        }

        //isDefinite
        if (calcIsDefinite(markable_string))
        {
            result.features.add(MentionType.Features.isNominal);
            result.features.add(MentionType.Features.isDefinite);
        } else if (calcIsIndefinite(markable_string)) {
            result.features.add(MentionType.Features.isNominal);
            result.features.add(MentionType.Features.isIndefinite);
        }
        if (calcIsDnewDeterminer(markable_string)) 
            result.features.add(MentionType.Features.isDnewDeterminer);


        //isDemonstrative
        if (startsWithDemonstrative(markable_string)) {
            result.features.add(MentionType.Features.isDemonstrative);
            if (markable.getDiscourseElementIDs().length == 1) {
                result.features.add(MentionType.Features.isDemPronoun);
            } else {
                result.features.add(MentionType.Features.isNominal);
                result.features.add(MentionType.Features.isDemNominal);
            }
        }

        //is poss NP ("her N")
        if (startsWithPossPronoun(markable_string) && (markable.getDiscourseElementIDs().length > 1)) 
            result.features.add(MentionType.Features.isPossNominal);
        

        //isPronoun
        if (calcPronoun(markable)) {
            is_nominal=false;
            result.features.add(MentionType.Features.isPronoun);
            // and its subtypes (MP)
            if (calcReflPronoun(markable)) {
                result.features.add(MentionType.Features.isReflexive);
            }
            if (calcPossPronoun(markable)) {
                result.features.add(MentionType.Features.isPossPronoun);
            }
            if (calcPersPronoun(markable)) {
                result.features.add(MentionType.Features.isPersPronoun);
                if (calcSpeechPronoun(markable_string)) {
                    result.features.add(MentionType.Features.isFirstSecondPerson);
                }
            }
        }
        if (calcIsSingular(markable)) {
            result.features.add(MentionType.Features.isSingular);
        }
        result.semanticClass = calcSemClass(markable);
        result.gender = calcGender(markable, result.semanticClass);
        if (result.semanticClass==SemanticClass.UNKNOWN) {
            if (result.gender==Gender.MALE) {
                result.semanticClass=SemanticClass.MALE;
            } if (result.gender==Gender.FEMALE) {
                result.semanticClass=SemanticClass.FEMALE;
            }
        }
        if (is_nominal) 
            result.features.add(MentionType.Features.isNominal);

        return result;
    }

    public NPHeadFinder getHeadFinder() {
        return NPHeadFinder.getInstance();
    }

    public String getHead(Markable m) {
        return getHeadFinder().getHead(m);
    }
    public String getHeadLemma(Markable m) {
        return getHeadFinder().getHeadLemma(m);
    }
    protected static final Pattern unwantedL_re = Pattern.compile(String.format("%s|%s", ARTICLE, PUNCTUATION_MARK),
            Pattern.CASE_INSENSITIVE);

    public boolean unwanted_left(String tok) {
        return unwantedL_re.matcher(tok).matches();
    }
    
    protected static final Pattern unwantedR_re =
            Pattern.compile(String.format("%s|%s",SAXON_GENITIVE,
                PUNCTUATION_MARK));
    public boolean unwanted_right(String tok) {
        return unwantedR_re.matcher(tok).matches();
    }


    private Boolean iscoordnp(Tree np) {
// helper -- checks that a parse np-tree is in fact coordination (contains CC on the highest level)
      if (np==null) return false;
      if (!np.value().equalsIgnoreCase("NP")) return false;
      Tree[] chlds=np.children();
      for (int i=0; i<chlds.length; i++) {
        if (chlds[i].value().equalsIgnoreCase("CC")) return true;
      }
      return false;
    }
  


    public Tree[] calcParseExtra(Tree sentenceTree,
            int startWord, int endWord, Tree prsHead,
            HeadFinder StHeadFinder) {

       List<Tree> Leaves = sentenceTree.getLeaves();
        Tree startNode = Leaves.get(startWord);

        Tree endNode=null;

        if (endWord>=Leaves.size()) {
// for marks that do not respect sentence boundaries
         endNode=Leaves.get(Leaves.size()-1);
        }else{
         endNode = Leaves.get(endWord);
        }

        Tree prevNode = null;
        if (startWord>0) prevNode = Leaves.get(startWord-1);
        Tree nextNode = null;
        if (endWord < Leaves.size()-1) nextNode=Leaves.get(endWord+1);


       Tree[] result=new Tree[3];

//---------- calculate minimal np-like subtree, containing the head and included in the mention


       Tree HeadNode=prsHead;
       if (prsHead==null) {
// todo: this should be fixed somehow though
// todo (ctd): use getHeadIndex from NPHeadFinder, but need to reconstruct the markable
// todo (ctd): mind marks spanning over sentene boundaries

         result[0]=null;
         result[1]=null;
         result[2]=null;
         return result;
       }



       Tree mincand=prsHead;
       Tree t=mincand;
       Tree minnp=null;
       Tree maxnp=null;


       while(t!=null &&
             (prevNode == null || !t.dominates(prevNode)) &&
             (nextNode == null || !t.dominates(nextNode))) {
         if (t.value().equalsIgnoreCase("NP")) {
             mincand=t;
             t=null;
          }
          if (t!=null) t=t.parent(sentenceTree);
       }

       result[0]=mincand;

       t=mincand;   


       while(t!=null && (t==mincand || !iscoordnp(t))) {

          if (t.value().equalsIgnoreCase("NP")){


            if (t.headTerminal(StHeadFinder)==HeadNode) {
              maxnp=t;
              if (minnp==null) minnp=t;
            }else{
              t=null;
            }
          }
          if (t!=null) t=t.parent(sentenceTree);
       }

       result[1]=minnp;
       result[2]=maxnp;
       return result;

    }
     public List<Tree>[] calcParseInfo(Tree sentTree,
            int startWord, int endWord,
           MentionType mentionType) {
        List<Tree>[] result=new List[3];
        Tree lowest=calcLowestProjection(sentTree, startWord, endWord);
        List<Tree> projections=new ArrayList<Tree>();
        List<Tree> premodifiers=new ArrayList<Tree>();
        List<Tree> postmodifiers=new ArrayList<Tree>();
        result[0]=projections;
        result[1]=premodifiers;
        result[2]=postmodifiers;
        projections.add(lowest);
        calcPremodifiers(result, mentionType);
        calcHighestProjection(result, sentTree);
        return result;
    }
    
    private void calcPremodifiers(List<Tree>[] result,
            MentionType mentionType) {
        Tree lowestProjection=result[0].get(0);
        Tree[] chlds=lowestProjection.children();
        List<Tree> premodifiers=result[1];
        for (int i=0; i<chlds.length-1; i++)
        {
            String cat=chlds[i].value().toUpperCase();
            if (cat.endsWith("DT"))
                continue;
            if (cat.equals("NNP"))
            {
                if (mentionType.features.contains(
                        MentionType.Features.isProperName))
                    premodifiers.add(chlds[i]);
            }
            else
                premodifiers.add(chlds[i]);
        }
    }
    
     /** finds the highest projection of a node and
     *  collects all the postmodifiers.
     */
    private void calcHighestProjection(List<Tree>[] result,
            Tree sentenceTree)
    {
        Tree lowestProjection=result[0].get(0);
        Tree highestProjection=lowestProjection;
        List<Tree> projections=result[0];
        List<Tree> postmodifiers=result[2];
        if (!lowestProjection.value().equalsIgnoreCase("NP"))
        {
            return;
        }
        Tree parent;
        parentloop: while ((parent=highestProjection.parent(sentenceTree))!=null)
        {
            if (!parent.value().equalsIgnoreCase("NP"))
                break;
            // NN[P][S] and CC are a safe sign that this is not
            // a projection of our original NP
            for (Tree chld: parent.children())
            {
                if (chld.value().toUpperCase().startsWith("NN") ||
                        chld.value().equalsIgnoreCase("CC"))
                    break parentloop;
            }
            boolean chldSeen=false;
            for (Tree chld: parent.children())
            {
                if (chld==highestProjection)
                    chldSeen=true;
                else if (chldSeen &&
                        !chld.value().equals(",") &&
                        !chld.value().equals(":"))
                    postmodifiers.add(chld);
            }
            projections.add(parent);
            highestProjection=parent;
        }
    }

    
    /** determines baseNP node and highest projection.
     *  This code comes from Xiaofeng's SyntaxTreeFeature.alignToTree method,
     *  but was moved here so that (i) everyone profits from it and
     *  (ii) everyone (including Xiaofeng) profits from eventual bugfixes
     *  that are applied to this central place.
     */
    private Tree calcLowestProjection(Tree sentenceTree,
            int startWord, int endWord) {
        List<Tree> Leaves = sentenceTree.getLeaves();
        Tree startNode = Leaves.get(startWord);


        Tree endNode=null;

        if (endWord>=Leaves.size()) {
// for marks that do not respect sentence boundaries
         endNode=Leaves.get(Leaves.size()-1);
        }else{
         endNode = Leaves.get(endWord);
        }

        Tree parentNode = startNode;
        while (parentNode != null && !parentNode.dominates(endNode)) {
            parentNode = parentNode.parent(sentenceTree); }
        
        if (parentNode == null) {
            return startNode;
        }
        
        //to deal with the embeded NPs
        //like "(NP (dt the) [(nnp iditarod) (nnp trail)] [ (nnp sled) (nnp dog) (nn race)] )"
        
        if (parentNode.value().charAt(0) == 'N' ||
                parentNode.value().charAt(0) == 'n'){
            int LastNodeIndex = parentNode.children().length - 1;
            Tree lastNode = parentNode.children()[LastNodeIndex];
            if ((lastNode.value().equalsIgnoreCase("NP") ||
                    lastNode.value().equalsIgnoreCase("NNP") ||
                    lastNode.value().equalsIgnoreCase("NNPS") ||
                    lastNode.value().equalsIgnoreCase("NNS") ||
                    lastNode.value().equalsIgnoreCase("NN") ) &&
                    !lastNode.dominates(endNode) &&
                    lastNode != endNode) {
                return endNode.parent(parentNode);
            }
        }
        return parentNode;
    }

    private final static Pattern pat_NP=
            Pattern.compile("NP",Pattern.CASE_INSENSITIVE);
    private final static Pattern pat_CN=
            Pattern.compile("NN|NNS",Pattern.CASE_INSENSITIVE);
    private final static Pattern pat_PN=
            Pattern.compile("NNP|NNPS",Pattern.CASE_INSENSITIVE);
    private final static Pattern pat_PRO=
            Pattern.compile("PRO",Pattern.CASE_INSENSITIVE);
    private final static Pattern pat_PP=
            Pattern.compile("PP",Pattern.CASE_INSENSITIVE);
    private final static Pattern pat_PREP=
            Pattern.compile("IN",Pattern.CASE_INSENSITIVE);
    private final static Pattern pat_S=
            Pattern.compile("S|SBAR",Pattern.CASE_INSENSITIVE);
    private final static Pattern pat_VP=
            Pattern.compile("VP",Pattern.CASE_INSENSITIVE);
    private final static Pattern pat_DT=
            Pattern.compile("DT",Pattern.CASE_INSENSITIVE);
    private final static Pattern pat_DT2=
            Pattern.compile("WDT|PRP\\$",Pattern.CASE_INSENSITIVE);
    private final static Pattern pat_ADJ=
            Pattern.compile("JJ|JJR|JJS",Pattern.CASE_INSENSITIVE);
    private final static Pattern pat_ADV=
            Pattern.compile("RB|RBR|RBS",Pattern.CASE_INSENSITIVE);
    private final static Pattern pat_CC=
            Pattern.compile("CC",Pattern.CASE_INSENSITIVE);
    private final static Pattern pat_PUNCT=
            Pattern.compile("[\\.,\\(\\):#]|``|''|-LRB-|-RRB-",Pattern.CASE_INSENSITIVE);
    public NodeCategory labelCat(String cat) {
        if (pat_NP.matcher(cat).matches()) {
            return NodeCategory.NP;
        } else if (pat_CN.matcher(cat).matches()) {
            return NodeCategory.CN;
        } else if (pat_PN.matcher(cat).matches()) {
            return NodeCategory.PN;
        } else if (pat_PRO.matcher(cat).matches()) {
            return NodeCategory.PRO;
        } else if (pat_PP.matcher(cat).matches()) {
            return NodeCategory.PP;
        } else if (pat_PREP.matcher(cat).matches()) {
            return NodeCategory.PREP;
        } else if (pat_S.matcher(cat).matches()) {
            return NodeCategory.S;
        } else if (pat_VP.matcher(cat).matches()) {
            return NodeCategory.VP;
        } else if (pat_DT.matcher(cat).matches()) {
            return NodeCategory.DT;
        } else if (pat_DT2.matcher(cat).matches()) {
            return NodeCategory.DT2;
        } else if (pat_ADJ.matcher(cat).matches()) {
            return NodeCategory.ADJ;
        } else if (pat_ADV.matcher(cat).matches()) {
            return NodeCategory.ADV;
        } else if (pat_CC.matcher(cat).matches()) {
            return NodeCategory.CC;
        } else if (pat_PUNCT.matcher(cat).matches()) {
            return NodeCategory.PUNCT;
        } else {
            return NodeCategory.OTHER;
        }
    }

    /** Fix the caps: convert the first letter of each word to a capital letter */
    private String fixCaps(String origString) {
        StringBuffer buffer = new StringBuffer();
        String[] words = origString.split("\\s+");
        for (String word : words) {
            char[] originalChars = word.toCharArray();
            originalChars[0] = Character.toUpperCase(word.charAt(0));
            buffer.append(originalChars).append(" ");
        }
        return buffer.deleteCharAt(buffer.length()-1).toString();
    }

    public String getHeadOrName(Markable m) {
                if (m.getAttributeValue("type").equals("enamex")) {
            return fixCaps(markableString(m));
        } else {
            String result = NPHeadFinder.getInstance().getHeadLemma(m);
            // if unknown lemma pull the token
            if ("<unknown>".equals(result) || "@card@".equals(result))
                result = NPHeadFinder.getInstance().getHead(m);
            return result;
        }
    }
    
    /**Determine mentions markable string
     */
    public String markableString(final Markable markable) {
        return new StringBuffer(markable.toString()).
                deleteCharAt(markable.toString().length()-1).deleteCharAt(0).toString();
    }

    public String markablePOS(Markable markable) {
        return markable.getAttributeValue(DEFAULT_POS_LEVEL);
    }

    public String enamexType(Markable markable) {
        return markable.getAttributeValue(LABEL_ATTRIBUTE);
    }

        public void readMapping(LanguagePlugin.TableName table, String fname)
    {
        Map<String,String> map;
        try {
            File names_dir=new File(ConfigProperties.getInstance().getRoot(),"names");
            BufferedReader br=new BufferedReader(new FileReader(new File(names_dir,fname)));
            map=new HashMap<String,String>();
            String line;
            while ((line=br.readLine())!=null)
            {
                String[] entries=line.split("\t");
                String[] aliases=entries[1].split("; *");
                for (String alias: aliases) {
                    map.put(alias,entries[0]);
                }
            }
            aliasTables.put(table, map);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }


    public String lookupAlias(String original, TableName table) {
        Map<String,String> map=aliasTables.get(table);
        if (map==null) {
            return null;
        } else {
            return map.get(original);
        }
    }

    private boolean findCoord(Markable markable) {
        final String[] pos = markable.getAttributeValue(DEFAULT_POS_LEVEL).split(" ");
        for (String tag: pos) {
            if (tag.equalsIgnoreCase("CC")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Modified von NPHeadFinder.java
     *
     * @param markable
     * @return part of speech of the head
     */
    @Override
    public String getHeadPOS(Markable markable) {

        final String[] posTags = markable.getAttributeValue("pos").split(" ");
        // If the last word is tagged POS, return (last-word)
        if (posTags[(posTags.length-1)].equals("pos"))
        { return "pos"; }

        // Else search from right to left for the first child which
        // is an NN, NNP, NNPS, NNS, NX, POS, or JJR
        for (int pos = posTags.length-1; pos >= 0; pos--)
        {
            if (posTags[pos].matches("(nn|nns|np|nps|nnp|nnps|nx|pos|jjr)"))
            { return posTags[pos]; }
        }

        // Else search from left to right for the first child which is an NP
        for (int pos = 0; pos < posTags.length; pos++)
        {
            if (posTags[pos].equals("np"))
            { return "np"; }
        }

        // Else search from right to left for the first child which is a
        // $, ADJP or PRN
        for (int pos = posTags.length-1; pos >= 0; pos--)
        {
            if (posTags[pos].matches("(\\$|adjp|prn)"))
            { return posTags[pos]; }
        }

        // Else search from right to left for the first child which is a CD
        for (int pos = posTags.length-1; pos >= 0; pos--)
        {
            if (posTags[pos].equals("cd"))
            { return posTags[pos]; }
        }

        // Else search from right to left for the first child which is a
        // JJ, JJS, RB or QP
        for (int pos = posTags.length-1; pos >= 0; pos--)
        {
            if (posTags[pos].matches("(jj|jjs|rb|qb)"))
            { return posTags[pos]; }
        }

        // Else return the last word
        return posTags[posTags.length-1];
    }

    public boolean isExpletiveWordForm(String string) {
        return string.equalsIgnoreCase("it");
    }

    public boolean isExpletiveRB(Mention m) {
      if (!m.getMarkableString().equalsIgnoreCase("it")) return false;
      Tree sentenceTree=m.getSentenceTree();
      if (sentenceTree==null) return false;
      Tree mTree=m.getMinParseTree();
      if (mTree==null) return false;
      if (mTree.getLeaves()!=null && mTree.getLeaves().size()>0)
        mTree=mTree.getLeaves().get(0);
      if (mTree==null) return false;
      Tree pTree=sentenceTree;
      if (pTree==null) return false;
      List<Tree> leaves=pTree.getLeaves();
      int i=0;
      int lsz=leaves.size();
      while(i<lsz && leaves.get(i)!=mTree) {i++;}
      i++;

// skip adverbs (it clearly seems..)

      while(i<lsz && leaves.get(i).parent(pTree).value().equalsIgnoreCase("rb"))
         i++;
      if (i>=lsz) return false;
      if (leaves.get(i).value().equalsIgnoreCase("seems")) return true;
      if (i>=lsz-1) return false;
      if ((leaves.get(i).value().equalsIgnoreCase("turns") ||
         leaves.get(i).value().equalsIgnoreCase("turned")) &&
         leaves.get(i+1).value().equalsIgnoreCase("out")) return true;

      if (leaves.get(i).value().toLowerCase().matches("^(comes|came)$") &&
        leaves.get(i+1).value().equalsIgnoreCase("time")) return true;

      if (leaves.get(i).value().toLowerCase().matches(EXPL_VERB) &&
       leaves.get(i+1).value().equalsIgnoreCase("time")) return true;

      if (i<lsz-2 &&
       leaves.get(i).value().toLowerCase().matches("^(comes|came)$") &&
       leaves.get(i+1).value().equalsIgnoreCase("down") &&
       leaves.get(i+2).value().equalsIgnoreCase("to"))
        return true;

      if (leaves.get(i).value().equalsIgnoreCase("appears"))
        return true;


      if (!leaves.get(i).value().toLowerCase().matches(EXPL_VERB)) return false;

//skip adverbs, not
    i++;
    while(i<lsz && leaves.get(i).parent(pTree).value().equalsIgnoreCase("rb")) i++;
    if (i>=lsz) return false;

    if (leaves.get(i).value().equalsIgnoreCase("why")) return true;

    if (i<lsz-1 &&
     leaves.get(i).value().equalsIgnoreCase("known") &&
     leaves.get(i+1).value().equalsIgnoreCase("that")) return true;

    if (i<lsz-1 &&
     leaves.get(i).value().equalsIgnoreCase("time") &&
     leaves.get(i+1).value().equalsIgnoreCase("to")) return true;


    if (leaves.get(i).value().equalsIgnoreCase("hot") ||
leaves.get(i).value().equalsIgnoreCase("warm") ||
leaves.get(i).value().equalsIgnoreCase("cold")) return true;

    if (leaves.get(i).value().equalsIgnoreCase("correct") ||
leaves.get(i).value().equalsIgnoreCase("ridiculous") ||
leaves.get(i).value().equalsIgnoreCase("difficult") ||
leaves.get(i).value().equalsIgnoreCase("right") ||
leaves.get(i).value().equalsIgnoreCase("wrong") ||
leaves.get(i).value().equalsIgnoreCase("interesting") ||
leaves.get(i).value().equalsIgnoreCase("possible") ||
leaves.get(i).value().equalsIgnoreCase("true") ||
leaves.get(i).value().equalsIgnoreCase("impossible"))
   return true;


    if (i<lsz-1 &&
        (leaves.get(i).parent(pTree).value().equalsIgnoreCase("jj") ||
         leaves.get(i).parent(pTree).value().equalsIgnoreCase("jjr") ||
         leaves.get(i).parent(pTree).value().equalsIgnoreCase("vbn") ||
         leaves.get(i).parent(pTree).value().equalsIgnoreCase("jjs")) &&
        (leaves.get(i+1).value().equalsIgnoreCase("that") ||
         leaves.get(i+1).value().equalsIgnoreCase("to")))
        return true;


    if (i>=lsz-1) return false;

    if (leaves.get(i).value().equalsIgnoreCase("a") &&
      (leaves.get(i+1).value().equalsIgnoreCase("pity") ||
      leaves.get(i+1).value().equalsIgnoreCase("fact") ||
      leaves.get(i+1).value().equalsIgnoreCase("shame") ||
      leaves.get(i+1).value().equalsIgnoreCase("way") ||
      leaves.get(i+1).value().equalsIgnoreCase("question") ||
      leaves.get(i+1).value().equalsIgnoreCase("matter")))
         return true;

    if (i>=lsz-2) return false;

    if (leaves.get(i).value().equalsIgnoreCase("the") &&
      (leaves.get(i+1).value().equalsIgnoreCase("first") ||
      leaves.get(i+1).value().equalsIgnoreCase("second")) &&
      leaves.get(i+2).value().equalsIgnoreCase("time"))
         return true;

    return false;
}



}
