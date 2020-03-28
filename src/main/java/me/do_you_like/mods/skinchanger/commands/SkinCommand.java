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
import me.do_you_like.mods.skinchanger.utils.command.ModCommand;
import me.do_you_like.mods.skinchanger.utils.game.ChatColor;

import net.minecraft.command.ICommandSender;

/**
 * NOTE: This class is temporary & it's methods will eventually be tuned into a new gui.
 */
public class SkinCommand extends ModCommand {

    // Cached main menu, saves memory and options.
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
            } else if (args[0].equalsIgnoreCase("reload")) {
                this.mainMenu = new SkinChangerMenu();

                args = new String[0];
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
