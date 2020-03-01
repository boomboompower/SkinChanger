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
import me.boomboompower.skinchanger.mixins.Tweaker;
import me.boomboompower.skinchanger.utils.fake.FakePlayer;
import me.boomboompower.skinchanger.gui.utils.ModernButton;
import me.boomboompower.skinchanger.gui.utils.ModernGui;
import me.boomboompower.skinchanger.gui.utils.ModernTextBox;
import me.boomboompower.skinchanger.utils.ChatColor;

import me.boomboompower.skinchanger.utils.models.skins.PlayerSkinType;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class SettingsGui extends ModernGui {

    private final SkinChangerMod mod;
    
    private static final FakePlayer fakePlayer = new FakePlayer();
    private static final ResourceLocation defaultCape = new ResourceLocation("assets/skinchanger/cape.png");

    private static ModernTextBox textField;

    private String message = "";

    private boolean previewCape = false;

    public SettingsGui(SkinChangerMod modIn) {
        this(modIn, "");
    }

    public SettingsGui(SkinChangerMod modIn, String message) {
        this.mod = modIn;
        this.message = message;

        this.mod.getSkinManager().updatePlayer(null);
        
        fakePlayer.getPlayerInfo().setLocationSkin(DefaultPlayerSkin.getDefaultSkinLegacy());
        fakePlayer.getPlayerInfo().setLocationCape(null);
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        this.textList.add(textField = new ModernTextBox(0, this.width / 2 - 150, this.height / 2 - 22, 300, 20));

        this.buttonList.add(new ModernButton(1, this.width / 2 - 160, this.height / 2 + 26, 150, 20, "Preview skin"));
        this.buttonList.add(new ModernButton(2, this.width / 2 - 160, this.height / 2 + 50, 150, 20, "Reset skin"));
        this.buttonList.add(new ModernButton(3, this.width / 2 - 160, this.height / 2 + 74, 150, 20, "Confirm skin"));

        this.buttonList.add(new ModernButton(4, this.width / 2 + 10, this.height / 2 + 26, 150, 20, "Preview cape"));
        this.buttonList.add(new ModernButton(5, this.width / 2 + 10, this.height / 2 + 50, 150, 20, "Reset cape"));
        this.buttonList.add(new ModernButton(6, this.width / 2 + 10, this.height / 2 + 74, 150, 20, "Add cape"));

        this.buttonList.add(new ModernButton(7, this.width - 105, 10, 85, 20, "Experimental"));
        this.buttonList.add(new ModernButton(8, this.width - 105, 30, 85, 20, "Skin Type: " + this.mod.getSkinManager().getSkinType().getDisplayName()));

        textField.setMaxStringLength(1000);
        textField.setText(this.message);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        this.fontRendererObj.drawString("Skin Settings", this.width / 2 - 118, this.height / 2 + 8, Color.WHITE.getRGB(), false);
        this.fontRendererObj.drawString("Cape Settings", this.width / 2 + 50, this.height / 2 + 8, Color.WHITE.getRGB(), false);

        drawEntityOnScreen(this.width / 2, this.height / 2 - 45, 35, this.width / 2 - mouseX, (this.height / 2 - 90) - mouseY, fakePlayer, this.previewCape);

        if (this.previewCape) {
            drawCenteredString(this.mc.fontRendererObj,"Preview Cape", this.width / 2, this.height / 2 - 40, Color.WHITE.getRGB());
        } else {
            drawCenteredString(this.mc.fontRendererObj,"Preview Skin", this.width / 2, this.height / 2 - 40, Color.WHITE.getRGB());
        }
        
        if (this.previewCape) {
            drawCenteredString(this.mc.fontRendererObj,ChatColor.WHITE + "Hold Left-Alt to flip the cape!", this.width / 2, this.height / 2 + 100, Color.WHITE.getRGB());
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void buttonPressed(ModernButton button) {
        if (button.id == 8) {
            System.out.println(String.format("Current index: %s / %s", this.mod.getSkinManager().getSkinType().ordinal(), PlayerSkinType.values().length));

            this.mod.getSkinManager().setSkinType(this.mod.getSkinManager().getSkinType().getNextSkin());

            PlayerSkinType type = this.mod.getSkinManager().getSkinType();
            button.setText("Skin Type: " + type.getDisplayName());

            return;
        }

//        if (!textField.getText().isEmpty()) {
//            this.mod.getWebsiteUtils().runAsync(() -> {
//                if (button.id == 1) {
//                    String id = this.mod.getMojangHooker().getIdFromUsername(textField.getText());
//
//                    textField.setText(this.mod.getMojangHooker().hasSlimSkin(id) + "");
//                } else if (button.id == 2) {
//                    String id = this.mod.getMojangHooker().getIdFromUsername(textField.getText());
//
//                    textField.setText(this.mod.getMojangHooker().getIdFromUsername(id));
//                } else if (button.id == 3) {
//                    fakePlayer.getPlayerInfo().setLocationCape(this.mod.getMojangHooker().getSkinFromId(this.mod.getMojangHooker().getIdFromUsername("boomboompower")));
//                }
//            });
//        }

        switch (button.id) {
            case 1:
                this.previewCape = false;
                fakePlayer.getPlayerInfo().setLocationCape(null);

                if (!textField.getText().isEmpty() && textField.getText().length() >= 2) {
                    ResourceLocation loc = this.mod.getSkinManager().getSkin(textField.getText());

                    fakePlayer.getPlayerInfo().setLocationSkin(loc);
                }
                break;
            case 2:
                if (Tweaker.MIXINS_ENABLED) {

                } else {
                    this.mod.getSkinManager().reset();
                }
                sendChatMessage("Your skin has been reset!");
                this.mc.displayGuiScreen(null);
                break;
            case 3:
                if (!textField.getText().isEmpty() && textField.getText().length() >= 2) {
                    this.mod.getCapeManager().setExperimental(false);
                    this.mod.getSkinManager().update(textField.getText());
                    sendChatMessage(String.format("Your skin has been updated to %s!", ChatColor.GOLD + textField.getText() + ChatColor.GRAY));
                } else {
                    sendChatMessage("Not enough characters provided");
                    sendChatMessage("Use a name between 2 and 16 characters!");
                }
                this.mc.displayGuiScreen(null);
                break;
            case 4:
                this.previewCape = true;

                fakePlayer.getPlayerInfo().setLocationCape(defaultCape);
                //this.fakePlayerCapeManager.addCape();
                break;
            case 5:
                this.mod.getCapeManager().removeCape();
                sendChatMessage("Your cape has been removed!");
                this.mc.displayGuiScreen(null);
                break;
            case 6:
                this.mod.getCapeManager().setExperimental(false);
                this.mod.getCapeManager().addCape();
                sendChatMessage("You now have a cape!");
                this.mc.displayGuiScreen(null);
                break;
            case 7:
                this.mc.displayGuiScreen(new ExperimentalGui(this.mod));
                break;
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        this.mod.getLoader().save();
    }
}
