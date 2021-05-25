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

package wtf.boomy.mods.skinchanger.utils.cosmetic.impl;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import wtf.boomy.mods.skinchanger.SkinChangerMod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A storage class for currently applied patches to the game and the player cape and skin to use.
 *
 * @author boomboompower
 */
public final class SkinChangerStorage {
    
    // The singleton instance of this class
    private static SkinChangerStorage instance;
    
    // Which patches have been applied through the coremod
    private boolean isSkinPatchApplied = false;
    private boolean isCapePatchApplied = false;
    private boolean isSkinTypePatchApplied = false;
    
    // Cached resources
    private ResourceLocation playerSkin;
    private ResourceLocation playerCape;
    
    // The skin type to use
    private String skinType;
    
    // The current minecraft instance
    private Minecraft minecraft;
    
    // The stored mod instance
    private SkinChangerMod mod = null;
    
    // All cached checks for checking if the UUID is the player
    private final Map<UUID, Boolean> cachedComparisons = new HashMap<>();
    
    /**
     * Constructor for a new storage instance
     */
    protected SkinChangerStorage() {
    }
    
    /**
     * Sets the player skin to this value
     *
     * @param playerSkin the resource to use for the player skin
     */
    public void setPlayerSkin(ResourceLocation playerSkin) {
        this.playerSkin = playerSkin;
    }
    
    /**
     * Sets the player cape to this value
     *
     * @param playerCape the resource to use for the player cape
     */
    public void setPlayerCape(ResourceLocation playerCape) {
        this.playerCape = playerCape;
    }
    
    /**
     * Sets the skin type the mod should use
     *
     * @param skinType the skin type to use. Should be supported by the renderer
     */
    public void setSkinType(String skinType) {
        this.skinType = skinType;
    }
    
    /**
     * Returns the player skin for this profile
     *
     * @param profile the GameProfile to check for skin
     * @return the ResourceLocation to override vanilla for this player
     */
    public ResourceLocation getPlayerSkin(GameProfile profile) {
        // This should only work with patches
        if (!this.isSkinPatchApplied) {
            return null;
        }
    
        // If not initialized, then return no override
        if (profile == null || getMinecraft() == null) {
            return null;
        }
    
        // If it's me, return our skin or nothing
        return shouldOverrideProfile(profile) ? this.playerSkin : null;
    }
    
    /**
     * Returns the player cape for this profile
     *
     * @param profile the GameProfile to check for cape
     * @return the ResourceLocation to override vanilla for this player
     */
    public ResourceLocation getPlayerCape(GameProfile profile) {
        // This should only work with patches
        if (!this.isCapePatchApplied) {
            return null;
        }
    
        // If not initialized, then return no override
        if (profile == null || getMinecraft() == null) {
            return null;
        }
    
        // If it's me, return our skin or nothing
        return shouldOverrideProfile(profile) ? this.playerCape : null;
    }
    
    /**
     * Returns the player skin for this profile
     *
     * @param profile the GameProfile to check for cape
     * @return the ResourceLocation to override vanilla for this player
     */
    public String getSkinType(GameProfile profile) {
        // This should only work with patches
        if (!this.isSkinTypePatchApplied) {
            return null;
        }
    
        // If not initialized, then return no override
        if (profile == null || getMinecraft() == null) {
            return null;
        }
    
        // If it's me, return our skin or nothing
        return shouldOverrideProfile(profile) ? this.skinType : null;
    }
    
    /**
     * Has our skin patch from our transformer been applied? We have this accessor so other
     * classes can tell what features of the mod we're running. The skin patch is to override
     * the skin which the game should display for the player.
     *
     * @return true if the skin patch is applied
     */
    public boolean isSkinPatchApplied() {
        return this.isSkinPatchApplied;
    }
    
    /**
     * Has our skin patch from our transformer been applied? We have this accessor so other
     * classes can tell what features of the mod we're running. The cape patch is to override
     * the cape which the game should display for the player.
     *
     * @return true if the skin patch is applied
     */
    public boolean isCapePatchApplied() {
        return this.isCapePatchApplied;
    }
    
    /**
     * Has our skin type patch from our transformer been applied? We have this accessor so other
     * classes can tell what features of the mod we're running. The skin type patch is to override
     * the player model which type which the game should render.
     *
     * @return true if the skin type patch is applied
     */
    public boolean isSkinTypePatchApplied() {
        return this.isSkinTypePatchApplied;
    }
    
    /**
     * Activates the skin patch flag. Should be called from the transformer on a successful transform.
     */
    public void activateSkinPatch() {
        this.isSkinPatchApplied = true;
    }
    
    /**
     * Activates the cape patch flag. Should be called from the transformer on a successful transform.
     */
    public void activateCapePatch() {
        this.isCapePatchApplied = true;
    }
    
    /**
     * Activates the skin type patch flag. Should be called from the transformer on a successful transform.
     */
    public void activateCapeTypePatch() {
        this.isSkinTypePatchApplied = true;
    }
    
    /**
     * Checks if the the mod should override for the current profile. Generally
     * this only returns true if the incoming profile matches that of the logged in
     * player, however it will also return true if the config setting for setting
     * everyone's skin to the players skin is set to true.
     *
     * @param profile the game profile to check under.
     * @return true if the profile should be modified.
     */
    private boolean shouldOverrideProfile(GameProfile profile) {
        // If the mod isn't enabled or the profile input is null return nothing.
        if (this.mod != null && !this.mod.getConfig().isModEnabled() || profile == null) {
            return false;
        }
        
        // If the setting for giving everyone the same skin as the client
        // is set to true we should just always return true here.
        if (this.mod != null && this.mod.getConfig().isEveryoneMe()) {
            return true;
        }
        
        // If the cached comparisons is greater than 500 entries delete them all.
        // We cache so we don't have to keep running the string equals check.
        if (this.cachedComparisons.size() > 500) {
            this.cachedComparisons.clear();
        }
        
        // If the cache contains the current ID, return the cached value
        if (this.cachedComparisons.containsKey(profile.getId())) {
            return this.cachedComparisons.get(profile.getId());
        }
        
        // Get the ID from the incoming profile
        UUID profileId = profile.getId();
        // Get the ID of the current player
        UUID mcId = getMinecraft().getSession().getProfile().getId();
    
        // Do the two IDs match?
        boolean equals = profileId.equals(mcId);
        
        // Store the result
        this.cachedComparisons.put(profileId, equals);
        
        // Return the result
        return equals;
    }
    
    /**
     * Store an incoming skinchanger instance
     *
     * @param mod the mod instance to store
     * @return the storage
     */
    public SkinChangerStorage setMod(SkinChangerMod mod) {
        this.mod = mod;
        
        return this;
    }
    
    /**
     * Returns the currently stored mod instance
     *
     * @return the instance of in this storage
     */
    public SkinChangerMod getMod() {
        return this.mod;
    }
    
    /**
     * Caches a version of the minecraft system!
     *
     * @return the cached minecraft instance.
     */
    private Minecraft getMinecraft() {
        if (this.minecraft == null) {
            this.minecraft = Minecraft.getMinecraft();
        }
        
        return this.minecraft;
    }
    
    /**
     * Singleton for the a storage object for SkinChanger
     *
     * @return the singleton instance of the storage
     */
    public static SkinChangerStorage getInstance() {
        if (instance == null) {
            instance = new SkinChangerStorage();
        }
        
        return instance;
    }
}
