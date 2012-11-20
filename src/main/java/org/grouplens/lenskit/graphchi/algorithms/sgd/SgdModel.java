package org.grouplens.lenskit.graphchi.algorithms.sgd;
import org.grouplens.grapht.annotation.DefaultProvider;
import org.grouplens.lenskit.core.Shareable;
import org.grouplens.lenskit.graphchi.util.matrices.Matrix;
import org.grouplens.lenskit.graphchi.util.matrixmarket.UserItemMatrixSource;
import org.grouplens.lenskit.transform.clamp.ClampingFunction;
import org.grouplens.lenskit.util.Index;

import java.io.Serializable;

@DefaultProvider(SgdModelProvider.class)
@Shareable
public class SgdModel implements Serializable {
    public Matrix u;
    public Matrix v;
    public int featureCount;
    public Index userIndex;
    public Index itemIndex;
    public ClampingFunction clamp;

    public SgdModel(Matrix u, Matrix v, Index uids, Index iids, int featureCount, ClampingFunction clamp){
        userIndex = uids;
        itemIndex = iids;
        this.u = u;
        this.v = v;
        this.featureCount = featureCount;
        this.clamp = clamp;
    }
}
