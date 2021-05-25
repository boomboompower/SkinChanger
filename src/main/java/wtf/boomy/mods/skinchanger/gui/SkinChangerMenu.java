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

import com.google.gson.JsonObject;

import net.minecraft.client.renderer.GlStateManager;

import wtf.boomy.mods.modernui.uis.ModernGui;
import wtf.boomy.mods.modernui.uis.components.ButtonComponent;
import wtf.boomy.mods.modernui.uis.components.HeadButtonComponent;
import wtf.boomy.mods.modernui.uis.components.HeaderComponent;
import wtf.boomy.mods.modernui.uis.components.SliderComponent;

import wtf.boomy.mods.modernui.uis.faces.SkinnedUIElement;
import wtf.boomy.mods.skinchanger.SkinChangerMod;
import wtf.boomy.mods.skinchanger.gui.additional.ModOptionsMenu;
import wtf.boomy.mods.skinchanger.gui.additional.PlayerSelectMenu;
import wtf.boomy.mods.skinchanger.gui.additional.SkinCopyMenu;
import wtf.boomy.mods.skinchanger.locale.Language;
import wtf.boomy.mods.skinchanger.utils.ChatColor;
import wtf.boomy.mods.skinchanger.utils.cosmetic.impl.SkinChangerStorage;
import wtf.boomy.mods.skinchanger.utils.cosmetic.impl.fakeplayer.FakePlayerRender;
import wtf.boomy.mods.skinchanger.utils.cosmetic.options.ReflectionOptions;
import wtf.boomy.mods.skinchanger.utils.uis.PlayerModelUI;
import wtf.boomy.mods.skinchanger.utils.uis.components.LocaleButtonComponent;

import java.awt.Color;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * The base SkinChanger menu, redesigned to be more user-friendly.
 *
 * @since 3.0.0
 */
@SuppressWarnings("CodeBlock2Expr")
public class SkinChangerMenu extends ModernGui implements PlayerModelUI {
    
    private final ReflectionOptions reflectionOptions = new ReflectionOptions();
    
    private ModOptionsMenu optionsMenu;
    private SkinCopyMenu skinCopyMenu;
    
    private PlayerSelectMenu selectionMenu;
    
    private ButtonComponent m_optionsButton;
    private ButtonComponent m_revertButton;
    private ButtonComponent m_applyBackButton;
    
    private final SkinChangerStorage storage = SkinChangerMod.getInstance().getStorage();
    private SkinChangerMenu instance;
    
    protected String playerModelTranslation;
    protected SkinChangerMod mod;
    private FakePlayerRender fakePlayer;
    
