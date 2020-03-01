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

import me.boomboompower.skinchanger.SkinChangerMod;
import me.boomboompower.skinchanger.gui.SettingsGui;
import me.boomboompower.skinchanger.utils.ChatColor;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

import java.util.Arrays;
import java.util.List;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class MainCommand extends CommandBase {

    private SkinChangerMod mod;
    
    public MainCommand(SkinChangerMod modIn) {
        this.mod = modIn;
    }
    
    @Override
    public String getCommandName() {
        return "skinchanger";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return ChatColor.RED + "Usage: /" + getCommandName();
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("changeskin", "changecape");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length > 0 && args[0].length() == 1) {
            Display.setResizable(false);
            Display.setResizable(true);
            return;
        }

        if (args.length == 0) {
            new SettingsGui(this.mod).display();
        } else {
            new SettingsGui(this.mod, args[0]).display();
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
    

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 0;
    }
}
