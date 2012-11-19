package org.grouplens.lenskit.graphchi.util.algorithms;

import org.grouplens.lenskit.ItemRecommender;
import org.grouplens.lenskit.RatingPredictor;
import org.grouplens.lenskit.Recommender;
import org.grouplens.lenskit.RecommenderBuildException;
import org.grouplens.lenskit.baseline.BaselinePredictor;
import org.grouplens.lenskit.baseline.UserMeanPredictor;
import org.grouplens.lenskit.core.LenskitRecommenderEngine;
import org.grouplens.lenskit.core.LenskitRecommenderEngineFactory;
import org.grouplens.lenskit.data.dao.DAOFactory;
import org.grouplens.lenskit.data.dao.EventCollectionDAO;
import org.grouplens.lenskit.data.event.Rating;
import org.grouplens.lenskit.data.event.Ratings;
import org.grouplens.lenskit.graphchi.algorithms.sgd.BoundedClampingFunction;
import org.grouplens.lenskit.graphchi.algorithms.sgd.SgdRatingPredictor;
import org.grouplens.lenskit.graphchi.algorithms.sgd.SgdRecommender;
import org.grouplens.lenskit.graphchi.algorithms.sgd.param.FeatureCount;
import org.grouplens.lenskit.graphchi.algorithms.sgd.param.GraphchiLocation;
import org.grouplens.lenskit.graphchi.util.matrixmarket.PreferenceSnapshotMatrixSource;
import org.grouplens.lenskit.graphchi.util.matrixmarket.UserItemMatrixSource;
import org.grouplens.lenskit.params.IterationCount;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.List;

public class TestSgdRecommenderBuild {


    private LenskitRecommenderEngine engine;
    @Before
    public void init() throws RecommenderBuildException{
        List<Rating> rs = new ArrayList<Rating>();
        rs.add(Ratings.make(1, 5, 2));
        rs.add(Ratings.make(1, 7, 4));
        rs.add(Ratings.make(8, 4, 5));
        rs.add(Ratings.make(8, 5, 4));

        DAOFactory daoFactory = new EventCollectionDAO.Factory(rs);

        LenskitRecommenderEngineFactory factory = new LenskitRecommenderEngineFactory(daoFactory);
        factory.bind(Integer.class).withQualifier(FeatureCount.class).to(20);
        factory.bind(String.class).withQualifier(GraphchiLocation.class).to("/home/danny");
        factory.bind(UserItemMatrixSource.class).to(PreferenceSnapshotMatrixSource.class);
        factory.bind(RatingPredictor.class).to(SgdRatingPredictor.class);
        factory.bind(ItemRecommender.class).to(SgdRecommender.class);

        engine = factory.create();
    }

    @Test
    public void testSgdEngineCreate(){
        Recommender r = engine.open();
        try{
            assertThat(r.getRatingPredictor(), instanceOf(SgdRecommender.class));
            assertThat(r.getRatingPredictor(), instanceOf(SgdRatingPredictor.class));
        }
        finally{
            r.close();
        }
    }
}