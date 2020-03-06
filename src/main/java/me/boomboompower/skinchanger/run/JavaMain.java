/*
 *     Copyright (C) 2020 boomboompower
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.boomboompower.skinchanger.run;

import javax.swing.*;
import java.awt.*;

public class JavaMain {

    public static void main(String[] args) {
        for (String s : args) {
            if ("nogui".equals(s) || "--nogui".equals(s)) {
                System.out.println("Cannot run without Menu");
                System.out.println("Closing to avoid issues!");
                System.exit(0);
            }
        }

        if (!GraphicsEnvironment.isHeadless()) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception var3) {
            }

            RunGui.createMenu();
        }
    }
}
