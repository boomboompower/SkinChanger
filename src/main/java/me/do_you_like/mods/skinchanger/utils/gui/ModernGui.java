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
import me.do_you_like.mods.skinchanger.utils.general.XYPosition;
import me.do_you_like.mods.skinchanger.utils.gui.impl.ModernButton;
import me.do_you_like.mods.skinchanger.utils.gui.impl.ModernSlider;
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
 * @version 4.0
 */
@SuppressWarnings("WeakerAccess") // This is an API class. Weaker Access doesn't matter
public abstract class ModernGui extends UILock implements UISkeleton {

    protected final Minecraft mc = Minecraft.getMinecraft();
    protected final FontRenderer fontRendererObj = this.mc.fontRendererObj;
    protected final SkinChangerMod mod = SkinChangerMod.getInstance();

    private List<ModernTextBox> textList = Lists.newArrayList();
    private List<ModernDrawable> modernList = Lists.newLinkedList();

    /**
     * Do not use this. Use {@link #modernList} instead since it contains support for all Modern Types.
     *
     * @deprecated Superseded by {@link #modernList}
     */
    @SuppressWarnings("DeprecatedIsStillUsed") // Don't tell me, tell them :)))
    @Deprecated
    private List<ModernButton> buttonList = Collections.emptyList();

    private ModernButton selectedButton;
    private ModernSlider selectedSlider;

    protected int yTranslation = 0;

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

        for (ModernDrawable drawable : this.modernList) {
            GlStateManager.pushMatrix();

            if (drawable.isTranslatable()) {
                GlStateManager.translate(0, this.yTranslation, 0);
            }

            drawable.render(mouseX, mouseY, this.yTranslation);

            if (drawable.isTranslatable()) {
                GlStateManager.translate(0, -this.yTranslation, 0);
            }

            GlStateManager.popMatrix();
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

        for (ModernDrawable draw : this.modernList) {
            if (draw.isInside(mouseX, mouseY, this.yTranslation)) {
                if (draw instanceof InteractiveDrawable) {
                    InteractiveDrawable drawable = (InteractiveDrawable) draw;

                    switch (mouseButton) {
                        case 0:
                            drawable.onLeftClick(mouseX, mouseY, this.yTranslation);

                            if (drawable instanceof ModernButton) {
                                this.selectedButton = (ModernButton) drawable;

                                buttonPressed((ModernButton) drawable);
                            } else if (drawable instanceof ModernSlider) {
                                this.selectedSlider = (ModernSlider) drawable;
                            }
                            break;
                        case 1:
                            drawable.onRightClick(mouseX, mouseY, this.yTranslation);
                            break;
                        case 2:
                            drawable.onMiddleClick(mouseX, mouseY, this.yTranslation);
                            break;
                        default:
                            System.err.println("Unimplemented click (ID: " + mouseButton + "). Are you running the environment correctly?");
                    }
                }
            }
        }

        for (ModernTextBox text : this.textList) {
            text.mouseClicked(mouseX, mouseY, mouseButton);
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
        this.buttonList.clear();

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
            this.selectedButton.onMouseReleased(mouseX, mouseY, this.yTranslation);
            this.selectedButton = null;
        }

        if (this.selectedSlider != null && state == 0) {
            this.selectedSlider.onMouseReleased(mouseX, mouseY, this.yTranslation);
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
     * Registers an element into the element loader.
     *
     * @param drawable the drawable to register. Can be a {@link ModernDrawable} or {@link ModernTextBox}
     */
    public final void registerElement(Object drawable) {
        if (drawable == null) {
            return;
        }

        if (drawable instanceof ModernDrawable) {
            this.modernList.add((ModernDrawable) drawable);
        } else if (drawable instanceof ModernTextBox) {
            this.textList.add((ModernTextBox) drawable);
        } else {
            System.err.println("Unable to register element (Elem: " + drawable.getClass() + ") as it was not a ModernDrawable.");
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
                this.selectedSlider.onMouseReleased(0, 0, this.yTranslation);
            }

            this.selectedSlider = (ModernSlider) drawable;
        } else if (drawable instanceof ModernButton) {
            if (this.selectedButton != null) {
                this.selectedButton.onMouseReleased(0, 0, this.yTranslation);
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
