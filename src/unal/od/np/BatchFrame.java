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
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.prefs.Preferences;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import unal.od.jdiffraction.cpu.utils.ArrayUtils;

/**
 *
 * @author: Raul Castañeda (racastanedaq@unal.edu.co)
 * @author: Pablo Piedrahita-Quintero (jppiedrahitaq@unal.edu.co)
 * @author Jorge Garcia-Sucerquia (jigarcia@unal.edu.co)
 */
public class BatchFrame extends JFrame implements PreferencesKeys {

    // <editor-fold defaultstate="collapsed" desc="Frame variables">
    private static final String TITLE = "Batch Propagation";
//    private static final String separator = "\n---------------------------";

    private JPanel inputPanel;
    private JPanel btnsPanel;

    private JLabel fromLabel;
    private JLabel stepLabel;
    private JLabel toLabel;
    private JLabel planesLabel;

    private JTextField fromField;
    private JTextField stepField;
    private JTextField toField;
    private JTextField planesField;

    private JButton okBtn;
    private JButton cancelBtn;

    private float start;
    private float end;
    private float step;
    private int planes;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Prefs variables">
    String startString;
    String endString;
    String stepString;
    String planesString;

    private String zUnits;
    private boolean isStep;
    private int maxPlanes;
    // </editor-fold>

    private final Preferences pref;
    private final Data data;

    private final int idx;

    private final MainFrame parent;

