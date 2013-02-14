package org.grouplens.lenskit.graphchi.algorithms;

import org.codehaus.plexus.util.FileUtils;
import org.grouplens.lenskit.graphchi.util.GraphchiSerializer;
import org.grouplens.lenskit.graphchi.util.matrixmarket.UserItemMatrixSource;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import javax.inject.Provider;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

abstract public class GraphchiProvider {
    private static Logger logger = LoggerFactory.getLogger("GraphchiProvider");
    private static AtomicInteger globalId = new AtomicInteger(0);

    private String graphchi;
    protected String outputDir;
    protected UserItemMatrixSource input;

    public GraphchiProvider(UserItemMatrixSource input){
        this.graphchi =  System.getProperty("graphchi.location");
        if(graphchi == null){
            logger.error("No path for graphchi found. Defaulting to './graphchi'");
            graphchi = "./graphchi";
        }
        this.outputDir = "graphchiProvider"+globalId.getAndIncrement();
        this.input = input;
    }

   public void initGraphchi() throws IOException{
        serializeData(outputDir);
        runGraphchi(outputDir);
    }

    protected abstract String[] buildCommand(String currPath);

    /**
     *
     * Serializes the input matrix into a matrixmarket file.
     *
     * @param location The target location for the data
     * @throws IOException If anything in writing out the file throws an IOException
     */
    protected void serializeData(String location) throws IOException{
        File dir = new File(outputDir);
        if(!(dir.mkdir()) ||  !dir.exists()) {
            throw new IOException("Couldn't make new outputDir "+outputDir);
        }
        GraphchiSerializer.serializeMatrixSource(input, outputDir + "/train");
    }

    /**
     * Invokes graphchi on the serialized data. It uses the command provided by <code>buildCommand</code>
     * @param currPath The path to run the command from
     */
    protected void runGraphchi(String currPath){
        ProcessBuilder builder = new ProcessBuilder();
        builder.directory(new File(graphchi));
        String path = new File(outputDir).getAbsolutePath();
        System.out.println("Stuff should be written to: " + path);
        builder.command(buildCommand(path));
        try {
            Process algorithm = builder.start();
            algorithm.waitFor();
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}
