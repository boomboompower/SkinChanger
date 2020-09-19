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

package wtf.boomy.mods.skinchanger.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import wtf.boomy.mods.skinchanger.SkinChangerMod;
import wtf.boomy.mods.skinchanger.cosmetic.impl.SkinChangerStorage;
import wtf.boomy.mods.skinchanger.cosmetic.impl.fakeplayer.FakePlayerRender;
import wtf.boomy.mods.skinchanger.gui.additional.ModOptionsMenu;
import wtf.boomy.mods.skinchanger.gui.additional.PlayerSelectMenu;
import wtf.boomy.mods.skinchanger.options.SelectionOptions;
import wtf.boomy.mods.skinchanger.utils.game.ChatColor;
import wtf.boomy.mods.skinchanger.utils.gui.ModernGui;
import wtf.boomy.mods.skinchanger.utils.gui.impl.ModernButton;
import wtf.boomy.mods.skinchanger.utils.gui.impl.ModernHeader;
import wtf.boomy.mods.skinchanger.utils.gui.impl.ModernSlider;

import java.awt.Color;
import java.lang.invoke.MethodHandle;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * The base SkinChanger menu, redesigned to be more user-friendly.
 *
 * @since 3.0.0
 */
public class SkinChangerMenu extends ModernGui {
    
    private final SelectionOptions selectionOptions = new SelectionOptions();
    
    protected static float rotation = 0;
    
    private ModOptionsMenu optionsMenu;
    
    private PlayerSelectMenu selectionMenu;
    
    // Store the basic values.
    private final ResourceLocation originalSkin = Minecraft.getMinecraft().thePlayer.getLocationSkin();
    private final ResourceLocation originalCape = Minecraft.getMinecraft().thePlayer.getLocationCape();
    private final String originalSkinType = Minecraft.getMinecraft().thePlayer.getSkinType();
    
    private ModernButton m_optionsButton;
    private ModernButton m_revertButton;
    private ModernButton m_applyBackButton;
    private ModernButton m_applyCloseButton;
    
    private SkinChangerMenu instance;
    private SkinChangerStorage storage;
    
    private FakePlayerRender fakePlayer;
    
    // The location of the shader method.
    private MethodHandle shade;
    
    public SkinChangerMenu() {
        this.instance = this;
        this.storage = SkinChangerMod.getInstance().getStorage();
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
        this.fakePlayer = this.mod.getCosmeticFactory().getFakePlayerRender();
        
        float bottomPosBox = this.height - 20;
        
        bottomPosBox -= 5;
        
        float leftPosBox = (float) this.width / 2 + 20;
        float rightPosBox = this.width - 20;
        
        // 20 pixel margins.
        leftPosBox += 20;
        rightPosBox -= 20;
        
        float baseButtonWidth = ((rightPosBox - leftPosBox) / 3) - 1;
        float boxMiddlePoint = ((leftPosBox + rightPosBox) / 2);
        
        float buttonLeftXPos = boxMiddlePoint - (baseButtonWidth * 1.5F);
        float middleButtonXPos = boxMiddlePoint - (baseButtonWidth / 2);
        float buttonRightXPos = boxMiddlePoint + (baseButtonWidth / 2);
        
        ModernButton revertButton = new ModernButton(50, (int) buttonLeftXPos - 2, (int) bottomPosBox - 20, (int) baseButtonWidth, 20, "Revert", mouseButton -> {
            this.fakePlayer.copyFrom(this.originalSkin, this.originalCape, this.originalSkinType);
        });
        
        ModernButton confirmBackButton = new ModernButton(51, (int) middleButtonXPos, (int) bottomPosBox - 20, (int) baseButtonWidth, 20, "Preview", mouseButton -> {
            this.instance.display();
        });
        ModernButton applyButton = new ModernButton(52, (int) buttonRightXPos + 2, (int) bottomPosBox - 20, (int) baseButtonWidth, 20, "Apply", mouseButton -> {
            if (this.storage.isSkinPatchApplied()) {
                this.storage.setPlayerSkin(this.fakePlayer.getSkinLocation());
            } else {
                this.selectionOptions.setSkin(this.mc.thePlayer, this.fakePlayer.getSkinLocation(), null);
            }
    
            if (this.storage.isCapePatchApplied()) {
                this.storage.setPlayerCape(this.fakePlayer.getCapeLocation());
            } else {
                this.selectionOptions.setCape(this.mc.thePlayer, this.fakePlayer.getCapeLocation(), null);
            }
    
            if (this.storage.isSkinTypePatchApplied()) {
                this.storage.setSkinType(this.fakePlayer.getSkinType().getSecretName());
            } else {
                this.selectionOptions.setSkinType(this.mc.thePlayer, this.fakePlayer.getSkinType(), null);
            }
    
            sendChatMessage(ChatColor.GREEN + "Your skin & cape have been applied!");
    
            close();
        });
    
        confirmBackButton.setEnabled(false);
        
        registerElement(revertButton);
        registerElement(confirmBackButton);
        registerElement(applyButton);
        
        this.m_revertButton = revertButton;
        this.m_applyBackButton = confirmBackButton;
        this.m_applyCloseButton = applyButton;
        
        bottomPosBox -= 25;
        
        float sliderHeight = 20;
        float sliderWidth = rightPosBox - leftPosBox;
        
        float sliderXPos = ((leftPosBox + rightPosBox) / 2) - sliderWidth / 2;
        float sliderYPos = bottomPosBox - sliderHeight;
        
        ModernSlider slider = new ModernSlider(6, (int) sliderXPos, (int) sliderYPos, (int) sliderWidth, (int) sliderHeight, "Rotation: ", "\u00B0", 0.0F, 360.0F, rotation) {
            @Override
            public void onSliderUpdate() {
                rotation = (float) getValue();
            }
        };
        
        registerElement(slider.disableTranslatable());
    
        ModernButton modPauseButton = new ModernButton(101, (int) leftPosBox - 20, 20, 25, 25, this.mod.getConfigurationHandler().isUsingAnimatedPlayer() ? "\u2713" : "\u2717", buttonPressed -> {
            this.mod.getConfigurationHandler().setUsingAnimatedPlayer(!this.mod.getConfigurationHandler().isUsingAnimatedPlayer());
    
            buttonPressed.setText(this.mod.getConfigurationHandler().isUsingAnimatedPlayer() ? "\u2713" : "\u2717");
        });
        
        // You cheeky son of a gun
        if (this instanceof ModOptionsMenu) {
            modPauseButton.setEnabled(false);
        }
        
        ModernButton modSettingsButton = new ModernButton(102, this.width - 20 - 25, 20, 25, 25, "\u2699", mouseButton -> {
            if (this.m_optionsButton != null && this.m_optionsButton.getText().equalsIgnoreCase("\u2190")) {
                this.instance.display();
        
                return;
            }
    
            if (this.optionsMenu == null) {
                this.optionsMenu = new ModOptionsMenu(this);
            }
    
            this.optionsMenu.display();
        });
        
        registerElement(modPauseButton);
        registerElement(modSettingsButton);
        
        this.m_optionsButton = modSettingsButton;
        
        onGuiInitExtra();
    }
    
