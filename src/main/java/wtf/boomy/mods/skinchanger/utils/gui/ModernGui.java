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

package wtf.boomy.mods.skinchanger.utils.gui;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import org.lwjgl.input.Mouse;

import wtf.boomy.mods.skinchanger.SkinChangerMod;
import wtf.boomy.mods.skinchanger.utils.gui.faces.InteractiveUIElement;
import wtf.boomy.mods.skinchanger.utils.gui.faces.ModernUIElement;
import wtf.boomy.mods.skinchanger.utils.gui.faces.UISkeleton;
import wtf.boomy.mods.skinchanger.utils.gui.impl.ModernButton;
import wtf.boomy.mods.skinchanger.utils.gui.impl.ModernTextBox;

import java.awt.Color;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ModernGui, a better-looking GuiScreen which has more optimizations and features than the normal GuiScreen
 *
 * @author boomboompower
 * @version 4.3
 * @since 2.0.0
 */
public abstract class ModernGui extends UILock implements UISkeleton {
    
    protected final Minecraft mc = Minecraft.getMinecraft();
    protected final FontRenderer fontRendererObj = this.mc.fontRendererObj;
    protected final SkinChangerMod mod = SkinChangerMod.getInstance();
    
    private final List<ModernTextBox> textList = Lists.newArrayList();
    private final List<ModernUIElement> modernList = Lists.newLinkedList();
    
    /**
     * Do not use this. Use {@link #modernList} instead since it contains support for all Modern Types.
     *
     * @deprecated Superseded by {@link #modernList}
     */
    @Deprecated
    private final List<ModernButton> buttonList = Collections.emptyList();
    
    private final List<InteractiveUIElement> selectedElements = Lists.newArrayList();
    
    protected float yTranslation = 0;
    
    protected EntityPlayerSP player;
    
    @Override
    public final void initGui() {
        this.player = this.mc.thePlayer;
        
        this.textList.clear();
        this.modernList.clear();
        
        onGuiOpen();
    }
    
    @Override
    public final void onGuiClosed() {
        onGuiClose();
    }
    
