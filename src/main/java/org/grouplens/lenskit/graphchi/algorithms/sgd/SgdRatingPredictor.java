package org.grouplens.lenskit.graphchi.algorithms.sgd;

import org.grouplens.lenskit.RatingPredictor;
import org.grouplens.lenskit.data.Event;
import org.grouplens.lenskit.data.UserHistory;
import org.grouplens.lenskit.graphchi.util.matrices.Matrix;
import org.grouplens.lenskit.transform.clamp.ClampingFunction;
import org.grouplens.lenskit.util.Index;
import org.grouplens.lenskit.vectors.MutableSparseVector;
import org.grouplens.lenskit.vectors.SparseVector;

import java.util.Collection;

public class SgdRatingPredictor implements RatingPredictor{

    private Matrix users;
    private Matrix items;
    private Index userIds;
    private Index itemIds;
    private int featureCount;
    private ClampingFunction clamp;

    public SgdRatingPredictor(SgdModel model){
        users = model.u;
        items = model.v;
        userIds = model.source.getUserIndexes();
        itemIds = model.source.getItemIndexes();
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


    public SparseVector score(long user,  Collection<Long> items){
        MutableSparseVector vector = new MutableSparseVector(items);
        for(long i : vector.keySet()){
            vector.set(i, score(user, i));
        }
        return vector.freeze();
    }

    public void score(long user,  MutableSparseVector scores){
        for(long i : scores.keySet()){
            scores.set(i, score(user, i));
        }
    }

    public boolean canUseHistory(){
        return false;
    }

    public double score( UserHistory<? extends Event> profile, long item){
         return score(profile.getUserId(), item);
    }

    public SparseVector score( UserHistory<? extends Event> profile,  Collection<Long> items){
        return score(profile.getUserId(), items);
    }

    public void score( UserHistory<? extends Event> profile,  MutableSparseVector scores){
        score(profile.getUserId(), scores);
    }
}