    public SkinChangerMenu() {
        this.mod = SkinChangerMod.getInstance();
        this.instance = this;
        
        this.reflectionOptions.resetCachedValues();
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
        boolean buttonModern = !SkinChangerMod.getInstance().getConfig().isOldButtons();
        
        // Prevent blur
        this.mc.entityRenderer.stopUseShader();
        
        this.playerModelTranslation = Language.format("skinchanger.model.title");
        this.fakePlayer = this.mod.getCosmeticFactory().getFakePlayerRender();
        this.fakePlayer.setShouldCompute(true);
        
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
    
        ButtonComponent revertButton = new LocaleButtonComponent(50, (int) buttonLeftXPos - 2, (int) bottomPosBox - 20, (int) baseButtonWidth, 20, "Revert", "skinchanger.phrase.revert", mouseButton -> {
            this.reflectionOptions.resetPlayer(this.fakePlayer.getFakePlayer());
        });
    
        LocaleButtonComponent confirmBackButton = new LocaleButtonComponent(51, (int) middleButtonXPos, (int) bottomPosBox - 20, (int) baseButtonWidth, 20, "Preview", "skinchanger.phrase.preview", mouseButton -> {
            this.instance.display();
        });
    
        LocaleButtonComponent applyButton = new LocaleButtonComponent(52, (int) buttonRightXPos + 2, (int) bottomPosBox - 20, (int) baseButtonWidth, 20, "Apply", "skinchanger.phrase.apply", mouseButton -> {
            if (this.storage.isSkinPatchApplied()) {
                this.storage.setPlayerSkin(this.fakePlayer.getSkinLocation());
            } else {
                this.reflectionOptions.setSkin(this.player, this.fakePlayer.getSkinLocation(), null);
            }
        
            if (this.storage.isCapePatchApplied()) {
                this.storage.setPlayerCape(this.fakePlayer.getCapeLocation());
            } else {
                this.reflectionOptions.setCape(this.player, this.fakePlayer.getCapeLocation(), null);
            }
        
            if (this.storage.isSkinTypePatchApplied()) {
                this.storage.setSkinType(this.fakePlayer.getSkinType().getSecretName());
            } else {
                this.reflectionOptions.setSkinType(this.player, this.fakePlayer.getSkinType(), null);
            }
        
            sendChatMessage(ChatColor.GREEN + Language.format("skinchanger.chat.applied"));
        
            close();
        });
    
        confirmBackButton.setEnabled(false);
        
        registerElement(revertButton);
        registerElement(confirmBackButton);
        registerElement(applyButton);
        
        this.m_revertButton = revertButton;
        this.m_applyBackButton = confirmBackButton;
    
        bottomPosBox -= 25;
        
        float sliderHeight = 20;
        float sliderWidth = rightPosBox - leftPosBox;
        
        float sliderXPos = ((leftPosBox + rightPosBox) / 2) - sliderWidth / 2;
        float sliderYPos = bottomPosBox - sliderHeight;
        
        SliderComponent slider = new SliderComponent(6, (int) sliderXPos, (int) sliderYPos, (int) sliderWidth, (int) sliderHeight, Language.format("skinchanger.model.rotation"), "\u00B0", 0.0F, 360.0F, FakePlayerRender.getRotation()) {
            @Override
            public void onSliderUpdate() {
                FakePlayerRender.setRotation((float) getValue());
            }
        };
        
        registerElement(slider.disableTranslatable());
    
        ButtonComponent modPauseButton = new ButtonComponent(101, (int) leftPosBox - 20, 20, 26, 26, this.mod.getConfig().isUsingAnimatedPlayer() ? "\u2713" : "\u2717", buttonPressed -> {
            this.mod.getConfig().setUsingAnimatedPlayer(!this.mod.getConfig().isUsingAnimatedPlayer());
    
            buttonPressed.setText(this.mod.getConfig().isUsingAnimatedPlayer() ? "\u2713" : "\u2717");
        });
        
        // You cheeky son of a gun
        if (this instanceof ModOptionsMenu) {
            modPauseButton.setEnabled(false);
        }
        
        ButtonComponent modSettingsButton = new ButtonComponent(102, this.width - 46, 20, 26, 26, "\u2699", mouseButton -> {
            if (this.m_optionsButton != null && this.m_optionsButton.getText().equalsIgnoreCase("\u2190")) {
                this.instance.display();
        
                return;
            }
    
            getOptionsMenu().display();
        });
        
        registerElement(modPauseButton);
        registerElement(modSettingsButton);
        
        this.m_optionsButton = modSettingsButton;
        
        onGuiInitExtra(buttonModern);
    }
    
