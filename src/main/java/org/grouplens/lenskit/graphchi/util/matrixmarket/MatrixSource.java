package org.grouplens.lenskit.graphchi.util.matrixmarket;


import org.grouplens.grapht.annotation.DefaultImplementation;
import org.grouplens.lenskit.cursors.Cursor;
import org.grouplens.lenskit.graphchi.util.MatrixEntry;

@DefaultImplementation(PreferenceSnapshotMatrixSource.class)
public interface MatrixSource extends Cursor<MatrixEntry>{
    public int getMatrixRowCount();
    public int getMatrixColumnCount();
    public int getMatrixEntryCount();
    public boolean isSorted();
}
