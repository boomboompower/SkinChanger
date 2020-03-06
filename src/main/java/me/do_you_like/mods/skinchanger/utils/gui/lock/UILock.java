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

package me.do_you_like.mods.skinchanger.utils.gui.lock;

import java.awt.Color;
import java.io.IOException;

import me.boomboompower.skinchanger.utils.ChatColor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ChatComponentText;

/**
 * Locks the UI methods. Declutters the subclasses method list.
 */
public class UILock extends GuiScreen {

    @Override
    public final void drawDefaultBackground() {
        Gui.drawRect(0, 0, this.width, this.height, new Color(2, 2, 2, 120).getRGB());
    }

    @Override
    public final void drawCenteredString(FontRenderer fontRendererIn, String text, int x, int y, int color) {
        fontRendererIn.drawString(text, (float) (x - fontRendererIn.getStringWidth(text) / 2), (float) y, color, false);
    }

    public final void drawCenteredString(FontRenderer fontRendererIn, String text, int x, int y, int color, boolean shadow) {
        fontRendererIn.drawString(text, (float) (x - fontRendererIn.getStringWidth(text) / 2), (float) y, color, shadow);
    }

    @Override
    public final void drawString(FontRenderer fontRendererIn, String text, int x, int y, int color) {
        fontRendererIn.drawString(text, (float) x, (float) y, color, false);
    }

    @Override
    public final boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public final void sendChatMessage(String msg) {
        this.mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(
            ChatColor.AQUA + "SkinChanger" + ChatColor.GOLD + " > " + ChatColor.GRAY + msg));
    }

    @Override
    public final void confirmClicked(boolean result, int id) {
    }

    @Override
    public final void sendChatMessage(String msg, boolean addToChat) {
        sendChatMessage(msg);
    }

    @Override
    public final void drawBackground(int tint) {
        super.drawBackground(tint);
    }

    @Override
    public final void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
        super.drawTexturedModalRect(x, y, textureX, textureY, width, height);
    }

    @Override
    public final void drawTexturedModalRect(float xCoord, float yCoord, int minU, int minV, int maxU, int maxV) {
        super.drawTexturedModalRect(xCoord, yCoord, minU, minV, maxU, maxV);
    }

    @Override
    public final void drawTexturedModalRect(int xCoord, int yCoord, TextureAtlasSprite textureSprite, int widthIn, int heightIn) {
        super.drawTexturedModalRect(xCoord, yCoord, textureSprite, widthIn, heightIn);
    }

    @Override
    public final void drawWorldBackground(int tint) {
        super.drawWorldBackground(tint);
    }

    @Override
    public final void handleInput() throws IOException {
        super.handleInput();
    }

    @Override
    public final void handleKeyboardInput() throws IOException {
        super.handleKeyboardInput();
    }

    @Override
    public final void handleMouseInput() throws IOException {
        super.handleMouseInput();
    }

    @Override
    public final void onResize(Minecraft mcIn, int w, int h) {
        super.onResize(mcIn, w, h);
    }

    @Override
    protected final void actionPerformed(GuiButton button) {
    }

    @Override
    protected final void setText(String newChatText, boolean shouldOverwrite) {
    }

    @Override
    protected final void drawCreativeTabHoveringText(String tabName, int mouseX, int mouseY) {
    }

    @Override
    protected final void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
        super.drawGradientRect(left, top, right, bottom, startColor, endColor);
    }
}
