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

import com.google.common.collect.Lists;

import java.awt.Color;
import java.util.List;

import me.do_you_like.mods.skinchanger.SkinChangerMod;
import me.do_you_like.mods.skinchanger.utils.game.ChatColor;
import me.do_you_like.mods.skinchanger.utils.gui.ModernDrawable;
import me.do_you_like.mods.skinchanger.utils.gui.UISkeleton;
import me.do_you_like.mods.skinchanger.utils.gui.lock.UILock;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * ModernGui, a better-looking GuiScreen which has more optimizations and features than the normal GuiScreen
 *
 * @author boomboompower
 * @version 4.0
 */
public abstract class ModernGui extends UILock implements UISkeleton {

    protected final Minecraft mc = Minecraft.getMinecraft();
    protected final FontRenderer fontRendererObj = this.mc.fontRendererObj;
    protected final SkinChangerMod mod = SkinChangerMod.getInstance();

    protected List<ModernTextBox> textList = Lists.newArrayList();
    protected List<ModernButton> buttonList = Lists.newArrayList();
    protected List<ModernHeader> headerList = Lists.newArrayList();
    protected List<ModernSlider> sliderList = Lists.newArrayList();

    private ModernButton selectedButton;
    private ModernSlider selectedSlider;

    @Override
    public final void initGui() {
        this.textList.clear();
        this.buttonList.clear();
        this.sliderList.clear();
        this.headerList.clear();

        onGuiOpen();
    }

    @Override
    public final void onGuiClosed() {
        onGuiClose();
    }

    @Override
    public final void drawScreen(int mouseX, int mouseY, float partialTicks) {
        try {
            preRender();
        } catch (Exception ex) {
            drawCenteredString(this.fontRendererObj, "An error occurred during preRender();", 0, 0, Color.RED.getRGB());

            for (int i = 0; i < ex.getStackTrace().length; i++) {
                StackTraceElement element = ex.getStackTrace()[i];

                drawCenteredString(this.fontRendererObj, element.toString(), 5, 12 + (i * 12), Color.RED.getRGB());
            }

            return;
        }

        try {
            onRender(mouseX, mouseY, partialTicks);
        } catch (Exception ex) {
            drawCenteredString(this.fontRendererObj, "An error occurred during onRender();", 0, 0, Color.RED.getRGB());

            for (int i = 0; i < ex.getStackTrace().length; i++) {
                StackTraceElement element = ex.getStackTrace()[i];

                drawCenteredString(this.fontRendererObj, element.toString(), 5, 12 + (i * 12), Color.RED.getRGB());
            }

            return;
        }

        for (ModernButton button : this.buttonList) {
            button.render(mouseX, mouseY);
        }

        for (GuiLabel label : this.labelList) {
            label.drawLabel(this.mc, mouseX, mouseY);
        }

        for (ModernTextBox text : this.textList) {
            text.drawTextBox();
        }

        for (ModernSlider slider : this.sliderList) {
            slider.drawButton(this.mc, mouseX, mouseY);
        }

        for (ModernHeader header : this.headerList) {
            GlStateManager.pushMatrix();

            header.render(mouseX, mouseY);

            GlStateManager.popMatrix();
        }

        try {
            postRender();
        } catch (Exception ex) {
            drawCenteredString(this.fontRendererObj, "An error occurred during postRender();", 0, 0, Color.RED.getRGB());

            for (int i = 0; i < ex.getStackTrace().length; i++) {
                StackTraceElement element = ex.getStackTrace()[i];

                drawCenteredString(this.fontRendererObj, element.toString(), 5, 12 + (i * 12), Color.RED.getRGB());
            }
        }
    }