    public BatchFrame(MainFrame parent, int idx) {
        pref = Preferences.userNodeForPackage(getClass());
        data = Data.getInstance();

        this.idx = idx;
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
        setTitle(TITLE);
        setLocationRelativeTo(parent);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        ButtonListener btnListener = new ButtonListener();

        //parameters panel
        GridBagLayout gblInputPanel = new GridBagLayout();
        GridBagConstraints gbcInputPanel = new GridBagConstraints();
        gbcInputPanel.insets = new Insets(5, 5, 5, 5);
        inputPanel = new JPanel(gblInputPanel);

        //Labels column
        gbcInputPanel.fill = GridBagConstraints.NONE;
        gbcInputPanel.anchor = GridBagConstraints.EAST;
        gbcInputPanel.gridx = 0;

        loadPrefs();

        fromLabel = makeLabel("From [" + zUnits + "]:", gbcInputPanel, inputPanel);
        toLabel = makeLabel("To [" + zUnits + "]:", gbcInputPanel, inputPanel);
        if (isStep) {
            stepLabel = makeLabel("Step [" + zUnits + "]:", gbcInputPanel, inputPanel);
        } else {
            planesLabel = makeLabel("Planes:", gbcInputPanel, inputPanel);
        }

        //Combos and text fields column
        gbcInputPanel.anchor = GridBagConstraints.CENTER;
        gbcInputPanel.fill = GridBagConstraints.HORIZONTAL;
        gbcInputPanel.gridx = 1;

        fromField = makeField(startString, gbcInputPanel, inputPanel);
        toField = makeField(endString, gbcInputPanel, inputPanel);
        if (isStep) {
            stepField = makeField(stepString, gbcInputPanel, inputPanel);
        } else {
            planesField = makeField(planesString, gbcInputPanel, inputPanel);
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
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        add(inputPanel, gbc);
        add(btnsPanel, gbc);

        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png")));
        pack();

//        System.out.println(okBtn.getSize());
//        System.out.println(cancelBtn.getSize());
    }

    private void savePrefs() {
        pref.put(START, fromField.getText());
        pref.put(END, toField.getText());

        if (isStep) {
            pref.put(BATCH_STEP, stepField.getText());
        } else {
            pref.put(NUMBER_OF_PLANES, planesField.getText());
        }
    }

    private void loadPrefs() {
        startString = pref.get(START, "");
        endString = pref.get(END, "");
        stepString = pref.get(BATCH_STEP, "");
        planesString = pref.get(NUMBER_OF_PLANES, "");

        zUnits = pref.get(DISTANCE_UNITS, "m");
        isStep = pref.getBoolean(IS_STEP, true);
        maxPlanes = pref.getInt(MAX_PLANES, 10);
    }

    //<editor-fold defaultstate="collapsed" desc="Frame methods">
    private JLabel makeLabel(String label, GridBagConstraints gbc1, JPanel panel) {
        JLabel jLabel = new JLabel(label);
        panel.add(jLabel, gbc1);
        return jLabel;
    }

    private JTextField makeField(String txt, GridBagConstraints gbc1, JPanel panel) {
        JTextField jTextField = new JTextField(txt);
        jTextField.setColumns(9);
        panel.add(jTextField, gbc1);
        return jTextField;
    }

    private JButton makeBtn(String label, ActionListener listener, JPanel panel, GridBagConstraints gbc) {
        JButton btn = new JButton(label);
        panel.add(btn, gbc);
        btn.addActionListener(listener);
        return btn;
    }
//</editor-fold>

    private void ok() {

        startString = fromField.getText();
//        System.out.println(startString);
        try {
            start = Float.parseFloat(startString);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please insert a valid starting distance.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        endString = toField.getText();
        try {
            end = Float.parseFloat(endString);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please insert a valid ending distance.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (start == end) {
            JOptionPane.showMessageDialog(this, "Starting and ending distances can't be equal.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (isStep) {
            stepString = stepField.getText();
            try {
                step = Float.parseFloat(stepString);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please insert a valid step value.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (step >= end - start) {
                JOptionPane.showMessageDialog(this, "Step value must be less than the difference between ending and starting distances.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (end > start && step < 0) {
                JOptionPane.showMessageDialog(this, "Given the starting and ending distances, the step value must be positive.", "Error", JOptionPane.ERROR_MESSAGE);
                return;

            } else if (end < start && step > 0) {
                JOptionPane.showMessageDialog(this, "Given the starting and ending distances, the step value must be negative.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (step == 0) {
                JOptionPane.showMessageDialog(this, "Step value must be different from 0.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            BigDecimal endB = new BigDecimal(end);
            BigDecimal diff = endB.subtract(new BigDecimal(start));
            BigDecimal result = diff.divide(new BigDecimal(step), 5, RoundingMode.HALF_UP);

            planes = result.intValue() + 1;

//            planes = (int) ((end - start) / step) + 1;
//            planes = (int) Math.floor((end - start) / step);
//            System.out.println("r " + result);
//            System.out.println("p " + planes);
//            System.out.println("s " + step);
//            System.out.println("d " + (end - start));
        } else {
            try {
                planes = Integer.parseInt(planesField.getText());
                if (planes < 1) {
                    JOptionPane.showMessageDialog(this, "The number of planes must be 1 or more.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please insert a valid number of planes.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            step = (end - start) / (planes - 1);
            stepString = "" + step;
        }

        if (planes > maxPlanes) {
            String[] options = new String[]{"Yes", "No"};
            int n = JOptionPane.showOptionDialog(this, "More than " + maxPlanes
                    + " planes are going to be reconstructed.\nDo you want to continue?", "",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                    options, options[1]);

            if (n == 1) {
                return;
            }
        }

        DecimalFormat df = new DecimalFormat("#.#####", new DecimalFormatSymbols(Locale.US));
        String[] parameters = parent.getFormattedParameters(true);

//        parent.updateLog(separator);
        parent.updateLog(true,
                "\nMethod: " + MainFrame.POPAGATION_METHOD[idx]
                + "\nInput: " + parameters[0]
                + "\nWavelength: " + parameters[1]
                + "\nDistance: " + df.format(start) + " " + zUnits + " to " + df.format(end) + " " + zUnits
                + "\nPlanes: " + planes
                + "\nStep: " + df.format(step) + " " + zUnits
                + "\nInput Width: " + parameters[3]
                + "\nInput Height: " + parameters[4]
        );

        if (idx == 2) {
            parent.updateLog(false,
                    "\nOutput Width: " + parameters[5]
                    + "\nOutput Height: " + parameters[6]);
        }

        fixUnits();

        float z = start;
//        System.out.println(zString);

        setVisible(false);
        parent.setCursor(Cursor.getPredefinedCursor(3));

        //<editor-fold defaultstate="collapsed" desc="for">
        ImageStack phaseStack = new ImageStack(data.getM(), data.getN());
        ImageStack amplitudeStack = new ImageStack(data.getM(), data.getN());
        ImageStack intensityStack = new ImageStack(data.getM(), data.getN());

        for (int i = 0; i < planes; i++) {
//            IJ.showProgress(i, planes - 1);

            data.setDistance(z);

            if (pref.getBoolean(IS_PLANE, true)) {
                data.propagate(idx);
            } else {
                float curvRadius = unitsToum(pref.getFloat(CURV_RADIUS, 1), zUnits);
                data.propagate(idx, i == 0, curvRadius);
            }

            float[][] field = data.getOutputField();
            String label = "z = " + df.format(umToUnits(z)) + " " + zUnits;
//            System.out.println(label);

            if (data.isPhaseSelected()) {
                ImageProcessor ip1 = new FloatProcessor(ArrayUtils.phase(field));
//                phaseStack.addSlice("z = " + zString + " " + zUnits, ip1);
                phaseStack.addSlice(label, pref.getBoolean(PHASE_8_BIT, false) ? ip1.convertToByteProcessor() : ip1);
            }

            if (data.isAmplitudeSelected()) {
                ImageProcessor ip2 = new FloatProcessor(ArrayUtils.modulus(field));
                if (pref.getBoolean(AMPLITUDE_LOG, false)) {
                    ip2.log();
                }
//                amplitudeStack.addSlice("z = " + zString + " " + zUnits, ip2);
                amplitudeStack.addSlice(label, pref.getBoolean(AMPLITUDE_8_BIT, false) ? ip2.convertToByteProcessor() : ip2);
            }

            if (data.isIntensitySelected()) {
                ImageProcessor ip3 = new FloatProcessor(ArrayUtils.modulusSq(field));
                if (pref.getBoolean(INTENSITY_LOG, false)) {
                    ip3.log();
                }
//                intensityStack.addSlice("z = " + zString + " " + zUnits, ip3);
                intensityStack.addSlice(label, pref.getBoolean(INTENSITY_8_BIT, false) ? ip3.convertToByteProcessor() : ip3);
            }

            z += step;
        }

        if (data.isPhaseSelected()) {
            ImagePlus imp1 = new ImagePlus("Phase", phaseStack);
            imp1.show();

        }

        if (data.isAmplitudeSelected()) {
            ImagePlus imp2 = new ImagePlus("Amplitude", amplitudeStack);
            imp2.show();
        }

        if (data.isIntensitySelected()) {
            ImagePlus imp3 = new ImagePlus("Intensity", intensityStack);
            imp3.show();
        }
//</editor-fold>

        parent.setStepDistance(z);

        parent.setCursor(Cursor.getDefaultCursor());
        savePrefs();
//        setVisible(false);
        dispose();
    }

    private void fixUnits() {
        if (zUnits.equals("nm")) {
            start *= 1E-3f;
            end *= 1E-3f;
            step *= 1E-3f;
        } else if (zUnits.equals("mm")) {
            start *= 1E3f;
            end *= 1E3f;
            step *= 1E3f;
        } else if (zUnits.equals("cm")) {
            start *= 1E4f;
            end *= 1E4f;
            step *= 1E4f;
        } else if (zUnits.equals("m")) {
            start *= 1E6f;
            end *= 1E6f;
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
            }

        }
    }
}
