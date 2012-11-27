package org.grouplens.lenskit.graphchi.util.matrices;

public class DenseMatrix implements Matrix{
    double[][] entries;

    public DenseMatrix(double[][] entries){
        this.entries = entries;
    }

    public void print(){
        for(double[] j : entries){
            for(double i: j){
                System.out.print(i+" ");
            }
            System.out.println();
        }
    }

    public double get(int row, int column){
        return entries[row][column];
    }
}
