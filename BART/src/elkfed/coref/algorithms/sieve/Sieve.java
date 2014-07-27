package elkfed.coref.algorithms.sieve;

import java.util.List;

import elkfed.lang.EnglishLanguagePlugin;
import elkfed.lang.EnglishLinguisticConstants;
import elkfed.lang.GermanLanguagePlugin;
import elkfed.lang.GermanLinguisticConstants;
import elkfed.lang.LinguisticConstants;
import elkfed.coref.features.pairs.FE_DistanceWord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.Generics;
import elkfed.config.ConfigProperties;
import elkfed.coref.PairInstance;
import elkfed.coref.discourse_entities.DiscourseEntity;
import elkfed.coref.features.pairs.FE_AppositiveParse;
import elkfed.coref.features.pairs.FE_Copula;
import elkfed.coref.features.pairs.FE_Gender;
import elkfed.coref.features.pairs.FE_Number;
import elkfed.coref.features.pairs.FE_SentenceDistance;
import elkfed.coref.features.pairs.FE_Speech;
import elkfed.coref.mentions.Mention;
import elkfed.knowledge.SemanticClass;
import elkfed.lang.LanguagePlugin;
import elkfed.lang.LanguagePlugin.TableName;
import elkfed.ml.TriValued;
import elkfed.mmax.minidisc.Markable;
import elkfed.nlp.util.Gender;
import elkfed.nlp.util.Number;

/**
 * 
 * @author Xenia Kühling, Julian Baumann, Sebastian Ruder
 * 
 */
public abstract class Sieve {
	


	protected String name; // name of sub class
	// list of antecedents/potential coreferents
	protected List<Mention> mentions;

	/**
	 * Abstract method that uses the particular rules of a sieve to look for an
	 * antecedent for mention in the list of mentions that was provided to the
	 * sieve.
	 * 
	 * @param mention
	 *            the mention whose antecedent is sought
	 * @return index of antecedent; -1 if no antecedent is found
	 */
	abstract int runSieve(Mention mention);

	/**
	 * Returns name of sieve
	 * 
	 * @return name of the sieve
	 */
	String getName() {
		return this.name;
	}

	// language plugin is retrieved
	protected static final LanguagePlugin langPlugin = ConfigProperties
			.getInstance().getLanguagePlugin();

	/**
	 * Checks if a mention and its antecedent are in an appositive construction
	 * <p>
	 * Appositive constructions don't appear in TüBa-D/Z; here they form one
	 * mention.
	 * 
	 * @param pair
	 *            PairInstance of mention, antecedent
	 * @return true or false
	 */
	boolean isAppositive(PairInstance pair) {
		if (FE_AppositiveParse.getAppositivePrs(pair)) {
			System.out.println("APPOSITIVE");
			return true;
		}
		return false;
	}

	/**
	 * Checks if mention and antecedent are in a copulative subject-object
	 * relation
	 * 
	 * @param pair
	 *            PairInstance of mention, antecedent
	 * @return true or false
	 */
	boolean isPredicateNominative(PairInstance pair) {
		if (FE_Copula.getCopula(pair)) {
			System.out.println("PREDICATE NOMINATIVE");
			return true;
		}
		return false;
	}

	/**
	 * Checks if mention and antecedent are in a role appositive construction
	 * 
	 * @param pair
	 *            PairInstance of mention, antecedent
	 * @return true or false
	 */
	boolean isRoleAppositive(PairInstance pair) {
		Mention mention = pair.getAnaphor();
		Mention antecedent = pair.getAntecedent();

		if (mention.getProperName() && // check if person
				isAnimate(antecedent) && // check if animate
				!isNeutral(antecedent) && // check if neutral
				mention.embeds(antecedent)) { // check if antecedent is
												// contained in mention
			System.out.println("ROLE APPOSITIVE");
			return true;
		}
		return false;
	}

