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
import me.boomboompower.skinchanger.capes.CapeManager;
import me.boomboompower.skinchanger.gui.utils.FakePlayerUtils;
import me.boomboompower.skinchanger.gui.utils.ModernButton;
import me.boomboompower.skinchanger.gui.utils.ModernGui;
import me.boomboompower.skinchanger.gui.utils.ModernTextBox;
import me.boomboompower.skinchanger.skins.SkinManager;
import me.boomboompower.skinchanger.utils.ChatColor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import org.lwjgl.input.Keyboard;

import java.awt.*;

public class SettingsGui extends ModernGui {

    private FakePlayerUtils.FakePlayer fakePlayer = FakePlayerUtils.getFakePlayer();
    private SkinManager fakePlayerSkinManager = new SkinManager(fakePlayer, false);
    private CapeManager fakePlayerCapeManager = new CapeManager(fakePlayer, false);

    private ModernTextBox textField;

    private String message = "";

    private boolean previewCape = false;

    public SettingsGui() {
        this("");
    }

    public SettingsGui(String message) {
        this.message = message;

        SkinChangerMod.getInstance().getSkinManager().updatePlayer(null);
        this.fakePlayerSkinManager.replaceSkin(DefaultPlayerSkin.getDefaultSkinLegacy());
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        this.textList.add(this.textField = new ModernTextBox(0, this.width / 2 - 150, this.height / 2 - 22, 300, 20));

        this.buttonList.add(new ModernButton(1, this.width / 2 - 160, this.height / 2 + 26, 150, 20, "Preview skin"));
        this.buttonList.add(new ModernButton(2, this.width / 2 - 160, this.height / 2 + 50, 150, 20, "Reset skin"));
        this.buttonList.add(new ModernButton(3, this.width / 2 - 160, this.height / 2 + 74, 150, 20, "Confirm skin"));

        this.buttonList.add(new ModernButton(4, this.width / 2 + 10, this.height / 2 + 26, 150, 20, "Preview cape"));
        this.buttonList.add(new ModernButton(5, this.width / 2 + 10, this.height / 2 + 50, 150, 20, "Reset cape"));
        this.buttonList.add(new ModernButton(6, this.width / 2 + 10, this.height / 2 + 74, 150, 20, "Add cape"));

        this.buttonList.add(new ModernButton(7, this.width - 105, 10, 85, 20, "Experimental"));

        this.textField.setMaxStringLength(16);
        this.textField.setText(this.message);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        fontRendererObj.drawString("Skin Settings", this.width / 2 - 118, this.height / 2 + 8, Color.WHITE.getRGB(), false);
        fontRendererObj.drawString("Cape Settings", this.width / 2 + 50, this.height / 2 + 8, Color.WHITE.getRGB(), false);

        drawEntityOnScreen(this.width / 2, this.height / 2 - 45, 35, this.width / 2 - mouseX, (this.height / 2 - 90) - mouseY, this.fakePlayer, this.previewCape);

        if (this.previewCape) {
            drawCenteredString(this.mc.fontRendererObj,"Preview Cape", this.width / 2, this.height / 2 - 40, Color.WHITE.getRGB());
        } else {
            drawCenteredString(this.mc.fontRendererObj,"Preview Skin", this.width / 2, this.height / 2 - 40, Color.WHITE.getRGB());
        }
        if (SkinChangerMod.getInstance().getWebsiteUtils().isDisabled()) {
            drawCenteredString(this.mc.fontRendererObj,ChatColor.RED + "The mod is currently disabled and will not work!", this.width / 2, this.height / 2 + 100, Color.WHITE.getRGB());
        } else {
            if (this.previewCape) {
                drawCenteredString(this.mc.fontRendererObj,ChatColor.WHITE + "Hold Left-Alt to flip the cape!", this.width / 2, this.height / 2 + 100, Color.WHITE.getRGB());
            }
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
            case 1:
                this.previewCape = false;
                this.fakePlayerCapeManager.removeCape();
                if (!this.textField.getText().isEmpty() && this.textField.getText().length() >= 2) {
                    this.fakePlayerSkinManager.update(this.textField.getText());
                }
                break;
            case 2:
                SkinChangerMod.getInstance().getSkinManager().reset();
                sendChatMessage("Your skin has been reset!");
                this.mc.displayGuiScreen(null);
                break;
            case 3:
                if (!this.textField.getText().isEmpty() && this.textField.getText().length() >= 2) {
                    SkinChangerMod.getInstance().getCapeManager().setExperimental(false);
                    SkinChangerMod.getInstance().getSkinManager().update(this.textField.getText());
                    sendChatMessage(String.format("Your skin has been updated to %s!", ChatColor.GOLD + this.textField.getText() + ChatColor.GRAY));
                } else {
                    sendChatMessage("Not enough characters provided");
                    sendChatMessage("Use a name between 2 and 16 characters!");
                }
                this.mc.displayGuiScreen(null);
                break;
            case 4:
                this.previewCape = true;
                this.fakePlayerCapeManager.addCape();
                break;
            case 5:
                SkinChangerMod.getInstance().getCapeManager().removeCape();
                sendChatMessage("Your cape has been removed!");
                this.mc.displayGuiScreen(null);
                break;
            case 6:
                SkinChangerMod.getInstance().getCapeManager().setExperimental(false);
                SkinChangerMod.getInstance().getCapeManager().addCape();
                sendChatMessage("You now have a cape!");
                this.mc.displayGuiScreen(null);
                break;
            case 7:
                if (SkinChangerMod.getInstance().getWebsiteUtils().isExperimentsEnabled()) {
                    this.mc.displayGuiScreen(new ExperimentalGui());
                } else {
                    sendChatMessage(ChatColor.RED + "Experimental features are currently disabled!");
                    this.mc.displayGuiScreen(null);
                }
                break;
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        SkinChangerMod.getInstance().getLoader().save();
    }

    public void display() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        MinecraftForge.EVENT_BUS.unregister(this);
        Minecraft.getMinecraft().displayGuiScreen(this);
    }
}
