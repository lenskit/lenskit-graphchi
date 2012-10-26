package org.grouplens.lenskit.graphchi.algorithms;

import org.grouplens.lenskit.graphchi.util.GraphchiSerializer;
import org.grouplens.lenskit.graphchi.util.matrixmarket.MatrixSource;

import java.io.IOException;

public class SGD {
    private MatrixSource trainMatrix;
    private MatrixSource testMatrix;

    private static int globalId = 0;

    private int id;

    public SGD(MatrixSource train, MatrixSource test){
        trainMatrix = train;
        testMatrix = test;
        id = ++globalId;
    }

    public void serialize() throws IOException{
        GraphchiSerializer.serializeMatrixSource(trainMatrix, "sgd-"+id+"-train");
        GraphchiSerializer.serializeMatrixSource(testMatrix, "sgd-"+id+"-test");
    }

    public void compute(){
        //TODO call graphchi's sgd
    }
}
