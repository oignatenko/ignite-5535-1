/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.ml.math;

import java.util.Arrays;
import java.util.function.BiPredicate;
import org.apache.ignite.ml.math.impls.matrix.DenseLocalOnHeapMatrix;
import org.apache.ignite.ml.math.impls.matrix.SparseLocalOnHeapMatrix;
import org.apache.ignite.ml.math.impls.vector.DenseLocalOffHeapVector;
import org.apache.ignite.ml.math.impls.vector.DenseLocalOnHeapVector;
import org.apache.ignite.ml.math.impls.vector.SparseLocalVector;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/** Tests for BLAS operations (all operations are only available for local matrices and vectors). */
public class BlasOffHeapTest {
    /** Test off-heap blas availability. Todo remove later or make it OS-independent. */
    @Test
    public void testBlasOffHeap() {
        Assert.assertNotNull("Unexpected null BlasOffHeap instance.", BlasOffHeap.getInstance());
    }

    /** Test off-heap 'daxpy' operation for two vectors. */
    @Test
    public void testDaxpyArrayArray() {
        DenseLocalOffHeapVector y = new DenseLocalOffHeapVector(new double[] {1.0, 2.0});
        double a = 2.0;
        DenseLocalOffHeapVector x = new DenseLocalOffHeapVector(new double[] {1.0, 2.0});

        Vector exp = x.times(a).plus(y);
        BlasOffHeap.getInstance().daxpy(x.size(), a, x.ptr(), 1, y.ptr(), 1);

        Assert.assertEquals(y, exp);
    }

    /** Test 'dscal' operation for a vector. */
    @Test
    public void testScal() {
        double[] data = new double[] {1.0, 1.0};
        double alpha = 2.0;

        DenseLocalOffHeapVector v = new DenseLocalOffHeapVector(data);
        Vector exp = new DenseLocalOnHeapVector(data, true).times(alpha);
        BlasOffHeap.getInstance().dscal(v.size(), alpha, v.ptr(), 1);

        Assert.assertEquals(new DenseLocalOnHeapVector(v.size()).assign(v), exp);
    }

    /** Test 'axpy' operation for two array-based vectors. */
    @Test
    @Ignore("todo recover unit tests later")
    public void testAxpyArrayArray() {
        Vector y = new DenseLocalOnHeapVector(new double[] {1.0, 2.0});
        double a = 2.0;
        Vector x = new DenseLocalOnHeapVector(new double[] {1.0, 2.0});

        Vector exp = x.times(a).plus(y);
        Blas.axpy(a, x, y);

        Assert.assertEquals(y, exp);
    }

    /** Test 'axpy' operation for sparse vector and array-based vector. */
    @Test
    @Ignore("todo recover unit tests later")
    public void testAxpySparseArray() {
        DenseLocalOnHeapVector y = new DenseLocalOnHeapVector(new double[] {1.0, 2.0});
        double a = 2.0;
        SparseLocalVector x = sparseFromArray(new double[] {1.0, 2.0});

        SparseLocalVector exp = (SparseLocalVector)x.times(a).plus(y);
        Blas.axpy(a, x, y);

        Assert.assertTrue(elementsEqual(exp, y));
    }

    /** Test 'dot' operation. */
    @Test
    @Ignore("todo recover unit tests later")
    public void testDot() {
        DenseLocalOnHeapVector v1 = new DenseLocalOnHeapVector(new double[] {1.0, 1.0});
        DenseLocalOnHeapVector v2 = new DenseLocalOnHeapVector(new double[] {2.0, 2.0});

        Assert.assertEquals(Blas.dot(v1, v2), v1.dot(v2), 0.0);
    }

    /** Test 'scal' operation for dense matrix. */
    @Test
    @Ignore("todo recover unit tests later")
    public void testScalDense() {
        double[] data = new double[] {1.0, 1.0};
        double alpha = 2.0;

        DenseLocalOnHeapVector v = new DenseLocalOnHeapVector(data);
        Vector exp = new DenseLocalOnHeapVector(data, true).times(alpha);
        Blas.scal(alpha, v);

        Assert.assertEquals(v, exp);
    }

    /** Test 'scal' operation for sparse matrix. */
    @Test
    @Ignore("todo recover unit tests later")
    public void testScalSparse() {
        double[] data = new double[] {1.0, 1.0};
        double alpha = 2.0;

        SparseLocalVector v = sparseFromArray(data);
        Vector exp = sparseFromArray(data).times(alpha);

        Blas.scal(alpha, v);

        Assert.assertEquals(v, exp);
    }

