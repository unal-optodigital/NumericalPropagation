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

import ij.IJ;
import ij.ImageListener;
import ij.ImagePlus;
import ij.measure.Calibration;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.prefs.Preferences;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import unal.od.jdiffraction.cpu.utils.ArrayUtils;

/**
 *
 * @author Raul Castañeda (racastanedaq@unal.edu.co)
 * @author Pablo Piedrahita-Quintero (jppiedrahitaq@unal.edu.co)
 * @author Jorge Garcia-Sucerquia (jigarcia@unal.edu.co)
 */
public class FilterFrame extends javax.swing.JFrame implements ImageListener, PreferencesKeys {

    private static final String TITLE = "Filter";

    // <editor-fold defaultstate="collapsed" desc="Prefs variables">
    private boolean phaseEnabled;
    private boolean amplitudeEnabled;
    private boolean intensityEnabled;
    private boolean realEnabled;
    private boolean imaginaryEnabled;

    private String xString;
    private String yString;
    private String wString;
    private String hString;

    private boolean manual;

    private boolean showFreqDialog;

    private boolean isPlane;
    private float curvRadius;

    private boolean fftLogSelected;
    private boolean amplitudeLogSelected;
    private boolean intensityLogSelected;

    private boolean fftByteSelected;
    private boolean phaseByteSelected;
    private boolean amplitudeByteSelected;
    private boolean intensityByteSelected;
    private boolean realByteSelected;
    private boolean imaginaryByteSelected;
    // </editor-fold>

    private final Preferences pref;
    private final Data data;

    private final ImageProcessor ip;
    private final ImagePlus imp;

    private final int idx;
    private final int fftID;

    private final MainFrame parent;

    /**
     * Creates new form FilterFrame1
     *
     * @param parent
     * @param idx
     */
    public FilterFrame(MainFrame parent, int idx) {
        pref = Preferences.userNodeForPackage(getClass());
        data = Data.getInstance();

        this.idx = idx;
        this.parent = parent;

        loadPrefs();

        data.calculateFFT();
        ip = new FloatProcessor(data.getImageSpectrum());
        if (fftLogSelected) {
            ip.log();
        }
        imp = new ImagePlus("FFT", fftByteSelected ? ip.convertToByteProcessor() : ip);
        imp.show();

        fftID = imp.getID();

        ImagePlus.addImageListener(this);

        setLocationRelativeTo(parent);
        initComponents();

        setVisible(true);

        if (showFreqDialog) {
            JCheckBox showChk = new JCheckBox("Do not show this message again.", false);
            String msg = "Please select the area of interest on the FFT's spectrum of the input hologram.";
            Object[] msgContent = {msg, showChk};
            JOptionPane.showMessageDialog(this, msgContent, "Information",
                    JOptionPane.INFORMATION_MESSAGE);
            showFreqDialog = !showChk.isSelected();
        }

        IJ.setTool(3);
    }

    private void enableFields(boolean enabled) {
        xField.setEnabled(enabled);
        yField.setEnabled(enabled);
        wField.setEnabled(enabled);
        hField.setEnabled(enabled);
    }

    private void savePrefs() {
        pref.put(ROI_X, xField.getText());
        pref.put(ROI_Y, yField.getText());
        pref.put(ROI_WIDTH, wField.getText());
        pref.put(ROI_HEIGHT, hField.getText());

        pref.putBoolean(IS_MANUAL, manualRadio.isSelected());

        pref.putBoolean(SHOW_FREQUENCIES_DIALOG, showFreqDialog);
    }

    private void loadPrefs() {
        phaseEnabled = pref.getBoolean(PHASE_CHECKED, false);
        amplitudeEnabled = pref.getBoolean(AMPLITUDE_CHECKED, false);
        intensityEnabled = pref.getBoolean(INTENSITY_CHECKED, false);
        realEnabled = pref.getBoolean(REAL_CHECKED, false);
        imaginaryEnabled = pref.getBoolean(IMAGINARY_CHECKED, false);

        xString = pref.get(ROI_X, "");
        yString = pref.get(ROI_Y, "");
        wString = pref.get(ROI_WIDTH, "");
        hString = pref.get(ROI_HEIGHT, "");

        manual = pref.getBoolean(IS_MANUAL, true);

        showFreqDialog = pref.getBoolean(SHOW_FREQUENCIES_DIALOG, true);

        isPlane = pref.getBoolean(IS_PLANE, true);

        curvRadius = pref.getFloat(CURV_RADIUS, 1E6f);

        fftLogSelected = pref.getBoolean(FFT_LOG, true);
        amplitudeLogSelected = pref.getBoolean(AMPLITUDE_LOG, true);
        intensityLogSelected = pref.getBoolean(INTENSITY_LOG, true);

        fftByteSelected = pref.getBoolean(FFT_8_BIT, true);
        phaseByteSelected = pref.getBoolean(PHASE_8_BIT, true);
        amplitudeByteSelected = pref.getBoolean(AMPLITUDE_8_BIT, true);
        intensityByteSelected = pref.getBoolean(INTENSITY_8_BIT, true);
        realByteSelected = pref.getBoolean(REAL_8_BIT, true);
        imaginaryByteSelected = pref.getBoolean(IMAGINARY_8_BIT, true);
    }

