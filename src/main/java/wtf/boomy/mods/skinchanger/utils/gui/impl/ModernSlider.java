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

import java.awt.*;

import wtf.boomy.mods.skinchanger.utils.gui.faces.InteractiveUIElement;
import wtf.boomy.mods.skinchanger.utils.gui.ModernGui;
import wtf.boomy.mods.skinchanger.utils.gui.faces.StartEndUIElement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

/**
 * A basic horizontal slider implementation
 *
 * @author boomboompower
 */
public class ModernSlider implements InteractiveUIElement, StartEndUIElement {
    
    private final int id;
    private final int x;
    private final int y;
    
    private final int width;
    private final int height;
    
    private final String prefix;
    private final String suffix;
    
    private String displayString;
    
    private boolean visible = true;
    private boolean enabled = true;
    private boolean hovered = false;
    private boolean translatable = true;
    private boolean dragging = false;
    
    private double sliderValue;
    private final double minValue;
    private final double maxValue;
    
    public ModernSlider(int id, int xPos, int yPos, int width, int height, String prefix) {
        this(id, xPos, yPos, width, height, prefix, "", 0, 1, 0.5);
    }
    
    public ModernSlider(int id, int xPos, int yPos, int width, int height, String prefix, double minVal, double maxVal, double currentVal) {
        this(id, xPos, yPos, width, height, prefix, "", minVal, maxVal, currentVal);
    }
    
    public ModernSlider(int id, int xPos, int yPos, int width, int height, String prefix, String suffix, double minVal, double maxVal, double currentVal) {
        this.id = id;
        this.x = xPos;
        this.y = yPos;
        
        this.width = width;
        this.height = height;
        
        this.minValue = minVal;
        this.maxValue = maxVal;
        this.sliderValue = (currentVal - this.minValue) / (this.maxValue - this.minValue);
        
        this.prefix = prefix;
        this.suffix = suffix;
        
        updateSlider(false);
    }
    
    @Override
    public void render(int mouseX, int mouseY, float yTranslation) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        
        if (this.visible) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            
            Color backgroundColor;
            
            if (this.enabled) {
                backgroundColor = new Color(95, 255, 95, 75);
            } else {
                backgroundColor = new Color(67, 175, 67, 75);
            }
    
            ModernGui.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, backgroundColor.getRGB());
            
            mouseDragged(mouseX, mouseY);
            
            int color = 14737632;
            
            if (!this.enabled) {
                color = Color.WHITE.getRGB();
            } else if (this.hovered) {
                color = 16777120;
            }
            
            Minecraft mc = Minecraft.getMinecraft();
            
            String buttonText = this.displayString;
            
            int strWidth = mc.fontRendererObj.getStringWidth(buttonText);
            int ellipsisWidth = mc.fontRendererObj.getStringWidth("...");
            
            if (strWidth > this.width - 6 && strWidth > ellipsisWidth) {
                buttonText = mc.fontRendererObj.trimStringToWidth(buttonText, width - 6 - ellipsisWidth).trim() + "...";
                
                strWidth = mc.fontRendererObj.getStringWidth(buttonText);
            }
            
            mc.fontRendererObj.drawString(buttonText, (this.x + ((float) this.width / 2) - (float) strWidth / 2), this.y + ((float) this.height - 8) / 2, color, false);
        }
        
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
    
    @Override
    public void setAsPartOfHeader(ModernHeader parent) {
    }
    
    @Override
    public ModernSlider disableTranslatable() {
        this.translatable = false;
        
        return this;
    }
    
    private void mouseDragged(int mouseX, int mouseY) {
        if (this.visible) {
            if (this.dragging) {
                this.sliderValue = (mouseX - (this.x + 4)) / (float) (this.width - 8);
                updateSlider(true);
            }
            
            ModernGui.drawRect(this.x + (int) (this.sliderValue * (float) (this.width - 4)), this.y, this.x + (int) (this.sliderValue * (float) (this.width - 4)) + 4, this.y + this.height, Color.WHITE.getRGB());
        }
    }
    
    @Override
    public void onLeftClick(int mouseX, int mouseY, float yTranslation) {
        if (!isInside(mouseX, mouseY, yTranslation)) {
            return;
        }
        
        this.sliderValue = (float) (mouseX - (this.x + 4)) / (float) (this.width - 8);
        
        updateSlider(true);
        
        this.dragging = true;
    }
    
    @Override
    public void onMouseReleased(int mouseX, int mouseY, float yTranslation) {
        this.dragging = false;
    }
    
    @Override
    public boolean isInside(int mouseX, int mouseY, float yTranslation) {
        return this.visible && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
    }
    
    private void updateSlider(boolean triggerUpdate) {
        if (this.sliderValue < 0.0F) {
            this.sliderValue = 0.0F;
        }
        
        if (this.sliderValue > 1.0F) {
            this.sliderValue = 1.0F;
        }
        
        this.displayString = this.prefix + (int) Math.round(this.sliderValue * (this.maxValue - this.minValue) + this.minValue) + this.suffix;
        
        if (triggerUpdate) {
            onSliderUpdate();
        }
    }
    
    /**
     * Override as the trigger.
     */
    public void onSliderUpdate() {
    }
    
    public double getValue() {
        return this.sliderValue * (this.maxValue - this.minValue) + this.minValue;
    }
    
    public void setValue(double value) {
        this.sliderValue = (value - this.minValue) / (this.maxValue - this.minValue);
    }
    
    public int getId() {
        return id;
    }
    
    @Override
    public int getX() {
        return x;
    }
    
    @Override
    public int getY() {
        return y;
    }
    
    @Override
    public int getWidth() {
        return width;
    }
    
    @Override
    public int getHeight() {
        return height;
    }
    
    public String getPrefix() {
        return prefix;
    }
    
    public String getSuffix() {
        return suffix;
    }
    
    public String getDisplayString() {
        return displayString;
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    public boolean isHovered() {
        return hovered;
    }
    
    public void setTranslatable(boolean translatable) {
        this.translatable = translatable;
    }
    
    @Override
    public boolean isTranslatable() {
        return translatable;
    }
    
    public boolean isDragging() {
        return dragging;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}