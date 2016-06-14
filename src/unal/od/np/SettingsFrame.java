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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.prefs.Preferences;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 *
 * @author: Raul Castañeda (racastanedaq@unal.edu.co)
 * @author: Pablo Piedrahita-Quintero (jppiedrahitaq@unal.edu.co)
 * @author Jorge Garcia-Sucerquia (jigarcia@unal.edu.co)
 */
public class SettingsFrame extends JFrame implements PreferencesKeys{

    // <editor-fold defaultstate="collapsed" desc="Frame variables">
    private static final String title = "Settings";
    private static final String[] units = new String[]{"Nanometers", "Micrometers", "Millimeters", "Centimeters", "Meters"};
    private static final String[] unitsSymbol = new String[]{"nm", "um", "mm", "cm", "m"};

    private JPanel btnsPanel;
    private JPanel unitsPanel;
    private JPanel logScalePanel;
    private JPanel byteScalePanel;
    private JPanel batchPanel;
    private JPanel illuminationPanel;

    private JLabel lambdaLabel;
    private JLabel zLabel;
    private JLabel inputWLabel;
    private JLabel inputHLabel;
    private JLabel outputWLabel;
    private JLabel outputHLabel;
    private JLabel warningLabel;
    private JLabel curvRadiusLabel;

    private JTextField warningField;
    private JTextField curvRadiusField;

    private JComboBox lambdaCombo;
    private JComboBox zCombo;
    private JComboBox inputWCombo;
    private JComboBox inputHCombo;
    private JComboBox outputWCombo;
    private JComboBox outputHCombo;

    private JCheckBox fftLogChk;
    private JCheckBox amplitudeLogChk;
    private JCheckBox intensityLogChk;

    private JCheckBox fftByteChk;
    private JCheckBox phaseByteChk;
    private JCheckBox amplitudeByteChk;
    private JCheckBox intensityByteChk;

    private JRadioButton stepRadio;
    private JRadioButton planesRadio;

    private JRadioButton planeWaveRadio;
    private JRadioButton spheWaveRadio;

    private JButton okBtn;
    private JButton cancelBtn;
    // </editor-fold>

    private final Preferences pref;

    private final MainFrame parent;

    public SettingsFrame(MainFrame parent) {
        pref = Preferences.userNodeForPackage(getClass());
        this.parent = parent;
        initComponents();
    }

