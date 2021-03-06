package elkfed.coref.algorithms.sieve;

import java.util.List;
import elkfed.coref.PairInstance;
import elkfed.coref.mentions.Mention;

/**
 * This Sieve links a Mention to an antecedent, if its head word matches any
 * head word of the antecedents Discourse Entity. Additionally it needs to meet
 * the word inclusion, the compatible modifiers and the not I-within-I
 * requirements.
 * 
 * 
 * @see #entityHeadMatch(PairInstance)
 * @see #wordInclusion(PairInstance)
 * @see #compatibleModifiers(PairInstance)
 * @see #IWithinI(PairInstance)
 * 
 * @author Julian
 * 
 */
public class StrictHeadMatchASieve extends Sieve {

	public StrictHeadMatchASieve(List<Mention> mentions) {
		this.mentions = mentions;
		this.name = "StrictHeadMatchASieve";
	}

	public int runSieve(Mention mention) {

		int mention_idx = mentions.indexOf(mention);
		int ante_idx = -1;

		for (int idx = 0; idx < mention_idx; idx++) {
			Mention ante = mentions.get(idx);
			PairInstance pair = new PairInstance(mention, ante);
			if (entityHeadMatch(pair) && wordInclusion(pair)
					&& compatibleModifiers(pair) && !(IWithinI(pair))) {
				ante_idx = idx;

			}
		}
		return ante_idx;
	}
}
