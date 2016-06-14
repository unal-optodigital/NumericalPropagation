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

import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Calibration;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import java.awt.Cursor;
import java.awt.Toolkit;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import unal.od.jdiffraction.cpu.utils.ArrayUtils;

/**
 *
 * @author Raul Castañeda (racastanedaq@unal.edu.co)
 * @author Pablo Piedrahita-Quintero (jppiedrahitaq@unal.edu.co)
 * @author Jorge Garcia-Sucerquia (jigarcia@unal.edu.co)
 */
public class BatchFrame extends javax.swing.JFrame implements PreferencesKeys {

    private static final String TITLE = "Batch Propagation";

    private float from;
    private float to;
    private float step;
    private int planes;

    // <editor-fold defaultstate="collapsed" desc="Prefs variables">
    private String fromString;
    private String toString;
    private String stepString;
    private String planesString;

    private String zUnits;
    private boolean isStep;
    private int maxPlanes;

    private boolean filterEnabled;

    private boolean isPlane;
    private float curvRadius;

    private boolean phaseEnabled;
    private boolean amplitudeEnabled;
    private boolean intensityEnabled;
    private boolean realEnabled;
    private boolean imaginaryEnabled;

    private boolean amplitudeLogSelected;
    private boolean intensityLogSelected;

    private boolean phaseByteSelected;
    private boolean amplitudeByteSelected;
    private boolean intensityByteSelected;
//    private boolean realByteSelected;
//    private boolean imaginaryByteSelected;
    // </editor-fold>

    private final Preferences pref;
    private final Data data;

    private final int idx;

    private final MainFrame parent;

    /**
     * Creates new form BatchFrame1
     *
     * @param parent
     * @param idx
     */
    public BatchFrame(MainFrame parent, int idx) {
        pref = Preferences.userNodeForPackage(getClass());
        data = Data.getInstance();

        this.idx = idx;
        this.parent = parent;

        loadPrefs();

        setLocationRelativeTo(parent);
        initComponents();
    }

    private void savePrefs() {
        pref.put(BATCH_START, fromField.getText());
        pref.put(BATCH_END, toField.getText());

        if (isStep) {
            pref.put(BATCH_STEP, incrementsField.getText());
        } else {
            pref.put(BATCH_PLANES, incrementsField.getText());
        }
    }

    private void loadPrefs() {
        fromString = pref.get(BATCH_START, "");
        toString = pref.get(BATCH_END, "");
        stepString = pref.get(BATCH_STEP, "");
        planesString = pref.get(BATCH_PLANES, "");

        zUnits = pref.get(DISTANCE_UNITS, "m");
        isStep = pref.getBoolean(IS_STEP, true);
        maxPlanes = pref.getInt(MAX_PLANES, 10);

        filterEnabled = pref.getBoolean(IS_FILTER_ENABLED, true);

        isPlane = pref.getBoolean(IS_PLANE, true);
        curvRadius = pref.getFloat(CURV_RADIUS, 1E6f);

        phaseEnabled = pref.getBoolean(PHASE_CHECKED, false);
        amplitudeEnabled = pref.getBoolean(AMPLITUDE_CHECKED, false);
        intensityEnabled = pref.getBoolean(INTENSITY_CHECKED, false);
        realEnabled = pref.getBoolean(REAL_CHECKED, false);
        imaginaryEnabled = pref.getBoolean(IMAGINARY_CHECKED, false);

        amplitudeLogSelected = pref.getBoolean(AMPLITUDE_LOG, true);
        intensityLogSelected = pref.getBoolean(INTENSITY_LOG, true);

        phaseByteSelected = pref.getBoolean(PHASE_8_BIT, true);
        amplitudeByteSelected = pref.getBoolean(AMPLITUDE_8_BIT, true);
        intensityByteSelected = pref.getBoolean(INTENSITY_8_BIT, true);
//        realByteSelected = pref.getBoolean(REAL_8_BIT, true);
//        imaginaryByteSelected = pref.getBoolean(IMAGINARY_8_BIT, true);
    }

