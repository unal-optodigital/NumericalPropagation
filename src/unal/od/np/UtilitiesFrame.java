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
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import java.awt.Toolkit;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.prefs.Preferences;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import unal.od.jdiffraction.cpu.utils.ArrayUtils;

/**
 *
 * @author Raul Castañeda (racastanedaq@unal.edu.co)
 * @author Pablo Piedrahita-Quintero (jppiedrahitaq@unal.edu.co)
 * @author Jorge Garcia-Sucerquia (jigarcia@unal.edu.co)
 */
public class UtilitiesFrame extends javax.swing.JFrame implements ImageListener, PreferencesKeys {

    private static final String TITLE = "Utilities";

    public static final String[] OPERATIONS = {"Phase", "Amplitude", "Intensity",
        "Add", "Subtract", "Multiply"};

    private int[] windowsId;
    private String[] titles1;
    private String[] titles2;

    //plane user inputs in user units
    private int M;
    private int N;
    private float widthUser;
    private float heightUser;
    private float lambdaUser;
//    private float zUser;
    private float aUser;
    private float bUser;
    private double aRadians;
    private double bRadians;
//    private double C;

    private double alfa;
    private double beta;
    private double gamma;

    private float curvRadiusUser;

    //user inputs converted to um
    private float widthUm;
    private float heightUm;
    private float lambdaUm;
//    private float zUm;

    private float curvRadiusUm;

    //wavefront arrays
    private float[][] wavefront;
    private float[][] wavefrontReal;
    private float[][] wavefrontImaginary;

    // <editor-fold defaultstate="collapsed" desc="Prefs variables">
    //frame location
    private int locX;
    private int locY;

    //parameters units
    private String lambdaUnits;
    private String zUnits;
    private String inputSizeUnits;
    //    private String inputWUnits;
    //    private String inputHUnits;
    private String outputSizeUnits;
    //    private String outputWUnits;
    //    private String outputHUnits;
    private String curvRadiusUnits;

    //plane fields
    private String planeMString;
    private String planeNString;
    private String planeWidthString;
    private String planeHeightString;
    private String planeLambdaString;
    private String planeZString;
    private String planeAString;
    private String planeBString;
    private String planeCString;

    //spherical fields
    private String sphericalMString;
    private String sphericalNString;
    private String sphericalWidthString;
    private String sphericalHeightString;
    private String sphericalLambdaString;
//    private String sphericalZ;
    private String sphericalCurvRadiusString;
    // </editor-fold>

    //formatter
    private final DecimalFormat df;
    
    private final Preferences pref;

    /**
     * Creates new form UtilitiesFrame
     */
    public UtilitiesFrame() {
        df = new DecimalFormat("#.####", new DecimalFormatSymbols(Locale.US));
        pref = Preferences.userNodeForPackage(getClass());

        getOpenedImages();
        loadPrefs();

        initComponents();

        //adds this class as ImageListener
        ImagePlus.addImageListener(this);
    }

    private void getOpenedImages() {
        windowsId = WindowManager.getIDList();

        //titles real1 and imaginary1 inputs
        //titles real2 and imaginary2 inputs, with <none> option
        if (windowsId == null) {
            titles1 = new String[]{"<none>"};
            titles2 = new String[]{"<none>"};
        } else {
            titles1 = new String[windowsId.length];
            titles2 = new String[windowsId.length + 1];
            titles2[0] = "<none>";
            for (int i = 0; i < windowsId.length; i++) {
                ImagePlus imp = WindowManager.getImage(windowsId[i]);
                if (imp != null) {
                    titles1[i] = imp.getTitle();
                    titles2[i + 1] = imp.getTitle();
                } else {
                    titles1[i] = "";
                    titles2[i + 1] = "";
                }
            }
        }
    }

    private void savePrefs() {
        //frame location
        pref.putInt(UTILITIES_FRAME_LOC_X, getLocation().x);
        pref.putInt(UTILITIES_FRAME_LOC_Y, getLocation().y);

        //plane fields
        pref.put(PLANE_M, planeMField.getText());
        pref.put(PLANE_N, planeNField.getText());
        pref.put(PLANE_WIDTH, planeWidthField.getText());
        pref.put(PLANE_HEIGHT, planeHeightField.getText());
        pref.put(PLANE_WAVELENGTH, planeLambdaField.getText());
//        pref.put(PLANE_DISTANCE, planeZField.getText());
        pref.put(PLANE_A, aField.getText());
        pref.put(PLANE_B, bField.getText());
//        pref.put(PLANE_C, cField.getText());

        //spherical fields
        pref.put(SPHERICAL_M, sphericalMField.getText());
        pref.put(SPHERICAL_N, sphericalNField.getText());
        pref.put(SPHERICAL_WIDTH, sphericalWidthField.getText());
        pref.put(SPHERICAL_HEIGHT, sphericalHeightField.getText());
        pref.put(SPHERICAL_WAVELENGTH, sphericalLambdaField.getText());
        pref.put(SPHERICAL_CURV_RADIUS, sphericalCurvRadiusField.getText());
    }

    private void loadPrefs() {
        //frame location
        locX = pref.getInt(MAIN_FRAME_LOC_X, 300);
        locY = pref.getInt(MAIN_FRAME_LOC_Y, 300);

        //units
        lambdaUnits = pref.get(LAMBDA_UNITS, "nm");
        zUnits = pref.get(DISTANCE_UNITS, "m");
        inputSizeUnits = pref.get(INPUT_SIZE_UNITS, "mm");
//        inputWUnits = pref.get(INPUT_WIDTH_UNITS, "mm");
//        inputHUnits = pref.get(INPUT_HEIGHT_UNITS, "mm");
        outputSizeUnits = pref.get(OUTPUT_SIZE_UNITS, "mm");
//        outputWUnits = pref.get(OUTPUT_WIDTH_UNITS, "mm");
//        outputHUnits = pref.get(OUTPUT_HEIGHT_UNITS, "mm");
        curvRadiusUnits = pref.get(CURVATURE_RADIUS_UNITS, "m");

        //plane fields
        planeMString = pref.get(PLANE_M, "");
        planeNString = pref.get(PLANE_N, "");
        planeWidthString = pref.get(PLANE_WIDTH, "");
        planeHeightString = pref.get(PLANE_HEIGHT, "");
        planeLambdaString = pref.get(PLANE_WAVELENGTH, "");
        planeZString = pref.get(PLANE_DISTANCE, "");
        planeAString = pref.get(PLANE_A, "");
        planeBString = pref.get(PLANE_B, "");
        planeCString = pref.get(PLANE_C, "");

        //spherical fields
        sphericalMString = pref.get(SPHERICAL_M, "");
        sphericalNString = pref.get(SPHERICAL_N, "");
        sphericalWidthString = pref.get(SPHERICAL_WIDTH, "");
        sphericalHeightString = pref.get(SPHERICAL_HEIGHT, "");
        sphericalLambdaString = pref.get(SPHERICAL_WAVELENGTH, "");
//        sphericalZ = pref.get(SPHERICAL_DISTANCE, "");
        sphericalCurvRadiusString = pref.get(SPHERICAL_CURV_RADIUS, "");
    }

