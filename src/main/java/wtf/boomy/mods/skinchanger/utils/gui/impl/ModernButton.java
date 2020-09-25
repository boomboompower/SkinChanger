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

package wtf.boomy.mods.skinchanger.utils.gui.impl;

import wtf.boomy.mods.skinchanger.cosmetic.options.SimpleCallback;
import wtf.boomy.mods.skinchanger.utils.gui.InteractiveUIElement;
import wtf.boomy.mods.skinchanger.utils.gui.ModernGui;
import wtf.boomy.mods.skinchanger.utils.gui.StartEndUIElement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.awt.Color;
import java.util.List;

/**
 * ModernButton, a nicer looking button
 *
 * @author boomboompower
 *
 * @version 2.0
 * @since 3.0.0
 */
public class ModernButton implements InteractiveUIElement, StartEndUIElement {

    private final int id;

    private int width;
    private int height;

    private int x;
    private  int y;

    private boolean enabled;
    private boolean visible;

    private boolean hovered;

    private String displayString;

    private Color enabledColor = null;
    private Color disabledColor = null;
    
    private final SimpleCallback<? extends ModernButton> clickCallback;
    private List<String> messageLines = null;

    private boolean partOfHeader;

    private ModernHeader parentHeader;
    private int recommendedYPosition;

    private boolean translatable;

    public ModernButton(int buttonId, int x, int y, String buttonText) {
        this(buttonId, x, y, 200, 20, buttonText);
    }

    public ModernButton(int buttonId, String idName, int x, int y, String buttonText) {
        this(buttonId, x, y, 200, 20, buttonText);
    }

    public ModernButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        this(buttonId, x, y, widthIn, heightIn, buttonText, null);
    }
    
    public ModernButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, SimpleCallback<? extends ModernButton> clicked) {
        this.width = 200;
        this.height = 20;
        this.enabled = true;
        this.visible = true;
        this.id = buttonId;
        this.x = x;
        this.y = y;
        this.width = widthIn;
        this.height = heightIn;
        this.displayString = buttonText;
        this.clickCallback = clicked;
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public void render(int mouseX, int mouseY, float yTranslation) {
        if (this.visible) {
            FontRenderer fontrenderer = Minecraft.getMinecraft().fontRendererObj;

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            int xPosition = this.x;
            int yPosition = this.y;

            this.hovered = isInside(mouseX, mouseY, yTranslation);
            int i = this.getHoverState(this.hovered);

            int textColor = 14737632;

            if (this.enabled) {
                ModernGui.drawRect(xPosition, yPosition, xPosition + this.width, yPosition + this.height, getEnabledColor().getRGB());
            } else {
                ModernGui.drawRect(xPosition, yPosition, xPosition + this.width, yPosition + this.height, getDisabledColor().getRGB());
            }

            renderButtonString(fontrenderer, xPosition, yPosition, textColor);
        }
    }

    @Override
    public void renderFromHeader(int xPos, int yPos, float yTranslation, int mouseX, int mouseY, int recommendedYOffset) {
        if (this.visible) {
            FontRenderer fontrenderer = Minecraft.getMinecraft().fontRendererObj;

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            int xPosition = xPos + 5;
            int yPosition = yPos + recommendedYOffset;

            this.recommendedYPosition = yPosition;

            this.hovered = isInside(mouseX, mouseY, yTranslation);

            int i = this.getHoverState(this.hovered);

            int j = 14737632;

            if (this.enabled) {
                ModernGui.drawRect(xPosition, yPosition, xPosition + this.width, yPosition + height, getEnabledColor().getRGB());
            } else {
                ModernGui.drawRect(xPosition, yPosition, xPosition + this.width, yPosition + height, getDisabledColor().getRGB());
            }

            renderButtonString(fontrenderer, xPosition, yPosition, j);
        }
    }

    @Override
    public void onLeftClick(int mouseX, int mouseY, float yTranslation) {
        if (mouseX != -1 && mouseY != -1) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
        }
        
        if (this.clickCallback != null) {
            this.clickCallback.run(getMe());
        }
    }
    
    // A dumb hack because Java is stupid
    @Override
    public boolean isInside(int mouseX, int mouseY, float yTranslation) {
        if (!this.visible) {
            return false;
        }

        int xPosition = this.x;
        int yPosition = this.y;

        if (this.partOfHeader) {
            yPosition = this.recommendedYPosition;
            xPosition += this.parentHeader.getX();
        }
        
        yPosition += yTranslation;
        
        return mouseX >= xPosition && mouseX < xPosition + this.width &&
                mouseY >= yPosition && mouseY < yPosition + this.height;
    }
    
    @Override
    public void setAsPartOfHeader(ModernHeader parent) {
        this.partOfHeader = true;

        this.parentHeader = parent;
    }
    
    @Override
    public InteractiveUIElement disableTranslatable() {
        this.translatable = false;

        return this;
    }
    
    /**
     * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering over
     * this button.
     *
     * @param mouseOver true if the mouse is over this button
     * @return an integer state based on what the color of this button should be
     */
    protected int getHoverState(boolean mouseOver) {
        int state = 1;

        if (!this.enabled) {
            state = 0;
        } else if (mouseOver) {
            state = 2;
        }
        return state;
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
    
    public void setX(int x) {
        this.x = x;
    }
    
    public void setY(int y) {
        this.y = y;
    }
    
    public void setWidth(int width) {
        this.width = width;
    }
    
    public void setHeight(int height) {
        this.height = height;
    }
    
    public ModernButton setMessageLines(List<String> messageLines) {
        this.messageLines = messageLines;
        
        return this;
    }
    
    public List<String> getMessageLines() {
        return messageLines;
    }
    
    public ModernButton setEnabled(boolean isEnabled) {
        this.enabled = isEnabled;

        return this;
    }
    
    /**
     * Renders the string of the button
     *
     * @param fontrenderer the FontRenderer object
     * @param xPosition the x position of the button
     * @param yPosition the y position of the button
     * @param textColor the color of the text
     */
    private void renderButtonString(FontRenderer fontrenderer, int xPosition, int yPosition, int textColor) {
        if (!this.enabled) {
            textColor = 10526880;
        } else if (this.hovered) {
            textColor = 16777120;
        }

        fontrenderer.drawString(this.displayString, (xPosition + (float) this.width / 2 - (float) fontrenderer.getStringWidth(this.displayString) / 2), yPosition + ((float) this.height - 8) / 2, textColor, false);
    }
    
    public int getId() {
        return id;
    }
    
    @Override
    public int getWidth() {
        return width;
    }
    
    @Override
    public int getHeight() {
        return height;
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    public boolean isHovered() {
        return hovered;
    }
    
    public String getDisplayString() {
        return displayString;
    }
    
    public boolean isPartOfHeader() {
        return partOfHeader;
    }
    
    @Override
    public boolean isTranslatable() {
        return translatable;
    }
    
    private <T extends ModernButton> T getMe() {
        return (T) this;
    }
}