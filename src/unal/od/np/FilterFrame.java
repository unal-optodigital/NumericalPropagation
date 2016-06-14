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
import ij.ImagePlus;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.prefs.Preferences;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import unal.od.jdiffraction.cpu.utils.ArrayUtils;

/**
 *
 * @author: Raul Castañeda (racastanedaq@unal.edu.co)
 * @author: Pablo Piedrahita-Quintero (jppiedrahitaq@unal.edu.co)
 * @author Jorge Garcia-Sucerquia (jigarcia@unal.edu.co)
 */
public class FilterFrame extends JFrame implements PreferencesKeys{

    // <editor-fold defaultstate="collapsed" desc="Variables Frame">
    private static final String TITLE = "Filter";
//    private static final String separator = "\n---------------------------";

    private JPanel radioPanel;
    private JPanel parametersPanel;
    private JPanel btnsPanel;

    private JRadioButton manualRadio;
    private JRadioButton coordRadio;

    private JLabel xLabel;
    private JLabel yLabel;
    private JLabel wLabel;
    private JLabel hLabel;

    private JTextField xField;
    private JTextField yField;
    private JTextField wField;
    private JTextField hField;

    private JButton okBtn;
    private JButton cancelBtn;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Prefs variables">
    String xString;
    String yString;
    String wString;
    String hString;

    private boolean manual;
    // </editor-fold>

    private final Preferences pref;
    private final Data data;

    private final ImageProcessor ip;
    private final ImagePlus imp;

    private final int idx;

    private final MainFrame parent;

    public FilterFrame(MainFrame parent, int idx) {
        pref = Preferences.userNodeForPackage(getClass());
        data = Data.getInstance();

        this.idx = idx;
        this.parent = parent;

        data.calculateFFT();

        ip = new FloatProcessor(data.getImageSpectrum());

        if (pref.getBoolean(FFT_LOG, true)) {
            ip.log();
        }

        imp = new ImagePlus("FFT", pref.getBoolean(FFT_8_BIT, false) ? ip.convertToByteProcessor() : ip);

        imp.show();

        parent.setFftID(imp.getID());

        initComponents();
    }

    private void initComponents() {

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                close(true);
            }
        });

        setResizable(false);
        setTitle(TITLE);
        loadPrefs();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);

        //Frame
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        setLayout(gbl);

        ButtonListener btnListener = new ButtonListener();

        //Radio Panel
        GridBagLayout gblRadioPanel = new GridBagLayout();
        radioPanel = new JPanel(gblRadioPanel);
        GridBagConstraints gbcRadioPanel = new GridBagConstraints();

        gbcRadioPanel.anchor = GridBagConstraints.CENTER;
        gbcRadioPanel.fill = GridBagConstraints.BOTH;
        gbcRadioPanel.insets = new Insets(5, 5, 5, 5);
        gbcRadioPanel.gridx = 0;
        manualRadio = makeRadio("Manual", gbcRadioPanel, radioPanel);
        manualRadio.setSelected(true);
        gbcRadioPanel.gridx = 1;
        coordRadio = makeRadio("Coordinates", gbcRadioPanel, radioPanel);
        ButtonGroup group = new ButtonGroup();
        group.add(manualRadio);
        group.add(coordRadio);
        manualRadio.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                enableFields(!manualRadio.isSelected());
            }
        });
        coordRadio.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                enableFields(coordRadio.isSelected());
            }
        });

        if (manual) {
            manualRadio.setSelected(true);
        } else {
            coordRadio.setSelected(true);
        }

        //Parameters panel
        GridBagLayout gblParametersPanel = new GridBagLayout();
        parametersPanel = new JPanel(gblParametersPanel);
        GridBagConstraints gbcParametersPanel = new GridBagConstraints();

        gbcParametersPanel.anchor = GridBagConstraints.EAST;
        gbcParametersPanel.fill = GridBagConstraints.NONE;
        gbcParametersPanel.insets = new Insets(5, 5, 5, 5);
        gbcParametersPanel.gridx = 0;
        xLabel = makeLabel("X:", gbcParametersPanel, parametersPanel);
        yLabel = makeLabel("Y:", gbcParametersPanel, parametersPanel);
        gbcParametersPanel.gridx = 1;
        gbcParametersPanel.fill = GridBagConstraints.BOTH;
        xField = makeField(xString, gbcParametersPanel, parametersPanel);
        yField = makeField(yString, gbcParametersPanel, parametersPanel);
        gbcParametersPanel.gridx = 2;
        gbcParametersPanel.fill = GridBagConstraints.NONE;
        wLabel = makeLabel("Width:", gbcParametersPanel, parametersPanel);
        hLabel = makeLabel("Height:", gbcParametersPanel, parametersPanel);
        gbcParametersPanel.gridx = 3;
        gbcParametersPanel.fill = GridBagConstraints.BOTH;
        wField = makeField(wString, gbcParametersPanel, parametersPanel);
        hField = makeField(hString, gbcParametersPanel, parametersPanel);

        enableFields(!manual);

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
        gbcBtnsPanel.ipadx = 0;
        gbcBtnsPanel.gridx = 1;
        cancelBtn = makeBtn("Cancel", btnListener, btnsPanel, gbcBtnsPanel);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        add(radioPanel, gbc);
        add(parametersPanel, gbc);
        add(btnsPanel, gbc);

        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png")));
        pack();
        setVisible(true);

