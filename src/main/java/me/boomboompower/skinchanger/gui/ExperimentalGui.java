/*
 *     Copyright (C) 2017 boomboompower
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

package me.boomboompower.skinchanger.gui;

import me.boomboompower.skinchanger.gui.experimental.GuiExperimentalAllPlayers;
import me.boomboompower.skinchanger.gui.experimental.GuiExperimentalOptifine;
import me.boomboompower.skinchanger.gui.utils.ModernButton;
import me.boomboompower.skinchanger.gui.utils.ModernGui;

public class ExperimentalGui extends ModernGui {

    @Override
    public void initGui() {
        this.buttonList.add(new ModernButton(1, this.width / 2 - 75, this.height / 2 + 26, 150, 20, "All player utils"));
        this.buttonList.add(new ModernButton(2, this.width / 2 - 75, this.height / 2 + 50, 150, 20, "Optifine utils"));
    }

    @Override
    public void buttonPressed(ModernButton button) {
        switch (button.id) {
            case 1:
                mc.displayGuiScreen(new GuiExperimentalAllPlayers());
                break;
            case 2:
                mc.displayGuiScreen(new GuiExperimentalOptifine());
                break;
            default:
                mc.displayGuiScreen(null);
                break;
        }
    }
}