    private void fixUnits() {
        if (zUnits.equals("nm")) {
            from *= 1E-3f;
            to *= 1E-3f;
            step *= 1E-3f;
        } else if (zUnits.equals("mm")) {
            from *= 1E3f;
            to *= 1E3f;
            step *= 1E3f;
        } else if (zUnits.equals("cm")) {
            from *= 1E4f;
            to *= 1E4f;
            step *= 1E4f;
        } else if (zUnits.equals("m")) {
            from *= 1E6f;
            to *= 1E6f;
            step *= 1E6f;
        }
    }

    private float umToUnits(float n) {

        if (zUnits.equals("nm")) {
            return n * 1E3f;
        } else if (zUnits.equals("mm")) {
            return n * 1E-3f;
        } else if (zUnits.equals("cm")) {
            return n * 1E-4f;
        } else if (zUnits.equals("m")) {
            return n * 1E-6f;
        }

        return n;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        inputPanel = new javax.swing.JPanel();
        toLabel = new javax.swing.JLabel();
        fromLabel = new javax.swing.JLabel();
        fromField = new javax.swing.JTextField();
        toField = new javax.swing.JTextField();
        incrementsField = new javax.swing.JTextField();
        incrementLabel = new javax.swing.JLabel();
        btnsPanel = new javax.swing.JPanel();
        okBtn = new javax.swing.JButton();
        cancelBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(TITLE);
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png")));
        setResizable(false);

        toLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        toLabel.setText("To [" + zUnits +"]:");
        toLabel.setMaximumSize(new java.awt.Dimension(55, 14));
        toLabel.setMinimumSize(new java.awt.Dimension(55, 14));
        toLabel.setPreferredSize(new java.awt.Dimension(55, 14));

        fromLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        fromLabel.setText("From [" + zUnits +"]:");
        fromLabel.setMaximumSize(new java.awt.Dimension(55, 14));
        fromLabel.setPreferredSize(new java.awt.Dimension(55, 14));

        fromField.setColumns(7);
        fromField.setText(fromString);
        fromField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldFocusGained(evt);
            }
        });

        toField.setColumns(7);
        toField.setText(toString);
        toField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldFocusGained(evt);
            }
        });

        incrementsField.setColumns(7);
        incrementsField.setText(isStep ? stepString : planesString);
        incrementsField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldFocusGained(evt);
            }
        });

        incrementLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        incrementLabel.setText(isStep ? "Step [" + zUnits +"]:" : "Planes:");
        incrementLabel.setMaximumSize(new java.awt.Dimension(55, 14));
        incrementLabel.setMinimumSize(new java.awt.Dimension(55, 14));
        incrementLabel.setPreferredSize(new java.awt.Dimension(55, 14));

        javax.swing.GroupLayout inputPanelLayout = new javax.swing.GroupLayout(inputPanel);
        inputPanel.setLayout(inputPanelLayout);
        inputPanelLayout.setHorizontalGroup(
            inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(inputPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(fromLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(toLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(incrementLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(incrementsField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, inputPanelLayout.createSequentialGroup()
                            .addGap(0, 0, Short.MAX_VALUE)
                            .addComponent(fromField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(inputPanelLayout.createSequentialGroup()
                            .addComponent(toField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, Short.MAX_VALUE)))))
        );
        inputPanelLayout.setVerticalGroup(
            inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(inputPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fromLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fromField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(toLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(toField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(incrementLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(incrementsField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        okBtn.setText("Ok");
        okBtn.setPreferredSize(new java.awt.Dimension(65, 23));
        okBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okBtnActionPerformed(evt);
            }
        });

        cancelBtn.setText("Cancel");
        cancelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout btnsPanelLayout = new javax.swing.GroupLayout(btnsPanel);
        btnsPanel.setLayout(btnsPanelLayout);
        btnsPanelLayout.setHorizontalGroup(
            btnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btnsPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(okBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelBtn))
        );
        btnsPanelLayout.setVerticalGroup(
            btnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btnsPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(btnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancelBtn)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(inputPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(inputPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okBtnActionPerformed
        fromString = fromField.getText();
//        System.out.println(fromString);
        try {
            from = Float.parseFloat(fromString);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please insert a valid starting distance.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        toString = toField.getText();
        try {
            to = Float.parseFloat(toString);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please insert a valid ending distance.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (from == to) {
            JOptionPane.showMessageDialog(this, "Starting and ending distances must be different.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (isStep) {
            stepString = incrementsField.getText();
            try {
                step = Float.parseFloat(stepString);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please insert a valid step value.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (step >= to - from) {
                JOptionPane.showMessageDialog(this, "Step value must be less than the difference between ending and starting distances.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (to > from && step < 0) {
                JOptionPane.showMessageDialog(this, "Given the starting and ending distances, the step value must be positive.", "Error", JOptionPane.ERROR_MESSAGE);
                return;

            } else if (to < from && step > 0) {
                JOptionPane.showMessageDialog(this, "Given the starting and ending distances, the step value must be negative.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (step == 0) {
                JOptionPane.showMessageDialog(this, "Step value must be different from 0.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            BigDecimal endB = new BigDecimal(to);
            BigDecimal diff = endB.subtract(new BigDecimal(from));
            BigDecimal result = diff.divide(new BigDecimal(step), 5, RoundingMode.HALF_UP);

            planes = result.intValue() + 1;

        } else {
            try {
                planes = Integer.parseInt(incrementsField.getText());
                if (planes < 1) {
                    JOptionPane.showMessageDialog(this, "The number of planes must be 1 or more.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please insert a valid number of planes.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            step = (to - from) / (planes - 1);
            stepString = "" + step;
        }

        if (planes > maxPlanes) {
            String[] options = new String[]{"Yes", "No"};
            int n = JOptionPane.showOptionDialog(this, "More than " + maxPlanes
                    + " planes are going to be reconstructed. Do you want to continue?", "",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                    options, options[1]);

            if (n == 1) {
                return;
            }
        }

        DecimalFormat df = new DecimalFormat("#.#####", new DecimalFormatSymbols(Locale.US));
        String[] parameters = parent.getFormattedParameters(true);

        StringBuilder info = new StringBuilder();
        info.append("\nMethod: " + MainFrame.PROPAGATION_METHOD[idx]
                + "\nReal input: " + parameters[0]
                + "\nImaginary input: " + parameters[1]
                + "\nWavelength: " + parameters[2]
                + "\nDistance: " + df.format(from) + " " + zUnits + " to " + df.format(to) + " " + zUnits);

        if (isStep) {
            info.append("\nStep: " + df.format(step) + " " + zUnits);
        } else {
            info.append("\nPlanes: " + planes);
        }

        info.append("\nInput Width: " + parameters[4]
                + "\nInput Height: " + parameters[5]);

        if (idx == 2) {
            info.append("\nOutput Width: " + parameters[6]
                    + "\nOutput Height: " + parameters[7]);
        }

        parent.updateLog(true, info.toString());

        fixUnits();

        float z = from;

        setVisible(false);
        parent.setCursor(Cursor.getPredefinedCursor(3));

        //<editor-fold defaultstate="collapsed" desc="for">
        int M = data.getM();
        int N = data.getN();
        ImageStack phaseStack = new ImageStack(M, N);
        ImageStack amplitudeStack = new ImageStack(M, N);
        ImageStack intensityStack = new ImageStack(M, N);
        ImageStack realStack = new ImageStack(M, N);
        ImageStack imaginaryStack = new ImageStack(M, N);

        boolean fb = idx == 2;

        for (int i = 0; i < planes; i++) {

            data.setDistance(z, fb);

            data.propagate(idx, filterEnabled, isPlane, curvRadius);

            float[][] field = data.getOutputField();
            String label = "z = " + df.format(umToUnits(z)) + " " + zUnits;

            float[][] amplitude = null;
            float max = Float.MIN_VALUE;

            if (realEnabled || imaginaryEnabled) {
                amplitude = ArrayUtils.modulus(field);
                max = ArrayUtils.max(amplitude);
            }

            if (phaseEnabled) {
                ImageProcessor ip1 = new FloatProcessor(ArrayUtils.phase(field));
                phaseStack.addSlice(label, phaseByteSelected ? ip1.convertToByteProcessor() : ip1);
            }

            if (amplitudeEnabled) {
                ImageProcessor ip2 = new FloatProcessor(realEnabled || imaginaryEnabled ? amplitude : ArrayUtils.modulus(field));
                if (amplitudeLogSelected) {
                    ip2.log();
                }
                amplitudeStack.addSlice(label, amplitudeByteSelected ? ip2.convertToByteProcessor() : ip2);
            }

            if (intensityEnabled) {
                ImageProcessor ip3 = new FloatProcessor(ArrayUtils.modulusSq(field));
                if (intensityLogSelected) {
                    ip3.log();
                }
                intensityStack.addSlice(label, intensityByteSelected ? ip3.convertToByteProcessor() : ip3);
            }

            if (realEnabled) {
                float[][] real = ArrayUtils.real(field);
                ArrayUtils.divide(real, max);

                ImageProcessor ip4 = new FloatProcessor(real);
                realStack.addSlice(label, ip4);
            }

            if (imaginaryEnabled) {
                float[][] imaginary = ArrayUtils.imaginary(field);
                ArrayUtils.divide(imaginary, max);

                ImageProcessor ip5 = new FloatProcessor(imaginary);
                imaginaryStack.addSlice(label, ip5);
            }

            z += step;
        }

        Calibration cal = parent.getCalibration();

        String names = "; Re: " + parameters[0] + "; Im: " + parameters[1];

        if (phaseEnabled) {
            ImagePlus imp1 = new ImagePlus("Phase" + names, phaseStack);
            if (idx != 1) {
                imp1.setCalibration(cal);
            }
            imp1.show();
        }

        if (amplitudeEnabled) {
            ImagePlus imp2 = new ImagePlus("Amplitude" + names, amplitudeStack);
            if (idx != 1) {
                imp2.setCalibration(cal);
            }
            imp2.show();
        }

        if (intensityEnabled) {
            ImagePlus imp3 = new ImagePlus("Intensity" + names, intensityStack);
            if (idx != 1) {
                imp3.setCalibration(cal);
            }
            imp3.show();
        }

        if (realEnabled) {
            ImagePlus imp4 = new ImagePlus("Real" + names, realStack);
            if (idx != 1) {
                imp4.setCalibration(cal);
            }
            imp4.show();
        }

        if (imaginaryEnabled) {
            ImagePlus imp5 = new ImagePlus("Imaginary" + names, imaginaryStack);
            if (idx != 1) {
                imp5.setCalibration(cal);
            }
            imp5.show();
        }
//</editor-fold>

        parent.setCursor(Cursor.getDefaultCursor());
        savePrefs();
        dispose();
    }//GEN-LAST:event_okBtnActionPerformed

    private void cancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelBtnActionPerformed
        setVisible(false);
        dispose();
    }//GEN-LAST:event_cancelBtnActionPerformed

    private void textFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textFieldFocusGained
        JTextField field = (JTextField) evt.getComponent();
        field.selectAll();
    }//GEN-LAST:event_textFieldFocusGained

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel btnsPanel;
    private javax.swing.JButton cancelBtn;
    private javax.swing.JTextField fromField;
    private javax.swing.JLabel fromLabel;
    private javax.swing.JLabel incrementLabel;
    private javax.swing.JTextField incrementsField;
    private javax.swing.JPanel inputPanel;
    private javax.swing.JButton okBtn;
    private javax.swing.JTextField toField;
    private javax.swing.JLabel toLabel;
    // End of variables declaration//GEN-END:variables
}
