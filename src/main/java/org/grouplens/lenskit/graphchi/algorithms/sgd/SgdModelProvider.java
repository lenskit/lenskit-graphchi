package org.grouplens.lenskit.graphchi.algorithms.sgd;

import org.grouplens.lenskit.graphchi.util.GraphchiSerializer;

import org.grouplens.lenskit.graphchi.util.MatrixEntry;
import org.grouplens.lenskit.graphchi.util.matrices.DenseMatrix;
import org.grouplens.lenskit.graphchi.util.matrixmarket.BufferedReaderMatrixSource;
import org.grouplens.lenskit.graphchi.util.matrixmarket.MatrixSource;
import org.grouplens.lenskit.graphchi.util.matrixmarket.UserItemMatrixSource;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;

import java.io.File;
import java.io.IOException;

public class SgdModelProvider implements Provider<SgdModel> {
    private UserItemMatrixSource trainMatrix;

    private static int globalId = 0;

    private String directory;
    private int featureCount;
    private BoundedClampingFunction clamp;
    private String graphchi;

    @Inject
    public SgdModelProvider( @Nonnull UserItemMatrixSource source,int featureCount, @Nonnull BoundedClampingFunction clamp,
                            @Nonnull String graphchi){
        trainMatrix = source;
        int id = ++globalId;
        directory = "sgd"+id;
        this.featureCount = 20; // Magic number because GraphChi currently doesn't allow runtime configuration of
                                // feature counts.
        this.clamp = clamp;
        this.graphchi = graphchi;
    }

    private void serializeData() throws IOException{
        if(!(new File(directory).mkdir()))
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
        builder.directory(new File(directory));
        builder.command(graphchi+"sgd " +
                "--training=train --validation=train " +
                "--sgd_lambda=1e-4 --sgd_gamma=1e-4 " +
                "--minval="+clamp.lowerBound()+
                "--maxval="+clamp.upperBound()+
                "--max_iter=6 --quiet=1");
        try{
            builder.start();
        }
        catch(IOException e){
            throw new RuntimeException(e);
        }

        String fileroot = directory+"/train";

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
        double[][] vMatrix = new double [v.getMatrixRowCount()][v.getMatrixColumnCount()];

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

        return new SgdModel(new DenseMatrix(uMatrix), new DenseMatrix(vMatrix), trainMatrix, featureCount, clamp);
    }
}
