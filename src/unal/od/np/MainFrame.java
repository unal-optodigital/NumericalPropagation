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

import ij.ImageListener;
import ij.ImagePlus;
import ij.WindowManager;
import ij.measure.Calibration;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.prefs.Preferences;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import unal.od.jdiffraction.cpu.utils.ArrayUtils;

/**
 *
 * @author Raul Castañeda (racastanedaq@unal.edu.co)
 * @author Pablo Piedrahita-Quintero (jppiedrahitaq@unal.edu.co)
 * @author Jorge Garcia-Sucerquia (jigarcia@unal.edu.co)
 */
public class MainFrame extends javax.swing.JFrame implements ImageListener, PreferencesKeys {

    private static final String TITLE = "Numerical Propagation";
    private static final String LOG_HEADER = "Version 1.2 - August 2015";
    private static final String LOG_SEPARATOR = "\n---------------------------";

    public static final String[] PROPAGATION_METHOD = new String[]{"Angular Spectrum", "Fresnel", "Fresnel - Bluestein", "Automatic"};

    //user inputs in user units
    private float lambdaUser;
    private float zUser;
    private float inputWUser;
    private float inputHUser;
    private float outputWUser;
    private float outputHUser;

    private float stepUser;
    private float zStepUser;

    //user inputs converted to um
    private float lambdaUm;
    private float zUm;
    private float inputWUm;
    private float inputHUm;
    private float outputWUm;
    private float outputHUm;

    private float stepUm;
    private float zStepUm;

    //when filtering and same roi are enabled, this variables help to identify if
    //the inputs and the dimensions changed
    private int oldIDReal = Integer.MAX_VALUE, newIDReal, oldIDImaginary = Integer.MAX_VALUE, newIDImaginary;
    private int oldMReal = 0, newMReal, oldMImaginary = 0, newMImaginary;
    private int oldNReal = 0, newNReal, oldNImaginary = 0, newNImaginary;

    //input field dimensions, useful for output calibration
    private int M, N;

    //arrays with the current open images information
    private int[] windowsId;
    private String[] titles;

    //input images titles
    private String realTitle;
    private String imaginaryTitle;

    //calibration object for the output images
    private Calibration cal;

    //formatter
    private final DecimalFormat df;

    //preferences
    private final Preferences pref;

    //data object, performs the calculations
    private final Data data;

    //frames
    private SettingsFrame settingsFrame = null;
    private FilterFrame filterFrame = null;
    private BatchFrame batchFrame = null;

    // <editor-fold defaultstate="collapsed" desc="Prefs variables">
    //frame location
    private int locX;
    private int locY;

    //method combo index
    private int methodIdx;

    //last parameters used
    private String lambdaString;
    private String zString;
    private String inputWString;
    private String inputHString;
    private String outputWString;
    private String outputHString;
    private String stepString;

    //parameters units
    private String lambdaUnits;
    private String zUnits;
    private String inputSizeUnits;
//    private String inputWUnits;
//    private String inputHUnits;
    private String outputSizeUnits;
//    private String outputWUnits;
//    private String outputHUnits;

    //same roi option checked or not
    private boolean roiEnabled;

    //last outputs used
    private boolean phaseEnabled;
    private boolean amplitudeEnabled;
    private boolean intensityEnabled;
    private boolean realEnabled;
    private boolean imaginaryEnabled;

    //filtering
    private boolean filterEnabled;

    //plane illumination
    private boolean isPlane;

    //curvature radius for spherical illumination
    private float curvRadius;

    //log scaling options
    private boolean amplitudeLogSelected;
    private boolean intensityLogSelected;

    //8bit scaling options
    private boolean phaseByteSelected;
    private boolean amplitudeByteSelected;
    private boolean intensityByteSelected;
//    private boolean realByteSelected;
//    private boolean imaginaryByteSelected;

    private boolean relationLock;

    private boolean logWrapping;
    // </editor-fold>

