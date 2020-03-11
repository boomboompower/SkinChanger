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

package me.do_you_like.mods.skinchanger.commands;

import java.util.Arrays;
import java.util.List;

import me.do_you_like.mods.skinchanger.SkinChangerMod;
import me.do_you_like.mods.skinchanger.gui.SkinChangerMenu;
import me.do_you_like.mods.skinchanger.utils.installing.InternetConnection;
import me.do_you_like.mods.skinchanger.utils.game.ChatColor;
import me.do_you_like.mods.skinchanger.utils.command.ModCommand;

import net.minecraft.command.ICommandSender;

/**
 * NOTE: This class is temporary & it's methods will eventually be tuned into a new gui.
 */
public class SkinCommand extends ModCommand {
    public static boolean IS_SLIM_SKIN = false;

    private SkinChangerMenu mainMenu = null;
    
    public SkinCommand(SkinChangerMod modIn) {
        super(modIn);
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
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("fix")) {
                org.lwjgl.opengl.Display.setResizable(false);
                org.lwjgl.opengl.Display.setResizable(true);

                return;
            } else if (args[0].equalsIgnoreCase("slim")) {
                IS_SLIM_SKIN = !IS_SLIM_SKIN;

                sendMessage(ChatColor.AQUA + "Your skin is now " + (IS_SLIM_SKIN ? "slim" : "normal"));

                return;
            } else if (args[0].equalsIgnoreCase("ui") || args[0].equalsIgnoreCase("gui")) {
                getMenu(args.length > 1 ? args[1] : "").display();

                return;
            }
        }

        if (args.length == 0) {
            sendMessage(ChatColor.RED + "Incorrect usage, try: /skinchanger <name>");
        } else if (args[0].equalsIgnoreCase("null")) {
            IS_SLIM_SKIN = false;

            sendMessage(ChatColor.AQUA + "Your skin has been reset!");
        } else {
            if (!InternetConnection.hasInternetConnection()) {
                sendMessage(ChatColor.RED + "Could not connect to the internet. " + ChatColor.RED + "Make sure you have a stable internet connection!");
                return;
            }

            String id = this.mod.getMojangHooker().getRealNameFromName(args[0]);

            if (id == null) {
                id = args[0];
            }

            IS_SLIM_SKIN = this.mod.getMojangHooker().hasSlimSkin(id);

            sendMessage(ChatColor.AQUA + "Set skin to " + id + "\'s skin!");
        }

//        if (args.length == 0) {
//            new SettingsGui(this.mod).display();
//        } else {
//            new SettingsGui(this.mod, args[0]).display();
//        }
    }

    @Override
    protected boolean shouldMultithreadCommand(String[] args) {
        return true;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 0;
    }

    private SkinChangerMenu getMenu(String playerName) {
        if (this.mainMenu == null) {
            this.mainMenu = new SkinChangerMenu();
        }

        if (playerName != null) {
            this.mainMenu.handleIncomingInput(playerName);
        }

        return this.mainMenu;
    }
}
