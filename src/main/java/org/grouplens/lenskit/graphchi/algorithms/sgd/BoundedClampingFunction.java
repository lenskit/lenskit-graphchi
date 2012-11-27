package org.grouplens.lenskit.graphchi.algorithms.sgd;

import org.grouplens.lenskit.transform.clamp.ClampingFunction;

public class  BoundedClampingFunction implements ClampingFunction {
    public int lowerBound = 1;
    public int upperBound = 5;

    public double apply(long u, long i, double val){
        return val;
    }
}