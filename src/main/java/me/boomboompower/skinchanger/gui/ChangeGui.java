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

import me.boomboompower.skinchanger.SkinChanger;
import me.boomboompower.skinchanger.gui.utils.ModernButton;
import me.boomboompower.skinchanger.gui.utils.DrawingUtils;
import me.boomboompower.skinchanger.gui.utils.FakePlayerUtils;
import me.boomboompower.skinchanger.gui.utils.TextBox;
import me.boomboompower.skinchanger.utils.ChatColor;
import me.boomboompower.skinchanger.utils.GlobalUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;

public class ChangeGui extends GuiScreen {

    private FakePlayerUtils.FakePlayer fakePlayer = FakePlayerUtils.getFakePlayer();

    private TextBox textField;
    private ModernButton previewCape;

    private String message = "";

    public ChangeGui() {
        this("");
    }

    public ChangeGui(String message) {
        this.message = message;

        this.fakePlayer.setCapeLocation(null);
        this.fakePlayer.setSkinLocation(new ResourceLocation("textures/entity/steve.png"));
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        this.textField = new TextBox(0, this.width / 2 - 150, this.height / 2 - 22, 300, 20);

        this.buttonList.add(new ModernButton(1, this.width / 2 - 160, this.height / 2 + 26, 150, 20, "Preview skin"));
        this.buttonList.add(new ModernButton(2, this.width / 2 - 160, this.height / 2 + 50, 150, 20, "Reset skin"));
        this.buttonList.add(new ModernButton(3, this.width / 2 - 160, this.height / 2 + 74, 150, 20, "Confirm skin"));

        this.buttonList.add(this.previewCape = new ModernButton(4, this.width / 2 + 10, this.height / 2 + 26, 150, 20, "Preview cape"));
        this.buttonList.add(new ModernButton(5, this.width / 2 + 10, this.height / 2 + 50, 150, 20, "Reset cape"));
        this.buttonList.add(new ModernButton(6, this.width / 2 + 10, this.height / 2 + 74, 150, 20, "Add cape"));

        this.textField.setMaxStringLength(16);
        this.textField.setText(message);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawGuiBackground();

        previewCape.enabled = false;
        textField.setEnabled(true);
        textField.drawTextBox();

        super.drawScreen(mouseX, mouseY, partialTicks);

        fontRendererObj.drawString("Skin Settings", this.width / 2 - 118, this.height / 2 + 8, Color.WHITE.getRGB(), false);
        fontRendererObj.drawString("Cape Settings", this.width / 2 + 50, this.height / 2 + 8, Color.WHITE.getRGB(), false);

        fontRendererObj.drawString("Preview", (this.width / 2 - fontRendererObj.getStringWidth("Preview") / 2), this.height / 2 - 40, Color.WHITE.getRGB(), false);
        DrawingUtils.drawEntityOnScreen(this.width / 2, this.height / 2 - 50, 30, this.width / 2 - mouseX, (this.height / 2 - 90) - mouseY, fakePlayer);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 1:
                fakePlayer.setSkinLocation(SkinChanger.skinManager.getSkin(textField.getText()));
                break;
            case 2:
                SkinChanger.skinManager.reset(mc.thePlayer);
                sendChatMessage("Skin reset!");
                a();
                break;
            case 3:
                SkinChanger.skinManager.update(mc.thePlayer, textField.getText());
                sendChatMessage(String.format("Skin updated to %s!", ChatColor.GOLD + textField.getText() + ChatColor.GRAY));
                a();
                break;
            case 4:
                fakePlayer.setCapeLocation(SkinChanger.capeManager.getResourceLocation());
                break;
            case 5:
                SkinChanger.capeManager.removeCape(mc.thePlayer);
                sendChatMessage("Cape removed!");
                a();
                break;
            case 6:
                SkinChanger.capeManager.addCape(mc.thePlayer);
                sendChatMessage("You now have a cape!");
                a();
                break;
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        SkinChanger.loader.save();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void keyTyped(char c, int key) {
        if (key == 1) {
            mc.displayGuiScreen(null);
        } else {
            textField.textboxKeyTyped(c, key);
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int btn) {
        try {
            super.mouseClicked(x, y, btn);
            textField.mouseClicked(x, y, btn);
        } catch (Exception ex) {}
    }

    @Override
    public void sendChatMessage(String msg) {
        GlobalUtils.sendChatMessage(msg);
    }

    public void display() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        MinecraftForge.EVENT_BUS.unregister(this);
        Minecraft.getMinecraft().displayGuiScreen(this);
    }

    public void drawGuiBackground() {
        long lastPress = System.currentTimeMillis();
        int color = Math.min(255, (int) (2L * (System.currentTimeMillis() - lastPress)));
        Gui.drawRect(0, 0, width, height, 2013265920 + (color << 16) + (color << 8) + color);
    }

    private void a() {
        Minecraft.getMinecraft().displayGuiScreen(null);
    }
}
