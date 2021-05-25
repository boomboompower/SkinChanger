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

package wtf.boomy.mods.skinchanger.gui.additional;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;

import wtf.boomy.mods.modernui.uis.ModernGui;
import wtf.boomy.mods.modernui.uis.components.ButtonComponent;
import wtf.boomy.mods.modernui.uis.components.HeadButtonComponent;
import wtf.boomy.mods.modernui.uis.components.SliderComponent;
import wtf.boomy.mods.skinchanger.SkinChangerMod;
import wtf.boomy.mods.skinchanger.utils.cosmetic.impl.fakeplayer.FakePlayerRender;
import wtf.boomy.mods.skinchanger.locale.Language;
import wtf.boomy.mods.skinchanger.utils.uis.PlayerModelUI;

import java.util.List;

/**
 * This is a menu designed to clone a players skin from inside the game directly to the
 * client. It displays the players in the current world and allows the user to select the
 * skin they desire (it will show up on the right).
 *
 * Currently, any skin clicked is immediately applied to the player and the player renderer. This may change
 * Also, the client also is part of the list - this can be revisited at a later date.
 *
 * @author boomboompower
 */
public class SkinCopyMenu extends ModernGui implements PlayerModelUI {
    
    // Stores the previous UI which was opened.
    private final ModernGui previousUI;
    
    // Stored skinchanger instance
    protected SkinChangerMod mod;
    
    // Stores the translation above the player model
    protected String playerModelTranslation;
    private FakePlayerRender fakePlayer;
    private String lastPlayerName;
    
    public SkinCopyMenu(ModernGui menu) {
        this.mod = SkinChangerMod.getInstance();
        this.previousUI = menu;
    }
    
    @Override
    public void onGuiOpen() {
        boolean buttonModern = !SkinChangerMod.getInstance().getConfig().isOldButtons();
        
        // Cache the translation
        this.playerModelTranslation = Language.format("skinchanger.options.title");
        this.fakePlayer = this.mod.getCosmeticFactory().getFakePlayerRender();
        this.fakePlayer.setShouldCompute(true);
    
        float bottomPosBox = this.height - 25;
        float sliderMargin = 20;
        
        float leftPosBox = (float) this.width / 2 + 20 + sliderMargin;
        float rightPosBox = this.width - 20 - sliderMargin;
        
        // The vertical height of the slider
        float sliderHeight = 20;
        // The width of the slider is the difference between the starting x and ending x positions.
        float sliderWidth = rightPosBox - leftPosBox;
    
        float sliderXPos = ((leftPosBox + rightPosBox) / 2) - sliderWidth / 2;
        float sliderYPos = bottomPosBox - sliderHeight;
        
        SliderComponent slider = new SliderComponent(5, (int) sliderXPos, (int) sliderYPos, (int) sliderWidth, (int) sliderHeight, Language.format("skinchanger.model.rotation") + " ", "\u00B0", 0.0F, 360.0F, FakePlayerRender.getRotation()) {
            @Override
            public void onSliderUpdate() {
                FakePlayerRender.setRotation((float) getValue());
            }
        };
        
        // Prevents the button from moving when the UI is translated.
        slider.disableTranslatable();
        
        sliderYPos -= sliderHeight;
        sliderYPos -= 5;
        
        // Make the back button above the slider. Make it the same width and height as well.
        ButtonComponent backButton = new ButtonComponent(102, (int) sliderXPos, (int) sliderYPos, (int) sliderWidth, (int) sliderHeight, "\u2190", mouseButton -> this.previousUI.display());
    
        // Important. Prevents the button moving when the UI is translated.
        backButton.disableTranslatable();
        
        // Register both the back button and the slider
        // so they appear on the screen during draw calls.
        registerElement(backButton);
        registerElement(slider);
        
        List<EntityPlayer> players = this.mc.theWorld.playerEntities;
        
        // These should be the same value, currently the skin
        // will be skewed if they are different as it will stretch
        // to fit the available space. It's worth noting that the skin
        // will not take up this space exactly, check the logic in the
        // render method in the ButtonComponentHead class for more info.
        int btnWidth = 50;
        int btnHeight = 50;
        
        // The empty space between each button in both the
        // x and y direction.
        int margin = 3;
        
        int quantityPerRow = 8;
        
        // startingX is the initial x offset of the grid
        // xPos is the current x offset of the grid
        // yPos is the current y offset of the grid
        int startingX = 25;
        int xPos = startingX;
        int yPos = 20;
        
        // Iterate through every player and create a button for them
        // this can be performance heavy for lobbies with a large amount of players
        // maybe some kind of caching will be used here in the future (only refreshes every few init calls)
        //
        // Not using division and mods so the rows/columns appear dynamic and are not frame bound.
        for (EntityPlayer entityPlayer : players) {
            AbstractClientPlayer player = (AbstractClientPlayer) entityPlayer;
            
            // Invisible players shouldn't show up here for obvious reasons.
            if (player.isInvisible() || player.isPotionActive(Potion.invisibility)) {
                continue;
            }
            
            // The ending x position of the button.
            // used to determine if a wrap should occur.
            int endingX = xPos + btnWidth;
            
            // If button sizes are changed then
            if (endingX >= this.width / 2 + 15) {
                xPos = startingX;
            
                yPos += btnHeight;
            }
            
            HeadButtonComponent headButtonComponent = new HeadButtonComponent(420, xPos + margin, yPos + margin, btnWidth - margin, btnHeight - margin, player, mouseButton -> {
                SkinCopyMenu.this.lastPlayerName = player.getName();
    
                SkinChangerMod.getInstance().getStorage().setPlayerSkin(player.getLocationSkin());
                SkinChangerMod.getInstance().getStorage().setSkinType(player.getSkinType());
    
                this.fakePlayer.copyFrom(player);
            });
            
            headButtonComponent.enableNameOnHover().enableTranslatable();
        
            registerElement(headButtonComponent);
            
            // Increment the x offset by the width of this button.
            xPos += btnWidth;
        }
    }
    
    @Override
    public void preRender(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
    
        drawRenderBox(this.fontRendererObj, this.playerModelTranslation, this.width, this.height, this.lastPlayerName);
    }
    
    @Override
    public void onRender(int mouseX, int mouseY, float partialTicks) {
        renderFakePlayer(this.width, this.height, partialTicks, this.fakePlayer);
    }
    
    @Override
    public void onScrollUp() {
        this.yTranslation += 2.5f;
    }
    
    @Override
    public void onScrollDown() {
        this.yTranslation -= 2.5f;
    }
    
    @Override
    public void onGuiClose() {
        // Reset for compatibility
        this.yTranslation = 0.0f;
        this.fakePlayer.setShouldCompute(false);
    }
}
