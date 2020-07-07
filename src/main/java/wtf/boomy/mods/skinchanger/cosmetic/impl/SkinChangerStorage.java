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

package wtf.boomy.mods.skinchanger.cosmetic.impl;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import wtf.boomy.mods.skinchanger.SkinChangerMod;

public class SkinChangerStorage {
    
    private boolean isSkinPatchApplied = false;
    private boolean isCapePatchApplied = false;
    private boolean isSkinTypePatchApplied = false;
    
    private ResourceLocation playerSkin;
    private ResourceLocation playerCape;
    
    private String skinType;
    
    private final Minecraft minecraft;
    
    public SkinChangerStorage() {
        this.minecraft = Minecraft.getMinecraft();
    }
    
    public boolean isUsingSkin(GameProfile profile) {
        if (!this.isSkinPatchApplied) {
            return false;
        }
        
        if (profile == null || this.minecraft == null) {
            return false;
        }
        
        if (this.minecraft.thePlayer == null || this.minecraft.thePlayer.getGameProfile() == null) {
            return false;
        }
        
        return SkinChangerMod.getInstance().getConfigurationHandler().isEveryoneMe() || (this.minecraft.thePlayer.getGameProfile() == profile && this.playerSkin != null);
    }
    
    public boolean isUsingCape(GameProfile profile) {
        if (!this.isCapePatchApplied) {
            return false;
        }
        
        if (profile == null || this.minecraft == null) {
            return false;
        }
        
        if (this.minecraft.thePlayer == null || this.minecraft.thePlayer.getGameProfile() == null) {
            return false;
        }
        
        return this.minecraft.thePlayer.getGameProfile() == profile && this.playerCape != null;
    }
    
    public void setPlayerSkin(ResourceLocation playerSkin) {
        this.playerSkin = playerSkin;
    }
    
    public void setPlayerCape(ResourceLocation playerCape) {
        this.playerCape = playerCape;
    }
    
    public void setSkinType(String skinType) {
        this.skinType = skinType;
    }
    
    public ResourceLocation getSkin() {
        return this.playerSkin;
    }
    
    public ResourceLocation getCape() {
        return this.playerCape;
    }
    
    public String getSkinType(GameProfile profile) {
        if (!this.isSkinTypePatchApplied) {
            return null;
        }
        
        if (profile == null || this.minecraft == null) {
            return null;
        }
        
        if (this.minecraft.thePlayer == null || this.minecraft.thePlayer.getGameProfile() == null) {
            return null;
        }
        
        return (SkinChangerMod.getInstance().getConfigurationHandler().isEveryoneMe() || profile == this.minecraft.thePlayer.getGameProfile()) ? this.skinType : null;
    }
    
    public boolean isSkinPatchApplied() {
        return this.isSkinPatchApplied;
    }
    
    public boolean isCapePatchApplied() {
        return this.isCapePatchApplied;
    }
    
    public boolean isSkinTypePatchApplied() {
        return this.isSkinTypePatchApplied;
    }
    
    public void setSkinPatchApplied(boolean skinPatchApplied) {
        this.isSkinPatchApplied = skinPatchApplied;
    }
    
    public void setCapePatchApplied(boolean capePatchApplied) {
        this.isCapePatchApplied = capePatchApplied;
    }
    
    public void setSkinTypePatchApplied(boolean skinTypePatchApplied) {
        this.isSkinTypePatchApplied = skinTypePatchApplied;
    }
}
