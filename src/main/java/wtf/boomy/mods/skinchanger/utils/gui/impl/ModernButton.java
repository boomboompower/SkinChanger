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

import org.lwjgl.opengl.Display;
import wtf.boomy.mods.skinchanger.SkinChangerMod;
import wtf.boomy.mods.skinchanger.configuration.ConfigurationHandler;
import wtf.boomy.mods.skinchanger.utils.cosmetic.options.SimpleCallback;
import wtf.boomy.mods.skinchanger.utils.gui.faces.InteractiveUIElement;
import wtf.boomy.mods.skinchanger.utils.gui.ModernGui;
import wtf.boomy.mods.skinchanger.utils.gui.faces.StartEndUIElement;

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
    
    private static final ResourceLocation buttonTextures = new ResourceLocation("textures/gui/widgets.png");
    
    private final int id;
    protected final ConfigurationHandler handler;

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
    private Color hoverColor = null;
    
    private final SimpleCallback<? extends ModernButton> clickCallback;
    private List<String> messageLines = null;

    private boolean partOfHeader;

    private ModernHeader parentHeader;
    private int recommendedYPosition;

    private boolean translatable;
    
    private float storedGlow;
    private float glowPadding;
    private float glowIncrements;

    public ModernButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        this(buttonId, x, y, widthIn, heightIn, buttonText, null);
    }
    
    public ModernButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, SimpleCallback<? extends ModernButton> clicked) {
        this.handler = SkinChangerMod.getInstance().getConfig();
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
    public void render(int mouseX, int mouseY, float yTranslation, float partialTicks) {
        if (this.visible) {
            FontRenderer fontrenderer = Minecraft.getMinecraft().fontRendererObj;

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            int xPosition = this.x;
            int yPosition = this.y;

            this.hovered = isInside(mouseX, mouseY, yTranslation);
            int i = this.getHoverState(this.hovered);

            int textColor = 14737632;
    
            renderOuterGlow(xPosition, yPosition, partialTicks);
            
            if (this.handler.isOldButtons()) {
                drawOldBackground(xPosition, yPosition);
            } else {
                drawNewBackground(xPosition, yPosition, this.enabled ? getEnabledColor().getRGB() : getDisabledColor().getRGB());
            }

            renderButtonString(fontrenderer, xPosition, yPosition, textColor);
        }
    }

    @Override
    public void renderFromHeader(int xPos, int yPos, float yTranslation, float partialTicks, int mouseX, int mouseY, int recommendedYOffset) {
        if (this.visible) {
            FontRenderer fontrenderer = Minecraft.getMinecraft().fontRendererObj;

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            int xPosition = xPos + 5;
            int yPosition = yPos + recommendedYOffset;

            this.recommendedYPosition = yPosition;

            this.hovered = isInside(mouseX, mouseY, yTranslation);

            int i = this.getHoverState(this.hovered);

            int j = 14737632;
    
            renderOuterGlow(xPosition, yPosition, partialTicks);
            
            if (this.handler.isOldButtons()) {
                drawOldBackground(xPosition, yPosition);
            } else {
                drawNewBackground(xPosition, yPosition, this.enabled ? getEnabledColor().getRGB() : getDisabledColor().getRGB());
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
        
        if (isTranslatable()) {
            yPosition += yTranslation;
        }
        
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
    
    public InteractiveUIElement enableTranslatable() {
        this.translatable = true;
        
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
        return this.enabledColor == null ? (this.enabledColor = new Color(255, 255, 255, 75)) : this.enabledColor;
    }
    
    public Color getDisabledColor() {
        return this.disabledColor == null ? (this.disabledColor = new Color(100, 100, 100, 75)) : this.disabledColor;
    }
    
    public Color getTextHoverColor() {
        return this.hoverColor == null ? (this.hoverColor = new Color(255, 255, 160)) : this.hoverColor;
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
    
    public ModernButton setOuterGlow(float padding, float increments) {
        if (padding <= 0) {
            this.glowPadding = 0;
            this.glowIncrements = 0;
        } else {
            this.glowPadding = padding;
            this.glowIncrements = increments;
        }
        
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
    protected void renderButtonString(FontRenderer fontrenderer, int xPosition, int yPosition, int textColor) {
        if (!this.enabled) {
            textColor = 10526880;
        } else if (this.hovered) {
            textColor = getTextHoverColor().getRGB();
        }

        fontrenderer.drawString(this.displayString, (xPosition + (float) this.width / 2 - fontrenderer.getStringWidth(this.displayString) / 2.0f), yPosition + ((float) this.height - 8) / 2, textColor, false);
    }
    
    /**
     * Renders an outer glow on this button.
     *
     * If the game is not active (the foreground window), this will be skipped.
     *
     * @param xPosition the starting x position for the glow
     * @param yPosition the starting y position for the glow
     * @param partialTicks the amount of time since the previous frame
     */
    protected void renderOuterGlow(int xPosition, int yPosition, float partialTicks) {
        // Use glfwGetWindowAttrib with GLFW_FOCUSED for LWJGL 3.0
        if (!Display.isActive()) return;
        
        if (this.glowPadding > 0 && this.glowIncrements > 0) {
            float subsequentPadding = this.glowPadding + 1;
            
            this.storedGlow += this.glowIncrements * partialTicks;
        
            if (this.storedGlow > subsequentPadding + 3) {
                this.storedGlow = 0;
            }
            
            float alphaValue = (1 - (this.storedGlow / subsequentPadding));
        
            if (alphaValue > 1 || alphaValue < 0) alphaValue = 0;
    
            float left = xPosition + 1 - this.storedGlow;
            float top = yPosition + 1 - this.storedGlow;
            float right = xPosition - 2 + this.width + this.storedGlow;
            float bottom = yPosition - 2 + this.height + this.storedGlow;
            
            ModernGui.drawRectangleOutlineF(left, top, right, bottom, new Color(1, 1, 1, alphaValue).getRGB());
        }
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
    
    public boolean isHovered() {
        return hovered;
    }
    
    @Override
    public boolean isTranslatable() {
        return translatable;
    }
    
    private <T extends ModernButton> T getMe() {
        return (T) this;
    }
    
    private void drawNewBackground(int startX, int startY, int color) {
        ModernGui.drawRect(startX, startY, startX + this.width, startY + this.height, color);
    }
    
    private void drawOldBackground(int startX, int startY) {
        Minecraft minecraft = Minecraft.getMinecraft();
        minecraft.getTextureManager().bindTexture(buttonTextures);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        
        int hoverState = this.getHoverState(this.hovered);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.blendFunc(770, 771);
        
        // Pre-compute.
        float halfWidth = this.width / 2.0f;
        float halfHeight = this.height / 2.0f;
    
        // Dimensions of the button are 200 x 20
        // The last 4 pixels on the bottom should be used
    
        // Starting Y of texture is 66
        // Starting X of texture is 0
    
        // Ending Y is 88
        // Ending X is 199
        
        // This renders a scalable button texture
        // By default the game scales horizontally by splitting the texture on the y axis,
        // however this can be also done vertically by splitting on the x axis, allowing buttons
        // of any width and height to be rendered without the screen.
        ModernGui.drawTexturedModalRect(startX, startY + halfHeight, 0, 66 - halfHeight + (hoverState * 20), halfWidth, halfHeight);
        ModernGui.drawTexturedModalRect(startX + halfWidth, startY + halfHeight, 200 - halfWidth, 66 - halfHeight + (hoverState * 20), halfWidth, halfHeight);
        ModernGui.drawTexturedModalRect(startX, startY, 0, 46 + hoverState * 20, halfWidth, halfHeight);
        ModernGui.drawTexturedModalRect(startX + halfWidth, startY, 200 - halfWidth, 46 + hoverState * 20, halfWidth, halfHeight);
    }
}