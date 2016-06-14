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
import java.util.prefs.Preferences;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
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

    private int locX;
    private int locY;

    private int[] windowsId;
    private String[] titles1;
    private String[] titles2;

    private final Preferences pref;

    /**
     * Creates new form UtilitiesFrame
     */
    public UtilitiesFrame() {
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
        pref.putInt(UTILITIES_FRAME_LOC_X, getLocation().x);
        pref.putInt(UTILITIES_FRAME_LOC_Y, getLocation().y);
    }

    private void loadPrefs() {
        locX = pref.getInt(MAIN_FRAME_LOC_X, 300);
        locY = pref.getInt(MAIN_FRAME_LOC_Y, 300);
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
        
        System.out.println("" +M);
        System.out.println("" +N);

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
        ImagePlus imp1 = new ImagePlus("Real, result of sum", ip1);
        imp1.show();

        ImageProcessor ip2 = new FloatProcessor(imaginary1);
        ImagePlus imp2 = new ImagePlus("Imaginary, result of sum", ip2);
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

        okBtn = new javax.swing.JButton();
        operationCombo = new javax.swing.JComboBox();
        imaginaryCombo2 = new javax.swing.JComboBox();
        realCombo2 = new javax.swing.JComboBox();
        imaginaryCombo1 = new javax.swing.JComboBox();
        realCombo1 = new javax.swing.JComboBox();
        realLabel1 = new javax.swing.JLabel();
        imaginaryLabel1 = new javax.swing.JLabel();
        realLabel2 = new javax.swing.JLabel();
        imaginaryLabel2 = new javax.swing.JLabel();
        operationLabel = new javax.swing.JLabel();

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

        okBtn.setText("Ok");
        okBtn.setMaximumSize(new java.awt.Dimension(90, 23));
        okBtn.setMinimumSize(new java.awt.Dimension(90, 23));
        okBtn.setPreferredSize(new java.awt.Dimension(90, 23));
        okBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okBtnActionPerformed(evt);
            }
        });

        operationCombo.setModel(new DefaultComboBoxModel<String>(OPERATIONS));
        operationCombo.setMaximumSize(new java.awt.Dimension(120, 20));
        operationCombo.setMinimumSize(new java.awt.Dimension(120, 20));
        operationCombo.setPreferredSize(new java.awt.Dimension(120, 20));
        operationCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                operationComboActionPerformed(evt);
            }
        });

        imaginaryCombo2.setModel(new DefaultComboBoxModel<String>(titles2));
        imaginaryCombo2.setEnabled(false);
        imaginaryCombo2.setMaximumSize(new java.awt.Dimension(120, 20));
        imaginaryCombo2.setMinimumSize(new java.awt.Dimension(120, 20));
        imaginaryCombo2.setPreferredSize(new java.awt.Dimension(120, 20));

        realCombo2.setModel(new DefaultComboBoxModel<String>(titles2));
        realCombo2.setEnabled(false);
        realCombo2.setMaximumSize(new java.awt.Dimension(120, 20));
        realCombo2.setMinimumSize(new java.awt.Dimension(120, 20));
        realCombo2.setPreferredSize(new java.awt.Dimension(120, 20));

        imaginaryCombo1.setModel(new DefaultComboBoxModel<String>(titles1));
        imaginaryCombo1.setMaximumSize(new java.awt.Dimension(120, 20));
        imaginaryCombo1.setMinimumSize(new java.awt.Dimension(120, 20));
        imaginaryCombo1.setPreferredSize(new java.awt.Dimension(120, 20));

        realCombo1.setModel(new DefaultComboBoxModel<String>(titles1));
        realCombo1.setMaximumSize(new java.awt.Dimension(120, 20));
        realCombo1.setMinimumSize(new java.awt.Dimension(120, 20));
        realCombo1.setPreferredSize(new java.awt.Dimension(120, 20));

        realLabel1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        realLabel1.setText("Real input 1:");
        realLabel1.setMaximumSize(new java.awt.Dimension(88, 14));
        realLabel1.setMinimumSize(new java.awt.Dimension(88, 14));
        realLabel1.setPreferredSize(new java.awt.Dimension(88, 14));

        imaginaryLabel1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        imaginaryLabel1.setText("Imaginary input 1:");

        realLabel2.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        realLabel2.setText("Real input 2:");
        realLabel2.setMaximumSize(new java.awt.Dimension(88, 14));
        realLabel2.setMinimumSize(new java.awt.Dimension(88, 14));
        realLabel2.setPreferredSize(new java.awt.Dimension(88, 14));

        imaginaryLabel2.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        imaginaryLabel2.setText("Imaginary input 2:");

        operationLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        operationLabel.setText("Operation:");
        operationLabel.setMaximumSize(new java.awt.Dimension(88, 14));
        operationLabel.setMinimumSize(new java.awt.Dimension(88, 14));
        operationLabel.setPreferredSize(new java.awt.Dimension(88, 14));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(realLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(imaginaryLabel1))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(imaginaryCombo1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(realCombo1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(realLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(realCombo2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(imaginaryLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(imaginaryCombo2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(okBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(operationLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(operationCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(realCombo1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(realLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(imaginaryCombo1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(imaginaryLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(realCombo2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(realLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(imaginaryCombo2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(imaginaryLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(operationCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(operationLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(okBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

    private void okBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okBtnActionPerformed
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
    }//GEN-LAST:event_okBtnActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        savePrefs();
        ImagePlus.removeImageListener(this);
        setVisible(false);
        dispose();
    }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox imaginaryCombo1;
    private javax.swing.JComboBox imaginaryCombo2;
    private javax.swing.JLabel imaginaryLabel1;
    private javax.swing.JLabel imaginaryLabel2;
    private javax.swing.JButton okBtn;
    private javax.swing.JComboBox operationCombo;
    private javax.swing.JLabel operationLabel;
    private javax.swing.JComboBox realCombo1;
    private javax.swing.JComboBox realCombo2;
    private javax.swing.JLabel realLabel1;
    private javax.swing.JLabel realLabel2;
    // End of variables declaration//GEN-END:variables
}
