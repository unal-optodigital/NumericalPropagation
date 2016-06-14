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

import java.awt.Toolkit;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author Raul Castañeda (racastanedaq@unal.edu.co)
 * @author Pablo Piedrahita-Quintero (jppiedrahitaq@unal.edu.co)
 * @author Jorge Garcia-Sucerquia (jigarcia@unal.edu.co)
 */
public class SettingsFrame extends javax.swing.JFrame implements PreferencesKeys {

    private static final String TITLE = "Settings";
//    private static final String[] units = new String[]{"Nanometers", "Micrometers", "Millimeters", "Centimeters", "Meters"};
    private static final String[] unitsSymbol = new String[]{"nm", "um", "mm", "cm", "m"};

    //units
    private int lambdaIdx;
    private int zIdx;
    private int inputSizeIdx;
//    private int inputWIdx;
//    private int inputHIdx;
    private int outputSizeIdx;
//    private int outputWIdx;
//    private int outputHIdx;
    private int curvRadiusIdx;

    //propagation
    private boolean filterSelected;
    private boolean planeWaveSelected;
    private String curvRadiusString;
    private String curvRadiusUnits;

    private boolean stepSelected;
    private String maxPlanesString;

    //scaling
    private boolean fftLogSelected;
    private boolean amplitudeLogSelected;
    private boolean intensityLogSelected;

    private boolean fftByteSelected;
    private boolean phaseByteSelected;
    private boolean amplitudeByteSelected;
    private boolean intensityByteSelected;
//    private boolean realByteSelected;
//    private boolean imaginaryByteSelected;

    //flag
    private boolean propagationError = false;

    private final Preferences pref;

    private final MainFrame parent;

    /**
     * Creates new form SettingsFrame1
     *
     * @param parent
     */
    public SettingsFrame(MainFrame parent) {
        pref = Preferences.userNodeForPackage(getClass());
        this.parent = parent;

        loadPrefs();

        setLocationRelativeTo(parent);
        initComponents();
    }

