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
import ij.plugin.PlugIn;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Raul Castañeda (racastanedaq@unal.edu.co)
 * @author Pablo Piedrahita-Quintero (jppiedrahitaq@unal.edu.co)
 * @author Jorge Garcia-Sucerquia (jigarcia@unal.edu.co)
 */
public class NumericalPropagation_ implements PlugIn {

    private static MainFrame MAIN_FRAME;
    private static UtilitiesFrame UTILITIES_FRAME;

    private static final String about = "DH_OD v1.0\n"
            + "Raul Andrés Castañeda Quintero\n"
            + "Pablo Piedrahita-Quintero\n"
            + "Jorge Garcia-Sucerquia\n"
            + "Grupo de Procesamiento Optodigital\n"
            + "Universidad Nacional de Colombia - Sede Medellín";

    public NumericalPropagation_() {
    }

    @Override
    public void run(String arg) {

        if (IJ.versionLessThan("1.48s")) {
            return;
        }

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (UnsupportedLookAndFeelException e) {
        }

        if (arg.equalsIgnoreCase("utilities")) {
            if (UTILITIES_FRAME == null || !UTILITIES_FRAME.isDisplayable()) {
                UTILITIES_FRAME = new UtilitiesFrame();
                UTILITIES_FRAME.setVisible(true);
            } else {
                UTILITIES_FRAME.setVisible(true);
                UTILITIES_FRAME.toFront();
            }
            return;
        }

        if (arg.equalsIgnoreCase("about")) {
            showAbout();
            return;
        }

        if (MAIN_FRAME == null || !MAIN_FRAME.isDisplayable()) {
            MAIN_FRAME = new MainFrame();
            MAIN_FRAME.setVisible(true);
        } else {
            MAIN_FRAME.setVisible(true);
            MAIN_FRAME.toFront();
        }
    }

    private void showAbout() {
        new AboutFrame().setVisible(true);
    }

//    public static void main(String... args) {
//        new ImageJ();
//
//        String rute = "holo.bmp";
//        File f = new File(rute);
////        System.out.println(f.getAbsolutePath());
//        IJ.open(f.getAbsolutePath());
//
//        new NumericalPropagation_().run("");
//        new NumericalPropagation_().run("about");
//        new NumericalPropagation_().run("utilities");
//    }
}