    /**
     * Override to change the buttons which appear on the left of the screen
     *
     * Honestly I hate this code and want to refactor it when I have more time.
     */
    protected void onGuiInitExtra() {
        int yVal = 60;
        
        int middle = ((this.width / 2 + 15) / 2);
        
        int minXLeft = Math.max(5, middle - 155);
        int widthOfButtons = (middle - minXLeft) - 15;
        
        ModernHeader skinSettings = new ModernHeader(this, minXLeft, yVal, "Skin Settings", 1.5F, true, Color.WHITE);
        
        skinSettings.setOffsetBetweenChildren(24F);
        
        skinSettings.addChild(new ModernButton(12, 5, 20, widthOfButtons, 20, "Load from Player"));
        skinSettings.addChild(new ModernButton(13, 5, 20, widthOfButtons, 20, "Load from UUID"));
        skinSettings.addChild(new ModernButton(14, 5, 20, widthOfButtons, 20, "Load from URL"));
        
        // On left click we'll open a file screen to set the skin
        skinSettings.addChild(new ModernButton(15, 5, 20, widthOfButtons, 20, "Load from File", mouseButton -> {
            this.selectionOptions.loadFromFile((location) -> this.fakePlayer.setSkinLocation(location), false);
        }));
        
        // On left click we'll try reset the render to the original skin.
        skinSettings.addChild(new ModernButton(16, 5, 20, widthOfButtons, 20, "Reset Skin", mouseButton -> {
            this.fakePlayer.setSkinLocation(this.originalSkin);
        }));
        
        // ----------------------------------
        
        int capeSettingY = this.height / 2;
        
        if (skinSettings.getY() + skinSettings.getHeightOfHeader() > capeSettingY) {
            capeSettingY = skinSettings.getY() + skinSettings.getHeightOfHeader() + 44;
        }
        
        ModernHeader capeSettings = new ModernHeader(this, minXLeft, capeSettingY, "Cape Settings", 1.5F, true, Color.WHITE);
        
        // Makes the space between each element 24 pixels.
        capeSettings.setOffsetBetweenChildren(24F);
        
        // See onButtonPressedExtra for the callback for these methods.
        capeSettings.addChild(new ModernButton(17, 5, 20, widthOfButtons, 20, "Load from Player"));
        capeSettings.addChild(new ModernButton(18, 5, 20, widthOfButtons, 20, "Load from UUID"));
        capeSettings.addChild(new ModernButton(19, 5, 20, widthOfButtons, 20, "Load from URL"));
        
        // Open a file selector for this option
        capeSettings.addChild(new ModernButton(20, 5, 20, widthOfButtons, 20, "Load from File", mouseButton -> {
            this.selectionOptions.loadFromFile(location -> this.fakePlayer.setCapeLocation(location), true);
        }));
        
        // Set the cape to the original cape for the player.
        capeSettings.addChild(new ModernButton(21, 5, 20, widthOfButtons, 20, "Reset Cape", mouseButton -> {
            this.fakePlayer.setCapeLocation(this.originalCape);
        }));
    
        if (capeSettings.getY() + capeSettings.getHeightOfHeader() > this.height) {
            capeSettings.setX(skinSettings.getX() + skinSettings.getWidthOfHeader() + 10);
            capeSettings.setY(40);
            skinSettings.setY(40);
        } else {
            int doubleWidth = widthOfButtons * 2;
            
            skinSettings.setButtonWidth(doubleWidth);
            capeSettings.setButtonWidth(doubleWidth);
        }
        
        registerElement(skinSettings);
        registerElement(capeSettings);
    }
    
