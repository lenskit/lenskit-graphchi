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
import org.grouplens.grapht.annotation.DefaultProvider;
import org.grouplens.lenskit.core.Shareable;
import org.grouplens.lenskit.graphchi.util.matrices.Matrix;
import org.grouplens.lenskit.transform.clamp.ClampingFunction;
import org.grouplens.lenskit.util.Index;

import java.io.Serializable;

@DefaultProvider(SgdModelProvider.class)
@Shareable
public class SgdModel implements Serializable {
    public Matrix u;
    public Matrix v;
    public int featureCount;
    public Index userIndex;
    public Index itemIndex;
    public ClampingFunction clamp;

    public SgdModel(Matrix u, Matrix v, Index uids, Index iids, int featureCount, ClampingFunction clamp){
        userIndex = uids;
        itemIndex = iids;
        this.u = u;
        this.v = v;
        this.featureCount = featureCount;
        this.clamp = clamp;
    }
}
