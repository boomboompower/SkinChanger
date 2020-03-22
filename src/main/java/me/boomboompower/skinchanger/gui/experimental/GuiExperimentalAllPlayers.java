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

import me.do_you_like.mods.skinchanger.methods.impl.mixins.SkinChangerTweaker;

import me.do_you_like.mods.skinchanger.utils.game.ChatColor;
import me.do_you_like.mods.skinchanger.utils.gui.impl.ModernButton;
import me.do_you_like.mods.skinchanger.utils.gui.ModernGui;
import me.do_you_like.mods.skinchanger.utils.gui.impl.ModernTextBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("ALL")
@Deprecated
/**
 * @deprecated to be removed
 */
public class GuiExperimentalAllPlayers extends ModernGui {

    public static ResourceLocation forcedAllSkins;

    private final SkinChangerModOld mod;
    
    private ModernTextBox textField;

    private ModernButton resetButton;

    public GuiExperimentalAllPlayers(SkinChangerModOld theMod) {
        this.mod = theMod;
    }

    @Override
    public void onGuiOpen() {
        Keyboard.enableRepeatEvents(true);

        registerElement(this.textField = new ModernTextBox(0, this.width / 2 - 150, this.height / 2 - 22, 300, 20));

        registerElement(new ModernButton(0, this.width / 2 - 75, this.height / 2 + 26, 150, 20, "Give skin to everyone"));
        registerElement(this.resetButton = new ModernButton(2, this.width / 2 - 75, this.height / 2 + 74, 150, 20, "Reset all skins"));

        this.resetButton.setEnabledColor(new Color(255, 0, 0, 75));
    }

    @Override
    public void onRender(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        super.drawScreen(mouseX, mouseY, partialTicks);

        if (this.resetButton.isHovered()) {
            drawHoveringText(Arrays.asList("This button is dangerous and", ChatColor.DARK_RED.toString() + ChatColor.BOLD + "may" + ChatColor.RESET + " be bad for your game"), mouseX, mouseY);
        }
    }

    @Override
    public void buttonPressed(ModernButton button) {
        switch (button.getId()) {
            case 0:
                if (SkinChangerTweaker.MIXINS_ENABLED) {
                    forcedAllSkins = this.mod.getSkinManager().getSkin(this.textField.getText());
                } else {
                    for (EntityOtherPlayerMP player : get()) {
                        //new SkinManager(this.mod.getMojangHooker(), player, false).update(this.textField.getText());
                    }
                }

                this.mc.displayGuiScreen(null);
                break;

            case 2:
                if (SkinChangerTweaker.MIXINS_ENABLED) {
                    forcedAllSkins = null;
                } else {
                    for (EntityOtherPlayerMP player : get()) {
                        //new SkinManager(this.mod.getMojangHooker(), player, false).reset();
                    }
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
    public void onGuiClose() {
        Keyboard.enableRepeatEvents(false);
        SkinChangerModOld.getInstance().getLoader().save();
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
