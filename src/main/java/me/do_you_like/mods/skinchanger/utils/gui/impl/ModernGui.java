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

package me.do_you_like.mods.skinchanger.utils.gui.impl;

import com.google.common.collect.Lists;

import java.awt.Color;
import java.util.List;

import me.do_you_like.mods.skinchanger.SkinChangerMod;
import me.do_you_like.mods.skinchanger.utils.game.ChatColor;
import me.do_you_like.mods.skinchanger.utils.general.XYPosition;
import me.do_you_like.mods.skinchanger.utils.gui.ModernDrawable;
import me.do_you_like.mods.skinchanger.utils.gui.UISkeleton;
import me.do_you_like.mods.skinchanger.utils.gui.lock.UILock;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import org.lwjgl.input.Mouse;

/**
 * ModernGui, a better-looking GuiScreen which has more optimizations and features than the normal GuiScreen
 *
 * @author boomboompower
 * @version 4.0
 */
@SuppressWarnings("WeakerAccess") // This is an API class. Weaker Access doesn't matter
public abstract class ModernGui extends UILock implements UISkeleton {

    protected final Minecraft mc = Minecraft.getMinecraft();
    protected final FontRenderer fontRendererObj = this.mc.fontRendererObj;
    protected final SkinChangerMod mod = SkinChangerMod.getInstance();

    protected List<ModernTextBox> textList = Lists.newArrayList();
    protected List<ModernButton> buttonList = Lists.newArrayList();
    protected List<ModernHeader> headerList = Lists.newArrayList();
    protected List<ModernSlider> sliderList = Lists.newArrayList();

    private ModernButton selectedButton;
    private ModernSlider selectedSlider;

    protected int yTranslation = 0;

    @Override
    public final void initGui() {
        this.textList.clear();
        this.buttonList.clear();
        this.sliderList.clear();
        this.headerList.clear();

        onGuiOpen();
    }

    @Override
    public final void onGuiClosed() {
        onGuiClose();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        try {
            XYPosition position = preRender(mouseX, mouseY);

            if (position != null) {
                mouseX = position.getX_Int();
                mouseY = position.getY_Int();
            }
        } catch (Exception ex) {
            drawString(this.fontRendererObj, "An error occurred during preRender();", 5, 5, Color.RED.getRGB());

            int startCount = 0;

            if (ex.getMessage() != null) {
                startCount = 1;

                drawString(this.fontRendererObj, ex.getMessage(), 10, 16, Color.RED.getRGB());
            }

            for (int i = startCount; i < ex.getStackTrace().length; i++) {
                StackTraceElement element = ex.getStackTrace()[i];

                drawString(this.fontRendererObj, element.toString(), 10, 16 + (i * 12), Color.RED.getRGB());
            }

            return;
        }

        try {
            GlStateManager.pushMatrix();

            onRender(mouseX, mouseY, partialTicks);

            GlStateManager.popMatrix();
        } catch (Exception ex) {
            drawString(this.fontRendererObj, "An error occurred during onRender();", 5, 5, Color.RED.getRGB());

            int startCount = 0;

            if (ex.getMessage() != null) {
                startCount = 1;

                drawString(this.fontRendererObj, ex.getMessage(), 10, 16, Color.RED.getRGB());
            }

            for (int i = startCount; i < ex.getStackTrace().length; i++) {
                StackTraceElement element = ex.getStackTrace()[i];

                drawString(this.fontRendererObj, element.toString(), 10, 16 + (i * 12), Color.RED.getRGB());
            }

            return;
        }

        GlStateManager.pushMatrix();

        for (ModernButton button : this.buttonList) {
            if (button.isTranslatable()) {
                GlStateManager.translate(0, this.yTranslation, 0);
            }

            button.render(mouseX, mouseY, this.yTranslation);

            if (button.isTranslatable()) {
                GlStateManager.translate(0, -this.yTranslation, 0);
            }
        }

        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();

        for (GuiLabel label : this.labelList) {
            label.drawLabel(this.mc, mouseX, mouseY);
        }

        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();

        for (ModernTextBox text : this.textList) {
            text.drawTextBox();
        }

        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();

        for (ModernSlider slider : this.sliderList) {
            if (slider.isTranslatable()) {
                GlStateManager.translate(0, this.yTranslation, 0);
            }

            slider.render(mouseX, mouseY, this.yTranslation);

            if (slider.isTranslatable()) {
                GlStateManager.translate(0, -this.yTranslation, 0);
            }
        }

        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();

        for (ModernHeader header : this.headerList) {
            GlStateManager.pushMatrix();

            if (header.isTranslatable()) {
                GlStateManager.translate(0, this.yTranslation, 0);
            }

            header.render(mouseX, mouseY, this.yTranslation);

            if (header.isTranslatable()) {
                GlStateManager.translate(0, -this.yTranslation, 0);
            }

            GlStateManager.popMatrix();
        }

        GlStateManager.popMatrix();

        try {
            postRender();
        } catch (Exception ex) {
            drawString(this.fontRendererObj, "An error occurred during postRender();", 5, 5, Color.RED.getRGB());

            int startCount = 0;

            if (ex.getMessage() != null) {
                startCount = 1;

                drawString(this.fontRendererObj, ex.getMessage(), 10, 16, Color.RED.getRGB());
            }

            for (int i = startCount; i < ex.getStackTrace().length; i++) {
                StackTraceElement element = ex.getStackTrace()[i];

                drawString(this.fontRendererObj, element.toString(), 10, 16 + (i * 12), Color.RED.getRGB());
            }
        }
    }

