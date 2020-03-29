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
import java.lang.invoke.MethodHandle;
import java.net.MalformedURLException;
import java.net.URL;

import me.do_you_like.mods.skinchanger.cosmetic.impl.fakeplayer.FakePlayerRender;
import me.do_you_like.mods.skinchanger.gui.additional.ModOptionsMenu;
import me.do_you_like.mods.skinchanger.gui.additional.PlayerSelectMenu;
import me.do_you_like.mods.skinchanger.gui.additional.PlayerSelectMenu.StringSelectionType;
import me.do_you_like.mods.skinchanger.options.SelectionOptions;
import me.do_you_like.mods.skinchanger.utils.game.ChatColor;
import me.do_you_like.mods.skinchanger.utils.gui.ModernGui;
import me.do_you_like.mods.skinchanger.utils.gui.impl.ModernButton;
import me.do_you_like.mods.skinchanger.utils.gui.impl.ModernHeader;
import me.do_you_like.mods.skinchanger.utils.gui.impl.ModernScroller;
import me.do_you_like.mods.skinchanger.utils.gui.impl.ModernSlider;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

/**
 * The base SkinChanger menu, redesigned to be more user-friendly..
 *
 * @since 3.0.0
 */
public class SkinChangerMenu extends ModernGui {

    private final SelectionOptions selectionOptions = new SelectionOptions();
    private ModOptionsMenu optionsMenu;

    private PlayerSelectMenu p_playerSelectMenu;
    private PlayerSelectMenu p_urlSelectMenu;

    private PlayerSelectMenu c_playerSelectMenu;
    private PlayerSelectMenu c_urlSelectMenu;

    // Store the basic values.
    private final ResourceLocation originalSkin = Minecraft.getMinecraft().thePlayer.getLocationSkin();
    private final ResourceLocation originalCape = Minecraft.getMinecraft().thePlayer.getLocationCape();
    private final String originalSkinType = Minecraft.getMinecraft().thePlayer.getSkinType();

    private ModernButton m_optionsButton;
    private ModernButton m_revertButton;
    private ModernButton m_applyButton;

    private SkinChangerMenu instance;

    protected float rotation = 0;

    private FakePlayerRender fakePlayer;

    // The location of the shader method.
    private MethodHandle shade;

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
        this.mod.getCosmeticFactory().getBlurShader().applyShader();
        this.fakePlayer = this.mod.getCosmeticFactory().getFakePlayerRender();

        float bottomPosBox = this.height - 20;

        bottomPosBox -= 5;

        float leftPosBox = (float) this.width / 2 + 20;
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

        ModernScroller modernScroller = new ModernScroller(this.width - 15, 5, 10, this.height - 10).disableTranslatable();

        modernScroller.insertScrollCallback((val) -> this.yTranslation = -(val * this.height / 2));

        registerElement(modernScroller);

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
    public void onGuiClose() {
        this.mc.entityRenderer.stopUseShader();
    }

    @Override
    public void postRender() {
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();

        int scale = (int) ((1.5 * this.width) / 10);

        this.fakePlayer.renderFakePlayer(((this.width / 2 + 20) + (this.width - 20)) / 2, this.height - 10 - scale, scale, this.rotation);

        GlStateManager.popMatrix();
    }

