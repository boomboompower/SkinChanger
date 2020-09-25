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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkinChangerStorage {
    
    private boolean isSkinPatchApplied = false;
    private boolean isCapePatchApplied = false;
    private boolean isSkinTypePatchApplied = false;
    
    private ResourceLocation playerSkin;
    private ResourceLocation playerCape;
    
    private String skinType;
    
    private Minecraft minecraft;
    
    private final SkinChangerMod mod;
    
    private final Map<UUID, Boolean> cachedComparisons = new HashMap<>();
    
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
    
        return isMe(profile) ? this.playerSkin : null;
    }
    
    public ResourceLocation getPlayerCape(GameProfile profile) {
        if (!this.isCapePatchApplied) {
            return null;
        }
        
        if (profile == null || getMinecraft() == null) {
            return null;
        }
        
        return isMe(profile) ? this.playerCape : null;
    }
    
    public String getSkinType(GameProfile profile) {
        if (!this.isSkinTypePatchApplied) {
            return null;
        }
        
        if (profile == null || getMinecraft() == null) {
            return null;
        }
        
        return isMe(profile) ? this.skinType : null;
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
    
    private boolean isMe(GameProfile profile) {
        // If the mod isn't enabled trick em.
        if (!this.mod.getConfig().isModEnabled() || profile == null) {
            return false;
        }
        
        if (this.mod.getConfig().isEveryoneMe()) {
            return true;
        }
        
        if (this.cachedComparisons.size() > 300) {
            this.cachedComparisons.clear();
        }
        
        if (this.cachedComparisons.containsKey(profile.getId())) {
            return this.cachedComparisons.get(profile.getId());
        }
        
        UUID profileId = profile.getId();
        UUID mcId = getMinecraft().getSession().getProfile().getId();
    
        boolean equals = profileId.equals(mcId);
        
        this.cachedComparisons.put(profileId, equals);
        
        return equals;
    }
    
    private Minecraft getMinecraft() {
        if (this.minecraft == null) {
            this.minecraft = Minecraft.getMinecraft();
        }
        
        return this.minecraft;
    }
}
