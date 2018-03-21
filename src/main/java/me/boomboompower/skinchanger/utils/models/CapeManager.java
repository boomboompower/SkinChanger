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

package me.boomboompower.skinchanger.utils.models;

import me.boomboompower.skinchanger.SkinChangerMod;
import me.boomboompower.skinchanger.utils.ReflectUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CapeManager {

    private List<String> logs = new ArrayList<>();

    private AbstractClientPlayer playerIn;

    private String ofCapeUsername = "";

    private boolean isExperimental = false;
    private boolean isClientPlayer = false;
    private boolean usingCape = false;

    public CapeManager(AbstractClientPlayer playerIn, boolean isClientPlayer) {
        this.playerIn = playerIn;
        this.isClientPlayer = isClientPlayer;
    }

    public void addCape() {
        this.usingCape = true;
        Minecraft.getMinecraft().addScheduledTask(() -> setCape(new ResourceLocation(SkinChangerMod.MOD_ID, "cape.png")));
    }

    public void addCape(ResourceLocation location) {
        this.usingCape = true;
        Minecraft.getMinecraft().addScheduledTask(() -> setCape(location));
    }

    public void removeCape() {
        this.usingCape = false;
        Minecraft.getMinecraft().addScheduledTask(() -> setCape(null));
    }

    public void updatePlayer(AbstractClientPlayer playerIn) {
        this.playerIn = this.isClientPlayer ? Minecraft.getMinecraft().thePlayer : playerIn;
    }

    /*
     * MISC
     */

    public void setCape(ResourceLocation location) {
        if (this.isClientPlayer ? Minecraft.getMinecraft().thePlayer == null : this.playerIn == null) return;

        NetworkPlayerInfo info = null;

        try {
            info = (NetworkPlayerInfo) ReflectUtils.findMethod(AbstractClientPlayer.class, new String[] {"getPlayerInfo", "func_175155_b"}).invoke(getPlayer());
        } catch (Throwable ex) {
            log("Could not find player info, issue whilst invoking");
        }

        if (info == null) {
            log("playerInfo for cape was null, stopping so nothing gets broken");
            return;
        }

        try {
            if (getPlayer().getLocationCape() != null && !location.equals(getPlayer().getLocationCape())) {
                Minecraft.getMinecraft().renderEngine.deleteTexture(getPlayer().getLocationCape());
            }
            ReflectUtils.setPrivateValue(NetworkPlayerInfo.class, info, location, "locationCape", "field_178862_f");
        } catch (Throwable x) {
            x.printStackTrace();
        }
    }

    public boolean isUsingCape() {
        return this.usingCape;
    }

    public boolean isExperimental() {
        return this.isExperimental;
    }

    public void setUsingCape(boolean usingCape) {
        this.usingCape = usingCape;
    }

    public void setExperimental(boolean isExperimental) {
        this.isExperimental = isExperimental;
    }

    public void giveOfCape(String name) {
        this.isExperimental = true;
        this.setCape(getOfCape(this.ofCapeUsername = name));
    }

    public String getOfCapeName() {
        return this.ofCapeUsername;
    }

    protected ResourceLocation getOfCape(String name) {
        if (name != null && !name.isEmpty()) {
            final String url = "http://s.optifine.net/capes/" + name + ".png";
            final String id = UUID.nameUUIDFromBytes(name.getBytes()).toString();

            final ResourceLocation rl = new ResourceLocation("ofcape/" + id);

            File file1 = new File(new File("./mods/skinchanger".replace("/", File.separator), "ofcape"), id);
            File file2 = new File(file1, id + ".png");

            TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

            IImageBuffer imageBuffer = new IImageBuffer() {
                @Override
                public BufferedImage parseUserSkin(BufferedImage img) {
                    int imageWidth = 64;
                    int imageHeight = 32;
                    int srcWidth = img.getWidth();

                    for (int srcHeight = img.getHeight(); imageWidth < srcWidth || imageHeight < srcHeight; imageHeight *= 2) {
                        imageWidth *= 2;
                    }

                    BufferedImage imgNew = new BufferedImage(imageWidth, imageHeight, 2);
                    Graphics g = imgNew.getGraphics();
                    g.drawImage(img, 0, 0, null);
                    g.dispose();
                    return imgNew;
                }

                @Override
                public void skinAvailable() {
                }
            };
            ThreadDownloadImageData textureCape = new ThreadDownloadImageData(file2, url, null, imageBuffer);
            textureManager.loadTexture(rl, textureCape);

            return rl;
        } else {
            return null;
        }
    }

    private AbstractClientPlayer getPlayer() {
        return (this.isClientPlayer ? Minecraft.getMinecraft().thePlayer : this.playerIn);
    }

    protected void log(String message, Object... replace) {
        if (this.logs.contains(message)) return;

        System.out.println(String.format("[CapeManager] " + message, replace));
        this.logs.add(message);
    }
}

