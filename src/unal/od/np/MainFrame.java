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
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.prefs.Preferences;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;
import unal.od.jdiffraction.cpu.utils.ArrayUtils;

/**
 *
 * @author: Raul Castañeda (racastanedaq@unal.edu.co)
 * @author: Pablo Piedrahita-Quintero (jppiedrahitaq@unal.edu.co)
 * @author Jorge Garcia-Sucerquia (jigarcia@unal.edu.co)
 */
public class MainFrame extends JFrame implements ImageListener, PreferencesKeys {

    // <editor-fold defaultstate="collapsed" desc="Frame variables">
    private static final String TITLE = "Numerical Propagation";
    private static final String LOG_HEADER = "Version 1.0 - February 2015";
    private static final String LOG_SEPARATOR = "\n---------------------------";

    public static final String[] POPAGATION_METHOD = new String[]{"Angular Spectrum", "Fresnel", "Fresnel - Bluestein", "Automatic"};

    private JPanel btnsPanel;
    private JPanel propPanel;
//    private JPanel stepPanel;
    private JPanel inputsPanel;
    private JPanel checkPanel;
    private JScrollPane logPane;

    private JLabel methodLabel;
    private JLabel inputLabel;
    private JLabel lambdaLabel;
    private JLabel zLabel;
    private JLabel inputWLabel;
    private JLabel inputHLabel;
    private JLabel outputWLabel;
    private JLabel outputHLabel;

    private JComboBox methodCombo;
    private JComboBox inputCombo;

    private JTextField lambdaField;
    private JTextField zField;
    private JTextField inputWField;
    private JTextField inputHField;
    private JTextField outputWField;
    private JTextField outputHField;
    private JTextField stepField;

    private String lambdaString;
    private String zString;
    private String inputWString;
    private String inputHString;
    private String outputWString;
    private String outputHString;
    private String stepString;

//    private String logSelection;
    private float lambda;
    private float z;
    private float inputW;
    private float inputH;
    private float outputW;
    private float outputH;
    private float step;
    private float zStep;

    private int methodIdx;

    private int fftID;

    private int oldID, newID;
    private int oldM, newM;
    private int oldN, newN;

    private JTextArea log;

    private JPopupMenu popup;
    private JMenuItem copyItem;
    private JMenuItem copyAllItem;
    private JCheckBoxMenuItem wrapItem;
    private JMenuItem clearItem;

    private JCheckBox roiChk;
    private JCheckBox phaseChk;
    private JCheckBox amplitudeChk;
    private JCheckBox intensityChk;

    private JButton settingsBtn;
    private JButton batchBtn;
    private JButton incBtn;
    private JButton propagateBtn;
//    private JButton clearBtn;
    private JButton decBtn;

    private int[] windowsId;
    private String[] titles;

    private SettingsFrame settingsFrame = null;
    private FilterFrame filterFrame = null;
    private BatchFrame batchFrame = null;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Prefs variables">
    private int comboIdx;

    private String lambdaUnits;
    private String zUnits;
    private String inputWUnits;
    private String inputHUnits;
    private String outputWUnits;
    private String outputHUnits;

    private boolean roiEnabled;
    private boolean phaseEnabled;
    private boolean amplitudeEnabled;
    private boolean intensityEnabled;

    private boolean logWrapping;
    // </editor-fold>

    private String imageTitle;
    private final DecimalFormat df;

    private final Preferences pref;
    private final Data data;

    public MainFrame() {
        df = new DecimalFormat("#.####", new DecimalFormatSymbols(Locale.US));
        pref = Preferences.userNodeForPackage(getClass());
        data = Data.getInstance();
        initComponents();
    }

