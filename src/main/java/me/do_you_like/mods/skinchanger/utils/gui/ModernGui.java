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

package me.do_you_like.mods.skinchanger.utils.gui;

import com.google.common.collect.Lists;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

import me.do_you_like.mods.skinchanger.SkinChangerMod;
import me.do_you_like.mods.skinchanger.utils.game.ChatColor;
import me.do_you_like.mods.skinchanger.utils.gui.impl.ModernButton;
import me.do_you_like.mods.skinchanger.utils.gui.impl.ModernTextBox;
import me.do_you_like.mods.skinchanger.utils.gui.lock.UILock;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import org.lwjgl.input.Mouse;

/**
 * ModernGui, a better-looking GuiScreen which has more optimizations and features than the normal GuiScreen
 *
 * @author boomboompower
 * @version 4.3
 * @since 2.0.0
 */
@SuppressWarnings("WeakerAccess") // This is an API class. Weaker Access doesn't matter
public abstract class ModernGui extends UILock implements UISkeleton {

    protected final Minecraft mc = Minecraft.getMinecraft();
    protected final FontRenderer fontRendererObj = this.mc.fontRendererObj;
    protected final SkinChangerMod mod = SkinChangerMod.getInstance();

    private final List<ModernTextBox> textList = Lists.newArrayList();
    private final List<ModernUIElement> modernList = Lists.newLinkedList();

    /**
     * Do not use this. Use {@link #modernList} instead since it contains support for all Modern Types.
     *
     * @deprecated Superseded by {@link #modernList}
     */
    @Deprecated
    private final List<ModernButton> buttonList = Collections.emptyList();

    private final List<InteractiveUIElement> selectedElements = Lists.newArrayList();

    protected float yTranslation = 0;

    @Override
    public final void initGui() {
        this.textList.clear();
        this.modernList.clear();

        onGuiOpen();
    }

