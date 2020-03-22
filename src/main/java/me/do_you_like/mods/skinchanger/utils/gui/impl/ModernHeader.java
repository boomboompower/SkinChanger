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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import me.do_you_like.mods.skinchanger.utils.general.Prerequisites;
import me.do_you_like.mods.skinchanger.utils.general.XYPosition;
import me.do_you_like.mods.skinchanger.utils.gui.InteractiveDrawable;
import me.do_you_like.mods.skinchanger.utils.gui.ModernDrawable;
import me.do_you_like.mods.skinchanger.utils.gui.ModernGui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

/**
 * A simple class for drawing headers. Useful for UI categories.
 *
 * @version 1.1
 * @author boomboompower
 */
public class ModernHeader extends Gui implements InteractiveDrawable {

    @Getter
    @Setter
    private int x;

    @Getter
    @Setter
    private int y;

    @Getter
    private String headerText;

    @Getter
    @Setter
    private float scaleSize;

    @Getter
    private boolean drawUnderline;

    @Getter
    @Setter
    private boolean visible = true;

    @Getter
    @Setter
    private boolean drawCentered = false;

    @Getter
    @Setter
    private Color headerColor;

    @Getter
    private List<ModernDrawable> subDrawables;

    @Getter
    @Setter
    private float offsetBetweenDrawables = 12;

    private int widthOfSub;

    private ScaledResolution scaledResolution;

    /**
     * Basic constructor for UI headers. Scale size is 1.5 of normal text. Draws an underline.
     *
     * @param x the x location of the header
     * @param y the y location of the header
     * @param header the text which will be rendered
     */
    public ModernHeader(int x, int y, String header) {
        this(x, y, header, 1.5F, true);
    }

    /**
     * Basic constructor for UI headers. Draws an underline.
     *
     * @param x the x location of the header
     * @param y the y location of the header
     * @param header the text which will be rendered
     * @param scaleSize the scale of the text. (scale > 1 means bigger)
     */
    public ModernHeader(int x, int y, String header, float scaleSize) {
        this(x, y, header, scaleSize, true);
    }

    /**
     * Basic constructor for UI headers. Draws an underline.
     *
     * @param x the x location of the header
     * @param y the y location of the header
     * @param header the text which will be rendered
     * @param scaleSize the scale of the text. (scale > 1 means bigger)
     */
    public ModernHeader(int x, int y, String header, int scaleSize) {
        this(x, y, header, scaleSize, true);
    }

    /**
     * Basic constructor for UI headers.
     *
     * @param x the x location of the header
     * @param y the y location of the header
     * @param header the text which will be rendered
     * @param scaleSize the scale of the text. (scale > 1 means bigger)
     * @param drawUnderline true if an underline should be drawn.
     */
    public ModernHeader(int x, int y, String header, float scaleSize, boolean drawUnderline) {
        this(x, y, header, scaleSize, drawUnderline, Color.WHITE);
    }

    /**
     * Basic constructor for UI headers.
     *
     * @param x the x location of the header
     * @param y the y location of the header
     * @param header the text which will be rendered
     * @param scaleSize the scale of the text. (scale > 1 means bigger)
     * @param drawUnderline true if an underline should be drawn.
     * @param color the color of which the elements will be drawn.
     */
    public ModernHeader(int x, int y, String header, float scaleSize, boolean drawUnderline, Color color) {
        Prerequisites.notNull(header);
        Prerequisites.conditionMet(scaleSize > 0, "Scale cannot be less than 0");

        this.x = x;
        this.y = y;

        this.headerText = header;
        this.scaleSize = scaleSize;
        this.drawUnderline = drawUnderline;
        this.headerColor = color;

        this.scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        this.subDrawables = new ArrayList<ModernDrawable>() {
            @Override
            public boolean add(ModernDrawable o) {
                if (o.getWidth() > ModernHeader.this.widthOfSub) {
                    ModernHeader.this.widthOfSub = o.getWidth();
                }

                return super.add(o);
            }
        };

        this.widthOfSub = Minecraft.getMinecraft().fontRendererObj.getStringWidth(header);
    }