    private void initComponents() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                setVisible(false);
                dispose();
            }
        });

        setResizable(false);
        setTitle(title);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);

        ButtonListener btnListener = new ButtonListener();

        //parameters panel
        GridBagLayout gblUnitsPanel = new GridBagLayout();
        GridBagConstraints gbcUnitsPanel = new GridBagConstraints();
        gbcUnitsPanel.insets = new Insets(5, 5, 5, 5);
        unitsPanel = new JPanel(gblUnitsPanel);
        unitsPanel.setBorder(BorderFactory.createTitledBorder("Units"));

        //Labels column
        gbcUnitsPanel.fill = GridBagConstraints.NONE;
        gbcUnitsPanel.anchor = GridBagConstraints.EAST;
        gbcUnitsPanel.gridx = 0;

        lambdaLabel = makeLabel("Wavelength:", gbcUnitsPanel, unitsPanel);
        zLabel = makeLabel("Distance:", gbcUnitsPanel, unitsPanel);
        inputWLabel = makeLabel("Input Width:", gbcUnitsPanel, unitsPanel);
        inputHLabel = makeLabel("Input Height:", gbcUnitsPanel, unitsPanel);
        outputWLabel = makeLabel("Output Width:", gbcUnitsPanel, unitsPanel);
        outputHLabel = makeLabel("Output Height:", gbcUnitsPanel, unitsPanel);

        //Combos and text fields column
        gbcUnitsPanel.anchor = GridBagConstraints.CENTER;
        gbcUnitsPanel.fill = GridBagConstraints.HORIZONTAL;
        gbcUnitsPanel.gridx = 1;

        lambdaCombo = makeCombo(units, gbcUnitsPanel, unitsPanel);
        zCombo = makeCombo(units, gbcUnitsPanel, unitsPanel);
        inputWCombo = makeCombo(units, gbcUnitsPanel, unitsPanel);
        inputHCombo = makeCombo(units, gbcUnitsPanel, unitsPanel);
        outputWCombo = makeCombo(units, gbcUnitsPanel, unitsPanel);
        outputHCombo = makeCombo(units, gbcUnitsPanel, unitsPanel);

        lambdaCombo.setSelectedIndex(unitToIdx(pref.get(LAMBDA_UNITS, "nm")));
        zCombo.setSelectedIndex(unitToIdx(pref.get(DISTANCE_UNITS, "m")));
        inputWCombo.setSelectedIndex(unitToIdx(pref.get(INPUT_WIDTH_UNITS, "mm")));
        inputHCombo.setSelectedIndex(unitToIdx(pref.get(INPUT_HEIGHT_UNITS, "mm")));
        outputWCombo.setSelectedIndex(unitToIdx(pref.get(OUTPUT_WIDTH_UNITS, "mm")));
        outputHCombo.setSelectedIndex(unitToIdx(pref.get(OUTPUT_HEIGHT_UNITS, "mm")));

        //Log Scale panel
        GridBagLayout gblLogScalePanel = new GridBagLayout();
        GridBagConstraints gbcLogScalePanel = new GridBagConstraints();
        logScalePanel = new JPanel(gblLogScalePanel);
        logScalePanel.setBorder(BorderFactory.createTitledBorder("Logarithmic Scaling"));

        gbcLogScalePanel.fill = GridBagConstraints.NONE;
        gbcLogScalePanel.anchor = GridBagConstraints.WEST;
        gbcLogScalePanel.gridx = 0;

        fftLogChk = makeCheckBox("Fast Fourier Transform", pref.getBoolean(FFT_LOG, true), logScalePanel, gbcLogScalePanel);
        amplitudeLogChk = makeCheckBox("Amplitude", pref.getBoolean(AMPLITUDE_LOG, false), logScalePanel, gbcLogScalePanel);
        intensityLogChk = makeCheckBox("Intensity", pref.getBoolean(INTENSITY_LOG, false), logScalePanel, gbcLogScalePanel);

        //Scale panel
        GridBagLayout gblByteScalePanel = new GridBagLayout();
        GridBagConstraints gbcByteScalePanel = new GridBagConstraints();
        byteScalePanel = new JPanel(gblByteScalePanel);
        byteScalePanel.setBorder(BorderFactory.createTitledBorder("8-bit Scaling"));

        gbcByteScalePanel.fill = GridBagConstraints.NONE;
        gbcByteScalePanel.anchor = GridBagConstraints.WEST;
        gbcByteScalePanel.gridx = 0;

        fftByteChk = makeCheckBox("Fast Fourier Transform", pref.getBoolean(FFT_8_BIT, false), byteScalePanel, gbcByteScalePanel);
        phaseByteChk = makeCheckBox("Phase", pref.getBoolean(PHASE_8_BIT, false), byteScalePanel, gbcByteScalePanel);
        amplitudeByteChk = makeCheckBox("Amplitude", pref.getBoolean(AMPLITUDE_8_BIT, false), byteScalePanel, gbcByteScalePanel);
        intensityByteChk = makeCheckBox("Intensity", pref.getBoolean(INTENSITY_8_BIT, false), byteScalePanel, gbcByteScalePanel);

        //Batch panel
        GridBagLayout gblBatchPanel = new GridBagLayout();
        GridBagConstraints gbcBatchPanel = new GridBagConstraints();
        batchPanel = new JPanel(gblBatchPanel);
        batchPanel.setBorder(BorderFactory.createTitledBorder("Batch Propagation"));

        gbcBatchPanel.anchor = GridBagConstraints.WEST;
        gbcBatchPanel.fill = GridBagConstraints.HORIZONTAL;
        gbcBatchPanel.gridx = 0;
        gbcBatchPanel.gridwidth = 2;
        gbcBatchPanel.weightx = 1;
        stepRadio = makeRadio("Step", gbcBatchPanel, batchPanel);
        stepRadio.setToolTipText("<html>In automatic propagation, choose the step size<br>or the number of planes to be reconstructed.</html>");
        planesRadio = makeRadio("Planes", gbcBatchPanel, batchPanel);
        planesRadio.setToolTipText("<html>In automatic propagation, choose the step size<br>or the number of planes to be reconstructed.</html>");

        gbcBatchPanel.insets = new Insets(5, 5, 5, 5);
        gbcBatchPanel.gridx = 0;
        gbcBatchPanel.gridwidth = 1;
        gbcBatchPanel.weightx = 0;
        gbcBatchPanel.anchor = GridBagConstraints.EAST;
        warningLabel = makeLabel("Max. Planes:", gbcBatchPanel, batchPanel);
        gbcBatchPanel.gridx = 1;
        gbcBatchPanel.weightx = 1;
        gbcBatchPanel.fill = GridBagConstraints.HORIZONTAL;
        warningField = makeField("" + pref.getInt(MAX_PLANES, 10), gbcBatchPanel, batchPanel);
        warningField.setToolTipText("<html>The maximum number of planes<br>allowed to be reconstructed.</html>");

        ButtonGroup groupBatch = new ButtonGroup();
        groupBatch.add(stepRadio);
        groupBatch.add(planesRadio);

        if (pref.getBoolean(IS_STEP, true)) {
            stepRadio.setSelected(true);
        } else {
            planesRadio.setSelected(true);
        }

        //Illumination Panel
        GridBagLayout gblIlluPanel = new GridBagLayout();
        GridBagConstraints gbcIlluPanel = new GridBagConstraints();
        illuminationPanel = new JPanel(gblIlluPanel);
        illuminationPanel.setBorder(BorderFactory.createTitledBorder("Illumination"));

        gbcIlluPanel.fill = GridBagConstraints.HORIZONTAL;
        gbcIlluPanel.anchor = GridBagConstraints.WEST;
        gbcIlluPanel.gridx = 0;
        gbcIlluPanel.gridwidth = 2;
        gbcIlluPanel.weightx = 1;
        planeWaveRadio = makeRadio("Plane", gbcIlluPanel, illuminationPanel);
        spheWaveRadio = makeRadio("Spherical", gbcIlluPanel, illuminationPanel);

        gbcIlluPanel.insets = new Insets(5, 5, 5, 5);
        gbcIlluPanel.gridx = 0;
        gbcIlluPanel.gridwidth = 1;
        gbcIlluPanel.weightx = 0;
        gbcIlluPanel.anchor = GridBagConstraints.EAST;
        curvRadiusLabel = makeLabel("Curv. Radius:", gbcIlluPanel, illuminationPanel);
        gbcIlluPanel.gridx = 1;
        gbcIlluPanel.weightx = 1;
        gbcIlluPanel.anchor = GridBagConstraints.WEST;
        gbcIlluPanel.fill = GridBagConstraints.HORIZONTAL;
        curvRadiusField = makeField("" + pref.getFloat(CURV_RADIUS, 1), gbcIlluPanel, illuminationPanel);

        ButtonGroup groupIllu = new ButtonGroup();
        groupIllu.add(planeWaveRadio);
        groupIllu.add(spheWaveRadio);

        planeWaveRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                curvRadiusField.setEnabled(!planeWaveRadio.isSelected());
            }
        });
        spheWaveRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                curvRadiusField.setEnabled(spheWaveRadio.isSelected());
            }
        });

        if (pref.getBoolean(IS_PLANE, true)) {
            planeWaveRadio.setSelected(true);
            curvRadiusField.setEnabled(false);
        } else {
            spheWaveRadio.setSelected(true);
            curvRadiusField.setEnabled(true);
        }

        //Buttons Panel
        GridBagLayout gblBtnsPanel = new GridBagLayout();
        GridBagConstraints gbcBtnsPanel = new GridBagConstraints();
        btnsPanel = new JPanel(gblBtnsPanel);
        gbcBtnsPanel.fill = GridBagConstraints.HORIZONTAL;
        gbcBtnsPanel.anchor = GridBagConstraints.CENTER;
        gbcBtnsPanel.insets = new Insets(5, 5, 5, 5);
        gbcBtnsPanel.gridx = 0;
        gbcBtnsPanel.ipadx = 20;
        okBtn = makeBtn("Ok", btnListener, btnsPanel, gbcBtnsPanel);
        gbcBtnsPanel.gridx = 1;
        gbcBtnsPanel.ipadx = 0;
        cancelBtn = makeBtn("Cancel", btnListener, btnsPanel, gbcBtnsPanel);

        //
        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridheight = 6;
        add(unitsPanel, gbc);

        gbc.gridx = 1;
        gbc.weighty = 1;
        gbc.gridheight = 3;
        add(batchPanel, gbc);
        add(illuminationPanel, gbc);
        gbc.weighty = 0;

        gbc.gridx = 2;
        gbc.gridheight = 2;
        add(logScalePanel, gbc);
        gbc.gridheight = 4;
        add(byteScalePanel, gbc);

        gbc.gridx = 0;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        add(btnsPanel, gbc);

        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png")));
        pack();

