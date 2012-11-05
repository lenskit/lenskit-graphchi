package org.grouplens.lenskit.graphchi.util.matrixmarket;
import org.grouplens.lenskit.util.Index;

public interface UserItemMatrixSource extends MatrixSource{
    Index getUserIndexes();
    Index getItemIndexes();

}
