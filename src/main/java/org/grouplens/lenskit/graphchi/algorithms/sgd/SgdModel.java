package org.grouplens.lenskit.graphchi.algorithms.sgd;
import org.grouplens.lenskit.graphchi.util.matrices.Matrix;
import org.grouplens.lenskit.graphchi.util.matrixmarket.UserItemMatrixSource;

public class SgdModel{
    public Matrix u;
    public Matrix v;
    public UserItemMatrixSource source;

    public SgdModel(Matrix u, Matrix v, UserItemMatrixSource source){
        this.u = u;
        this.v = v;
        this.source = source;
    }
}
