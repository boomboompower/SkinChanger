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

import me.boomboompower.skinchanger.SkinChangerMod;
import me.boomboompower.skinchanger.utils.ReflectUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class CapeManager {

    private List<String> logs = new ArrayList<>();

    private AbstractClientPlayer playerIn;

    private boolean normalPlayer = false;
    private boolean usingCape = false;

    public CapeManager(AbstractClientPlayer playerIn, boolean normalPlayer) {
        this.playerIn = playerIn;
        this.normalPlayer = normalPlayer;
    }

    public void addCape() {
        usingCape = true;
        Minecraft.getMinecraft().addScheduledTask(() -> setCape(new ResourceLocation(SkinChangerMod.MOD_ID,"cape.png")));
    }

    public void removeCape() {
        usingCape = false;
        Minecraft.getMinecraft().addScheduledTask(() -> setCape(null));
    }

    public void updatePlayer(AbstractClientPlayer playerIn) {
        this.playerIn = normalPlayer ? Minecraft.getMinecraft().thePlayer : playerIn;
    }

    /*
     * MISC
     */

    public void setCape(ResourceLocation location) {
        if (SkinChangerMod.getInstance().getWebsiteUtils().isDisabled() || (normalPlayer ? Minecraft.getMinecraft().thePlayer == null : playerIn == null)) return;

        NetworkPlayerInfo info = null;

        try {
            info = (NetworkPlayerInfo) ReflectUtils.findMethod(AbstractClientPlayer.class, new String[] {"getPlayerInfo", "func_175155_b"}).invoke(normalPlayer ? Minecraft.getMinecraft().thePlayer : playerIn);
        } catch (Throwable ex) {
            log("Could not find player info, issue whilst invoking");
        }

        if (info == null) {
            log("playerInfo for cape was null, stopping so nothing gets broken");
            return;
        }

        try {
            ReflectUtils.setPrivateValue(NetworkPlayerInfo.class, info, location, "locationCape", "field_178862_f");
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

    protected boolean isHelper() {
        return false; // TODO
    }
}

