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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ModOptionsMenu extends SkinChangerMenu {
    
    private final SkinChangerMenu skinChangerMenu;
    private final ConfigurationHandler skinChangerSettings;
    
    private boolean saveMixinConfig = false;
    private boolean saveNormalConfig = false;
    
    private PlayerSkinType currentSkinType;
    
    public ModOptionsMenu(SkinChangerMenu skinChangerMenu) {
        this.skinChangerMenu = skinChangerMenu;
        
        this.skinChangerSettings = SkinChangerMod.getInstance().getConfigurationHandler();
    }
    
    @Override
    protected void onGuiInitExtra() {
        // Call first
        setAsSubMenu(this.skinChangerMenu);
        
        this.currentSkinType = this.mod.getCosmeticFactory().getFakePlayerRender().getSkinType();
        
        int yVal = 10;
        
        int middle = ((this.width / 2 + 15) / 2);
        
        int minXLeft = Math.max(5, middle - 205);
        int widthOfButtons = middle - minXLeft;
        
        ModernButton mixinSkin = new ModernButton(0x70, minXLeft, yVal, widthOfButtons, 20, "Patch Skins: " + getYesOrNo(ClassTransformer.shouldPatchSkinGetter));
        ModernButton skinType  = new ModernButton(0x71, middle + 4, yVal, widthOfButtons, 20, "Type: " + ChatColor.AQUA + this.currentSkinType.getDisplayName());
    
        skinType.setEnabled(this.mod.getStorage().isSkinTypePatchApplied());
        
        yVal += mixinSkin.getHeight() + 3;
    
        ModernButton mixinCape = new ModernButton(0x72, minXLeft, yVal, widthOfButtons, 20, "Patch Capes: " + getYesOrNo(ClassTransformer.shouldPatchCapeGetter));
        ModernButton apiType = new ModernButton(0x73, middle + 4, yVal, widthOfButtons, 20, "API: " + ChatColor.AQUA + this.skinChangerSettings.getSkinAPIType().getDisplayName());
    
        yVal += mixinCape.getHeight() + 3;
    
        ModernButton mixinSkinType = new ModernButton(0x74, minXLeft, yVal, widthOfButtons, 20, "Patch Skin Type: " + getYesOrNo(ClassTransformer.shouldPatchSkinType));
        ModernButton movingPlayerModel = new ModernButton(0x75, middle + 4, yVal, widthOfButtons, 20, "Animated Model: " + getYesOrNo(this.skinChangerSettings.isUsingAnimatedPlayer()));
        
        yVal += mixinCape.getHeight() + 3;
    
        ModernButton uselessButton = new ModernButton(0x76, minXLeft, yVal, widthOfButtons, 20, "Blur UI's: " + getYesOrNo(this.skinChangerSettings.shouldBlurUI()));
        ModernButton movingCapeModel = new ModernButton(0x77, middle + 4, yVal, widthOfButtons, 20, "Animated Cape: " + getYesOrNo(this.skinChangerSettings.isUsingAnimatedCape()));
    
        yVal += mixinCape.getHeight() + 3;
    
        ModernButton uselessButton2 = new ModernButton(0x78, minXLeft, yVal, widthOfButtons, 20, "All me: " + getYesOrNo(this.skinChangerSettings.isEveryoneMe()));
        ModernButton uselessButton3 = new ModernButton(0x79, middle + 4, yVal, widthOfButtons, 20, "Useless: " + getYesOrNo(true));
    
        registerElement(mixinSkin);
        registerElement(skinType);
        registerElement(mixinCape);
        registerElement(apiType);
        registerElement(mixinSkinType);
        registerElement(movingPlayerModel);
        registerElement(uselessButton);
        registerElement(movingCapeModel);
        registerElement(uselessButton2);
        registerElement(uselessButton3);
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
        // bitwise AND operator. If both bits are 1 it will return 1.
        int isFirstRow = button.getId() & 1;
        
        if (isFirstRow == 0) {
            this.saveMixinConfig = true;
        } else {
            this.saveNormalConfig = true;
        }
        
        switch (button.getId()) {
            case 0x70:
                ClassTransformer.shouldPatchSkinGetter = !ClassTransformer.shouldPatchSkinGetter;
                
                button.setText("Patch Skins: " + getYesOrNo(ClassTransformer.shouldPatchSkinGetter));
                
                break;
            case 0x71:
                this.currentSkinType = this.currentSkinType.getNextSkin();
                
                button.setText("Type: " + ChatColor.AQUA + this.currentSkinType.getDisplayName());
                
                this.mod.getCosmeticFactory().getFakePlayerRender().setSkinType(this.currentSkinType.getSecretName());
                
                break;
            case 0x72:
                ClassTransformer.shouldPatchCapeGetter = !ClassTransformer.shouldPatchCapeGetter;
    
                button.setText("Patch Capes: " + getYesOrNo(ClassTransformer.shouldPatchCapeGetter));
                
                break;
            case 0x73:
                this.skinChangerSettings.setSkinAPIType(this.skinChangerSettings.getSkinAPIType().nextValue());
        
                button.setText("API: " + ChatColor.AQUA + this.skinChangerSettings.getSkinAPIType().getDisplayName());
        
                break;
            case 0x74:
                ClassTransformer.shouldPatchSkinType = !ClassTransformer.shouldPatchSkinType;
        
                button.setText("Patch Slim Skins: " + getYesOrNo(ClassTransformer.shouldPatchSkinType));
        
                break;
            case 0x75:
                this.skinChangerSettings.setUsingAnimatedPlayer(!this.skinChangerSettings.isUsingAnimatedPlayer());
    
                button.setText("Animated Model: " + getYesOrNo(this.skinChangerSettings.isUsingAnimatedPlayer()));
    
                break;
            case 0x76:
                this.skinChangerSettings.setShouldBlurUI(!this.skinChangerSettings.shouldBlurUI());
    
                button.setText("Blur UI's: " + getYesOrNo(this.skinChangerSettings.shouldBlurUI()));
        
                break;
            case 0x77:
                this.skinChangerSettings.setUsingAnimatedCape(!this.skinChangerSettings.isUsingAnimatedCape());
    
                button.setText("Animated Cape: " + getYesOrNo(this.skinChangerSettings.isUsingAnimatedCape()));
    
                break;
            case 0x78:
                this.skinChangerSettings.setEveryoneMe(!this.skinChangerSettings.isEveryoneMe());
                
                button.setText("All me: " + getYesOrNo(this.skinChangerSettings.isEveryoneMe()));
    
                break;
            case 0x79:
                System.err.println("Unimplemented 3 o,o");
        
                break;
            
        }
    }
    
    @Override
    public void onGuiClose() {
        super.onGuiClose();
        
        if (this.saveMixinConfig) {
            System.out.println("Saving ASM config.");
            
            writeToFile(new File(this.skinChangerSettings.getConfigFile().getParentFile(), "asm.txt"), "" +
                    "PatchSkins: " + (ClassTransformer.shouldPatchSkinGetter ? "yes" : "no") + System.lineSeparator() +
                    "PatchCapes: " + (ClassTransformer.shouldPatchCapeGetter ? "yes" : "no") + System.lineSeparator() +
                    "PatchSkinType: " + (ClassTransformer.shouldPatchSkinType ? "yes" : "no")
            );
        }
        
        if (this.saveNormalConfig) {
            this.skinChangerSettings.save();
        }
    }
    
    private String getYesOrNo(boolean in) {
        return (in ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No") + ChatColor.RESET;
    }
    
    /**
     * This must run independently, Gson is not loaded when this is called
     * and will crash the game if it's used. Therefore we need our own method.
     *
     * @param file  the file to save the value to
     * @param value the value to write to the file.
     */
    @SuppressWarnings({"ResultOfMethodCallIgnored", "DuplicatedCode"})
    private void writeToFile(File file, String value) {
        if (file == null || (file.exists() && file.isDirectory())) {
            // Do nothing if future issues may occur
            return;
        }
        
        try {
            if (!file.exists()) {
                File parent = file.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                file.createNewFile();
            }
            
            FileWriter writer = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(value);
            bufferedWriter.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