    /** Test 'spr' operation for dense vector v and dense matrix A. */
    @Test
    @Ignore("todo recover unit tests later")
    public void testSprDenseDense() {
        double alpha = 3.0;

        DenseLocalOnHeapVector v = new DenseLocalOnHeapVector(new double[] {1.0, 2.0});
        DenseLocalOnHeapVector u = new DenseLocalOnHeapVector(new double[] {3.0, 13.0, 20.0, 0.0});

        // m is alpha * v * v^t
        DenseLocalOnHeapMatrix m = (DenseLocalOnHeapMatrix)new DenseLocalOnHeapMatrix(new double[][] {{1.0, 0.0},
            {2.0, 4.0}}, StorageConstants.COLUMN_STORAGE_MODE).times(alpha);
        DenseLocalOnHeapMatrix a = new DenseLocalOnHeapMatrix(new double[][] {{3.0, 0.0}, {13.0, 20.0}},
            StorageConstants.COLUMN_STORAGE_MODE);

        //m := alpha * v * v.t + A
        Blas.spr(alpha, v, u);

        DenseLocalOnHeapMatrix mu = fromVector(u, a.rowSize(), StorageConstants.COLUMN_STORAGE_MODE, (i, j) -> i >= j);
        Assert.assertEquals(m.plus(a), mu);
    }

    /** Test 'spr' operation for sparse vector v (sparse in representation, dense in fact) and dense matrix A. */
    @Test
    @Ignore("todo recover unit tests later")
    public void testSprSparseDense1() {
        double alpha = 3.0;

        SparseLocalVector v = sparseFromArray(new double[] {1.0, 2.0});
        DenseLocalOnHeapVector u = new DenseLocalOnHeapVector(new double[] {3.0, 13.0, 20.0, 0.0});

        DenseLocalOnHeapMatrix a = new DenseLocalOnHeapMatrix(new double[][] {{3.0, 0.0}, {13.0, 20.0}},
            StorageConstants.COLUMN_STORAGE_MODE);
        DenseLocalOnHeapMatrix exp = (DenseLocalOnHeapMatrix)new DenseLocalOnHeapMatrix(new double[][] {{1.0, 0.0},
            {2.0, 4.0}}, StorageConstants.COLUMN_STORAGE_MODE).times(alpha).plus(a);

        //m := alpha * v * v.t + A
        Blas.spr(alpha, v, u);
        DenseLocalOnHeapMatrix mu = fromVector(u, a.rowSize(), StorageConstants.COLUMN_STORAGE_MODE, (i, j) -> i >= j);
        Assert.assertEquals(exp, mu);
    }

    /** Test 'spr' operation for sparse vector v (sparse in representation, sparse in fact) and dense matrix A. */
    @Test
    @Ignore("todo recover unit tests later")
    public void testSprSparseDense2() {
        double alpha = 3.0;

        SparseLocalVector v = new SparseLocalVector(2, StorageConstants.RANDOM_ACCESS_MODE);
        v.set(0, 1);

        DenseLocalOnHeapVector u = new DenseLocalOnHeapVector(new double[] {3.0, 13.0, 20.0, 0.0});

        // m is alpha * v * v^t
        DenseLocalOnHeapMatrix m = (DenseLocalOnHeapMatrix)new DenseLocalOnHeapMatrix(new double[][] {{1.0, 0.0},
            {0.0, 0.0}}, StorageConstants.COLUMN_STORAGE_MODE).times(alpha);
        DenseLocalOnHeapMatrix a = new DenseLocalOnHeapMatrix(new double[][] {{3.0, 0.0}, {13.0, 20.0}},
            StorageConstants.COLUMN_STORAGE_MODE);

        //m := alpha * v * v.t + A
        Blas.spr(alpha, v, u);
        DenseLocalOnHeapMatrix mu = fromVector(u, a.rowSize(), StorageConstants.COLUMN_STORAGE_MODE, (i, j) -> i >= j);
        Assert.assertEquals(m.plus(a), mu);
    }

