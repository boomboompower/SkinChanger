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
import wtf.boomy.mods.skinchanger.utils.cosmetic.PlayerSkinType;
import wtf.boomy.mods.skinchanger.gui.SkinChangerMenu;
import wtf.boomy.mods.skinchanger.utils.ChatColor;
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
                new ModernLocaleButton(0x69, 0, 0, 0, 20, "Mod Enabled", "skinchanger.options.mod-enabled.format", getYesOrNo(this.config.isModEnabled()), button -> {
                    this.config.setModEnabled(!this.config.isModEnabled());;
            
                    button.updateTitleWithValue(getYesOrNo(this.config.isModEnabled()));
                }).setDefaultValue(getYesOrNo(true)),
                
                new ModernLocaleButton(0x70, 0, 0, 0, 20, "Patch Skins", "skinchanger.options.patch-skins.format", getYesOrNo(ClassTransformer.shouldPatchSkinGetter), button -> {
                    ClassTransformer.shouldPatchSkinGetter = !ClassTransformer.shouldPatchSkinGetter;
            
                    button.updateTitleWithValue(getYesOrNo(ClassTransformer.shouldPatchSkinGetter));
                }).setDefaultValue(getYesOrNo(true)),
                
                new ModernLocaleButton(0x71, 0, 0, 0, 20, "Type", "skinchanger.options.type.format", ChatColor.AQUA + this.mod.getCosmeticFactory().getFakePlayerRender().getSkinType().getDisplayName(), button -> {
                    this.currentSkinType = this.currentSkinType.getNextSkin();
            
                    button.updateTitleWithValue(ChatColor.AQUA + this.currentSkinType.getDisplayName());
            
                    this.mod.getCosmeticFactory().getFakePlayerRender().setSkinType(this.currentSkinType.getSecretName());
                }).setDefaultValue(ChatColor.AQUA + "Steve"),
                
                new ModernLocaleButton(0x72, 0, 0, 0, 20, "Patch Capes", "skinchanger.options.patch-capes.format", getYesOrNo(ClassTransformer.shouldPatchCapeGetter), button -> {
                    ClassTransformer.shouldPatchCapeGetter = !ClassTransformer.shouldPatchCapeGetter;
            
                    button.updateTitleWithValue(getYesOrNo(ClassTransformer.shouldPatchCapeGetter));
                }).setDefaultValue(getYesOrNo(true)),
                
                new ModernLocaleButton(0x73, 0, 0, 0, 20, "API", "skinchanger.options.api.format", ChatColor.AQUA + this.config.getSkinAPIType().getDisplayName(), button -> {
                    this.config.setSkinAPIType(this.config.getSkinAPIType().nextValue());
            
                    button.updateTitleWithValue(ChatColor.AQUA + this.config.getSkinAPIType().getDisplayName());
                }).setDefaultValue(ChatColor.AQUA + "Ashcon"),
                
                new ModernLocaleButton(0x74, 0, 0, 0, 20, "Patch Skin Type", "skinchanger.options.patch-skin-type.format", getYesOrNo(ClassTransformer.shouldPatchSkinType), button -> {
                    ClassTransformer.shouldPatchSkinType = !ClassTransformer.shouldPatchSkinType;
            
                    button.updateTitleWithValue(getYesOrNo(ClassTransformer.shouldPatchSkinType));
                }).setDefaultValue(getYesOrNo(true)),
        
                new ModernLocaleButton(0x75, 0, 0, 0, 20, "Old Buttons", "skinchanger.options.old-buttons.format", getYesOrNo(this.config.isOldButtons()), button -> {
                    this.config.setOldButtons(!this.config.isOldButtons());
            
                    button.updateTitleWithValue(getYesOrNo(this.config.isOldButtons()));
                }).setDefaultValue(getYesOrNo(false)),
        
                new ModernLocaleButton(0x76, 0, 0, 0, 20, "Patch Optifine", "skinchanger.options.patch-optifine.format", getYesOrNo(ClassTransformer.shouldPatchOptifine), button -> {
                    ClassTransformer.shouldPatchOptifine = !ClassTransformer.shouldPatchOptifine;
            
                    button.updateTitleWithValue(getYesOrNo(ClassTransformer.shouldPatchOptifine));
                }).setDefaultValue(getYesOrNo(true)),
        
                new ModernLocaleButton(0x77, 0, 0, 0, 20, "Animated Player", "skinchanger.options.animated-player.format", getYesOrNo(this.config.isUsingAnimatedPlayer()), button -> {
                    this.config.setUsingAnimatedPlayer(!this.config.isUsingAnimatedPlayer());
            
                    button.updateTitleWithValue(getYesOrNo(this.config.isUsingAnimatedPlayer()));
                }).setDefaultValue(getYesOrNo(true)),
                
                new ModernLocaleButton(0x78, 0, 0, 0, 20, "Animated Cape", "skinchanger.options.animated-capes.format", getYesOrNo(this.config.isUsingAnimatedCape()), button -> {
                    this.config.setUsingAnimatedCape(!this.config.isUsingAnimatedCape());
            
                    button.updateTitleWithValue(getYesOrNo(this.config.isUsingAnimatedCape()));
                }).setDefaultValue(getYesOrNo(true)),
        
                new ModernLocaleButton(0x79, 0, 0, 0, 20, "Animation Speed", "skinchanger.options.animation-speed.format", ChatColor.AQUA.toString() + Double.toString((this.config.getAnimationSpeed() * 10) / 4), button -> {
                    float animationSpeed = (this.config.getAnimationSpeed() * 10) + 1F;
                    
                    if (animationSpeed > 8) {
                        animationSpeed = 4F;
                    }
                    
                    this.config.setAnimationSpeed(animationSpeed / 10);
            
                    button.updateTitleWithValue(ChatColor.AQUA.toString() + (Double.toString(animationSpeed / 4)));
                }).setDefaultValue(ChatColor.AQUA + "1.0"),
        
                new ModernLocaleButton(0x80, 0, 0, 0, 20, "Animation Lighting", "skinchanger.options.animation-lighting.format", getYesOrNo(this.config.isUsingLighting()), button -> {
                    this.config.setUsingLighting(!this.config.isUsingLighting());
            
                    button.updateTitleWithValue(getYesOrNo(this.config.isUsingLighting()));
                }).setDefaultValue(getYesOrNo(false)),
                
                new ModernLocaleButton(0x81, 0, 0, 0, 20, "All me", "skinchanger.options.all-me.format", getYesOrNo(this.config.isEveryoneMe()), button -> {
                    this.config.setEveryoneMe(!this.config.isEveryoneMe());
            
                    button.updateTitleWithValue(getYesOrNo(this.config.isEveryoneMe()));
                }).setDefaultValue(getYesOrNo(false)),
                
                new ModernLocaleButton(0x82, 0, 0, 0, 20, "Update Checker", "skinchanger.options.updates.format", getYesOrNo(this.config.shouldRunUpdater()), button -> {
                    this.config.setRunUpdater(!this.config.shouldRunUpdater());
                    
                    button.updateTitleWithValue(getYesOrNo(this.config.shouldRunUpdater()));
                    
                }).setDefaultValue(getYesOrNo(true)),
        
                new ModernLocaleButton(0x83, 0, 0, 0, 20, "Clear Cache", "skinchanger.options.clear-cache.format", getYesOrNo(this.mod.getInternalCache().isInvalidateCacheOnLoad()), button -> {
                    this.mod.getInternalCache().setInvalidateCacheOnLoad(!this.mod.getInternalCache().isInvalidateCacheOnLoad());
                    
                    button.updateTitleWithValue(getYesOrNo(this.mod.getInternalCache().isInvalidateCacheOnLoad()));
                }).setDefaultValue(getYesOrNo(true)),
                
                new ModernButton(0x84, 0, 0, 0, 20, "Useless: " + getYesOrNo(true), button -> {
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
                
                String loreKey = locale.getKey();
                
                // Special case
                if (loreKey.endsWith(".format")) {
                    loreKey = loreKey.substring(0, loreKey.length() - ".format".length());
                    loreKey = loreKey + ".description";
                }
                
                button = locale.interpretLoreKey(loreKey, defaultValue);
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
        drawRenderBox(this.fontRendererObj, this.playerModelTranslation, this.width, this.height);
    }
    
    @Override
    public void buttonPressed(ModernButton button) {
        boolean isASMRow = button.getId() >= 0x70 && button.getId() <= 0x76;
        boolean isConfigRange = button.getId() >= 0x69 && button.getId() <= 0x80;
        
        if (isASMRow) {
            this.saveMixinConfig = true;
        }
    
        this.saveNormalConfig = true;
    }
    
    @Override
    public void onGuiClose() {
        super.onGuiClose();
        
        if (this.saveMixinConfig) {
            this.logger.debug("Saving ASM config.");
            
            writeToFile(new File(this.config.getConfigFile().getParentFile(), "asm.txt"), "" +
                    "PatchSkins: " + (ClassTransformer.shouldPatchSkinGetter ? "yes" : "no") + System.lineSeparator() +
                    "PatchCapes: " + (ClassTransformer.shouldPatchCapeGetter ? "yes" : "no") + System.lineSeparator() +
                    "PatchSkinType: " + (ClassTransformer.shouldPatchSkinType ? "yes" : "no") + System.lineSeparator() +
                    "PatchOF: " + (ClassTransformer.shouldPatchOptifine ? "yes" : "no")
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
