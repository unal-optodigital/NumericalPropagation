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
 * @author: Raul Castañeda (racastanedaq@unal.edu.co)
 * @author: Pablo Piedrahita-Quintero (jppiedrahitaq@unal.edu.co)
 * @author Jorge Garcia-Sucerquia (jigarcia@unal.edu.co)
 */
public class NumericalPropagation_ implements PlugIn {

    private static MainFrame FRAME;

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

        if (arg.equalsIgnoreCase("about")) {
            showAbout();
            return;
        }

        if (FRAME == null || !FRAME.isDisplayable()) {
            FRAME = new MainFrame();
            FRAME.setVisible(true);
        } else {
            FRAME.setVisible(true);
            FRAME.toFront();
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
//    }
}
