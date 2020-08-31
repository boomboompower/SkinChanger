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
    
    private Minecraft minecraft;
    
    private final SkinChangerMod mod;
    
    public SkinChangerStorage(SkinChangerMod mod) {
        this.mod = mod;
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
    
    public ResourceLocation getPlayerSkin(GameProfile profile) {
        if (!this.isSkinPatchApplied) {
            return null;
        }
    
        if (profile == null || getMinecraft() == null) {
            return null;
        }
    
        return (this.mod.getConfigurationHandler().isEveryoneMe() || profile.getId() == getMinecraft().getSession().getProfile().getId()) ? this.playerSkin : null;
    }
    
    public ResourceLocation getPlayerCape(GameProfile profile) {
        if (!this.isCapePatchApplied) {
            return null;
        }
        
        if (profile == null || getMinecraft() == null) {
            return null;
        }
        
        return (this.mod.getConfigurationHandler().isEveryoneMe() || profile.getId() == getMinecraft().getSession().getProfile().getId()) ? this.playerCape : null;
    }
    
    public String getSkinType(GameProfile profile) {
        if (!this.isSkinTypePatchApplied) {
            return null;
        }
        
        if (profile == null || getMinecraft() == null) {
            return null;
        }
        
        return (this.mod.getConfigurationHandler().isEveryoneMe() || profile.getId() == getMinecraft().getSession().getProfile().getId()) ? this.skinType : null;
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
    
    private Minecraft getMinecraft() {
        if (this.minecraft == null) {
            this.minecraft = Minecraft.getMinecraft();
        }
        
        return this.minecraft;
    }
}
