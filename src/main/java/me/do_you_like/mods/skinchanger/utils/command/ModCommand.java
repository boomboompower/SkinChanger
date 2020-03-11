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

package me.do_you_like.mods.skinchanger.utils.command;

import java.util.List;
import me.do_you_like.mods.skinchanger.SkinChangerMod;
import me.do_you_like.mods.skinchanger.utils.backend.ThreadFactory;
import me.do_you_like.mods.skinchanger.utils.game.ChatColor;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

/**
 * An enhanced CommandBase implementation which provides a few extra features.
 *
 * @since SkinChanger v3.0
 * @author boomboompower
 */
public abstract class ModCommand extends CommandBase {

    // Async thread handler.
    private final ThreadFactory threadFactory = new ThreadFactory("ModCommand");

    protected SkinChangerMod mod;

    public ModCommand(SkinChangerMod skinChangerMod) {
        this.mod = skinChangerMod;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return ChatColor.RED + "Usage: /" + getCommandName();
    }

    @Override
    public final List<String> getCommandAliases() {
        return getAliases();
    }

    @Override
    public final void processCommand(ICommandSender sender, String[] args) {
        try {
            if (shouldMultithreadCommand(args)) {
                this.threadFactory.runAsync(() -> onCommand(sender, args));
            } else {
                onCommand(sender, args);
            }
        } catch (Exception ex) {
            sendMessage(ChatColor.RED + "An error occurred whilst running this command.");

            ex.printStackTrace();
        }
    }

    /**
     * A safer way to execute a command. Errors which occur here will not cause the game to crash.
     *
     * @param sender the sender of the command
     * @param args the arguments of the command
     */
    public abstract void onCommand(ICommandSender sender, String[] args);

    public abstract List<String> getAliases();

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    /**
     * Return true if the command should be run on its own thread.
     *
     * @param args the arguments of the command
     * @return true if the command should be async
     */
    protected boolean shouldMultithreadCommand(String[] args) {
        return false;
    }

    /**
     * Sends a raw message to the client.
     *
     * @param message the message to send
     */
    protected void sendMessage(String message) {
        Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(message));
    }
}
