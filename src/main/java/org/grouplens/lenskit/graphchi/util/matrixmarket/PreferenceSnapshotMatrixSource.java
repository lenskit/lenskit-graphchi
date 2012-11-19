package org.grouplens.lenskit.graphchi.util.matrixmarket;

import org.grouplens.grapht.annotation.DefaultProvider;
import org.grouplens.lenskit.cursors.AbstractPollingCursor;
import org.grouplens.lenskit.data.dao.DataAccessObject;
import org.grouplens.lenskit.data.pref.IndexedPreference;
import org.grouplens.lenskit.data.snapshot.PreferenceSnapshot;
import org.grouplens.lenskit.data.snapshot.PackedPreferenceSnapshot;
import org.grouplens.lenskit.graphchi.util.MatrixEntry;
import org.grouplens.lenskit.util.Index;

import javax.inject.Inject;
import java.util.Iterator;

@DefaultProvider(PreferenceSnapshotMatrixSource.Provider.class)
public class PreferenceSnapshotMatrixSource extends AbstractPollingCursor<MatrixEntry> implements UserItemMatrixSource{

    public static class Provider implements javax.inject.Provider<PreferenceSnapshotMatrixSource>{

        private PreferenceSnapshot snapshot;
        private boolean sorted = false;

        @Inject
        public Provider(DataAccessObject dao){
            snapshot = new PackedPreferenceSnapshot.Provider(dao).get();
        }

        public Provider(DataAccessObject dao, boolean sorted){
            this(dao);
            this.sorted = sorted;
        }

        @Override
        public PreferenceSnapshotMatrixSource get(){
            return new PreferenceSnapshotMatrixSource(snapshot, sorted);
        }
    }

    private int rows;
    private int columns;
    private int entries;
    private boolean isSorted;
    private MatrixEntry nextEntry;
    private Iterator<IndexedPreference> fastIterator;
    private Index userIds;
    private Index itemIds;

     protected PreferenceSnapshotMatrixSource(PreferenceSnapshot snapshot, boolean isSorted){
        super(snapshot.getRatings().size());
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
            return nextEntry.set(pref.getUserIndex()+1, pref.getItemIndex()+1, pref.getValue());
        }
        return null;
    }

    public Index getUserIndexes(){
        return userIds;
    }

    public Index getItemIndexes(){
        return itemIds;
    }
}
