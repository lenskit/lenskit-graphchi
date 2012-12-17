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
 * The implementation of the Matrix interface for dense matrices. This class is immutable.
 * It allows for <code>double[][]</code> arrays to be used with the Matrix interface.
 *
 * @author Daniel Gratzer < danny.gratzer@gmail.com >
 */
public class DenseMatrix implements Matrix{
    double[][] entries;

    /**
     * Constructs an immutable dense matrix, with size and elements determined by <code>entries</code>
     *
     * @param entries The double 2 dimensional array which is wrapped into the Matrix interface.
     */
    public DenseMatrix(double[][] entries){
        this.entries = entries;
    }

    public double get(int row, int column){
        return entries[row][column];
    }
}
