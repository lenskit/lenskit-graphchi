package org.grouplens.lenskit.graphchi.util.matrixmarket;
import  org.grouplens.lenskit.cursors.AbstractPollingCursor;
import org.grouplens.lenskit.cursors.Cursor;
import org.grouplens.lenskit.graphchi.util.MatrixEntry;

import java.io.IOException;
import java.io.BufferedReader;
import java.util.regex.Pattern;

public class BufferedReaderMatrixSource extends AbstractPollingCursor<MatrixEntry> implements MatrixSource{
    private int columns;
    private int rows;
    private int entries;
    private BufferedReader inputSource;
    private MatrixEntry entry;
    private final Pattern whiteSpacePattern = Pattern.compile("\\s+");

    public BufferedReaderMatrixSource(BufferedReader inputSource, int columns, int rows, int entries){
        super(entries);
        this.columns = columns;
        this.rows = rows;
        this.entries = entries;
        this.inputSource = inputSource;
        entry = new MatrixEntry();
    }


    public int getColumnCount(){
        return columns;
    }
    public int getRowCount(){
        return rows;
    }
    public int getEntryCount(){
        return entries;
    }

    /**
     * @return the next line of our matrix wrapped in a MatrixEntry or null if we have reached the end of the file.
     */
    public MatrixEntry poll(){
        return parseLine()? entry : null;
    }

    public void close(){
        try{
            inputSource.close();
        }
        catch(IOException e){
            throw new RuntimeException("Error while closing BufferedReader");
        }
    }

    private boolean parseLine(){
        String nextLine;
        try{
            nextLine = inputSource.readLine();
        }
        catch(IOException e){
            //If this happens, major things are wrong, crash hard.
            throw new RuntimeException("Error while reading from file ");
        }
        if(nextLine==null)
            return false;

        String[] data = whiteSpacePattern.split(nextLine);
        entry.set(Long.parseLong(data[0]), Long.parseLong(data[1]), Double.parseDouble(data[2]));
        return true;
    }

}



