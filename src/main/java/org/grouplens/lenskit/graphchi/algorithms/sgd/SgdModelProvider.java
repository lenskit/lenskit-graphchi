package org.grouplens.lenskit.graphchi.algorithms.sgd;

import org.grouplens.lenskit.graphchi.util.GraphchiSerializer;
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


    public SgdResult get() {//throws IOException{
        //Todo Compute graphchi result
        return new SgdResult(directory+"/train");
    }
}
