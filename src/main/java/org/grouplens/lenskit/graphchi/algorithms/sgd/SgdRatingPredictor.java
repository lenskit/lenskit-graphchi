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

import org.grouplens.lenskit.RatingPredictor;
import org.grouplens.lenskit.baseline.BaselinePredictor;
import org.grouplens.lenskit.data.Event;
import org.grouplens.lenskit.data.UserHistory;
import org.grouplens.lenskit.graphchi.util.matrices.Matrix;
import org.grouplens.lenskit.transform.clamp.ClampingFunction;
import org.grouplens.lenskit.util.Index;
import org.grouplens.lenskit.vectors.ImmutableSparseVector;
import org.grouplens.lenskit.vectors.MutableSparseVector;
import org.grouplens.lenskit.vectors.SparseVector;
import org.grouplens.lenskit.vectors.VectorEntry;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Collection;

/**
 * The rating predictor associated with GraphChi's SGD algorithm.
 *
 * Currently it does not use folding in. Rating prediction methods are similar
 * to the ones described at <a href="http://bickson.blogspot.com/2012/08/collaborative-filtering-with-graphchi.html">Dr. Bickson's blog</a>.
 *
 * @author Daniel Gratzer < danny.gratzer@gmail.com >
 */
public class SgdRatingPredictor implements RatingPredictor{

    private Matrix users;
    private Matrix items;
    private Index userIds;
    private Index itemIds;
    private int featureCount;
    private ClampingFunction clamp;
    private BaselinePredictor baseline;

    @Inject
    public SgdRatingPredictor(SgdModel model){
        users = model.u;
        items = model.v;
        userIds = model.userIndex;
        itemIds = model.itemIndex;
        featureCount = model.featureCount;
        clamp = model.clamp;
        baseline = model.baseline;
    }

    /**
     * Predicts a score for the given user and item.
     * @param user The user ID of the target user.
     * @param item The item ID of the target item.
     * @return The predicted score for the user and item.
     */
    public double score(long user, long item){
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
     * Scores a collection of items for a user.
     * @param user The user's ID
     * @param items A collection of the items' IDs
     * @return A SparseVector of scores in the same order as their items.
     */
    @Nonnull
    public SparseVector score(long user,  @Nonnull Collection<Long> items){
        MutableSparseVector vector = new MutableSparseVector(items);
        score(user, vector);
        return vector.freeze();
    }

    /**
     * Scores a list of items for a given user and sets the MutableSparseVector with the scores.
     * @param user The user's ID.
     * @param vector The mutable sparse vector containing the item IDs. that will be set with each item's score.
     */
    public void score(long user,  @Nonnull MutableSparseVector vector){
        for(VectorEntry i : vector.fast(VectorEntry.State.EITHER)){

            //If we can't find this item, don't predict for it
            if(itemIds.getIndex(i.getKey()) == -1) {
                vector.clear(i);
                continue;
            }
            vector.set(i, score(user, i.getKey()));
        }

        //Catch all the unset items and predict for them
        baseline.predict(user, new ImmutableSparseVector(), vector, false);
    }

    /**
     * No folding in is allowed, so this returns false.
     * @return false.
     */
    public boolean canUseHistory(){
        return false;
    }

    /**
     * Ignores the user's history and scores the item using the <code>score(long, long)</code> method.
     */
    public double score( @Nonnull UserHistory<? extends Event> profile, long item){
        return score(profile.getUserId(), item);
    }

    /**
     * Ignores the user's history and scores each item using the <code>score(long, Collection<long>)</code> method.
     */
    @Nonnull
    public SparseVector score( @Nonnull UserHistory<? extends Event> profile,  @Nonnull Collection<Long> items){
        return score(profile.getUserId(), items);
    }

    /**
     * Ignores the user's history and scores each item using the <code>score(long, MutableSparseVector)</code> method.
     */
    public void score( @Nonnull UserHistory<? extends Event> profile,  @Nonnull MutableSparseVector scores){
        score(profile.getUserId(), scores);
    }
}
