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

package wtf.boomy.mods.skinchanger.utils.gui.faces;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import wtf.boomy.mods.skinchanger.utils.cosmetic.impl.fakeplayer.FakePlayerRender;
import wtf.boomy.mods.skinchanger.utils.gui.ModernGui;

import java.awt.Color;

public interface PlayerModelUI {
    
    public default void renderFakePlayer(int width, int height, float partialTicks, FakePlayerRender fakePlayer) {
        int halfWidth = width / 2 + 20;
    
        int scale = (int) ((1.5 * width) / 10);
    
        GlStateManager.pushMatrix();
    
        fakePlayer.renderFakePlayer((halfWidth + (width - 20)) / 2, height - 10 - scale, scale, partialTicks, FakePlayerRender.getRotation());
    
        GlStateManager.popMatrix();
    }
    
    /**
     * Draws the background for the render box
     */
    public default void drawRenderBox(FontRenderer fontRendererObj, String playerModelTranslation, int width, int height) {
        drawRenderBox(fontRendererObj, playerModelTranslation, width, height, null);
    }
    
    public default void drawRenderBox(FontRenderer fontRendererObj, String playerModelTranslation, int width, int height, String underName) {
        int x = ((width / 2 + 20) + (width - 20)) / 2;
        int y = 30;
        
        GlStateManager.pushMatrix();
        
        // 20 pixel margin.
        ModernGui.drawRect(width / 2 + 20, 20, width - 20, height - 20, 867414963);
    
        fontRendererObj.drawString(playerModelTranslation, (float) (x - fontRendererObj.getStringWidth(playerModelTranslation) / 2), (float) y, Color.WHITE.getRGB(), false);
        
        if (underName != null) {
            fontRendererObj.drawString(underName, (float) (x - fontRendererObj.getStringWidth(underName) / 2), (float) y + 15, Color.WHITE.getRGB(), false);
        }
        
        GlStateManager.popMatrix();
    }
}