    private void initComponents() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
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
                removeListener();
                setVisible(false);
                dispose();
            }
        });

        setResizable(false);
        setTitle(TITLE);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(300, 300, 501, 291);

        getOpenedImages();
        loadPrefs();

        ButtonListener btnListener = new ButtonListener();

        //parameters panel
        GridBagLayout gblParametersPanel = new GridBagLayout();
        GridBagConstraints gbcParametersPanel = new GridBagConstraints();
        gbcParametersPanel.insets = new Insets(5, 5, 5, 5);
        inputsPanel = new JPanel(gblParametersPanel);

        //Labels column
        gbcParametersPanel.fill = GridBagConstraints.NONE;
        gbcParametersPanel.anchor = GridBagConstraints.EAST;
        gbcParametersPanel.gridx = 0;
        gbcParametersPanel.gridy = GridBagConstraints.RELATIVE;

        methodLabel = makeLabel("Method:", gbcParametersPanel, inputsPanel);
        inputLabel = makeLabel("Input:", gbcParametersPanel, inputsPanel);

        lambdaLabel = makeLabel("Wavelength [" + lambdaUnits + "]:", gbcParametersPanel, inputsPanel);
        zLabel = makeLabel("Distance [" + zUnits + "]:", gbcParametersPanel, inputsPanel);
        inputWLabel = makeLabel("Input Width [" + inputWUnits + "]:", gbcParametersPanel, inputsPanel);
        inputHLabel = makeLabel("Input Height [" + inputHUnits + "]:", gbcParametersPanel, inputsPanel);
        outputWLabel = makeLabel("Output Width [" + outputWUnits + "]:", gbcParametersPanel, inputsPanel);
        outputHLabel = makeLabel("Output Height [" + outputHUnits + "]:", gbcParametersPanel, inputsPanel);

        //Combos and text fields column
        gbcParametersPanel.anchor = GridBagConstraints.CENTER;
        gbcParametersPanel.fill = GridBagConstraints.HORIZONTAL;
        gbcParametersPanel.gridx = 1;

        methodCombo = makeCombo(POPAGATION_METHOD, gbcParametersPanel, inputsPanel);
        methodCombo.setSelectedIndex(comboIdx);
        methodCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (methodCombo.getSelectedIndex() == 2) {
                    outputWField.setEnabled(true);
                    outputHField.setEnabled(true);
                } else {
                    outputWField.setEnabled(false);
                    outputHField.setEnabled(false);
                }
            }
        });
        inputCombo = makeCombo(titles, gbcParametersPanel, inputsPanel);

        lambdaField = makeField(lambdaString, gbcParametersPanel, inputsPanel);
        zField = makeField(zString, gbcParametersPanel, inputsPanel);
        inputWField = makeField(inputWString, gbcParametersPanel, inputsPanel);
        inputHField = makeField(inputHString, gbcParametersPanel, inputsPanel);
        outputWField = makeField(outputWString, gbcParametersPanel, inputsPanel);
        outputHField = makeField(outputHString, gbcParametersPanel, inputsPanel);

        if (comboIdx != 2) {
            outputWField.setEnabled(false);
            outputHField.setEnabled(false);
        }

        inputsPanel.setBorder(BorderFactory.createTitledBorder("Parameters"));

        //Propagate panel
        GridBagLayout gblPropPanel = new GridBagLayout();
        GridBagConstraints gbcPropPanel = new GridBagConstraints();
        propPanel = new JPanel(gblPropPanel);
        gbcPropPanel.fill = GridBagConstraints.HORIZONTAL;
        gbcPropPanel.anchor = GridBagConstraints.CENTER;
//        gbcPropPanel.insets = new Insets(5, 5, 5, 5);
        gbcPropPanel.weightx = 0.5;
        gbcPropPanel.gridx = 0;
        propagateBtn = makeBtn("Propagate", btnListener, propPanel, gbcPropPanel);
        gbcPropPanel.gridx = 1;
        roiChk = makeCheckBox("Same ROI", roiEnabled, propPanel, gbcPropPanel);
        roiChk.setEnabled(false);
        propPanel.setBorder(BorderFactory.createTitledBorder(""));

        //Buttons Panel        
        GridBagLayout gblBtnsPanel = new GridBagLayout();
        GridBagConstraints gbcBtnsPanel = new GridBagConstraints();
        btnsPanel = new JPanel(gblBtnsPanel);
        gbcBtnsPanel.fill = GridBagConstraints.HORIZONTAL;
        gbcBtnsPanel.anchor = GridBagConstraints.CENTER;
        gbcBtnsPanel.insets = new Insets(5, 5, 5, 5);

        gbcBtnsPanel.gridx = 0;
        gbcBtnsPanel.gridwidth = 3;
//        propagateBtn = makeBtn("Propagate", btnListener, btnsPanel, gbcBtnsPanel);
        gbcBtnsPanel.weightx = 1;
        btnsPanel.add(propPanel, gbcBtnsPanel);
