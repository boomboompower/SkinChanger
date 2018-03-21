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

package me.boomboompower.skinchanger.gui.experimental;

import me.boomboompower.skinchanger.SkinChangerMod;
import me.boomboompower.skinchanger.utils.models.CapeManager;
import me.boomboompower.skinchanger.utils.fake.FakePlayer;
import me.boomboompower.skinchanger.gui.utils.ModernButton;
import me.boomboompower.skinchanger.gui.utils.ModernGui;
import me.boomboompower.skinchanger.gui.utils.ModernTextBox;
import me.boomboompower.skinchanger.utils.ChatColor;

import org.lwjgl.input.Keyboard;

import java.awt.*;

public class GuiExperimentalOptifine extends ModernGui {

    private FakePlayer fakePlayer = new FakePlayer();
    private CapeManager fakePlayerCapeManager = new CapeManager(this.fakePlayer, false);

    private ModernTextBox textField;

    public GuiExperimentalOptifine() {
        SkinChangerMod.getInstance().getSkinManager().updatePlayer(null);
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        this.textList.add(this.textField = new ModernTextBox(0, this.width / 2 - 150, this.height / 2 - 22, 300, 20));

        this.buttonList.add(new ModernButton(0, this.width / 2 - 75, this.height / 2 + 26, 150, 20, "Preview cape"));
        this.buttonList.add(new ModernButton(1, this.width / 2 - 75, this.height / 2 + 50, 150, 20, "Reset cape"));
        this.buttonList.add(new ModernButton(2, this.width / 2 - 75, this.height / 2 + 74, 150, 20, "Confirm cape"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        drawCenteredString(this.mc.fontRendererObj,"Names are case sensitive! Ensure you are using the correct name", this.width / 2, this.height / 2 + 8, Color.WHITE.getRGB());

        drawEntityOnScreen(this.width / 2, this.height / 2 - 45, 35, this.width / 2 - mouseX, (this.height / 2 - 90) - mouseY, this.fakePlayer, true);
    
        drawCenteredString(this.mc.fontRendererObj,ChatColor.WHITE + "Hold Left-Alt to flip the cape!", this.width / 2, this.height / 2 + 100, Color.WHITE.getRGB());

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void buttonPressed(ModernButton button) {
        switch (button.id) {
            case 0:
                if (!this.textField.getText().isEmpty() && this.textField.getText().length() >= 2) {
                    this.fakePlayerCapeManager.giveOfCape(this.textField.getText());
                }
                break;
            case 1:
                SkinChangerMod.getInstance().getCapeManager().setExperimental(false);
                SkinChangerMod.getInstance().getCapeManager().removeCape();
                this.mc.displayGuiScreen(null);
                break;
            case 2:
                if (!this.textField.getText().isEmpty() && this.textField.getText().length() >= 2) {
                    SkinChangerMod.getInstance().getCapeManager().giveOfCape(this.textField.getText());
                    this.mc.displayGuiScreen(null);
                }
                break;
            default:
                this.mc.displayGuiScreen(null);
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        SkinChangerMod.getInstance().getLoader().save();
    }
}
