package org.grouplens.lenskit.graphchi.util.matrixmarket;

import org.grouplens.lenskit.cursors.AbstractPollingCursor;
import org.grouplens.lenskit.data.snapshot.PackedPreferenceSnapshot;
import org.grouplens.lenskit.graphchi.util.MatrixEntry;

public class PackedRatingMatrixSource extends AbstractPollingCursor<MatrixEntry> implements MatrixSource{

    private int rows;
    private int columns;
    private int entries;
    private MatrixEntry matrix;
    private PackedPreferenceSnapshot snapshot;

    public PackedRatingMatrixSource(int rows, int columns, int entries, PackedPreferenceSnapshot snapshot){
        this.rows = rows;
        this.columns = columns;
        this.entries = entries;
        this.snapshot = snapshot;
    }

    public int getRowCount(){
        return rows;
    }

    public int getColumnCount(){
        return columns;
    }

    public int getEntryCount(){
        return entries;
    }


    public MatrixEntry poll(){

        return null;
    }

    private void nextEntry(){

    }
}
