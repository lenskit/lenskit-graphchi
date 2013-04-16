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

package org.grouplens.lenskit.graphchi.algorithms.als;

import org.grouplens.lenskit.baseline.BaselinePredictor;
import org.grouplens.lenskit.basic.AbstractRatingPredictor;
import org.grouplens.lenskit.data.Event;
import org.grouplens.lenskit.data.UserHistory;
import org.grouplens.lenskit.data.dao.DataAccessObject;
import org.grouplens.lenskit.graphchi.util.matrices.Matrix;
import org.grouplens.lenskit.transform.clamp.ClampingFunction;
import org.grouplens.lenskit.util.Index;
import org.grouplens.lenskit.vectors.ImmutableSparseVector;
import org.grouplens.lenskit.vectors.MutableSparseVector;
import org.grouplens.lenskit.vectors.VectorEntry;

import javax.annotation.Nonnull;
import javax.inject.Inject;

/**
 * The rating predictor associated with GraphChi's ALS algorithm.
 *
 * Currently it does not use folding in. Rating prediction methods are similar
 * to the ones described at <a href="http://bickson.blogspot.com/2012/08/collaborative-filtering-with-graphchi.html">Dr. Bickson's blog</a>.
 *
 * @author Daniel Gratzer < danny.gratzer@gmail.com >
 */
public class AlsRatingPredictor extends AbstractRatingPredictor {
    private Matrix users;
    private Matrix items;
    private Index userIds;
    private Index itemIds;
    private int featureCount;
    private ClampingFunction clamp;
    private BaselinePredictor baseline;

    @Inject
    public AlsRatingPredictor(AlsModel model, DataAccessObject dao){
        super(dao);
        users = model.u;
        items = model.v;
        userIds = model.userIndex;
        itemIds = model.itemIndex;
        featureCount = model.featureCount;
        clamp = model.clamp;
        baseline = model.baseline;
    }

    /**
     * No folding in is allowed, so this returns false.
     * @return false.
     */
    @Override
    public boolean canUseHistory(){
        return false;
    }

    @Override
    public double predict(long user, long item){
        int uid = userIds.getIndex(user);
        int iid = itemIds.getIndex(item);
        if (iid == -1) {
            return Double.NaN;
        }
        double score = 0.0;
        for(int i = 0; i < featureCount; ++i){
            score += users.get(uid, i) * items.get(iid, i);
        }
        return clamp.apply(user, item, score);
    }
    /**
     * Ignores the user's history and predicts each item using the <code>predict(long, MutableSparseVector)</code> method.
     */
    public void predict( @Nonnull UserHistory<? extends Event> profile,  @Nonnull MutableSparseVector predicts){
        long user = profile.getUserId();
        for(VectorEntry i : predicts.fast(VectorEntry.State.EITHER)){

            //If we can't find this item, don't predict for it
            if(itemIds.getIndex(i.getKey()) == -1) {
                predicts.clear(i);
                continue;
            }
            predicts.set(i, predict(user, i.getKey()));
        }

        //Catch all the unset items and predict for them
        baseline.predict(user, new ImmutableSparseVector(), predicts, false);
    }
}
