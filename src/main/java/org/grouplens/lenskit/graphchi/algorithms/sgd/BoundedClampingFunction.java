package org.grouplens.lenskit.graphchi.algorithms.sgd;

import org.grouplens.lenskit.transform.clamp.ClampingFunction;

public interface BoundedClampingFunction extends ClampingFunction {
    int lowerBound();
    int upperBound();
}