package org.grouplens.lenskit.graphchi.util;

import static org.junit.Assert.*;

import org.grouplens.lenskit.collections.FastCollection;
import org.grouplens.lenskit.data.snapshot.PreferenceSnapshot;
import  org.junit.*;

import org.grouplens.lenskit.data.dao.EventCollectionDAO;
import org.grouplens.lenskit.data.event.Rating;
import org.grouplens.lenskit.data.event.SimpleRating;
import org.grouplens.lenskit.data.snapshot.PackedPreferenceSnapshot;
import org.grouplens.lenskit.graphchi.util.matrixmarket.PreferenceSnapshotMatrixSource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TestGraphChiSerializer {
    private BufferedReader outputTester;

    private static int rows;
    private static int columns;
    private static int entries;

    static private PreferenceSnapshot prefSnapshot;
    static private String filename = "testserializer.txt.tmp";

    private static int eid;
    private static Rating generateRating(long uid, long iid, double value, long ts) {
        return new SimpleRating(eid++, uid, iid, value, ts);
    }

    @BeforeClass
    public static void serialize() throws IOException {
        List<Rating> rs = new ArrayList<Rating>();
        rs.add(generateRating(1, 1, 4, 1));
        rs.add(generateRating(1, 2, 3, 1));
        rs.add(generateRating(1, 3, 5, 1));
        rs.add(generateRating(2, 1, 4, 1));
        rs.add(generateRating(2, 2, 3, 1));
        rs.add(generateRating(2, 3, 5, 1));
        rs.add(generateRating(3, 1, 3, 1));
        rs.add(generateRating(3, 2, 3, 1));
        rs.add(generateRating(3, 3, 3, 1));
        EventCollectionDAO.Factory manager = new EventCollectionDAO.Factory(rs);
        prefSnapshot = new PackedPreferenceSnapshot.Provider(manager.create()).get();

        rows = 3;
        columns = 3;
        entries = 9;

        GraphchiSerializer.serializeMatrixSource( new PreferenceSnapshotMatrixSource(prefSnapshot, false),filename);
    }

    @AfterClass
    public static void cleanup() throws IOException{
        new File(filename).delete();
    }

    @Before
    public void setup() throws IOException{
        outputTester = new  BufferedReader(new FileReader(filename));
    }

    @After
    public void close() throws IOException{
        outputTester.close();
    }

    @Test
    public void verifyTag() throws IOException{
        assertEquals(outputTester.readLine(), "%%MatrixMarket matrix coordinate real general");
    }

    @Test
    public void verifyMetadata() throws IOException{
        outputTester.readLine(); //Discard the tag
        Scanner scanner = new Scanner(outputTester.readLine());
        assertEquals(rows, scanner.nextInt());
        assertEquals(columns, scanner.nextInt());
        assertEquals(entries, scanner.nextInt());
        scanner.close();
    }

    @Test
    public void verifyContents() throws IOException{
        String expectedContents = "%%MatrixMarket matrix coordinate real general\n" +
                "3 3 9\n" +
                "0 0 4.0\n" +
                "0 1 3.0\n" +
                "0 2 5.0\n" +
                "1 0 4.0\n" +
                "1 1 3.0\n" +
                "1 2 5.0\n" +
                "2 0 3.0\n" +
                "2 1 3.0\n" +
                "2 2 3.0\n";
        StringBuilder fileContents = new StringBuilder();
        for(String line; (line=outputTester.readLine())!=null;){
            fileContents.append(line+"\n");
        }
        assertEquals(fileContents.toString(), expectedContents);
    }

}
