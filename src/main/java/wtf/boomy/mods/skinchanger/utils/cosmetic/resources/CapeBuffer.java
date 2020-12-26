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

package wtf.boomy.mods.skinchanger.utils.cosmetic.resources;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * Parses a cape texture to be the correct width + height for the player.
 */
public class CapeBuffer implements ImageBuffer {
    
    private static BufferedImage whiteSquare;
    
    @Override
    public BufferedImage parseIncomingBuffer(BufferedImage img) {
        // If the image is null, provide them with a 32*32 white square.
        if (img == null) {
            if (whiteSquare == null) {
                whiteSquare = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
    
                Graphics graphics = whiteSquare.getGraphics();
                graphics.setColor(Color.WHITE);
                graphics.fillRect(0, 0, whiteSquare.getWidth(), whiteSquare.getHeight());
                graphics.dispose();
            }
    
            return whiteSquare;
        }
        
        int imageWidth = 64;
        int imageHeight = 32;
        int srcWidth = img.getWidth(null);
        
        for (int srcHeight = img.getHeight(null); imageWidth < srcWidth || imageHeight < srcHeight; imageHeight *= 2) {
            imageWidth *= 2;
        }
        
        BufferedImage imgNew = new BufferedImage(imageWidth, imageHeight, 2);
        Graphics g = imgNew.getGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return imgNew;
    }
}
