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

package wtf.boomy.mods.skinchanger.cosmetic.resources;

import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Loads a local file into a ResourceLocation.
 *
 * @author boomboompower
 * @version 1.0
 * @since 3.0
 */
public class LocalFileData extends SimpleTexture {
    
    // The BufferedImage variant of this texture
    private BufferedImage bufferedImage;
    // True if this texture has been pushed to the GL stack.
    private boolean textureUploaded;
    
    // The buffer for this texture
    private final ImageBuffer imageBuffer;
    
    // The location of this texture
    private final File fileLocation;
    
    // The resource data
    private IResource resource;
    
    /**
     * Loads a texture from a local file to a ResourceLocation
     *
     * @param textureLocation an overridable texture, just use DefaultPlayerSkin.getDefaultSkin() if you don't know what this is
     * @param fileToLoad      the file to load the texture from
     */
    public LocalFileData(ResourceLocation textureLocation, File fileToLoad) {
        this(textureLocation, fileToLoad, null);
    }
    
    /**
     * Loads a texture from a local file to a ResourceLocation
     *
     * @param textureLocation an overridable texture, just use DefaultPlayerSkin.getDefaultSkin() if you don't know what this is
     * @param fileToLoad      the file to load the texture from
     * @param imageBuffer     the buffer which the texture will be parsed through. See {@link CapeBuffer} or {@link SkinBuffer}
     */
    public LocalFileData(ResourceLocation textureLocation, File fileToLoad, ImageBuffer imageBuffer) {
        super(textureLocation);
        
        this.fileLocation = fileToLoad;
        this.imageBuffer = imageBuffer;
    }
    
    @Override
    public void loadTexture(IResourceManager resourceManager) throws IOException {
        if (this.bufferedImage == null && this.textureLocation != null) {
            originalLoad(resourceManager);
        }
        
        if (this.fileLocation != null && this.fileLocation.isFile()) {
            try {
                resourceManager.getResource(this.textureLocation);
                
                // Load the image from the file.
                this.bufferedImage = ImageIO.read(this.fileLocation);
                
                // A buffer is not required.
                if (this.imageBuffer != null) {
                    // Since a buffer exists, parse the image through the buffer
                    this.bufferedImage = this.imageBuffer.parseIncomingBuffer(this.bufferedImage);
                }
    
                checkTextureUploaded();
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
    
    public IResource getResource() {
        return this.resource;
    }
    
    public File getFileLocation() {
        return this.fileLocation;
    }
    
    @Override
    public String toString() {
        return "LocalFileData{" +
                "textureUploaded=" + this.textureUploaded +
                ", fileLocation=" + this.fileLocation +
                ", glTextureId=" + this.glTextureId + '}';
    }
    
    /**
     * Checks to see if this texture has been pushed to GL yet.
     * <p>
     * If not it is pushed and the texture ID is assigned
     */
    private void checkTextureUploaded() {
        if (!this.textureUploaded) {
            if (this.bufferedImage != null) {
                if (this.textureLocation != null) {
                    deleteGlTexture();
                }
                
                // Minecraft Util to push an image to GL
                TextureUtil.uploadTextureImage(super.getGlTextureId(), this.bufferedImage);
                
                this.textureUploaded = true;
            }
        }
    }
    
    private void originalLoad(IResourceManager resourceManager) throws IOException {
        this.deleteGlTexture();
        InputStream inputstream = null;
        
        try {
            this.resource = resourceManager.getResource(this.textureLocation);
            inputstream = this.resource.getInputStream();
            BufferedImage bufferedimage = TextureUtil.readBufferedImage(inputstream);
            boolean hasBlur = false;
            boolean isClamped = false;
            
            if (this.resource.hasMetadata()) {
                try {
                    TextureMetadataSection meta = this.resource.getMetadata("texture");
                    
                    if (meta != null) {
                        hasBlur = meta.getTextureBlur();
                        isClamped = meta.getTextureClamp();
                    }
                } catch (RuntimeException runtimeexception) {
                    System.out.println("Failed reading metadata of: " + this.textureLocation);
                    
                    runtimeexception.printStackTrace();
                }
            }
            
            TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), bufferedimage, hasBlur, isClamped);
        } finally {
            if (inputstream != null) {
                inputstream.close();
            }
        }
    }
}
