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
package org.grouplens.lenskit.graphchi.util.matrices;

/**
 * The generic interface for readable matrices. It is used for a generic way of handling both dense and sparse matrices
 * throughout the GraphChi interface. It is not generic for speed and space reasons.
 *
 * @author Daniel Gratzer < danny.gratzer@gmail.com >
 */
public interface Matrix {
    /**
     * Retrieves the element at <code>row</code> and <code>column</code>. It provides no guarantees for
     * behavior if the row or column provided are out of bounds.
     *
     * @param row The row of the target item
     * @param column The column of the target item
     * @return The item at (row, column) or an implementation specific error.
     */
    double get(int row, int column);
}
