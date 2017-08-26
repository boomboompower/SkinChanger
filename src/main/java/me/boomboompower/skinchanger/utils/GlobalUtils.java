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

package me.boomboompower.skinchanger.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public class GlobalUtils {

    public static final String PREFIX = ChatColor.AQUA + "SkinChanger" + ChatColor.GOLD + " > " + ChatColor.GRAY;

    public static void sendChatMessage(String msg) {
        sendChatMessage(msg, true);
    }

    public static void sendChatMessage(String msg, boolean usePrefix) {
        Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText((usePrefix ? PREFIX : ChatColor.GRAY) + msg));
    }
}