//        System.out.println(okBtn.getSize());
//        System.out.println(cancelBtn.getSize());
    }

    private int unitToIdx(String unit) {
        if (unit.equals("nm")) {
            return 0;
        } else if (unit.equals("um")) {
            return 1;
        } else if (unit.equals("mm")) {
            return 2;
        } else if (unit.equals("cm")) {
            return 3;
        } else {
            return 4;
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Frame methods">
    private JLabel makeLabel(String label, GridBagConstraints gbc1, JPanel panel) {
        JLabel jLabel = new JLabel(label);
        panel.add(jLabel, gbc1);
        return jLabel;
    }

    private JComboBox makeCombo(String[] items, GridBagConstraints gbc1, JPanel panel) {
        JComboBox combo = new JComboBox<String>(new DefaultComboBoxModel<String>(items));
        panel.add(combo, gbc1);
        return combo;
    }

    private JButton makeBtn(String label, ActionListener listener, JPanel panel, GridBagConstraints gbc) {
        JButton btn = new JButton(label);
        panel.add(btn, gbc);
        btn.addActionListener(listener);
        return btn;
    }

    private JCheckBox makeCheckBox(String label, boolean selected, JPanel panel, GridBagConstraints gbc) {
        JCheckBox chk = new JCheckBox(label, selected);
        panel.add(chk, gbc);
        return chk;
    }

    private JTextField makeField(String text, GridBagConstraints gbc1, JPanel panel) {
        JTextField jTextField = new JTextField(text, 4);
        panel.add(jTextField, gbc1);
        return jTextField;
    }

    private JRadioButton makeRadio(String label, GridBagConstraints gbc1, JPanel panel) {
        JRadioButton jRadioButton = new JRadioButton(label);
        panel.add(jRadioButton, gbc1);
        return jRadioButton;
    }
//</editor-fold>

    private void ok() {
        pref.put(LAMBDA_UNITS, unitsSymbol[lambdaCombo.getSelectedIndex()]);
        pref.put(DISTANCE_UNITS, unitsSymbol[zCombo.getSelectedIndex()]);
        pref.put(INPUT_WIDTH_UNITS, unitsSymbol[inputWCombo.getSelectedIndex()]);
        pref.put(INPUT_HEIGHT_UNITS, unitsSymbol[inputHCombo.getSelectedIndex()]);
        pref.put(OUTPUT_WIDTH_UNITS, unitsSymbol[outputWCombo.getSelectedIndex()]);
        pref.put(OUTPUT_HEIGHT_UNITS, unitsSymbol[outputHCombo.getSelectedIndex()]);

        pref.putBoolean(FFT_LOG, fftLogChk.isSelected());
        pref.putBoolean(AMPLITUDE_LOG, amplitudeLogChk.isSelected());
        pref.putBoolean(INTENSITY_LOG, intensityLogChk.isSelected());

        pref.putBoolean(FFT_8_BIT, fftByteChk.isSelected());
        pref.putBoolean(PHASE_8_BIT, phaseByteChk.isSelected());
        pref.putBoolean(AMPLITUDE_8_BIT, amplitudeByteChk.isSelected());
        pref.putBoolean(INTENSITY_8_BIT, intensityByteChk.isSelected());

        pref.putBoolean(IS_STEP, stepRadio.isSelected());
        try {
            int planes = Integer.parseInt(warningField.getText());
            if (planes <= 1) {
                JOptionPane.showMessageDialog(this, "Max. Planes must be greater than 1.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            pref.putInt(MAX_PLANES, planes);
        } catch (NumberFormatException exc) {
            JOptionPane.showMessageDialog(this, "Please insert a valid integer.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        pref.putBoolean(IS_PLANE, planeWaveRadio.isSelected());
        if (spheWaveRadio.isSelected()) {
            try {
                float curvature = Float.parseFloat(curvRadiusField.getText());
                if (curvature == 0) {
                    JOptionPane.showMessageDialog(this, "Curvature value must be different from 0.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                pref.putFloat(CURV_RADIUS, curvature);
            } catch (NumberFormatException exc) {
                JOptionPane.showMessageDialog(this, "Please insert a valid curvature value.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        parent.updateUnits();

        setVisible(false);
        dispose();
    }

    private class ButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            JButton source = (JButton) e.getSource();
            try {
                if (source == okBtn) {
                    ok();
                } else if (source == cancelBtn) {
                    setVisible(false);
                    dispose();
                }
            } catch (OutOfMemoryError exc) {
                IJ.error("Out of memory.");
            }

        }
    }

}
