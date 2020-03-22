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

package me.do_you_like.mods.skinchanger.gui;

import java.awt.Color;

import me.do_you_like.mods.skinchanger.gui.additional.ModOptionsMenu;
import me.do_you_like.mods.skinchanger.gui.additional.PlayerSelectMenu;
import me.do_you_like.mods.skinchanger.gui.additional.PlayerSelectMenu.StringSelectionType;
import me.do_you_like.mods.skinchanger.utils.game.ChatColor;
import me.do_you_like.mods.skinchanger.utils.gui.impl.ModernCheckbox;
import me.do_you_like.mods.skinchanger.utils.gui.options.SelectionOptions;
import me.do_you_like.mods.skinchanger.utils.gui.player.FakePlayer;
import me.do_you_like.mods.skinchanger.utils.gui.impl.ModernButton;
import me.do_you_like.mods.skinchanger.utils.gui.ModernGui;
import me.do_you_like.mods.skinchanger.utils.gui.impl.ModernHeader;
import me.do_you_like.mods.skinchanger.utils.gui.impl.ModernSlider;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class SkinChangerMenu extends ModernGui {

    private static FakePlayer fakePlayer = new FakePlayer(Minecraft.getMinecraft().thePlayer);

    private SelectionOptions selectionOptions = new SelectionOptions();
    private ModOptionsMenu optionsMenu;

    private PlayerSelectMenu p_playerSelectMenu;
    private PlayerSelectMenu p_urlSelectMenu;

    private PlayerSelectMenu c_playerSelectMenu;
    private PlayerSelectMenu c_urlSelectMenu;

    // Store the basic values.
    private ResourceLocation originalSkin = Minecraft.getMinecraft().thePlayer.getLocationSkin();
    private ResourceLocation originalCape = Minecraft.getMinecraft().thePlayer.getLocationCape();

    private ModernButton m_optionsButton;
    private ModernButton m_revertButton;
    private ModernButton m_applyButton;

    private SkinChangerMenu instance;

    protected float rotation = 0;

    public SkinChangerMenu() {
        this.instance = this;
    }

    // ModernHeader hack
    //
    // For the middle of the left half of the screen do:
    // xPosition = (this.width / 2) / scale
    //
    // For the middle of the right half of the screen do:
    // xPosition = (this.width / 2) * scale

    @Override
    public final void onGuiOpen() {
//        this.sliderList.add(new ModernSlider(5, this.width / 2 - 100, this.height / 2 + 74, 200, 20, "Scale: ", 1.0F, 200.0F, 100.0F) {
//            @Override
//            public void onSliderUpdate() {
//                System.out.println(getValue() / 100);
//
//                for (ModernHeader header : SkinChangerMenu.this.headerList) {
//                    if (header == title) {
//                        continue;
//                    }
//
//                    header.setScaleSize((float) (getValue() / 100.0D));
//                }
//            }
//        });

        float bottomPosBox = this.height - 20;

        bottomPosBox -= 5;

        float leftPosBox = this.width / 2 + 20;
        float rightPosBox = this.width - 20;

        // 20 pixel margins.
        leftPosBox += 20;
        rightPosBox -= 20;

        float baseButtonWidth = ((rightPosBox - leftPosBox) / 2) - 2;

        float buttonLeftXPos = ((leftPosBox + rightPosBox) / 2) - baseButtonWidth;
        float buttonRightXPos = rightPosBox - baseButtonWidth;

        ModernButton revertButton = new ModernButton(50, (int) buttonLeftXPos - 2, (int) bottomPosBox - 20,(int) baseButtonWidth, 20, "Revert");
        ModernButton confirmButton = new ModernButton(51, (int) buttonRightXPos, (int) bottomPosBox - 20,(int) baseButtonWidth, 20, "Confirm");

        registerElement(revertButton);
        registerElement(confirmButton);

        this.m_revertButton = revertButton;
        this.m_applyButton = confirmButton;

        bottomPosBox -= 25;

        float sliderHeight = 20;
        float sliderWidth = rightPosBox - leftPosBox;

        float sliderXPos = ((leftPosBox + rightPosBox) / 2) - sliderWidth / 2;
        float sliderYPos = bottomPosBox - sliderHeight;

        ModernSlider slider = new ModernSlider(6, (int) sliderXPos, (int) sliderYPos, (int) sliderWidth, (int) sliderHeight, "Rotation: ", "\u00B0", 0.0F, 360.0F, this.rotation) {
            @Override
            public void onSliderUpdate() {
                SkinChangerMenu.this.rotation = (float) getValue();

                setRotation((float) getValue());
            }
        };

        registerElement(slider.disableTranslatable());

        ModernButton modSettingsButton = new ModernButton(101, this.width - 20 - 25, 20, 25, 25, "\u2699");

        registerElement(modSettingsButton);

        this.m_optionsButton = modSettingsButton;

        onGuiInitExtra();
    }

    @Override
    public void preRender(int mouseX, int mouseY) {
        drawDefaultBackground();

        GlStateManager.pushMatrix();
    }

    @Override
    public void onRender(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.pushMatrix();

        GlStateManager.scale(0.75F, 0.75F, 0F);
        drawCenteredString(this.fontRendererObj, "by boomboompower", (int) (((this.width / 4) + 10) / 0.75F), (int) (20 / 0.75F), Color.WHITE.getRGB());

        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();

        GlStateManager.scale(1.5F, 1.5F, 0F);
        drawCenteredString(this.fontRendererObj, "SkinChanger", (int) (((this.width / 4) + 10) / 1.5F), 6, Color.WHITE.getRGB());

        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();

        // 20 pixel margin.
        drawRect(this.width / 2 + 20, 20, this.width - 20, this.height - 20, new Color(0.7F, 0.7F, 0.7F, 0.2F).getRGB());

        drawCenteredString(this.fontRendererObj, "Player Model", ((this.width / 2 + 20) + (this.width - 20)) / 2, 30, Color.WHITE.getRGB());

        GlStateManager.popMatrix();
    }

    @Override
    public void postRender() {
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();

        int scale = (int) ((1.5 * this.width) / 10);

        // Stops clipping of entity. (Pushes it closer to the camera).
        GlStateManager.translate(0, 0, 100);

        drawEntityWithRot(((this.width / 2 + 20) + (this.width - 20)) / 2, this.height - 10 - scale, scale, this.rotation);

        GlStateManager.popMatrix();
    }

    @Override
    public final void buttonPressed(ModernButton button) {
        switch (button.getId()) {
            case 50:
                fakePlayer.copyFrom(this.mc.thePlayer);

                return;
            case 51:
                this.selectionOptions.setSkin(this.mc.thePlayer, fakePlayer.getPlayerInfo().getLocationSkin(), null);
                this.selectionOptions.setCape(this.mc.thePlayer, fakePlayer.getPlayerInfo().getLocationCape(), null);

                sendChatMessage(ChatColor.GREEN + "Your skin & cape have been applied!");
                return;
            case 101:
                if (this.m_optionsButton != null && this.m_optionsButton.getText().equalsIgnoreCase("\u2190")) {
                    this.instance.display();

                    return;
                }

                if (this.optionsMenu == null) {
                    this.optionsMenu = new ModOptionsMenu(this);
                }

                this.optionsMenu.display();

                return;
            default:
                System.out.println(button.getText() + " : " + button.getId());
        }

        onButtonPressedExtra(button);
    }

    @Override
    public void onKeyTyped(int keyCode, char keyCharacter) {

    }

    @Override
    public void onScrollUp() {
        this.yTranslation += 6;
    }

    @Override
    public void onScrollDown() {
        this.yTranslation -= 6;
    }

    /**
     * Override to change the buttons which appear on the left of the screen
     */
    protected void onGuiInitExtra() {
        int buttonWidth = this.mc.fontRendererObj.getStringWidth("Load from Player") + 5;

        ModernHeader skinSettings = new ModernHeader(this, 15, 30, "Skin Settings", 1.24F, true, Color.WHITE);

        skinSettings.setOffsetBetweenDrawables(24F);

        skinSettings.getSubDrawables().add(new ModernButton(12, 5, 20, buttonWidth, 20, "Load from Player").setAsPartOfHeader(skinSettings));
        skinSettings.getSubDrawables().add(new ModernButton(13, 5, 20, buttonWidth, 20, "Load from URL").setAsPartOfHeader(skinSettings));
        skinSettings.getSubDrawables().add(new ModernButton(14, 5, 20, buttonWidth, 20, "Load from File").setAsPartOfHeader(skinSettings));
        skinSettings.getSubDrawables().add(new ModernButton(15, 5, 20, buttonWidth, 20, "Reset Skin").setAsPartOfHeader(skinSettings));

        // ----------------------------------

        int capeSettingY = this.height / 2;

        if (skinSettings.getY() + skinSettings.getHeightOfHeader() > capeSettingY) {
            capeSettingY = skinSettings.getY() + skinSettings.getHeightOfHeader() + 24;
        }

        ModernHeader capeSettings = new ModernHeader(this, 15, capeSettingY, "Cape Settings", 1.24F, true, Color.WHITE);

        capeSettings.setOffsetBetweenDrawables(24F);

        capeSettings.getSubDrawables().add(new ModernButton(16, 5, 20, buttonWidth, 20, "Load from Player").setAsPartOfHeader(capeSettings));
        capeSettings.getSubDrawables().add(new ModernButton(17, 5, 20, buttonWidth, 20, "Load from URL").setAsPartOfHeader(capeSettings));
        capeSettings.getSubDrawables().add(new ModernButton(18, 5, 20, buttonWidth, 20, "Load from File").setAsPartOfHeader(capeSettings));
        capeSettings.getSubDrawables().add(new ModernButton(19, 5, 20, buttonWidth, 20, "Reset Cape").setAsPartOfHeader(capeSettings));

        // ----------------------------------

        ModernHeader recentSkins = new ModernHeader(this, skinSettings.getX() + skinSettings.getWidthOfHeader() + 20, 30, "Recent Skins", 1.24F, true, Color.WHITE);

        // ----------------------------------

        ModernHeader recentCapes = new ModernHeader(this, capeSettings.getX() + capeSettings.getWidthOfHeader() + 20, capeSettingY, "Recent Capes", 1.24F, true, Color.WHITE);

        // ----------------------------------

        ModernCheckbox checkbox = new ModernCheckbox(this.width / 2 - 150, this.height / 2 - 150, 300, 300);

        registerElement(skinSettings);
        registerElement(capeSettings);
        registerElement(recentSkins);
        registerElement(recentCapes);
        registerElement(checkbox);
    }

    private void drawEntityWithRot(int posX, int posY, int scale, float rotation) {
        FakePlayer entity = fakePlayer;

        GlStateManager.enableColorMaterial();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.pushMatrix();

        GlStateManager.translate((float) posX, (float) posY, 50.0F);
        GlStateManager.scale((float) (-scale), (float) scale, (float) scale);

        GlStateManager.rotate(180.0F, 0.0F, 0.0F , 1.0F);

        // Rotates based on the rotation variable
        GlStateManager.rotate(rotation, 0F, 270F, 0F);

        // Store original values
        //float prevSwingProgress = entity.swingProgress;
        float prevYawOffset = entity.renderYawOffset;
        float prevYaw = entity.rotationYaw;
        float prevPitch = entity.rotationPitch;
        float prevYawRotation = entity.prevRotationYawHead;
        float prevHeadRotation = entity.rotationYawHead;

        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);

        RenderHelper.enableStandardItemLighting();

        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);

        //entity.swingProgress = System.currentTimeMillis() % 15 / 100;
        entity.renderYawOffset = 0.0F;
        entity.rotationYaw = 0.0F;
        entity.rotationPitch = 0.0F;
        entity.rotationYawHead = entity.rotationYaw;
        entity.prevRotationYawHead = entity.rotationYaw;

        entity.prevChasingPosX = 2;
        entity.chasingPosX = 0;

        entity.prevChasingPosY = 0;
        entity.chasingPosY = 0;

        entity.prevChasingPosZ = 0;
        entity.chasingPosZ = 0;

        entity.prevRenderYawOffset = 0;
        entity.prevCameraYaw = 0;

        entity.prevDistanceWalkedModified = 1;
        entity.distanceWalkedModified = 0;

        // Simulate player movement
        entity.limbSwingAmount += (0.6F - entity.limbSwingAmount) * 0.4F;
        entity.limbSwing += (entity.limbSwingAmount) / 6;

        entity.prevPosX = 0;
        entity.posX = 0;

        entity.prevPosY = 0;
        entity.posY = 0;

        entity.prevPosZ = entity.posZ;

        //entity.posZ = Math.sin((System.currentTimeMillis() % (720 * 1.5)) * (Math.PI / (180 * 3)));
        //entity.posZ += entity.prevPosZ / 10;

        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();

        float capeSwing = MathHelper.cos(entity.limbSwing / 2 * 0.662F) * 1.1F * entity.limbSwingAmount / 2;

        entity.posZ = lerp(0, capeSwing, 0.5F);
        entity.posZ += 0.5;

        GlStateManager.disableLighting();

        rendermanager.setPlayerViewY(rotation);
        rendermanager.setRenderShadow(false);
        rendermanager.doRenderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, true);
        rendermanager.setRenderShadow(true);

        GlStateManager.enableLighting();

        //entity.swingProgress = prevSwingProgress;
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

    protected float lerp(float point1, float point2, float alpha) {
        return point1 + alpha * (point2 - point1);
    }

    protected void onButtonPressedExtra(ModernButton button) {
        switch (button.getId()) {
            case 12:
                // Minecraft.getMinecraft().getRenderManager().getSkinMap().get(this.fakePlayer.getPlayerInfo().getSkinType()).addLayer()

                if (this.p_playerSelectMenu == null) {
                    this.p_playerSelectMenu = new PlayerSelectMenu(this, StringSelectionType.P_USERNAME);
                }

                this.p_playerSelectMenu.display();

                break;
            case 13:
                if (this.p_playerSelectMenu == null) {
                    this.p_playerSelectMenu = new PlayerSelectMenu(this, StringSelectionType.P_URL);
                }

                this.p_playerSelectMenu.display();

                break;
            case 14:
                this.selectionOptions.loadFromFile((location) -> fakePlayer.getPlayerInfo().setLocationSkin(location), false);

                break;
            case 15:
                fakePlayer.getPlayerInfo().setLocationSkin(this.originalSkin);

                break;

            // Cape Settings
            case 16:
                if (this.c_playerSelectMenu == null) {
                    this.c_playerSelectMenu = new PlayerSelectMenu(this, StringSelectionType.C_USERNAME);
                }

                this.c_playerSelectMenu.display();

                break;
            case 17:
                if (this.c_playerSelectMenu == null) {
                    this.c_playerSelectMenu = new PlayerSelectMenu(this, StringSelectionType.C_URL);
                }

                this.c_playerSelectMenu.display();

                break;
            case 18:
                this.selectionOptions.loadFromFile((location) -> fakePlayer.getPlayerInfo().setLocationCape(location), true);

                break;
            case 19:
                fakePlayer.getPlayerInfo().setLocationCape(this.originalCape);

                break;
        }
    }

    protected final void setAsSubMenu(SkinChangerMenu menu) {
        if (this.m_applyButton != null) {
            this.m_applyButton.setEnabled(false);
        }

        if (this.m_revertButton != null) {
            this.m_revertButton.setEnabled(false);
        }

        if (this.m_optionsButton != null) {
            this.m_optionsButton.setText("\u2190");
        }

        if (this.p_playerSelectMenu != null) {
            this.p_playerSelectMenu.rotation = menu.rotation;
        }

        if (this.p_urlSelectMenu != null) {
            this.p_urlSelectMenu.rotation = menu.rotation;
        }

        if (this.c_playerSelectMenu != null) {
            this.c_playerSelectMenu.rotation = menu.rotation;
        }

        if (this.c_urlSelectMenu != null) {
            this.c_urlSelectMenu.rotation = menu.rotation;
        }

        this.rotation = menu.rotation;

        this.instance = menu;
    }

    public void handleIncomingInput(String playerName) {

    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public static FakePlayer getFakePlayer() {
        return fakePlayer;
    }
}