    @Override
    protected final void keyTyped(char typedChar, int keyCode) {
        onKeyTyped(keyCode, typedChar);

        if (keyCode == 1) {
            this.mc.displayGuiScreen(null);

            if (this.mc.currentScreen == null) {
                this.mc.setIngameFocus();
            }
        } else {
            for (ModernTextBox text : textList) {
                text.textboxKeyTyped(typedChar, keyCode);
            }
        }
    }

    protected XYPosition preMouseClicked(int mouseX, int mouseY, int mouseButton) {
        return null;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        XYPosition position = preMouseClicked(mouseX, mouseY, mouseButton);

        if (position != null) {
            mouseX = position.getX_Int();
            mouseY = position.getY_Int();
        }

        if (mouseButton == 0) {
            for (ModernHeader header : this.headerList) {
                if (header.getSubDrawables().size() > 0) {
                    for (ModernDrawable drawable : header.getSubDrawables()) {
                        if (drawable instanceof ModernButton) {
                            ModernButton button = (ModernButton) drawable;

                            if (button.mousePressed(this.mc, mouseX, mouseY)) {
                                this.selectedButton = button;

                                button.playPressSound(this.mc.getSoundHandler());

                                buttonPressed(button);
                            }
                        } else if (drawable instanceof ModernCheckbox) {
                            ModernCheckbox checkbox = (ModernCheckbox) drawable;

                            if (checkbox.isInside(mouseX, mouseY)) {
                                checkbox.onClick();
                            }
                        }
                    }
                }
            }

            for (ModernButton button : this.buttonList) {
                if (button.mousePressed(this.mc, mouseX, mouseY)) {
                    this.selectedButton = button;

                    button.playPressSound(this.mc.getSoundHandler());

                    this.buttonPressed(button);
                }
            }
        }

        if (mouseButton == 1) {
            for (ModernHeader header : this.headerList) {
                if (header.getSubDrawables().size() > 0) {
                    for (ModernDrawable drawable : header.getSubDrawables()) {
                        if (drawable instanceof ModernButton) {
                            ModernButton button = (ModernButton) drawable;

                            if (button.mousePressed(this.mc, mouseX, mouseY)) {
                                rightClicked(button);
                            }
                        }
                    }
                }
            }

            for (ModernButton button : this.buttonList) {
                if (button != null) {
                    if (button.mousePressed(this.mc, mouseX, mouseY)) {
                        this.rightClicked(button);
                    }
                }
            }
        }

        for (ModernTextBox text : this.textList) {
            text.mouseClicked(mouseX, mouseY, mouseButton);
        }

        for (ModernSlider slider : this.sliderList) {
            if (slider.onMousePressed(mouseX, mouseY)) {
                this.selectedSlider = slider;
            }
        }
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        // Required for compatibility
        super.mc = mc;

        this.itemRender = mc.getRenderItem();
        this.width = width;
        this.height = height;

        this.textList.clear();
        this.buttonList.clear();
        this.headerList.clear();

        initGui();
    }

