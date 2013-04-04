package org.grouplens.lenskit.graphchi.algorithms;

import org.codehaus.plexus.util.FileUtils;
import org.grouplens.lenskit.graphchi.util.GraphchiSerializer;
import org.grouplens.lenskit.graphchi.util.matrixmarket.BufferedReaderMatrixSource;
import org.grouplens.lenskit.graphchi.util.matrixmarket.UserItemMatrixSource;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import javax.inject.Provider;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

abstract public class GraphchiProvider<T> implements Provider<T> {
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

    public T get(){
        try{
            initGraphchi();
            //Get the results
            String fileroot = new File(outputDir).getAbsolutePath()+"/train";
            return gatherResults(fileroot);
        }
        catch(IOException e){
            throw new RuntimeException(e);
        }
        finally {
            try{
                FileUtils.deleteDirectory(new File(outputDir));
            }
            catch(IOException e){
                throw new RuntimeException(e);
            }
        }
    }

   private void initGraphchi() throws IOException{
        serializeData();
        runGraphchi();
    }

    protected abstract String[] buildCommand(String currPath);
    protected abstract T        gatherResults(String path);

    /**
     *
     * Serializes the input matrix into a matrixmarket file.
     *
     * @throws IOException If anything in writing out the file throws an IOException
     */
    protected void serializeData() throws IOException{
        File dir = new File(outputDir);
        if(!(dir.mkdir()) ||  !dir.exists()) {
            throw new IOException("Couldn't make new outputDir "+outputDir);
        }
        GraphchiSerializer.serializeMatrixSource(input, outputDir + "/train");
    }

    /**
     * Invokes graphchi on the serialized data. It uses the command provided by <code>buildCommand</code>
     */
    protected void runGraphchi(){
        ProcessBuilder builder = new ProcessBuilder();
        builder.directory(new File(graphchi));
        String path = new File(outputDir).getAbsolutePath();
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
