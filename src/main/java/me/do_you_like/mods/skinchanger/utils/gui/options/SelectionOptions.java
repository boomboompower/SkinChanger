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

import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;

import me.do_you_like.mods.skinchanger.compatability.DefaultPlayerSkin;
import me.do_you_like.mods.skinchanger.utils.backend.ThreadFactory;
import me.do_you_like.mods.skinchanger.utils.resources.CapeBuffer;
import me.do_you_like.mods.skinchanger.utils.resources.LocalFileData;
import me.do_you_like.mods.skinchanger.utils.resources.SkinBuffer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ResourceLocation;

public class SelectionOptions {

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
        if (player == null) {
            // Error has occurred.
            return;
        }

        player.onSkinAvailable(Type.SKIN, newLocation);

        if (response != null) {
            response.run(null);
        }
    }

    public void setCape(AbstractClientPlayer player, ResourceLocation newLocation, OptionResponse<Void> response) {
        if (player == null) {
            // Error has occurred.
            return;
        }

        player.onSkinAvailable(Type.CAPE, newLocation);

        if (response != null) {
            response.run(null);
        }
    }
}
