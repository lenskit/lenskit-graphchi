package org.grouplens.lenskit.graphchi.util.matrixmarket;
import  org.grouplens.lenskit.cursors.AbstractPollingCursor;
import org.grouplens.lenskit.graphchi.util.MatrixEntry;

import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.regex.Pattern;

public class BufferedReaderMatrixSource extends AbstractPollingCursor<MatrixEntry> implements MatrixSource{
    private int columns;
    private int rows;
    private int entries;
    private boolean isSorted;
    private BufferedReader inputSource;
    private MatrixEntry entry;
    private static Pattern whiteSpacePattern = Pattern.compile("\\s+");

    private int currentRow = 0;
    private int currentColumn = 1;

    private BufferedReaderMatrixSource(BufferedReader inputSource, int columns, int rows, int entries, boolean isSorted){
        super(entries);
        this.columns = columns;
        this.rows = rows;
        this.entries = entries;
        this.isSorted = isSorted;
        this.inputSource = inputSource;
        entry = new MatrixEntry();
    }

    public static BufferedReaderMatrixSource getDenseMatrixSource(String filename, boolean hasMetadata, boolean isSorted) throws IOException{
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        if(hasMetadata){
            reader.mark(1);
            while(reader.read()=='%'){
                reader.readLine();
                reader.mark(1);
            }
            reader.reset();
            String[] metadata = whiteSpacePattern.split(reader.readLine());
            int rows = Integer.parseInt(metadata[0]);
            int columns = Integer.parseInt(metadata[1]);
            return new BufferedReaderMatrixSource(reader, rows, columns, rows*columns, isSorted);
        }
        else{
            int entries = 0;
            for(String line; (line = reader.readLine()) != null; ++entries){
                if(line.charAt(0) == '%')
                    --entries;
            }
            return new BufferedReaderMatrixSource(new BufferedReader(new FileReader(filename)), -1, -1, entries, isSorted);
        }
    }

    public int getMatrixColumnCount(){
        return columns;
    }
    public int getMatrixRowCount(){
        return rows;
    }
    public int getMatrixEntryCount(){
        return entries;
    }
    public boolean isSorted(){
        return isSorted;
    }

    @Override
    public int getRowCount(){
       return entries;
    }

    /**
     * @return the next line of our Matrix wrapped in a MatrixEntry or null if we have reached the end of the file.
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

        double rating = Double.parseDouble(nextLine);
        int c = 0;
        int r = 0;
        if(rows == -1 || columns == -1){
            r = -1;
            c = -1;
        }

        if(currentRow == rows){
            r = 1;
            currentRow = 1;
            c = ++currentColumn;
        }
        else{
            r = ++currentRow;
            c = currentColumn;
        }

        entry.set(r-1, c-1, rating);
        return true;
    }

}

