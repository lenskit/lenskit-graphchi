package org.grouplens.lenskit.graphchi.algorithms.param;

import org.grouplens.grapht.annotation.DefaultInteger;
import org.grouplens.lenskit.core.Parameter;

import javax.inject.Qualifier;
import java.lang.annotation.*;

/**
 * The annotation for the number of features used for an algorithm.
 *
 * There may be odd warnings associated with this tag since graphchi
 * currently doesn't allow configuration of features without a rebuild.
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