    @Override
    public final void buttonPressed(ModernButton button) {
        switch (button.getId()) {
            case 50:
                this.fakePlayer.copyFrom(this.originalSkin, this.originalCape, this.originalSkinType);

                return;
            case 51:
                this.selectionOptions.setSkin(this.mc.thePlayer, this.fakePlayer.getSkinLocation(), null);
                this.selectionOptions.setCape(this.mc.thePlayer, this.fakePlayer.getCapeLocation(), null);

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
        //this.yTranslation += 6;
    }

    @Override
    public void onScrollDown() {
        //this.yTranslation -= 6;
    }

    /**
     * Override to change the buttons which appear on the left of the screen
     */
    protected void onGuiInitExtra() {
        int buttonWidth = this.mc.fontRendererObj.getStringWidth("Load from Player") + 5;

        ModernHeader skinSettings = new ModernHeader(this, 15, 30, "Skin Settings", 1.24F, true, Color.WHITE);

        skinSettings.setOffsetBetweenDrawables(24F);

        skinSettings.getSubDrawables().add(new ModernButton(12, 5, 20, buttonWidth, 20, "Load from Player").setAsPartOfHeader(skinSettings));
        skinSettings.getSubDrawables().add(new ModernButton(13, 5, 20, buttonWidth, 20, "Load from UUID").setAsPartOfHeader(skinSettings));
        skinSettings.getSubDrawables().add(new ModernButton(14, 5, 20, buttonWidth, 20, "Load from URL").setAsPartOfHeader(skinSettings));
        skinSettings.getSubDrawables().add(new ModernButton(15, 5, 20, buttonWidth, 20, "Load from File").setAsPartOfHeader(skinSettings));
        skinSettings.getSubDrawables().add(new ModernButton(16, 5, 20, buttonWidth, 20, "Reset Skin").setAsPartOfHeader(skinSettings));

        // ----------------------------------

        int capeSettingY = this.height / 2;

        if (skinSettings.getY() + skinSettings.getHeightOfHeader() > capeSettingY) {
            capeSettingY = skinSettings.getY() + skinSettings.getHeightOfHeader() + 24;
        }

        ModernHeader capeSettings = new ModernHeader(this, 15, capeSettingY, "Cape Settings", 1.24F, true, Color.WHITE);

        capeSettings.setOffsetBetweenDrawables(24F);

        capeSettings.getSubDrawables().add(new ModernButton(17, 5, 20, buttonWidth, 20, "Load from Player").setAsPartOfHeader(capeSettings));
        capeSettings.getSubDrawables().add(new ModernButton(18, 5, 20, buttonWidth, 20, "Load from UUID").setAsPartOfHeader(capeSettings));
        capeSettings.getSubDrawables().add(new ModernButton(19, 5, 20, buttonWidth, 20, "Load from URL").setAsPartOfHeader(capeSettings));
        capeSettings.getSubDrawables().add(new ModernButton(20, 5, 20, buttonWidth, 20, "Load from File").setAsPartOfHeader(capeSettings));
        capeSettings.getSubDrawables().add(new ModernButton(21, 5, 20, buttonWidth, 20, "Reset Cape").setAsPartOfHeader(capeSettings));

        // ----------------------------------

        ModernHeader recentSkins = new ModernHeader(this, skinSettings.getX() + skinSettings.getWidthOfHeader() + 20, 30, "Recent Skins", 1.24F, true, Color.WHITE);

        // ----------------------------------

        ModernHeader recentCapes = new ModernHeader(this, capeSettings.getX() + capeSettings.getWidthOfHeader() + 20, capeSettingY, "Recent Capes", 1.24F, true, Color.WHITE);

        // ----------------------------------

        registerElement(skinSettings);
        registerElement(capeSettings);
        registerElement(recentSkins);
        registerElement(recentCapes);
    }

    /**
     * An extension of onButtonPressed.
     *
     * @param button the button which has been pressed
     */
    protected void onButtonPressedExtra(ModernButton button) {
        switch (button.getId()) {
            // Skin from a username
            case 12:
                if (this.p_playerSelectMenu == null) {
                    this.p_playerSelectMenu = new PlayerSelectMenu(this, StringSelectionType.P_USERNAME);
                }

                this.p_playerSelectMenu.display();

                break;

            // Skin from a UUID
            case 13:

                break;

            // Skin from a URL
            case 14:
                if (this.p_playerSelectMenu == null) {
                    this.p_playerSelectMenu = new PlayerSelectMenu(this, StringSelectionType.P_URL);
                }

                this.p_playerSelectMenu.display();

                break;

            // Skin from a file
            case 15:
                this.selectionOptions.loadFromFile((location) -> this.fakePlayer.setSkinLocation(location), false);

                break;

            // Reset Skin
            case 16:
                this.fakePlayer.setSkinLocation(this.originalSkin);

                break;

            // Cape from a player name
            case 17:
                if (this.c_playerSelectMenu == null) {
                    this.c_playerSelectMenu = new PlayerSelectMenu(this, StringSelectionType.C_USERNAME);
                }

                this.c_playerSelectMenu.display();

                break;

            // Cape from a UUID
            case 18:


                break;

            // Cape from a URL
            case 19:
                if (this.c_playerSelectMenu == null) {
                    this.c_playerSelectMenu = new PlayerSelectMenu(this, StringSelectionType.C_URL);
                }

                this.c_playerSelectMenu.display();

                break;

            // Cape from a file
            case 20:
                this.selectionOptions.loadFromFile((location) -> this.fakePlayer.setCapeLocation(location), true);

                break;

            // Resets the cape of the entity
            case 21:
                this.fakePlayer.setCapeLocation(this.originalCape);

                break;
        }
    }

    /**
     * Should be called by a Subclass so that buttons are configured correctly.
     * Also makes the FakePlayer maintain its rotation across menus.
     *
     * @param menu the REAL SkinChangerMenu instance (Main UI instance)
     */
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

    /**
     * Called when the player supplies an argument to the SkinChanger main command.
     *
     * @param incomingInput the first argument of the command
     * @return true if the input was valid
     */
    public boolean handleIncomingInput(String incomingInput) {
        try {
            // Try parse it as a URL
            URL url = new URL(incomingInput);

            return this.p_urlSelectMenu.handleIncomingInput(incomingInput);
        } catch (MalformedURLException ignored) { }

        // Try parse it as a player name
        if (incomingInput.length() < 2 || incomingInput.length() > 16) {
            return false;
        } else {
            return this.p_playerSelectMenu.handleIncomingInput(incomingInput);
        }
    }

    /**
     * Forces sets the rotation of the FakePlayer to this value
     *
     * @param rotation the rotation of the FakePlayer
     */
    public void setRotation(float rotation) {
        this.rotation = rotation;
    }
}
