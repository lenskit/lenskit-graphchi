package org.grouplens.lenskit.graphchi.util;

public class MatrixEntry implements Cloneable{
    public int row;
    public int column;
    public double rating;

    public MatrixEntry set(int user, int item, double rating){
        this.row = user;
        this.column = item;
        this.rating = rating;
        return this;
    }

    public MatrixEntry clone(){
        return new MatrixEntry().set(row, column, rating);
    }

    public String toString(){
        return row +" "+ column +" "+rating;
    }
}
