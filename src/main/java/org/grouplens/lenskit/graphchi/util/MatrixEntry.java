package org.grouplens.lenskit.graphchi.util;

public class MatrixEntry implements Cloneable{
    public int user;
    public int item;
    public double rating;

    public MatrixEntry set(int user, int item, double rating){
        this.user = user;
        this.item = item;
        this.rating = rating;
        return this;
    }

    public MatrixEntry clone(){
        return new MatrixEntry().set(user, item, rating);
    }

    public String toString(){
        return user+" "+item +" "+rating;
    }
}
