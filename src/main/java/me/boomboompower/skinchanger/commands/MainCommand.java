/*
 *     Copyright (C) 2017 boomboompower
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

package me.boomboompower.skinchanger.commands;

import me.boomboompower.skinchanger.SkinChanger;
import me.boomboompower.skinchanger.gui.SettingsGui;
import me.boomboompower.skinchanger.utils.ChatColor;
import me.boomboompower.skinchanger.utils.GlobalUtils;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

import java.util.Arrays;
import java.util.List;

public class MainCommand implements ICommand {

    @Override
    public String getCommandName() {
        return "changeskin";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return ChatColor.RED + "Usage: /" + getCommandName();
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("changecape", "skin", "cape");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (SkinChanger.isOn) {
            if (SkinChanger.useWhitelist && !SkinChanger.isOnWhitelist) {
                GlobalUtils.sendChatMessage("SkinChanger is currently in whitelist-only mode", false);
                GlobalUtils.sendChatMessage("Contact boomboompower for permission to use!", false);
            } else {
                if (args.length == 0) {
                    new SettingsGui().display();
                } else {
                    if (args[0].equalsIgnoreCase("/toggle")) {
                        SkinChanger.isOn = !SkinChanger.isOn;
                        GlobalUtils.sendChatMessage("Master toggle switch activated");
                        GlobalUtils.sendChatMessage(String.format("Mod is now forced %s.", SkinChanger.isOn ? ChatColor.GREEN + "on" + ChatColor.GRAY : ChatColor.RED + "off" + ChatColor.GRAY));
                        return;
                    }
                    new SettingsGui(args[0]).display();
                }
            }
        } else {
            GlobalUtils.sendChatMessage("SkinChager is currently disabled.", false);
            GlobalUtils.sendChatMessage("Message boomboompower on the forums for more info!", false);
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 0;
    }

    @Override
    public int compareTo(ICommand o) {
        return 0;
    }
}