    @Override
    public final void updateScreen() {
        for (ModernTextBox textBox : this.textList) {
            textBox.updateCursorCounter();
        }
    }

    @Override
    protected final void mouseReleased(int mouseX, int mouseY, int state) {
        if (this.selectedButton != null && state == 0) {
            this.selectedButton.mouseReleased(mouseX, mouseY);
            this.selectedButton = null;
        }

        if (this.selectedSlider != null && state == 0) {
            this.selectedSlider.mouseReleased(mouseX, mouseY);
            this.selectedSlider = null;
        }
    }

    @Override
    public final void onMouse() {
        int i = Mouse.getEventDWheel();

        if (i < 0) {
            onScrollDown();
        } else if (i > 0) {
            onScrollUp();
        }
    }

    /**
     * Draws multiple lines on the screen
     *
     * @param startingX the starting x position of the text
     * @param startingY the starting y position of the text
     * @param separation the Y valye  separatation between each line
     * @param lines the lines which will be drawn
     */
    public void writeInformation(int startingX, int startingY, int separation, String... lines) {
        writeInformation(startingX, startingY, separation, true, lines);
    }

    /**
     * Draws multiple lines on the screen
     *
     * @param startingX the starting x position of the text
     * @param startingY the starting y position of the text
     * @param separation the Y valye separatation between each line
     * @param centered true if the text being rendered should be rendered as a centered string
     * @param lines the lines which will be drawn
     */
    public void writeInformation(int startingX, int startingY, int separation, boolean centered, String... lines) {
        if (lines == null || lines.length == 0) {
            return;
        }

        // Loop through the lines
        for (String line : lines) {
            // Null components will be treated as an empty string
            if (line == null) {
                line = "";
            }

            if (centered) {
                drawCenteredString(this.fontRendererObj, ChatColor.translateAlternateColorCodes('&', line), startingX, startingY, Color.WHITE.getRGB());
            } else {
                drawString(this.fontRendererObj, ChatColor.translateAlternateColorCodes('&', line), startingX, startingY, Color.WHITE.getRGB());
            }

            startingY += separation;
        }
    }

    public final void display() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public final void onTick(TickEvent.ClientTickEvent event) {
        MinecraftForge.EVENT_BUS.unregister(this);
        Minecraft.getMinecraft().displayGuiScreen(this);
    }

    protected void forceSet(ModernDrawable drawable) {
        if (drawable instanceof ModernSlider) {
            if (this.selectedSlider != null) {
                this.selectedSlider.mouseReleased(0, 0);
            }

            this.selectedSlider = (ModernSlider) drawable;
        } else if (drawable instanceof ModernButton) {
            if (this.selectedButton != null) {
                this.selectedButton.mouseReleased(0, 0);
            }

            this.selectedButton = (ModernButton) drawable;
        }
    }

    public static void drawRectF(float startX, float startY, float endX, float endY, int color) {
        drawRect((int) startX, (int) startY, (int) endX, (int) endY, color);
    }

    public static void drawRectangleOutlineF(float startX, float startY, float endX, float endY, int color) {
        drawRectangleOutline((int) startX, (int) startY, (int) endX, (int) endY, color);
    }

    public static void drawRectangleOutline(int startX, int startY, int endX, int endY, int color) {
        // Top
        drawHorizontalLine_(startX, endX, startY, color);

        // Right
        drawVerticalLine_(endX, startY, endY, color);

        // Bottom
        drawHorizontalLine_(startX, endX, endY, color);

        // Left
        drawVerticalLine_(startX, startY, endY, color);
    }

    public static void drawHorizontalLine_(int startX, int endX, int y, int color) {
        if (endX < startX) {
            int i = startX;
            startX = endX;
            endX = i;
        }

        drawRect(startX, y, endX + 1, y + 1, color);
    }

    public static void drawVerticalLine_(int x, int startY, int endY, int color) {
        if (endY < startY) {
            int i = startY;
            startY = endY;
            endY = i;
        }

        drawRect(x, startY + 1, x + 1, endY, color);
    }

}
