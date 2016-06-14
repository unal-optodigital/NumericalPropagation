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

/**
 *
 * @author Raul Castañeda (racastanedaq@unal.edu.co)
 * @author Pablo Piedrahita-Quintero (jppiedrahitaq@unal.edu.co)
 * @author Jorge Garcia-Sucerquia (jigarcia@unal.edu.co)
 */
public interface PreferencesKeys {

    //utilities frame
    final static String UTILITIES_FRAME_LOC_X = "utilLocX";
    final static String UTILITIES_FRAME_LOC_Y = "utilLocY";
    
    //main frame parameters
    final static String MAIN_FRAME_LOC_X = "mainLocX";
    final static String MAIN_FRAME_LOC_Y = "mainLocY";

    final static String METHOD_IDX = "methodIdx";
    final static String LAMBDA = "lambda";
    final static String DISTANCE = "distance";
    final static String INPUT_WIDTH = "inputWidth";
    final static String INPUT_HEIGHT = "inputHeight";
    final static String OUTPUT_WIDTH = "outputWidth";
    final static String OUTPUT_HEIGHT = "outputHeight";
    final static String STEP = "step";
    final static String ROI_CHECKED = "roi";
    final static String PHASE_CHECKED = "phaseChecked";
    final static String AMPLITUDE_CHECKED = "amplitudeChecked";
    final static String INTENSITY_CHECKED = "intensityChecked";
    final static String REAL_CHECKED = "realChecked";
    final static String IMAGINARY_CHECKED = "imaginaryChecked";
    final static String RELATION_LOCK = "relationLock";
    final static String LOG_WRAPPING = "logWrapping";

    //units
    final static String LAMBDA_UNITS = "lambdaUnits";
    final static String DISTANCE_UNITS = "distanceUnits";
    final static String INPUT_SIZE_UNITS = "inputSizeUnits";
//    final static String INPUT_WIDTH_UNITS = "inputWidthUnits";
//    final static String INPUT_HEIGHT_UNITS = "inputHeightUnits";
    final static String OUTPUT_SIZE_UNITS = "outputSizeUnits";
//    final static String OUTPUT_WIDTH_UNITS = "outputWidthUnits";
//    final static String OUTPUT_HEIGHT_UNITS = "outputHeightUnits";
    final static String CURVATURE_RADIUS_UNITS = "curvRadiusUnits";

    //illumination
    final static String IS_PLANE = "isPlane";
    final static String CURV_RADIUS = "curvatureRadius";

    //log scaling
    final static String FFT_8_BIT = "8BitFFT";
    final static String PHASE_8_BIT = "8BitPhase";
    final static String AMPLITUDE_8_BIT = "8BitAmplitude";
    final static String INTENSITY_8_BIT = "8BitIntensity";
    final static String REAL_8_BIT = "8BitReal";
    final static String IMAGINARY_8_BIT = "8BitImaginary";

    //8bit scaling
    final static String FFT_LOG = "LogFFT";
    final static String AMPLITUDE_LOG = "LogAmplitude";
    final static String INTENSITY_LOG = "LogIntensity";

    //filter frame
    final static String IS_FILTER_ENABLED = "filterSelected";
    final static String ROI_X = "roiX";
    final static String ROI_Y = "roiY";
    final static String ROI_WIDTH = "roiWidth";
    final static String ROI_HEIGHT = "roiHeight";

    final static String IS_MANUAL = "isManual";

    //batch frame
    final static String BATCH_START = "start";
    final static String BATCH_END = "end";
    final static String BATCH_STEP = "batchStep";
    final static String BATCH_PLANES = "numberOfPlanes";

    final static String IS_STEP = "isStep";
    final static String MAX_PLANES = "maxPlanes";

    //dialogs
    final static String SHOW_FREQUENCIES_DIALOG = "showFreqDialog";
}
