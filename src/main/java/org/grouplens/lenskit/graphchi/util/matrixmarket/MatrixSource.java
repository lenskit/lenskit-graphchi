package org.grouplens.lenskit.graphchi.util.matrixmarket;


import org.grouplens.lenskit.cursors.Cursor;
import org.grouplens.lenskit.graphchi.util.MatrixEntry;
import java.io.Closeable;

public interface MatrixSource extends Cursor<MatrixEntry>{
    public int getRowCount();
    public int getColumnCount();
    public int getEntryCount();
}
