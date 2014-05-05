/*
 * Copyright 2007 Yannick Versley / Univ. Tuebingen
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
package elkfed.ml.tournament;

import elkfed.ml.FeatureDescription;
import elkfed.ml.FeatureExtractor;
import elkfed.ml.FeatureType;
import elkfed.ml.Instance;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author versley
 */
public class CombinerDiff implements FeatureExtractor {
    protected final List<FeatureDescription> singleFeatures;
    protected final List<FeatureDescription> newFeatures;
    
    public CombinerDiff(List<FeatureDescription> features)
    {
        singleFeatures=features;
        newFeatures=new ArrayList<FeatureDescription>(features.size());
        for (FeatureDescription fd : singleFeatures)
        {
            assert fd.type == FeatureType.FT_SCALAR;
            FeatureDescription f1=new FeatureDescription(
                    FeatureType.FT_SCALAR,
                    Double.class, fd.name + "_diff");
            newFeatures.add(f1);
        }
    }
    
    public void describeFeatures(List fds) {
        fds.addAll(newFeatures);
    }

    public void extractFeatures(Instance inst0) {
        CandPairInstance inst=(CandPairInstance) inst0;
        for (int i=0; i<singleFeatures.size(); i++)
        {
            FeatureDescription fd=singleFeatures.get(i);
            FeatureDescription<Double> f1=newFeatures.get(i);
            Number n1=(Number)inst.inst1.getFeature(fd);
            Number n2=(Number)inst.inst2.getFeature(fd);
            if (n1!=null && n2!=null)
            {
                inst.setFeature(f1, n1.doubleValue()-n2.doubleValue());
            }
        }
    }
}