	/**
	 * Checks if mention is animate
	 * <p>
	 * Animacy is set using: (a) a static list for pronouns; (b) NER and Gender
	 * labels (e.g., PERSON and MALE are animate); and (c) lists of animate and
	 * inanimate unigrams taken from
	 * https://github.com/castiron/didh/tree/master/lib/vendor/snlp/dcoref;
	 * German lists have been translated using Google Tranlate
	 * 
	 * @param mention
	 *            mention
	 * @return true or false
	 */
	boolean isAnimate(Mention mention) {
		// (a) check with pronoun list
		// not necessary for isRoleAppositive, maybe for other applications
		String[] tokens = mention.getMarkable().getDiscourseElements();
		if (mention.getPronoun()) {
			return false;
		}
		// (b) check with NER and Gender labels
		if (SemanticClass.isaPerson(mention.getSemanticClass())
				|| mention.getGender().equals(Gender.MALE)
				|| mention.getGender().equals(Gender.FEMALE)) {
			return true;
		} else if (SemanticClass.isaObject(mention.getSemanticClass())
				|| SemanticClass.isaNumeric(mention.getSemanticClass())
				|| mention.getGender().equals(Gender.NEUTRAL)) {
			return false;
		}
		// (c) check with animate and inanimate lists
		for (int i = 0; i < tokens.length; i++) {
			/*
			 * at the moment, one token suffices to be animate or inanimate to
			 * arrive at a decision; maybe only consider the head word?
			 */
			if (langPlugin.isInAnimateList(tokens[i])) {
				return true;
			} else if (langPlugin.isInInanimateList(tokens[i])) {
				return false;
			}
		}
		return false;
	}

	/**
	 * Checks if mention is neutral.
	 * <p>
	 * Uses NER and Gender labels, and male, female, and neutral lists taken
	 * from https://github.com/castiron/didh/tree/master/lib/vendor/snlp/dcoref;
	 * German lists have been translated using Google Tranlate
	 * 
	 * @param mention
	 *            mention
	 * @return true or false
	 */
	boolean isNeutral(Mention mention) {
		if (SemanticClass.isaPerson(mention.getSemanticClass())
				|| mention.getGender().equals(Gender.MALE)
				|| mention.getGender().equals(Gender.FEMALE)) {
			return false;
		}
		String[] tokens = mention.getMarkable().getDiscourseElements();

		for (int i = 0; i < tokens.length; i++) {
			String t = tokens[i];
			// same comment as loop above
			if (langPlugin.isInNeutralList(t)) {
				return true;
			} else if (langPlugin.isInMaleList(t)
					|| langPlugin.isInFemaleList(t)) {
				return false;
			}
		}
		return false;
	}

