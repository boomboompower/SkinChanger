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
import me.boomboompower.skinchanger.gui.utils.ModernButton;
import me.boomboompower.skinchanger.gui.utils.ModernTextBox;
import me.boomboompower.skinchanger.skins.SkinManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;

import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExperimentalGui extends GuiScreen {

    private ModernTextBox textField;

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        this.textField = new ModernTextBox(0, this.width / 2 - 150, this.height / 2 - 22, 300, 20);

        this.buttonList.add(new ModernButton(1, this.width / 2 - 75, this.height / 2 + 26, 150, 20, "Give skin to all players"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        this.textField.setEnabled(true);
        this.textField.drawTextBox();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 1:
                for (EntityOtherPlayerMP player : get()) {
                    new SkinManager(player, false).update(textField.getText());
                }
                break;
            default:
                mc.displayGuiScreen(null);
                break;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            this.mc.displayGuiScreen(null);
        } else {
            this.textField.textboxKeyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.textField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        SkinChangerMod.getInstance().getLoader().save();
    }

    private List<EntityOtherPlayerMP> get() {
        List<EntityOtherPlayerMP> ppl = new ArrayList<>();
        for (Entity entity : Minecraft.getMinecraft().theWorld.loadedEntityList) {
            if (entity instanceof EntityOtherPlayerMP) {
                ppl.add((EntityOtherPlayerMP) entity);
            }
        }
        return ppl;
    }
}
