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

package wtf.boomy.mods.skinchanger.commands.impl;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.command.ICommandSender;

import wtf.boomy.mods.skinchanger.SkinChangerMod;
import wtf.boomy.mods.skinchanger.commands.ModCommand;
import wtf.boomy.mods.skinchanger.cosmetic.impl.SkinChangerStorage;
import wtf.boomy.mods.skinchanger.gui.SkinChangerMenu;
import wtf.boomy.mods.skinchanger.language.Language;
import wtf.boomy.mods.skinchanger.utils.ChatColor;
import wtf.boomy.mods.skinchanger.utils.gui.impl.ModernButton;

import java.util.Arrays;
import java.util.List;

/**
 * NOTE: This class is temporary and it's methods will eventually be tuned into a new gui.
 */
public class SkinCommand extends ModCommand {
    
    // Cached main menu, saves memory and options.
    private SkinChangerMenu mainMenu = null;
    private final SkinChangerStorage storage;
    
    public SkinCommand(SkinChangerMod modIn) {
        super(modIn);
        
        this.storage = modIn.getStorage();
    }
    
    @Override
    public String getCommandName() {
        return "skinchanger";
    }
    
    @Override
    public List<String> getAliases() {
        return Arrays.asList("changeskin", "changecape");
    }
    
    @Override
    public void onCommand(ICommandSender sender, String[] args) {
        // TODO remove this during production.
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                this.mainMenu = new SkinChangerMenu();
    
                args = new String[0];
            } else if (args[0].equalsIgnoreCase("resources")) {
                sendMessage("Skin is: " + ((AbstractClientPlayer)sender).getLocationSkin().toString());
                sendMessage("Cape is: " + ((AbstractClientPlayer)sender).getLocationCape().toString());
                sendMessage("Type is: " + ((AbstractClientPlayer)sender).getSkinType());
                
                return;
            }
        }
    
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reset")) {
                getMenu().getReflectionOptions().resetPlayer(this.mod.getStorage());
                
                sendBrandedMessage(ChatColor.GREEN + Language.format("skinchanger.phrase.apply"));
                
                return;
//            } else if (args[0].equalsIgnoreCase("skin") || args[0].equalsIgnoreCase("cape")) {
            } else if (args[0].equalsIgnoreCase("toggle")) {
                if (args.length == 1) {
                    this.mod.getConfig().setModEnabled(!this.mod.getConfig().isModEnabled());
                
                    sendBrandedMessage("SkinChanger is now " + Language.format("skinchanger.phrase." + (this.mod.getConfig().isModEnabled() ? "enabled" : "disabled")));
                
                    return;
                } else {
                    String identifier = args[1].toLowerCase();
                    SkinChangerMenu menu = getMenu();
                    
                    if (menu == null) {
                        return;
                    }
                    
                    ModernButton found = null;
                    
                    for (ModernButton button : menu.getOptionsMenu().getExtraButtons()) {
                        if (button.getText().toLowerCase().startsWith(identifier)) {
                            found = button;
                        }
                    }
                    
                    if (found == null) {
                        sendBrandedMessage("Unable to find a setting matching that name");
                        
                        return;
                    } else {
                        found.onLeftClick(-1, -1, 0);
                        
                        sendBrandedMessage(found.getText());
                    }
                }
            }
        }
        
        SkinChangerMenu menu = getMenu();
        
        // Something went wrong or an argument was incorrect.
        if (menu == null) {
            sendMessage(ChatColor.RED + "Invalid command arguments, try without arguments.");
            
            return;
        }
        
        menu.display();
    }
    
    @Override
    protected boolean shouldMultithreadCommand(String[] args) {
        return true;
    }
    
    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 0;
    }
    
    /**
     * Gets the cached SkinChanger menu
     *
     * @return the cached SkinChanger menu if one exists, or a new one.
     */
    private SkinChangerMenu getMenu() {
        // Check if a cached menu exists.
        if (this.mainMenu == null) {
            this.mainMenu = new SkinChangerMenu();
        }
        
        return this.mainMenu;
    }
}
