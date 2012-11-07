package org.grouplens.lenskit.graphchi.algorithms.sgd;

import org.grouplens.lenskit.RatingPredictor;
import org.grouplens.lenskit.data.Event;
import org.grouplens.lenskit.data.UserHistory;
import org.grouplens.lenskit.vectors.MutableSparseVector;
import org.grouplens.lenskit.vectors.SparseVector;

import javax.annotation.Nonnull;
import java.util.Collection;

public class SgdRatingPredictor implements RatingPredictor{

    private SgdModel model;
    public SgdRatingPredictor(SgdModel model){
        this.model = model;
    }


    public double score(long user, long item){
        return 0.0;
    }

    @Nonnull
    public SparseVector score(long user, @Nonnull Collection<Long> items){
        return null;
    }

    public void score(long user, @Nonnull MutableSparseVector scores){

    }


    public boolean canUseHistory(){
        return false;
    }

    public double score(@Nonnull UserHistory<? extends Event> profile, long item){
         return 0.0;
    }

    @Nonnull
    public SparseVector score(@Nonnull UserHistory<? extends Event> profile, @Nonnull Collection<Long> items){
        return null;
    }

    public void score(@Nonnull UserHistory<? extends Event> profile, @Nonnull MutableSparseVector scores){

    }
}
