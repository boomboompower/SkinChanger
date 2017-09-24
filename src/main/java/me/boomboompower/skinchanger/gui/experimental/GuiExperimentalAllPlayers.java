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
import me.boomboompower.skinchanger.gui.utils.ModernButton;
import me.boomboompower.skinchanger.gui.utils.ModernGui;
import me.boomboompower.skinchanger.gui.utils.ModernTextBox;
import me.boomboompower.skinchanger.skins.SkinManager;
import me.boomboompower.skinchanger.utils.ChatColor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;

import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GuiExperimentalAllPlayers extends ModernGui {

    private ModernTextBox textField;

    private ModernButton resetButton;

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        this.textList.add(this.textField = new ModernTextBox(0, this.width / 2 - 150, this.height / 2 - 22, 300, 20));

        this.buttonList.add(new ModernButton(0, this.width / 2 - 75, this.height / 2 + 26, 150, 20, "Give skin to everyone"));
        this.buttonList.add(new ModernButton(1, this.width / 2 - 75, this.height / 2 + 50, 150, 20, "Switch skins around"));
        this.buttonList.add(this.resetButton = new ModernButton(2, this.width / 2 - 75, this.height / 2 + 74, 150, 20, "Reset all skins"));

        this.resetButton.setBackEnabled(new Color(255, 0, 0, 75));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (this.resetButton.isMouseOver()) {
            drawHoveringText(Arrays.asList("This button is dangerous and", ChatColor.DARK_RED.toString() + ChatColor.BOLD + "may" + ChatColor.RESET + " be bad for your game"), mouseX, mouseY);
        }

        if (SkinChangerMod.getInstance().getWebsiteUtils().isDisabled()) {
            drawCenteredString(this.mc.fontRendererObj, ChatColor.RED + "The mod is currently disabled and will not work!", this.width / 2, this.height / 2 + 100, Color.WHITE.getRGB());
        }
    }

    @Override
    public void buttonPressed(ModernButton button) {
        if (SkinChangerMod.getInstance().getWebsiteUtils().isDisabled()) {
            sendChatMessage("SkinChangerMod is currently disabled, check back soon!");
            this.mc.displayGuiScreen(null);
            return;
        }

        switch (button.id) {
            case 0:
                for (EntityOtherPlayerMP player : get()) {
                    new SkinManager(player, false).update(this.textField.getText());
                }
                this.mc.displayGuiScreen(null);
                break;
            case 1:
                List<String> names = new ArrayList<>();

                get().forEach(p -> names.add(p.getName()));

                for (EntityOtherPlayerMP player : get()) {
                    String name = names.get(new Random().nextInt(names.size()));
                    new SkinManager(player, false).update(name);
                    names.remove(name);
                }
                sendChatMessage("All players skins were switched!");
                this.mc.displayGuiScreen(null);
                break;
            case 2:
                for (EntityOtherPlayerMP player : get()) {
                    new SkinManager(player, false).reset();
                }
                sendChatMessage("All players skins have been reset!");
                this.mc.displayGuiScreen(null);
                break;
            default:
                this.mc.displayGuiScreen(null);
                break;
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        SkinChangerMod.getInstance().getLoader().save();
    }

    private List<EntityOtherPlayerMP> get() {
        List<EntityOtherPlayerMP> ppl = new ArrayList<>();
        if (Minecraft.getMinecraft().theWorld != null && Minecraft.getMinecraft().theWorld.loadedEntityList != null) {
            for (Entity entity : Minecraft.getMinecraft().theWorld.loadedEntityList) {
                if (entity instanceof EntityOtherPlayerMP) {
                    ppl.add((EntityOtherPlayerMP) entity);
                }
            }
        }
        return ppl;
    }
}
