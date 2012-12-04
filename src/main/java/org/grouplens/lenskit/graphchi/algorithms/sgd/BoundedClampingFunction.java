package org.grouplens.lenskit.graphchi.algorithms.sgd;

import org.grouplens.lenskit.transform.clamp.ClampingFunction;

public class  BoundedClampingFunction implements ClampingFunction {
    public int lowerBound = 1;
    public int upperBound = 5;

    public double apply(long u, long i, double val){
       if(val > 5)
           return 5;
        if(val<0)
            return 0;
        return val;
    }
}