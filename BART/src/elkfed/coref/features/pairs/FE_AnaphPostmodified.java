/*
 * FE_CoRef.java
 *
 * Created on July 17, 2007, 2:54 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package elkfed.coref.features.pairs;
import edu.stanford.nlp.trees.Tree;
import elkfed.coref.*;
import java.util.List;
import elkfed.ml.*;


/**
 * Feature used to identify in training whether two mentions are coreferent with each other. Either T/F
 * @author vae2101
 */
public class FE_AnaphPostmodified implements PairFeatureExtractor {
 
     public static final FeatureDescription<Boolean> FD_ANAPHPOSTMOD=
            new FeatureDescription<Boolean>(FeatureType.FT_BOOL, "AnaphPostmod");
     
     public void describeFeatures(List<FeatureDescription> fds) {
         fds.add(FD_ANAPHPOSTMOD);
     }

     public void extractFeatures(PairInstance inst) {
        inst.setFeature(FD_ANAPHPOSTMOD, isAnaphoraPostmodified(inst));
    }
     
      public boolean isAnaphoraPostmodified(PairInstance inst) {
        List<Tree> postmods = inst.getAnaphor().getPostmodifiers();
        boolean isPostmod = false;
        if (!(postmods == null) && !postmods.isEmpty()) {
           isPostmod = true;
        }
        return isPostmod;
    //
      }
     

    
}
