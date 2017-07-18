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

package me.boomboompower.skinchanger.capes;

import me.boomboompower.skinchanger.SkinChanger;
import me.boomboompower.skinchanger.utils.ReflectUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.invoke.MethodHandle;
import java.util.ArrayList;
import java.util.List;

public class CapeManager {

    private List<String> logs = new ArrayList<>();
    private boolean usingCape = false;

    private static final MethodHandle GET_PLAYER_INFO = ReflectUtils.findMethod(AbstractClientPlayer.class, new String[] {"getPlayerInfo", "func_175155_b"});

    private ResourceLocation resourceLocation = new ResourceLocation(SkinChanger.MOD_ID,"cape.png");

    public CapeManager() {
    }

    public ResourceLocation getResourceLocation() {
        return this.resourceLocation;
    }

    public void setResourceLocation(ResourceLocation location) {
        this.resourceLocation = location;
    }

    public void addCape(AbstractClientPlayer thePlayer) {
        usingCape = true;
        if (resourceLocation == null) setResourceLocation(new ResourceLocation(SkinChanger.MOD_ID,"cape.png"));
        Minecraft.getMinecraft().addScheduledTask(() -> setCape(thePlayer, resourceLocation));
    }

    public void removeCape(AbstractClientPlayer thePlayer) {
        usingCape = false;
        setResourceLocation(null);
        Minecraft.getMinecraft().addScheduledTask(() -> setCape(thePlayer, null));
    }

    /*
     * MISC
     */

    public void setCape(AbstractClientPlayer player, ResourceLocation location) {
        NetworkPlayerInfo info = null;

        try {
            info = (NetworkPlayerInfo) GET_PLAYER_INFO.invoke(player);
        } catch (Throwable ex) {
            log("Could not find player info, issue whilst invoking");
        }

        if (info == null) {
            log("playerInfo was null, returning!");
            return;
        }

        try {
            ObfuscationReflectionHelper.setPrivateValue(NetworkPlayerInfo.class, info, location, "locationCape", "field_178862_f");
        } catch (Throwable x) {
            x.printStackTrace();
        }
    }

    public boolean isUsingCape() {
        return this.usingCape;
    }

    public void setUsingCape(boolean usingCape) {
        this.usingCape = usingCape;
    }

//    public BufferedImage parseCape(BufferedImage img) {
//        int imageWidth = 64;
//        int imageHeight = 32;
//        int srcWidth = img.getWidth();
//
//        for (int srcHeight = img.getHeight(); imageWidth < srcWidth || imageHeight < srcHeight; imageHeight *= 2) {
//            imageWidth *= 2;
//        }
//
//        BufferedImage imgNew = new BufferedImage(imageWidth, imageHeight, 2);
//        Graphics g = imgNew.getGraphics();
//        g.drawImage(img, 0, 0, null);
//        g.dispose();
//        return imgNew;
//    }

    protected void log(String message, Object... replace) {
        if (logs.contains(message)) return;

        System.out.println(String.format("[CapeManager] " + message, replace));
        logs.add(message);
    }
}

