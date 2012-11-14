package org.grouplens.lenskit.graphchi.algorithms.sgd;
import org.grouplens.grapht.annotation.DefaultProvider;
import org.grouplens.lenskit.core.Shareable;
import org.grouplens.lenskit.graphchi.util.matrices.Matrix;
import org.grouplens.lenskit.graphchi.util.matrixmarket.UserItemMatrixSource;
import org.grouplens.lenskit.transform.clamp.ClampingFunction;

import java.io.Serializable;

@DefaultProvider(SgdModelProvider.class)
@Shareable
public class SgdModel implements Serializable {
    public Matrix u;
    public Matrix v;
    public UserItemMatrixSource source;
    public int featureCount;
    public ClampingFunction clamp;

    public SgdModel(Matrix u, Matrix v, UserItemMatrixSource source, int featureCount, ClampingFunction clamp){
        this.u = u;
        this.v = v;
        this.source = source;
        this.featureCount = featureCount;
        this.clamp = clamp;
    }
}
