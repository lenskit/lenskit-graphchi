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

import org.grouplens.lenskit.basic.ScoreBasedItemRecommender;
import org.grouplens.lenskit.data.dao.DataAccessObject;

import javax.inject.Inject;

/**
 * The recommender for the GraphChi SGD Algorithm. Currently it doesn't use user history.
 * Other than this, it behaves identically to ScoreBasedItemRecommender.
 *
 * @author Daniel Gratzer < danny.gratzer@gmail.com >
 */
public class SgdRecommender extends ScoreBasedItemRecommender {

    @Inject
    public SgdRecommender(DataAccessObject dao, SgdRatingPredictor pred){
        super (dao, pred);
    }

    @Override
    public boolean canUseHistory(){
        return false;
    }

}