package org.grouplens.lenskit.graphchi.algorithms.sgd;

import org.grouplens.lenskit.graphchi.util.matrixmarket.BufferedReaderMatrixSource;

import java.io.IOException;

public class SgdResult {
    private String fileroot;

    public SgdResult(String fileroot){
        this.fileroot = fileroot;
    }

    public BufferedReaderMatrixSource getUMatrix() throws IOException {
        return BufferedReaderMatrixSource.getDenseMatrixSource(fileroot+"_U.mm", true, true);
    }

    public BufferedReaderMatrixSource getVMatrix() throws IOException{
        return BufferedReaderMatrixSource.getDenseMatrixSource(fileroot+"_V.mm", true, true);
    }
}
