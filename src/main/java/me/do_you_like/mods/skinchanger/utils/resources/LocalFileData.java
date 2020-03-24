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

package me.do_you_like.mods.skinchanger.utils.resources;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import me.do_you_like.mods.skinchanger.utils.general.Prerequisites;

import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

/**
 * Loads a local file into a ResourceLocation.
 */
public class LocalFileData extends SimpleTexture {

    private BufferedImage bufferedImage;
    private boolean textureUploaded;

    private IImageBuffer imageBuffer;
    private File fileLocation;

    public LocalFileData(ResourceLocation textureLocation, File fileToLoad) {
        super(textureLocation);

        Prerequisites.notNull(textureLocation);
        Prerequisites.notNull(fileToLoad);

        this.fileLocation = fileToLoad;
    }

    public LocalFileData(
        ResourceLocation textureLocation, File fileToLoad, IImageBuffer imageBuffer) {
        super(textureLocation);

        Prerequisites.notNull(textureLocation);
        Prerequisites.notNull(fileToLoad);

        this.fileLocation = fileToLoad;
        this.imageBuffer = imageBuffer;
    }

    @Override
    public void loadTexture(IResourceManager resourceManager) throws IOException {
        if (this.bufferedImage == null && this.textureLocation != null) {
            super.loadTexture(resourceManager);
        }

        if (this.fileLocation != null && this.fileLocation.isFile()) {
            try {
                // Load the image from the file.
                this.bufferedImage = ImageIO.read(this.fileLocation);

                // A buffer is not required.
                if (this.imageBuffer != null) {
                    // Since a buffer exists, parse the image through the buffer
                    this.bufferedImage = this.imageBuffer.parseUserSkin(this.bufferedImage);

                    // Buffer may have been set to null in the above call.
                    // If it is still not null throw it a callback.
                    if (this.imageBuffer != null) {
                        // Callback
                        this.imageBuffer.func_152634_a();
                    }
                }
            } catch (IOException ex) {
                System.err.println("Unable to read file.");

                ex.printStackTrace();
            }
        } else {
            System.err.println("File did not exist.");
        }
    }

    @Override
    public int getGlTextureId() {
        // Assigns a Gl ID to the texture if one does not already exist.
        checkTextureUploaded();

        return super.getGlTextureId();
    }

    private void checkTextureUploaded() {
        if (!this.textureUploaded) {
            if (this.bufferedImage != null) {
                if (this.textureLocation != null) {
                    this.deleteGlTexture();
                }

                TextureUtil.uploadTextureImage(super.getGlTextureId(), this.bufferedImage);
                this.textureUploaded = true;
            }
        }
    }
}
