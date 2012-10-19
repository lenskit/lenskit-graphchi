package org.grouplens.lenskit.graphchi.util.matrixmarket;

import org.grouplens.lenskit.graphchi.util.MatrixEntry;
import org.junit.*;
import static org.junit.Assert.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

public class TestBufferedReaderMatrixSource {

    private static String filename1 = "testbufferedreader-nodata.txt.tmp";
    private static String filename2 = "testbufferedreader-metadata.txt.tmp";
    private static ArrayList<Double> data;

    private static final double EPSILON = 1e-6;

    private BufferedReaderMatrixSource metaDataSource;
    private BufferedReaderMatrixSource noDataSource;

    private int rows;
    private int columns;
    private int entries;

    @BeforeClass
    public static void createFile() throws IOException {
        BufferedWriter writerMetaData = new BufferedWriter(new FileWriter(filename1));
        writerMetaData.write("%%MatrixMarket matrix array real general\n"+
                "3 4\n"+
                "0.8147\n"+
                "0.9058\n"+
                "0.1270\n"+
                "0\n"+
                "0.6324\n"+
                "0.0975\n"+
                "0.2785\n"+
                "0.5469\n"+
                "0.9575\n"+
                "0\n"+
                "0.1576\n"+
                "0");

        writerMetaData.close();

        BufferedWriter writerNoData = new BufferedWriter(new FileWriter(filename2));
        writerNoData.write("0.8147\n"+
                "0.9058\n"+
                "0.1270\n"+
                "0\n"+
                "0.6324\n"+
                "0.0975\n"+
                "0.2785\n"+
                "0.5469\n"+
                "0.9575\n"+
                "0\n"+
                "0.1576\n"+
                "0");
        writerNoData.close();
    }

    @AfterClass
    public static void removeFile() throws IOException {
        new File(filename1).delete();
        new File(filename2).delete();
    }

    @Before
    public void setupSources() throws IOException{
        metaDataSource = BufferedReaderMatrixSource.getDenseMatrixSource(filename1, true, false);
        noDataSource = BufferedReaderMatrixSource.getDenseMatrixSource(filename2, false, false);

        rows = 4;
        columns = 3;
        entries = 12;

        data = new ArrayList<Double>();
        data.add(.8147);
        data.add(.9058);
        data.add(.1270);
        data.add(.0);
        data.add(.6324);
        data.add(.0975);
        data.add(.2785);
        data.add(.5469);
        data.add(.9575);
        data.add(.0);
        data.add(.1576);
        data.add(.0);
    }

    @After
    public void closeSources() throws IOException{
        metaDataSource.close();
        noDataSource.close();
    }

    @Test
    public void testMetaData(){
        assertEquals(rows, metaDataSource.getMatrixRowCount());
        assertEquals(columns, metaDataSource.getMatrixColumnCount());
        assertEquals(entries, metaDataSource.getMatrixEntryCount());

        assertEquals(entries, noDataSource.getMatrixEntryCount());
    }

    @Test
    public void testContents(){
        Iterator<Double> dataIterator = data.iterator();

        while(dataIterator.hasNext()){
            double value = dataIterator.next();
            assertEquals(value, metaDataSource.next().rating, EPSILON);
            assertEquals(value, noDataSource.next().rating, EPSILON);
        }
    }

    @Test
    public void testPositions(){
        MatrixEntry entry;
        for(int i = 1; i<=columns; ++i)
            for(int j = 1; j<=rows; ++j){
                entry = metaDataSource.next();
                assertEquals(i, entry.item);
                assertEquals(j, entry.user);
            }
    }

}