    /** Tests 'syr' operation for dense vector x and dense matrix A. */
    @Test
    @Ignore("todo recover unit tests later")
    public void testSyrDenseDense() {
        double alpha = 2.0;
        DenseLocalOnHeapVector x = new DenseLocalOnHeapVector(new double[] {1.0, 2.0});
        DenseLocalOnHeapMatrix a = new DenseLocalOnHeapMatrix(new double[][] {{10.0, 20.0}, {20.0, 10.0}});

        // alpha * x * x^T + A
        DenseLocalOnHeapMatrix exp = (DenseLocalOnHeapMatrix)new DenseLocalOnHeapMatrix(new double[][] {
            {1.0, 2.0},
            {2.0, 4.0}}).times(alpha).plus(a);

        Blas.syr(alpha, x, a);

        Assert.assertEquals(exp, a);
    }

    /** Tests 'gemm' operation for dense matrix A, dense matrix B and dense matrix C. */
    @Test
    @Ignore("todo recover unit tests later")
    public void testGemmDenseDenseDense() {
        // C := alpha * A * B + beta * C
        double alpha = 1.0;
        DenseLocalOnHeapMatrix a = new DenseLocalOnHeapMatrix(new double[][] {{10.0, 11.0}, {0.0, 1.0}});
        DenseLocalOnHeapMatrix b = new DenseLocalOnHeapMatrix(new double[][] {{1.0, 0.3}, {0.0, 1.0}});
        double beta = 0.0;
        DenseLocalOnHeapMatrix c = new DenseLocalOnHeapMatrix(new double[][] {{1.0, 2.0}, {2.0, 3.0}});

        DenseLocalOnHeapMatrix exp = (DenseLocalOnHeapMatrix)a.times(b);//.times(alpha).plus(c.times(beta));

        Blas.gemm(alpha, a, b, beta, c);

        Assert.assertEquals(exp, c);
    }

    /** Tests 'gemm' operation for sparse matrix A, dense matrix B and dense matrix C. */
    @Test
    @Ignore("todo recover unit tests later")
    public void testGemmSparseDenseDense() {
        // C := alpha * A * B + beta * C
        double alpha = 1.0;
        SparseLocalOnHeapMatrix a = (SparseLocalOnHeapMatrix)new SparseLocalOnHeapMatrix(2, 2)
            .assign(new double[][] {{10.0, 11.0}, {0.0, 1.0}});
        DenseLocalOnHeapMatrix b = new DenseLocalOnHeapMatrix(new double[][] {{1.0, 0.3}, {0.0, 1.0}});

        double beta = 0.0;
        DenseLocalOnHeapMatrix c = new DenseLocalOnHeapMatrix(new double[][] {{1.0, 2.0}, {2.0, 3.0}});

        Matrix exp = a.times(b);//.times(alpha).plus(c.times(beta));

        Blas.gemm(alpha, a, b, beta, c);

        Assert.assertTrue(Arrays.equals(exp.getStorage().data(), c.getStorage().data()));
    }

    /** Tests 'gemv' operation for dense matrix A, dense vector x and dense vector y. */
    @Test
    @Ignore("todo recover unit tests later")
    public void testGemvSparseDenseDense() {
        // y := alpha * A * x + beta * y
        double alpha = 3.0;
        SparseLocalOnHeapMatrix a = (SparseLocalOnHeapMatrix)new SparseLocalOnHeapMatrix(2, 2)
            .assign(new double[][] {{10.0, 11.0}, {0.0, 1.0}});
        DenseLocalOnHeapVector x = new DenseLocalOnHeapVector(new double[] {1.0, 2.0});

        double beta = 2.0;
        DenseLocalOnHeapVector y = new DenseLocalOnHeapVector(new double[] {3.0, 4.0});

        DenseLocalOnHeapVector exp = (DenseLocalOnHeapVector)y.times(beta).plus(a.times(x).times(alpha));

        Blas.gemv(alpha, a, x, beta, y);

        Assert.assertEquals(exp, y);
    }

    /** Tests 'gemv' operation for dense matrix A, sparse vector x and dense vector y. */
    @Test
    @Ignore("todo recover unit tests later")
    public void testGemvDenseSparseDense() {
        // y := alpha * A * x + beta * y
        double alpha = 3.0;
        SparseLocalOnHeapMatrix a = (SparseLocalOnHeapMatrix)new SparseLocalOnHeapMatrix(2, 2)
            .assign(new double[][] {{10.0, 11.0}, {0.0, 1.0}});
        SparseLocalVector x = sparseFromArray(new double[] {1.0, 2.0});

        double beta = 2.0;
        DenseLocalOnHeapVector y = new DenseLocalOnHeapVector(new double[] {3.0, 4.0});

        DenseLocalOnHeapVector exp = (DenseLocalOnHeapVector)y.times(beta).plus(a.times(x).times(alpha));

        Blas.gemv(alpha, a, x, beta, y);

        Assert.assertEquals(exp, y);
    }

