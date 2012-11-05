package org.grouplens.lenskit.graphchi.util.matrixmarket;

import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import org.grouplens.lenskit.data.dao.EventCollectionDAO;
import org.grouplens.lenskit.data.event.Rating;
import org.grouplens.lenskit.data.event.SimpleRating;
import org.grouplens.lenskit.data.pref.IndexedPreference;
import org.grouplens.lenskit.data.snapshot.PackedPreferenceSnapshot;
import org.grouplens.lenskit.data.snapshot.PreferenceSnapshot;
import org.grouplens.lenskit.graphchi.util.MatrixEntry;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TestPreferenceSnapshotMatrixSource {
    PreferenceSnapshot snapshot;
    PreferenceSnapshotMatrixSource source;

    private static int eid;
    private static Rating generateRating(long uid, long iid, double value, long ts) {
        return new SimpleRating(eid++, uid, iid, value, ts);
    }

    private int rows;
    private int columns;
    private int entries;

    private static double EPSILON = 1e-6;

    @Before
    public void setupTests(){
        List<Rating> rs = new ArrayList<Rating>();
        rs.add(generateRating(1, 7, 4, 1));
        rs.add(generateRating(3, 7, 3, 1));
        rs.add(generateRating(4, 7, 5, 1));
        rs.add(generateRating(4, 7, 4, 2));
        rs.add(generateRating(5, 7, 3, 1));
        rs.add(generateRating(6, 7, 5, 1));
        rs.add(generateRating(1, 8, 4, 1));
        rs.add(generateRating(1, 8, 5, 2));
        rs.add(generateRating(3, 8, 3, 1));
        rs.add(generateRating(4, 8, 2, 1));
        rs.add(generateRating(5, 8, 3, 1));
        rs.add(generateRating(5, 8, 5, 2));
        rs.add(generateRating(6, 8, 5, 1));
        rs.add(generateRating(7, 8, 2, 1));
        rs.add(generateRating(1, 9, 3, 1));
        rs.add(generateRating(3, 9, 4, 1));
        rs.add(generateRating(4, 9, 5, 1));
        rs.add(generateRating(7, 9, 2, 1));
        rs.add(generateRating(7, 9, 3, 2));
        rs.add(generateRating(4, 10, 4, 1));
        rs.add(generateRating(7, 10, 4, 1));
        rs.add(generateRating(1, 11, 5, 1));
        rs.add(generateRating(3, 11, 5, 2));
        rs.add(generateRating(4, 11, 5, 1));
        rs.add(generateRating(20, 20, 8, 1));
        EventCollectionDAO.Factory manager = new EventCollectionDAO.Factory(rs);
        snapshot = new PackedPreferenceSnapshot.Provider(manager.create()).get();
        source = new PreferenceSnapshotMatrixSource(snapshot, false);
        rows = 7;
        columns = 6;
        entries = 21;
    }

    @Test
    public void testMetadata(){
        assertEquals(rows, source.getMatrixRowCount());
        assertEquals(columns, source.getMatrixColumnCount());
        assertEquals(entries, source.getMatrixEntryCount());
        assertFalse(source.isSorted());
    }

    @Test
    public void testContents(){
        Iterator<MatrixEntry> sourceIterator = source.fast().iterator();
        Iterator<IndexedPreference> snapshotIterator = snapshot.getRatings().fastIterator();
        MatrixEntry entry;
        IndexedPreference pref;
        while(sourceIterator.hasNext() && snapshotIterator.hasNext()){
            entry = sourceIterator.next();
            pref = snapshotIterator.next();
            assertEquals(entry.user, pref.getUserIndex());
            assertEquals(entry.item, pref.getItemIndex());
            assertEquals(entry.rating, pref.getValue(), EPSILON);
        }
    }

}
