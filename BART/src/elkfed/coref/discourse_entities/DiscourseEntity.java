/**************************************************************************
 *  GuiTAR - A General Tool for Anaphora Resolution
 *  Copyright (C) 2004-2007
 *  Mijail Kabadjov, Massimo Poesio, Olivia Sanchez-Graillet, Philippe Goux
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 **************************************************************************/
package elkfed.coref.discourse_entities;



import elkfed.coref.mentions.*;
import edu.stanford.nlp.trees.Tree;
import elkfed.config.ConfigProperties;
import elkfed.lang.GermanLanguagePlugin;
import elkfed.lang.LanguagePlugin;
import elkfed.lang.NodeCategory;

import java.util.*;
import java.util.logging.Logger;

import elkfed.knowledge.SemanticClass;
import elkfed.lang.EnglishLanguagePlugin;
import elkfed.lang.MentionType.Features;
import elkfed.mmax.minidisc.Markable;
import elkfed.mmax.minidisc.MarkableLevel;
import elkfed.nlp.util.Gender;
import elkfed.nlp.util.Number;
/**
 * @author julianbaumann
 */

   /*
    * minimal DiscourseEntity, should work for StringMatch by iterating over mentions
    */

public class DiscourseEntity {
	
	private static final LanguagePlugin langPlugin = ConfigProperties
			.getInstance().getLanguagePlugin();
	
	private static int nextID = 0;
	private int ID;
	
	private boolean firstMention_isFirstMention;
	
	private TreeSet<Mention> mentions;
    private Set<String> words;
    private Set<String> heads;
    private Set<Tree> modifiers;
    private Set<Gender> genders;
    private Set<Number> numbers; 
    
	public DiscourseEntity(Mention m) {
		ID = nextID;
		nextID++;
		mentions = new TreeSet<Mention>();
		mentions.add(m);
		words = new HashSet<String>();
		addWords(m);
		
		heads = new HashSet<String>();
		//heads.add(m.getHeadString());
		heads.add(m.getHeadLemma());
		modifiers = new HashSet<Tree>();				
		modifiers.addAll(m._premodifiers);		
		modifiers.addAll(m._postmodifiers);
		
		genders = new HashSet<Gender>();
		genders.add(m.getGender());	
		
		numbers = new HashSet<Number>();
		numbers.add(m.getNumberLabel());
	}	

	
	public TreeSet<Mention> getMentions() {
		return mentions;
	}
	
	public Mention getFirstMention() {
		return mentions.first();
	}
	
	public void addWords(Mention m) {
		if (langPlugin instanceof GermanLanguagePlugin) {		
			for (String word: m.getDiscourseElementsByLevel("lemma")) {
				if (!langPlugin.isInStopwordList(word)) {
					words.add(word);
				}
			}
		}
		
		else if (langPlugin instanceof EnglishLanguagePlugin) {
			for (String word: m.getDiscourseElementsByLevel("morph")) {
				if (!langPlugin.isInStopwordList(word)) {
					words.add(word);
				}
			}
		}
	}        

	
	public Set<Gender> getGenders() {
		return genders;
	}
	
	public void setGender(Gender gender) {
		genders = new HashSet<Gender>();
		genders.add(gender);
	}
	
	public void addGender(Gender gender) {
		genders.add(gender);
	}
	
	public Set<Number> getNumbers() {
		return numbers;
	}
	
	public void merge(Mention ante) {
		
		DiscourseEntity deAnte = ante.getDiscourseEntity();
		for (Mention m : deAnte.getMentions()) {
			mentions.add(m);
			m.setDiscourseEntity(this);
			addWords(m);
		}
		genders.addAll(getGenders());
		numbers.addAll(getNumbers());
		words.addAll(deAnte.getWords());		
		modifiers.addAll(deAnte.getModifiers());
		heads.addAll(deAnte.getHeads());
	}
	
	public Set<Tree> getModifiers() {
		return modifiers;
	}
	
	public String getWordsString() {
		StringBuilder w = new StringBuilder();
	    for (String word : words) {
	    	w.append(word);
	    	w.append(" ");
	    }
	    return w.toString();
	}
	public Set<String> getHeads() {
		return heads;
	}
	
	public int getID() {
		return ID;
	}
	
	public Set<String> getWords() {
		return words;
	}
	
	public String getHeadsString() {
		StringBuilder w = new StringBuilder();
	    for (String head : heads) {
	    	w.append(head);
	    	w.append(" ");	    
	    }
	    return w.toString();
	}
	
	public void set_firstMention_isFirstMention(boolean isFirstMention) {
	        firstMention_isFirstMention = isFirstMention;
	    }
}