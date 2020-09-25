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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import wtf.boomy.mods.skinchanger.SkinChangerMod;
import wtf.boomy.mods.skinchanger.configuration.ConfigurationHandler;
import wtf.boomy.mods.skinchanger.core.ClassTransformer;
import wtf.boomy.mods.skinchanger.gui.SkinChangerMenu;
import wtf.boomy.mods.skinchanger.utils.ChatColor;
import wtf.boomy.mods.skinchanger.cosmetic.PlayerSkinType;
import wtf.boomy.mods.skinchanger.utils.gui.impl.ModernButton;
import wtf.boomy.mods.skinchanger.utils.gui.impl.ModernLocaleButton;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ModOptionsMenu extends SkinChangerMenu {
    
    private final Logger logger = LogManager.getLogger("SkinChanger - Settings");
    
    private final SkinChangerMenu skinChangerMenu;
    private final ConfigurationHandler config;
    
    private boolean saveMixinConfig = false;
    private boolean saveNormalConfig = false;
    
    private PlayerSkinType currentSkinType;
    
    private final List<ModernButton> extraButtons;
    
    public ModOptionsMenu(SkinChangerMenu skinChangerMenu) {
        this.skinChangerMenu = skinChangerMenu;
        
        this.config = SkinChangerMod.getInstance().getConfig();
        
        this.extraButtons = Arrays.asList(
                
                new ModernLocaleButton(0x69, 0, 0, 0, 20, "Mod Enabled", getYesOrNo(this.config.isModEnabled()), button -> {
                    this.config.setModEnabled(!this.config.isModEnabled());;
            
                    button.updateValue(getYesOrNo(this.config.isModEnabled()));
                }).setDefaultValue(getYesOrNo(true)),
                
                new ModernLocaleButton(0x70, 0, 0, 0, 20, "Patch Skins", getYesOrNo(ClassTransformer.shouldPatchSkinGetter), button -> {
                    ClassTransformer.shouldPatchSkinGetter = !ClassTransformer.shouldPatchSkinGetter;
            
                    button.updateValue(getYesOrNo(ClassTransformer.shouldPatchSkinGetter));
                }).setDefaultValue(getYesOrNo(true)),
                
                new ModernLocaleButton(0x71, 0, 0, 0, 20, "Type", ChatColor.AQUA + this.mod.getCosmeticFactory().getFakePlayerRender().getSkinType().getDisplayName(), button -> {
                    this.currentSkinType = this.currentSkinType.getNextSkin();
            
                    button.updateValue(ChatColor.AQUA + this.currentSkinType.getDisplayName());
            
                    this.mod.getCosmeticFactory().getFakePlayerRender().setSkinType(this.currentSkinType.getSecretName());
                }).setDefaultValue(ChatColor.AQUA + "Steve"),
                
                new ModernLocaleButton(0x72, 0, 0, 0, 20, "Patch Capes", getYesOrNo(ClassTransformer.shouldPatchCapeGetter), button -> {
                    ClassTransformer.shouldPatchCapeGetter = !ClassTransformer.shouldPatchCapeGetter;
            
                    button.updateValue(getYesOrNo(ClassTransformer.shouldPatchCapeGetter));
                }).setDefaultValue(getYesOrNo(true)),
                
                new ModernLocaleButton(0x73, 0, 0, 0, 20, "API", ChatColor.AQUA + this.config.getSkinAPIType().getDisplayName(), button -> {
                    this.config.setSkinAPIType(this.config.getSkinAPIType().nextValue());
            
                    button.updateValue(ChatColor.AQUA + this.config.getSkinAPIType().getDisplayName());
                }).setDefaultValue(ChatColor.AQUA + "Ashcon"),
                
                new ModernLocaleButton(0x74, 0, 0, 0, 20, "Patch Skin Type", getYesOrNo(ClassTransformer.shouldPatchSkinType), button -> {
                    ClassTransformer.shouldPatchSkinType = !ClassTransformer.shouldPatchSkinType;
            
                    button.updateValue(getYesOrNo(ClassTransformer.shouldPatchSkinType));
                }).setDefaultValue(getYesOrNo(true)),
                
                new ModernLocaleButton(0x75, 0, 0, 0, 20, "Animated Model", getYesOrNo(this.config.isUsingAnimatedPlayer()), button -> {
                    this.config.setUsingAnimatedPlayer(!this.config.isUsingAnimatedPlayer());
            
                    button.updateValue(getYesOrNo(this.config.isUsingAnimatedPlayer()));
                }).setDefaultValue(getYesOrNo(true)),
                
                new ModernLocaleButton(0x77, 0, 0, 0, 20, "Animated Cape", getYesOrNo(this.config.isUsingAnimatedCape()), button -> {
                    this.config.setUsingAnimatedCape(!this.config.isUsingAnimatedCape());
            
                    button.updateValue(getYesOrNo(this.config.isUsingAnimatedCape()));
                }).setDefaultValue(getYesOrNo(true)),
        
                new ModernLocaleButton(0x78, 0, 0, 0, 20, "Animation Speed", ChatColor.AQUA.toString() + this.config.getAnimationSpeed(), button -> {
                    float animationSpeed = this.config.getAnimationSpeed() + 0.25F;
                    
                    if (animationSpeed > 2) {
                        animationSpeed = 0.5F;
                    }
                    
                    this.config.setAnimationSpeed(animationSpeed);
            
                    button.updateValue(ChatColor.AQUA.toString() + animationSpeed);
                }).setDefaultValue(ChatColor.AQUA + "1.0"),
        
                new ModernLocaleButton(0x81, 0, 0, 0, 20, "Animation Lighting", getYesOrNo(this.config.isUsingLighting()), button -> {
                    this.config.setUsingLighting(!this.config.isUsingLighting());
            
                    button.updateValue(getYesOrNo(this.config.isUsingLighting()));
                }).setDefaultValue(getYesOrNo(false)),
                
                new ModernLocaleButton(0x80, 0, 0, 0, 20, "All me", getYesOrNo(this.config.isEveryoneMe()), button -> {
                    this.config.setEveryoneMe(!this.config.isEveryoneMe());
            
                    button.updateValue(getYesOrNo(this.config.isEveryoneMe()));
                }).setDefaultValue(getYesOrNo(false)),
                
                new ModernButton(0x82, 0, 0, 0, 20, "Useless: " + getYesOrNo(true), button -> {
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
        this.saveMixinConfig = false;
        this.saveNormalConfig = false;
        
        int yVal = 10;
        
        int middle = ((this.width / 2 + 15) / 2);
        
        int minXLeft = Math.max(5, middle - 205);
        int widthOfButtons = middle - minXLeft;
        
        for (int i = 0; i < this.extraButtons.size(); i++) {
            int mod = i % 2;
            
            ModernButton button = this.extraButtons.get(i);
            
            if (button instanceof ModernLocaleButton) {
                ModernLocaleButton locale = (ModernLocaleButton) button;
                
                String defaultValue = locale.getDefaultValue();
                
                if (defaultValue == null) {
                    defaultValue = ChatColor.RED + "No";
                }
                
                locale.interpretLoreKey(locale.getKey(), defaultValue);
            }
            
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
        drawRenderBox();
    }
    
    @Override
    public void buttonPressed(ModernButton button) {
        boolean isASMRow = button.getId() >= 0x70 && button.getId() <= 0x74;
        boolean isConfigRange = button.getId() >= 0x69 && button.getId() <= 0x80;
        
        if (isASMRow) {
            this.saveMixinConfig = true;
        } else if (isConfigRange) {
            this.saveNormalConfig = true;
        }
    }
    
    @Override
    public void onGuiClose() {
        if (this.saveMixinConfig) {
            this.logger.debug("Saving ASM config.");
            
            writeToFile(new File(this.config.getConfigFile().getParentFile(), "asm.txt"), "" +
                    "PatchSkins: " + (ClassTransformer.shouldPatchSkinGetter ? "yes" : "no") + System.lineSeparator() +
                    "PatchCapes: " + (ClassTransformer.shouldPatchCapeGetter ? "yes" : "no") + System.lineSeparator() +
                    "PatchSkinType: " + (ClassTransformer.shouldPatchSkinType ? "yes" : "no")
            );
        }
        
        if (this.saveNormalConfig) {
            this.logger.debug("Saving Normal config.");
            
            this.config.save();
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
    
    public List<ModernButton> getExtraButtons() {
        return Collections.unmodifiableList(extraButtons);
    }
}
