package elkfed.coref.algorithms.sieve;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.cscott.jutil.DisjointSet;
import elkfed.coref.CorefResolver;
import elkfed.coref.discourse_entities.DiscourseEntity;
import elkfed.coref.eval.LinkScorer;
import elkfed.coref.eval.SplitLinkScorer;
import elkfed.coref.mentions.Mention;

/**
* Implements the decodeDocument method which walks through the document
* and runs the appropriate sieve for every mention.
*
* @author sebastianruder
* 
*/
public class SieveDecoder implements CorefResolver {
	protected static final Logger _logger = Logger.getAnonymousLogger();
	protected LinkScorer _scorer = new SplitLinkScorer();
	protected SieveFactory _factory = new SieveFactory();
	
	public DisjointSet<Mention> decodeDocument(List<Mention> mentions,
			Map<Mention, Mention> antecedents) {
		// creates data structure where disjoint sets of mentions are
		// going to be stored
		DisjointSet<Mention> mention_clusters = new DisjointSet<Mention>();
		Evaluation eval = new Evaluation(mentions);
		Sieve sieve;
		// counts number of links
		int numLinks = 0;		
        _logger.log(Level.INFO,
                String.format("%s: decode document with %d mentions\n",
                getClass().getSimpleName(),
                mentions.size()));        
        // counts number of walk_throughs
        for (int walk_through = 1; walk_through < 11; walk_through++) {
        	//condition to exclude/include specific sieve
//        	if (walk_through == 1) {
//        		continue;
//        	}
        	// the approriate sieve is created by the factory based on the walk-through
        	sieve = _factory.createSieve(walk_through, mentions);
	    	String sieveName = sieve.getName();  
	    	// the sieve runs through the mentions
		    for (int i = 0; i < mentions.size(); i++) {
		    	// indefinite mentions are filtered out by default
		    	if (mentions.get(i).getIndefinite()) {
		    		continue;
		    	}
		    	/* ante_idx is the index of the antecedent;
		    	 * if no-one is found, it is set to -1 by the sieve
		    	 */
	    		int ante_idx = sieve.runSieve(mentions.get(i));
		    	if (ante_idx==-1) {
		           _scorer.scoreNonlink(mentions,i);
		        }
		    	else {
		            numLinks++;
		            mention_clusters.union(mentions.get(i),mentions.get(ante_idx));
		            antecedents.put(mentions.get(i), mentions.get(ante_idx));
		            
		            if (!(mentions.get(i).getDiscourseEntity() == mentions.get(ante_idx).getDiscourseEntity())) {
		            	//need better solution to stop merging of already merged entities
		            	mentions.get(i).linkToAntecedent(mentions.get(ante_idx));
		            	eval.setLink(mentions.get(i), mentions.get(ante_idx), sieveName);
		            }
		           //mentions.get(i).linkToAntecedent(mentions.get(ante_idx));
		            //Kontrollausgabe
		            DiscourseEntity d = mentions.get(i).getDiscourseEntity();
		            DiscourseEntity dAnte = mentions.get(ante_idx).getDiscourseEntity();
		            if (!(d == dAnte)) {
		            	System.err.println("error: not merged");
		            }
		            _scorer.scoreLink(mentions, ante_idx, i);
		            if (_logger.isLoggable(Level.FINE)) {
		                Object[] args={mentions.get(i),mentions.get(ante_idx)};
		                    _logger.log(Level.FINE,
		                                "joining %s and %s\n",
		                                args);
		            }
		        }
		    }
        }
        _logger.log(Level.INFO,String.format("joined %d pairs in %d mentions",
                numLinks,mentions.size()));
        
        eval.printEvaluation();
	    //_scorer.displayResults();
	    return mention_clusters;
	}

    public void printStatistics() {
        _scorer.displayResultsShort();
    }
    
}
