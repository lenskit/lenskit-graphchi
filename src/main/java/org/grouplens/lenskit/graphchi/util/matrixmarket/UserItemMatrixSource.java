package org.grouplens.lenskit.graphchi.util.matrixmarket;
import org.grouplens.grapht.annotation.DefaultImplementation;
import org.grouplens.lenskit.util.Index;

@DefaultImplementation(PreferenceSnapshotMatrixSource.class)
public interface UserItemMatrixSource extends MatrixSource{
    /**
     * @return An index mapping the users' indices to their user IDs
     */
    Index getUserIndexes();

    /**
     * @return An index mapping the users indices to their item IDs
     */
    Index getItemIndexes();
}
