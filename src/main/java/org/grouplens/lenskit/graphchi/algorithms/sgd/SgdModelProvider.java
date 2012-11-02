package org.grouplens.lenskit.graphchi.algorithms.sgd;

import org.grouplens.lenskit.graphchi.util.GraphchiSerializer;
import org.grouplens.lenskit.graphchi.util.MatrixEntry;
import org.grouplens.lenskit.graphchi.util.matrices.DenseMatrix;
import org.grouplens.lenskit.graphchi.util.matrixmarket.BufferedReaderMatrixSource;
import org.grouplens.lenskit.graphchi.util.matrixmarket.MatrixSource;
import javax.inject.Provider;

import java.io.File;
import java.io.IOException;

public class SgdModelProvider implements Provider<SgdResult> {
    private MatrixSource trainMatrix;
    private MatrixSource testMatrix;

    private static int globalId = 0;

    private int id;

    private String directory;

    public SgdModelProvider(MatrixSource train, MatrixSource test){
        trainMatrix = train;
        testMatrix = test;
        id = ++globalId;
        directory = "sgd"+id;
    }

    public void serializeData() throws IOException{
        if(!(new File("sgd"+id).mkdir()))
            throw new IOException("Couldn't make new directory sgd"+id);
        GraphchiSerializer.serializeMatrixSource(trainMatrix, directory+"/train");
        GraphchiSerializer.serializeMatrixSource(testMatrix, directory+"/test");
    }


    public SgdResult get() {
        try{
            serializeData();
        }
        catch(IOException e){
            throw new RuntimeException(e);
        }

        //Todo Compute graphchi result, block until complete
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
            uMatrix[entry.user][entry.item] = entry.rating;
        }

        //Populate the V matrix
        for(MatrixEntry entry : v){
            vMatrix[entry.user][entry.item] = entry.rating;
        }
        return new SgdModel(new DenseMatrix(uMatrix), new DenseMatrix(vMatrix));
    }
}
