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
 * @author: Raul Castañeda (racastanedaq@unal.edu.co)
 * @author: Pablo Piedrahita-Quintero (jppiedrahitaq@unal.edu.co)
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

    private float[][] inputImage, imageSpectrum;
    private float[][] fftImage, filteredImage, filteredImageSpherical, outputField;

    private boolean filtered = false;

    private boolean phaseChecked, amplitudeSelected, intensitySelected;

    private FloatFFT_2D fft;

    private FloatPropagator propagator;

    public void calculateFFT() {
        fft = new FloatFFT_2D(M, N);
        fftImage = new float[M][2 * N];

        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                fftImage[i][2 * j] = inputImage[i][j];
                fftImage[i][2 * j + 1] = 0;
            }
        }

        fft.complexForward(fftImage);
        ArrayUtils.complexShift(fftImage);

        imageSpectrum = ArrayUtils.modulusSq(fftImage);
    }

    public void center() {
        if (mask == null) {
            center(x, y, w, h);
            return;
        }

        filteredImage = new float[M][2 * N];
        filteredImageSpherical = new float[M][2 * N];

        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                filteredImage[i][2 * j] = 0;
                filteredImage[i][2 * j + 1] = 0;
            }
        }

        int a = (M - w - 2 * x) / 2;
        int b = (N - h - 2 * y) / 2;
        int i2 = 0;

        for (int i = x; i < x + w; i++) {
            int j2 = 0;
            for (int j = y; j < y + h; j++) {
                if (mask[i2][j2] != 0) {
                    filteredImage[i + a][2 * (j + b)] = fftImage[i][2 * j];
                    filteredImage[i + a][2 * (j + b) + 1] = fftImage[i][2 * j + 1];
                }
                j2++;
            }
            i2++;
        }

        ArrayUtils.complexShift(filteredImage);
        fft.complexInverse(filteredImage, true);
    }

    private void center(int x, int y, int width, int height) {
        filteredImage = new float[M][2 * N];
        filteredImageSpherical = new float[M][2 * N];

        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                filteredImage[i][2 * j] = 0;
                filteredImage[i][2 * j + 1] = 0;
            }
        }

        int a = (M - width - 2 * x) / 2;
        int b = (N - height - 2 * y) / 2;
        for (int i = x; i < x + width - 1; i++) {
            for (int j = y; j < y + height - 1; j++) {
                filteredImage[i + a][2 * (j + b)] = fftImage[i][2 * j];
                filteredImage[i + a][2 * (j + b) + 1] = fftImage[i][2 * j + 1];
            }
        }

        ArrayUtils.complexShift(filteredImage);
        fft.complexInverse(filteredImage, true);

//        ImageProcessor ip = new FloatProcessor(ArrayUtils.modulusSq(filteredImage));
//        ImagePlus imp = new ImagePlus("holo_f", ip);
//        imp.show();
    }

    public void propagate(int idx) {
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
//                    System.out.println("AS: " + z);
                    propagator = new FloatAngularSpectrum(M, N, lambda, z, dx, dy);
                } else {
//                    System.out.println("FF: " + z);
                    propagator = new FloatFresnelFourier(M, N, lambda, z, dx, dy);
                }
                break;
        }

        outputField = new float[M][];

        for (int i = 0; i < M; i++) {
            outputField[i] = filteredImage[i].clone();
        }

        propagator.diffract(outputField);
    }

    public void propagate(int idx, boolean calculate, float c) {
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
                if (z < zCrit) {
                    propagator = new FloatAngularSpectrum(M, N, lambda, z, dx, dy);
                } else {
                    propagator = new FloatFresnelFourier(M, N, lambda, z, dx, dy);
                }
                break;
        }

        if (calculate) {
            outputField = new float[M][2 * N];
            int M2 = M / 2;
            int N2 = N / 2;
            float f = 1 / (2 * c);

            for (int i = 0; i < M; i++) {
                int i2 = i - M2 + 1;
                float a = (dx * dx * i2 * i2);

                for (int j = 0; j < N; j++) {
                    int j2 = j - N2 + 1;
                    float b = (dy * dy * j2 * j2);
                    float phase = f * (a + b);

                    outputField[i][2 * j]
                            = filteredImageSpherical[i][2 * j]
                            = filteredImage[i][2 * j] * (float) Math.cos(phase);

                    outputField[i][2 * j + 1]
                            = filteredImageSpherical[i][2 * j + 1]
                            = filteredImage[i][2 * j + 1] * (float) Math.sin(phase);

                }
            }
        } else {
            for (int i = 0; i < M; i++) {
                outputField[i] = filteredImageSpherical[i].clone();
            }
        }

        propagator.diffract(outputField);
    }

    // <editor-fold defaultstate="collapsed" desc="Setters and getters">
    public void setDimensions(int M, int N) {
        this.M = M;
        this.N = N;
    }

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
        
//        dxOut = lambda * z / (M * dx);//outputW / M;
//        dyOut = lambda * z / (N * dy);//outputH / N;
//        dxOut = (z < 0) ? -outputW / M : outputW / M;
//        dyOut = (z < 0) ? -outputH / N : outputH / N;

//        System.out.println("---");
//        System.out.println("" + M);
//        System.out.println("" + N);
//        System.out.println("---");
//        System.out.println("" + outputW / M);
//        System.out.println("" + outputH / N);
//        System.out.println("---");
//        System.out.println("" + dxOut);
//        System.out.println("" + dyOut);
    }

    public void setParameters(float lambda, float z, float inputW, float inputH) {
        this.lambda = lambda;
        this.z = z;
        dx = inputW / M;
        dy = inputH / N;
    }

    public void setDistance(float z) {
        this.z = z;
        
        int sign = (int) Math.signum(z);
        dxOut = sign * outputW / M;
        dyOut = sign * outputH / N;
    }

    public void setInputImage(float[][] inputImage) {
        this.inputImage = inputImage;
    }

    public void setOutputs(boolean phase, boolean amplitude, boolean intensity) {
        this.phaseChecked = phase;
        this.amplitudeSelected = amplitude;
        this.intensitySelected = intensity;
    }

    public void setFiltered(boolean filtered) {
        this.filtered = filtered;
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

    public boolean isFiltered() {
        return filtered;
    }

    public boolean isPhaseSelected() {
        return phaseChecked;
    }

    public boolean isAmplitudeSelected() {
        return amplitudeSelected;
    }

    public boolean isIntensitySelected() {
        return intensitySelected;
    }
// </editor-fold>
}
