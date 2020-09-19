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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ModOptionsMenu extends SkinChangerMenu {
    
    private final SkinChangerMenu skinChangerMenu;
    private final ConfigurationHandler skinChangerSettings;
    
    private boolean saveMixinConfig = false;
    private boolean saveNormalConfig = false;
    
    private PlayerSkinType currentSkinType;
    
    private final List<ModernButton> extraButtons;
    
    public ModOptionsMenu(SkinChangerMenu skinChangerMenu) {
        this.skinChangerMenu = skinChangerMenu;
        
        this.skinChangerSettings = SkinChangerMod.getInstance().getConfigurationHandler();
        
        this.extraButtons = Arrays.asList(
                
                new ModernButton(0x69, 0, 0, 0, 20, "Mod Enabled: " + getYesOrNo(this.skinChangerSettings.isModEnabled()), button -> {
                    this.skinChangerSettings.setModEnabled(!this.skinChangerSettings.isModEnabled());;
            
                    button.setText("Mod Enabled: " + getYesOrNo(this.skinChangerSettings.isModEnabled()));
                }).setMessageLines(Arrays.asList("Toggles the mod globally, ", "disabling all skin & cape overrides.", "", "Default: " + getYesOrNo(true))),
                
                new ModernButton(0x70, 0, 0, 0, 20, "Patch Skins: " + getYesOrNo(ClassTransformer.shouldPatchSkinGetter), button -> {
                    ClassTransformer.shouldPatchSkinGetter = !ClassTransformer.shouldPatchSkinGetter;
            
                    button.setText("Patch Skins: " + getYesOrNo(ClassTransformer.shouldPatchSkinGetter));
                }).setMessageLines(Arrays.asList("Overrides the internal skin service, ", "improving skin change performance. ", "", "You must restart your game for", "this change to take effect.", "", "Default: " + getYesOrNo(true))),
                
                new ModernButton(0x71, 0, 0, 0, 20, "Type: " + ChatColor.AQUA + this.mod.getCosmeticFactory().getFakePlayerRender().getSkinType().getDisplayName(), button -> {
                    this.currentSkinType = this.currentSkinType.getNextSkin();
            
                    button.setText("Type: " + ChatColor.AQUA + this.currentSkinType.getDisplayName());
            
                    this.mod.getCosmeticFactory().getFakePlayerRender().setSkinType(this.currentSkinType.getSecretName());
                }).setMessageLines(Arrays.asList("Sets the current model skin type", "hit apply to use this.", "", "Default: " + ChatColor.AQUA + "Steve")),
                
                new ModernButton(0x72, 0, 0, 0, 20, "Patch Capes: " + getYesOrNo(ClassTransformer.shouldPatchCapeGetter), button -> {
                    ClassTransformer.shouldPatchCapeGetter = !ClassTransformer.shouldPatchCapeGetter;
            
                    button.setText("Patch Capes: " + getYesOrNo(ClassTransformer.shouldPatchCapeGetter));
                }).setMessageLines(Arrays.asList("Overrides the internal cape service, ", "improving cape change performance. ", "", "You must restart your game for", "this change to take effect.", "", "Default: " + getYesOrNo(true))),
                
                new ModernButton(0x73, 0, 0, 0, 20, "API: " + ChatColor.AQUA + this.skinChangerSettings.getSkinAPIType().getDisplayName(), button -> {
                    this.skinChangerSettings.setSkinAPIType(this.skinChangerSettings.getSkinAPIType().nextValue());
            
                    button.setText("API: " + ChatColor.AQUA + this.skinChangerSettings.getSkinAPIType().getDisplayName());
                }).setMessageLines(Arrays.asList("Choose where skins are retrieved from.", "", "Only change this if you're having", "issues with skins loading.", "", "Ashcon - Faster (recommended)", "Mojang - Slower, multiple requests.", "", "Default: " + ChatColor.AQUA + "Ashcon")),
                
                new ModernButton(0x74, 0, 0, 0, 20, "Patch Skin Type: " + getYesOrNo(ClassTransformer.shouldPatchSkinType), button -> {
                    ClassTransformer.shouldPatchSkinType = !ClassTransformer.shouldPatchSkinType;
            
                    button.setText("Patch Skin Type: " + getYesOrNo(ClassTransformer.shouldPatchSkinType));
                }).setMessageLines(Arrays.asList("Overrides the skin type, ", "letting you use slim skins.", "", "You must restart your game for", "this change to take effect.", "", "Default: " + getYesOrNo(true))),
                
                new ModernButton(0x75, 0, 0, 0, 20, "Animated Model: " + getYesOrNo(this.skinChangerSettings.isUsingAnimatedPlayer()), button -> {
                    this.skinChangerSettings.setUsingAnimatedPlayer(!this.skinChangerSettings.isUsingAnimatedPlayer());
            
                    button.setText("Animated Model: " + getYesOrNo(this.skinChangerSettings.isUsingAnimatedPlayer()));
                }).setMessageLines(Arrays.asList("Toggles the moving player model", "This includes the cape animation.", "", "Default: " + getYesOrNo(true))),
                
                new ModernButton(0x77, 0, 0, 0, 20, "Animated Cape: " + getYesOrNo(this.skinChangerSettings.isUsingAnimatedCape()), button -> {
                    this.skinChangerSettings.setUsingAnimatedCape(!this.skinChangerSettings.isUsingAnimatedCape());
            
                    button.setText("Animated Cape: " + getYesOrNo(this.skinChangerSettings.isUsingAnimatedCape()));
                }).setMessageLines(Arrays.asList("Toggles the moving cape animation", "", "Default: " + getYesOrNo(true))),
                
                new ModernButton(0x78, 0, 0, 0, 20, "All me: " + getYesOrNo(this.skinChangerSettings.isEveryoneMe()), button -> {
                    this.skinChangerSettings.setEveryoneMe(!this.skinChangerSettings.isEveryoneMe());
            
                    button.setText("All me: " + getYesOrNo(this.skinChangerSettings.isEveryoneMe()));
                }).setMessageLines(Arrays.asList("Gives every player the same skin as you", "", "Default: " + getYesOrNo(false))),
                
                new ModernButton(0x79, 0, 0, 0, 20, "Useless: " + getYesOrNo(true), button -> {
                    System.err.println("Unimplemented o.o");
                }).setMessageLines(Collections.singletonList("Does nothing"))
        );
    }
    
    /**
     * I really, really hate this function, it's way too long and has way too many callbacks.
     */
    @Override
    protected void onGuiInitExtra() {
        // Call first
        setAsSubMenu(this.skinChangerMenu);
        
        this.currentSkinType = this.mod.getCosmeticFactory().getFakePlayerRender().getSkinType();
        
        int yVal = 10;
        
        int middle = ((this.width / 2 + 15) / 2);
        
        int minXLeft = Math.max(5, middle - 205);
        int widthOfButtons = middle - minXLeft;
        
        for (int i = 0; i < this.extraButtons.size(); i++) {
            int mod = i % 2;
            
            ModernButton button = this.extraButtons.get(i);
            
            // Left button
            if (mod == 0) {
                button.setX(minXLeft);
            } else {
                button.setX(middle + 4);
            }
            
            button.setY(yVal);
            button.setWidth(widthOfButtons);
            button.setHeight(20);
            
            // Shift down
            if (i != 0 && mod == 1) {
                yVal += button.getHeight() + 3;
            }
        }
        
        registerElements(this.extraButtons);
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
    public void buttonPressed(ModernButton button) {
        // bitwise AND operator. If both bits are 1 it will return 1.
        int isFirstRow = button.getId() & 1;
        
        if (isFirstRow == 0) {
            this.saveMixinConfig = true;
        } else {
            this.saveNormalConfig = true;
        }
    }
    
    @Override
    public void onGuiClose() {
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