    @Override
    protected final void keyTyped(char typedChar, int keyCode) {
        onKeyTyped(keyCode, typedChar);

        if (keyCode == 1) {
            this.mc.displayGuiScreen(null);

            if (this.mc.currentScreen == null) {
                this.mc.setIngameFocus();
            }
        } else {
            for (ModernTextBox text : textList) {
                text.textboxKeyTyped(typedChar, keyCode);
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            for (ModernHeader header : this.headerList) {
                if (header.getSubDrawables().size() > 0) {
                    for (ModernDrawable drawable : header.getSubDrawables()) {
                        if (drawable instanceof ModernButton) {
                            ModernButton button = (ModernButton) drawable;

                            if (button.mousePressed(this.mc, mouseX, mouseY)) {
                                this.selectedButton = button;

                                button.playPressSound(this.mc.getSoundHandler());

                                buttonPressed(button);
                            }
                        }
                    }
                }
            }

            for (ModernButton button : this.buttonList) {
                if (button.mousePressed(this.mc, mouseX, mouseY)) {
                    this.selectedButton = button;

                    button.playPressSound(this.mc.getSoundHandler());

                    this.buttonPressed(button);
                }
            }
        }

        if (mouseButton == 1) {
            for (ModernHeader header : this.headerList) {
                if (header.getSubDrawables().size() > 0) {
                    for (ModernDrawable drawable : header.getSubDrawables()) {
                        if (drawable instanceof ModernButton) {
                            ModernButton button = (ModernButton) drawable;

                            if (button.mousePressed(this.mc, mouseX, mouseY)) {
                                rightClicked(button);
                            }
                        }
                    }
                }
            }

            for (ModernButton button : this.buttonList) {
                if (button != null) {
                    if (button.mousePressed(this.mc, mouseX, mouseY)) {
                        this.rightClicked(button);
                    }
                }
            }
        }

        for (ModernTextBox text : this.textList) {
            text.mouseClicked(mouseX, mouseY, mouseButton);
        }

        for (ModernSlider slider : this.sliderList) {
            if (slider.mousePressed(this.mc, mouseX, mouseY)) {
                this.selectedSlider = slider;
            }
        }
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        // Required for compatibility
        super.mc = mc;

        this.itemRender = mc.getRenderItem();
        this.width = width;
        this.height = height;

        this.textList.clear();
        this.buttonList.clear();
        this.headerList.clear();

        initGui();
    }

    @Override
    public final void updateScreen() {
        for (ModernTextBox textBox : this.textList) {
            textBox.updateCursorCounter();
        }
    }

    @Override
    protected final void mouseReleased(int mouseX, int mouseY, int state) {
        if (this.selectedButton != null && state == 0) {
            this.selectedButton.mouseReleased(mouseX, mouseY);
            this.selectedButton = null;
        }

        if (this.selectedSlider != null && state == 0) {
            this.selectedSlider.mouseReleased(mouseX, mouseY);
            this.selectedSlider = null;
        }
    }

    /**
     * Draws multiple lines on the screen
     *
     * @param startingX the starting x position of the text
     * @param startingY the starting y position of the text
     * @param separation the Y valye  separatation between each line
     * @param lines the lines which will be drawn
     */
    public void writeInformation(int startingX, int startingY, int separation, String... lines) {
        writeInformation(startingX, startingY, separation, true, lines);
    }

    /**
     * Draws multiple lines on the screen
     *
     * @param startingX the starting x position of the text
     * @param startingY the starting y position of the text
     * @param separation the Y valye separatation between each line
     * @param centered true if the text being rendered should be rendered as a centered string
     * @param lines the lines which will be drawn
     */
    public void writeInformation(int startingX, int startingY, int separation, boolean centered, String... lines) {
        if (lines == null || lines.length == 0) {
            return;
        }

        // Loop through the lines
        for (String line : lines) {
            // Null components will be treated as an empty string
            if (line == null) {
                line = "";
            }

            if (centered) {
                drawCenteredString(this.fontRendererObj, ChatColor.translateAlternateColorCodes('&', line), startingX, startingY, Color.WHITE.getRGB());
            } else {
                drawString(this.fontRendererObj, ChatColor.translateAlternateColorCodes('&', line), startingX, startingY, Color.WHITE.getRGB());
            }

            startingY += separation;
        }
    }

    public final void display() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public final void onTick(TickEvent.ClientTickEvent event) {
        MinecraftForge.EVENT_BUS.unregister(this);
        Minecraft.getMinecraft().displayGuiScreen(this);
    }
    
    /**
     * Code used to draw an entity on screen, stolen from the Inventory code
     *
     * @param posX the x position of the entity
     * @param posY the y position of the entity
     * @param scale the scale the entity should be rendered at
     * @param mouseX the x location of the mouse
     * @param mouseY the y location of the mouse
     * @param entity the entity to render on screen
     * @param previewCape true if the entities cape is being previewed
     */
    protected final void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, EntityLivingBase entity, boolean previewCape) {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) posX, (float) posY, 50.0F);
        GlStateManager.scale((float) (-scale), (float) scale, (float) scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F , 1.0F);
        if (previewCape) {
            GlStateManager.rotate(180F, 0F, 360F, 0F);
        }
        float prevYawOffset = entity.renderYawOffset;
        float prevYaw = entity.rotationYaw;
        float prevPitch = entity.rotationPitch;
        float prevYawRotation = entity.prevRotationYawHead;
        float prevHeadRotation = entity.rotationYawHead;
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-((float) Math.atan((double) (mouseY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
        entity.renderYawOffset = previewCape ? -(float) Math.atan((double) (mouseX / 40.0F)) * 20.0F : (float) Math.atan((double) (mouseX / 40.0F)) * 20.0F;
        entity.rotationYaw = previewCape ? -(float) Math.atan((double) (mouseX / 40.0F)) * 40.0F : (float) Math.atan((double) (mouseX / 40.0F)) * 40.0F;
        entity.rotationPitch = -((float) Math.atan((double) (mouseY / 40.0F))) * 20.0F;
        entity.rotationYawHead = entity.rotationYaw;
        entity.prevRotationYawHead = entity.rotationYaw;
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(previewCape ? 180.0F : 0.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.doRenderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, true);
        rendermanager.setRenderShadow(true);
        entity.renderYawOffset = prevYawOffset;
        entity.rotationYaw = prevYaw;
        entity.rotationPitch = prevPitch;
        entity.prevRotationYawHead = prevYawRotation;
        entity.rotationYawHead = prevHeadRotation;

        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }
}