//        gbcBtnsPanel.insets = new Insets(5, 5, 5, 5);
        gbcBtnsPanel.gridx = 3;
        gbcBtnsPanel.gridwidth = 1;
        gbcBtnsPanel.weightx = 0;
//        gbcBtnsPanel.ipadx = 12;
        settingsBtn = makeBtn("Settings", btnListener, btnsPanel, gbcBtnsPanel);
//        gbcBtnsPanel.gridx = 3;
        batchBtn = makeBtn("Batch", btnListener, btnsPanel, gbcBtnsPanel);
//        gbcBtnsPanel.gridx = 6;
//        gbcBtnsPanel.ipadx = 26;
//        clearBtn = makeBtn("Clear", btnListener, btnsPanel, gbcBtnsPanel);

        gbcBtnsPanel.gridx = 0;
//        gbcBtnsPanel.gridy = 1;
//        gbcBtnsPanel.ipadx = 0;
        gbcBtnsPanel.fill = GridBagConstraints.NONE;
        gbcBtnsPanel.anchor = GridBagConstraints.WEST;
//        gbcBtnsPanel.gridx = 0;
//        gbcBtnsPanel.gridwidth = 2;
        decBtn = makeBtn("-", btnListener, btnsPanel, gbcBtnsPanel);

        gbcBtnsPanel.fill = GridBagConstraints.HORIZONTAL;
        gbcBtnsPanel.anchor = GridBagConstraints.CENTER;
        gbcBtnsPanel.gridx = 1;
        gbcBtnsPanel.weightx = 1;
        stepField = makeField(stepString, gbcBtnsPanel, btnsPanel);
//        stepField.setColumns(2);
//        stepField.setColumns(3);

        gbcBtnsPanel.fill = GridBagConstraints.NONE;
        gbcBtnsPanel.anchor = GridBagConstraints.EAST;
        gbcBtnsPanel.gridx = 2;
        gbcBtnsPanel.weightx = 0;
        incBtn = makeBtn("+", btnListener, btnsPanel, gbcBtnsPanel);

        //CheckBox panel
        GridBagLayout gblChkPanel = new GridBagLayout();
        GridBagConstraints gbcChkPanel = new GridBagConstraints();
        checkPanel = new JPanel(gblChkPanel);
        gbcChkPanel.fill = GridBagConstraints.HORIZONTAL;
        gbcChkPanel.anchor = GridBagConstraints.CENTER;
        gbcChkPanel.insets = new Insets(5, 5, 5, 5);
        gbcChkPanel.gridy = 0;
        phaseChk = makeCheckBox("Phase", phaseEnabled, checkPanel, gbcChkPanel);
        amplitudeChk = makeCheckBox("Amplitude", amplitudeEnabled, checkPanel, gbcChkPanel);
        intensityChk = makeCheckBox("Intensity", intensityEnabled, checkPanel, gbcChkPanel);

        //log panel
        log = new JTextArea(7, 27);
        log.setEditable(false);
        log.setLineWrap(logWrapping);
        log.setWrapStyleWord(true);
        logPane = new JScrollPane(log, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        DefaultCaret caret = (DefaultCaret) log.getCaret(); //autoscroll
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        log.append(LOG_HEADER);

        popup = new JPopupMenu();
        PopupListener popupListener = new PopupListener(popup);
        ItemListener itemListener = new ItemListener();

        copyItem = new JMenuItem("Copy");
        copyItem.addActionListener(itemListener);
        popup.add(copyItem);

        copyAllItem = new JMenuItem("Copy All");
        copyAllItem.addActionListener(itemListener);
        popup.add(copyAllItem);

        popup.addSeparator();

        wrapItem = new JCheckBoxMenuItem("Wrap text", logWrapping);
        wrapItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBoxMenuItem source = (JCheckBoxMenuItem) e.getSource();

                log.setLineWrap(source.isSelected());
            }
        });
        popup.add(wrapItem);

        popup.addSeparator();

        clearItem = new JMenuItem("Clear");
        clearItem.addActionListener(itemListener);
        popup.add(clearItem);

        log.addMouseListener(popupListener);

        //
        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridheight = 6;
        add(inputsPanel, gbc);
        gbc.gridx = 1;
        gbc.gridheight = 3;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weighty = 1;
        add(logPane, gbc);
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.gridheight = 1;
        gbc.weighty = 0;
        add(checkPanel, gbc);
        gbc.gridheight = 2;
        add(btnsPanel, gbc);

