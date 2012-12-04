package org.grouplens.lenskit.graphchi.algorithms.sgd;

import org.grouplens.lenskit.core.ScoreBasedItemRecommender;
import org.grouplens.lenskit.data.dao.DataAccessObject;

import javax.inject.Inject;

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