package org.grouplens.lenskit.graphchi.algorithms.sgd;

import org.codehaus.plexus.util.FileUtils;
import org.grouplens.lenskit.core.Transient;
import org.grouplens.lenskit.graphchi.algorithms.sgd.param.FeatureCount;
import org.grouplens.lenskit.graphchi.algorithms.sgd.param.GraphchiLocation;
import org.grouplens.lenskit.graphchi.util.GraphchiSerializer;

import org.grouplens.lenskit.graphchi.util.MatrixEntry;
import org.grouplens.lenskit.graphchi.util.matrices.DenseMatrix;
import org.grouplens.lenskit.graphchi.util.matrixmarket.BufferedReaderMatrixSource;
import org.grouplens.lenskit.graphchi.util.matrixmarket.MatrixSource;
import org.grouplens.lenskit.graphchi.util.matrixmarket.UserItemMatrixSource;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Provider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class SgdModelProvider implements Provider<SgdModel> {
    private UserItemMatrixSource trainMatrix;

    private static int globalId = 0;

    private String directory;
    private int featureCount;
    private BoundedClampingFunction clamp;
    private String graphchi;

    @Inject
    public SgdModelProvider( @Transient @Nonnull UserItemMatrixSource source,@FeatureCount int featureCount, @Transient @Nonnull BoundedClampingFunction clamp,
                            @GraphchiLocation @Nonnull String graphchi){
        trainMatrix = source;
        int id = ++globalId;
        directory = "sgd"+id;
        this.featureCount = 20; // Magic number because GraphChi currently doesn't allow runtime configuration of
                                // feature counts.
        this.clamp = clamp;
        this.graphchi = graphchi;
    }

    private void serializeData() throws IOException{
        File dir = new File(directory);
        if(!(dir.mkdir()) &&  !dir.exists())
            throw new IOException("Couldn't make new directory "+directory);
        GraphchiSerializer.serializeMatrixSource(trainMatrix, directory+"/train");
    }


    public SgdModel get() {
        try{
            serializeData();
        }
        catch(IOException e){
            throw new RuntimeException(e);
        }

        ProcessBuilder builder = new ProcessBuilder();
        String currPath = new File(directory).getAbsolutePath()+"/";
        builder.directory(new File(graphchi));
        builder.command("./toolkits/collaborative_filtering/sgd" ,
                "--training="+ currPath+"train",
                "--sgd_lambda=.015",
                "--sgd_gamma=1e-3" ,
                "--minval="+clamp.lowerBound,
                " --maxval="+clamp.upperBound,
                " --max_iter=6",
                "--quiet=1");
        try {
            Process sgd = builder.start();
            sgd.waitFor();

        }
        catch(IOException e){
            throw new RuntimeException(e);
        }
        catch(InterruptedException e){
            throw new RuntimeException(e);
        }
        String fileroot = currPath+"/train";
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
            uMatrix[entry.user][entry.item] = entry.rating;
        }

        //Populate the V matrix
        for(MatrixEntry entry : v){
            //Item Feature -> Preference
            vMatrix[entry.user][entry.item] = entry.rating;
        }
        try{
            FileUtils.deleteDirectory(new File(directory));

        }
        catch(IOException e){
            throw new RuntimeException(e);
        }
        return new SgdModel(new DenseMatrix(uMatrix), new DenseMatrix(vMatrix),
                trainMatrix.getUserIndexes(), trainMatrix.getItemIndexes(), featureCount, clamp);
    }
}
