/*
 * Copyright 2015 Universidad Nacional de Colombia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package unal.od.np;

import org.jtransforms.fft.FloatFFT_2D;
import unal.od.jdiffraction.cpu.FloatAngularSpectrum;
import unal.od.jdiffraction.cpu.FloatFresnelBluestein;
import unal.od.jdiffraction.cpu.FloatFresnelFourier;
import unal.od.jdiffraction.cpu.FloatPropagator;
import unal.od.jdiffraction.cpu.utils.ArrayUtils;

/**
 *
 * @author Raul Castañeda (racastanedaq@unal.edu.co)
 * @author Pablo Piedrahita-Quintero (jppiedrahitaq@unal.edu.co)
 * @author Jorge Garcia-Sucerquia (jigarcia@unal.edu.co)
 */
public class Data {

    private static Data INSTANCE = null;

    private Data() {
    }

    private synchronized static void createInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Data();
        }
    }

    public static Data getInstance() {
        if (INSTANCE == null) {
            createInstance();
        }
        return INSTANCE;
    }

    private float outputW, outputH;
    private float lambda, z, dx, dy, dxOut, dyOut;
    private int M, N;

    private int x, y, w, h;
    private int[][] mask;

    private float[][] imageSpectrum;
    private float[][] field, fieldSpherical, outputField;
    private float[][] filteredField, filteredFieldSpherical;

    private float curvRadius;
    private float[][] sphericalWave;

//    private boolean filtered = false;
    private FloatFFT_2D fft;

    private FloatPropagator propagator;

    public void calculateFFT() {
        fft = new FloatFFT_2D(M, N);

        fft.complexForward(field);
        ArrayUtils.complexShift(field);

        imageSpectrum = ArrayUtils.modulus(field);
    }

    public void center() {
        if (mask == null) {
            center(x, y, w, h);
            return;
        }

        filteredField = new float[M][2 * N];

        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                filteredField[i][2 * j] = 0;
                filteredField[i][2 * j + 1] = 0;
            }
        }

        int a = (M - w - 2 * x) / 2;
        int b = (N - h - 2 * y) / 2;
        int i2 = 0;

        for (int i = x; i < x + w; i++) {
            int j2 = 0;
            for (int j = y; j < y + h; j++) {
                if (mask[i2][j2] != 0) {
                    filteredField[i + a][2 * (j + b)] = field[i][2 * j];
                    filteredField[i + a][2 * (j + b) + 1] = field[i][2 * j + 1];
                }
                j2++;
            }
            i2++;
        }

        ArrayUtils.complexShift(filteredField);
        fft.complexInverse(filteredField, true);

//        for (int i = 0; i < M; i++) {
//            System.arraycopy(filteredField[i], 0, field[i], 0, filteredField[i].length);
//        }
    }

    private void center(int x, int y, int width, int height) {
        filteredField = new float[M][2 * N];

        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                filteredField[i][2 * j] = 0;
                filteredField[i][2 * j + 1] = 0;
            }
        }

        int a = (M - width - 2 * x) / 2;
        int b = (N - height - 2 * y) / 2;
        for (int i = x; i < x + width - 1; i++) {
            for (int j = y; j < y + height - 1; j++) {
                filteredField[i + a][2 * (j + b)] = field[i][2 * j];
                filteredField[i + a][2 * (j + b) + 1] = field[i][2 * j + 1];
            }
        }

        ArrayUtils.complexShift(filteredField);
        fft.complexInverse(filteredField, true);

