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

package org.grouplens.lenskit.graphchi.algorithms.uvmatrix.sgd;

import org.grouplens.lenskit.data.dao.DataAccessObject;
import org.grouplens.lenskit.graphchi.algorithms.uvmatrix.UVRatingPredictor;
import javax.inject.Inject;

/**
 * The rating predictor associated with GraphChi's SGD algorithm.
 *
 * Currently it does not use folding in. Rating prediction methods are similar
 * to the ones described at <a href="http://bickson.blogspot.com/2012/08/collaborative-filtering-with-graphchi.html">Dr. Bickson's blog</a>.
 *
 * @author Daniel Gratzer < danny.gratzer@gmail.com >
 */
public class SgdRatingPredictor extends UVRatingPredictor {
    @Inject
    SgdRatingPredictor(SgdModel model, DataAccessObject dao){
        super(model, dao);
    }
}

