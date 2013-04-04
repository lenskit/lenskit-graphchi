package org.grouplens.lenskit.graphchi.algorithms.als;

import org.grouplens.lenskit.baseline.BaselinePredictor;
import org.grouplens.lenskit.core.Transient;
import org.grouplens.lenskit.data.pref.PreferenceDomain;
import org.grouplens.lenskit.graphchi.algorithms.GraphchiProvider;
import org.grouplens.lenskit.graphchi.algorithms.param.FeatureCount;
import org.grouplens.lenskit.graphchi.util.MatrixEntry;
import org.grouplens.lenskit.graphchi.util.matrices.DenseMatrix;
import org.grouplens.lenskit.graphchi.util.matrixmarket.BufferedReaderMatrixSource;
import org.grouplens.lenskit.graphchi.util.matrixmarket.MatrixSource;
import org.grouplens.lenskit.graphchi.util.matrixmarket.UserItemMatrixSource;
import org.grouplens.lenskit.iterative.params.RegularizationTerm;
import org.grouplens.lenskit.transform.clamp.ClampingFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class AlsModelProvider extends GraphchiProvider<AlsModel> {
    private static AtomicInteger globalId = new AtomicInteger(0);
    private static Logger logger = LoggerFactory.getLogger(AlsModelProvider.class);


    private int featureCount;
    private UserItemMatrixSource trainMatrix;
    private String graphchi;
    private String directory;
    private double lambda;
    private PreferenceDomain domain;
    private ClampingFunction clamp;
    private BaselinePredictor baseline;

    @Inject
    public AlsModelProvider(@Transient @Nonnull UserItemMatrixSource source,@FeatureCount int featureCount,
                            @RegularizationTerm double lambda,
                            @Transient @Nonnull ClampingFunction clamp, @Nullable PreferenceDomain domain,
                            @Nullable BaselinePredictor baseline)
    {
        super(source);
        if(featureCount != 20){
            logger.error("Ignoring feature count of {} and defaulting to 20 features", featureCount);
        }
        this.featureCount = 20;
        graphchi = System.getProperty("graphchi.location");
        if(graphchi == null){
            logger.error("No path for graphchi found. Defaulting to the home graphchi");
            graphchi = "./graphchi";
        }
        trainMatrix = source;
        this.lambda = lambda;
        this.clamp  = clamp;
        this.domain = domain;
        this.baseline = baseline;
        directory = "als"+globalId.incrementAndGet();
    }

    protected AlsModel gatherResults(String fileroot){
        //Get the results
        MatrixSource u;
        MatrixSource v;
        try{
            u = BufferedReaderMatrixSource.getDenseMatrixSource(fileroot + "_U.mm", true, true);
            v = BufferedReaderMatrixSource.getDenseMatrixSource(fileroot + "_V.mm", true, true);
        }
        catch(IOException e){
            throw new RuntimeException(e);
        }

        //These will be used to generate the DenseMatrix objects for the model
        double[][] uMatrix = new double[u.getMatrixRowCount()][u.getMatrixColumnCount()];
        double[][] vMatrix = new double[v.getMatrixRowCount()][v.getMatrixColumnCount()];

        //Populate the U matrix
        for(MatrixEntry entry : u){
            //User Feature -> Preference
            uMatrix[entry.row][entry.column] = entry.rating;
        }
        //Populate the V matrix
        for(MatrixEntry entry : v){
            //Item Feature -> Preference
            vMatrix[entry.row][entry.column] = entry.rating;
        }
       return new AlsModel(new DenseMatrix(uMatrix), new DenseMatrix(vMatrix),
                trainMatrix.getUserIndexes(), trainMatrix.getItemIndexes(), featureCount, clamp,
                baseline);
    }


    /*
     * Builds the arguments for the ALS command. It supplies an optional upper and lower bound if the PreferenceDomain is given.
     */
    protected String[] buildCommand(String path){
        String[] args;
        if(domain!=null){
            args = new String[7];
        }
        else{
            args = new String[5];
        }
        args[0] = "./toolkits/collaborative_filtering/als";
        args[1] = "--training="+ path+"train";
        args[2] = "--lambda="+ lambda;
        args[3] = "--max_iter=6";
        args[4] = "--quiet=1";
        if(domain != null){
            args[5] = "--minval="+domain.getMinimum();
            args[6] = "--maxval="+domain.getMaximum();
        }
        return args;
    }

}