//        ImageIcon icon = new ImageIcon("/icon.png");
//        setIconImage(icon.getImage());
//        setIconImage(Toolkit.getDefaultToolkit().getImage("icon.png"));
        enableSteps(false);
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png")));
        pack();

//        System.out.println(propagateBtn.getSize());
//        System.out.println(settingsBtn.getSize());
//        System.out.println(clearBtn.getSize());
//        System.out.println(getSize());
        inputCombo.setPreferredSize(methodCombo.getSize());

        //adds this class as ImageListener
        ImagePlus.addImageListener(this);

    }

    private void getOpenedImages() {
        windowsId = WindowManager.getIDList();
        if (windowsId == null) {
            titles = new String[]{"<none>"};
        } else {
            //Titles for input
            titles = new String[windowsId.length];
            for (int i = 0; i < windowsId.length; i++) {
                ImagePlus imp = WindowManager.getImage(windowsId[i]);
                if (imp != null) {
                    titles[i] = imp.getTitle();
                } else {
                    titles[i] = "";
                }
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Frame methods">
    private JLabel makeLabel(String label, GridBagConstraints gbc1, JPanel panel) {
        JLabel jLabel = new JLabel(label);
        panel.add(jLabel, gbc1);
        return jLabel;
    }

    private JTextField makeField(String txt, GridBagConstraints gbc1, JPanel panel) {
        JTextField jTextField = new JTextField(txt);
        panel.add(jTextField, gbc1);
        return jTextField;
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
    // </editor-fold>

    private void savePrefs() {
        pref.putInt(METHOD_IDX, methodCombo.getSelectedIndex());

        pref.put(LAMBDA, lambdaField.getText());
        pref.put(DISTANCE, zField.getText());
        pref.put(INPUT_WIDTH, inputWField.getText());
        pref.put(INPUT_HEIGHT, inputHField.getText());
        pref.put(OUTPUT_WIDTH, outputWField.getText());
        pref.put(OUTPUT_HEIGHT, outputHField.getText());

        pref.put(STEP, stepField.getText());

        pref.putBoolean(ROI_CHECKED, roiChk.isSelected());
        pref.putBoolean(PHASE_CHECKED, phaseChk.isSelected());
        pref.putBoolean(AMPLITUDE_CHECKED, amplitudeChk.isSelected());
        pref.putBoolean(INTENSITY_CHECKED, intensityChk.isSelected());

        pref.putBoolean(LOG_WRAPPING, log.getLineWrap());
    }

    private void loadPrefs() {
        comboIdx = pref.getInt(METHOD_IDX, 0);

        lambdaString = pref.get(LAMBDA, "");
        zString = pref.get(DISTANCE, "");
        inputWString = pref.get(INPUT_WIDTH, "");
        inputHString = pref.get(INPUT_HEIGHT, "");
        outputWString = pref.get(OUTPUT_WIDTH, "");
        outputHString = pref.get(OUTPUT_HEIGHT, "");

        stepString = pref.get(STEP, "");

        lambdaUnits = pref.get(LAMBDA_UNITS, "nm");
        zUnits = pref.get(DISTANCE_UNITS, "m");
        inputWUnits = pref.get(INPUT_WIDTH_UNITS, "mm");
        inputHUnits = pref.get(INPUT_HEIGHT_UNITS, "mm");
        outputWUnits = pref.get(OUTPUT_WIDTH_UNITS, "mm");
        outputHUnits = pref.get(OUTPUT_HEIGHT_UNITS, "mm");

        roiEnabled = pref.getBoolean(ROI_CHECKED, false);
        phaseEnabled = pref.getBoolean(PHASE_CHECKED, false);
        amplitudeEnabled = pref.getBoolean(AMPLITUDE_CHECKED, false);
        intensityEnabled = pref.getBoolean(INTENSITY_CHECKED, false);

        logWrapping = pref.getBoolean(LOG_WRAPPING, true);
    }

    public void updateLog(boolean useSep, String s) {
        if (useSep) {
            log.append(LOG_SEPARATOR);
        }
        log.append(s);
    }

    public void updateUnits() {
        lambdaUnits = pref.get(LAMBDA_UNITS, "nm");
        zUnits = pref.get(DISTANCE_UNITS, "m");
        inputWUnits = pref.get(INPUT_WIDTH_UNITS, "mm");
        inputHUnits = pref.get(INPUT_HEIGHT_UNITS, "mm");
        outputWUnits = pref.get(OUTPUT_WIDTH_UNITS, "mm");
        outputHUnits = pref.get(OUTPUT_HEIGHT_UNITS, "mm");

        lambdaLabel.setText("Wavelength [" + lambdaUnits + "]:");
        zLabel.setText("Distance [" + zUnits + "]:");
        inputWLabel.setText("Input Width [" + inputWUnits + "]:");
        inputHLabel.setText("Input Height [" + inputHUnits + "]:");
        outputWLabel.setText("Output Width [" + outputWUnits + "]:");
        outputHLabel.setText("Output Height [" + outputHUnits + "]:");
    }

    public void enableSteps(boolean enabled) {
        roiChk.setEnabled(enabled);
        decBtn.setEnabled(enabled);
        stepField.setEnabled(enabled);
        incBtn.setEnabled(enabled);
        batchBtn.setEnabled(enabled);
    }

    public void setStepDistance(float z) {
        zStep = z;
    }

    public String[] getFormattedParameters(boolean useZ) {
        String[] s = new String[]{
            imageTitle,
            //            String.format(Locale.US, "%.3f %s", umToUnits(lambda, lambdaUnits), lambdaUnits),
            //            String.format(Locale.US, "%.3f %s", umToUnits(useZ ? z : zStep, zUnits), zUnits),
            //            String.format(Locale.US, "%.3f %s", umToUnits(inputW, inputWUnits), inputWUnits),
            //            String.format(Locale.US, "%.3f %s", umToUnits(inputH, inputHUnits), inputHUnits),
            //            String.format(Locale.US, "%.3f %s", umToUnits(outputW, outputWUnits), outputWUnits),
            //            String.format(Locale.US, "%.3f %s", umToUnits(outputH, outputHUnits), outputHUnits)
            "" + df.format(umToUnits(lambda, lambdaUnits)) + " " + lambdaUnits,
            "" + df.format(umToUnits(useZ ? z : zStep, zUnits)) + " " + zUnits,
            "" + df.format(umToUnits(inputW, inputWUnits)) + " " + inputWUnits,
            "" + df.format(umToUnits(inputH, inputHUnits)) + " " + inputHUnits,
            "" + df.format(umToUnits(outputW, outputWUnits)) + " " + outputWUnits,
            "" + df.format(umToUnits(outputH, outputHUnits)) + " " + outputHUnits
        };

        return s;
    }

    private void propagate() {
        if (filterFrame != null && filterFrame.isVisible()) {
            filterFrame.setState(Frame.NORMAL);
            filterFrame.toFront();
            return;
        }

        imageTitle = inputCombo.getSelectedItem().toString();
        if (imageTitle.equalsIgnoreCase("<none>")) {
//            IJ.noImage();
            JOptionPane.showMessageDialog(this, "The are no images open.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ImagePlus imp = WindowManager.getImage(imageTitle);
        ImageProcessor ip = imp.getProcessor();

        newID = imp.getID();
        newM = ip.getWidth();
        newN = ip.getHeight();

        boolean differentDimensions = oldM != newM || oldN != newN;
        boolean differentID = oldID != imp.getID();

//        if (differentDimensions || differentID) {
        data.setInputImage(ip.getFloatArray());
        data.setDimensions(newM, newN);
//        }

        lambdaString = lambdaField.getText();
        try {
            lambda = Float.parseFloat(lambdaField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please insert a valid wavelength.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        zString = zField.getText();
        try {
            z = Float.parseFloat(zField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please insert a valid distance.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        inputWString = inputWField.getText();
        try {
            inputW = Float.parseFloat(inputWField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please insert a valid input width.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        inputHString = inputWField.getText();
        try {
            inputH = Float.parseFloat(inputHField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please insert a valid input height.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        methodIdx = methodCombo.getSelectedIndex();

        if (methodIdx == 2) {
            outputWString = outputWField.getText();
            try {
                outputW = Float.parseFloat(outputWField.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please insert a valid output width.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            outputHString = outputHField.getText();
            try {
                outputH = Float.parseFloat(outputHField.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please insert a valid output height.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

//            System.out.println("" + outputW);
//            System.out.println("" + outputH);
//            System.out.println("---");
        }

        fixUnits();

        if (methodIdx == 2) {
//            System.out.println("" + outputW);
//            System.out.println("" + outputH);

            data.setParameters(lambda, z, inputW, inputH, outputW, outputH);
        } else {
            data.setParameters(lambda, z, inputW, inputH);
        }

        phaseEnabled = phaseChk.isSelected();
        amplitudeEnabled = amplitudeChk.isSelected();
        intensityEnabled = intensityChk.isSelected();

        if (roiChk.isEnabled() && roiChk.isSelected() && !differentDimensions) {
            if (differentID) {
                data.center();
            }

            if (pref.getBoolean(IS_PLANE, true)) {
                data.propagate(methodIdx);
            } else {
                float curvRadius = unitsToum(pref.getFloat(CURV_RADIUS, 1), zUnits);
                data.propagate(methodIdx, true, curvRadius);
            }

            String[] parameters = getFormattedParameters(true);

//        parent.updateLog(separator);
            updateLog(true,
                    "\nMethod: " + POPAGATION_METHOD[methodIdx]
                    + "\nInput: " + parameters[0]
                    + "\nWavelength: " + parameters[1]
                    + "\nDistance: " + parameters[2]
                    + "\nInput Width: " + parameters[3]
                    + "\nInput Height: " + parameters[4]);

            if (methodIdx == 2) {
                updateLog(false,
                        "\nOutput Width: " + parameters[5]
                        + "\nOutput Height: " + parameters[6]);
            }

            float[][] field = data.getOutputField();
            data.setFiltered(true);
            setStepDistance(data.getZ());

            if (phaseEnabled) {
                ImageProcessor ip1 = new FloatProcessor(ArrayUtils.phase(field));
                ImagePlus imp1 = new ImagePlus("Phase; z = " + parameters[2],
                        pref.getBoolean(PHASE_8_BIT, false) ? ip1.convertToByteProcessor() : ip1);
                imp1.show();
            }

            if (amplitudeEnabled) {
                ImageProcessor ip2 = new FloatProcessor(ArrayUtils.modulus(field));
                if (pref.getBoolean(AMPLITUDE_LOG, false)) {
                    ip2.log();
                }

                ImagePlus imp2 = new ImagePlus("Amplitude; z = " + parameters[2],
                        pref.getBoolean(AMPLITUDE_8_BIT, false) ? ip2.convertToByteProcessor() : ip2);
                imp2.show();
            }

            if (intensityEnabled) {
                ImageProcessor ip3 = new FloatProcessor(ArrayUtils.modulusSq(field));
                if (pref.getBoolean(INTENSITY_LOG, false)) {
                    ip3.log();
                }

                ImagePlus imp3 = new ImagePlus("Intensity; z = " + parameters[2],
                        pref.getBoolean(INTENSITY_8_BIT, false) ? ip3.convertToByteProcessor() : ip3);
                imp3.show();
            }

            setImageProps();
        } else {
            data.setOutputs(phaseEnabled, amplitudeEnabled, intensityEnabled);

            if (filterFrame == null || !filterFrame.isDisplayable()) {
                filterFrame = new FilterFrame(this, methodIdx);
                filterFrame.setVisible(true);
            }
        }
    }

    private void settings() {
        if (settingsFrame == null || !settingsFrame.isDisplayable()) {
            settingsFrame = new SettingsFrame(this);
            settingsFrame.setVisible(true);
        } else {
            settingsFrame.setState(Frame.NORMAL);
            settingsFrame.toFront();
        }
    }

    private void increaseOrDecrease(boolean inc) {
//        if (!data.isFiltered()) {
//            IJ.error("   In order to use \"+\" and \"-\" buttons, you\n"
//                    + "need to use the propagate function before.");
//            JOptionPane.showMessageDialog(this, "   In order to use \"+\" and \"-\" buttons, you\n"
//                    + "need to use the propagate function before.", "Error", JOptionPane.ERROR_MESSAGE);
//            return;
//        }

        try {
            stepString = stepField.getText();
            step = Float.parseFloat(stepString);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please insert a valid step.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (zUnits.equals("nm")) {
            step *= 1E-3f;
        } else if (zUnits.equals("mm")) {
            step *= 1E3f;
        } else if (zUnits.equals("cm")) {
            step *= 1E4f;
        } else if (zUnits.equals("m")) {
            step *= 1E6f;
        }

        if (inc) {
            zStep += step;
        } else {
            zStep -= step;
        }

        lambdaString = lambdaField.getText();
        try {
            lambda = Float.parseFloat(lambdaField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please insert a valid wavelength.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        inputWString = inputWField.getText();
        try {
            inputW = Float.parseFloat(inputWField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please insert a valid input width.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        inputHString = inputWField.getText();
        try {
            inputH = Float.parseFloat(inputHField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please insert a valid input height.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        methodIdx = methodCombo.getSelectedIndex();

        if (methodIdx == 2) {
            outputWString = outputWField.getText();
            try {
                outputW = Float.parseFloat(outputWField.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please insert a valid output width.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            outputHString = outputHField.getText();
            try {
                outputH = Float.parseFloat(outputHField.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please insert a valid output height.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        fixUnits();
//        data.setDistance(zStep);

        if (methodIdx == 2) {
            data.setParameters(lambda, zStep, inputW, inputH, outputW, outputH);
        } else {
            data.setParameters(lambda, zStep, inputW, inputH);
        }

        if (pref.getBoolean(IS_PLANE, true)) {
            data.propagate(methodIdx);
        } else {
            float curvRadius = unitsToum(pref.getFloat(CURV_RADIUS, 1), zUnits);
            data.propagate(methodIdx, true, curvRadius);
        }

        String[] parameters = getFormattedParameters(false);
//        methodIdx = methodCombo.getSelectedIndex();

//        log.append(separator);
        updateLog(true,
                "\nMethod: " + POPAGATION_METHOD[methodIdx]
                + "\nInput: " + parameters[0]
                + "\nWavelength: " + parameters[1]
                + "\nDistance: " + parameters[2]
                + "\nInput Width: " + parameters[3]
                + "\nInput Height: " + parameters[4]);

        if (methodIdx == 2) {
            updateLog(false,
                    "\nOutput Width: " + parameters[5]
                    + "\nOutput Height: " + parameters[6]);
        }

        float[][] field = data.getOutputField();
        String label = "z = " + df.format(umToUnits(zStep, zUnits)) + " " + zUnits;

        if (phaseChk.isSelected()) {
            ImageProcessor ip1 = new FloatProcessor(ArrayUtils.phase(field));
            ImagePlus imp1 = new ImagePlus("Phase; " + label,
                    pref.getBoolean(PHASE_8_BIT, false) ? ip1.convertToByteProcessor() : ip1);
            imp1.show();
        }

        if (amplitudeChk.isSelected()) {
            ImageProcessor ip2 = new FloatProcessor(ArrayUtils.modulus(field));
            if (pref.getBoolean(AMPLITUDE_LOG, false)) {
                ip2.log();
            }

            ImagePlus imp2 = new ImagePlus("Amplitude; " + label,
                    pref.getBoolean(AMPLITUDE_8_BIT, false) ? ip2.convertToByteProcessor() : ip2);

            imp2.show();
        }

        if (intensityChk.isSelected()) {
            ImageProcessor ip3 = new FloatProcessor(ArrayUtils.modulusSq(field));
            if (pref.getBoolean(INTENSITY_LOG, false)) {
                ip3.log();
            }

            ImagePlus imp3 = new ImagePlus("Intensity; " + label,
                    pref.getBoolean(INTENSITY_8_BIT, false) ? ip3.convertToByteProcessor() : ip3);

            imp3.show();
        }
    }

    private void batch() {
//        if (!data.isFiltered()) {
//            IJ.error("In order to use Automatic propagation, you\n"
//                    + "need to use the propagate function before.");
//            JOptionPane.showMessageDialog(this, "In order to use Automatic propagation, you\n"
//                    + "need to use the propagate function before.", "Error", JOptionPane.ERROR_MESSAGE);
//            return;
//        }

        if (batchFrame != null && batchFrame.isVisible()) {
            batchFrame.setState(Frame.NORMAL);
            batchFrame.toFront();
            return;
        }

        lambdaString = lambdaField.getText();
        try {
            lambda = Float.parseFloat(lambdaField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please insert a valid wavelength.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        inputWString = inputWField.getText();
        try {
            inputW = Float.parseFloat(inputWField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please insert a valid input width.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        inputHString = inputWField.getText();
        try {
            inputH = Float.parseFloat(inputHField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please insert a valid input height.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        methodIdx = methodCombo.getSelectedIndex();

        if (methodIdx == 2) {
            outputWString = outputWField.getText();
            try {
                outputW = Float.parseFloat(outputWField.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please insert a valid output width.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            outputHString = outputHField.getText();
            try {
                outputH = Float.parseFloat(outputHField.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please insert a valid output height.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        fixUnits();

        if (methodIdx == 2) {
            data.setParameters(lambda, 1f, inputW, inputH, outputW, outputH);
        } else {
            data.setParameters(lambda, 1f, inputW, inputH);
        }

        phaseEnabled = phaseChk.isSelected();
        amplitudeEnabled = amplitudeChk.isSelected();
        intensityEnabled = intensityChk.isSelected();

        data.setOutputs(phaseEnabled, amplitudeEnabled, intensityEnabled);

        if (batchFrame == null || !batchFrame.isDisplayable()) {
            batchFrame = new BatchFrame(this, methodIdx);
            batchFrame.setVisible(true);
        }
    }

    private void fixUnits() {

        lambda = unitsToum(lambda, lambdaUnits);
        z = unitsToum(z, zUnits);
        inputW = unitsToum(inputW, inputWUnits);
        inputH = unitsToum(inputH, inputHUnits);

        if (methodIdx == 2) {
            outputW = unitsToum(outputW, outputWUnits);
            outputH = unitsToum(outputH, outputHUnits);
        }
    }

    private float umToUnits(float val, String units) {

        if (units.equals("nm")) {
            return val * 1E3f;
        } else if (units.equals("mm")) {
            return val * 1E-3f;
        } else if (units.equals("cm")) {
            return val * 1E-4f;
        } else if (units.equals("m")) {
            return val * 1E-6f;
        }

        return val;
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

    public void setImageProps() {
        oldID = newID;
        oldM = newM;
        oldN = newN;
    }

    public void setFftID(int id) {
        fftID = id;
    }

    @Override
    public void imageOpened(ImagePlus imp) {
        int idx = inputCombo.getSelectedIndex();

        getOpenedImages();
        inputCombo.setModel(new DefaultComboBoxModel<String>(titles));
        inputCombo.setSelectedIndex(idx);
    }

    @Override
    public void imageClosed(ImagePlus imp) {

        int idx = inputCombo.getSelectedIndex();

        getOpenedImages();
        inputCombo.setModel(new DefaultComboBoxModel<String>(titles));
        inputCombo.setSelectedIndex((idx >= titles.length) ? titles.length - 1 : idx);

        if (filterFrame != null && filterFrame.isVisible() && imp.getID() == fftID) {
            filterFrame.close(true);
        }
    }

    @Override
    public void imageUpdated(ImagePlus imp) {
    }

    private void removeListener() {
        ImagePlus.removeImageListener(this);
    }

    private class ButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            JButton source = (JButton) e.getSource();
//            try {
            if (source == propagateBtn) {
                propagate();
            } else if (source == settingsBtn) {
                settings();
            } else if (source == incBtn) {
                increaseOrDecrease(true);
            } else if (source == decBtn) {
                increaseOrDecrease(false);
            } else if (source == batchBtn) {
                batch();
            }
//                else if (source == clearBtn){
//                    clear();
//                }
//            } catch (OutOfMemoryError exc) {
//                IJ.error("Out of memory.");
//            }

        }
    }

    private class PopupListener extends MouseAdapter {

        JPopupMenu popup;

        PopupListener(JPopupMenu popupMenu) {
            popup = popupMenu;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);

//            logSelection = log.getSelectedText();
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popup.show(e.getComponent(),
                        e.getX(), e.getY());
            }
        }
    }

    private class ItemListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JMenuItem source = (JMenuItem) e.getSource();

//            try {
            if (source == copyItem) {

                String s = log.getSelectedText();

                StringSelection stringSelection = new StringSelection(s);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);

            } else if (source == copyAllItem) {

                String s = log.getText();

                StringSelection stringSelection = new StringSelection((s != null) ? s : "");
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);

            } else if (source == clearItem) {
                log.setText(LOG_HEADER);
            }
//            } catch (OutOfMemoryError exc) {
//                IJ.error("Out of memory.");
//            }
        }

    }
}
