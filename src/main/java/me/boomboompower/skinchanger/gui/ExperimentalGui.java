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

import me.boomboompower.skinchanger.SkinChangerMod;
import me.boomboompower.skinchanger.gui.experimental.GuiExperimentalAllPlayers;
import me.boomboompower.skinchanger.gui.experimental.GuiExperimentalOptifine;
import me.boomboompower.skinchanger.gui.utils.ModernButton;
import me.boomboompower.skinchanger.gui.utils.ModernGui;
import me.boomboompower.skinchanger.utils.ChatColor;

import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.io.File;
import java.util.Arrays;

public class ExperimentalGui extends ModernGui {

    private ModernButton skinCache;

    @Override
    public void initGui() {
        this.buttonList.add(new ModernButton(0, this.width / 2 - 75, this.height / 2 - 22, 150, 20,
                "Rending: " + (SkinChangerMod.getInstance().isRenderingEnabled() ? ChatColor.GREEN + "On" : ChatColor.GRAY + "Off")));
        this.buttonList.add(new ModernButton(1, this.width / 2 - 75, this.height / 2 + 2, 150, 20, "All player utils"));
        this.buttonList.add(new ModernButton(2, this.width / 2 - 75, this.height / 2 + 26, 150, 20, "Optifine utils"));
        this.buttonList.add(this.skinCache = new ModernButton(3, this.width / 2 - 75, this.height / 2 + 50, 150, 20, "Delete skin cache"));

        this.skinCache.setBackEnabled(new Color(255, 0, 0, 75));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        super.drawScreen(mouseX, mouseY, partialTicks);

        if (this.skinCache.isMouseOver()) {
            drawHoveringText(Arrays.asList("This button is dangerous and", ChatColor.DARK_RED.toString() + ChatColor.BOLD + "may" + ChatColor.RESET + " be bad for your game"), mouseX, mouseY);
        }
    }

    @Override
    public void buttonPressed(ModernButton button) {
        switch (button.id) {
            case 0:
                SkinChangerMod.getInstance().setRenderingEnabled(!SkinChangerMod.getInstance().isRenderingEnabled());
                button.setText("Rending: " + (SkinChangerMod.getInstance().isRenderingEnabled() ? ChatColor.GREEN + "On" : ChatColor.GRAY + "Off"));
                break;
            case 1:
                this.mc.displayGuiScreen(new GuiExperimentalAllPlayers());
                break;
            case 2:
                this.mc.displayGuiScreen(new GuiExperimentalOptifine());
                break;
            case 3:
                FileUtils.deleteQuietly(new File("./mods/skinchanger".replace("/", File.separator), "skins"));
                break;
            default:
                this.mc.displayGuiScreen(null);
                break;
        }
    }
}