    /**
     * Override to change the buttons which appear on the left of the screen
     *
     * Honestly I hate this code and want to refactor it when I have more time.
     */
    protected void onGuiInitExtra(boolean buttonModern) {
        int yVal = 60;
        
        int middle = ((this.width / 2 + 15) / 2);
        
        int minXLeft = Math.max(5, middle - 155);
        int widthOfButtons = (middle - minXLeft) - 15;
        
        HeaderComponent skinSettings = new HeaderComponent(this, minXLeft, yVal, Language.format("skinchanger.buttons.skins.title"), 1.5F, true, Color.WHITE);
        
        skinSettings.setOffsetBetweenChildren(24F);
    
        skinSettings.addChild(new LocaleButtonComponent(12, 5, 20, widthOfButtons, 20, "Load from Player", "skinchanger.buttons.load-from-player").setDrawingModern(buttonModern));
        skinSettings.addChild(new LocaleButtonComponent(13, 5, 20, widthOfButtons, 20, "Load from UUID", "skinchanger.buttons.load-from-uuid").setDrawingModern(buttonModern));
        skinSettings.addChild(new LocaleButtonComponent(14, 5, 20, widthOfButtons, 20, "Load from URL", "skinchanger.buttons.load-from-url").setDrawingModern(buttonModern));
    
        // On left click we'll open a file screen to set the skin
        skinSettings.addChild(new LocaleButtonComponent(15, 5, 20, widthOfButtons, 20, "Load from File", "skinchanger.buttons.load-from-file", mouseButton -> {
            this.reflectionOptions.loadFromFile((location) -> this.fakePlayer.setSkinLocation(location), false);
        }).setDrawingModern(buttonModern));
    
        // On left click we'll try reset the render to the original skin.
        skinSettings.addChild(new LocaleButtonComponent(16, 5, 20, widthOfButtons, 20, "Reset Skin", "skinchanger.buttons.skins.reset", mouseButton -> {
            this.fakePlayer.setSkinLocation(this.reflectionOptions.getOriginalSkin());
            this.fakePlayer.setSkinType(this.reflectionOptions.getOriginalSkinType());
        }).setDrawingModern(buttonModern));
    
        // ------------------------------------------
        
        int capeSettingY = this.height / 2;
        
        if (skinSettings.getY() + skinSettings.getHeightOfHeader() > capeSettingY) {
            capeSettingY = skinSettings.getY() + skinSettings.getHeightOfHeader() + 44;
        }
    
        HeaderComponent capeSettings = new HeaderComponent(this, minXLeft, capeSettingY, Language.format("skinchanger.buttons.capes.title"), 1.5F, true, Color.WHITE);
    
        // Makes the space between each element 24 pixels.
        capeSettings.setOffsetBetweenChildren(24F);
    
        // See onButtonPressedExtra for the callback for these methods.
        capeSettings.addChild(new LocaleButtonComponent(17, 5, 20, widthOfButtons, 20, "Load from Player", "skinchanger.buttons.load-from-player").setDrawingModern(buttonModern));
        capeSettings.addChild(new LocaleButtonComponent(18, 5, 20, widthOfButtons, 20, "Load from UUID", "skinchanger.buttons.load-from-uuid").setDrawingModern(buttonModern));
        capeSettings.addChild(new LocaleButtonComponent(19, 5, 20, widthOfButtons, 20, "Load from URL", "skinchanger.buttons.load-from-url").setDrawingModern(buttonModern));
    
        // Open a file selector for this option
        capeSettings.addChild(new LocaleButtonComponent(20, 5, 20, widthOfButtons, 20, "Load from File", "skinchanger.buttons.load-from-file", mouseButton -> {
            this.reflectionOptions.loadFromFile(location -> this.fakePlayer.setCapeLocation(location), true);
        }).setDrawingModern(buttonModern));
    
        // Set the cape to the original cape for the player.
        capeSettings.addChild(new LocaleButtonComponent(21, 5, 20, widthOfButtons, 20, "Reset Cape", "skinchanger.buttons.capes.reset", mouseButton -> {
            this.fakePlayer.setCapeLocation(this.reflectionOptions.getOriginalCape());
        }).setDrawingModern(buttonModern));
    
        if (capeSettings.getY() + capeSettings.getHeightOfHeader() > this.height) {
            capeSettings.setX(skinSettings.getX() + skinSettings.getWidthOfHeader() + 10);
            capeSettings.setY(40);
            skinSettings.setY(40);
        } else {
            int doubleWidth = widthOfButtons * 2;
        
            skinSettings.setButtonWidth(doubleWidth);
            capeSettings.setButtonWidth(doubleWidth);
        }
        
        // ------------------------------------------
    
        ButtonComponent stealButton = new HeadButtonComponent(104, this.width - 46, 50, 26, 26, this.fakePlayer.getFakePlayer(), mouseButton -> {
            getSkinCopyMenu().display();
        }).setDrawingModern(buttonModern);
    
        // ------------------------------------------
        
        registerElement(skinSettings);
        registerElement(capeSettings);
        registerElement(stealButton);
        registerUpdateChecker();
    }
    
    @Override
    public void preRender(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        
        renderFakePlayer(this.width, this.height, partialTicks, this.fakePlayer);
    }
    