//        System.out.println(okBtn.getSize());
//        System.out.println(cancelBtn.getSize());
        //        IJ.showMessage("Please select the area of interest on\nthe image of the spatial frequencies!");
        JOptionPane.showMessageDialog(this, "Please select the area of interest on the FFT of the input hologram.");
        IJ.setTool(3);
    }

//<editor-fold defaultstate="collapsed" desc="Frame methods">
    private JLabel makeLabel(String label, GridBagConstraints gbc1, JPanel panel) {
        JLabel jLabel = new JLabel(label);
        panel.add(jLabel, gbc1);
        return jLabel;
    }

    private JButton makeBtn(String label, ActionListener listener, JPanel panel, GridBagConstraints gbc) {
        JButton btn = new JButton(label);
        panel.add(btn, gbc);
        btn.addActionListener(listener);
        return btn;
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
    }

    private void loadPrefs() {
        xString = pref.get(ROI_X, "");
        yString = pref.get(ROI_Y, "");
        wString = pref.get(ROI_WIDTH, "");
        hString = pref.get(ROI_HEIGHT, "");

        manual = pref.getBoolean(IS_MANUAL, true);
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

    private void ok() {

        if (manualRadio.isSelected()) {
            ImageProcessor ipRoi = imp.getProcessor();

            Rectangle roi = ipRoi.getRoi();
            ImageProcessor ipMask = imp.getMask();

            parent.setFftID(Integer.MAX_VALUE);
            imp.hide();

            data.setROI(roi.x, roi.y, roi.width, roi.height, (ipMask != null) ? ipMask.getIntArray() : null);
            data.center();
        } else if (coordRadio.isSelected()) {
            parent.setFftID(Integer.MAX_VALUE);
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

        if (pref.getBoolean(IS_PLANE, true)) {
            data.propagate(idx);
        } else {
            float curvRadius = unitsToum(pref.getFloat(CURV_RADIUS, 1), pref.get(DISTANCE_UNITS, "m"));
                data.propagate(idx, true, curvRadius);
        }

        String[] parameters = parent.getFormattedParameters(true);

//        parent.updateLog(separator);
        parent.updateLog(true,
                "\nMethod: " + MainFrame.POPAGATION_METHOD[idx]
                + "\nInput: " + parameters[0]
                + "\nWavelength: " + parameters[1]
                + "\nDistance: " + parameters[2]
                + "\nInput Width: " + parameters[3]
                + "\nInput Height: " + parameters[4]);

        if (idx == 2) {
            parent.updateLog(false,
                    "\nOutput Width: " + parameters[5]
                    + "\nOutput Height: " + parameters[6]);
        }

        float[][] field = data.getOutputField();
        data.setFiltered(true);
        parent.setStepDistance(data.getZ());
        parent.enableSteps(true);
        parent.setImageProps();

        if (data.isPhaseSelected()) {
            ImageProcessor ip1 = new FloatProcessor(ArrayUtils.phase(field));
            ImagePlus imp1 = new ImagePlus("Phase; z = " + parameters[2],
                    pref.getBoolean(PHASE_8_BIT, false) ? ip1.convertToByteProcessor() : ip1);
            imp1.show();
        }

        if (data.isAmplitudeSelected()) {
            ImageProcessor ip2 = new FloatProcessor(ArrayUtils.modulus(field));
            if (pref.getBoolean(AMPLITUDE_LOG, false)) {
                ip2.log();
            }

            ImagePlus imp2 = new ImagePlus("Amplitude; z = " + parameters[2],
                    pref.getBoolean(AMPLITUDE_8_BIT, false) ? ip2.convertToByteProcessor() : ip2);
            imp2.show();
        }

        if (data.isIntensitySelected()) {
            ImageProcessor ip3 = new FloatProcessor(ArrayUtils.modulusSq(field));
            if (pref.getBoolean(INTENSITY_LOG, false)) {
                ip3.log();
            }

            ImagePlus imp3 = new ImagePlus("Intensity; z = " + parameters[2],
                    pref.getBoolean(INTENSITY_8_BIT, false) ? ip3.convertToByteProcessor() : ip3);
            imp3.show();
        }

        savePrefs();
        setVisible(false);
        dispose();
    }

    public void close(boolean showDialog) {
        parent.setFftID(Integer.MAX_VALUE);

        imp.hide();
        setVisible(false);
//        IJ.error("The image must be filtered.");
        if (showDialog) {
            JOptionPane.showMessageDialog(this, "The image must be filtered.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        dispose();
    }

    private class ButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JButton source = (JButton) e.getSource();
            if (source == okBtn) {
                ok();
            } else if (source == cancelBtn) {
                close(true);
            }
        }
    }
}
