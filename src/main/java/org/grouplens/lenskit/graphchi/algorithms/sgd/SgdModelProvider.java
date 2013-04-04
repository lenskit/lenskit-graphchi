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

import org.grouplens.lenskit.baseline.BaselinePredictor;
import org.grouplens.lenskit.core.Transient;
import org.grouplens.lenskit.data.pref.PreferenceDomain;
import org.grouplens.lenskit.graphchi.algorithms.GraphchiProvider;
import org.grouplens.lenskit.graphchi.algorithms.param.FeatureCount;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;

/**
 * SGD recommender builder. It uses GraphChi's SGD algorithm to compute the factorized matrices.
 * These are documented at <a href="https://code.google.com/p/graphchi/">its code.google page</a>
 *
 * @author Daniel Gratzer < danny.gratzer@gmail.com >
 */
public class SgdModelProvider extends GraphchiProvider<SgdModel>  {

    private static Logger logger = LoggerFactory.getLogger(SgdModelProvider.class);
    private int featureCount;
    private double lambda;
    private double gamma;
    private ClampingFunction clamp;
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
        super(source);
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

    }


    protected SgdModel gatherResults(String fileroot) {
        MatrixSource u;
        MatrixSource v;
        try{
            //Get the results
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
        return new SgdModel(new DenseMatrix(uMatrix), new DenseMatrix(vMatrix),
                super.input.getUserIndexes(), super.input.getItemIndexes(), featureCount, clamp,
                baseline);
    }

    /*
     * Builds the arguments for the SGD command. It supplies an optional upper and lower bound if the PreferenceDomain is given.
     */
    protected String[] buildCommand(String currPath){
        String[] args;
        if(domain!=null){
            args = new String[8];
        }
        else{
            args = new String[6];
        }
        args[0] = "./toolkits/collaborative_filtering/sgd";
        args[1] = "--training="+ currPath+"/train";
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
