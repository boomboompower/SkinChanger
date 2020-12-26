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

package wtf.boomy.mods.skinchanger.utils.cosmetic.options;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;

import wtf.boomy.mods.skinchanger.SkinChangerMod;
import wtf.boomy.mods.skinchanger.utils.cosmetic.impl.SkinChangerStorage;
import wtf.boomy.mods.skinchanger.utils.cosmetic.impl.fakeplayer.FakePlayer;
import wtf.boomy.mods.skinchanger.utils.ambiguous.ReflectionUtils;
import wtf.boomy.mods.skinchanger.utils.ambiguous.ThreadFactory;
import wtf.boomy.mods.skinchanger.utils.cosmetic.PlayerSkinType;
import wtf.boomy.mods.skinchanger.utils.cosmetic.resources.CapeBuffer;
import wtf.boomy.mods.skinchanger.utils.cosmetic.resources.LocalFileData;
import wtf.boomy.mods.skinchanger.utils.cosmetic.resources.SkinBuffer;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.util.HashMap;

public class ReflectionOptions {
    
    private final HashMap<AbstractClientPlayer, NetworkPlayerInfo> cachedPlayerInfo = new HashMap<>();
    private final ThreadFactory threadFactory = new ThreadFactory("ReflectionOptions");
    
    private ResourceLocation originalSkin;
    private ResourceLocation originalCape = Minecraft.getMinecraft().thePlayer.getLocationCape();
    private String originalSkinType = Minecraft.getMinecraft().thePlayer.getSkinType();
    
    /**
     * Loads a ResourceLocation from a file, with a buffer if possible
     *
     * @param callback the callback with the ResourceLocation to be ran if this succeeds.
     * @param isCape true if a cape buffer should be used.
     */
    public void loadFromFile(SimpleCallback<ResourceLocation> callback, boolean isCape) {
        // Calling this code on the main thread will hang the game
        if (Minecraft.getMinecraft().isCallingFromMinecraftThread()) {
            this.threadFactory.runAsync(() -> loadFromFile(callback, isCape));
            
            return;
        }
        
        try {
            FileDialog dialog = new FileDialog((Frame) null, "Select File to Open");
            dialog.setMode(FileDialog.LOAD);
            dialog.setFile("*.png;*.jpg;*.jpeg;*.gif");
            dialog.setMultipleMode(false);
            
            if (isCape) {
                dialog.setDirectory(SkinChangerMod.getInstance().getConfig().getCapesDirectory().getAbsolutePath());
            }
            
            dialog.setVisible(true);
            dialog.requestFocus();
            
            if (dialog.getFiles().length > 0) {
                File f = dialog.getFiles()[0];
                
                ResourceLocation customResource = new ResourceLocation("skins/" + f.getName());
                
                Minecraft.getMinecraft().addScheduledTask(() -> {
                    Minecraft.getMinecraft().renderEngine.loadTexture(customResource, new LocalFileData(DefaultPlayerSkin.getDefaultSkinLegacy(), f, isCape ? new CapeBuffer() : new SkinBuffer()));
    
                    callback.run(customResource);
                });
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
    
    /**
     * Uses reflection to set the player skin
     *
     * @param player the player of which to set the skin
     * @param newLocation the location of the skin
     * @param response the callback for once this operation is complete
     */
    public void setSkin(AbstractClientPlayer player, ResourceLocation newLocation, SimpleCallback<Void> response) {
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
    
    /**
     * Uses reflection to set the player cape
     *
     * @param player the player of which to set the cape
     * @param newLocation the location of the cape
     * @param response the callback for once this operation is complete
     */
    public void setCape(AbstractClientPlayer player, ResourceLocation newLocation, SimpleCallback<Void> response) {
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
    
    /**
     * Uses reflection to set the players skin type
     *
     * @param player the player to use
     * @param skinType the skin type to use
     * @param response called once the operation is complete
     */
    public void setSkinType(AbstractClientPlayer player, PlayerSkinType skinType, SimpleCallback<Void> response) {
        setSkinType(player, skinType.getSecretName(), response);
    }
    
    /**
     * Uses reflection to set the players skin type
     *
     * @param player the player to use
     * @param skinType the skin type to use
     * @param response called once the operation is complete
     */
    public void setSkinType(AbstractClientPlayer player, String skinType, SimpleCallback<Void> response) {
        NetworkPlayerInfo playerInfo = getPlayerInfo(player, response);
    
        if (playerInfo == null) {
            // Error has occurred.
            return;
        }
    
        ReflectionUtils.setPrivateValue(NetworkPlayerInfo.class, playerInfo, skinType, "skinType", "field_178863_g", "g");
    
        if (response != null) {
            response.run(null);
        }
    }
    
    public void resetCachedValues() {
        this.originalSkin = Minecraft.getMinecraft().thePlayer.getLocationSkin();
        this.originalCape = Minecraft.getMinecraft().thePlayer.getLocationCape();
        this.originalSkinType = Minecraft.getMinecraft().thePlayer.getSkinType();
    }
    
    public void resetPlayer(FakePlayer player) {
        player.copyFrom(this.originalSkin, this.originalCape, this.originalSkinType);
    }
    
    public void resetPlayer(SkinChangerStorage storage) {
        if (storage.isSkinPatchApplied()) {
            storage.setPlayerSkin(null);
        } else {
            setSkin(Minecraft.getMinecraft().thePlayer, this.originalSkin, null);
        }
    
        if (storage.isCapePatchApplied()) {
            storage.setPlayerCape(null);
        } else {
            setCape(Minecraft.getMinecraft().thePlayer, this.originalCape, null);
        }
    
        if (storage.isSkinTypePatchApplied()) {
            storage.setSkinType(null);
        } else {
            setSkinType(Minecraft.getMinecraft().thePlayer, this.originalSkinType, null);
        }
    }
    
    public ResourceLocation getOriginalSkin() {
        return originalSkin;
    }
    
    public ResourceLocation getOriginalCape() {
        return originalCape;
    }
    
    public String getOriginalSkinType() {
        return originalSkinType;
    }
    
    private NetworkPlayerInfo getPlayerInfo(AbstractClientPlayer player, SimpleCallback<Void> response) {
        if (this.cachedPlayerInfo.containsKey(player)) {
            return this.cachedPlayerInfo.get(player);
        }
    
        if (this.cachedPlayerInfo.size() > 300) {
            this.cachedPlayerInfo.clear();
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
