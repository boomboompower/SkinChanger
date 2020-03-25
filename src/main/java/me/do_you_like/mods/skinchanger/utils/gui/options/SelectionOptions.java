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

package me.do_you_like.mods.skinchanger.utils.gui.options;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;

import java.util.HashMap;

import me.do_you_like.mods.skinchanger.utils.backend.ReflectionUtils;
import me.do_you_like.mods.skinchanger.utils.backend.ThreadFactory;
import me.do_you_like.mods.skinchanger.utils.resources.CapeBuffer;
import me.do_you_like.mods.skinchanger.utils.resources.LocalFileData;
import me.do_you_like.mods.skinchanger.utils.resources.SkinBuffer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;

public class SelectionOptions {

    private HashMap<AbstractClientPlayer, NetworkPlayerInfo> cachedPlayerInfo = new HashMap<>();
    private ThreadFactory threadFactory = new ThreadFactory("SelectionOptions");

    public void loadFromFile(OptionResponse<ResourceLocation> callback, boolean isCape) {
        // Calling this code on the main thread will hang the game
        if (Minecraft.getMinecraft().isCallingFromMinecraftThread()) {
            this.threadFactory.runAsync(() -> loadFromFile(callback, isCape));

            return;
        }

        try {
            FileDialog dialog = new FileDialog((Frame) null, "Select File to Open");
            dialog.setMode(FileDialog.LOAD);
            dialog.setFile("*.png;*.jpg;*.jpeg");
            dialog.setMultipleMode(false);

            dialog.setVisible(true);

            if (dialog.getFiles().length > 0) {
                File f = dialog.getFiles()[0];

                ResourceLocation customResource = new ResourceLocation("skins/" + f.getName());

                Minecraft.getMinecraft().addScheduledTask(() -> {
                    Minecraft.getMinecraft().renderEngine.loadTexture(customResource, new LocalFileData(DefaultPlayerSkin.getDefaultSkinLegacy(), f, isCape ? new CapeBuffer() : new SkinBuffer()));
                });

                callback.run(customResource);
            } else {
                callback.onCancel();
            }
        } catch (Exception ex) {
            ex.printStackTrace();

            if (ex.getMessage() != null) {
                callback.onError(ex.getMessage());
            } else {
                callback.onCancel();
            }
        }
    }

    public void setSkin(AbstractClientPlayer player, ResourceLocation newLocation, OptionResponse<Void> response) {
        NetworkPlayerInfo playerInfo = getPlayerInfo(player, response);

        if (playerInfo == null) {
            // Error has occurred.
            return;
        }

        ReflectionUtils.setPrivateValue(NetworkPlayerInfo.class, playerInfo, newLocation, "locationSkin", "field_178865_e");

        if (response != null) {
            response.run(null);
        }
    }

    public void setCape(AbstractClientPlayer player, ResourceLocation newLocation, OptionResponse<Void> response) {
        NetworkPlayerInfo playerInfo = getPlayerInfo(player, response);

        if (playerInfo == null) {
            // Error has occurred.
            return;
        }

        ReflectionUtils.setPrivateValue(NetworkPlayerInfo.class, playerInfo, newLocation, "locationCape", "field_178862_f");

        if (response != null) {
            response.run(null);
        }
    }

    private NetworkPlayerInfo getPlayerInfo(AbstractClientPlayer player, OptionResponse<Void> response) {
        if (this.cachedPlayerInfo.containsKey(player)) {
            return this.cachedPlayerInfo.get(player);
        }

        try {
            NetworkPlayerInfo playerInfo = (NetworkPlayerInfo) ReflectionUtils.findMethod(AbstractClientPlayer.class, new String[] {"getPlayerInfo", "func_175155_b"}).invoke(player);

            this.cachedPlayerInfo.put(player, playerInfo);

            return playerInfo;
        } catch (Throwable t) {
            t.printStackTrace();

            if (t.getMessage() != null) {
                response.onError(t.getMessage());
            } else {
                response.onError(t.getClass().getName());
            }

            return null;
        }
    }
}
