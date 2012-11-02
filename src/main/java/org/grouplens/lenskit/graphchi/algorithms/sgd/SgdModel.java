package org.grouplens.lenskit.graphchi.algorithms.sgd;

import org.grouplens.lenskit.graphchi.util.matrices.Matrix;

public class SgdModel {
    Matrix u;
    Matrix v;

    public SgdModel(Matrix u, Matrix v){
        this.u = u;
        this.v = v;
    }


}
