package org.grouplens.lenskit.graphchi.util.matrixmarket;

import org.grouplens.lenskit.cursors.AbstractPollingCursor;
import org.grouplens.lenskit.data.pref.IndexedPreference;
import org.grouplens.lenskit.data.snapshot.PreferenceSnapshot;
import org.grouplens.lenskit.graphchi.util.MatrixEntry;
import org.grouplens.lenskit.util.Index;

import java.util.Iterator;

public class PreferenceSnapshotMatrixSource extends AbstractPollingCursor<MatrixEntry> implements MatrixSource{

    private int rows;
    private int columns;
    private int entries;
    private boolean isSorted;
    private MatrixEntry nextEntry;
    private PreferenceSnapshot snapshot;
    private Iterator<IndexedPreference> fastIterator;
    private Index userIds;
    private Index itemIds;

    public PreferenceSnapshotMatrixSource(PreferenceSnapshot snapshot, boolean isSorted){
        super(snapshot.getRatings().size());
        this.snapshot = snapshot;
        fastIterator = snapshot.getRatings().fastIterator();
        nextEntry = new MatrixEntry();
        rows = snapshot.userIndex().getObjectCount();
        columns = snapshot.itemIndex().getObjectCount();
        entries  = super.getRowCount();
        this.isSorted = isSorted;
        userIds = snapshot.userIndex();
        itemIds = snapshot.itemIndex();
    }


    public int getMatrixRowCount(){
        return rows;
    }
    public int getMatrixColumnCount(){
        return columns;
    }
    public int getMatrixEntryCount(){
        return entries;
    }
    public boolean isSorted(){
        return isSorted;
    }


    public MatrixEntry poll(){
        if(fastIterator.hasNext()){
            IndexedPreference pref = fastIterator.next();
            return nextEntry.set(pref.getUserIndex(), pref.getItemIndex(), pref.getValue());
        }
        return null;
    }

    long getUserId(int index){
        return userIds.getId(index);
    }

    long getItemId(int index){
        return itemIds.getId(index);
    }
}
