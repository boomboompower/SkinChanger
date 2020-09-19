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

package wtf.boomy.mods.skinchanger.utils.resources;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/**
 * A modified version of the vanilla Skin buffer, with a couple tweaks
 */
public class SkinBuffer implements ImageBuffer {
    
    private int[] imageData;
    private final int imageWidth = 64;
    private final int imageHeight = 64;
    
    public BufferedImage parseIncomingBuffer(BufferedImage image) {
        if (image == null) {
            return null;
        } else {
            BufferedImage bufferedimage = new BufferedImage(this.imageWidth, this.imageHeight, 2);
            Graphics graphics = bufferedimage.getGraphics();
            graphics.drawImage(image, 0, 0, null);
            
            if (image.getHeight() == 32) {
                graphics.drawImage(bufferedimage, 24, 48, 20, 52, 4, 16, 8, 20, null);
                graphics.drawImage(bufferedimage, 28, 48, 24, 52, 8, 16, 12, 20, null);
                graphics.drawImage(bufferedimage, 20, 52, 16, 64, 8, 20, 12, 32, null);
                graphics.drawImage(bufferedimage, 24, 52, 20, 64, 4, 20, 8, 32, null);
                graphics.drawImage(bufferedimage, 28, 52, 24, 64, 0, 20, 4, 32, null);
                graphics.drawImage(bufferedimage, 32, 52, 28, 64, 12, 20, 16, 32, null);
                graphics.drawImage(bufferedimage, 40, 48, 36, 52, 44, 16, 48, 20, null);
                graphics.drawImage(bufferedimage, 44, 48, 40, 52, 48, 16, 52, 20, null);
                graphics.drawImage(bufferedimage, 36, 52, 32, 64, 48, 20, 52, 32, null);
                graphics.drawImage(bufferedimage, 40, 52, 36, 64, 44, 20, 48, 32, null);
                graphics.drawImage(bufferedimage, 44, 52, 40, 64, 40, 20, 44, 32, null);
                graphics.drawImage(bufferedimage, 48, 52, 44, 64, 52, 20, 56, 32, null);
            }
            
            graphics.dispose();
            this.imageData = ((DataBufferInt) bufferedimage.getRaster().getDataBuffer()).getData();
            setAreaOpaque(0, 0, 32, 16);
            setAreaTransparent(32, 0, 64, 32);
            setAreaOpaque(0, 16, 64, 32);
            setAreaTransparent(0, 32, 16, 48);
            setAreaTransparent(16, 32, 40, 48);
            setAreaTransparent(40, 32, 56, 48);
            setAreaTransparent(0, 48, 16, 64);
            setAreaOpaque(16, 48, 48, 64);
            setAreaTransparent(48, 48, 64, 64);
            
            return bufferedimage;
        }
    }
    
    /**
     * Makes the given area of the image transparent if it was previously completely opaque (used to remove the outer
     * layer of a skin around the head if it was saved all opaque; this would be redundant so it's assumed that the skin
     * maker is just using an image editor without an alpha channel)
     */
    private void setAreaTransparent(int startingX, int startingY, int endingX, int endingY) {
        if (!this.hasTransparency(startingX, startingY, endingX, endingY)) {
            for (int i = startingX; i < endingX; ++i) {
                for (int j = startingY; j < endingY; ++j) {
                    this.imageData[i + j * this.imageWidth] &= 0x1000000;
                }
            }
        }
    }
    
    /**
     * Makes the given area of the image opaque
     */
    private void setAreaOpaque(int startAreaX, int startAreaY, int endAreaX, int endAreaY) {
        for (int i = startAreaX; i < endAreaX; ++i) {
            for (int j = startAreaY; j < endAreaY; ++j) {
                this.imageData[i + j * this.imageWidth] |= -0x1000000;
            }
        }
    }
    
    /**
     * Returns true if the given area of the image contains transparent pixels
     */
    private boolean hasTransparency(int startingX, int startingY, int endingX, int endingY) {
        for (int i = startingX; i < endingX; ++i) {
            for (int j = startingY; j < endingY; ++j) {
                int data = this.imageData[i + j * this.imageWidth];
                
                // If it's transparent
                if ((data >> 24 & 255) < 128) {
                    return true;
                }
            }
        }
        
        return false;
    }
}
