package org.grouplens.lenskit.graphchi.algorithms.sgd.param;

import org.grouplens.grapht.annotation.DefaultInteger;
import org.grouplens.lenskit.core.Parameter;

import javax.inject.Qualifier;
import java.lang.annotation.*;

/**
 * The annotation for the number of features used for the SGD algorithm.
 *
 * It defaults to 40.
 */
@Documented
@DefaultInteger(40)
@Parameter(Integer.class)
@Qualifier
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface FeatureCount {
}