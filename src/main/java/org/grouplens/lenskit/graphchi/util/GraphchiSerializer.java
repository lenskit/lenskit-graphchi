package org.grouplens.lenskit.graphchi.util;


import it.unimi.dsi.fastutil.ints.AbstractIntComparator;
import org.grouplens.lenskit.graphchi.util.matrixmarket.MatrixSource;

import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import static it.unimi.dsi.fastutil.Arrays.quickSort;

/**
 * This class provides a way to serialize and load implementations of the {@code MatrixSource} interface into MatrixSource Market
 * compliant files. In order to make it suitable for use with the Sliding Window and Sharding algorithms used in
 * GraphChi, the outputs are sorted, however it does not require this of files used for loading.
 */
public class GraphchiSerializer {

    /**
     * Will serialize the target matrixmarket and store it in sorted MatrixSource Market form in {@code filename}
     *
     * Will throw an exception if it is unable to open and write to filename.
     *
     * @param source the matrixmarket which will be serialized.
     * @param filename the file which will be written to.
     * @throws IOException if any IOExceptions occur during the process of opening writing to and closing the file.
     * Nothing is caught.
     */
    public static void serializeMatrixSource(MatrixSource source, String filename) throws IOException {
        BufferedWriter io = new BufferedWriter(new FileWriter(filename));
        io.write("%%MatrixMarket matrix coordinate real general");
        io.newLine();
        io.write(source.getMatrixRowCount()+" "+source.getMatrixColumnCount()+" "+source.getMatrixEntryCount());
        io.newLine();

        if(source.isSorted()){
            writeSorted(source, io);
            return;
        }

        writeUnsorted(source, io);

    }

    private static void writeSorted(MatrixSource source, BufferedWriter io) throws IOException{
        for(MatrixEntry entry: source.fast()){
            io.write(entry.toString());
            io.newLine();
        }
        io.close();
        source.close();
    }

    private static void writeUnsorted(MatrixSource source, BufferedWriter io) throws IOException{
        long[] users = new long[source.getMatrixEntryCount()];
        long[] items = new long[source.getMatrixEntryCount()];
        double[] ratings = new double[source.getMatrixEntryCount()];
        int index = 0;
        for(MatrixEntry entry : source.fast()){
            users[index] = entry.user;
            items[index] = entry.item;
            ratings[index] = entry.rating;
            ++index;
        }
        quickSort(0, users.length, new Comparator(users, items, ratings), new Swapper(users, items, ratings));
        for(int i = 0; i<users.length; ++i){
            io.write(users[i]+" "+items[i]+" "+ratings[i]);
            io.newLine();
        }
        io.close();
        source.close();
    }

    private static class Swapper implements it.unimi.dsi.fastutil.Swapper{
        long[] users;
        long[] items;
        double[] ratings;

        public Swapper(long[] users, long[] items, double[] ratings){
            this.users = users;
            this.items = items;
            this.ratings = ratings;
        }

        public void swap(int i1, int i2){
            long usersTemp = users[i1];
            users[i1] = users[i2];
            users[i2] = usersTemp;

            long itemsTemp = items[i1];
            items[i1] = items[i2];
            items[i2] = itemsTemp;

            double ratingsTemp = ratings[i1];
            ratings[i1] = ratings[i2];
            ratings[i2] = ratingsTemp;
        }
    }

    private static class Comparator extends AbstractIntComparator {
        long[] users;
        long[] items;
        double[] ratings;

        public Comparator(long[] users, long[] items, double[] ratings){
            this.users = users;
            this.items = items;
            this.ratings = ratings;
        }

        private int compareLongs(long a, long b){
            if(a == b)
                return 0;
            return a>b ? 1 : -1;
        }

        public int compare(int i1, int i2){
            if(users[i1] == users[i2])
                return compareLongs(items[i1], items[i2]);

            return compareLongs(users[i1], users[i2]);
        }

    }
}