    private void phase() {
        int realIdx = realCombo1.getSelectedIndex();
        int imaginaryIdx = imaginaryCombo1.getSelectedIndex();

        String realTitle = titles1[realIdx];
        String imaginaryTitle = titles1[imaginaryIdx];

        if (realTitle.equalsIgnoreCase("<none>") || imaginaryTitle.equalsIgnoreCase("<none>")) {
            JOptionPane.showMessageDialog(this, "Please select the inputs for the complex field.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ImagePlus impReal = WindowManager.getImage(windowsId[realIdx]);
        ImagePlus impImaginary = WindowManager.getImage(windowsId[imaginaryIdx]);

        ImageProcessor ipReal = impReal.getProcessor();
        ImageProcessor ipImaginary = impImaginary.getProcessor();

        int M = ipReal.getWidth();
        int N = ipReal.getHeight();

        if (M != ipImaginary.getWidth() || N != ipImaginary.getHeight()) {
            JOptionPane.showMessageDialog(this, "Input images must have the same dimensions.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        float[][] field = ArrayUtils.complexAmplitude2(ipReal.getFloatArray(), ipImaginary.getFloatArray());

        float[][] phase = ArrayUtils.phase(field);

        ImageProcessor ip = new FloatProcessor(phase);
        ImagePlus imp = new ImagePlus("Phase of " + realTitle
                + " and " + imaginaryTitle, ip);
        imp.show();
    }

    private void amplitude() {
        int realIdx = realCombo1.getSelectedIndex();
        int imaginaryIdx = imaginaryCombo1.getSelectedIndex();

        String realTitle = titles1[realIdx];
        String imaginaryTitle = titles1[imaginaryIdx];

        if (realTitle.equalsIgnoreCase("<none>") || imaginaryTitle.equalsIgnoreCase("<none>")) {
            JOptionPane.showMessageDialog(this, "Please select the inputs for the complex field.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ImagePlus impReal = WindowManager.getImage(windowsId[realIdx]);
        ImagePlus impImaginary = WindowManager.getImage(windowsId[imaginaryIdx]);

        ImageProcessor ipReal = impReal.getProcessor();
        ImageProcessor ipImaginary = impImaginary.getProcessor();

        int M = ipReal.getWidth();
        int N = ipReal.getHeight();

        if (M != ipImaginary.getWidth() || N != ipImaginary.getHeight()) {
            JOptionPane.showMessageDialog(this, "Input images must have the same dimensions.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        float[][] field = ArrayUtils.complexAmplitude2(ipReal.getFloatArray(), ipImaginary.getFloatArray());

        float[][] amplitude = ArrayUtils.modulus(field);

        ImageProcessor ip = new FloatProcessor(amplitude);
        ImagePlus imp = new ImagePlus("Amplitude of " + realTitle
                + " and " + imaginaryTitle, ip);
        imp.show();
    }

    private void intensity() {
        int realIdx = realCombo1.getSelectedIndex();
        int imaginaryIdx = imaginaryCombo1.getSelectedIndex();

        String realTitle = titles1[realIdx];
        String imaginaryTitle = titles1[imaginaryIdx];

        if (realTitle.equalsIgnoreCase("<none>") || imaginaryTitle.equalsIgnoreCase("<none>")) {
            JOptionPane.showMessageDialog(this, "Please select the inputs for the complex field.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ImagePlus impReal = WindowManager.getImage(windowsId[realIdx]);
        ImagePlus impImaginary = WindowManager.getImage(windowsId[imaginaryIdx]);

        ImageProcessor ipReal = impReal.getProcessor();
        ImageProcessor ipImaginary = impImaginary.getProcessor();

        int M = ipReal.getWidth();
        int N = ipReal.getHeight();

        if (M != ipImaginary.getWidth() || N != ipImaginary.getHeight()) {
            JOptionPane.showMessageDialog(this, "Input images must have the same dimensions.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        float[][] field = ArrayUtils.complexAmplitude2(ipReal.getFloatArray(), ipImaginary.getFloatArray());

        float[][] intensity = ArrayUtils.modulusSq(field);

        ImageProcessor ip = new FloatProcessor(intensity);
        ImagePlus imp = new ImagePlus("Intensity of " + realTitle
                + " and " + imaginaryTitle, ip);
        imp.show();
    }

    private void add() {
        int realIdx1 = realCombo1.getSelectedIndex();
        int imaginaryIdx1 = imaginaryCombo1.getSelectedIndex();

        String realTitle1 = titles1[realIdx1];
        String imaginaryTitle1 = titles1[imaginaryIdx1];

        if (realTitle1.equalsIgnoreCase("<none>") || imaginaryTitle1.equalsIgnoreCase("<none>")) {
            JOptionPane.showMessageDialog(this, "Please both inputs for the first complex field.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int realIdx2 = realCombo2.getSelectedIndex();
        int imaginaryIdx2 = imaginaryCombo2.getSelectedIndex();

        String realTitle2 = titles2[realIdx2];
        String imaginaryTitle2 = titles2[imaginaryIdx2];

        if (realTitle2.equalsIgnoreCase("<none>") || imaginaryTitle2.equalsIgnoreCase("<none>")) {
            JOptionPane.showMessageDialog(this, "Please both inputs for the second complex field.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ImagePlus impReal1 = WindowManager.getImage(windowsId[realIdx1]);
        ImagePlus impImaginary1 = WindowManager.getImage(windowsId[imaginaryIdx1]);

        ImageProcessor ipReal1 = impReal1.getProcessor();
        ImageProcessor ipImaginary1 = impImaginary1.getProcessor();

        int M = ipReal1.getWidth();
        int N = ipReal1.getHeight();

        ImagePlus impReal2 = WindowManager.getImage(windowsId[realIdx2 - 1]);
        ImagePlus impImaginary2 = WindowManager.getImage(windowsId[imaginaryIdx2 - 1]);

        ImageProcessor ipReal2 = impReal2.getProcessor();
        ImageProcessor ipImaginary2 = impImaginary2.getProcessor();

        if (M != ipImaginary1.getWidth() || N != ipImaginary1.getHeight()
                || M != ipReal2.getWidth() || N != ipReal2.getHeight()
                || M != ipImaginary2.getWidth() || N != ipImaginary2.getHeight()) {
            JOptionPane.showMessageDialog(this, "Input images must have the same dimensions.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

//        System.out.println("" + M);
//        System.out.println("" + N);
        float[][] real1 = ipReal1.getFloatArray();
        float[][] imaginary1 = ipImaginary1.getFloatArray();

        float[][] real2 = ipReal2.getFloatArray();
        float[][] imaginary2 = ipImaginary2.getFloatArray();

        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                real1[i][j] += real2[i][j];
                imaginary1[i][j] += imaginary2[i][j];
            }
        }

        ImageProcessor ip1 = new FloatProcessor(real1);
        ImagePlus imp1 = new ImagePlus("Real; result of sum", ip1);
        imp1.show();

        ImageProcessor ip2 = new FloatProcessor(imaginary1);
        ImagePlus imp2 = new ImagePlus("Imaginary; result of sum", ip2);
        imp2.show();
    }

    private void subtract() {
        int realIdx1 = realCombo1.getSelectedIndex();
        int imaginaryIdx1 = imaginaryCombo1.getSelectedIndex();

        String realTitle1 = titles1[realIdx1];
        String imaginaryTitle1 = titles1[imaginaryIdx1];

        if (realTitle1.equalsIgnoreCase("<none>") || imaginaryTitle1.equalsIgnoreCase("<none>")) {
            JOptionPane.showMessageDialog(this, "Please both inputs for the first complex field.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int realIdx2 = realCombo2.getSelectedIndex();
        int imaginaryIdx2 = imaginaryCombo2.getSelectedIndex();

        String realTitle2 = titles2[realIdx2];
        String imaginaryTitle2 = titles2[imaginaryIdx2];

        if (realTitle2.equalsIgnoreCase("<none>") || imaginaryTitle2.equalsIgnoreCase("<none>")) {
            JOptionPane.showMessageDialog(this, "Please both inputs for the second complex field.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ImagePlus impReal1 = WindowManager.getImage(windowsId[realIdx1]);
        ImagePlus impImaginary1 = WindowManager.getImage(windowsId[imaginaryIdx1]);

        ImageProcessor ipReal1 = impReal1.getProcessor();
        ImageProcessor ipImaginary1 = impImaginary1.getProcessor();

        int M = ipReal1.getWidth();
        int N = ipReal1.getHeight();

        ImagePlus impReal2 = WindowManager.getImage(windowsId[realIdx2 - 1]);
        ImagePlus impImaginary2 = WindowManager.getImage(windowsId[imaginaryIdx2 - 1]);

        ImageProcessor ipReal2 = impReal2.getProcessor();
        ImageProcessor ipImaginary2 = impImaginary2.getProcessor();

        if (M != ipImaginary1.getWidth() || N != ipImaginary1.getHeight()
                || M != ipReal2.getWidth() || N != ipReal2.getHeight()
                || M != ipImaginary2.getWidth() || N != ipImaginary2.getHeight()) {
            JOptionPane.showMessageDialog(this, "Input images must have the same dimensions.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        float[][] real1 = ipReal1.getFloatArray();
        float[][] imaginary1 = ipImaginary1.getFloatArray();

        float[][] real2 = ipReal2.getFloatArray();
        float[][] imaginary2 = ipImaginary2.getFloatArray();

        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                real1[i][j] -= real2[i][j];
                imaginary1[i][j] -= imaginary2[i][j];
            }
        }

        ImageProcessor ip1 = new FloatProcessor(real1);
        ImagePlus imp1 = new ImagePlus("Real, result of subtraction", ip1);
        imp1.show();

        ImageProcessor ip2 = new FloatProcessor(imaginary1);
        ImagePlus imp2 = new ImagePlus("Imaginary, result of subtraction", ip2);
        imp2.show();
    }

    private void multiply() {
        int realIdx1 = realCombo1.getSelectedIndex();
        int imaginaryIdx1 = imaginaryCombo1.getSelectedIndex();

        String realTitle1 = titles1[realIdx1];
        String imaginaryTitle1 = titles1[imaginaryIdx1];

        if (realTitle1.equalsIgnoreCase("<none>") || imaginaryTitle1.equalsIgnoreCase("<none>")) {
            JOptionPane.showMessageDialog(this, "Please select the inputs for the first complex field.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int realIdx2 = realCombo2.getSelectedIndex();
        int imaginaryIdx2 = imaginaryCombo2.getSelectedIndex();

        String realTitle2 = titles2[realIdx2];
        String imaginaryTitle2 = titles2[imaginaryIdx2];

        boolean hasReal = !realTitle2.equalsIgnoreCase("<none>");
        boolean hasImaginary = !imaginaryTitle2.equalsIgnoreCase("<none>");

        if (!hasReal && !hasImaginary) {
            JOptionPane.showMessageDialog(this, "Please select at least one input for the second complex field.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ImagePlus impReal1 = WindowManager.getImage(windowsId[realIdx1]);
        ImagePlus impImaginary1 = WindowManager.getImage(windowsId[imaginaryIdx1]);

        ImageProcessor ipReal1 = impReal1.getProcessor();
        ImageProcessor ipImaginary1 = impImaginary1.getProcessor();

        int M = ipReal1.getWidth();
        int N = ipReal1.getHeight();

        float[][] field2 = null;

        if (hasReal && hasImaginary) {
            ImagePlus impReal2 = WindowManager.getImage(windowsId[realIdx2 - 1]);
            ImagePlus impImaginary2 = WindowManager.getImage(windowsId[imaginaryIdx2 - 1]);

            ImageProcessor ipReal2 = impReal2.getProcessor();
            ImageProcessor ipImaginary2 = impImaginary2.getProcessor();

            if (M != ipImaginary1.getWidth() || N != ipImaginary1.getHeight()
                    || M != ipReal2.getWidth() || N != ipReal2.getHeight()
                    || M != ipImaginary2.getWidth() || N != ipImaginary2.getHeight()) {
                JOptionPane.showMessageDialog(this, "Input images must have the same dimensions.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            field2 = ArrayUtils.complexAmplitude2(ipReal2.getFloatArray(), ipImaginary2.getFloatArray());
        } else if (hasReal && !hasImaginary) {
            ImagePlus impReal2 = WindowManager.getImage(windowsId[realIdx2 - 1]);

            ImageProcessor ipReal2 = impReal2.getProcessor();

            if (M != ipImaginary1.getWidth() || N != ipImaginary1.getHeight()
                    || M != ipReal2.getWidth() || N != ipReal2.getHeight()) {
                JOptionPane.showMessageDialog(this, "Input images must have the same dimensions.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            field2 = ArrayUtils.complexAmplitude2(ipReal2.getFloatArray(), null);
        } else if (!hasReal && hasImaginary) {
            ImagePlus impImaginary2 = WindowManager.getImage(windowsId[imaginaryIdx2 - 1]);

            ImageProcessor ipImaginary2 = impImaginary2.getProcessor();

            if (M != ipImaginary1.getWidth() || N != ipImaginary1.getHeight()
                    || M != ipImaginary2.getWidth() || N != ipImaginary2.getHeight()) {
                JOptionPane.showMessageDialog(this, "Input images must have the same dimensions.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            field2 = ArrayUtils.complexAmplitude2(null, ipImaginary2.getFloatArray());
        }

        float[][] field1 = ArrayUtils.complexAmplitude2(ipReal1.getFloatArray(), ipImaginary1.getFloatArray());

        ArrayUtils.complexMultiplication2(field1, field2);

        ImageProcessor ip1 = new FloatProcessor(ArrayUtils.real(field1));
        ImagePlus imp1 = new ImagePlus("Real, result of multiplication", ip1);
        imp1.show();

        ImageProcessor ip2 = new FloatProcessor(ArrayUtils.imaginary(field1));
        ImagePlus imp2 = new ImagePlus("Imaginary, result of multiplication", ip2);
        imp2.show();
    }

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

    private boolean setPlaneParameters() {
        try {
            M = Integer.parseInt(planeMField.getText());
            N = Integer.parseInt(planeNField.getText());

            if (M <= 0 || N <= 0) {
                JOptionPane.showMessageDialog(this, "The number of pixels must be a positive integer and different from 0.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please insert a valid number of pixels.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            widthUser = Float.parseFloat(planeWidthField.getText());
            if (widthUser == 0) {
                JOptionPane.showMessageDialog(this, "Width must be different from 0.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please insert a valid width.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            heightUser = Float.parseFloat(planeHeightField.getText());
            if (heightUser == 0) {
                JOptionPane.showMessageDialog(this, "Height must be different from 0.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please insert a valid height.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            lambdaUser = Float.parseFloat(planeLambdaField.getText());
            if (lambdaUser <= 0) {
                JOptionPane.showMessageDialog(this, "Wavelength must be a positive number, different from 0.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please insert a valid wavelength.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            aUser = Float.parseFloat(aField.getText());
            aRadians = Math.toRadians(aUser);
            if (aUser < 0 || aUser > 180) {
                JOptionPane.showMessageDialog(this, "Director angle a must be in the [0,180] range.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please insert a valid direction angle a.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            bUser = Float.parseFloat(bField.getText());
            bRadians = Math.toRadians(bUser);
            if (bUser < 0 || bUser > 180) {
                JOptionPane.showMessageDialog(this, "Director angle b must be in the [0,180] range.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please insert a valid direction angle b.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        alfa = Math.cos(aRadians);
        beta = Math.cos(bRadians);

        gamma = Math.sqrt(1 - (alfa * alfa) - (beta * beta));

        if (Double.isNaN(gamma)) {
            JOptionPane.showMessageDialog(this, "Please insert a valid pair of director angles. Remember that cos^2(a) + cos^2(b) <= 1.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        widthUm = unitsToum(widthUser, inputSizeUnits);
        heightUm = unitsToum(heightUser, inputSizeUnits);
        lambdaUm = unitsToum(lambdaUser, lambdaUnits);
        
        double pi2 = 2 * Math.PI; 
        double k3 = 3 * pi2 / lambdaUm;
        double dx = widthUm / M;
        double dy = heightUm / N;
        
        if (k3 * alfa * dx > pi2){
            double angle = Math.toDegrees(Math.acos(pi2 / (k3 * dx)));
            
            JOptionPane.showMessageDialog(this, "In order to fulfill the sampling theorem, "
                    + " the director angle a >= " + df.format(angle), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (k3 * beta * dy > pi2){
            double angle = Math.toDegrees(Math.acos(pi2 / (k3 * dy)));
            
            JOptionPane.showMessageDialog(this, "In order to fulfill the sampling theorem, "
                    + " the director angle b >= " + df.format(angle), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        wavefrontReal = new float[M][N];
        wavefrontImaginary = new float[M][N];

        return true;
    }

    private void plane() {
        float dx = widthUm / M;
        float dy = heightUm / N;

        int M2 = M / 2;
        int N2 = N / 2;

//        double alfa = Math.cos(aRadians);
//        double beta = Math.cos(bRadians);
//        double gamma = Math.cos(C);
//        double gamma = Math.sqrt(1 - (alfa * alfa) - (beta * beta));
//
//        System.out.println("" + gamma);
//
//        if (Double.isNaN(gamma)) {
//            System.out.println("pailaaaaaaa");
//        }
//
//        System.out.println("" + alfa);
//        System.out.println("" + beta);
//        System.out.println("" + gamma);
//        
        float k = 2 * (float) Math.PI / lambdaUm;
        double c = k * gamma;

        for (int i = 0; i < M; i++) {
            int i2 = i - M2 + 1;
            double a = k * (i2 * dx) * alfa;

            for (int j = 0; j < N; j++) {
                int j2 = j - N2 + 1;
                double b = k * (j2 * dy) * beta;

                double phase = a + b + c;

//                wavefront[i][2 * j] = (float) Math.cos(phase);
//                wavefront[i][2 * j + 1] = (float) Math.sin(phase);
                wavefrontReal[i][j] = (float) Math.cos(phase);
                wavefrontImaginary[i][j] = (float) Math.sin(phase);
            }
        }
//
//        wavefrontReal = ArrayUtils.real(wavefront);
//        wavefrontImaginary = ArrayUtils.imaginary(wavefront);
//
        ImageProcessor ipReal = new FloatProcessor(wavefrontReal);
        ImageProcessor ipImaginary = new FloatProcessor(wavefrontImaginary);

        ImagePlus impReal = new ImagePlus("Real; a: " + aUser + "; b: " + bUser, ipReal);
        ImagePlus impImaginary = new ImagePlus("Imaginary; a: " + aUser + "; b: " + bUser, ipImaginary);

        impReal.show();
        impImaginary.show();
    }

    private boolean setSphericalParameters() {
        try {
            M = Integer.parseInt(sphericalMField.getText());
            N = Integer.parseInt(sphericalNField.getText());

            if (M <= 0 || N <= 0) {
                JOptionPane.showMessageDialog(this, "The number of pixels must be a positive integer and different from 0.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please insert a valid number of pixels.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            widthUser = Float.parseFloat(sphericalWidthField.getText());
            if (widthUser == 0) {
                JOptionPane.showMessageDialog(this, "Width must be different from 0.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please insert a valid width.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            heightUser = Float.parseFloat(sphericalHeightField.getText());
            if (heightUser == 0) {
                JOptionPane.showMessageDialog(this, "Height must be different from 0.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please insert a valid height.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            lambdaUser = Float.parseFloat(sphericalLambdaField.getText());
            if (lambdaUser <= 0) {
                JOptionPane.showMessageDialog(this, "Wavelength must be a positive number, different from 0.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please insert a valid wavelength.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            curvRadiusUser = Float.parseFloat(sphericalCurvRadiusField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please insert a valid distance.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        widthUm = unitsToum(widthUser, inputSizeUnits);
        heightUm = unitsToum(heightUser, inputSizeUnits);
        lambdaUm = unitsToum(lambdaUser, lambdaUnits);
        curvRadiusUm = unitsToum(curvRadiusUser, curvRadiusUnits);

//        wavefront = new float[M][2 * N];
        wavefrontReal = new float[M][N];
        wavefrontImaginary = new float[M][N];

        return true;
    }

    private void spherical() {
        float dx = widthUm / M;
        float dy = heightUm / N;

        int M2 = M / 2;
        int N2 = N / 2;

        float k = 2 * (float) Math.PI / lambdaUm;
        float f = k / (2 * curvRadiusUm);
        float z2 = curvRadiusUm * curvRadiusUm;

        for (int i = 0; i < M; i++) {
            int i2 = i - M2 + 1;
            float a = (dx * dx * i2 * i2);

            for (int j = 0; j < N; j++) {
                int j2 = j - N2 + 1;
                float b = (dy * dy * j2 * j2);
                float phase = f * (a + b);

                float r = (float) Math.sqrt(z2 + a + b);

//                wavefront[i][2 * j] = (float) Math.cos(phase) / r;
//                wavefront[i][2 * j + 1] = (float) Math.sin(phase) / r;
                wavefrontReal[i][j] = (float) Math.cos(phase) / r;
                wavefrontImaginary[i][j] = (float) Math.sin(phase) / r;
            }
        }

//        wavefrontReal = ArrayUtils.real(wavefront);
//        wavefrontImaginary = ArrayUtils.imaginary(wavefront);
        ImageProcessor ipReal = new FloatProcessor(wavefrontReal);
        ImageProcessor ipImaginary = new FloatProcessor(wavefrontImaginary);

        ImagePlus impReal = new ImagePlus("Real; Curv. radius: " + curvRadiusUser + " " + curvRadiusUnits, ipReal);
        ImagePlus impImaginary = new ImagePlus("Imaginary; Curv. radius: " + curvRadiusUser + " " + curvRadiusUnits, ipImaginary);

        impReal.show();
        impImaginary.show();
    }

    @Override
    public void imageClosed(ImagePlus ip) {
        updateCombos();
    }

    @Override
    public void imageOpened(ImagePlus ip) {
        updateCombos();
    }

    @Override
    public void imageUpdated(ImagePlus ip) {
        updateCombos();
    }

    private void updateCombos() {
        int realIdx1 = realCombo1.getSelectedIndex();
        int imaginaryIdx1 = imaginaryCombo1.getSelectedIndex();
        int realIdx2 = realCombo2.getSelectedIndex();
        int imaginaryIdx2 = imaginaryCombo2.getSelectedIndex();

        getOpenedImages();
        realCombo1.setModel(new DefaultComboBoxModel<String>(titles1));
        realCombo1.setSelectedIndex((realIdx1 >= titles1.length)
                ? titles1.length - 1 : realIdx1);

        imaginaryCombo1.setModel(new DefaultComboBoxModel<String>(titles1));
        imaginaryCombo1.setSelectedIndex((imaginaryIdx1 >= titles1.length)
                ? titles1.length - 1 : imaginaryIdx1);

        realCombo2.setModel(new DefaultComboBoxModel<String>(titles2));
        realCombo2.setSelectedIndex((realIdx2 >= titles2.length)
                ? titles2.length - 1 : realIdx2);

        imaginaryCombo2.setModel(new DefaultComboBoxModel<String>(titles2));
        imaginaryCombo2.setSelectedIndex((imaginaryIdx2 >= titles2.length)
                ? titles2.length - 1 : imaginaryIdx2);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        mathPanel = new javax.swing.JPanel();
        realCombo1 = new javax.swing.JComboBox();
        imaginaryCombo1 = new javax.swing.JComboBox();
        realCombo2 = new javax.swing.JComboBox();
        imaginaryCombo2 = new javax.swing.JComboBox();
        operationCombo = new javax.swing.JComboBox();
        okMathBtn = new javax.swing.JButton();
        operationLabel = new javax.swing.JLabel();
        imaginaryLabel2 = new javax.swing.JLabel();
        realLabel2 = new javax.swing.JLabel();
        imaginaryLabel1 = new javax.swing.JLabel();
        realLabel1 = new javax.swing.JLabel();
        planePanel = new javax.swing.JPanel();
        sizePlaneLabel = new javax.swing.JLabel();
        planeMField = new javax.swing.JTextField();
        planeNField = new javax.swing.JTextField();
        xPlaneLabel = new javax.swing.JLabel();
        planeWidthField = new javax.swing.JTextField();
        planeHeightField = new javax.swing.JTextField();
        planeLambdaField = new javax.swing.JTextField();
        widthPlaneLabel = new javax.swing.JLabel();
        heightPlaneLabel = new javax.swing.JLabel();
        lambdaPlaneLabel = new javax.swing.JLabel();
        directionLabel = new javax.swing.JLabel();
        aLabel = new javax.swing.JLabel();
        aField = new javax.swing.JTextField();
        bField = new javax.swing.JTextField();
        bLabel = new javax.swing.JLabel();
        okPlaneBtn = new javax.swing.JButton();
        sphericalPanel = new javax.swing.JPanel();
        sizeSphericalLabel = new javax.swing.JLabel();
        sphericalMField = new javax.swing.JTextField();
        sphericalNField = new javax.swing.JTextField();
        xSphericalLabel = new javax.swing.JLabel();
        sphericalWidthField = new javax.swing.JTextField();
        sphericalHeightField = new javax.swing.JTextField();
        sphericalLambdaField = new javax.swing.JTextField();
        widthSphericalLabel = new javax.swing.JLabel();
        heightSphericalLabel = new javax.swing.JLabel();
        lambdaSphericalLabel = new javax.swing.JLabel();
        okSphericalBtn = new javax.swing.JButton();
        curvRadiusSphericalLabel = new javax.swing.JLabel();
        sphericalCurvRadiusField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(TITLE);
        setBounds(new java.awt.Rectangle(locX, locY, 0, 0));
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png")));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        mathPanel.setMaximumSize(new java.awt.Dimension(260, 219));
        mathPanel.setMinimumSize(new java.awt.Dimension(260, 219));
        mathPanel.setName(""); // NOI18N
        mathPanel.setPreferredSize(new java.awt.Dimension(260, 219));

        realCombo1.setModel(new DefaultComboBoxModel<String>(titles1));
        realCombo1.setMaximumSize(new java.awt.Dimension(134, 20));
        realCombo1.setMinimumSize(new java.awt.Dimension(134, 20));
        realCombo1.setPreferredSize(new java.awt.Dimension(134, 20));

        imaginaryCombo1.setModel(new DefaultComboBoxModel<String>(titles1));
        imaginaryCombo1.setMaximumSize(new java.awt.Dimension(134, 20));
        imaginaryCombo1.setMinimumSize(new java.awt.Dimension(134, 20));
        imaginaryCombo1.setPreferredSize(new java.awt.Dimension(134, 20));

        realCombo2.setModel(new DefaultComboBoxModel<String>(titles2));
        realCombo2.setEnabled(false);
        realCombo2.setMaximumSize(new java.awt.Dimension(134, 20));
        realCombo2.setMinimumSize(new java.awt.Dimension(134, 20));
        realCombo2.setPreferredSize(new java.awt.Dimension(134, 20));

        imaginaryCombo2.setModel(new DefaultComboBoxModel<String>(titles2));
        imaginaryCombo2.setEnabled(false);
        imaginaryCombo2.setMaximumSize(new java.awt.Dimension(134, 20));
        imaginaryCombo2.setMinimumSize(new java.awt.Dimension(134, 20));
        imaginaryCombo2.setPreferredSize(new java.awt.Dimension(134, 20));

        operationCombo.setModel(new DefaultComboBoxModel<String>(OPERATIONS));
        operationCombo.setMaximumSize(new java.awt.Dimension(134, 20));
        operationCombo.setMinimumSize(new java.awt.Dimension(134, 20));
        operationCombo.setPreferredSize(new java.awt.Dimension(134, 20));
        operationCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                operationComboActionPerformed(evt);
            }
        });

        okMathBtn.setText("Ok");
        okMathBtn.setMaximumSize(new java.awt.Dimension(90, 23));
        okMathBtn.setMinimumSize(new java.awt.Dimension(90, 23));
        okMathBtn.setPreferredSize(new java.awt.Dimension(90, 23));
        okMathBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okMathBtnActionPerformed(evt);
            }
        });

        operationLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        operationLabel.setText("Operation:");
        operationLabel.setMaximumSize(new java.awt.Dimension(88, 14));
        operationLabel.setMinimumSize(new java.awt.Dimension(88, 14));
        operationLabel.setPreferredSize(new java.awt.Dimension(88, 14));

        imaginaryLabel2.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        imaginaryLabel2.setText("Imaginary input 2:");

        realLabel2.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        realLabel2.setText("Real input 2:");
        realLabel2.setMaximumSize(new java.awt.Dimension(88, 14));
        realLabel2.setMinimumSize(new java.awt.Dimension(88, 14));
        realLabel2.setPreferredSize(new java.awt.Dimension(88, 14));

        imaginaryLabel1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        imaginaryLabel1.setText("Imaginary input 1:");

        realLabel1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        realLabel1.setText("Real input 1:");
        realLabel1.setMaximumSize(new java.awt.Dimension(88, 14));
        realLabel1.setMinimumSize(new java.awt.Dimension(88, 14));
        realLabel1.setPreferredSize(new java.awt.Dimension(88, 14));

        javax.swing.GroupLayout mathPanelLayout = new javax.swing.GroupLayout(mathPanel);
        mathPanel.setLayout(mathPanelLayout);
        mathPanelLayout.setHorizontalGroup(
            mathPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mathPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mathPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mathPanelLayout.createSequentialGroup()
                        .addComponent(operationLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(operationCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(mathPanelLayout.createSequentialGroup()
                        .addGroup(mathPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(realLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(imaginaryLabel1))
                        .addGroup(mathPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(mathPanelLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(imaginaryCombo1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(mathPanelLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(realCombo1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mathPanelLayout.createSequentialGroup()
                        .addGap(149, 149, 149)
                        .addComponent(okMathBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(mathPanelLayout.createSequentialGroup()
                        .addGroup(mathPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(imaginaryLabel2)
                            .addComponent(realLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(mathPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(imaginaryCombo2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(realCombo2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        mathPanelLayout.setVerticalGroup(
            mathPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mathPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mathPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(realCombo1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(realLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(mathPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(imaginaryCombo1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(imaginaryLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(mathPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(realCombo2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(realLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(mathPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(imaginaryCombo2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(imaginaryLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(mathPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(operationCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(operationLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addComponent(okMathBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Complex Math", null, mathPanel, "Camplex math utilities.");

        planePanel.setMaximumSize(new java.awt.Dimension(260, 219));
        planePanel.setMinimumSize(new java.awt.Dimension(260, 219));

        sizePlaneLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        sizePlaneLabel.setText("Size [pixels]:");
        sizePlaneLabel.setMaximumSize(new java.awt.Dimension(90, 14));
        sizePlaneLabel.setMinimumSize(new java.awt.Dimension(90, 14));
        sizePlaneLabel.setPreferredSize(new java.awt.Dimension(90, 14));

        planeMField.setText(planeMString);
        planeMField.setMaximumSize(new java.awt.Dimension(59, 20));
        planeMField.setMinimumSize(new java.awt.Dimension(59, 20));
        planeMField.setName(""); // NOI18N
        planeMField.setPreferredSize(new java.awt.Dimension(59, 20));
        planeMField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldFocusGained(evt);
            }
        });

        planeNField.setText(planeNString);
        planeNField.setMaximumSize(new java.awt.Dimension(59, 20));
        planeNField.setMinimumSize(new java.awt.Dimension(59, 20));
        planeNField.setPreferredSize(new java.awt.Dimension(59, 20));
        planeNField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldFocusGained(evt);
            }
        });

        xPlaneLabel.setText("x");

        planeWidthField.setText(planeWidthString);
        planeWidthField.setMaximumSize(new java.awt.Dimension(132, 20));
        planeWidthField.setMinimumSize(new java.awt.Dimension(132, 20));
        planeWidthField.setPreferredSize(new java.awt.Dimension(132, 20));
        planeWidthField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldFocusGained(evt);
            }
        });

        planeHeightField.setText(planeHeightString);
        planeHeightField.setMaximumSize(new java.awt.Dimension(132, 20));
        planeHeightField.setMinimumSize(new java.awt.Dimension(132, 20));
        planeHeightField.setPreferredSize(new java.awt.Dimension(132, 20));
        planeHeightField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldFocusGained(evt);
            }
        });

        planeLambdaField.setText(planeLambdaString);
        planeLambdaField.setMaximumSize(new java.awt.Dimension(132, 20));
        planeLambdaField.setMinimumSize(new java.awt.Dimension(132, 20));
        planeLambdaField.setPreferredSize(new java.awt.Dimension(132, 20));
        planeLambdaField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldFocusGained(evt);
            }
        });

        widthPlaneLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        widthPlaneLabel.setText("Width [" + inputSizeUnits + "]:");
        widthPlaneLabel.setMaximumSize(new java.awt.Dimension(90, 14));
        widthPlaneLabel.setMinimumSize(new java.awt.Dimension(90, 14));
        widthPlaneLabel.setPreferredSize(new java.awt.Dimension(90, 14));

        heightPlaneLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        heightPlaneLabel.setText("Height [" + inputSizeUnits + "]:");
        heightPlaneLabel.setMaximumSize(new java.awt.Dimension(90, 14));
        heightPlaneLabel.setMinimumSize(new java.awt.Dimension(90, 14));
        heightPlaneLabel.setPreferredSize(new java.awt.Dimension(90, 14));

        lambdaPlaneLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lambdaPlaneLabel.setText("Wavelength [" + lambdaUnits + "]:");
        lambdaPlaneLabel.setMaximumSize(new java.awt.Dimension(90, 14));
        lambdaPlaneLabel.setMinimumSize(new java.awt.Dimension(90, 14));
        lambdaPlaneLabel.setPreferredSize(new java.awt.Dimension(90, 14));

        directionLabel.setText("Direction angles [degrees]:");

        aLabel.setText("a:");

        aField.setText(planeAString);
        aField.setMaximumSize(new java.awt.Dimension(95, 20));
        aField.setMinimumSize(new java.awt.Dimension(95, 20));
        aField.setPreferredSize(new java.awt.Dimension(95, 20));
        aField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldFocusGained(evt);
            }
        });

        bField.setText(planeBString);
        bField.setMaximumSize(new java.awt.Dimension(95, 20));
        bField.setMinimumSize(new java.awt.Dimension(95, 20));
        bField.setPreferredSize(new java.awt.Dimension(95, 20));
        bField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldFocusGained(evt);
            }
        });

        bLabel.setText("b:");

        okPlaneBtn.setText("Ok");
        okPlaneBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okPlaneBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout planePanelLayout = new javax.swing.GroupLayout(planePanel);
        planePanel.setLayout(planePanelLayout);
        planePanelLayout.setHorizontalGroup(
            planePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(planePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(planePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(planePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(planePanelLayout.createSequentialGroup()
                            .addComponent(aLabel)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(aField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(bLabel)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(bField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(directionLabel, javax.swing.GroupLayout.Alignment.LEADING))
                    .addGroup(planePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(okPlaneBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(planePanelLayout.createSequentialGroup()
                            .addGroup(planePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(lambdaPlaneLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(heightPlaneLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(widthPlaneLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(sizePlaneLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(18, 18, 18)
                            .addGroup(planePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(planeHeightField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(planeLambdaField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(planeWidthField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(planePanelLayout.createSequentialGroup()
                                    .addComponent(planeMField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(xPlaneLabel)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(planeNField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap())
        );
        planePanelLayout.setVerticalGroup(
            planePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(planePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(planePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sizePlaneLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(planeMField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(planeNField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(xPlaneLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(planePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(planeWidthField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(widthPlaneLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(planePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(planeHeightField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(heightPlaneLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(planePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(planeLambdaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lambdaPlaneLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(directionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(planePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(aLabel)
                    .addComponent(bLabel)
                    .addComponent(bField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(aField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(okPlaneBtn)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Plane Waves", null, planePanel, "Plane wavefront generation.");

        sphericalPanel.setMaximumSize(new java.awt.Dimension(260, 219));
        sphericalPanel.setMinimumSize(new java.awt.Dimension(260, 219));
        sphericalPanel.setPreferredSize(new java.awt.Dimension(260, 219));

        sizeSphericalLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        sizeSphericalLabel.setText("Size [pixels]:");
        sizeSphericalLabel.setMaximumSize(new java.awt.Dimension(90, 14));
        sizeSphericalLabel.setMinimumSize(new java.awt.Dimension(90, 14));
        sizeSphericalLabel.setPreferredSize(new java.awt.Dimension(90, 14));

        sphericalMField.setText(sphericalMString);
        sphericalMField.setMaximumSize(new java.awt.Dimension(72, 20));
        sphericalMField.setMinimumSize(new java.awt.Dimension(72, 20));
        sphericalMField.setPreferredSize(new java.awt.Dimension(59, 20));
        sphericalMField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldFocusGained(evt);
            }
        });

        sphericalNField.setText(sphericalNString);
        sphericalNField.setMaximumSize(new java.awt.Dimension(59, 20));
        sphericalNField.setMinimumSize(new java.awt.Dimension(59, 20));
        sphericalNField.setPreferredSize(new java.awt.Dimension(59, 20));
        sphericalNField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldFocusGained(evt);
            }
        });

        xSphericalLabel.setText("x");

        sphericalWidthField.setText(sphericalWidthString);
        sphericalWidthField.setMaximumSize(new java.awt.Dimension(132, 20));
        sphericalWidthField.setMinimumSize(new java.awt.Dimension(132, 20));
        sphericalWidthField.setPreferredSize(new java.awt.Dimension(132, 20));
        sphericalWidthField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldFocusGained(evt);
            }
        });

        sphericalHeightField.setText(sphericalHeightString);
        sphericalHeightField.setMaximumSize(new java.awt.Dimension(132, 20));
        sphericalHeightField.setMinimumSize(new java.awt.Dimension(132, 20));
        sphericalHeightField.setPreferredSize(new java.awt.Dimension(132, 20));
        sphericalHeightField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldFocusGained(evt);
            }
        });

        sphericalLambdaField.setText(sphericalLambdaString);
        sphericalLambdaField.setMaximumSize(new java.awt.Dimension(132, 20));
        sphericalLambdaField.setMinimumSize(new java.awt.Dimension(132, 20));
        sphericalLambdaField.setPreferredSize(new java.awt.Dimension(132, 20));
        sphericalLambdaField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldFocusGained(evt);
            }
        });

        widthSphericalLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        widthSphericalLabel.setText("Width [" + inputSizeUnits + "]:");
        widthSphericalLabel.setMaximumSize(new java.awt.Dimension(90, 14));
        widthSphericalLabel.setMinimumSize(new java.awt.Dimension(90, 14));
        widthSphericalLabel.setPreferredSize(new java.awt.Dimension(90, 14));

        heightSphericalLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        heightSphericalLabel.setText("Height [" + inputSizeUnits + "]:");
        heightSphericalLabel.setMaximumSize(new java.awt.Dimension(90, 14));
        heightSphericalLabel.setMinimumSize(new java.awt.Dimension(90, 14));
        heightSphericalLabel.setPreferredSize(new java.awt.Dimension(90, 14));

        lambdaSphericalLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lambdaSphericalLabel.setText("Wavelength [" + lambdaUnits + "]:");
        lambdaSphericalLabel.setMaximumSize(new java.awt.Dimension(90, 14));
        lambdaSphericalLabel.setMinimumSize(new java.awt.Dimension(90, 14));
        lambdaSphericalLabel.setPreferredSize(new java.awt.Dimension(90, 14));

        okSphericalBtn.setText("Ok");
        okSphericalBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okSphericalBtnActionPerformed(evt);
            }
        });

        curvRadiusSphericalLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        curvRadiusSphericalLabel.setText("Curv. radius [" + curvRadiusUnits + "]:");
        curvRadiusSphericalLabel.setMaximumSize(new java.awt.Dimension(90, 14));
        curvRadiusSphericalLabel.setMinimumSize(new java.awt.Dimension(90, 14));
        curvRadiusSphericalLabel.setPreferredSize(new java.awt.Dimension(90, 14));

        sphericalCurvRadiusField.setText(sphericalCurvRadiusString);
        sphericalCurvRadiusField.setMaximumSize(new java.awt.Dimension(132, 20));
        sphericalCurvRadiusField.setMinimumSize(new java.awt.Dimension(132, 20));
        sphericalCurvRadiusField.setPreferredSize(new java.awt.Dimension(132, 20));
        sphericalCurvRadiusField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldFocusGained(evt);
            }
        });

        javax.swing.GroupLayout sphericalPanelLayout = new javax.swing.GroupLayout(sphericalPanel);
        sphericalPanel.setLayout(sphericalPanelLayout);
        sphericalPanelLayout.setHorizontalGroup(
            sphericalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sphericalPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sphericalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(sphericalPanelLayout.createSequentialGroup()
                        .addGroup(sphericalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lambdaSphericalLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(heightSphericalLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(widthSphericalLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sizeSphericalLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(curvRadiusSphericalLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(sphericalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(sphericalHeightField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sphericalLambdaField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sphericalWidthField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sphericalCurvRadiusField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(sphericalPanelLayout.createSequentialGroup()
                                .addComponent(sphericalMField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(xSphericalLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sphericalNField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(10, 10, 10))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sphericalPanelLayout.createSequentialGroup()
                        .addComponent(okSphericalBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        sphericalPanelLayout.setVerticalGroup(
            sphericalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sphericalPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sphericalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sizeSphericalLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sphericalMField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sphericalNField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(xSphericalLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(sphericalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sphericalWidthField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(widthSphericalLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(sphericalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sphericalHeightField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(heightSphericalLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(sphericalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sphericalLambdaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lambdaSphericalLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(sphericalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sphericalCurvRadiusField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(curvRadiusSphericalLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(50, 50, 50)
                .addComponent(okSphericalBtn)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Spherical Waves", null, sphericalPanel, "Spherical wavefront generation.");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void operationComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_operationComboActionPerformed
        int idx = operationCombo.getSelectedIndex();
        boolean enable = !((idx == 0) || (idx == 1) || (idx == 2));

        realCombo2.setEnabled(enable);
        imaginaryCombo2.setEnabled(enable);
    }//GEN-LAST:event_operationComboActionPerformed

    private void okMathBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okMathBtnActionPerformed
        int idx = operationCombo.getSelectedIndex();

        switch (idx) {
            case 0:
                phase();
                break;
            case 1:
                amplitude();
                break;
            case 2:
                intensity();
                break;
            case 3:
                add();
                break;
            case 4:
                subtract();
                break;
            case 5:
                multiply();
                break;
        }
    }//GEN-LAST:event_okMathBtnActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        savePrefs();
        ImagePlus.removeImageListener(this);
        setVisible(false);
        dispose();
    }//GEN-LAST:event_formWindowClosing

    private void okSphericalBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okSphericalBtnActionPerformed
        boolean success = setSphericalParameters();
        if (!success) {
            return;
        }

        spherical();
    }//GEN-LAST:event_okSphericalBtnActionPerformed

    private void okPlaneBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okPlaneBtnActionPerformed
        boolean success = setPlaneParameters();
        if (!success) {
            return;
        }
        plane();
    }//GEN-LAST:event_okPlaneBtnActionPerformed

    private void textFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textFieldFocusGained
        JTextField field = (JTextField) evt.getComponent();
        field.selectAll();
    }//GEN-LAST:event_textFieldFocusGained

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField aField;
    private javax.swing.JLabel aLabel;
    private javax.swing.JTextField bField;
    private javax.swing.JLabel bLabel;
    private javax.swing.JLabel curvRadiusSphericalLabel;
    private javax.swing.JLabel directionLabel;
    private javax.swing.JLabel heightPlaneLabel;
    private javax.swing.JLabel heightSphericalLabel;
    private javax.swing.JComboBox imaginaryCombo1;
    private javax.swing.JComboBox imaginaryCombo2;
    private javax.swing.JLabel imaginaryLabel1;
    private javax.swing.JLabel imaginaryLabel2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lambdaPlaneLabel;
    private javax.swing.JLabel lambdaSphericalLabel;
    private javax.swing.JPanel mathPanel;
    private javax.swing.JButton okMathBtn;
    private javax.swing.JButton okPlaneBtn;
    private javax.swing.JButton okSphericalBtn;
    private javax.swing.JComboBox operationCombo;
    private javax.swing.JLabel operationLabel;
    private javax.swing.JTextField planeHeightField;
    private javax.swing.JTextField planeLambdaField;
    private javax.swing.JTextField planeMField;
    private javax.swing.JTextField planeNField;
    private javax.swing.JPanel planePanel;
    private javax.swing.JTextField planeWidthField;
    private javax.swing.JComboBox realCombo1;
    private javax.swing.JComboBox realCombo2;
    private javax.swing.JLabel realLabel1;
    private javax.swing.JLabel realLabel2;
    private javax.swing.JLabel sizePlaneLabel;
    private javax.swing.JLabel sizeSphericalLabel;
    private javax.swing.JTextField sphericalCurvRadiusField;
    private javax.swing.JTextField sphericalHeightField;
    private javax.swing.JTextField sphericalLambdaField;
    private javax.swing.JTextField sphericalMField;
    private javax.swing.JTextField sphericalNField;
    private javax.swing.JPanel sphericalPanel;
    private javax.swing.JTextField sphericalWidthField;
    private javax.swing.JLabel widthPlaneLabel;
    private javax.swing.JLabel widthSphericalLabel;
    private javax.swing.JLabel xPlaneLabel;
    private javax.swing.JLabel xSphericalLabel;
    // End of variables declaration//GEN-END:variables
}