    @Override
    public void render(int mouseX, int mouseY, float yTranslation) {
        if (!this.visible) {
            return;
        }

        ScaledResolution resolution = getScaledResolution();

        if (resolution == null) {
            return;
        }

        // Retrieve the renderer.
        FontRenderer fontRenderer = getFontRenderer();

        // Ensure minecraft actually has a font renderer we can use.
        if (fontRenderer == null) {
            return;
        }

        // Push the stack, making our own GL sandbox.
        GlStateManager.pushMatrix();

        // Reset the colors.
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        //GlStateManager.scale(-Minecraft.getMinecraft().gameSettings.guiScale,  -Minecraft.getMinecraft().gameSettings.guiScale, 0F);

        // Scales it up
        GlStateManager.scale(this.scaleSize, this.scaleSize, 0F);

        float xPos = this.x ;/// Minecraft.getMinecraft().gameSettings.guiScale;
        float yPos = this.y ;/// Minecraft.getMinecraft().gameSettings.guiScale;

        if (this.drawCentered) {
            // Draws the text
            fontRenderer.drawString(this.headerText, (xPos / this.scaleSize) - (getWidth() / this.scaleSize), yPos / this.scaleSize, this.headerColor.getRGB(), false);
        } else {
            // Draws the text
            fontRenderer.drawString(this.headerText, xPos / this.scaleSize, yPos / this.scaleSize, this.headerColor.getRGB(), false);
        }

        // Check if the header should have an underline or not.
        if (this.drawUnderline) {
            drawHorizontalLine((int) (xPos / this.scaleSize), (int) ((xPos + (getWidth() * this.scaleSize)) / this.scaleSize), (int) ((yPos + (12 * this.scaleSize)) / this.scaleSize), this.headerColor.getRGB());
        }

        // Pop the changes to the gl stack.
        GlStateManager.popMatrix();

        if (this.subDrawables.size() > 0) {
            float yOffset = (12 * this.scaleSize) + this.offsetBetweenDrawables / 2;

            for (ModernDrawable drawable : this.subDrawables) {
                if (drawable.isEnabled()) {

                    // Renders relative to this headers position.
                    if (drawable.renderRelativeToHeader()) {
                        GlStateManager.pushMatrix();

                        drawable.renderFromHeader((int) xPos, (int) yPos, yTranslation, mouseX, mouseY, (int) yOffset);

                        GlStateManager.popMatrix();
                    } else {
                        drawable.render(mouseX, mouseY, yTranslation);
                    }
                }

                yOffset += this.offsetBetweenDrawables;
            }
        }
    }

    @Override
    public boolean isInside(int mouseX, int mouseY, float yTranslation) {
        ModernGui.drawRect(this.x, this.y, this.x + getWidthOfHeader(), this.y + getHeightOfHeader(), new Color(0.5F, 0.5F, 0.5F, 0.5F).getRGB());

        return this.visible && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + getWidthOfHeader() && mouseY < this.y + getHeightOfHeader();
    }

    @Override
    public boolean isEnabled() {
        return this.visible;
    }

    @Override
    public ModernDrawable setAsPartOfHeader(ModernHeader parent) {
        return this;
    }

    /**
     * The width of this string based off the FontRenderer's width calculations.
     *
     * @return the width of the string.
     */
    public int getWidth() {
        if (getFontRenderer() == null) {
            return 0;
        }

        return getFontRenderer().getStringWidth(this.headerText);
    }

    /**
     * Retrieves the minecraft font renderer if one exists, or null if not applicable
     *
     * @return the minecraft font renderer.
     */
    private FontRenderer getFontRenderer() {
        if (Minecraft.getMinecraft() == null) {
            return null;
        }

        return Minecraft.getMinecraft().fontRendererObj;
    }

    /**
     * Retrieves Minecraft's scaled resolution.
     *
     * @return scaled resolution.
     */
    private ScaledResolution getScaledResolution() {
        if (this.scaledResolution != null) {
            return this.scaledResolution;
        }

        if (Minecraft.getMinecraft() == null) {
            return null;
        }

        this.scaledResolution = new ScaledResolution(Minecraft.getMinecraft());

        return this.scaledResolution;
    }

    /**
     * Force updates the resolution.
     */
    public void updateResolution() {
        this.scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
    }

    public int getWidthOfHeader() {
        return this.widthOfSub;
    }

    public int getHeightOfHeader() {
        if (this.subDrawables.size() == 0) {
            FontRenderer fontRenderer = getFontRenderer();

            if (fontRenderer == null) {
                return 0;
            }

            return (int) (fontRenderer.FONT_HEIGHT * this.scaleSize);
        }

        return (int) (((12 * this.scaleSize) + this.offsetBetweenDrawables / 2) * this.subDrawables.size());
    }

    @Override
    public void onLeftClick(int mouseX, int mouseY, float yTranslation) {
        if (this.subDrawables.size() > 0) {
            for (ModernDrawable sub : this.subDrawables) {
                if (sub instanceof InteractiveDrawable) {
                    InteractiveDrawable interactive = (InteractiveDrawable) sub;

                    if (interactive.isInside(mouseX, mouseY, yTranslation)) {
                        interactive.onLeftClick(mouseX, mouseY, yTranslation);
                    }
                }
            }
        }
    }

    public XYPosition getScreenPositionFromLocal(ModernDrawable drawable) {
        return getScreenPositionFromLocal(drawable.getX(), drawable.getY());
    }

    public XYPosition getScreenPositionFromLocal(int x, int y) {
        return new XYPosition(this.x + x, this.y + y);
    }


    /* Some good scales.
     *
     * Largest  2.0
     * Larger   1.58
     * Large    1.27 (a bit weird)
     * Skinny   1.24 (larger skinny text)
     * Skinny   1.05 (normal skinny text)
     * Normal   1.0
     * Fat      0.94 (only k's are slightly distored, seems fat).
     * Skinny   0.75 (a bit distorted, but skinny)
     * Half     0.5 (lowest scale where text is not distorted)
     */
}