    /** Tests 'gemv' operation for sparse matrix A, sparse vector x and dense vector y. */
    @Test
    @Ignore("todo recover unit tests later")
    public void testGemvSparseSparseDense() {
        // y := alpha * A * x + beta * y
        double alpha = 3.0;
        DenseLocalOnHeapMatrix a = new DenseLocalOnHeapMatrix(new double[][] {{10.0, 11.0}, {0.0, 1.0}}, 2);
        SparseLocalVector x = sparseFromArray(new double[] {1.0, 2.0});
        double beta = 2.0;
        DenseLocalOnHeapVector y = new DenseLocalOnHeapVector(new double[] {3.0, 4.0});

        DenseLocalOnHeapVector exp = (DenseLocalOnHeapVector)y.times(beta).plus(a.times(x).times(alpha));

        Blas.gemv(alpha, a, x, beta, y);

        Assert.assertEquals(exp, y);
    }

    /** Tests 'gemv' operation for dense matrix A, dense vector x and dense vector y. */
    @Test
    @Ignore("todo recover unit tests later")
    public void testGemvDenseDenseDense() {
        // y := alpha * A * x + beta * y
        double alpha = 3.0;
        DenseLocalOnHeapMatrix a = new DenseLocalOnHeapMatrix(new double[][] {{10.0, 11.0}, {0.0, 1.0}}, 2);
        DenseLocalOnHeapVector x = new DenseLocalOnHeapVector(new double[] {1.0, 2.0});
        double beta = 2.0;
        DenseLocalOnHeapVector y = new DenseLocalOnHeapVector(new double[] {3.0, 4.0});

        DenseLocalOnHeapVector exp = (DenseLocalOnHeapVector)y.times(beta).plus(a.times(x).times(alpha));

        Blas.gemv(alpha, a, x, beta, y);

        Assert.assertEquals(exp, y);
    }

    /**
     * Create a sparse vector from array.
     *
     * @param arr Array with vector elements.
     * @return sparse local on-heap vector.
     */
    private static SparseLocalVector sparseFromArray(double[] arr) {
        SparseLocalVector res = new SparseLocalVector(2, StorageConstants.RANDOM_ACCESS_MODE);

        for (int i = 0; i < arr.length; i++)
            res.setX(i, arr[i]);

        return res;
    }

    /**
     * Checks if two vectors have equal elements.
     *
     * @param a Matrix a.
     * @param b Vector b
     * @return true if vectors are equal element-wise, false otherwise.
     */
    private static boolean elementsEqual(Vector a, Vector b) {
        int n = b.size();

        for (int i = 0; i < n; i++)
            if (a.get(i) != b.get(i))
                return false;

        return true;
    }

    /**
     * Creates dense local on-heap matrix from vector v entities filtered by bipredicate p.
     *
     * @param v Vector with entities for matrix to be created.
     * @param rows Rows number in the target matrix.
     * @param acsMode column or row major mode.
     * @param p bipredicate to filter entities by.
     * @return dense local on-heap matrix.
     */
    private static DenseLocalOnHeapMatrix fromVector(DenseLocalOnHeapVector v, int rows, int acsMode,
        BiPredicate<Integer, Integer> p) {
        double[] data = v.getStorage().data();
        int cols = data.length / rows;
        double[] d = new double[data.length];

        int iLim = acsMode == StorageConstants.ROW_STORAGE_MODE ? rows : cols;
        int jLim = acsMode == StorageConstants.ROW_STORAGE_MODE ? cols : rows;

        int shift = 0;

        for (int i = 0; i < iLim; i++)
            for (int j = 0; j < jLim; j++) {
                int ind = i * jLim + j;

                if (!p.test(i, j)) {
                    shift++;
                    d[ind] = 0.0;
                    continue;
                }
                d[ind] = data[ind - shift];
            }

        return new DenseLocalOnHeapMatrix(d, rows, acsMode);
    }
}