    /**
     * Creates the main frame
     */
    public MainFrame() {
        //initialized objects
        df = new DecimalFormat("#.####", new DecimalFormatSymbols(Locale.US));
        pref = Preferences.userNodeForPackage(getClass());
        data = Data.getInstance();

        //gets the current open images and load the last preferences
        getOpenedImages();
        loadPrefs();

        initComponents();

        //adds this class as ImageListener
        ImagePlus.addImageListener(this);

//        DefaultCaret caret = (DefaultCaret) log.getCaret(); //autoscroll
//        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    }

    /**
     * Fills the arrays with the information of the open images.
     */
    private void getOpenedImages() {
        //gets the IDs of the opened images
        windowsId = WindowManager.getIDList();

        if (windowsId == null) {
            //if there are no images open, just adds <none> option
            titles = new String[]{"<none>"};
        } else {
            //titles for both inputs, with <none> option
            titles = new String[windowsId.length + 1];
            titles[0] = "<none>";
            for (int i = 0; i < windowsId.length; i++) {
                ImagePlus imp = WindowManager.getImage(windowsId[i]);
                if (imp != null) {
                    titles[i + 1] = imp.getTitle();
                } else {
                    titles[i + 1] = "";
                }
            }
        }
    }

    /**
     * Saves the preferences when the frame is closed.
     */
    private void savePrefs() {
        //frame location
        pref.putInt(MAIN_FRAME_LOC_X, getLocation().x);
        pref.putInt(MAIN_FRAME_LOC_Y, getLocation().y);

        //method combo idx
        pref.putInt(METHOD_IDX, methodCombo.getSelectedIndex());

        //parameters text
        pref.put(LAMBDA, lambdaField.getText());
        pref.put(DISTANCE, zField.getText());
        pref.put(INPUT_WIDTH, inputWField.getText());
        pref.put(INPUT_HEIGHT, inputHField.getText());
        pref.put(OUTPUT_WIDTH, outputWField.getText());
        pref.put(OUTPUT_HEIGHT, outputHField.getText());

        pref.put(STEP, stepField.getText());

        //same roi option
        pref.putBoolean(ROI_CHECKED, roiChk.isSelected());

        //outputs
        pref.putBoolean(PHASE_CHECKED, phaseChk.isSelected());
        pref.putBoolean(AMPLITUDE_CHECKED, amplitudeChk.isSelected());
        pref.putBoolean(INTENSITY_CHECKED, intensityChk.isSelected());
        pref.putBoolean(REAL_CHECKED, realChk.isSelected());
        pref.putBoolean(IMAGINARY_CHECKED, imaginaryChk.isSelected());

        pref.putBoolean(RELATION_LOCK, lockBtn.isSelected());

        pref.putBoolean(LOG_WRAPPING, log.getLineWrap());
    }

    /**
     * Loads the preferences when the plugin starts.
     */
    private void loadPrefs() {
        //frame location
        locX = pref.getInt(MAIN_FRAME_LOC_X, 300);
        locY = pref.getInt(MAIN_FRAME_LOC_Y, 300);

        //m,ethod combo idx
        methodIdx = pref.getInt(METHOD_IDX, 0);

        //parameters text
        lambdaString = pref.get(LAMBDA, "");
        zString = pref.get(DISTANCE, "");
        inputWString = pref.get(INPUT_WIDTH, "");
        inputHString = pref.get(INPUT_HEIGHT, "");
        outputWString = pref.get(OUTPUT_WIDTH, "");
        outputHString = pref.get(OUTPUT_HEIGHT, "");

        stepString = pref.get(STEP, "");

        //same roi option
        roiEnabled = pref.getBoolean(ROI_CHECKED, false);

        //outputs
        phaseEnabled = pref.getBoolean(PHASE_CHECKED, false);
        amplitudeEnabled = pref.getBoolean(AMPLITUDE_CHECKED, false);
        intensityEnabled = pref.getBoolean(INTENSITY_CHECKED, false);
        realEnabled = pref.getBoolean(REAL_CHECKED, false);
        imaginaryEnabled = pref.getBoolean(IMAGINARY_CHECKED, false);

        relationLock = pref.getBoolean(RELATION_LOCK, false);

        logWrapping = pref.getBoolean(LOG_WRAPPING, true);

        //parameters units
        loadUnitsPrefs();

        //propagation
        loadPropagationPrefs();

        //scaling
        loadScalingPrefs();
    }

    /**
     * Loads the units of the parameters.
     */
    private void loadUnitsPrefs() {
        lambdaUnits = pref.get(LAMBDA_UNITS, "nm");
        zUnits = pref.get(DISTANCE_UNITS, "m");
        inputSizeUnits = pref.get(INPUT_SIZE_UNITS, "mm");
//        inputWUnits = pref.get(INPUT_WIDTH_UNITS, "mm");
//        inputHUnits = pref.get(INPUT_HEIGHT_UNITS, "mm");
        outputSizeUnits = pref.get(OUTPUT_SIZE_UNITS, "mm");
//        outputWUnits = pref.get(OUTPUT_WIDTH_UNITS, "mm");
//        outputHUnits = pref.get(OUTPUT_HEIGHT_UNITS, "mm");
    }

    /**
     * Loads filtering and illumination options.
     */
    private void loadPropagationPrefs() {
        filterEnabled = pref.getBoolean(IS_FILTER_ENABLED, true);
        isPlane = pref.getBoolean(IS_PLANE, true);

        curvRadius = pref.getFloat(CURV_RADIUS, 1E6f);
    }

    /**
     * Loads scaling options.
     */
    private void loadScalingPrefs() {
        amplitudeLogSelected = pref.getBoolean(AMPLITUDE_LOG, true);
        intensityLogSelected = pref.getBoolean(INTENSITY_LOG, true);

        phaseByteSelected = pref.getBoolean(PHASE_8_BIT, true);
        amplitudeByteSelected = pref.getBoolean(AMPLITUDE_8_BIT, true);
        intensityByteSelected = pref.getBoolean(INTENSITY_8_BIT, true);
//        realByteSelected = pref.getBoolean(REAL_8_BIT, true);
//        imaginaryByteSelected = pref.getBoolean(IMAGINARY_8_BIT, true);
    }

    /**
     * Updates units labels.
     */
    public void updateUnitsPrefs() {
        loadUnitsPrefs();

        lambdaLabel.setText("Wavelength [" + lambdaUnits + "]:");
        zLabel.setText("Distance [" + zUnits + "]:");
//        inputWLabel.setText("Input width [" + inputWUnits + "]:");
//        inputHLabel.setText("Input height [" + inputHUnits + "]:");
        inputWLabel.setText("Input width [" + inputSizeUnits + "]:");
        inputHLabel.setText("Input height [" + inputSizeUnits + "]:");
//        outputWLabel.setText("Output width [" + outputWUnits + "]:");
//        outputHLabel.setText("Output height [" + outputHUnits + "]:");
        outputWLabel.setText("Output width [" + outputSizeUnits + "]:");
        outputHLabel.setText("Output height [" + outputSizeUnits + "]:");
    }

    /**
     * Updates filtering and illumination options.
     */
    public void updatePropagationPrefs() {
        loadPropagationPrefs();

        if (roiChk.isEnabled()) {
            roiChk.setEnabled(filterEnabled);
        }
    }

    /**
     * Updates scaling options.
     */
    public void updateScalingPrefs() {
        loadScalingPrefs();
    }

    /**
     * Posts a message (s) on the log. If useSeparator is true prints a
     * separator before the message.
     *
     * @param useSeparator
     * @param s
     */
    public void updateLog(boolean useSeparator, String s) {
        if (useSeparator) {
            log.append(LOG_SEPARATOR);
        }
        log.append(s);
    }

    /**
     * Enables the fields after a propagation is performed. - Same ROI Checkbox
     * - Increase and decrease buttons - Step TextField - Batch button
     *
     * @param enabled
     */
    public void enableAfterPropagationOpt(boolean enabled) {
        if (filterEnabled) {
            roiChk.setEnabled(enabled);
        }
        decBtn.setEnabled(enabled);
        stepField.setEnabled(enabled);
        incBtn.setEnabled(enabled);
        batchBtn.setEnabled(enabled);
    }

    /**
     * Sets the distance to be used when the increase or decrease buttons are
     * activated, intended to be used after a propagation is performed.
     */
    public void setStepDistance() {
        zStepUser = zUser;
        zStepUm = zUm;
    }

    /**
     * Returns an array containing the parameters used in the last propagation.
     *
     * @param useZ
     * @return
     */
    public String[] getFormattedParameters(boolean useZ) {
        String[] s = new String[]{
            realTitle,
            imaginaryTitle,
            df.format(lambdaUser) + " " + lambdaUnits,
            df.format(useZ ? zUser : zStepUser) + " " + zUnits,
            //            df.format(inputWUser) + " " + inputWUnits,
            //            df.format(inputHUser) + " " + inputHUnits,
            df.format(inputWUser) + " " + inputSizeUnits,
            df.format(inputHUser) + " " + inputSizeUnits,
            //            df.format(outputWUser) + " " + outputWUnits,
            //            df.format(outputHUser) + " " + outputHUnits
            df.format(outputWUser) + " " + outputSizeUnits,
            df.format(outputHUser) + " " + outputSizeUnits
        };

        return s;
    }

    /**
     * Converts to um the user inputs.
     */
    private void fixUnits() {
        lambdaUm = unitsToum(lambdaUser, lambdaUnits);
        zUm = unitsToum(zUser, zUnits);
//        inputWUm = unitsToum(inputWUser, inputWUnits);
//        inputHUm = unitsToum(inputHUser, inputHUnits);
        inputWUm = unitsToum(inputWUser, inputSizeUnits);
        inputHUm = unitsToum(inputHUser, inputSizeUnits);

        if (methodIdx == 2) {
//            outputWUm = unitsToum(outputWUser, outputWUnits);
//            outputHUm = unitsToum(outputHUser, outputHUnits);
            outputWUm = unitsToum(outputWUser, outputSizeUnits);
            outputHUm = unitsToum(outputHUser, outputSizeUnits);
        }
    }

    /**
     * Helper method to convert from {units} to um.
     *
     * @param val
     * @param units
     * @return
     */
    private float unitsToum(float val, String units) {
        if (units.equals("nm")) {
            return val * 1E-3f;
        } else if (units.equals("mm")) {
            return val * 1E3f;
        } else if (units.equals("cm")) {
            return val * 1E4f;
        } else if (units.equals("m")) {
            return val * 1E6f;
        }

        return val;
    }

    /**
     * Sets the image properties to know if the inputs change, useful when
     * filtering and same roi options are enabled.
     */
    public void setImageProps() {
        oldIDReal = newIDReal;
        oldMReal = newMReal;
        oldNReal = newNReal;

        oldIDImaginary = newIDImaginary;
        oldMImaginary = newMImaginary;
        oldNImaginary = newNImaginary;
    }

    /**
     * Sets the input images from the user selections. Returns false if an error
     * occurs.
     *
     * @param realIdx
     * @param imaginaryIdx
     * @return success
     */
    private boolean setInputImages(int realIdx, int imaginaryIdx) {
        realTitle = titles[realIdx];
        imaginaryTitle = titles[imaginaryIdx];

        boolean hasReal = !realTitle.equalsIgnoreCase("<none>");
        boolean hasImaginary = !imaginaryTitle.equalsIgnoreCase("<none>");

        if (!hasReal && !hasImaginary) {
            //if no inputs are chosen shows an error message
            JOptionPane.showMessageDialog(this, "Please select at least one input image.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (hasReal && hasImaginary) {
            ImagePlus realImp = WindowManager.getImage(windowsId[realIdx - 1]);
            ImageProcessor realIp = realImp.getProcessor();

            newIDReal = realImp.getID();
            newMReal = realIp.getWidth();
            newNReal = realIp.getHeight();

            ImagePlus imaginaryImp = WindowManager.getImage(windowsId[imaginaryIdx - 1]);
            ImageProcessor imaginaryIp = imaginaryImp.getProcessor();

            newIDImaginary = imaginaryImp.getID();
            newMImaginary = imaginaryIp.getWidth();
            newNImaginary = imaginaryIp.getHeight();

            //checks dimensions
            if (newMReal != newMImaginary || newNReal != newNImaginary) {
                JOptionPane.showMessageDialog(this, "Input images must have the same dimensions.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            M = newMReal;
            N = newNReal;
            data.setInputImages(M, N, realIp.getFloatArray(), imaginaryIp.getFloatArray());

        } else if (hasReal && !hasImaginary) {
            ImagePlus realImp = WindowManager.getImage(windowsId[realIdx - 1]);
            ImageProcessor realIp = realImp.getProcessor();

            newIDReal = realImp.getID();
            newMReal = realIp.getWidth();
            newNReal = realIp.getHeight();

            newIDImaginary = Integer.MAX_VALUE;
            newMImaginary = -1;
            newNImaginary = -1;

            M = newMReal;
            N = newNReal;
            data.setInputImages(M, N, realIp.getFloatArray(), null);

        } else if (!hasReal && hasImaginary) {
            ImagePlus imaginaryImp = WindowManager.getImage(windowsId[imaginaryIdx - 1]);
            ImageProcessor imaginaryIp = imaginaryImp.getProcessor();

            newIDReal = Integer.MAX_VALUE;
            newMReal = -1;
            newNReal = -1;

            newIDImaginary = imaginaryImp.getID();
            newMImaginary = imaginaryIp.getWidth();
            newNImaginary = imaginaryIp.getHeight();

            M = newMImaginary;
            N = newNImaginary;
            data.setInputImages(M, N, null, imaginaryIp.getFloatArray());
        }

        return true;
    }

    /**
     * Sets the input parameters from the user selections. Returns false if an
     * error occurs.
     *
     * @return success
     */
    private boolean setParameters() {
        //lambda
        try {
            lambdaUser = Float.parseFloat(lambdaField.getText());
            if (lambdaUser <= 0) {
                JOptionPane.showMessageDialog(this, "Wavelength must be a positive number and different from 0.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please insert a valid wavelength.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        //z
        try {
            zUser = Float.parseFloat(zField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please insert a valid distance.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        //input width
        try {
            inputWUser = Float.parseFloat(inputWField.getText());
            if (inputWUser == 0) {
                JOptionPane.showMessageDialog(this, "Input width must be different from 0.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please insert a valid input width.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        //input height
        try {
            inputHUser = Float.parseFloat(inputHField.getText());
            if (inputHUser == 0) {
                JOptionPane.showMessageDialog(this, "Input height must be different from 0.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please insert a valid input height.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        methodIdx = methodCombo.getSelectedIndex();

        //output width and height (Fresnel-Bluestein)
        if (methodIdx == 2) {
            try {
                outputWUser = Float.parseFloat(outputWField.getText());
                if (outputWUser == 0) {
                    JOptionPane.showMessageDialog(this, "Output width must be different from 0.", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please insert a valid output width.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            try {
                outputHUser = Float.parseFloat(outputHField.getText());
                if (outputHUser == 0) {
                    JOptionPane.showMessageDialog(this, "Output height must be different from 0.", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please insert a valid output height.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        fixUnits();

        //sets the parameters, if methodIdx == 2 -> Fresnel-Bluestein
        if (methodIdx == 2) {
            data.setParameters(lambdaUm, zUm, inputWUm, inputHUm, outputWUm, outputHUm);
        } else {
            data.setParameters(lambdaUm, zUm, inputWUm, inputHUm);
        }

        phaseEnabled = phaseChk.isSelected();
        amplitudeEnabled = amplitudeChk.isSelected();
        intensityEnabled = intensityChk.isSelected();
        realEnabled = realChk.isSelected();
        imaginaryEnabled = imaginaryChk.isSelected();

        //if there isn't at least one output image selected returns error
        if (!phaseEnabled && !amplitudeEnabled && !intensityEnabled && !realEnabled && !imaginaryEnabled) {
            JOptionPane.showMessageDialog(this, "Please select at least one output.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    /**
     * Sets the input parameters from the user selections. Returns false if an
     * error occurs.
     *
     * @return
     */
    private boolean setParameters(boolean inc) {
        //lambda
        try {
            lambdaUser = Float.parseFloat(lambdaField.getText());
            if (lambdaUser <= 0) {
                JOptionPane.showMessageDialog(this, "Wavelength must be a positive number, different from 0.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please insert a valid wavelength.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        //z - step
        try {
            stepUser = Float.parseFloat(stepField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please insert a valid step.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        stepUm = stepUser;

        if (zUnits.equals("nm")) {
            stepUm = stepUser * 1E-3f;
        } else if (zUnits.equals("mm")) {
            stepUm = stepUser * 1E3f;
        } else if (zUnits.equals("cm")) {
            stepUm = stepUser * 1E4f;
        } else if (zUnits.equals("m")) {
            stepUm = stepUser * 1E6f;
        }

        zStepUser = inc ? zStepUser + stepUser : zStepUser - stepUser;
        zStepUm = inc ? zStepUm + stepUm : zStepUm - stepUm;

        //input width
        try {
            inputWUser = Float.parseFloat(inputWField.getText());
            if (inputWUser == 0) {
                JOptionPane.showMessageDialog(this, "Input width must be different from 0.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please insert a valid input width.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        //input height
        try {
            inputHUser = Float.parseFloat(inputHField.getText());
            if (inputHUser == 0) {
                JOptionPane.showMessageDialog(this, "Input height must be different from 0.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please insert a valid input height.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        //output width and height (Fresnel-Bluestein)
        methodIdx = methodCombo.getSelectedIndex();

        if (methodIdx == 2) {
            try {
                outputWUser = Float.parseFloat(outputWField.getText());
                if (outputWUser == 0) {
                    JOptionPane.showMessageDialog(this, "Output width must be different from 0.", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please insert a valid output width.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            try {
                outputHUser = Float.parseFloat(outputHField.getText());
                if (outputHUser == 0) {
                    JOptionPane.showMessageDialog(this, "Output height must be different from 0.", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please insert a valid output height.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        fixUnits();
//        data.setDistance(zStep);

        //sets the parameters, if methodIdx == 2 -> Fresnel-Bluestein
        if (methodIdx == 2) {
            data.setParameters(lambdaUm, zStepUm, inputWUm, inputHUm, outputWUm, outputHUm);
        } else {
            data.setParameters(lambdaUm, zStepUm, inputWUm, inputHUm);
        }

        phaseEnabled = phaseChk.isSelected();
        amplitudeEnabled = amplitudeChk.isSelected();
        intensityEnabled = intensityChk.isSelected();
        realEnabled = realChk.isSelected();
        imaginaryEnabled = imaginaryChk.isSelected();

        //if there isn't at least one output image selected returns error
        if (!phaseEnabled && !amplitudeEnabled && !intensityEnabled && !realEnabled && !imaginaryEnabled) {
            JOptionPane.showMessageDialog(this, "Please select at least one output.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        //returns success
        return true;
    }

    /**
     * Propagates, prints the information on the log and shows the output
     * images.
     *
     * @param useZ
     */
    private void propagate(boolean useZ) {

        data.propagate(methodIdx, filterEnabled, isPlane, curvRadius);

        String[] parameters = getFormattedParameters(useZ);

        updateLog(true,
                "\nMethod: " + PROPAGATION_METHOD[methodIdx]
                + "\nReal input: " + parameters[0]
                + "\nImaginary input: " + parameters[1]
                + "\nWavelength: " + parameters[2]
                + "\nDistance: " + parameters[3]
                + "\nInput Width: " + parameters[4]
                + "\nInput Height: " + parameters[5]);

        if (methodIdx == 2) {
            updateLog(false,
                    "\nOutput Width: " + parameters[6]
                    + "\nOutput Height: " + parameters[7]);
        }

        float[][] field = data.getOutputField();
        if (useZ) {
            setStepDistance();
        }

        calibrate(useZ);

        String names = "; Re: " + parameters[0] + "; Im: " + parameters[1];

        float[][] amplitude = null;
        float max = Float.MIN_VALUE;

        if (realEnabled || imaginaryEnabled) {
            amplitude = ArrayUtils.modulus(field);
            max = ArrayUtils.max(amplitude);
        }

        if (phaseEnabled) {
            ImageProcessor ip1 = new FloatProcessor(ArrayUtils.phase(field));
            ImagePlus imp1 = new ImagePlus("Phase; z = " + parameters[3] + names,
                    phaseByteSelected ? ip1.convertToByteProcessor() : ip1);
            imp1.setCalibration(cal);
            imp1.show();
        }

        if (amplitudeEnabled) {
            ImageProcessor ip2 = new FloatProcessor(realEnabled || imaginaryEnabled ? amplitude : ArrayUtils.modulus(field));
            if (amplitudeLogSelected) {
                ip2.log();
            }

            ImagePlus imp2 = new ImagePlus("Amplitude; z = " + parameters[3] + names,
                    amplitudeByteSelected ? ip2.convertToByteProcessor() : ip2);
            imp2.setCalibration(cal);
            imp2.show();
        }

        if (intensityEnabled) {
            ImageProcessor ip3 = new FloatProcessor(ArrayUtils.modulusSq(field));
            if (intensityLogSelected) {
                ip3.log();
            }

            ImagePlus imp3 = new ImagePlus("Intensity; z = " + parameters[3] + names,
                    intensityByteSelected ? ip3.convertToByteProcessor() : ip3);
            imp3.setCalibration(cal);
            imp3.show();
        }

        if (realEnabled) {
            float[][] real = ArrayUtils.real(field);
            ArrayUtils.divide(real, max);

            ImageProcessor ip4 = new FloatProcessor(real);
            ImagePlus imp4 = new ImagePlus("Real; z = " + parameters[3] + names, ip4);
            imp4.setCalibration(cal);
            imp4.show();
        }

        if (imaginaryEnabled) {
            float[][] imaginary = ArrayUtils.imaginary(field);
            ArrayUtils.divide(imaginary, max);

            ImageProcessor ip5 = new FloatProcessor(imaginary);
            ImagePlus imp5 = new ImagePlus("Imaginary; z = " + parameters[3] + names, ip5);
            imp5.setCalibration(cal);
            imp5.show();
        }
    }

    /**
     * Creates the calibration object for the output images.
     *
     * @param useZ
     */
    private void calibrate(boolean useZ) {
        float dx = inputWUm / M;
        float dy = inputHUm / N;

        float dxOut = 0;
        float dyOut = 0;

        cal = new Calibration();

        if (methodIdx == 0) {
            //angular spectrum, the output field has teh same size of the input
            dxOut = inputWUm / M;
            dyOut = inputHUm / N;
        } else if (methodIdx == 1) {
            //fresnel, the output field has a modified pixel size, given by
            //dxOut = lambda * z / (M * dx)
            dxOut = useZ ? lambdaUm * zUm / (M * dx) : lambdaUm * zStepUm / (M * dx);
            dyOut = useZ ? lambdaUm * zUm / (N * dy) : lambdaUm * zStepUm / (N * dy);

            //sign correction when z < 0
            dxOut *= Math.signum(useZ ? zUm : zStepUm);
            dyOut *= Math.signum(useZ ? zUm : zStepUm);
        } else if (methodIdx == 2) {
            //fresnel-bluestein, the output field size is given by the user
            dxOut = outputWUm / M;
            dyOut = outputHUm / N;
        } else if (methodIdx == 3) {
            float zCrit = M * dx * dx / lambdaUm;
            //if z < zCrit, uses angular spectrum, else fresnel
            if (Math.abs(useZ ? zUm : zStepUm) < zCrit) {
                dxOut = inputWUm / M;
                dyOut = inputHUm / N;
            } else {
                dxOut = useZ ? lambdaUm * zUm / (M * dx) : lambdaUm * zStepUm / (M * dx);
                dyOut = useZ ? lambdaUm * zUm / (N * dy) : lambdaUm * zStepUm / (N * dy);

                dxOut *= Math.signum(useZ ? zUm : zStepUm);
                dyOut *= Math.signum(useZ ? zUm : zStepUm);
            }
        }

        //converts the output size, to user units
        if (outputSizeUnits.equals("nm")) {
            dxOut *= 1E3f;
            dyOut *= 1E3f;
        } else if (outputSizeUnits.equals("mm")) {
            dxOut *= 1E-3f;
            dyOut *= 1E-3f;
        } else if (outputSizeUnits.equals("cm")) {
            dxOut *= 1E-4f;
            dyOut *= 1E-4f;
        } else if (outputSizeUnits.equals("m")) {
            dxOut *= 1E-6f;
            dyOut *= 1E-6f;
        }

        cal.setUnit(outputSizeUnits);
        cal.pixelWidth = dxOut;
        cal.pixelHeight = dyOut;
    }

    /**
     * Returns the calibration object.
     *
     * @return
     */
    public Calibration getCalibration() {
        return cal;
    }

    /**
     * Listener method, updates the input combos.
     *
     * @param imp
     */
    @Override
    public void imageClosed(ImagePlus imp) {
        updateCombos();
    }

    /**
     * Listener method, updates the input combos.
     *
     * @param imp
     */
    @Override
    public void imageOpened(ImagePlus imp) {
        updateCombos();
    }

    /**
     * Listener method, updates the input combos.
     *
     * @param imp
     */
    @Override
    public void imageUpdated(ImagePlus imp) {
        updateCombos();
    }

    /**
     * Updates the information on the combos.
     */
    private void updateCombos() {
        int realIdx = realInputCombo.getSelectedIndex();
        int imaginaryIdx = imaginaryInputCombo.getSelectedIndex();

        getOpenedImages();
        realInputCombo.setModel(new DefaultComboBoxModel<String>(titles));
        realInputCombo.setSelectedIndex((realIdx >= titles.length)
                ? titles.length - 1 : realIdx);

        imaginaryInputCombo.setModel(new DefaultComboBoxModel<String>(titles));
        imaginaryInputCombo.setSelectedIndex((imaginaryIdx >= titles.length)
                ? titles.length - 1 : imaginaryIdx);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popup = new javax.swing.JPopupMenu();
        copyItem = new javax.swing.JMenuItem();
        copyAllItem = new javax.swing.JMenuItem();
        sep1 = new javax.swing.JPopupMenu.Separator();
        wrapItem = new javax.swing.JCheckBoxMenuItem();
        sep2 = new javax.swing.JPopupMenu.Separator();
        clearItem = new javax.swing.JMenuItem();
        parametersPanel = new javax.swing.JPanel();
        methodCombo = new javax.swing.JComboBox();
        realInputCombo = new javax.swing.JComboBox();
        imaginaryInputCombo = new javax.swing.JComboBox();
        lambdaField = new javax.swing.JTextField();
        zField = new javax.swing.JTextField();
        inputWField = new javax.swing.JTextField();
        inputHField = new javax.swing.JTextField();
        outputWField = new javax.swing.JTextField();
        outputHField = new javax.swing.JTextField();
        lockBtn = new javax.swing.JToggleButton();
        outputHLabel = new javax.swing.JLabel();
        outputWLabel = new javax.swing.JLabel();
        inputHLabel = new javax.swing.JLabel();
        inputWLabel = new javax.swing.JLabel();
        zLabel = new javax.swing.JLabel();
        lambdaLabel = new javax.swing.JLabel();
        imaginaryInputLabel = new javax.swing.JLabel();
        realInputLabel = new javax.swing.JLabel();
        methodLabel = new javax.swing.JLabel();
        btnsPanel = new javax.swing.JPanel();
        propagatePanel = new javax.swing.JPanel();
        propagateBtn = new javax.swing.JButton();
        roiChk = new javax.swing.JCheckBox();
        settingsBtn = new javax.swing.JButton();
        batchBtn = new javax.swing.JButton();
        incBtn = new javax.swing.JButton();
        stepField = new javax.swing.JTextField();
        decBtn = new javax.swing.JButton();
        chkPanel = new javax.swing.JPanel();
        phaseChk = new javax.swing.JCheckBox();
        amplitudeChk = new javax.swing.JCheckBox();
        intensityChk = new javax.swing.JCheckBox();
        realChk = new javax.swing.JCheckBox();
        imaginaryChk = new javax.swing.JCheckBox();
        logPane = new javax.swing.JScrollPane();
        log = new javax.swing.JTextArea();

        copyItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/page_white_copy.png"))); // NOI18N
        copyItem.setText("Copy");
        copyItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyItemActionPerformed(evt);
            }
        });
        popup.add(copyItem);

        copyAllItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/page_copy.png"))); // NOI18N
        copyAllItem.setText("Copy All");
        copyAllItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyAllItemActionPerformed(evt);
            }
        });
        popup.add(copyAllItem);
        popup.add(sep1);

        wrapItem.setSelected(logWrapping);
        wrapItem.setText("Wrap");
        wrapItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wrapItemActionPerformed(evt);
            }
        });
        popup.add(wrapItem);
        popup.add(sep2);

        clearItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/page_delete.png"))); // NOI18N
        clearItem.setText("Clear");
        clearItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearItemActionPerformed(evt);
            }
        });
        popup.add(clearItem);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(TITLE);
        setBounds(new java.awt.Rectangle(locX, locY, 0, 0)
        );
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png")));
        setMaximumSize(new java.awt.Dimension(545, 311));
        setMinimumSize(new java.awt.Dimension(545, 311));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        parametersPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Parameters"));
        parametersPanel.setMaximumSize(new java.awt.Dimension(255, 301));
        parametersPanel.setMinimumSize(new java.awt.Dimension(255, 301));

        methodCombo.setModel(new DefaultComboBoxModel<String>(PROPAGATION_METHOD));
        methodCombo.setSelectedIndex(methodIdx);
        methodCombo.setMaximumSize(new java.awt.Dimension(115, 20));
        methodCombo.setMinimumSize(new java.awt.Dimension(115, 20));
        methodCombo.setPreferredSize(new java.awt.Dimension(115, 20));
        methodCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                methodComboActionPerformed(evt);
            }
        });

        realInputCombo.setModel(new DefaultComboBoxModel<String>(titles)
        );
        realInputCombo.setSelectedIndex(titles.length > 1 ? 1 : 0);
        realInputCombo.setMaximumSize(new java.awt.Dimension(115, 20));
        realInputCombo.setMinimumSize(new java.awt.Dimension(115, 20));
        realInputCombo.setPreferredSize(new java.awt.Dimension(115, 20));

        imaginaryInputCombo.setModel(new DefaultComboBoxModel<String>(titles));
        imaginaryInputCombo.setSelectedIndex(titles.length > 2 ? 2 : 0);
        imaginaryInputCombo.setMaximumSize(new java.awt.Dimension(115, 20));
        imaginaryInputCombo.setMinimumSize(new java.awt.Dimension(115, 20));
        imaginaryInputCombo.setPreferredSize(new java.awt.Dimension(115, 20));

        lambdaField.setText(lambdaString);
        lambdaField.setMaximumSize(new java.awt.Dimension(115, 20));
        lambdaField.setMinimumSize(new java.awt.Dimension(115, 20));
        lambdaField.setPreferredSize(new java.awt.Dimension(115, 20));
        lambdaField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldFocusGained(evt);
            }
        });

        zField.setText(zString);
        zField.setMaximumSize(new java.awt.Dimension(115, 20));
        zField.setMinimumSize(new java.awt.Dimension(115, 20));
        zField.setPreferredSize(new java.awt.Dimension(115, 20));
        zField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldFocusGained(evt);
            }
        });

        inputWField.setText(inputWString);
        inputWField.setMaximumSize(new java.awt.Dimension(115, 20));
        inputWField.setMinimumSize(new java.awt.Dimension(115, 20));
        inputWField.setPreferredSize(new java.awt.Dimension(115, 20));
        inputWField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldFocusGained(evt);
            }
        });

        inputHField.setText(inputHString);
        inputHField.setMaximumSize(new java.awt.Dimension(115, 20));
        inputHField.setMinimumSize(new java.awt.Dimension(115, 20));
        inputHField.setPreferredSize(new java.awt.Dimension(115, 20));
        inputHField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldFocusGained(evt);
            }
        });

        outputWField.setText(outputWString);
        outputWField.setEnabled(methodIdx == 2);
        outputWField.setMaximumSize(new java.awt.Dimension(83, 20));
        outputWField.setMinimumSize(new java.awt.Dimension(83, 20));
        outputWField.setPreferredSize(new java.awt.Dimension(83, 20));
        outputWField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldFocusGained(evt);
            }
        });
        outputWField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outputWFieldActionPerformed(evt);
            }
        });

        outputHField.setText(outputHString);
        outputHField.setEnabled(methodIdx == 2);
        outputHField.setMaximumSize(new java.awt.Dimension(83, 20));
        outputHField.setMinimumSize(new java.awt.Dimension(83, 20));
        outputHField.setPreferredSize(new java.awt.Dimension(83, 20));
        outputHField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldFocusGained(evt);
            }
        });
        outputHField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outputHFieldActionPerformed(evt);
            }
        });

        lockBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource(relationLock ? "/lock.png" : "/lock_open.png")));
        lockBtn.setEnabled(methodIdx == 2);
        lockBtn.setMaximumSize(new java.awt.Dimension(25, 25));
        lockBtn.setMinimumSize(new java.awt.Dimension(25, 25));
        lockBtn.setPreferredSize(new java.awt.Dimension(25, 25));
        lockBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lockBtnActionPerformed(evt);
            }
        });

        outputHLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        outputHLabel.setText("Output height [" + outputSizeUnits + "]:");
        outputHLabel.setMaximumSize(new java.awt.Dimension(100, 14));
        outputHLabel.setMinimumSize(new java.awt.Dimension(100, 14));
        outputHLabel.setPreferredSize(new java.awt.Dimension(100, 14));

        outputWLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        outputWLabel.setText("Output width [" + outputSizeUnits + "]:");
        outputWLabel.setMaximumSize(new java.awt.Dimension(100, 14));
        outputWLabel.setMinimumSize(new java.awt.Dimension(100, 14));
        outputWLabel.setPreferredSize(new java.awt.Dimension(100, 14));

        inputHLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        inputHLabel.setText("Input height [" + inputSizeUnits + "]:");
        inputHLabel.setMaximumSize(new java.awt.Dimension(100, 14));
        inputHLabel.setMinimumSize(new java.awt.Dimension(100, 14));
        inputHLabel.setPreferredSize(new java.awt.Dimension(100, 14));

        inputWLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        inputWLabel.setText("Input width [" + inputSizeUnits + "]:");
        inputWLabel.setMaximumSize(new java.awt.Dimension(100, 14));
        inputWLabel.setMinimumSize(new java.awt.Dimension(100, 14));
        inputWLabel.setPreferredSize(new java.awt.Dimension(100, 14));

        zLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        zLabel.setText("Distance ["+zUnits+"]:");
        zLabel.setMaximumSize(new java.awt.Dimension(100, 14));
        zLabel.setMinimumSize(new java.awt.Dimension(100, 14));
        zLabel.setPreferredSize(new java.awt.Dimension(100, 14));

        lambdaLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lambdaLabel.setText("Wavelength ["+lambdaUnits+"]:");
        lambdaLabel.setMaximumSize(new java.awt.Dimension(100, 14));
        lambdaLabel.setMinimumSize(new java.awt.Dimension(100, 14));
        lambdaLabel.setPreferredSize(new java.awt.Dimension(100, 14));

        imaginaryInputLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        imaginaryInputLabel.setText("Imaginary input:");
        imaginaryInputLabel.setMaximumSize(new java.awt.Dimension(100, 14));
        imaginaryInputLabel.setMinimumSize(new java.awt.Dimension(100, 14));
        imaginaryInputLabel.setPreferredSize(new java.awt.Dimension(100, 14));

        realInputLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        realInputLabel.setText("Real input:");
        realInputLabel.setMaximumSize(new java.awt.Dimension(100, 14));
        realInputLabel.setMinimumSize(new java.awt.Dimension(100, 14));
        realInputLabel.setPreferredSize(new java.awt.Dimension(100, 14));

        methodLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        methodLabel.setText("Method:");
        methodLabel.setMaximumSize(new java.awt.Dimension(100, 14));
        methodLabel.setMinimumSize(new java.awt.Dimension(100, 14));
        methodLabel.setPreferredSize(new java.awt.Dimension(100, 14));

        javax.swing.GroupLayout parametersPanelLayout = new javax.swing.GroupLayout(parametersPanel);
        parametersPanel.setLayout(parametersPanelLayout);
        parametersPanelLayout.setHorizontalGroup(
            parametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, parametersPanelLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(parametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(outputHLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(outputWLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inputHLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inputWLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(zLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lambdaLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(imaginaryInputLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(realInputLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(methodLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(parametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(methodCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(realInputCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(imaginaryInputCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lambdaField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(zField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inputWField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inputHField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(parametersPanelLayout.createSequentialGroup()
                        .addGroup(parametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(outputHField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(outputWField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lockBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5))
        );
        parametersPanelLayout.setVerticalGroup(
            parametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(parametersPanelLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(parametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(methodCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(methodLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(parametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(realInputCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(realInputLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(parametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(imaginaryInputCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(imaginaryInputLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(parametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lambdaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lambdaLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(parametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(zField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(zLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(parametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inputWField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inputWLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(parametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inputHField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inputHLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(parametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(parametersPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(parametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(outputWLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(outputWField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(parametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(outputHLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(outputHField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(parametersPanelLayout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(lockBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5))
        );

        btnsPanel.setMaximumSize(new java.awt.Dimension(270, 66));
        btnsPanel.setMinimumSize(new java.awt.Dimension(270, 66));

        propagatePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        propagatePanel.setMaximumSize(new java.awt.Dimension(173, 32));
        propagatePanel.setMinimumSize(new java.awt.Dimension(173, 32));

        propagateBtn.setText("Propagate");
        propagateBtn.setMaximumSize(new java.awt.Dimension(90, 23));
        propagateBtn.setMinimumSize(new java.awt.Dimension(90, 23));
        propagateBtn.setPreferredSize(new java.awt.Dimension(90, 23));
        propagateBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                propagateBtnActionPerformed(evt);
            }
        });

        roiChk.setSelected(roiEnabled);
        roiChk.setText("Same ROI");
        roiChk.setEnabled(false);

        javax.swing.GroupLayout propagatePanelLayout = new javax.swing.GroupLayout(propagatePanel);
        propagatePanel.setLayout(propagatePanelLayout);
        propagatePanelLayout.setHorizontalGroup(
            propagatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(propagatePanelLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(propagateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(roiChk)
                .addGap(2, 2, 2))
        );
        propagatePanelLayout.setVerticalGroup(
            propagatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(propagatePanelLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(propagatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(propagateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(roiChk))
                .addGap(2, 2, 2))
        );

        settingsBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wrench.png"))); // NOI18N
        settingsBtn.setText("Settings");
        settingsBtn.setMaximumSize(new java.awt.Dimension(91, 23));
        settingsBtn.setMinimumSize(new java.awt.Dimension(91, 23));
        settingsBtn.setPreferredSize(new java.awt.Dimension(91, 23));
        settingsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settingsBtnActionPerformed(evt);
            }
        });

        batchBtn.setText("Batch");
        batchBtn.setEnabled(false);
        batchBtn.setMaximumSize(new java.awt.Dimension(91, 23));
        batchBtn.setMinimumSize(new java.awt.Dimension(91, 23));
        batchBtn.setPreferredSize(new java.awt.Dimension(91, 23));
        batchBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batchBtnActionPerformed(evt);
            }
        });

        incBtn.setText("+");
        incBtn.setEnabled(false);
        incBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                incBtnActionPerformed(evt);
            }
        });

        stepField.setText(stepString);
        stepField.setEnabled(false);
        stepField.setMaximumSize(new java.awt.Dimension(79, 20));
        stepField.setMinimumSize(new java.awt.Dimension(79, 20));
        stepField.setPreferredSize(new java.awt.Dimension(79, 20));
        stepField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldFocusGained(evt);
            }
        });

        decBtn.setText("-");
        decBtn.setEnabled(false);
        decBtn.setMaximumSize(new java.awt.Dimension(41, 23));
        decBtn.setMinimumSize(new java.awt.Dimension(41, 23));
        decBtn.setPreferredSize(new java.awt.Dimension(41, 23));
        decBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                decBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout btnsPanelLayout = new javax.swing.GroupLayout(btnsPanel);
        btnsPanel.setLayout(btnsPanelLayout);
        btnsPanelLayout.setHorizontalGroup(
            btnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btnsPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(btnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(btnsPanelLayout.createSequentialGroup()
                        .addComponent(decBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(stepField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(incBtn))
                    .addComponent(propagatePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(btnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(settingsBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(batchBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );
        btnsPanelLayout.setVerticalGroup(
            btnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btnsPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(btnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(propagatePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(settingsBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(btnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(decBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(stepField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(incBtn)
                    .addComponent(batchBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );

        chkPanel.setMaximumSize(new java.awt.Dimension(269, 23));
        chkPanel.setMinimumSize(new java.awt.Dimension(269, 23));

        phaseChk.setSelected(phaseEnabled);
        phaseChk.setText("Phase");

        amplitudeChk.setSelected(amplitudeEnabled);
        amplitudeChk.setText("Amp.");
        amplitudeChk.setToolTipText("Amplitude");

        intensityChk.setSelected(intensityEnabled);
        intensityChk.setText("Int.");
        intensityChk.setToolTipText("Intensity");

        realChk.setSelected(realEnabled);
        realChk.setText("Real");

        imaginaryChk.setSelected(imaginaryEnabled);
        imaginaryChk.setText("Imaginary");

        javax.swing.GroupLayout chkPanelLayout = new javax.swing.GroupLayout(chkPanel);
        chkPanel.setLayout(chkPanelLayout);
        chkPanelLayout.setHorizontalGroup(
            chkPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(chkPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(phaseChk)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(amplitudeChk)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(intensityChk)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(realChk)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(imaginaryChk)
                .addGap(0, 0, 0))
        );
        chkPanelLayout.setVerticalGroup(
            chkPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(chkPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(chkPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(phaseChk)
                    .addComponent(amplitudeChk)
                    .addComponent(intensityChk)
                    .addComponent(realChk)
                    .addComponent(imaginaryChk))
                .addGap(0, 0, 0))
        );

        logPane.setAutoscrolls(true);
        logPane.setMaximumSize(new java.awt.Dimension(274, 196));
        logPane.setMinimumSize(new java.awt.Dimension(274, 196));
        logPane.setPreferredSize(new java.awt.Dimension(274, 196));

        log.setEditable(false);
        log.setColumns(20);
        log.setLineWrap(logWrapping);
        log.setRows(5);
        log.setText(LOG_HEADER);
        log.setWrapStyleWord(true);
        log.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                logMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                logMouseReleased(evt);
            }
        });
        logPane.setViewportView(log);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(parametersPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(logPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(logPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(8, 8, 8)
                        .addComponent(chkPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(btnsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(parametersPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void propagateBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_propagateBtnActionPerformed
        if (filterFrame != null && filterFrame.isVisible()) {
            filterFrame.setState(Frame.NORMAL);
            filterFrame.toFront();
            return;
        }

        int realIdx = realInputCombo.getSelectedIndex();
        int imaginaryIdx = imaginaryInputCombo.getSelectedIndex();

        boolean success = setInputImages(realIdx, imaginaryIdx);
        if (!success) {
            return;
        }

        success = setParameters();
        if (!success) {
            return;
        }

        if (!filterEnabled) {
            propagate(true);
            enableAfterPropagationOpt(true);

            return;
        }

        boolean differentDimensions = oldMReal != newMReal || oldNReal != newNReal
                || oldMImaginary != newMImaginary || oldNImaginary != newNImaginary;
        boolean differentIDs = oldIDReal != newIDReal || oldIDImaginary != newIDImaginary;

        if (roiChk.isEnabled() && roiChk.isSelected() && !differentDimensions) {
            if (differentIDs) {
                data.calculateFFT();
                data.center();
            }

            propagate(true);
            setImageProps();

            return;
        }

        pref.putBoolean(PHASE_CHECKED, phaseEnabled);
        pref.putBoolean(AMPLITUDE_CHECKED, amplitudeEnabled);
        pref.putBoolean(INTENSITY_CHECKED, intensityEnabled);
        pref.putBoolean(REAL_CHECKED, realEnabled);
        pref.putBoolean(IMAGINARY_CHECKED, imaginaryEnabled);

        calibrate(true);

        if (filterFrame == null || !filterFrame.isDisplayable()) {
            filterFrame = new FilterFrame(this, methodIdx);
        }
    }//GEN-LAST:event_propagateBtnActionPerformed

    private void settingsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_settingsBtnActionPerformed
        if (settingsFrame == null || !settingsFrame.isDisplayable()) {
            settingsFrame = new SettingsFrame(this);
            settingsFrame.setVisible(true);
        } else {
            settingsFrame.setState(Frame.NORMAL);
            settingsFrame.toFront();
        }
    }//GEN-LAST:event_settingsBtnActionPerformed

    private void decBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_decBtnActionPerformed
        boolean success = setParameters(false);
        if (!success) {
            return;
        }

        propagate(false);
    }//GEN-LAST:event_decBtnActionPerformed

    private void incBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_incBtnActionPerformed
        boolean success = setParameters(true);
        if (!success) {
            return;
        }

        propagate(false);
    }//GEN-LAST:event_incBtnActionPerformed

    private void batchBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batchBtnActionPerformed
        if (batchFrame != null && batchFrame.isVisible()) {
            batchFrame.setState(Frame.NORMAL);
            batchFrame.toFront();
            return;
        }

        setParameters();

        pref.putBoolean(PHASE_CHECKED, phaseEnabled);
        pref.putBoolean(AMPLITUDE_CHECKED, amplitudeEnabled);
        pref.putBoolean(INTENSITY_CHECKED, intensityEnabled);
        pref.putBoolean(REAL_CHECKED, realEnabled);
        pref.putBoolean(IMAGINARY_CHECKED, imaginaryEnabled);

        calibrate(true);

        if (batchFrame == null || !batchFrame.isDisplayable()) {
            batchFrame = new BatchFrame(this, methodIdx);
            batchFrame.setVisible(true);
        }
    }//GEN-LAST:event_batchBtnActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (filterFrame != null && filterFrame.isVisible()) {
            filterFrame.close(false);
        }

        if (settingsFrame != null && settingsFrame.isVisible()) {
            settingsFrame.setVisible(false);
            settingsFrame.dispose();
        }

        if (batchFrame != null && batchFrame.isVisible()) {
            batchFrame.setVisible(false);
            batchFrame.dispose();
        }

        savePrefs();
        ImagePlus.removeImageListener(this);
        setVisible(false);
        dispose();
    }//GEN-LAST:event_formWindowClosing

    private void methodComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_methodComboActionPerformed
        outputWField.setEnabled(methodCombo.getSelectedIndex() == 2);
        outputHField.setEnabled(methodCombo.getSelectedIndex() == 2);
        lockBtn.setEnabled(methodCombo.getSelectedIndex() == 2);
    }//GEN-LAST:event_methodComboActionPerformed

    private void lockBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lockBtnActionPerformed
        relationLock = lockBtn.isSelected();
        lockBtn.setIcon(new ImageIcon(getClass().getResource(relationLock
                ? "/lock.png" : "/lock_open.png")));
    }//GEN-LAST:event_lockBtnActionPerformed

    private void outputWFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outputWFieldActionPerformed
        if (!relationLock) {
            return;
        }

        float ratio, inW, inH, outW, outH;

        try {
            inW = Float.parseFloat(inputWField.getText());
            inH = Float.parseFloat(inputHField.getText());

            if (inW == 0 || inH == 0) {
                return;
            }
            ratio = inW / inH;

            outW = Float.parseFloat(outputWField.getText());
            if (outW == 0) {
                return;
            }
            outH = outW / ratio;
            outputHField.setText("" + outH);
        } catch (NumberFormatException exc) {

        }
    }//GEN-LAST:event_outputWFieldActionPerformed

    private void outputHFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outputHFieldActionPerformed
        if (!relationLock) {
            return;
        }

        float ratio, inW, inH, outW, outH;

        try {
            inW = Float.parseFloat(inputWField.getText());
            inH = Float.parseFloat(inputHField.getText());

            if (inW == 0 || inH == 0) {
                return;
            }
            ratio = inW / inH;

            outH = Float.parseFloat(outputHField.getText());
            if (outH == 0) {
                return;
            }
            outW = outH * ratio;
            outputWField.setText("" + outW);
        } catch (NumberFormatException exc) {

        }
    }//GEN-LAST:event_outputHFieldActionPerformed

    private void logMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logMousePressed
        if (evt.isPopupTrigger()) {
            popup.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_logMousePressed

    private void logMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logMouseReleased
        if (evt.isPopupTrigger()) {
            popup.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_logMouseReleased

    private void copyItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyItemActionPerformed
        String s = log.getSelectedText();

        StringSelection stringSelection = new StringSelection(s);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }//GEN-LAST:event_copyItemActionPerformed

    private void copyAllItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyAllItemActionPerformed
        String s = log.getText();

        StringSelection stringSelection = new StringSelection((s != null) ? s : "");
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }//GEN-LAST:event_copyAllItemActionPerformed

    private void wrapItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wrapItemActionPerformed
        log.setLineWrap(wrapItem.isSelected());
    }//GEN-LAST:event_wrapItemActionPerformed

    private void clearItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearItemActionPerformed
        log.setText(LOG_HEADER);
    }//GEN-LAST:event_clearItemActionPerformed

    private void textFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textFieldFocusGained
        JTextField field = (JTextField) evt.getComponent();
        field.selectAll();
    }//GEN-LAST:event_textFieldFocusGained

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox amplitudeChk;
    private javax.swing.JButton batchBtn;
    private javax.swing.JPanel btnsPanel;
    private javax.swing.JPanel chkPanel;
    private javax.swing.JMenuItem clearItem;
    private javax.swing.JMenuItem copyAllItem;
    private javax.swing.JMenuItem copyItem;
    private javax.swing.JButton decBtn;
    private javax.swing.JCheckBox imaginaryChk;
    private javax.swing.JComboBox imaginaryInputCombo;
    private javax.swing.JLabel imaginaryInputLabel;
    private javax.swing.JButton incBtn;
    private javax.swing.JTextField inputHField;
    private javax.swing.JLabel inputHLabel;
    private javax.swing.JTextField inputWField;
    private javax.swing.JLabel inputWLabel;
    private javax.swing.JCheckBox intensityChk;
    private javax.swing.JTextField lambdaField;
    private javax.swing.JLabel lambdaLabel;
    private javax.swing.JToggleButton lockBtn;
    private javax.swing.JTextArea log;
    private javax.swing.JScrollPane logPane;
    private javax.swing.JComboBox methodCombo;
    private javax.swing.JLabel methodLabel;
    private javax.swing.JTextField outputHField;
    private javax.swing.JLabel outputHLabel;
    private javax.swing.JTextField outputWField;
    private javax.swing.JLabel outputWLabel;
    private javax.swing.JPanel parametersPanel;
    private javax.swing.JCheckBox phaseChk;
    private javax.swing.JPopupMenu popup;
    private javax.swing.JButton propagateBtn;
    private javax.swing.JPanel propagatePanel;
    private javax.swing.JCheckBox realChk;
    private javax.swing.JComboBox realInputCombo;
    private javax.swing.JLabel realInputLabel;
    private javax.swing.JCheckBox roiChk;
    private javax.swing.JPopupMenu.Separator sep1;
    private javax.swing.JPopupMenu.Separator sep2;
    private javax.swing.JButton settingsBtn;
    private javax.swing.JTextField stepField;
    private javax.swing.JCheckBoxMenuItem wrapItem;
    private javax.swing.JTextField zField;
    private javax.swing.JLabel zLabel;
    // End of variables declaration//GEN-END:variables
}
