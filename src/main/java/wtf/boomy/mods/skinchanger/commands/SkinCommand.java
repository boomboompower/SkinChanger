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

package wtf.boomy.mods.skinchanger.commands;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.command.ICommandSender;

import net.minecraft.entity.player.EntityPlayer;
import wtf.boomy.mods.skinchanger.SkinChangerMod;
import wtf.boomy.mods.skinchanger.cosmetic.impl.SkinChangerStorage;
import wtf.boomy.mods.skinchanger.gui.SkinChangerMenu;
import wtf.boomy.mods.skinchanger.utils.command.ModCommand;
import wtf.boomy.mods.skinchanger.utils.game.ChatColor;

import java.io.File;
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
//        if (!this.storage.isSkinPatchApplied() && !this.storage.isCapePatchApplied() && !this.storage.isSkinTypePatchApplied()) {
//            if (args.length > 0 && args[0].equalsIgnoreCase("noconfig")) {
//                File file = new File(this.mod.getModConfigDirectory(), "asm.txt");
//
//                if (file.exists() && !file.delete()) {
//                    sendMessage(ChatColor.RED + "Unable " + ChatColor.GRAY + " to reset your SkinChanger ASM config. Please visit " + ChatColor.AQUA + "https://bot.boomy.wtf/support");
//                } else {
//                    sendMessage(ChatColor.GRAY + "ASM Config has been deleted. Please restart your game for the changes to go into effect.");
//                }
//
//                return;
//            }
//
//            sendMessage(ChatColor.RED + "No patches have been applied so the mod is disabled. ");
//            sendMessage("");
//            sendMessage(ChatColor.GRAY + "Try: ");
//            sendMessage(ChatColor.GRAY + "  1. Running " + ChatColor.AQUA + "/" + getCommandName() + " noconfig");
//            sendMessage(ChatColor.GRAY + "  2. Restarting your game.");
//            sendMessage("");
//            sendMessage(ChatColor.GRAY + "Please visit " + ChatColor.AQUA + "https://bot.boomy.wtf/support" + ChatColor.GRAY + " for more assistance.");
//
//            return;
//        }
        
        // TODO remove this during production.
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("fix")) {
                org.lwjgl.opengl.Display.setResizable(false);
                org.lwjgl.opengl.Display.setResizable(true);
                
                return;
            } else if (args[0].equalsIgnoreCase("reload")) {
                this.mainMenu = new SkinChangerMenu();
                
                args = new String[0];
            } else if (args[0].equalsIgnoreCase("debug")) {
                sendMessage("Skin is: " + ((AbstractClientPlayer)sender).getLocationSkin().toString());
                sendMessage("Cape is: " + ((AbstractClientPlayer)sender).getLocationCape().toString());
                sendMessage("Type is: " + ((AbstractClientPlayer)sender).getSkinType());
                
                return;
            }
        }
        
        SkinChangerMenu menu = getMenu(args.length > 0 ? args[0] : null);
        
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
     * @param incomingInput the name of the player or a URL
     *
     * @return the cached SkinChanger menu if one exists, or a new one.
     */
    private SkinChangerMenu getMenu(String incomingInput) {
        // Check if a cached menu exists.
        if (this.mainMenu == null) {
            this.mainMenu = new SkinChangerMenu();
        }
        
        // If the player has specified an input it should be handled
        if (incomingInput != null && !incomingInput.isEmpty()) {
            if (!this.mainMenu.handleIncomingInput(incomingInput)) {
                return null;
            }
        }
        
        return this.mainMenu;
    }
}