    private void loadPrefs() {
        //units
        lambdaIdx = unitToIdx(pref.get(LAMBDA_UNITS, "nm"));
        zIdx = unitToIdx(pref.get(DISTANCE_UNITS, "m"));
        inputSizeIdx = unitToIdx(pref.get(INPUT_SIZE_UNITS, "mm"));
//        inputWIdx = unitToIdx(pref.get(INPUT_WIDTH_UNITS, "nm"));
//        inputHIdx = unitToIdx(pref.get(INPUT_HEIGHT_UNITS, "nm"));
        outputSizeIdx = unitToIdx(pref.get(OUTPUT_SIZE_UNITS, "mm"));
//        outputWIdx = unitToIdx(pref.get(OUTPUT_WIDTH_UNITS, "nm"));
//        outputHIdx = unitToIdx(pref.get(OUTPUT_HEIGHT_UNITS, "nm"));
        curvRadiusIdx = unitToIdx(pref.get(CURVATURE_RADIUS_UNITS, "mm"));

        //propagation
        filterSelected = pref.getBoolean(IS_FILTER_ENABLED, true);
        planeWaveSelected = pref.getBoolean(IS_PLANE, true);
        curvRadiusUnits = pref.get(CURVATURE_RADIUS_UNITS, "m");

        float curvRadius = pref.getFloat(CURV_RADIUS, 1E6f);
        if (curvRadiusUnits.equals("nm")) {
            curvRadius *= 1E3f;
        } else if (curvRadiusUnits.equals("mm")) {
            curvRadius *= 1E-3f;
        } else if (curvRadiusUnits.equals("cm")) {
            curvRadius *= 1E-4f;
        } else if (curvRadiusUnits.equals("m")) {
            curvRadius *= 1E-6f;
        }

        curvRadiusString = "" + curvRadius;
        stepSelected = pref.getBoolean(IS_STEP, true);
        maxPlanesString = "" + pref.getInt(MAX_PLANES, 10);

        //scaling
        fftLogSelected = pref.getBoolean(FFT_LOG, true);
        amplitudeLogSelected = pref.getBoolean(AMPLITUDE_LOG, true);
        intensityLogSelected = pref.getBoolean(INTENSITY_LOG, true);

        fftByteSelected = pref.getBoolean(FFT_8_BIT, true);
        phaseByteSelected = pref.getBoolean(PHASE_8_BIT, true);
        amplitudeByteSelected = pref.getBoolean(AMPLITUDE_8_BIT, true);
        intensityByteSelected = pref.getBoolean(INTENSITY_8_BIT, true);
//        realByteSelected = pref.getBoolean(REAL_8_BIT, true);
//        imaginaryByteSelected = pref.getBoolean(IMAGINARY_8_BIT, true);
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        illuminationGroup = new javax.swing.ButtonGroup();
        batchGroup = new javax.swing.ButtonGroup();
        settingsPane = new javax.swing.JTabbedPane();
        tabUnitsPanel = new javax.swing.JPanel();
        unitsPanel = new javax.swing.JPanel();
        lambdaCombo = new javax.swing.JComboBox();
        inputSizeCombo = new javax.swing.JComboBox();
        zCombo = new javax.swing.JComboBox();
        outputSizeCombo = new javax.swing.JComboBox();
        curvRadiusCombo = new javax.swing.JComboBox();
        lambdaLabel = new javax.swing.JLabel();
        zLabel = new javax.swing.JLabel();
        inputWLabel = new javax.swing.JLabel();
        outputWLabel = new javax.swing.JLabel();
        curvRadiusCLabel = new javax.swing.JLabel();
        applyUnitsBtn = new javax.swing.JButton();
        propagationPanel = new javax.swing.JPanel();
        illuminationPanel = new javax.swing.JPanel();
        planeWaveRadio = new javax.swing.JRadioButton();
        spheWaveRadio = new javax.swing.JRadioButton();
        curvRadiusLabel = new javax.swing.JLabel();
        curvRadiusField = new javax.swing.JTextField();
        applyPropagationPanel = new javax.swing.JButton();
        batchPanel = new javax.swing.JPanel();
        stepRadio = new javax.swing.JRadioButton();
        planesRadio = new javax.swing.JRadioButton();
        warningLabel = new javax.swing.JLabel();
        warningField = new javax.swing.JTextField();
        filterChk = new javax.swing.JCheckBox();
        rstDialogsBtn = new javax.swing.JButton();
        scalingPanel = new javax.swing.JPanel();
        logPanel = new javax.swing.JPanel();
        intensityLogChk = new javax.swing.JCheckBox();
        amplitudeLogChk = new javax.swing.JCheckBox();
        fftLogChk = new javax.swing.JCheckBox();
        applyScalingBtn = new javax.swing.JButton();
        bytePanel = new javax.swing.JPanel();
        fftByteChk = new javax.swing.JCheckBox();
        phaseByteChk = new javax.swing.JCheckBox();
        amplitudeByteChk = new javax.swing.JCheckBox();
        intensityByteChk = new javax.swing.JCheckBox();
        cancelBtn = new javax.swing.JButton();
        okBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(TITLE);
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png")));
        setMaximumSize(new java.awt.Dimension(229, 389));
        setMinimumSize(new java.awt.Dimension(229, 389));
        setResizable(false);

        tabUnitsPanel.setMaximumSize(new java.awt.Dimension(214, 322));
        tabUnitsPanel.setMinimumSize(new java.awt.Dimension(214, 322));
        tabUnitsPanel.setPreferredSize(new java.awt.Dimension(214, 322));

        unitsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Units"));
        unitsPanel.setMaximumSize(new java.awt.Dimension(194, 189));
        unitsPanel.setMinimumSize(new java.awt.Dimension(194, 189));

        lambdaCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Nanometers", "Micrometers", "Millimeters", "Centimeters", "Meters" }));
        lambdaCombo.setSelectedIndex(lambdaIdx);
        lambdaCombo.setMaximumSize(new java.awt.Dimension(83, 20));

        inputSizeCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Nanometers", "Micrometers", "Millimeters", "Centimeters", "Meters" }));
        inputSizeCombo.setSelectedIndex(inputSizeIdx);
        inputSizeCombo.setMaximumSize(new java.awt.Dimension(83, 20));

        zCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Nanometers", "Micrometers", "Millimeters", "Centimeters", "Meters" }));
        zCombo.setSelectedIndex(zIdx);
        zCombo.setMaximumSize(new java.awt.Dimension(83, 20));

        outputSizeCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Nanometers", "Micrometers", "Millimeters", "Centimeters", "Meters" }));
        outputSizeCombo.setSelectedIndex(outputSizeIdx);
        outputSizeCombo.setMaximumSize(new java.awt.Dimension(83, 20));

        curvRadiusCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Nanometers", "Micrometers", "Millimeters", "Centimeters", "Meters" }));
        curvRadiusCombo.setSelectedIndex(curvRadiusIdx);
        curvRadiusCombo.setMaximumSize(new java.awt.Dimension(83, 20));

        lambdaLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lambdaLabel.setText("Wavelength:");
        lambdaLabel.setMaximumSize(new java.awt.Dimension(69, 14));
        lambdaLabel.setMinimumSize(new java.awt.Dimension(69, 14));
        lambdaLabel.setPreferredSize(new java.awt.Dimension(69, 14));

        zLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        zLabel.setText("Distance:");
        zLabel.setMaximumSize(new java.awt.Dimension(69, 14));
        zLabel.setMinimumSize(new java.awt.Dimension(69, 14));
        zLabel.setPreferredSize(new java.awt.Dimension(69, 14));

        inputWLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        inputWLabel.setText("Input size:");
        inputWLabel.setMaximumSize(new java.awt.Dimension(69, 14));
        inputWLabel.setMinimumSize(new java.awt.Dimension(69, 14));
        inputWLabel.setPreferredSize(new java.awt.Dimension(69, 14));

        outputWLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        outputWLabel.setText("Output size:");
        outputWLabel.setMaximumSize(new java.awt.Dimension(69, 14));
        outputWLabel.setMinimumSize(new java.awt.Dimension(69, 14));
        outputWLabel.setPreferredSize(new java.awt.Dimension(69, 14));

        curvRadiusCLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        curvRadiusCLabel.setText("Curv. Radius:");
        curvRadiusCLabel.setMaximumSize(new java.awt.Dimension(69, 14));
        curvRadiusCLabel.setMinimumSize(new java.awt.Dimension(69, 14));
        curvRadiusCLabel.setPreferredSize(new java.awt.Dimension(69, 14));

        javax.swing.GroupLayout unitsPanelLayout = new javax.swing.GroupLayout(unitsPanel);
        unitsPanel.setLayout(unitsPanelLayout);
        unitsPanelLayout.setHorizontalGroup(
            unitsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(unitsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(unitsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(unitsPanelLayout.createSequentialGroup()
                        .addComponent(inputWLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(inputSizeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(unitsPanelLayout.createSequentialGroup()
                        .addComponent(lambdaLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lambdaCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(unitsPanelLayout.createSequentialGroup()
                        .addComponent(zLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(zCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(unitsPanelLayout.createSequentialGroup()
                        .addComponent(curvRadiusCLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(curvRadiusCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(unitsPanelLayout.createSequentialGroup()
                        .addComponent(outputWLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(outputSizeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        unitsPanelLayout.setVerticalGroup(
            unitsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(unitsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(unitsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lambdaCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lambdaLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(unitsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(zCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(zLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(unitsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inputSizeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inputWLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(unitsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(outputSizeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(outputWLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(unitsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(curvRadiusCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(curvRadiusCLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11))
        );

        applyUnitsBtn.setText("Apply");
        applyUnitsBtn.setMaximumSize(new java.awt.Dimension(70, 23));
        applyUnitsBtn.setMinimumSize(new java.awt.Dimension(70, 23));
        applyUnitsBtn.setPreferredSize(new java.awt.Dimension(70, 23));
        applyUnitsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyUnitsBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout tabUnitsPanelLayout = new javax.swing.GroupLayout(tabUnitsPanel);
        tabUnitsPanel.setLayout(tabUnitsPanelLayout);
        tabUnitsPanelLayout.setHorizontalGroup(
            tabUnitsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabUnitsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabUnitsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabUnitsPanelLayout.createSequentialGroup()
                        .addGap(124, 124, 124)
                        .addComponent(applyUnitsBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(unitsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        tabUnitsPanelLayout.setVerticalGroup(
            tabUnitsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabUnitsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(unitsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(88, 88, 88)
                .addComponent(applyUnitsBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        settingsPane.addTab("Units", tabUnitsPanel);

        propagationPanel.setMaximumSize(new java.awt.Dimension(214, 322));
        propagationPanel.setMinimumSize(new java.awt.Dimension(214, 322));
        propagationPanel.setPreferredSize(new java.awt.Dimension(214, 322));

        illuminationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Illumination"));
        illuminationPanel.setMaximumSize(new java.awt.Dimension(194, 117));
        illuminationPanel.setMinimumSize(new java.awt.Dimension(194, 117));
        illuminationPanel.setPreferredSize(new java.awt.Dimension(194, 117));

        illuminationGroup.add(planeWaveRadio);
        planeWaveRadio.setSelected(planeWaveSelected);
        planeWaveRadio.setText("Plane");
        planeWaveRadio.setMargin(new java.awt.Insets(2, 0, 2, 2));
        planeWaveRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                planeWaveRadioActionPerformed(evt);
            }
        });

        illuminationGroup.add(spheWaveRadio);
        spheWaveRadio.setSelected(!planeWaveSelected);
        spheWaveRadio.setText("Spherical");
        spheWaveRadio.setMargin(new java.awt.Insets(2, 0, 2, 2));
        spheWaveRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                spheWaveRadioActionPerformed(evt);
            }
        });

        curvRadiusLabel.setText("Curv. Radius [" + curvRadiusUnits + "]:");
        curvRadiusLabel.setMaximumSize(new java.awt.Dimension(93, 14));
        curvRadiusLabel.setMinimumSize(new java.awt.Dimension(93, 14));
        curvRadiusLabel.setPreferredSize(new java.awt.Dimension(93, 14));

        curvRadiusField.setText(curvRadiusString);
        curvRadiusField.setEnabled(!planeWaveSelected);
        curvRadiusField.setMaximumSize(new java.awt.Dimension(59, 20));
        curvRadiusField.setMinimumSize(new java.awt.Dimension(59, 20));
        curvRadiusField.setPreferredSize(new java.awt.Dimension(59, 20));
        curvRadiusField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldFocusGained(evt);
            }
        });

        javax.swing.GroupLayout illuminationPanelLayout = new javax.swing.GroupLayout(illuminationPanel);
        illuminationPanel.setLayout(illuminationPanelLayout);
        illuminationPanelLayout.setHorizontalGroup(
            illuminationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(illuminationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(illuminationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(illuminationPanelLayout.createSequentialGroup()
                        .addComponent(curvRadiusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(curvRadiusField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(planeWaveRadio)
                    .addComponent(spheWaveRadio))
                .addGap(10, 10, 10))
        );
        illuminationPanelLayout.setVerticalGroup(
            illuminationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(illuminationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(planeWaveRadio)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(spheWaveRadio)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(illuminationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(curvRadiusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(curvRadiusField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11))
        );

        applyPropagationPanel.setText("Apply");
        applyPropagationPanel.setMaximumSize(new java.awt.Dimension(70, 23));
        applyPropagationPanel.setMinimumSize(new java.awt.Dimension(70, 23));
        applyPropagationPanel.setPreferredSize(new java.awt.Dimension(70, 23));
        applyPropagationPanel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyPropagationPanelActionPerformed(evt);
            }
        });

        batchPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Batch Propagation"));
        batchPanel.setMaximumSize(new java.awt.Dimension(194, 117));
        batchPanel.setMinimumSize(new java.awt.Dimension(194, 117));
        batchPanel.setPreferredSize(new java.awt.Dimension(194, 117));

        batchGroup.add(stepRadio);
        stepRadio.setSelected(stepSelected);
        stepRadio.setText("Step size");
        stepRadio.setMargin(new java.awt.Insets(2, 0, 2, 2));

        batchGroup.add(planesRadio);
        planesRadio.setSelected(!stepSelected);
        planesRadio.setText("Number of planes");
        planesRadio.setMargin(new java.awt.Insets(2, 0, 2, 2));

        warningLabel.setText("Max. Planes:");
        warningLabel.setMaximumSize(new java.awt.Dimension(93, 14));
        warningLabel.setMinimumSize(new java.awt.Dimension(93, 14));
        warningLabel.setPreferredSize(new java.awt.Dimension(93, 14));

        warningField.setText(maxPlanesString);
        warningField.setMaximumSize(new java.awt.Dimension(59, 20));
        warningField.setMinimumSize(new java.awt.Dimension(59, 20));
        warningField.setPreferredSize(new java.awt.Dimension(59, 20));
        warningField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldFocusGained(evt);
            }
        });

        javax.swing.GroupLayout batchPanelLayout = new javax.swing.GroupLayout(batchPanel);
        batchPanel.setLayout(batchPanelLayout);
        batchPanelLayout.setHorizontalGroup(
            batchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(batchPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(batchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(batchPanelLayout.createSequentialGroup()
                        .addComponent(warningLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(warningField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(stepRadio)
                    .addComponent(planesRadio))
                .addGap(10, 10, 10))
        );
        batchPanelLayout.setVerticalGroup(
            batchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(batchPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(stepRadio)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(planesRadio)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(batchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(warningLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(warningField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11))
        );

        filterChk.setSelected(filterSelected);
        filterChk.setText("Filter before propagation");

        rstDialogsBtn.setText("Reset dialogs");
        rstDialogsBtn.setPreferredSize(new java.awt.Dimension(100, 23));
        rstDialogsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rstDialogsBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout propagationPanelLayout = new javax.swing.GroupLayout(propagationPanel);
        propagationPanel.setLayout(propagationPanelLayout);
        propagationPanelLayout.setHorizontalGroup(
            propagationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(propagationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(propagationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, propagationPanelLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(rstDialogsBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(applyPropagationPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(propagationPanelLayout.createSequentialGroup()
                        .addGroup(propagationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(filterChk)
                            .addComponent(illuminationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(batchPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        propagationPanelLayout.setVerticalGroup(
            propagationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(propagationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(filterChk)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(illuminationPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(batchPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(propagationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(applyPropagationPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rstDialogsBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11))
        );

        settingsPane.addTab("Propagation", propagationPanel);

        scalingPanel.setMaximumSize(new java.awt.Dimension(214, 322));
        scalingPanel.setMinimumSize(new java.awt.Dimension(214, 322));
        scalingPanel.setPreferredSize(new java.awt.Dimension(214, 322));

        logPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Logarithmic Scaling"));
        logPanel.setMaximumSize(new java.awt.Dimension(194, 98));
        logPanel.setMinimumSize(new java.awt.Dimension(194, 98));
        logPanel.setPreferredSize(new java.awt.Dimension(194, 98));

        intensityLogChk.setSelected(intensityLogSelected);
        intensityLogChk.setText("Intensity");
        intensityLogChk.setMargin(new java.awt.Insets(0, 0, 0, 0));

        amplitudeLogChk.setSelected(amplitudeLogSelected);
        amplitudeLogChk.setText("Amplitude");
        amplitudeLogChk.setMargin(new java.awt.Insets(0, 0, 0, 0));

        fftLogChk.setSelected(fftLogSelected);
        fftLogChk.setText("Fast Fourier Transform");
        fftLogChk.setMargin(new java.awt.Insets(0, 0, 0, 0));

        javax.swing.GroupLayout logPanelLayout = new javax.swing.GroupLayout(logPanel);
        logPanel.setLayout(logPanelLayout);
        logPanelLayout.setHorizontalGroup(
            logPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(logPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(logPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fftLogChk)
                    .addComponent(amplitudeLogChk)
                    .addComponent(intensityLogChk))
                .addGap(41, 41, 41))
        );
        logPanelLayout.setVerticalGroup(
            logPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(logPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fftLogChk)
                .addGap(0, 0, 0)
                .addComponent(amplitudeLogChk)
                .addGap(0, 0, 0)
                .addComponent(intensityLogChk)
                .addContainerGap())
        );

        applyScalingBtn.setText("Apply");
        applyScalingBtn.setMaximumSize(new java.awt.Dimension(70, 23));
        applyScalingBtn.setMinimumSize(new java.awt.Dimension(70, 23));
        applyScalingBtn.setPreferredSize(new java.awt.Dimension(70, 23));
        applyScalingBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyScalingBtnActionPerformed(evt);
            }
        });

        bytePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("8-bit Scaling"));
        bytePanel.setMaximumSize(new java.awt.Dimension(194, 162));
        bytePanel.setMinimumSize(new java.awt.Dimension(194, 162));
        bytePanel.setPreferredSize(new java.awt.Dimension(194, 162));

        fftByteChk.setSelected(fftByteSelected);
        fftByteChk.setText("Fast Fourier Transform");
        fftByteChk.setMargin(new java.awt.Insets(0, 0, 0, 0));

        phaseByteChk.setSelected(phaseByteSelected);
        phaseByteChk.setText("Phase");
        phaseByteChk.setMargin(new java.awt.Insets(0, 0, 0, 0));

        amplitudeByteChk.setSelected(amplitudeByteSelected);
        amplitudeByteChk.setText("Amplitude");
        amplitudeByteChk.setMargin(new java.awt.Insets(0, 0, 0, 0));

        intensityByteChk.setSelected(intensityByteSelected);
        intensityByteChk.setText("Intensity");
        intensityByteChk.setMargin(new java.awt.Insets(0, 0, 0, 0));

        javax.swing.GroupLayout bytePanelLayout = new javax.swing.GroupLayout(bytePanel);
        bytePanel.setLayout(bytePanelLayout);
        bytePanelLayout.setHorizontalGroup(
            bytePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bytePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(bytePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fftByteChk)
                    .addComponent(amplitudeByteChk)
                    .addComponent(intensityByteChk)
                    .addComponent(phaseByteChk))
                .addGap(41, 41, 41))
        );
        bytePanelLayout.setVerticalGroup(
            bytePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bytePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fftByteChk)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(phaseByteChk)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(amplitudeByteChk)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(intensityByteChk)
                .addContainerGap())
        );

        javax.swing.GroupLayout scalingPanelLayout = new javax.swing.GroupLayout(scalingPanel);
        scalingPanel.setLayout(scalingPanelLayout);
        scalingPanelLayout.setHorizontalGroup(
            scalingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scalingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(scalingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, scalingPanelLayout.createSequentialGroup()
                        .addGap(124, 124, 124)
                        .addComponent(applyScalingBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(scalingPanelLayout.createSequentialGroup()
                        .addGroup(scalingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(logPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(bytePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        scalingPanelLayout.setVerticalGroup(
            scalingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scalingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(logPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bytePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50)
                .addComponent(applyScalingBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        settingsPane.addTab("Scaling", scalingPanel);

        cancelBtn.setText("Cancel");
        cancelBtn.setMaximumSize(new java.awt.Dimension(70, 23));
        cancelBtn.setMinimumSize(new java.awt.Dimension(70, 23));
        cancelBtn.setPreferredSize(new java.awt.Dimension(70, 23));
        cancelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelBtnActionPerformed(evt);
            }
        });

        okBtn.setText("Ok");
        okBtn.setMaximumSize(new java.awt.Dimension(70, 23));
        okBtn.setMinimumSize(new java.awt.Dimension(70, 23));
        okBtn.setPreferredSize(new java.awt.Dimension(70, 23));
        okBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(settingsPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(okBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(settingsPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(okBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void applyUnitsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyUnitsBtnActionPerformed
        pref.put(LAMBDA_UNITS, unitsSymbol[lambdaCombo.getSelectedIndex()]);
        pref.put(DISTANCE_UNITS, unitsSymbol[zCombo.getSelectedIndex()]);
        pref.put(INPUT_SIZE_UNITS, unitsSymbol[inputSizeCombo.getSelectedIndex()]);
//        pref.put(INPUT_WIDTH_UNITS, unitsSymbol[inputWCombo.getSelectedIndex()]);
//        pref.put(INPUT_HEIGHT_UNITS, unitsSymbol[inputHCombo.getSelectedIndex()]);
        pref.put(OUTPUT_SIZE_UNITS, unitsSymbol[outputSizeCombo.getSelectedIndex()]);
//        pref.put(OUTPUT_WIDTH_UNITS, unitsSymbol[outputWCombo.getSelectedIndex()]);
//        pref.put(OUTPUT_HEIGHT_UNITS, unitsSymbol[outputHCombo.getSelectedIndex()]);
        pref.put(CURVATURE_RADIUS_UNITS, unitsSymbol[curvRadiusCombo.getSelectedIndex()]);

        curvRadiusLabel.setText("Curv. Radius ["
                + unitsSymbol[curvRadiusCombo.getSelectedIndex()] + "]:");

        parent.updateUnitsPrefs();
    }//GEN-LAST:event_applyUnitsBtnActionPerformed

    private void applyPropagationPanelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyPropagationPanelActionPerformed
        pref.putBoolean(IS_FILTER_ENABLED, filterChk.isSelected());

        pref.putBoolean(IS_STEP, stepRadio.isSelected());
        try {
            int planes = Integer.parseInt(warningField.getText());
            if (planes <= 1) {
                JOptionPane.showMessageDialog(this, "Max. Planes must be greater than 1.", "Error", JOptionPane.ERROR_MESSAGE);
                propagationError = true;
                return;
            }
            pref.putInt(MAX_PLANES, planes);
        } catch (NumberFormatException exc) {
            JOptionPane.showMessageDialog(this, "Please insert a valid integer.", "Error", JOptionPane.ERROR_MESSAGE);
            propagationError = true;
            return;
        }

        pref.putBoolean(IS_PLANE, planeWaveRadio.isSelected());
        if (spheWaveRadio.isSelected()) {
            try {
                float curvature = Float.parseFloat(curvRadiusField.getText());
                if (curvature == 0) {
                    JOptionPane.showMessageDialog(this, "Curvature radius value must be different from 0.", "Error", JOptionPane.ERROR_MESSAGE);
                    propagationError = true;
                    return;
                }

                String units = pref.get(CURVATURE_RADIUS_UNITS, "m");
                if (units.equals("nm")) {
                    curvature *= 1E-3f;
                } else if (units.equals("mm")) {
                    curvature *= 1E3f;
                } else if (units.equals("cm")) {
                    curvature *= 1E4f;
                } else if (units.equals("m")) {
                    curvature *= 1E6f;
                }

                pref.putFloat(CURV_RADIUS, curvature);
            } catch (NumberFormatException exc) {
                JOptionPane.showMessageDialog(this, "Please insert a valid curvature value.", "Error", JOptionPane.ERROR_MESSAGE);
                propagationError = true;
                propagationError = true;
                return;
            }
        }

        propagationError = false;
        parent.updatePropagationPrefs();
    }//GEN-LAST:event_applyPropagationPanelActionPerformed

    private void applyScalingBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyScalingBtnActionPerformed
        pref.putBoolean(FFT_LOG, fftLogChk.isSelected());
        pref.putBoolean(AMPLITUDE_LOG, amplitudeLogChk.isSelected());
        pref.putBoolean(INTENSITY_LOG, intensityLogChk.isSelected());

        pref.putBoolean(FFT_8_BIT, fftByteChk.isSelected());
        pref.putBoolean(PHASE_8_BIT, phaseByteChk.isSelected());
        pref.putBoolean(AMPLITUDE_8_BIT, amplitudeByteChk.isSelected());
        pref.putBoolean(INTENSITY_8_BIT, intensityByteChk.isSelected());
//        pref.putBoolean(REAL_8_BIT, realByteChk.isSelected());
//        pref.putBoolean(IMAGINARY_8_BIT, imaginaryByteChk.isSelected());

        parent.updateScalingPrefs();
    }//GEN-LAST:event_applyScalingBtnActionPerformed

    private void planeWaveRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_planeWaveRadioActionPerformed
        curvRadiusField.setEnabled(!planeWaveRadio.isSelected());
    }//GEN-LAST:event_planeWaveRadioActionPerformed

    private void spheWaveRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_spheWaveRadioActionPerformed
        curvRadiusField.setEnabled(spheWaveRadio.isSelected());
    }//GEN-LAST:event_spheWaveRadioActionPerformed

    private void rstDialogsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rstDialogsBtnActionPerformed
        pref.putBoolean(SHOW_FREQUENCIES_DIALOG, true);
    }//GEN-LAST:event_rstDialogsBtnActionPerformed

    private void okBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okBtnActionPerformed
        applyPropagationPanelActionPerformed(null);
        if (propagationError) {
            propagationError = false;
            return;
        }

        applyUnitsBtnActionPerformed(null);
        applyScalingBtnActionPerformed(null);

        setVisible(false);
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
    private javax.swing.JCheckBox amplitudeByteChk;
    private javax.swing.JCheckBox amplitudeLogChk;
    private javax.swing.JButton applyPropagationPanel;
    private javax.swing.JButton applyScalingBtn;
    private javax.swing.JButton applyUnitsBtn;
    private javax.swing.ButtonGroup batchGroup;
    private javax.swing.JPanel batchPanel;
    private javax.swing.JPanel bytePanel;
    private javax.swing.JButton cancelBtn;
    private javax.swing.JLabel curvRadiusCLabel;
    private javax.swing.JComboBox curvRadiusCombo;
    private javax.swing.JTextField curvRadiusField;
    private javax.swing.JLabel curvRadiusLabel;
    private javax.swing.JCheckBox fftByteChk;
    private javax.swing.JCheckBox fftLogChk;
    private javax.swing.JCheckBox filterChk;
    private javax.swing.ButtonGroup illuminationGroup;
    private javax.swing.JPanel illuminationPanel;
    private javax.swing.JComboBox inputSizeCombo;
    private javax.swing.JLabel inputWLabel;
    private javax.swing.JCheckBox intensityByteChk;
    private javax.swing.JCheckBox intensityLogChk;
    private javax.swing.JComboBox lambdaCombo;
    private javax.swing.JLabel lambdaLabel;
    private javax.swing.JPanel logPanel;
    private javax.swing.JButton okBtn;
    private javax.swing.JComboBox outputSizeCombo;
    private javax.swing.JLabel outputWLabel;
    private javax.swing.JCheckBox phaseByteChk;
    private javax.swing.JRadioButton planeWaveRadio;
    private javax.swing.JRadioButton planesRadio;
    private javax.swing.JPanel propagationPanel;
    private javax.swing.JButton rstDialogsBtn;
    private javax.swing.JPanel scalingPanel;
    private javax.swing.JTabbedPane settingsPane;
    private javax.swing.JRadioButton spheWaveRadio;
    private javax.swing.JRadioButton stepRadio;
    private javax.swing.JPanel tabUnitsPanel;
    private javax.swing.JPanel unitsPanel;
    private javax.swing.JTextField warningField;
    private javax.swing.JLabel warningLabel;
    private javax.swing.JComboBox zCombo;
    private javax.swing.JLabel zLabel;
    // End of variables declaration//GEN-END:variables
}
