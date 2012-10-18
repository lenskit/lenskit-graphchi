package org.grouplens.lenskit.graphchi.util.matrixmarket;


import org.grouplens.lenskit.cursors.Cursor;
import org.grouplens.lenskit.graphchi.util.MatrixEntry;
import java.io.Closeable;

public interface MatrixSource extends Cursor<MatrixEntry>{
    public int getMatrixRowCount();
    public int getMatrixColumnCount();
    public int getMatrixEntryCount();
    public boolean isSorted();
}
