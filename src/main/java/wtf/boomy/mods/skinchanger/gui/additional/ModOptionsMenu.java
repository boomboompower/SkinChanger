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

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

import wtf.boomy.mods.skinchanger.SkinChangerMod;
import wtf.boomy.mods.skinchanger.configuration.ConfigurationHandler;
import wtf.boomy.mods.skinchanger.core.ClassTransformer;
import wtf.boomy.mods.skinchanger.gui.SkinChangerMenu;
import wtf.boomy.mods.skinchanger.utils.game.ChatColor;
import wtf.boomy.mods.skinchanger.utils.general.PlayerSkinType;
import wtf.boomy.mods.skinchanger.utils.gui.impl.ModernButton;

import java.awt.Color;

public class ModOptionsMenu extends SkinChangerMenu {
    
    private final SkinChangerMenu skinChangerMenu;
    private final ConfigurationHandler skinChangerSettings;
    
    private boolean saveMixinConfig = false;
    
    public ModOptionsMenu(SkinChangerMenu skinChangerMenu) {
        this.skinChangerMenu = skinChangerMenu;
        
        this.skinChangerSettings = SkinChangerMod.getInstance().getConfigurationHandler();
    }
    
    @Override
    protected void onGuiInitExtra() {
        // Call first
        setAsSubMenu(this.skinChangerMenu);
        
        int yVal = 10;
        
        int middle = ((this.width / 2 + 15) / 2);
        
        int minXLeft = Math.max(5, middle - 205);
        int widthOfButtons = middle - minXLeft;
        
        ModernButton mixinSkin = new ModernButton(0x69, minXLeft, yVal, widthOfButtons, 20, "Patch Skins: " + (ClassTransformer.patchSkinMethod ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No"));
        ModernButton skinType  = new ModernButton(0x691, middle + 4, yVal, widthOfButtons, 20, "Type: " + ChatColor.AQUA + PlayerSkinType.getTypeFromString(this.mod.getCosmeticFactory().getFakePlayerRender().getSkinType()).getDisplayName());
    
        yVal += mixinSkin.getHeight() + 3;
    
        ModernButton mixinCape = new ModernButton(0x70, minXLeft, yVal, widthOfButtons, 20, "Patch Capes: " + (ClassTransformer.patchCapeMethod ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No"));
        ModernButton movingPlayerModel = new ModernButton(0x701, middle + 4, yVal, widthOfButtons, 20, "Animated Model: " + (this.skinChangerSettings.isUsingAnimatedPlayer() ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No"));
    
        yVal += mixinCape.getHeight() + 3;
    
        ModernButton mixinSkinType = new ModernButton(0x71, minXLeft, yVal, widthOfButtons, 20, "Patch Slim Skins: " + (ClassTransformer.patchSkinTypeMethod ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No"));
        ModernButton movingCapeModel = new ModernButton(0x711, middle + 4, yVal, widthOfButtons, 20, "Animated Cape: " + (this.skinChangerSettings.isUsingAnimatedCape() ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No"));
        
        registerElement(mixinSkin);
        registerElement(skinType);
        registerElement(mixinCape);
        registerElement(movingPlayerModel);
        registerElement(mixinSkinType);
        registerElement(movingCapeModel);
    }
    
    @Override
    public void onRender(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.pushMatrix();
    
        // 20 pixel margin.
        Gui.drawRect(this.width / 2 + 20, 20, this.width - 20, this.height - 20, new Color(0.7F, 0.7F, 0.7F, 0.2F).getRGB());
    
        drawCenteredString(this.fontRendererObj, "Player Model", ((this.width / 2 + 20) + (this.width - 20)) / 2, 30, Color.WHITE.getRGB());
    
        GlStateManager.popMatrix();
        
        this.topYSnip = 0;
    }
    
    @Override
    protected void onButtonPressedExtra(ModernButton button) {
    
    }
}
