/*
 *     Copyright (C) 2017 boomboompower
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

package me.boomboompower.skinchanger.gui;

import me.boomboompower.skinchanger.SkinChangerMod;
import me.boomboompower.skinchanger.capes.CapeManager;
import me.boomboompower.skinchanger.gui.utils.ModernButton;
import me.boomboompower.skinchanger.gui.utils.FakePlayerUtils;
import me.boomboompower.skinchanger.gui.utils.ModernTextBox;
import me.boomboompower.skinchanger.skins.SkinManager;
import me.boomboompower.skinchanger.utils.ChatColor;
import me.boomboompower.skinchanger.utils.GlobalUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;

public class SettingsGui extends GuiScreen {

    private FakePlayerUtils.FakePlayer fakePlayer = FakePlayerUtils.getFakePlayer();
    private SkinManager fakePlayerSkinManager = new SkinManager(fakePlayer, false);
    private CapeManager fakePlayerCapeManager = new CapeManager(fakePlayer, false);

    private ModernTextBox textField;

    private String message = "";

    private boolean previewCape = false;

    public SettingsGui() {
        this("");
    }

    public SettingsGui(String message) {
        this.message = message;

        SkinChangerMod.getInstance().getSkinManager().updatePlayer(null);
        fakePlayerSkinManager.replaceSkin(DefaultPlayerSkin.getDefaultSkinLegacy());
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        this.textField = new ModernTextBox(0, this.width / 2 - 150, this.height / 2 - 22, 300, 20);

        this.buttonList.add(new ModernButton(1, this.width / 2 - 160, this.height / 2 + 26, 150, 20, "Preview skin"));
        this.buttonList.add(new ModernButton(2, this.width / 2 - 160, this.height / 2 + 50, 150, 20, "Reset skin"));
        this.buttonList.add(new ModernButton(3, this.width / 2 - 160, this.height / 2 + 74, 150, 20, "Confirm skin"));

        this.buttonList.add(new ModernButton(4, this.width / 2 + 10, this.height / 2 + 26, 150, 20, "Preview cape"));
        this.buttonList.add(new ModernButton(5, this.width / 2 + 10, this.height / 2 + 50, 150, 20, "Reset cape"));
        this.buttonList.add(new ModernButton(6, this.width / 2 + 10, this.height / 2 + 74, 150, 20, "Add cape"));

        this.textField.setMaxStringLength(16);
        this.textField.setText(message);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawGuiBackground();

        textField.setEnabled(true);
        textField.drawTextBox();

        super.drawScreen(mouseX, mouseY, partialTicks);

        fontRendererObj.drawString("Skin Settings", this.width / 2 - 118, this.height / 2 + 8, Color.WHITE.getRGB(), false);
        fontRendererObj.drawString("Cape Settings", this.width / 2 + 50, this.height / 2 + 8, Color.WHITE.getRGB(), false);

        drawEntityOnScreen(this.width / 2, this.height / 2 - 45, 35, this.width / 2 - mouseX, (this.height / 2 - 90) - mouseY, fakePlayer);

        if (previewCape) {
            a("Preview Cape", this.width / 2, this.height / 2 - 40, Color.WHITE.getRGB());
        } else {
            a("Preview Skin", this.width / 2, this.height / 2 - 40, Color.WHITE.getRGB());
        }
        if (SkinChangerMod.getInstance().getWebsiteUtils().isDisabled()) {
            a(ChatColor.RED + "The mod is currently disabled and will not work!", this.width / 2, this.height / 2 + 98, Color.WHITE.getRGB());
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (SkinChangerMod.getInstance().getWebsiteUtils().isDisabled()) {
            sendChatMessage("SkinChangerMod is currently disabled, check back soon!");
            mc.displayGuiScreen(null);
            return;
        }
        switch (button.id) {
            case 1:
                previewCape = false;
                fakePlayerCapeManager.removeCape();
                if (!textField.getText().isEmpty() && textField.getText().length() >= 2) {
                    fakePlayerSkinManager.update(textField.getText());
                }
                break;
            case 2:
                SkinChangerMod.getInstance().getSkinManager().reset();
                sendChatMessage("Your skin has been reset!");
                mc.displayGuiScreen(null);
                break;
            case 3:
                if (!textField.getText().isEmpty() && textField.getText().length() >= 2) {
                    SkinChangerMod.getInstance().getSkinManager().update(textField.getText());
                    sendChatMessage(String.format("Your skin has been updated to %s!", ChatColor.GOLD + textField.getText() + ChatColor.GRAY));
                } else {
                    sendChatMessage("Not enough characters provided");
                    sendChatMessage("Use a name between 2 and 16 characters!");
                }
                mc.displayGuiScreen(null);
                break;
            case 4:
                previewCape = true;
                fakePlayerCapeManager.addCape();
                break;
            case 5:
                SkinChangerMod.getInstance().getCapeManager().removeCape();
                sendChatMessage("Your cape has been removed!");
                mc.displayGuiScreen(null);
                break;
            case 6:
                SkinChangerMod.getInstance().getCapeManager().addCape();
                sendChatMessage("You now have a cape!");
                mc.displayGuiScreen(null);
                break;
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        SkinChangerMod.getInstance().getLoader().save();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void keyTyped(char c, int key) {
        if (key == 1) {
            mc.displayGuiScreen(null);
        } else {
            textField.textboxKeyTyped(c, key);
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int btn) {
        try {
            super.mouseClicked(x, y, btn);
            textField.mouseClicked(x, y, btn);
        } catch (Exception ex) {}
    }

    @Override
    public void sendChatMessage(String msg) {
        GlobalUtils.sendChatMessage(msg);
    }

    public void display() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        MinecraftForge.EVENT_BUS.unregister(this);
        Minecraft.getMinecraft().displayGuiScreen(this);
    }

    public void drawGuiBackground() {
        long lastPress = System.currentTimeMillis();
        int color = Math.min(255, (int) (2L * (System.currentTimeMillis() - lastPress)));
        Gui.drawRect(0, 0, width, height, 2013265920 + (color << 16) + (color << 8) + color);
    }

    private void a(String message, int x, int y, int color) {
        fontRendererObj.drawString(message, (float) (x - fontRendererObj.getStringWidth(message) / 2), (float) y, color, false);
    }

    private void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, EntityLivingBase ent) {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) posX, (float) posY, 50.0F);
        GlStateManager.scale((float) (-scale), (float) scale, (float) scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F , 1.0F);
        if (previewCape) {
            GlStateManager.rotate(180F, 0F, 360F, 0F);
        }
        float f = ent.renderYawOffset;
        float f1 = ent.rotationYaw;
        float f2 = ent.rotationPitch;
        float f3 = ent.prevRotationYawHead;
        float f4 = ent.rotationYawHead;
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        if (!previewCape) {
            GlStateManager.rotate(-((float) Math.atan((double) (mouseY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
            ent.renderYawOffset = (float) Math.atan((double) (mouseX / 40.0F)) * 20.0F;
            ent.rotationYaw = (float) Math.atan((double) (mouseX / 40.0F)) * 40.0F;
            ent.rotationPitch = -((float) Math.atan((double) (mouseY / 40.0F))) * 20.0F;
        }
        ent.rotationYawHead = ent.rotationYaw;
        ent.prevRotationYawHead = ent.rotationYaw;
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(previewCape ? 180.0F : 0.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.doRenderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, true);
        rendermanager.setRenderShadow(true);
        ent.renderYawOffset = f;
        ent.rotationYaw = f1;
        ent.rotationPitch = f2;
        ent.prevRotationYawHead = f3;
        ent.rotationYawHead = f4;

        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }
}
