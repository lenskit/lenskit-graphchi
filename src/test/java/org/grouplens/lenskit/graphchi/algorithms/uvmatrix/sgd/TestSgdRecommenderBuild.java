package org.grouplens.lenskit.graphchi.algorithms.uvmatrix.sgd;

import org.grouplens.lenskit.ItemRecommender;
import org.grouplens.lenskit.RatingPredictor;
import org.grouplens.lenskit.Recommender;
import org.grouplens.lenskit.RecommenderBuildException;
import org.grouplens.lenskit.core.LenskitRecommender;
import org.grouplens.lenskit.core.LenskitRecommenderEngine;
import org.grouplens.lenskit.core.LenskitRecommenderEngineFactory;
import org.grouplens.lenskit.data.dao.DAOFactory;
import org.grouplens.lenskit.data.dao.EventCollectionDAO;
import org.grouplens.lenskit.data.event.Rating;
import org.grouplens.lenskit.data.event.Ratings;
import org.grouplens.lenskit.graphchi.algorithms.param.FeatureCount;
import org.grouplens.lenskit.graphchi.util.matrixmarket.PreferenceSnapshotMatrixSource;
import org.grouplens.lenskit.graphchi.util.matrixmarket.UserItemMatrixSource;
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
        factory.bind(UserItemMatrixSource.class).to(PreferenceSnapshotMatrixSource.class);
        factory.bind(RatingPredictor.class).to(SgdRatingPredictor.class);
        factory.bind(ItemRecommender.class).to(SgdRecommender.class);

        engine = factory.create();
    }

    @Test
    public void testSgdEngineCreate(){
        Recommender r = engine.open();
        try{
            assertThat(r.getItemRecommender(), instanceOf(SgdRecommender.class));
            assertThat(r.getRatingPredictor(), instanceOf(SgdRatingPredictor.class));
        }
        finally{
            r.close();
        }
    }

    @SuppressWarnings("unchecked")
	@Test
    public void testConfigSeparation() {
        LenskitRecommender rec1 = null;
        LenskitRecommender rec2 = null;
        try {
            rec1 = engine.open();
            rec2 = engine.open();

            assertThat(rec1.getItemScorer(),
                    not(sameInstance(rec2.getItemScorer())));
            assertThat(rec1.get(SgdModel.class),
                    allOf(not(nullValue()),
                            sameInstance(rec2.get(SgdModel.class))));
        } finally {
            if (rec2 != null) {
                rec2.close();
            }
            if (rec1 != null) {
                rec1.close();
            }
        }
    }
}