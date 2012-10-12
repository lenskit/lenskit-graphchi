package org.grouplens.lenskit.graphchi.util;

/**
 * User: Danny Gratzer
 * Date: 27/09/12
 * Time: 3:18 PM
 */

import org.grouplens.lenskit.cursors.Cursor;
import org.grouplens.lenskit.graphchi.util.matrixmarket.BufferedReaderMatrixSource;
import org.grouplens.lenskit.graphchi.util.matrixmarket.MatrixSource;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class provides a way to serialize and load implementations of the {@code MatrixSource} interface into MatrixSource Market
 * compliant files. In order to make it suitable for use with the Sliding Window and Sharding algorithms used in
 * GraphChi, the outputs are sorted, however it does not require this of files used for loading.
 */
public class GraphchiParser {
    /**
     * Should parse the file at {@code filename} according to MatrixSource Market syntax and return the matrixmarket cursor.
     *
     * Will throw an exception if the file is not found.
     * @param filename the name of the file to be parsed.
     * @return A BufferedReaderMatrixSource to the file.
     */
    public BufferedReaderMatrixSource parseFile(String filename) throws IOException {
        BufferedReader io = new BufferedReader(new FileReader(filename));
        io.mark(1);
        while(io.read()=='%'){
            io.readLine();
            io.mark(1);
        }
        io.reset();
        return new BufferedReaderMatrixSource(io, -1, -1, -1);//As of right now, no meta data is associated with GraphChi output,
                                                //the null will hold the place of any metadata until this is added,
    }


    /**
     * Will serialize the target matrixmarket and store it in sorted MatrixSource Market form in {@code filename}
     *
     * Will throw an exception if it is unable to open and write to filename.
     *
     * @param source the matrixmarket which will be serialized.
     * @param filename the file which will be written to.
     */
    public void serializeMatrixSource(MatrixSource source, String filename) throws IOException {
        ArrayList<String> output = new ArrayList<String>();
        while(source.hasNext()){
            output.add(source.fastNext().toString());
        }
        Collections.sort(output);
        BufferedWriter io = new BufferedWriter(new FileWriter(filename));
        io.write("%%MatrixSourceMarket matrixmarket coordinate real general\n");
        io.write(source.getRowCount()+" "+source.getColumnCount()+" "+source.getEntryCount());

        for(String next:output){
            io.write(next);
            io.newLine();
        }
        io.close();
        source.close();

    }
}