	/**
	 * Checks if the mention is a relative pronoun and modifies the antecedent.
	 * 
	 * @param pair
	 *            PairInstance of mention, antecedent
	 * @return true or false
	 */
	boolean isRelativePronoun(PairInstance pair) {
		Mention mention = pair.getAnaphor();
		Mention antecedent = pair.getAntecedent();
		Markable m1 = mention.getMarkable();
		Markable m2 = antecedent.getMarkable();
		String relative_pronouns = "";
		String[] tokens = mention.getMarkable().getDiscourseElements();
		// checks langPlugin to know which relative pronouns to use
		if (langPlugin instanceof GermanLanguagePlugin) {
			relative_pronouns = GermanLinguisticConstants.RELATIVE_PRONOUN;
		} else if (langPlugin instanceof EnglishLanguagePlugin) {
			relative_pronouns = EnglishLinguisticConstants.RELATIVE_PRONOUN;
		}

		if (tokens.length == 1 && tokens[0].matches(relative_pronouns)) {
			String rp = tokens[0];
			Gender gender = Gender.UNKNOWN;
			if (rp.equals("die") || rp.equals("welche") || rp.equals("deren")) {
				gender = Gender.FEMALE;
			} else if (rp.equals("der") || rp.equals("welcher") || rp.equals("dem")) {
				gender = Gender.MALE;
			} else if (rp.equals("das") || rp.equals("welches")) {
				gender = Gender.NEUTRAL;
			}
			int word_distance = m1.getLeftmostDiscoursePosition()
					- m2.getRightmostDiscoursePosition();
			// word distance = 3 means there is one word between mention and
			// antecedent
			if (word_distance <= 3 && gender.equals(antecedent.getGender())) {
				// makes sure that antecedent is the uppermost mention and
				// is not embedded by another one
				for (Mention antecedent_of_antecedent : mentions) {
					if (antecedent_of_antecedent.equals(antecedent)) {
						break;
					}
					else if (antecedent_of_antecedent.embeds(antecedent) &&
							gender.equals(antecedent_of_antecedent.getGender())) {
						return false;
					}
				}
				System.out.println(String
								.format("RELATIVE PRONOUN! %s and %s (Distance: %d, Gender: %s)",
										mention.getMarkable().getID(),
										antecedent.getMarkable().getID(),
										word_distance, antecedent.getGender()));
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if one mention is an acronym of the other mention and vice versa.
	 * 
	 * @param pair
	 *            PairInstance of mention, antecedent
	 * @return true or false;
	 */
	boolean isAcronym(PairInstance pair) {
		String mention = pair.getAnaphor().toString();
		String antecedent = pair.getAntecedent().toString();
		return checkOneWayAcronym(mention, antecedent)
				|| checkOneWayAcronym(antecedent, mention);
	}

	/**
	 * Checks if one string is an acronym of the other string
	 * 
	 * @param acronym
	 *            String thought to be an acronym
	 * @param expression
	 *            String whose initials are thought to form said acronym
	 * @return true or false
	 */
	boolean checkOneWayAcronym(String acronym, String expression) {
		if (acronym.toUpperCase().equals(acronym)) {
			String initials = "";
			for (String word : expression.split(" ")) {
				initials += word.substring(0, 1).toUpperCase();
			}
			if (acronym.equals(initials)) {
				System.out.println("ACRONYM");
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if one expression is a demonym of the other using a static list of
	 * countries and their gentilic forms from Wikipedia
	 * 
	 * @param pair
	 *            PairInstance of mention, antecedent
	 * @return true or false
	 */
	boolean isDemonym(PairInstance pair) {
		String mention = pair.getAnaphor().toString();
		String antecedent = pair.getAntecedent().toString();
		String mention_lookup = langPlugin.lookupAlias(mention,
				TableName.DemonymMap);
		String antecedent_lookup = langPlugin.lookupAlias(antecedent,
				TableName.DemonymMap);

		if ((mention_lookup != null && mention_lookup.equals(antecedent))
				|| (antecedent_lookup != null && antecedent_lookup
						.equals(mention))) {
			System.out.println("DEMONYM");
			return true;
		}
		return false;
	}

	/**
	 * Compute if sentences in which mention and antecedent appear are not more
	 * than 4 sentences apart.
	 * 
	 * @param pair
	 *            PairInstance of mention, antecedent
	 * @return true or false
	 */

	public boolean sentenceDistance(PairInstance pair) {
		// FE_SentenceDistance sd = new FE_SentenceDistance();
		if (FE_SentenceDistance.getSentDist(pair) < 4) {
			return true;
		}
		return false;
	}

	/**
	 * Check if mention and antecedent are both in the same state of animacy
	 * 
	 * @param pair
	 *            PairInstance of mention, antecedent
	 * @return true or false
	 */

	public boolean animacyAgreement(PairInstance pair) {
		if (isAnimate(pair.getAnaphor()) == isAnimate(pair.getAntecedent())) {
			return true;
		}
		return false;

	}

	/**
	 * Check if mention and antecedent have the same gender
	 * 
	 * @param pair
	 *            PairInstance of mention, antecedent
	 * @return true or false
	 */
	public boolean genderAgreement(PairInstance pair) {

		for (Gender g: pair.getAnaphor().getDiscourseEntity().getGenders()){
			//if (g.equals(Gender.UNKNOWN) && !pair.getAntecedent().getGender().equals(Gender.UNKNOWN)){
				//return true;
			//}
			if (pair.getAntecedent().getGender().equals(g)){
				return true;
			}
		}
		return false;

	}

	/**
	 * Check if mention and antecedent have the same numerus (plural/ singular)
	 * 
	 * @param pair
	 *            PairInstance of mention, antecedent
	 * @return true or false
	 */

	public boolean numberAgreement(PairInstance pair) {
		Number mNumber = pair.getAnaphor().getNumberLabel();
		Number anteNumber = pair.getAntecedent().getNumberLabel();
		
		if (mNumber.equals(Number.UNKNOWN) || anteNumber.equals(Number.UNKNOWN)){
			return true;
			}
			
		if (anteNumber == mNumber) {
			return true;
		}
		return false;
	 }

	/**
	 * 
	 * Person â€“ we assign person attributes only to pronouns. We do not
	 * enforce this constraint when linking two pronouns, however, if one
	 * appears within quotes. This is a simple heuristic for speaker detection
	 * (e.g., I and she point to the same person in â€œ[I] voted my
	 * conscience,â€� [she] said).
	 */

	public void personAgreement(PairInstance pair) {
		// missing

	}

	/**
	 * Check if mention and antecedent belong to the same semantic class or
	 * mention's or antecedent's semantic class equals "unknown"
	 * 
	 * @param pair
	 *            PairInstance of mention, antecedent
	 * @return true or false
	 */
	boolean NERAgreement(PairInstance pair) {
		if ((pair.getAnaphor().getSemanticClass().equals(pair.getAntecedent()
				.getSemanticClass()))
				|| (pair.getAnaphor().getSemanticClass()
						.equals(SemanticClass.UNKNOWN))
				|| (pair.getAntecedent().getSemanticClass()
						.equals(SemanticClass.UNKNOWN))) {
			return true;
		}

		return false;

	}

	/**
	 * 
	 * 
	 * @param mention
	 * @param ante
	 * @return
	 */

	boolean IWithinI(PairInstance pair) {
		
		if (!isAppositive(pair) && !isRelativePronoun(pair)
				&& !isRoleAppositive(pair)) {
			if (pair.getAnaphor().embeds(pair.getAntecedent())
					|| pair.getAntecedent().embeds(pair.getAnaphor())) {
				return true;
			}
		}
		return false;
	}

	public boolean noNumericMismatch(PairInstance pair) {
		Set<String> mentionWords = new HashSet<String>();
		Set<String> anteWords = new HashSet<String>();
		String numbers_relex = ".*(höchstens|mindestens|eins|zwei|drei|vier|fünf|sechs|sieben|acht|neun|zehn|elf|zwölf|hundert|tausend|million|milliarde).*";

		mentionWords = pair.getAnaphor().getDiscourseEntity().getWords();
		anteWords = pair.getAntecedent().getDiscourseEntity().getWords();

		for (String mentionWord : mentionWords) {
			if (mentionWord.toLowerCase().matches(numbers_relex)
					|| mentionWord.matches(".*[0-9]+.*")) {
				if (!anteWords.contains(mentionWord)) {
					return false;
				}
				else {

					for (String anteWord : anteWords) {
						if (anteWord.toLowerCase().matches(numbers_relex)
								|| anteWord.matches(".*[0-9]+.*")) {
							if (!mentionWords.contains(anteWord)) {
								return false;
							}
						}
					}
				}
			}
		}
		return true;
	}

	public boolean noLocationMismatch(PairInstance pair) {
		Set<String> mentionWords = new HashSet<String>();
		Set<String> anteWords = new HashSet<String>();
		
		Mention mention = pair.getAnaphor();
		Mention ante = pair.getAntecedent();

		mentionWords = pair.getAnaphor().getDiscourseEntity().getWords();
		anteWords = pair.getAntecedent().getDiscourseEntity().getWords();

		List<String> mentionWordsPOS = new ArrayList<String>();
		List<String> anteWordsPOS = new ArrayList<String>();

		
		mentionWordsPOS = mention.getDiscourseElementsByLevel("pos");
		anteWordsPOS = ante.getDiscourseElementsByLevel("pos");
		
		
		for (String mentionWord: mentionWords){
			if (mentionWord.matches("(nördlich.*|südlich.*|.*westlich.*|.*östlich.*|obere.*|niedere.*)")) {
				if (!anteWords.contains(mentionWord)) {
					return false;
				}
			}
		}

			
		for (String anteWord: anteWords){	
			if (anteWord.matches("(nördlich.*|südlich.*|.*westlich.*|.*östlich.*|obere.*|niedere.*)")) {
				if (!mentionWords.contains(anteWord)) {
					return false;
				}
			}
		}
		
		int countMention = 0;
		
		for (String mentionWordPOS: mentionWordsPOS){
			if (mentionWordPOS.equalsIgnoreCase("ne")){
				countMention++;
			}
		}
		
		int countAnte = 0;
		
		for (String anteWordPOS: anteWordsPOS){
			if (anteWordPOS.equalsIgnoreCase("ne")){
				countAnte++;
			}
		}
		
		if (countAnte != countMention){
			return false;
		}
								
							
		return true;
	}

	

	public boolean entityHeadMatch(PairInstance pair) {
		Mention m = pair.getAnaphor();
		Mention ante = pair.getAntecedent();
		if (m.getPronoun() || ante.getPronoun()) {
			return false;
		}
		if (m.getNumber() != ante.getNumber()) {
			return false;
		}
		Set<String> mheadLemmas= new HashSet<>();
		String mHead = m.getHeadLemma();
		mheadLemmas.add(mHead);
		mheadLemmas.addAll(Arrays.asList(mHead.split(" ")));
		Set<String> dAnteHeads = ante.getDiscourseEntity().getHeads();
		for (String head : dAnteHeads) {
			if(mheadLemmas.contains(head) || Arrays.asList(head.split(" ")).contains(mHead)) {
				return true;
			}
		}

		return false;
	}

	public boolean properNameAgreement(PairInstance pair) {
		Mention m = pair.getAnaphor();
		Mention ante = pair.getAntecedent();
		
		if (m.getProperName() || ante.getProperName()) {
			if ((m.getSemanticClass().equals(ante.getSemanticClass()))
					&& (!m.getSemanticClass().equals(SemanticClass.UNKNOWN))) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public boolean relaxedEntityHeadMatch(PairInstance pair) {
		Mention m = pair.getAnaphor();
		Mention ante = pair.getAntecedent();

		if (m.getPronoun() || ante.getPronoun()) {
			return false;
		}
		String mHead = m.getHeadLemma();
		if (langPlugin.isInStopwordList(mHead)) {
			return false;
		}
		Set<String> mheadLemmas= new HashSet<>();
		
		mheadLemmas.add(mHead);
		mheadLemmas.addAll(Arrays.asList(mHead.split(" ")));
		Set<String> dAnteWords = ante.getDiscourseEntity().getWords();
		for (String head : dAnteWords) {
			if(mheadLemmas.contains(head) || Arrays.asList(head.split(" ")).contains(mHead)) {
				return true;
			}
		}
	
//		Set<String> dAnteWords = ante.getDiscourseEntity().getWords();
//		if (dAnteWords.contains(mHead)) {
//			return true;
//		}
		return false;

	}

	public boolean wordInclusion(PairInstance pair) {
		Mention m = pair.getAnaphor();
		Mention ante = pair.getAntecedent();

		
		Set<String> dmWords = m.getDiscourseEntity().getWords();
		
		Set<String> dAnteWords = ante.getDiscourseEntity().getWords();
		
		if (dAnteWords.containsAll(dmWords)) {
			return true;
		}

		return false;

	}

	public boolean compatibleModifiers(PairInstance pair) {
		Mention m = pair.getAnaphor();
		Mention ante = pair.getAntecedent();		
		
		if (!(noNumericMismatch(pair) && noLocationMismatch(pair))) {
			return false;
		}
		String posTags_regex = "(adja|nn|ne|pidat)";
		List<String> anteWords = ante.getDiscourseElementsByLevel("lemma");
		
		List<String> mWords = m.getDiscourseElementsByLevel("lemma");
		
		List<String> mPos	= m.getDiscourseElementsByLevel("pos");
		
		Set<String> toTest = new HashSet<>();

		for (int i = 0; i < mWords.size(); i++) {
			
			if(mPos.get(i).matches(posTags_regex) && !mWords.get(i).equals(m.getHeadLemma())) {
				toTest.add(mWords.get(i));
			}
		}	
		//return false if the mentions are too far apart and share no modifiers
		if (!sentenceDistance(pair) && toTest.size() == 0) {
			return false;
		}
		
		if(anteWords.containsAll(toTest)) {
			return true;
		} 
		return false;
		}

	

	public boolean isVorfeldEs(Mention mention) {
		if (!(mention.getMarkable().toString().equals("[es]") || mention
				.getMarkable().toString().equals("[Es]"))) {
			return false;
		}

		else {
			if (mention
					.getSentenceTree()
					.toString()
					.matches(
							"\\(Start(.*)\\([VM]F \\(NX \\(PPER [Ee]s\\)(.*)")) {
				return true;
			}
		}
		
		
		return false;

	}
	
	public boolean contains_article(Mention mention) {
		String[] tokens = mention.getMarkable().getDiscourseElements();
		String def_articles = null;
		String indef_articles = null;
		if (langPlugin instanceof GermanLanguagePlugin) {
			def_articles = GermanLinguisticConstants.DEF_ARTICLE;
			indef_articles = GermanLinguisticConstants.INDEF_ARTICLE;
		} else if (langPlugin instanceof EnglishLanguagePlugin) {
			def_articles = EnglishLinguisticConstants.RELATIVE_PRONOUN;
			indef_articles = GermanLinguisticConstants.INDEF_ARTICLE;
		}
		for (String s : tokens) {
			if (s.matches(def_articles) || s.matches(indef_articles)) {
				return true;
			}
		}
		return false;		
	}
	
	public boolean contains_day_month_year(Mention mention) {
		String[] tokens = mention.getMarkable().getDiscourseElements();
		String days_months_year = null;
		if (langPlugin instanceof GermanLanguagePlugin) {
			days_months_year = GermanLinguisticConstants.DAYS_MONTHS_YEAR;
		} else if (langPlugin instanceof EnglishLanguagePlugin) {
			days_months_year = EnglishLinguisticConstants.DAYS_MONTHS_YEAR;
		}
		for (String s : tokens) {
			if (s.matches(days_months_year)) {
				return true;
			}
		}
		return false;		
	}
	
	public boolean antecedent_is_more_specific(PairInstance pair) {
		return pair.getAntecedent().getMarkable().getDiscourseElements().length >=
				pair.getAnaphor().getMarkable().getDiscourseElements().length;
	}
	public static boolean isInCooargumentDomain(PairInstance pair) {
		if (FE_SentenceDistance.getSentDist(pair) > 0) {
			return false;
		}
		Mention m = pair.getAnaphor();
		Mention ante = pair.getAntecedent();
		
		Tree sentenceTree = m.getSentenceTree();
		Tree joinedTree = sentenceTree.joinNode(m.getHighestProjection(), ante.getHighestProjection());
		List<Tree> domPathMention = joinedTree.dominationPath(m.getHighestProjection());
		//List<Tree> domPathAnte = joinedTree.dominationPath(ante.getHighestProjection());
		for(Tree node: domPathMention) {
			if (node.value().equals("SIMPX") && node != joinedTree) {
				return false;
			}
		}
		return true;
	}
	
	
public boolean isSpeakerSpeechLeft(Mention mention){
		
		// this method tries to recognize all speakers that have speech to their left, e.g.:
		// "...", so A
		// "...", speech_verb A
		
		if (FE_Speech.isMentionInSpeech(mention)){ // speaker cannot be in speech
			return false;
		}
		
		// returns true if 5 lemmata left to mention contain so, ',' and '"' 
		if (mention.getDiscourseElementsByLevelAndExtendedSpan("lemma", 5, 0).contains("so") && 
				mention.getDiscourseElementsByLevelAndExtendedSpan("lemma", 5, 0).contains("\"") &&
				mention.getDiscourseElementsByLevelAndExtendedSpan("lemma", 5, 0).contains(",")){
			return true;
		}
		
		// returns true if 2 lemmata left to mention containa speech verb
		for (String word: mention.getDiscourseElementsByLevelAndExtendedSpan("lemma", 2, 0)){
			if (langPlugin.isInSpeechVerbList(word)){
				return true;
			}
		}

		return false;
		
		
	}
	
	
	public boolean isSpeakerSpeechRight(Mention mention){
		
		// this method tries to recognize all speakers that have speech to their right, e.g.:
		// A: "..."
		// A speech_verb (:) "..."
		
		if (FE_Speech.isMentionInSpeech(mention)){ // speaker cannot be in speech
			return false;
		}
				
		// returns true if 5 lemmate right to mention contain ':' and '"'
		if (mention.getDiscourseElementsByLevelAndExtendedSpan("lemma", 0, 3).contains(":") && 
				mention.getDiscourseElementsByLevelAndExtendedSpan("lemma", 0, 3).contains("\"") 
				){
			return true;
		}
		
		// returns true if 2 lemmata right to mention containa speech verb
		for (String word: mention.getDiscourseElementsByLevelAndExtendedSpan("lemma", 0 , 2)){
			if (langPlugin.isInSpeechVerbList(word)){
				return true;
			}
		}
		
		return false;

	}
	
		
	
}
