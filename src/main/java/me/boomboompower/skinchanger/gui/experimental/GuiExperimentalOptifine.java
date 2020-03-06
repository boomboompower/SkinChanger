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

package me.boomboompower.skinchanger.gui.experimental;

import me.boomboompower.skinchanger.SkinChangerModOld;
import me.boomboompower.skinchanger.utils.models.capes.CapeManager;
import me.boomboompower.skinchanger.utils.fake.FakePlayer;

import me.do_you_like.mods.skinchanger.utils.game.ChatColor;
import me.do_you_like.mods.skinchanger.utils.gui.impl.ModernButton;
import me.do_you_like.mods.skinchanger.utils.gui.impl.ModernGui;
import me.do_you_like.mods.skinchanger.utils.gui.impl.ModernTextBox;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class GuiExperimentalOptifine extends ModernGui {

    private FakePlayer fakePlayer = new FakePlayer();
    private CapeManager fakePlayerCapeManager = new CapeManager(this.fakePlayer, false);

    private ModernTextBox textField;

    public GuiExperimentalOptifine() {
        SkinChangerModOld.getInstance().getSkinManager().updatePlayer(null);
    }

    @Override
    public void onGuiOpen() {
        Keyboard.enableRepeatEvents(true);

        this.textList.add(this.textField = new ModernTextBox(0, this.width / 2 - 150, this.height / 2 - 22, 300, 20));

        this.buttonList.add(new ModernButton(0, this.width / 2 - 75, this.height / 2 + 26, 150, 20, "Preview cape"));
        this.buttonList.add(new ModernButton(1, this.width / 2 - 75, this.height / 2 + 50, 150, 20, "Reset cape"));
        this.buttonList.add(new ModernButton(2, this.width / 2 - 75, this.height / 2 + 74, 150, 20, "Confirm cape"));
    }

    @Override
    public void onRender(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        drawCenteredString(this.mc.fontRendererObj,"Names are case sensitive! Ensure you are using the correct name", this.width / 2, this.height / 2 + 8, Color.WHITE.getRGB());

        drawEntityOnScreen(this.width / 2, this.height / 2 - 45, 35, this.width / 2 - mouseX, (this.height / 2 - 90) - mouseY, this.fakePlayer, true);
    
        drawCenteredString(this.mc.fontRendererObj, ChatColor.WHITE + "Hold Left-Alt to flip the cape!", this.width / 2, this.height / 2 + 100, Color.WHITE.getRGB());

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void buttonPressed(ModernButton button) {
        switch (button.getId()) {
            case 0:
                //this.fakePlayer.getPlayerInfo().setLocationCape();

                if (!this.textField.getText().isEmpty() && this.textField.getText().length() >= 2) {
                    this.fakePlayerCapeManager.giveOfCape(this.textField.getText());
                }
                break;
            case 1:
                SkinChangerModOld.getInstance().getCapeManager().setExperimental(false);
                SkinChangerModOld.getInstance().getCapeManager().removeCape();
                this.mc.displayGuiScreen(null);
                break;
            case 2:
                if (!this.textField.getText().isEmpty() && this.textField.getText().length() >= 2) {
                    SkinChangerModOld.getInstance().getCapeManager().giveOfCape(this.textField.getText());
                    this.mc.displayGuiScreen(null);
                }
                break;
            default:
                this.mc.displayGuiScreen(null);
        }
    }

    @Override
    public void onGuiClose() {
        Keyboard.enableRepeatEvents(false);
        SkinChangerModOld.getInstance().getLoader().save();
    }
}
