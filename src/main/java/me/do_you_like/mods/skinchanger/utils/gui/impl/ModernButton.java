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

import lombok.Getter;
import lombok.Setter;

import me.do_you_like.mods.skinchanger.utils.general.XYPosition;
import me.do_you_like.mods.skinchanger.utils.gui.ModernDrawable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

/**
 * ModernButton, a nicer looking button
 *
 * @author boomboompower
 * @version 2.0
 */
public class ModernButton extends Gui implements ModernDrawable {

    protected static final ResourceLocation buttonTextures = new ResourceLocation("textures/gui/widgets.png");

    @Getter
    private int id;

    @Setter
    @Getter
    private int width;

    private int height;
    private int xPosition;
    private int yPosition;

    @Getter
    private boolean enabled;

    @Setter
    @Getter
    private boolean visible;

    @Getter
    @Setter
    private boolean favourite;

    @Getter
    private boolean hovered;

    @Getter
    private String buttonId;
    private String displayString;

    private Color enabledColor = null;
    private Color disabledColor = null;

    @Getter
    private Object buttonData;

    @Getter
    private boolean partOfHeader;

    private ModernHeader parentHeader;
    private int recommendedYPosition;

    @Getter
    private boolean translatable;

    public ModernButton(int buttonId, int x, int y, String buttonText) {
        this(buttonId, "", x, y, 200, 20, buttonText);
    }

    public ModernButton(int buttonId, String idName, int x, int y, String buttonText) {
        this(buttonId, idName, x, y, 200, 20, buttonText);
    }