    @Override
    public final void drawScreen(int mouseX, int mouseY, float partialTicks) {
        try {
            preRender(mouseX, mouseY, partialTicks);
        } catch (Exception ex) {
            drawString(this.fontRendererObj, "An error occurred during preRender();", 5, 5, Color.RED.getRGB());
            
            drawError(ex);
            
            return;
        }
        
        try {
            GlStateManager.pushMatrix();
            
            onRender(mouseX, mouseY, partialTicks);
            
            GlStateManager.popMatrix();
        } catch (Exception ex) {
            drawString(this.fontRendererObj, "An error occurred during onRender();", 5, 5, Color.RED.getRGB());
            
            drawError(ex);
        }
        
        try {
            for (ModernUIElement element : this.modernList) {
                GlStateManager.pushMatrix();
                
                if (element.isTranslatable()) {
                    GlStateManager.translate(0, this.yTranslation, 0);
                }
                
                element.render(mouseX, mouseY, this.yTranslation);
                
                if (element.isTranslatable()) {
                    GlStateManager.translate(0, -this.yTranslation, 0);
                }
                
                GlStateManager.popMatrix();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        for (ModernTextBox text : this.textList) {
            GlStateManager.pushMatrix();
            
            text.drawTextBox();
            
            GlStateManager.popMatrix();
        }
        
        try {
            postRender(partialTicks);
        } catch (Exception ex) {
            drawString(this.fontRendererObj, "An error occurred during postRender();", 5, 5, Color.RED.getRGB());
            
            drawError(ex);
        }
        
        if (this.mod.getApagogeHandler().getBuildType() == -1) {
            GlStateManager.pushMatrix();
            
            drawString(this.fontRendererObj, "Open Beta - Subject to change", 5, this.height - 10, Color.LIGHT_GRAY.getRGB());
            
            GlStateManager.popMatrix();
        }
    
        for (ModernUIElement element : this.modernList) {
            if (!(element instanceof ModernButton)) {
                continue;
            }
        
            // For drawing text on the screen when hovered.
            if (((ModernButton) element).isHovered() && ((ModernButton) element).getMessageLines() != null) {
                GlStateManager.pushMatrix();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.disableAlpha();
            
                drawHoveringText(((ModernButton) element).getMessageLines(), mouseX, mouseY, this.fontRendererObj);
            
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.enableAlpha();
                GlStateManager.popMatrix();
            }
        }
    }
    
    @Override
    protected final void keyTyped(char typedChar, int keyCode) {
        onKeyTyped(keyCode, typedChar);
        
        if (keyCode == 1) {
            close();
            
            return;
        }
    
        for (ModernTextBox text : textList) {
            text.onKeyTyped(typedChar, keyCode);
        }
    }
    
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        this.modernList.stream()
                .filter(element -> element instanceof InteractiveUIElement)
                .filter(ModernUIElement::isEnabled)
                .filter(element -> ((InteractiveUIElement) element).isInside(mouseX, mouseY, this.yTranslation))
                .collect(Collectors.toList())
                .forEach(dummy -> {
                    InteractiveUIElement element = (InteractiveUIElement) dummy;
    
                    if (!(element instanceof ModernButton) && !element.isInside(mouseX, mouseY, this.yTranslation)) {
                        return;
                    } else if (element instanceof ModernButton && !((ModernButton) element).isHovered()) {
                        return;
                    }
                    
                    switch (mouseButton) {
                        case 0:
                            element.onLeftClick(mouseX, mouseY, this.yTranslation);
        
                            this.selectedElements.add(element);
        
                            if (element instanceof ModernButton) {
                                buttonPressed((ModernButton) element);
                            }
                            break;
                        case 1:
                            element.onRightClick(mouseX, mouseY, this.yTranslation);
                            break;
                        case 2:
                            element.onMiddleClick(mouseX, mouseY, this.yTranslation);
                            break;
                        default:
                            System.err.println("Unimplemented click (ID: " + mouseButton + "). Are you running the environment correctly?");
                    }
        });
        
        try {
            for (ModernTextBox text : this.textList) {
                text.mouseClicked(mouseX, mouseY, mouseButton);
            }
        } catch (NullPointerException exception) {
            exception.printStackTrace();
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
        this.modernList.clear();
        
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
        if (state != 0) {
            return;
        }
        
        this.selectedElements.forEach((d) -> d.onMouseReleased(mouseX, mouseY, this.yTranslation));
        this.selectedElements.clear();
    }
    
    @Override
    public final void onMouse() {
        int i = Mouse.getEventDWheel();
        
        if (i < 0) {
            onScrollDown();
        } else if (i > 0) {
            onScrollUp();
        }
    }
    
    /**
     * Registers an element into the element loader.
     *
     * @param element the element to register. Can be a {@link ModernUIElement} or {@link ModernTextBox}
     */
    public final void registerElement(Object element) {
        if (element == null) {
            return;
        }
        
        if (element instanceof ModernUIElement) {
            this.modernList.add((ModernUIElement) element);
        } else if (element instanceof ModernTextBox) {
            this.textList.add((ModernTextBox) element);
        } else {
            System.err.println("Unable to register element (Elem: " + element.getClass() + ") as it was not a ModernDrawable.");
        }
    }
    
    /**
     * Registers a list of elements
     *
     * @param elementList a list of elements to register
     */
    public final void registerElements(List<?> elementList) {
        for (Object element : elementList) {
            registerElement(element);
        }
    }
    
    /**
     * Displays this GUI to the Minecraft client
     */
    public final void display() {
        this.mod.registerEvents(this);
    }
    
    /**
     * Closes this GUI and makes the game go into focus if possible
     */
    public final void close() {
        this.mc.displayGuiScreen(null);
        
        if (this.mc.currentScreen == null) {
            this.mc.setIngameFocus();
        }
    }
    
    /**
     * Opens the GUI on the next client tick
     *
     * @param event the client event
     */
    @SubscribeEvent
    public final void onTick(TickEvent.ClientTickEvent event) {
        Minecraft.getMinecraft().displayGuiScreen(this);
        
        this.mod.unregisterEvents(this);
    }
    
    /**
     * Draws a hollow rectangle on the screen
     *
     * @param startX the starting x position of the rectangle
     * @param startY the starting y position of the rectangle
     * @param endX   the ending x position of the rectangle
     * @param endY   the ending y position of the rectangle
     * @param color  the color of the rectangle
     */
    public static void drawRectangleOutline(int startX, int startY, int endX, int endY, int color) {
        // Top
        drawHorizontalLine_(startX, endX, startY, color);
        
        // Right
        drawVerticalLine_(endX, startY, endY, color);
        
        // Bottom
        drawHorizontalLine_(startX, endX, endY, color);
        
        // Left
        drawVerticalLine_(startX, startY, endY, color);
    }
    
    /**
     * Draws a horizontal line (just a 1x2 rectangle)
     *
     * @param startX the starting x position of the line
     * @param endX   the ending x position of the line
     * @param y      the y position of the line
     * @param color  the color of the line
     */
    public static void drawHorizontalLine_(int startX, int endX, int y, int color) {
        if (endX < startX) {
            int i = startX;
            startX = endX;
            endX = i;
        }
        
        drawRect(startX, y, endX + 1, y + 1, color);
    }
    
    /**
     * Draws a vertical line (just a 2x1 rectangle)
     *
     * @param x      the x position of the line
     * @param startY the starting y position of the line
     * @param endY   the ending y position of the line
     * @param color  the color of the line
     */
    public static void drawVerticalLine_(int x, int startY, int endY, int color) {
        if (endY < startY) {
            int i = startY;
            startY = endY;
            endY = i;
        }
        
        drawRect(x, startY + 1, x + 1, endY, color);
    }
    
    // =============================================== PRIVATE ================================================= //
    
    private void drawError(Exception ex) {
        int startCount = 0;
        
        if (ex.getMessage() != null) {
            startCount = 1;
            
            drawString(this.fontRendererObj, ex.getMessage(), 10, 16, Color.RED.getRGB());
        }
        
        for (int i = startCount; i < ex.getStackTrace().length; i++) {
            StackTraceElement element = ex.getStackTrace()[i];
            
            drawString(this.fontRendererObj, element.toString(), 10, 16 + (i * 12), Color.RED.getRGB());
        }
    }
    
    public static void drawTexturedModalRect(float x, float y, float textureX, float textureY, float width, float height) {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, y + height, 0).tex(textureX * f, (textureY + height) * f1).endVertex();
        worldrenderer.pos(x + width, y + height, 0).tex((textureX + width) * f, (textureY + height) * f1).endVertex();
        worldrenderer.pos(x + width, y, 0).tex((textureX + width) * f, textureY * f1).endVertex();
        worldrenderer.pos(x, y, 0).tex(textureX * f, textureY * f1).endVertex();
        tessellator.draw();
    }
}