    @Override
    public void preRender(int mouseX, int mouseY) {
        drawDefaultBackground();
        
        int halfWidth = this.width / 2 + 20;
        
        int scale = (int) ((1.5 * this.width) / 10);
    
        GlStateManager.pushMatrix();
        
        this.fakePlayer.renderFakePlayer((halfWidth + (this.width - 20)) / 2, this.height - 10 - scale, scale, 0, rotation);
        
        GlStateManager.popMatrix();
        
        GlStateManager.pushMatrix();
    }
    
    @Override
    public void onRender(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.pushMatrix();
        
        GlStateManager.scale(1.5F, 1.5F, 0F);
        drawCenteredString(this.fontRendererObj, "SkinChanger", (int) (((this.width / 4) + 10) / 1.5F), 6, Color.WHITE.getRGB());
        
        GlStateManager.popMatrix();
    
        GlStateManager.pushMatrix();
    
        float smallScale = 0.65F;
    
        GlStateManager.scale(smallScale, smallScale, 0F);
        drawCenteredString(this.fontRendererObj, "by " + ChatColor.AQUA + "boomboompower" + ChatColor.RESET, (int) (((this.width / 4) + 10) / smallScale), (int) (20 / smallScale), Color.WHITE.getRGB());
    
        GlStateManager.popMatrix();
        
        GlStateManager.pushMatrix();
        
        // 20 pixel margin.
        Gui.drawRect(this.width / 2 + 20, 20, this.width - 20, this.height - 20, new Color(0.7F, 0.7F, 0.7F, 0.2F).getRGB());
        
        drawCenteredString(this.fontRendererObj, "Player Model", ((this.width / 2 + 20) + (this.width - 20)) / 2, 30, Color.WHITE.getRGB());
        
        GlStateManager.popMatrix();
        
        this.topYSnip = 55;
    }
    
    @Override
    public void postRender(float partialTicks) {
        GlStateManager.popMatrix();
    }
    
    @Override
    public void buttonPressed(ModernButton button) {
        int id = button.getId();
    
        for (PlayerSelectMenu.StringSelectionType selectionType : PlayerSelectMenu.StringSelectionType.values()) {
            if (selectionType.getButtonID() == id) {
                if (this.selectionMenu == null) {
                    this.selectionMenu = new PlayerSelectMenu(this, selectionType);
                }
            
                this.selectionMenu.displayExtra(this, selectionType);
            
                return;
            }
        }
    }
    
    /**
     * Should be called by a Subclass so that buttons are configured correctly.
     * Also makes the FakePlayer maintain its rotation across menus.
     * <p>
     * This should always be called first if it's going to be used.
     *
     * @param menu the REAL SkinChangerMenu instance (Main UI instance)
     */
    protected final void setAsSubMenu(SkinChangerMenu menu) {
        if (this.m_applyBackButton != null) {
            this.m_applyBackButton.setEnabled(true);
        }
        
        if (this.m_revertButton != null) {
            this.m_revertButton.setEnabled(false);
        }
        
        if (this.m_optionsButton != null) {
            this.m_optionsButton.setText("\u2190");
        }
        
        this.instance = menu;
    }
    
    /**
     * Called when the player supplies an argument to the SkinChanger main command.
     *
     * @param incomingInput the first argument of the command
     *
     * @return true if the input was valid
     */
    public boolean handleIncomingInput(String incomingInput) {
        try {
            // Try parse it as a URL
            URL url = new URL(incomingInput);
            
            return this.selectionMenu.handleIncomingInput(incomingInput, PlayerSelectMenu.StringSelectionType.P_URL);
        } catch (MalformedURLException ignored) {
        }
        
        // Try parse it as a player name
        if (incomingInput.length() < 2 || incomingInput.length() > 16) {
            return false;
        } else {
            return this.selectionMenu.handleIncomingInput(incomingInput, PlayerSelectMenu.StringSelectionType.P_USERNAME);
        }
    }
}