    @Override
    public void onRender(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.pushMatrix();
        
        GlStateManager.scale(1.5F, 1.5F, 0F);
        drawCenteredString(this.fontRendererObj, "SkinChanger", (int) (((this.width / 4) + 10) / 1.5F), 6, Color.WHITE.getRGB());
        
        GlStateManager.popMatrix();
    
        GlStateManager.pushMatrix();
    
        float smallScale = 0.66F;
    
        GlStateManager.scale(smallScale, smallScale, 0F);
        drawCenteredString(this.fontRendererObj, "by " + ChatColor.AQUA + "boomboompower" + ChatColor.RESET, (int) (((this.width / 4) + 10) / smallScale), (int) (20 / smallScale), Color.WHITE.getRGB());
    
        GlStateManager.popMatrix();
    
        drawRenderBox(this.fontRendererObj, this.playerModelTranslation, this.width, this.height);
    }
    
    @Override
    public void postRender(float partialTicks) {
    }
    
    @Override
    public void buttonPressed(ButtonComponent button) {
        int id = button.getId();
    
        for (StringSelectionType selectionType : StringSelectionType.values()) {
            if (selectionType.getButtonID() == id) {
                if (this.selectionMenu == null) {
                    this.selectionMenu = new PlayerSelectMenu(this, selectionType);
                }
            
                this.selectionMenu.displayExtra(this, selectionType);
            
                return;
            }
        }
    }
    
    @Override
    public void onGuiClose() {
        // Tell the fake player to stop working out animations in the background
        this.fakePlayer.setShouldCompute(false);
    }
    
    @Override
    public void registerElement(Object element) {
        if (element instanceof SkinnedUIElement) {
            ((SkinnedUIElement) element).setDrawingModern(!SkinChangerMod.getInstance().getConfig().isOldButtons());
        }
        
        super.registerElement(element);
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
     * Returns the cached instance of the mods option menu
     *
     * @return the cached option menu
     */
    public ModOptionsMenu getOptionsMenu() {
        if (this.optionsMenu == null) {
            this.optionsMenu = new ModOptionsMenu(this);
        }
        
        return this.optionsMenu;
    }
    
    /**
     * Returns the cached instance of the skin stealer menu
     *
     * @return the cached skin stealer menu
     */
    public SkinCopyMenu getSkinCopyMenu() {
        if (this.skinCopyMenu == null) {
            this.skinCopyMenu = new SkinCopyMenu(this);
        }
        
        return this.skinCopyMenu;
    }
    
    /**
     * Returns the cached reflection options, including the cape, skin and skin-type from launch
     *
     * @return the reflection options instance.
     */
    public ReflectionOptions getReflectionOptions() {
        return reflectionOptions;
    }
    
    /**
     * Places the update checker button on the screen if an update has been found
     */
    private void registerUpdateChecker() {
        if (this.mod.getApagogeHandler().getUpdateData() != null) {
            JsonObject updateData = this.mod.getApagogeHandler().getUpdateData();
            String newerVersion = "??";
            String versionURL = "https://mods.boomy.wtf/";
            
            if (updateData.has("latestVersion")) {
                // versions.boomy.wtf
                newerVersion = updateData.get("version").getAsString();
                versionURL = updateData.get("download").getAsString();
            } else if (updateData.has("tag_name")) {
                // github builds
                newerVersion = updateData.get("tag_name").getAsString();
                versionURL = updateData.get("html_url").getAsString();
            }
        
            List<String> lore = new ArrayList<>(Language.getMultiLine("skinchanger.buttons.updates.lines"));
        
            lore.add(" ");
            lore.add(Language.format("skinchanger.buttons.updates.version", ChatColor.GOLD + newerVersion));
            lore.add(" ");
        
            for (String line : Language.getMultiLine("skinchanger.buttons.updates.info")) {
                lore.add(ChatColor.DARK_GRAY + ChatColor.ITALIC.toString() + line);
            }
        
            final String foundVersionLink = versionURL;
        
            ButtonComponent updateButton = new LocaleButtonComponent(105, 5, 5, 50, 25, "Update", "skinchanger.buttons.updates.title", (button) -> {
                try {
                    Desktop.getDesktop().browse(new URI(foundVersionLink));
                } catch (IOException | URISyntaxException exception) {
                    exception.printStackTrace();
                }
            }).setMessageLines(lore).setOuterGlow(3, 0.15f);
    
            registerElement(updateButton);
        }
        
        
    }
}
