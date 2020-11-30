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

import com.google.common.collect.Lists;

import java.awt.Color;
import java.util.List;

import wtf.boomy.mods.skinchanger.cosmetic.options.SimpleCallback;
import wtf.boomy.mods.skinchanger.utils.gui.faces.InteractiveUIElement;
import wtf.boomy.mods.skinchanger.utils.gui.ModernGui;
import wtf.boomy.mods.skinchanger.utils.gui.faces.StartEndUIElement;

/**
 * A class which handles scrolling.
 */
public class ModernScroller implements InteractiveUIElement, StartEndUIElement {
    
    private final int x;
    private final int y;
    
    private final int width;
    private final int height;
    
    private boolean translatable;
    
    private float lastYLoc;
    private float lastTrackedProgress;
    private float currentProgress;
    private boolean dragging = false;
    
    private final List<SimpleCallback<Float>> callbacks = Lists.newArrayList();
    
    public ModernScroller(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        
        this.width = width;
        
        // Default height.
        this.height = height;
        
        // Make them match
        this.currentProgress = this.lastTrackedProgress = 0.01F;
    }
    
    @Override
    public void render(int mouseX, int mouseY, float yTranslation) {
        Color transparentBlue = new Color(200, 200, 255, 120);
        
        ModernGui.drawRect(this.x, this.y - 5, this.x + this.width + 1, this.y + this.height + 5, transparentBlue.getRGB());
        
        if (this.dragging) {
            this.lastYLoc = mouseY;
        }
        
        if (this.lastYLoc <= this.y) {
            this.lastYLoc = this.y;
        }
        
        if (this.lastYLoc >= this.y + this.height) {
            this.lastYLoc = this.y + this.height;
        }
        
        if (this.lastYLoc <= 0) {
            this.lastYLoc = 0.001F;
        }
        
        float tallest = this.y + this.height;
        
        // Run our callbacks
        this.currentProgress = (this.lastYLoc / tallest);
        
        if (this.lastTrackedProgress != this.currentProgress) {
            this.callbacks.forEach((c) -> c.run(this.currentProgress));
            
            this.lastTrackedProgress = this.currentProgress;
        }
        
        ModernGui.drawRect(this.x + 2, (int) this.lastYLoc - 3, this.x + this.width - 1, (int) this.lastYLoc + 3, transparentBlue.getRGB());
    }
    
    @Override
    public boolean isInside(int mouseX, int mouseY, float yTranslation) {
        return mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
    }
    
    @Override
    public void onLeftClick(int mouseX, int mouseY, float yTranslation) {
        this.dragging = true;
    }
    
    @Override
    public void onMouseReleased(int mouseX, int mouseY, float yTranslation) {
        this.dragging = false;
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }
    
    @Override
    public void setAsPartOfHeader(ModernHeader parent) {
        throw new UnsupportedOperationException("ModernScrollbar's cannot be added to a header.");
    }
    
    @Override
    public ModernScroller disableTranslatable() {
        this.translatable = false;
        
        return this;
    }
    
    /**
     * Inserts a callback onto this Scrollbar which will be called whenever the
     * scrollbars position changes
     *
     * @param callback the callback which will be run
     */
    public void insertScrollCallback(SimpleCallback<Float> callback) {
        if (callback == null) {
            return;
        }
        
        this.callbacks.add(callback);
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
    
    @Override
    public boolean isTranslatable() {
        return translatable;
    }
}