    public void close(boolean showDialog) {
        ImagePlus.removeImageListener(this);
        
        imp.hide();
        setVisible(false);
        if (showDialog) {
            JOptionPane.showMessageDialog(this, "Input field must be filtered.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        dispose();
    }
    
    @Override
    public void imageClosed(ImagePlus imp) {
        if (imp.getID() == fftID) {
            close(true);
        }
    }

    @Override
    public void imageOpened(ImagePlus imp) {
    }

    @Override
    public void imageUpdated(ImagePlus imp) {
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        group = new javax.swing.ButtonGroup();
        coordinatesPanel = new javax.swing.JPanel();
        xLabel = new javax.swing.JLabel();
        xField = new javax.swing.JTextField();
        yLabel = new javax.swing.JLabel();
        yField = new javax.swing.JTextField();
        hLabel = new javax.swing.JLabel();
        hField = new javax.swing.JTextField();
        wField = new javax.swing.JTextField();
        wLabel = new javax.swing.JLabel();
        radioPanel = new javax.swing.JPanel();
        manualRadio = new javax.swing.JRadioButton();
        coordRadio = new javax.swing.JRadioButton();
        btnsPanel = new javax.swing.JPanel();
        okBtn = new javax.swing.JButton();
        cancelBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(TITLE);
        setAlwaysOnTop(true);
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png")));
        setMaximumSize(new java.awt.Dimension(179, 164));
        setMinimumSize(new java.awt.Dimension(179, 164));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        coordinatesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Coordinates"));

        xLabel.setText("X:");

        xField.setColumns(4);
        xField.setText(xString);
        xField.setEnabled(!manual);

        yLabel.setText("Y:");

        yField.setColumns(4);
        yField.setText(yString);
        yField.setEnabled(!manual);

        hLabel.setText("Height");

        hField.setColumns(4);
        hField.setText(hString);
        hField.setEnabled(!manual);

        wField.setColumns(4);
        wField.setText(wString);
        wField.setEnabled(!manual);

        wLabel.setText("Width:");

        javax.swing.GroupLayout coordinatesPanelLayout = new javax.swing.GroupLayout(coordinatesPanel);
        coordinatesPanel.setLayout(coordinatesPanelLayout);
        coordinatesPanelLayout.setHorizontalGroup(
            coordinatesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(coordinatesPanelLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(coordinatesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(coordinatesPanelLayout.createSequentialGroup()
                        .addComponent(yLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(yField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(hLabel))
                    .addGroup(coordinatesPanelLayout.createSequentialGroup()
                        .addComponent(xLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(xField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(wLabel)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(coordinatesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(wField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(hField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );
        coordinatesPanelLayout.setVerticalGroup(
            coordinatesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(coordinatesPanelLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(coordinatesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(xLabel)
                    .addComponent(xField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(wField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(wLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(coordinatesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(yLabel)
                    .addComponent(yField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(hLabel)
                    .addComponent(hField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );

        group.add(manualRadio);
        manualRadio.setSelected(manual);
        manualRadio.setText("Manual");
        manualRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manualRadioActionPerformed(evt);
            }
        });

        group.add(coordRadio);
        coordRadio.setSelected(!manual);
        coordRadio.setText("Coordinates");
        coordRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                coordRadioActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout radioPanelLayout = new javax.swing.GroupLayout(radioPanel);
        radioPanel.setLayout(radioPanelLayout);
        radioPanelLayout.setHorizontalGroup(
            radioPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(radioPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(manualRadio)
                .addGap(18, 18, 18)
                .addComponent(coordRadio)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        radioPanelLayout.setVerticalGroup(
            radioPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(radioPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(radioPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(manualRadio)
                    .addComponent(coordRadio))
                .addGap(0, 0, 0))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cancelBtn)
                .addGap(0, 0, 0))
        );
        btnsPanelLayout.setVerticalGroup(
            btnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btnsPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(btnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancelBtn))
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(btnsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(coordinatesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.CENTER, layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(radioPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(radioPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(coordinatesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void manualRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manualRadioActionPerformed
        enableFields(!manualRadio.isSelected());
    }//GEN-LAST:event_manualRadioActionPerformed

    private void coordRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_coordRadioActionPerformed
        enableFields(coordRadio.isSelected());
    }//GEN-LAST:event_coordRadioActionPerformed

    private void okBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okBtnActionPerformed
        if (manualRadio.isSelected()) {
            ImageProcessor ipRoi = imp.getProcessor();

            Rectangle roi = ipRoi.getRoi();
            ImageProcessor ipMask = imp.getMask();

            ImagePlus.removeImageListener(this);
            imp.hide();

            data.setROI(roi.x, roi.y, roi.width, roi.height, (ipMask != null) ? ipMask.getIntArray() : null);
            data.center();
        } else if (coordRadio.isSelected()) {
            ImagePlus.removeImageListener(this);
            imp.hide();

            int x, y, w, h;

            try {
                x = Integer.parseInt(xField.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please insert a valid x value.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                y = Integer.parseInt(yField.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please insert a valid y value.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                w = Integer.parseInt(wField.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please insert a valid width value.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                h = Integer.parseInt(hField.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please insert a valid height value.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            data.setROI(x, y, w, h, null);
            data.center();
        }

        data.propagate(idx, true, isPlane, curvRadius);

        String[] parameters = parent.getFormattedParameters(true);
        StringBuilder info = new StringBuilder();
        info.append("\nMethod: " + MainFrame.PROPAGATION_METHOD[idx]
                + "\nReal input: " + parameters[0]
                + "\nImaginary input: " + parameters[1]
                + "\nWavelength: " + parameters[2]
                + "\nDistance: " + parameters[3]
                + "\nInput Width: " + parameters[4]
                + "\nInput Height: " + parameters[5]);

        if (idx == 2) {
            info.append("\nOutput Width: " + parameters[6]
                    + "\nOutput Height: " + parameters[7]);
        }
        parent.updateLog(true, info.toString());

        float[][] field = data.getOutputField();
        parent.setStepDistance();
        parent.enableAfterPropagationOpt(true);
        parent.setImageProps();

        Calibration cal = parent.getCalibration();

        if (phaseEnabled) {
            ImageProcessor ip1 = new FloatProcessor(ArrayUtils.phase(field));
            ImagePlus imp1 = new ImagePlus("Phase; z = " + parameters[3],
                    phaseByteSelected ? ip1.convertToByteProcessor() : ip1);
            imp1.setCalibration(cal);
            imp1.show();
        }

        if (amplitudeEnabled) {
            ImageProcessor ip2 = new FloatProcessor(ArrayUtils.modulus(field));
            if (amplitudeLogSelected) {
                ip2.log();
            }

            ImagePlus imp2 = new ImagePlus("Amplitude; z = " + parameters[3],
                    amplitudeByteSelected ? ip2.convertToByteProcessor() : ip2);
            imp2.setCalibration(cal);
            imp2.show();
        }

        if (intensityEnabled) {
            ImageProcessor ip3 = new FloatProcessor(ArrayUtils.modulusSq(field));
            if (intensityLogSelected) {
                ip3.log();
            }

            ImagePlus imp3 = new ImagePlus("Intensity; z = " + parameters[3],
                    intensityByteSelected ? ip3.convertToByteProcessor() : ip3);
            imp3.setCalibration(cal);
            imp3.show();
        }

        if (realEnabled) {
            ImageProcessor ip4 = new FloatProcessor(ArrayUtils.real(field));
            ImagePlus imp4 = new ImagePlus("Real; z = " + parameters[3],
                    realByteSelected ? ip4.convertToByteProcessor() : ip4);
            imp4.setCalibration(cal);
            imp4.show();
        }

        if (imaginaryEnabled) {
            ImageProcessor ip5 = new FloatProcessor(ArrayUtils.imaginary(field));
            ImagePlus imp5 = new ImagePlus("Imaginary; z = " + parameters[3],
                    imaginaryByteSelected ? ip5.convertToByteProcessor() : ip5);
            imp5.setCalibration(cal);
            imp5.show();
        }

        savePrefs();
        setVisible(false);
        dispose();
    }//GEN-LAST:event_okBtnActionPerformed

    private void cancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelBtnActionPerformed
        close(true);
    }//GEN-LAST:event_cancelBtnActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        close(true);
    }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel btnsPanel;
    private javax.swing.JButton cancelBtn;
    private javax.swing.JRadioButton coordRadio;
    private javax.swing.JPanel coordinatesPanel;
    private javax.swing.ButtonGroup group;
    private javax.swing.JTextField hField;
    private javax.swing.JLabel hLabel;
    private javax.swing.JRadioButton manualRadio;
    private javax.swing.JButton okBtn;
    private javax.swing.JPanel radioPanel;
    private javax.swing.JTextField wField;
    private javax.swing.JLabel wLabel;
    private javax.swing.JTextField xField;
    private javax.swing.JLabel xLabel;
    private javax.swing.JTextField yField;
    private javax.swing.JLabel yLabel;
    // End of variables declaration//GEN-END:variables
}
