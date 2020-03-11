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

package me.do_you_like.mods.skinchanger.utils.gui.impl;

import java.awt.Color;
import java.io.File;

import lombok.Getter;
import lombok.Setter;

import me.do_you_like.mods.skinchanger.utils.gui.ModernDrawable;
import me.do_you_like.mods.skinchanger.utils.resources.LocalFileData;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class ModernBlurBox implements ModernDrawable {

    @Getter
    private int x;

    @Getter
    private int y;

    @Getter
    @Setter
    private int width;

    @Getter
    @Setter
    private int height;

    @Getter
    @Setter
    private boolean enabled;

    private boolean partOfHeader;
    private ModernHeader parent;
    
    public ModernBlurBox(int xPos, int yPos, int width, int height) {
        this.x = xPos;
        this.y = yPos;

        this.width = width;
        this.height = height;
    }

    @Override
    public void render(int mouseX, int mouseY) {
        renderAtPos(this.x, this.y);
    }

    @Override
    public void renderFromHeader(int xPos, int yPos, int mouseX, int mouseY, int recommendedYOffset) {
        renderAtPos(xPos, yPos + recommendedYOffset);
    }

    @Override
    public ModernDrawable setAsPartOfHeader(ModernHeader parent) {
        this.partOfHeader = true;
        this.parent = parent;
        
        return this;
    }

    private LocalFileData data = new LocalFileData(new ResourceLocation("textures/misc/vignette.png"), new File("icon.png"));

    private void renderAtPos(int xPos, int yPos) {
        if (!Minecraft.getMinecraft().isCallingFromMinecraftThread()) {
            Minecraft.getMinecraft().addScheduledTask(() -> renderAtPos(xPos, yPos));

            return;
        }

        GlStateManager.pushMatrix();

        Gui.drawRect(xPos, yPos, xPos + this.width, yPos + this.height, Color.WHITE.getRGB());

        // Adapted from https://gist.github.com/fkaa/1499181

        int xLoc = this.x;
        int yLoc = this.y;

        float frame = 30;

        float radiusTime = frame / 300.0f;
        float blurRadius = (MathHelper.sin(radiusTime) + 1.0f) * 1.0f;

        float tapTime = frame / 199.0f;
        float blurTap0Strength = (MathHelper.sin(tapTime) + 1.0f) * 0.5f;
        float blurTap1Strength = (MathHelper.sin(tapTime) + 1.0f) * 0.4f;
        float blurTap2Strength = (MathHelper.sin(tapTime) + 1.0f) * 0.3f;

        if(blurRadius >= 10.0f) {
            blurRadius = 10.0f;
        } else if(blurRadius <= 0.0f) {
            blurRadius = 0.0f;
        }

        GlStateManager.disableDepth();
        GlStateManager.disableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.alphaFunc(770, 771);

        GlStateManager.bindTexture(this.data.getGlTextureId());

        GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, xLoc, yLoc, xLoc + this.width, yLoc + this.height, 1);

        ScaledResolution scaler = new ScaledResolution(Minecraft.getMinecraft());
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer renderer = tessellator.getWorldRenderer();

        // tessellator.setColorRGBA_F(1.0f, 1.0f, 1.0f, 1.0f);
        // to
        // renderer.color(1.0F, 1.0F, 1.0F, 1.0F);

        renderer.color(1.0F, 1.0F, 1.0F, 1.0F);

        // tessellator.startDrawingQuads();
        // to
        // renderer.begin(7, DefaultVertexFormats.POSITION_TEX);

        renderer.begin(7, DefaultVertexFormats.POSITION_TEX); // 7 = OpenGl.GL_QUADS

        // tessellator.addVertexWithUV(x,y,z,u,v);
        // to
        // renderer.pos(x,y,z).tex(u,v).endVertex();

        renderer.pos(xLoc / (scaler.getScaleFactor() * 2.0f), yLoc + this.height / (scaler.getScaleFactor() * 2.0f), 0).tex(0.0f, 0.0f).endVertex();
        renderer.pos(xLoc + this.width / (scaler.getScaleFactor() * 2.0f), yLoc + this.height / (scaler.getScaleFactor() * 2.0f), 1.0F).tex(1.0f, 0.0f).endVertex();
        renderer.pos(xLoc + this.width / (scaler.getScaleFactor() * 2.0f), yLoc / (scaler.getScaleFactor() * 2.0f), 0).tex(1.0f, 1.0f).endVertex();
        renderer.pos( xLoc / (scaler.getScaleFactor() * 2.0f), yLoc / (scaler.getScaleFactor() * 2.0f), 0).tex(0.0f, 1.0f).endVertex();

        GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, xLoc, yLoc, xLoc + this.width, yLoc + this.height, 1);

        // Perform a horizontal gaussian blur
        renderer.begin(7, DefaultVertexFormats.POSITION_TEX);

        // Center top
        renderer.color(1.0F, 1.0F, 1.0F, 1.0F);

        renderer.pos(xLoc / scaler.getScaleFactor(), yLoc + this.height / scaler.getScaleFactor(), 0).tex(0.0f, 0.5f).endVertex();
        renderer.pos(xLoc + this.width / scaler.getScaleFactor(), yLoc + this.height / scaler.getScaleFactor(), 0).tex(0.5f, 0.5f).endVertex();
        renderer.pos(xLoc + this.width / scaler.getScaleFactor(), yLoc / scaler.getScaleFactor(), 0).tex(0.5f, 1.0f).endVertex();
        renderer.pos(xLoc / scaler.getScaleFactor(), yLoc / scaler.getScaleFactor(), 0).tex(0.0f, 1.0f).endVertex();

        // Tap +/- 1
        doHorizontalTap(scaler, renderer, blurRadius, xLoc, yLoc, 1);

        // Tap +/- 2
        renderer.color(1.0F, 1.0F, 1.0F, blurTap1Strength);
        doHorizontalTap(scaler, renderer, blurRadius, xLoc, yLoc, 2);

        // Tap +/- 3
        renderer.color(1.0F, 1.0F, 1.0F, blurTap2Strength);
        doHorizontalTap(scaler, renderer, blurRadius, xLoc, yLoc,3);

        // Finish horizontal
        tessellator.draw();

        GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, xLoc, yLoc, xLoc + this.width, yLoc + this.height, 1);

        // -----------------------------------------

        // Start vertical
        renderer.begin(7, DefaultVertexFormats.POSITION_TEX);

        // Center tap
        renderer.pos(xLoc / scaler.getScaleFactor(), yLoc + this.height / scaler.getScaleFactor(), 0).tex(0.0f, 0.5f).endVertex();
        renderer.pos(xLoc + this.width / scaler.getScaleFactor(), yLoc + this.height / scaler.getScaleFactor(), 0).tex(1.0f, 0.0f).endVertex();
        renderer.pos(xLoc + this.width / scaler.getScaleFactor(), yLoc / scaler.getScaleFactor(), 0).tex(1.0f, 1.0f).endVertex();
        renderer.pos(xLoc / scaler.getScaleFactor(), yLoc / scaler.getScaleFactor(), 0).tex(0.0f, 1.0f).endVertex();

        // Tap +/- 1
        renderer.color(1.0F, 1.0F, 1.0F, blurTap0Strength);
        doVerticalTap(scaler, renderer, blurRadius, xLoc, yLoc, 1);

        // Tap +/- 2
        renderer.color(1.0F, 1.0F, 1.0F, blurTap1Strength);
        doVerticalTap(scaler, renderer, blurRadius, xLoc, yLoc, 2);

        // Tap +/- 3
        renderer.color(1.0F, 1.0F, 1.0F, blurTap2Strength);
        doVerticalTap(scaler, renderer, blurRadius, xLoc, yLoc, 3);

        // Finish vertical
        tessellator.draw();

        // -----------------------------------------

        // Reset states
        GlStateManager.colorMask(true, true, true, false);
        GlStateManager.disableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.enableAlpha();
        GlStateManager.enableDepth();

        // Clear the color.
        renderer.color(1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.popMatrix();
    }

    private void doHorizontalTap(ScaledResolution scaler, WorldRenderer renderer, float blurRadius, int xLoc, int yLoc, float stage) {
        renderer.pos((xLoc - blurRadius * stage) / scaler.getScaleFactor(), yLoc + this.height / scaler.getScaleFactor(), 0).tex(0.0f, 0.5f).endVertex();
        renderer.pos((xLoc + this.width - blurRadius * stage) / scaler.getScaleFactor(), yLoc + this.height / scaler.getScaleFactor(), 0).tex(0.5f, 0.5f).endVertex();
        renderer.pos((xLoc + this.width - blurRadius * stage) / scaler.getScaleFactor(), yLoc / scaler.getScaleFactor(), 0).tex(0.5f, 1.0f).endVertex();
        renderer.pos((xLoc - blurRadius * stage) / scaler.getScaleFactor(), yLoc / scaler.getScaleFactor(), 0).tex(0.0f, 1.0f).endVertex();
        renderer.pos((xLoc + blurRadius * stage) / scaler.getScaleFactor(), yLoc + this.height / scaler.getScaleFactor(), 0).tex(0.0f, 0.5f).endVertex();
        renderer.pos((xLoc + this.width + blurRadius * stage) / scaler.getScaleFactor(), yLoc + this.height / scaler.getScaleFactor(), 0).tex(0.5f, 0.5f).endVertex();
        renderer.pos((xLoc + this.width + blurRadius * stage) / scaler.getScaleFactor(), yLoc / scaler.getScaleFactor(), 0).tex(0.5f, 1.0f).endVertex();
        renderer.pos((xLoc + blurRadius * stage) / scaler.getScaleFactor(), yLoc / scaler.getScaleFactor(), 0).tex(0.0f, 1.0f).endVertex();
    }

    private void doVerticalTap(ScaledResolution scaler, WorldRenderer renderer, float blurRadius, int xLoc, int yLoc, float stage) {
        renderer.pos((xLoc - 0.0f) / scaler.getScaleFactor(), (yLoc + this.height - blurRadius * stage) / scaler.getScaleFactor(), 0).tex(0.0f, 0.0f).endVertex();
        renderer.pos((xLoc + this.width - 0.0f) / scaler.getScaleFactor(), (yLoc + this.height - blurRadius * stage) / scaler.getScaleFactor(), 0).tex(1.0f, 0.0f).endVertex();
        renderer.pos((xLoc + this.width - 0.0f) / scaler.getScaleFactor(), (yLoc - blurRadius * stage) / scaler.getScaleFactor(), 0).tex(1.0f, 1.0f).endVertex();
        renderer.pos((xLoc - 0.0f) / scaler.getScaleFactor(), (yLoc - blurRadius * stage) / scaler.getScaleFactor(), 0).tex(0.0f, 1.0f).endVertex();
        renderer.pos((xLoc + 0.0f) / scaler.getScaleFactor(), (yLoc + this.height + blurRadius * stage) / scaler.getScaleFactor(), 0).tex(0.0f, 0.0f).endVertex();
        renderer.pos((xLoc + this.width + 0.0f) / scaler.getScaleFactor(), (yLoc + this.height + blurRadius * stage) / scaler.getScaleFactor(), 0).tex(1.0f, 0.0f).endVertex();
        renderer.pos((xLoc + this.width + 0.0f) / scaler.getScaleFactor(), (yLoc + blurRadius * stage) / scaler.getScaleFactor(), 0).tex(1.0f, 1.0f).endVertex();
        renderer.pos((xLoc + 0.0f) / scaler.getScaleFactor(), (yLoc + blurRadius * stage) / scaler.getScaleFactor(), 0).tex(0.0f, 1.0f).endVertex();
    }
}