    @Override
    public final void onGuiClosed() {
        onGuiClose();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        try {
            preRender(mouseX, mouseY);
        } catch (Exception ex) {
            drawString(this.fontRendererObj, "An error occurred during preRender();", 5, 5, Color.RED.getRGB());

            drawError(ex);

            return;
        }

        try {
            GlStateManager.pushMatrix();

            onRender(mouseX, mouseY, partialTicks);

            GlStateManager.popMatrix();
        } catch (Exception ex) {
            drawString(this.fontRendererObj, "An error occurred during onRender();", 5, 5, Color.RED.getRGB());

            drawError(ex);
        }

        try {
            for (ModernUIElement elements : this.modernList) {
                GlStateManager.pushMatrix();

                if (elements.isTranslatable()) {
                    GlStateManager.translate(0, this.yTranslation, 0);
                }

                elements.render(mouseX, mouseY, this.yTranslation);

                if (elements.isTranslatable()) {
                    GlStateManager.translate(0, -this.yTranslation, 0);
                }

                GlStateManager.popMatrix();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        for (ModernTextBox text : this.textList) {
            GlStateManager.pushMatrix();

            text.drawTextBox();

            GlStateManager.popMatrix();
        }

        try {
            postRender();
        } catch (Exception ex) {
            drawString(this.fontRendererObj, "An error occurred during postRender();", 5, 5, Color.RED.getRGB());

            drawError(ex);
        }
    }

    @Override
    protected final void keyTyped(char typedChar, int keyCode) {
        onKeyTyped(keyCode, typedChar);

        if (keyCode == 1) {
            close();
        } else {
            for (ModernTextBox text : textList) {
                text.textboxKeyTyped(typedChar, keyCode);
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        for (ModernUIElement draw : this.modernList) {
            if (draw instanceof InteractiveUIElement) {
                InteractiveUIElement element = (InteractiveUIElement) draw;

                if (!element.isEnabled()) {
                    continue;
                }

                // Patch for buttons :)
                if (!(draw instanceof ModernButton) && !element.isInside(mouseX, mouseY, this.yTranslation)) {
                    continue;
                } else if (draw instanceof ModernButton && !((ModernButton) draw).isHovered()) {
                    continue;
                }

                switch (mouseButton) {
                    case 0:
                        element.onLeftClick(mouseX, mouseY, this.yTranslation);

                        this.selectedElements.add(element);

                        if (element instanceof ModernButton) {
                            buttonPressed((ModernButton) element);
                        }
                        break;
                    case 1:
                        element.onRightClick(mouseX, mouseY, this.yTranslation);
                        break;
                    case 2:
                        element.onMiddleClick(mouseX, mouseY, this.yTranslation);
                        break;
                    default:
                        System.err.println("Unimplemented click (ID: " + mouseButton + "). Are you running the environment correctly?");
                }
            }
        }

        try {
            for (ModernTextBox text : this.textList) {
                text.mouseClicked(mouseX, mouseY, mouseButton);
            }
        } catch (NullPointerException exception) {
            exception.printStackTrace();
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
        this.modernList.clear();

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
        if (state != 0) {
            return;
        }

        this.selectedElements.forEach((d) -> d.onMouseReleased(mouseX, mouseY, this.yTranslation));
        this.selectedElements.clear();
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
     * Registers an element into the element loader.
     *
     * @param element the element to register. Can be a {@link ModernUIElement} or {@link ModernTextBox}
     */
    public final void registerElement(Object element) {
        if (element == null) {
            return;
        }

        if (element instanceof ModernUIElement) {
            this.modernList.add((ModernUIElement) element);
        } else if (element instanceof ModernTextBox) {
            this.textList.add((ModernTextBox) element);
        } else {
            System.err.println("Unable to register element (Elem: " + element.getClass() + ") as it was not a ModernDrawable.");
        }
    }

    /**
     * Draws multiple lines on the screen
     *
     * @param startingX the starting x position of the text
     * @param startingY the starting y position of the text
     * @param separation the Y value separation between each line
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
     * @param separation the Y value separation between each line
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

    /**
     * Displays this GUI to the Minecraft client
     *
     * If using a version before 1.8.8 use
     * FMLCommonHandler.instance().bus().register(this)
     */
    public final void display() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * Closes this GUI and makes the game go into focus if possible
     */
    public final void close() {
        this.mc.displayGuiScreen(null);

        if (this.mc.currentScreen == null) {
            this.mc.setIngameFocus();
        }
    }

    /**
     * Part of the {@link #display()} method. Again, if using a version below
     * 1.8.8 then the following code should be used
     * FMLCommonHandler.instance().bus().unregister(this)
     *
     * @param event the client event
     */
    @SubscribeEvent
    public final void onTick(TickEvent.ClientTickEvent event) {
        MinecraftForge.EVENT_BUS.unregister(this);
        Minecraft.getMinecraft().displayGuiScreen(this);
    }

    /**
     * Draws a rectangle with float values
     *
     * @param startX the starting x position of the rectangle
     * @param startY the starting y position of the rectangle
     * @param endX the ending x position of the rectangle
     * @param endY the ending y position of the rectangle
     * @param color the color of the rectangle
     */
    public static void drawRectF(float startX, float startY, float endX, float endY, int color) {
        drawRect((int) startX, (int) startY, (int) endX, (int) endY, color);
    }

    /**
     * Draws a hollow rectangle on the screen with float values
     *
     * @param startX the starting x position of the rectangle
     * @param startY the starting y position of the rectangle
     * @param endX the ending x position of the rectangle
     * @param endY the ending y position of the rectangle
     * @param color the color of the rectangle
     */
    public static void drawRectangleOutlineF(float startX, float startY, float endX, float endY, int color) {
        drawRectangleOutline((int) startX, (int) startY, (int) endX, (int) endY, color);
    }

    /**
     * Draws a hollow rectangle on the screen
     *
     * @param startX the starting x position of the rectangle
     * @param startY the starting y position of the rectangle
     * @param endX the ending x position of the rectangle
     * @param endY the ending y position of the rectangle
     * @param color the color of the rectangle
     */
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

    /**
     * Draws a horizontal line (just a 1x2 rectangle)
     *
     * @param startX the starting x position of the line
     * @param endX the ending x position of the line
     * @param y the y position of the line
     * @param color the color of the line
     */
    public static void drawHorizontalLine_(int startX, int endX, int y, int color) {
        if (endX < startX) {
            int i = startX;
            startX = endX;
            endX = i;
        }

        drawRect(startX, y, endX + 1, y + 1, color);
    }

    /**
     * Draws a vertical line (just a 2x1 rectangle)
     *
     * @param x the x position of the line
     * @param startY the starting y position of the line
     * @param endY the ending y position of the line
     * @param color the color of the line
     */
    public static void drawVerticalLine_(int x, int startY, int endY, int color) {
        if (endY < startY) {
            int i = startY;
            startY = endY;
            endY = i;
        }

        drawRect(x, startY + 1, x + 1, endY, color);
    }

    // =============================================== PRIVATE ================================================= //

    private void drawError(Exception ex) {
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
