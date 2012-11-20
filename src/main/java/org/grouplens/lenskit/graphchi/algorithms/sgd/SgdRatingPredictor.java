package org.grouplens.lenskit.graphchi.algorithms.sgd;

import org.grouplens.lenskit.RatingPredictor;
import org.grouplens.lenskit.data.Event;
import org.grouplens.lenskit.data.UserHistory;
import org.grouplens.lenskit.graphchi.util.matrices.Matrix;
import org.grouplens.lenskit.transform.clamp.ClampingFunction;
import org.grouplens.lenskit.util.Index;
import org.grouplens.lenskit.vectors.MutableSparseVector;
import org.grouplens.lenskit.vectors.SparseVector;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Collection;

public class SgdRatingPredictor implements RatingPredictor{

    private Matrix users;
    private Matrix items;
    private Index userIds;
    private Index itemIds;
    private int featureCount;
    private ClampingFunction clamp;

    @Inject
    public SgdRatingPredictor(SgdModel model){
        users = model.u;
        items = model.v;
        userIds = model.userIndex;
        itemIds = model.itemIndex;
        featureCount = model.featureCount;
        clamp = model.clamp;
    }


    public double score(long user, long item){
        int uid = userIds.getIndex(user);
        int iid = itemIds.getIndex(item);

        double score = 0.0;

        for(int i = 0; i < featureCount; ++i){
            score += users.get(uid, i) * items.get(iid, i);
            score = clamp.apply(user, item, score);
        }
        return score;
    }


    @Nonnull
    public SparseVector score(long user,  @Nonnull Collection<Long> items){
        MutableSparseVector vector = new MutableSparseVector(items);
        for(long i : vector.keySet()){
            vector.set(i, score(user, i));
        }
        return vector.freeze();
    }

    public void score(long user,  @Nonnull MutableSparseVector scores){
        for(long i : scores.keySet()){
            scores.set(i, score(user, i));
        }
    }

    public boolean canUseHistory(){
        return false;
    }

    public double score( @Nonnull UserHistory<? extends Event> profile, long item){
         return score(profile.getUserId(), item);
    }

    @Nonnull
    public SparseVector score( @Nonnull UserHistory<? extends Event> profile,  @Nonnull Collection<Long> items){
        return score(profile.getUserId(), items);
    }

    public void score( @Nonnull UserHistory<? extends Event> profile,  @Nonnull MutableSparseVector scores){
        score(profile.getUserId(), scores);
    }
}
