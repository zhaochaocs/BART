package elkfed.coref.algorithms.sieve;

import java.util.List;

/**
 * @author julianbaumann
 */

/*
 * ToDo:  Proper WordInclusion with removed StopWords, modificator match,  i within i
 */
import elkfed.coref.mentions.Mention;
/**
 * This Sieve links a Mention to an antecedent,
 * if its head word matches any head word of the antecedents Discourse Entity. 
 * Additionally it needs to meet the word inclusion, the compatible modifiers
 * and the not I-within-I requirements.
 * 
 * 
 * @see SieveUtilities#entityHeadMatch(Mention, Mention) 
 * @see SieveUtilities#wordInclusion(Mention, Mention) 
 * @see SieveUtilities#compatibleModifiers(Mention, Mention) 
 * @see SieveUtilities#IWithinI(Mention, Mention)
 * 
 * @author Julian
 *
 */
public class StrictHeadMatchASieve extends Sieve {
	
	private static final SieveUtilities s = new SieveUtilities();
	
	public StrictHeadMatchASieve(List<Mention> mentions) {
		this.mentions = mentions;
		this.name = "StrictHeadMatchASieve";
	}

public int runSieve(Mention mention){		
		
		int mention_idx = mentions.indexOf(mention);
		int ante_idx = -1;
		
		for (int idx = 0; idx < mention_idx; idx++){
			Mention potAnte = mentions.get(idx);
			
			if (s.entityHeadMatch(mention, potAnte)) {
				
				if(s.wordInclusion(mention, potAnte)) {
					if (s.compatibleModifiers(mention, potAnte)) {
						if (!(s.IWithinI(mention, potAnte))) {
							if (!potAnte.getPronoun()) {
								ante_idx = idx;
							}
							
						}
					}
				}
				
				
			}
		}
		return ante_idx;
	}
}
