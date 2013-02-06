/*
 * LensKit, an open source recommender systems toolkit.
 * Copyright 2010-2012 Regents of the University of Minnesota and contributors
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package org.grouplens.lenskit.graphchi.algorithms.sgd;

import org.codehaus.plexus.util.FileUtils;
import org.grouplens.lenskit.baseline.BaselinePredictor;
import org.grouplens.lenskit.core.Transient;
import org.grouplens.lenskit.data.pref.PreferenceDomain;
import org.grouplens.lenskit.graphchi.algorithms.param.FeatureCount;
import org.grouplens.lenskit.graphchi.util.GraphchiSerializer;

import org.grouplens.lenskit.graphchi.util.MatrixEntry;
import org.grouplens.lenskit.graphchi.util.matrices.DenseMatrix;
import org.grouplens.lenskit.graphchi.util.matrixmarket.BufferedReaderMatrixSource;
import org.grouplens.lenskit.graphchi.util.matrixmarket.MatrixSource;
import org.grouplens.lenskit.graphchi.util.matrixmarket.UserItemMatrixSource;
import org.grouplens.lenskit.transform.clamp.ClampingFunction;
import org.grouplens.lenskit.iterative.params.LearningRate;
import org.grouplens.lenskit.iterative.params.RegularizationTerm;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * SGD recommender builder. It uses GraphChi's SGD algorithm to compute the factorized matrices.
 * These are documented at <a href="https://code.google.com/p/graphchi/">its code.google page</a>
 *
 * @author Daniel Gratzer < danny.gratzer@gmail.com >
 */
public class SgdModelProvider implements Provider<SgdModel> {

    private static AtomicInteger globalId = new AtomicInteger(0);
    private static Logger logger = LoggerFactory.getLogger(SgdModelProvider.class);
    private UserItemMatrixSource trainMatrix;
    private String directory;
    private int featureCount;
    private double lambda;
    private double gamma;
    private ClampingFunction clamp;
    private String graphchi;
    private PreferenceDomain domain;
    private BaselinePredictor baseline;

    /**
     *
     * @param source the matrix factorized using GraphChi's SGD algorithm.
     * @param featureCount The number of features the SGD algorithm will use. Currently this is ignored and set to 20.
     * @param lambda The lambda (Regularization Term) supplied to GraphChi.
     * @param gamma The gamma (learning rate) supplied to Graphchi when the source matrix is factored.
     * @param clamp The clamping function which is used only during recommendations, not during the factorization.
     * @param domain The PreferenceDomain containing the upper and lower bounds for the SGD algorithm to clamp with.
     */
    @Inject
    public SgdModelProvider( @Transient @Nonnull UserItemMatrixSource source,@FeatureCount int featureCount,
                             @LearningRate double gamma, @RegularizationTerm double lambda,
                             @Transient @Nonnull ClampingFunction clamp, @Nullable PreferenceDomain domain,
                             @Nullable BaselinePredictor baseline){
        trainMatrix = source;
        if(featureCount != 20) {
            logger.error("Ignoring feature count of {} and using 20 features.", featureCount);
        }
        this.featureCount = 20; // Magic number because GraphChi currently doesn't allow runtime configuration of
                                // feature counts.
        this.gamma = gamma;
        this.lambda = lambda;
        this.clamp = clamp;
        this.domain = domain;
        this.baseline = baseline;

        this.graphchi =  System.getProperty("graphchi.location");
        if(graphchi == null){
            //Attempt to default to CWD?
            logger.error("No path for graphchi found. Defaulting to './graphchi'");
            graphchi = "./graphchi";
        }
        int id = globalId.incrementAndGet();
        directory = "sgd"+id;
    }


    /**
     * get() serializes the input matrix given in the constructor.
     * It then runs GraphChi's SGD algorithm on the file and loads the results into an SGDModel.
     * Finally it deletes the whole temporary directory.
     *
     * If any IOException occurs during the serializing, reading, or executing of graphchi, it is thrown as a RuntimeException.
     * @return an SGDModel with a filled U and V matrix
     */
    public SgdModel get() {
        String currPath = new File(directory).getAbsolutePath()+"/";

        //Serialize data and run graphchi on it
        runGraphchi(currPath);
        String fileroot = currPath+"/train";

        //Get the results
        MatrixSource u;
        MatrixSource v;
        try{
            u = BufferedReaderMatrixSource.getDenseMatrixSource(fileroot + "_U.mm", true, true);
            v = BufferedReaderMatrixSource.getDenseMatrixSource(fileroot + "_V.mm", true, true);
        }
        catch(IOException e){
            throw new RuntimeException(e);
        }

        //These will be used to generate the DenseMatrix objects for the model
        double[][] uMatrix = new double[u.getMatrixRowCount()][u.getMatrixColumnCount()];
        double[][] vMatrix = new double[v.getMatrixRowCount()][v.getMatrixColumnCount()];

        //Populate the U matrix
        for(MatrixEntry entry : u){
            //User Feature -> Preference
            uMatrix[entry.row][entry.column] = entry.rating;
        }
        //Populate the V matrix
        for(MatrixEntry entry : v){
            //Item Feature -> Preference
            vMatrix[entry.row][entry.column] = entry.rating;
        }

        //Clean up temps
        try{
            FileUtils.deleteDirectory(new File(directory));
        }
        catch(IOException e){
            throw new RuntimeException(e);
        }
        return new SgdModel(new DenseMatrix(uMatrix), new DenseMatrix(vMatrix),
                trainMatrix.getUserIndexes(), trainMatrix.getItemIndexes(), featureCount, clamp,
                baseline);
    }

    /*
     * Creates a new directory called sgd**** where **** is an int ID
     * Serializes the source matrix into a matrix-market file.
     * If any exception occurs, it is thrown as a RuntimeException.
     */
    private void serializeData() throws IOException{
        File dir = new File(directory);
        if(!(dir.mkdir()) &&  !dir.exists()) {
            throw new IOException("Couldn't make new directory "+directory);
        }
        GraphchiSerializer.serializeMatrixSource(trainMatrix, directory+"/train");
    }


    /*
     * Calls serialize data and then invokes Graphchi's SGD algorithm in the matrix market format.
     *
     * All resulting files are stored in the same directory as
     */
    private void runGraphchi(String currPath){
        try{
            serializeData();
        }
        catch(IOException e){
            throw new RuntimeException(e);
        }

        //Build and run SGD command.
        ProcessBuilder builder = new ProcessBuilder();
        builder.directory(new File(graphchi));
        builder.command(buildCommand(currPath));
        try {
            Process sgd = builder.start();
            sgd.waitFor();
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    /*
     * Builds the arguments for the SGD command. It supplies an optional upper and lower bound if the PreferenceDomain is given.
     */
    private String[] buildCommand(String path){
        String[] args;
        if(domain!=null){
            args = new String[8];
        }
        else{
            args = new String[6];
        }
        args[0] = "./toolkits/collaborative_filtering/sgd";
        args[1] = "--training="+ path+"train";
        args[2] = "--sgd_lambda="+ lambda;
        args[3] = "--sgd_gamma=" + gamma;
        args[4] = "--max_iter=6";
        args[5] = "--quiet=1";
        if(domain != null){
            args[6] = "--minval="+domain.getMinimum();
            args[7] = "--maxval="+domain.getMaximum();
        }
        return args;
    }
}
