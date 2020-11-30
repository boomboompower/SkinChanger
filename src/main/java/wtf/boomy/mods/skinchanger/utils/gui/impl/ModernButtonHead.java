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

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EnumPlayerModelParts;

import wtf.boomy.mods.skinchanger.cosmetic.options.SimpleCallback;
import wtf.boomy.mods.skinchanger.utils.gui.ModernGui;

/**
 * An implementation of a button with a players head instead of text.
 *
 * The code is based off the values derived from the player list. Works better with even width + heights.
 *
 * @author boomboompower
 */
public class ModernButtonHead extends ModernButton {
    
    private final AbstractClientPlayer player;
    
    public ModernButtonHead(int buttonId, int x, int y, AbstractClientPlayer player) {
        super(buttonId, x, y, player == null ? "" : player.getName());
        
        this.player = player;
    }
    
    public ModernButtonHead(int buttonId, int x, int y, int widthIn, int heightIn, AbstractClientPlayer player, SimpleCallback<? extends ModernButton> clicked) {
        super(buttonId, x, y, widthIn, heightIn, player == null ? "" : player.getName(), clicked);
        
        this.player = player;
    }
    
    @Override
    protected void renderButtonString(FontRenderer fontrenderer, int xPosition, int yPosition, int textColor) {
        if (this.player == null || this.player.getLocationSkin() == null) return;
        
        // If the texture is flipped... it will be upside down!
        boolean isFlipped = this.player.isWearing(EnumPlayerModelParts.CAPE) && isSpecialPlayer(this.player.getName());
        Minecraft.getMinecraft().getTextureManager().bindTexture(this.player.getLocationSkin()); // bind the skin
        int v = 8 + (isFlipped ? 8 : 0); // calculate the vertical position of the head texture
        int vHeight = 8 * (isFlipped ? -1 : 1); // calculate the height of the texture
        
        // Precompute
        int xPos = xPosition + 5;
        int yPos = yPosition + 5;
        int width = getWidth() - 10;
        int height = getHeight() - 10;
        
        GlStateManager.pushMatrix();
        GlStateManager.enableAlpha(); // Required
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        
        // Read the 64x64 texture, choose the pixels where the players head is and draw them
        ModernGui.drawScaledCustomSizeModalRect(xPos, yPos, 8.0F, (float) v, 8, vHeight, width, height, 64.0F, 64.0F);
        
        // If the player model has a hat, render the hat on top of the skin.
        if (this.player.isWearing(EnumPlayerModelParts.HAT)) {
            ModernGui.drawScaledCustomSizeModalRect(xPos, yPos, 40.0F, (float)v, 8, vHeight, width, height, 64.0F, 64.0F);
        }
        
        // If the button is hovered we will outline it with the normal text color
        if (isHovered()) {
            ModernGui.drawRectangleOutline(xPos - 3, yPos - 3, xPos + width + 2, yPos + height + 2, getTextHoverColor().getRGB());
        }
        
        // pop the gl buffer to the stackv
        GlStateManager.disableAlpha();
        GlStateManager.popMatrix();
    }
    
    private boolean isSpecialPlayer(String name) {
        // Secret?
        return (name.equals("Dinnerbone") || name.equals("Grumm") || name.equals("boomboompower"));
    }
}