//        for (int i = 0; i < M; i++) {
//            System.arraycopy(filteredImage[i], 0, field[i], 0, filteredImage[i].length);
//        }
    }

    public void propagate(int idx, boolean filtered, boolean isPlane, float curvRadius) {
        switch (idx) {
            case 0:
                propagator = new FloatAngularSpectrum(M, N, lambda, z, dx, dy);
                break;
            case 1:
                propagator = new FloatFresnelFourier(M, N, lambda, z, dx, dy);
                break;
            case 2:
                propagator = new FloatFresnelBluestein(M, N, lambda, z, dx, dy, dxOut, dyOut);
                break;
            case 3:
                float zCrit = M * dx * dx / lambda;
                if (Math.abs(z) < zCrit) {
                    propagator = new FloatAngularSpectrum(M, N, lambda, z, dx, dy);
                } else {
                    propagator = new FloatFresnelFourier(M, N, lambda, z, dx, dy);
                }
                break;
        }

        outputField = new float[M][2 * N];

        if (filtered) {
            if (isPlane) {
                for (int i = 0; i < M; i++) {
                    System.arraycopy(filteredField[i], 0, outputField[i], 0, 2 * N);
                }
            } else {
                calculateSphericalWave(curvRadius);
                outputField = ArrayUtils.complexMultiplication(filteredField, sphericalWave);
            }
        } else {
            if (isPlane) {
                for (int i = 0; i < M; i++) {
                    System.arraycopy(field[i], 0, outputField[i], 0, 2 * N);
                }
            } else {
                calculateSphericalWave(curvRadius);
                outputField = ArrayUtils.complexMultiplication(field, sphericalWave);
            }
        }

        propagator.diffract(outputField);
    }

    private void calculateSphericalWave(float curvRadius) {
        if (this.curvRadius == curvRadius && sphericalWave.length == M
                && sphericalWave[0].length == 2 * N) {
            return;
        }

        sphericalWave = new float[M][2 * N];
        this.curvRadius = curvRadius;

        int M2 = M / 2;
        int N2 = N / 2;
        float f = 1 / (2 * curvRadius);

        for (int i = 0; i < M; i++) {
            int i2 = i - M2 + 1;
            float a = (dx * dx * i2 * i2);

            for (int j = 0; j < N; j++) {
                int j2 = j - N2 + 1;
                float b = (dy * dy * j2 * j2);
                float phase = f * (a + b);

                sphericalWave[i][2 * j] = (float) Math.cos(phase);
                sphericalWave[i][2 * j + 1] = (float) Math.sin(phase);
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Setters and getters">
    public void setROI(int x, int y, int width, int height, int[][] mask) {
        this.x = x;
        this.y = y;
        this.w = width;
        this.h = height;

        this.mask = mask;
    }

    public void setParameters(float lambda, float z, float inputW, float inputH, float outputW, float outputH) {
        this.lambda = lambda;
        this.z = z;
        dx = inputW / M;
        dy = inputH / N;
        this.outputW = outputW;
        this.outputH = outputH;

        int sign = (int) Math.signum(z);
        dxOut = sign * outputW / M;
        dyOut = sign * outputH / N;
    }

    public void setParameters(float lambda, float z, float inputW, float inputH) {
        this.lambda = lambda;
        this.z = z;
        dx = inputW / M;
        dy = inputH / N;
    }

    public void setDistance(float z, boolean fb) {
        this.z = z;

        if (fb) {
            int sign = (int) Math.signum(z);
            dxOut = sign * outputW / M;
            dyOut = sign * outputH / N;
        }
    }

    public void setInputImages(int M, int N, float[][] inputReal, float[][] inputImaginary) {
        this.M = M;
        this.N = N;
        field = ArrayUtils.complexAmplitude2(inputReal, inputImaginary);

        /*
         if (inputReal != null && inputImaginary != null) {

         M = inputReal.length;
         N = inputReal[0].length;

         field = new float[M][2 * N];

         for (int i = 0; i < M; i++) {
         for (int j = 0; j < N; j++) {
         field[i][2 * j] = inputReal[i][j];
         field[i][2 * j + 1] = inputImaginary[i][j];
         }
         }
         } else if (inputReal != null && !(inputImaginary != null)) {

         M = inputReal.length;
         N = inputReal[0].length;

         field = new float[M][2 * N];

         for (int i = 0; i < M; i++) {
         for (int j = 0; j < N; j++) {
         field[i][2 * j] = inputReal[i][j];
         field[i][2 * j + 1] = 0;
         }
         }
         } else if (!(inputReal != null) && inputImaginary != null) {

         M = inputImaginary.length;
         N = inputImaginary[0].length;

         field = new float[M][2 * N];

         for (int i = 0; i < M; i++) {
         for (int j = 0; j < N; j++) {
         field[i][2 * j] = 0;
         field[i][2 * j + 1] = inputImaginary[i][j];
         }
         }
         }
         */
    }

    public float getZ() {
        return z;
    }

    public int getM() {
        return M;
    }

    public int getN() {
        return N;
    }

    public float[][] getImageSpectrum() {
        return imageSpectrum;
    }

    public float[][] getOutputField() {
        return outputField;
    }
// </editor-fold>
}
