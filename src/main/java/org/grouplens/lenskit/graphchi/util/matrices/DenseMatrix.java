package org.grouplens.lenskit.graphchi.util.matrices;

public class DenseMatrix implements Matrix{
    double[][] entries;

    public DenseMatrix(double[][] entries){
        this.entries = entries;
    }

    public double get(int row, int column){
        return entries[row][column];
    }
}
