package org.grouplens.lenskit.graphchi.algorithms.sgd.param;

import org.grouplens.grapht.annotation.DefaultString;
import org.grouplens.lenskit.core.Parameter;

import javax.inject.Qualifier;
import java.lang.annotation.*;

@Documented
@DefaultString(".")
@Parameter(String.class)
@Qualifier
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface GraphchiLocation {
}