package org.grouplens.lenskit.graphchi.util.matrixmarket;
import org.grouplens.grapht.annotation.DefaultImplementation;
import org.grouplens.lenskit.util.Index;

@DefaultImplementation(PreferenceSnapshotMatrixSource.class)
public interface UserItemMatrixSource extends MatrixSource{
    Index getUserIndexes();
    Index getItemIndexes();

}
