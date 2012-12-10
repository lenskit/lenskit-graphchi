package org.grouplens.lenskit.graphchi.algorithms.sgd;

import org.codehaus.plexus.util.FileUtils;
import org.grouplens.lenskit.core.Transient;
import org.grouplens.lenskit.data.pref.PreferenceDomain;
import org.grouplens.lenskit.graphchi.algorithms.sgd.param.FeatureCount;
import org.grouplens.lenskit.graphchi.algorithms.sgd.param.GraphchiLocation;
import org.grouplens.lenskit.graphchi.util.GraphchiSerializer;

import org.grouplens.lenskit.graphchi.util.MatrixEntry;
import org.grouplens.lenskit.graphchi.util.matrices.DenseMatrix;
import org.grouplens.lenskit.graphchi.util.matrixmarket.BufferedReaderMatrixSource;
import org.grouplens.lenskit.graphchi.util.matrixmarket.MatrixSource;
import org.grouplens.lenskit.graphchi.util.matrixmarket.UserItemMatrixSource;
import org.grouplens.lenskit.transform.clamp.ClampingFunction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Provider;

import java.io.File;
import java.io.IOException;

public class SgdModelProvider implements Provider<SgdModel> {
    private UserItemMatrixSource trainMatrix;

    private static int globalId = 0;

    private String directory;
    private int featureCount;
    private ClampingFunction clamp;
    private String graphchi;
    private PreferenceDomain domain;

    @Inject
    public SgdModelProvider( @Transient @Nonnull UserItemMatrixSource source,@FeatureCount int featureCount, @Transient @Nonnull ClampingFunction clamp,
                            @GraphchiLocation @Nonnull String graphchi, @Nullable PreferenceDomain domain){
        trainMatrix = source;
        int id = ++globalId;
        directory = "sgd"+id;
        this.featureCount = 20; // Magic number because GraphChi currently doesn't allow runtime configuration of
                                // feature counts.
        this.clamp = clamp;
        this.graphchi = graphchi;
        this.domain = domain;
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
        builder.command(buildCommand(currPath));
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
            uMatrix[entry.user][entry.column] = entry.rating;
        }

        //Populate the V matrix
        for(MatrixEntry entry : v){
            //Item Feature -> Preference
            vMatrix[entry.user][entry.column] = entry.rating;
        }
        try{ //Clean up temps
            FileUtils.deleteDirectory(new File(directory));

        }
        catch(IOException e){
            throw new RuntimeException(e);
        }
        return new SgdModel(new DenseMatrix(uMatrix), new DenseMatrix(vMatrix),
                trainMatrix.getUserIndexes(), trainMatrix.getItemIndexes(), featureCount, clamp);
    }

    private String[] buildCommand(String path){
        String[] args;
        if(domain!=null){
            args = new String[8];
        }
        else{
            args = new String[6];
        }
        args[0] = "./toolkits/collaborative_filtering/sgd";
        args[1] = "--training="+ path+"train";
        args[2] = "--sgd_lambda=.015";
        args[3] = "--sgd_gamma=1e-3" ;
        args[4] = "--max_iter=6";
        args[5] = "--quiet=1";
        if(domain != null){
            args[6] = "--minval="+domain.getMinimum();
            args[7] = "--maxval="+domain.getMaximum();
        }
        return args;
    }
}