    public ModernButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        this(buttonId, "", x, y, widthIn, heightIn, buttonText);
    }

    public ModernButton(int buttonId, String idName, int x, int y, int widthIn, int heightIn, String buttonText) {
        this.width = 200;
        this.height = 20;
        this.enabled = true;
        this.visible = true;
        this.id = buttonId;
        this.xPosition = x;
        this.yPosition = y;
        this.width = widthIn;
        this.height = heightIn;
        this.buttonId = idName;
        this.displayString = buttonText;
    }

    /**
     * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering over
     * this button.
     */
    protected int getHoverState(boolean mouseOver) {
        int i = 1;

        if (!this.enabled) {
            i = 0;
        } else if (mouseOver) {
            i = 2;
        }
        return i;
    }

    @Override
    public int getX() {
        return this.xPosition;
    }

    @Override
    public int getY() {
        return this.yPosition;
    }

    @Override
    public void render(int mouseX, int mouseY) {
        render(mouseX, mouseY, 0);
    }

    public void render(int mouseX, int mouseY, int yTranslation) {
        if (this.visible) {
            FontRenderer fontrenderer = Minecraft.getMinecraft().fontRendererObj;

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            int xPosition = this.xPosition;
            int yPosition = this.yPosition;

            this.hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + this.width && mouseY < yPosition + this.height;
            int i = this.getHoverState(this.hovered);

            int j = 14737632;

            boolean modern = false; //SkinChangerMod.getInstance().getConfigurationHandler().isModernButton();

            if (modern) {
                Minecraft.getMinecraft().getTextureManager().bindTexture(buttonTextures);

                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.blendFunc(770, 771);

                this.drawTexturedModalRect(xPosition, yPosition, 0, 46 + i * 20, this.width / 2, this.height);
                this.drawTexturedModalRect(xPosition + this.width / 2, yPosition, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
            } else {
                if (this.enabled) {
                    drawRect(xPosition, yPosition, xPosition + this.width, yPosition + height, getEnabledColor().getRGB());
                } else {
                    drawRect(xPosition, yPosition, xPosition + this.width, yPosition + height, getDisabledColor().getRGB());
                }
            }

            if (!this.enabled) {
                j = 10526880;
            } else if (this.hovered) {
                j = 16777120;
            }

            if (this.enabled && this.favourite) {
                fontrenderer.drawString("\u2726", xPosition + this.width - fontrenderer.getStringWidth("\u2726") - 4, yPosition + ((fontrenderer.FONT_HEIGHT / 2) + 2), Color.ORANGE.getRGB());
            }

            fontrenderer.drawString(this.displayString, (xPosition + this.width / 2 - fontrenderer.getStringWidth(this.displayString) / 2), yPosition + (this.height - 8) / 2, j, modern);
        }
    }

    @Override
    public void renderFromHeader(int xPos, int yPos, int mouseX, int mouseY, int recommendedYOffset) {
        if (this.visible) {
            FontRenderer fontrenderer = Minecraft.getMinecraft().fontRendererObj;

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            int xPosition = xPos + 5;
            int yPosition = yPos + recommendedYOffset;

            this.recommendedYPosition = yPosition;

            this.hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + this.width && mouseY < yPosition + this.height;
            int i = this.getHoverState(this.hovered);

            int j = 14737632;

            boolean modern = false; //SkinChangerMod.getInstance().getConfigurationHandler().isModernButton();

            if (modern) {
                Minecraft.getMinecraft().getTextureManager().bindTexture(buttonTextures);

                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.blendFunc(770, 771);

                this.drawTexturedModalRect(xPosition, yPosition, 0, 46 + i * 20, this.width / 2, this.height);
                this.drawTexturedModalRect(xPosition + this.width / 2, yPosition, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
            } else {
                if (this.enabled) {
                    drawRect(xPosition, yPosition, xPosition + this.width, yPosition + height, getEnabledColor().getRGB());
                } else {
                    drawRect(xPosition, yPosition, xPosition + this.width, yPosition + height, getDisabledColor().getRGB());
                }
            }

            if (!this.enabled) {
                j = 10526880;
            } else if (this.hovered) {
                j = 16777120;
            }

            if (this.enabled && this.favourite) {
                fontrenderer.drawString("\u2726", xPosition + this.width - fontrenderer.getStringWidth("\u2726") - 4, yPosition + ((fontrenderer.FONT_HEIGHT / 2) + 2), Color.ORANGE.getRGB());
            }

            fontrenderer.drawString(this.displayString, (xPosition + this.width / 2 - fontrenderer.getStringWidth(this.displayString) / 2), yPosition + (this.height - 8) / 2, j, modern);
        }
    }

    @Override
    public ModernDrawable setAsPartOfHeader(ModernHeader parent) {
        this.partOfHeader = true;

        this.parentHeader = parent;

        return this;
    }

    @Override
    public ModernDrawable disableTranslatable() {
        this.translatable = false;

        return this;
    }

    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        int xPosition = this.xPosition;
        int yPosition = this.yPosition;

        if (this.partOfHeader) {
            yPosition = this.recommendedYPosition;
        }

        return this.enabled && this.visible && mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + this.width && mouseY < yPosition + this.height;
    }

    public void mouseReleased(int mouseX, int mouseY) {
    }

    public void playPressSound(SoundHandler soundHandlerIn) {
        soundHandlerIn.playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
    }

    public Color getEnabledColor() {
        return this.enabledColor == null ? new Color(255, 255, 255, 75) : this.enabledColor;
    }

    public ModernButton setEnabledColor(Color colorIn) {
        this.enabledColor = colorIn;

        return this;
    }

    public Color getDisabledColor() {
        return this.disabledColor == null ? new Color(100, 100, 100, 75) : this.disabledColor;
    }

    public ModernButton setDisabledColor(Color colorIn) {
        this.disabledColor = colorIn;

        return this;
    }

    public String getText() {
        return this.displayString;
    }

    public void setText(String text) {
        this.displayString = text != null ? text : "";
    }

    public ModernButton setEnabled(boolean isEnabled) {
        this.enabled = isEnabled;

        return this;
    }

    public boolean hasButtonData() {
        return this.buttonData != null;
    }

    public Object setButtonData(Object buttonData) {
        this.buttonData = buttonData;

        return this;
    }
}