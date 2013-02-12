package org.grouplens.lenskit.graphchi.algorithms;

import org.grouplens.lenskit.graphchi.util.GraphchiSerializer;
import org.grouplens.lenskit.graphchi.util.matrixmarket.UserItemMatrixSource;

import javax.inject.Provider;
import java.io.File;
import java.io.IOException;

abstract public class GraphchiProvider<T> implements Provider<T> {
    private String graphchi;
    private String outputDir;
    private UserItemMatrixSource input;

    public GraphchiProvider(String graphchi, String outputDir, UserItemMatrixSource input){
        this.graphchi = graphchi;
        this.outputDir = outputDir;
        this.input = input;
    }

    public T get(){
        try{
            serializeData(outputDir);
            runGraphchi(outputDir);
            return buildModel();
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    protected abstract T buildModel();
    protected abstract String[] buildCommand();

    /**
     *
     * Serializes the input matrix into a matrixmarket file.
     *
     * @param location The target location for the data
     * @throws IOException If anything in writing out the file throws an IOException
     */
    protected void serializeData(String location) throws IOException{
        File dir = new File(outputDir);
        if(!(dir.mkdir()) &&  !dir.exists()) {
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
        builder.command(buildCommand());
        try {
            Process algorithm = builder.start();
            algorithm.waitFor();
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}
