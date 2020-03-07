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

import java.io.File;
import lombok.Getter;
import lombok.Setter;

import me.do_you_like.mods.skinchanger.utils.gui.ModernDrawable;
import me.do_you_like.mods.skinchanger.utils.resources.LocalFileData;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
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

    public ModernBlurBox(int xPos, int yPos, int width, int height) {

    }

    @Override
    public void render(int mouseX, int mouseY) {
        ResourceLocation location = new ResourceLocation("textures/misc/vignette.png");

        LocalFileData data = new LocalFileData(location, new File("icon.png"));

        GlStateManager.disableDepth();
        GlStateManager.disableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.alphaFunc(770, 771);

        GlStateManager.bindTexture(data.getGlTextureId());

        GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, this.x, this.y, this.x + this.width, this.y + this.height, 1);

        ScaledResolution scaler = new ScaledResolution(Minecraft.getMinecraft());
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer renderer = tessellator.getWorldRenderer();

        renderer.color(1.0F, 1.0F, 1.0F, 1.0F);
        renderer.begin(7, DefaultVertexFormats.POSITION_TEX); // 7 = OpenGl.GL_QUADS

        // tessellator.addVertexWithUV(x,y,z,u,v);
        // to
        // renderer.pos(x,y,z).tex(u,v).endVertex();

        renderer.pos(this.x / (scaler.getScaleFactor() * 2.0f), this.y + this.height / (scaler.getScaleFactor() * 2.0f), 0).tex(0.0f, 0.0f).endVertex();
        renderer.pos(this.x + this.width / (scaler.getScaleFactor() * 2.0f), this.y + this.height / (scaler.getScaleFactor() * 2.0f), 1.0F).tex(1.0f, 0.0f).endVertex();
        renderer.pos(this.x + this.width / (scaler.getScaleFactor() * 2.0f), this.y / (scaler.getScaleFactor() * 2.0f), 0).tex(1.0f, 1.0f).endVertex();
        renderer.pos( this.x / (scaler.getScaleFactor() * 2.0f), this.y / (scaler.getScaleFactor() * 2.0f), 0).tex(0.0f, 1.0f).endVertex();

        GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, this.x, this.y, this.x + this.width, this.y + this.height, 1);

        tessellator.draw();
    }

    @Override
    public void renderFromHeader(int xPos, int yPos, int mouseX, int mouseY, int recommendedYOffset) {

    }

    @Override
    public boolean renderRelativeToHeader() {
        return true;
    }

    @Override
    public ModernDrawable setAsPartOfHeader(ModernHeader parent) {
        return this;
    }
}
