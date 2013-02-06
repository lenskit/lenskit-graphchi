package org.grouplens.lenskit.graphchi.algorithms.als;

import org.grouplens.lenskit.baseline.BaselinePredictor;
import org.grouplens.lenskit.core.Transient;
import org.grouplens.lenskit.data.pref.PreferenceDomain;
import org.grouplens.lenskit.graphchi.algorithms.param.FeatureCount;
import org.grouplens.lenskit.graphchi.util.matrixmarket.UserItemMatrixSource;
import org.grouplens.lenskit.iterative.params.RegularizationTerm;
import org.grouplens.lenskit.transform.clamp.ClampingFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Provider;
import java.util.concurrent.atomic.AtomicInteger;

public class AlsModelProvider implements Provider<AlsModel> {
    private static AtomicInteger globalId = new AtomicInteger(0);
    private static Logger logger = LoggerFactory.getLogger(AlsModelProvider.class);


    private UserItemMatrixSource trainMatrix;
    private String directory;
    private double lambda;
    private PreferenceDomain domain;
    private ClampingFunction clamp;

    public AlsModelProvider(@Transient @Nonnull UserItemMatrixSource source,@FeatureCount int featureCount,
                            @RegularizationTerm double lambda,
                            @Transient @Nonnull ClampingFunction clamp, @Nullable PreferenceDomain domain,
                            @Nullable BaselinePredictor baseline)
    {
        if(featureCount != 20){
            logger.error("Ignoring feature count of {} and defaulting to 20 features", featureCount);
            trainMatrix = source;
            this.lambda = lambda;
            this.clamp  = clamp;
            this.domain = domain;
        }

    }
    public AlsModel get(){

        return null;
    }

}
