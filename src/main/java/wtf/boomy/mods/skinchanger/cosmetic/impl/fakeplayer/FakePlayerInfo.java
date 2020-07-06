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

package wtf.boomy.mods.skinchanger.cosmetic.impl.fakeplayer;

import com.google.common.base.Objects;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;

/**
 * The NetworkPlayerInfo of the {@link FakePlayer}
 * <p>
 * Provides additional methods not included in the normal NetworkPlayerInfo class. This saves us from using reflection instead.
 */
public class FakePlayerInfo extends NetworkPlayerInfo {
    
    private final GameProfile profile;
    
    /**
     * The ResourceLocation for the skin
     */
    private ResourceLocation locationSkin;
    
    /**
     * The ResourceLocation for the cape
     */
    private ResourceLocation locationCape;
    
    /**
     * The type of skin model
     * <p>
     * Steve = "default"
     * Alex  = "slim"
     */
    private String skinType;
    
    public FakePlayerInfo(FakePlayer player) {
        super(player.getFakeGameProfile());
        
        this.profile = player.getFakeGameProfile();
    }
    
    @Override
    public boolean hasLocationSkin() {
        return this.locationSkin != null;
    }
    
    @Override
    public ResourceLocation getLocationSkin() {
        if (this.locationSkin == null) {
            this.loadPlayerTextures();
        }
        
        return Objects.firstNonNull(this.locationSkin, DefaultPlayerSkin.getDefaultSkinLegacy());
    }
    
    public void setLocationSkin(ResourceLocation locationSkin) {
        this.locationSkin = locationSkin;
    }
    
    @Override
    public ResourceLocation getLocationCape() {
        return this.locationCape;
    }
    
    public void setLocationCape(ResourceLocation locationCape) {
        this.locationCape = locationCape;
    }
    
    @Override
    public String getSkinType() {
        return this.skinType == null ? this.skinType = "default" : this.skinType;
    }
    
    public void setSkinType(String skinType) {
        if (skinType == null || skinType.trim().isEmpty()) {
            skinType = "default";
        }
        
        this.skinType = skinType;
    }
    
    @Override
    public void loadPlayerTextures() {
        this.locationSkin = DefaultPlayerSkin.getDefaultSkinLegacy();
        this.locationCape = null;
        this.skinType = "default";
    }
    
    @Override
    public int getResponseTime() {
        return 0;
    }
    
    @Override
    protected void setResponseTime(int time) {
    }
    
    @Override
    public GameProfile getGameProfile() {
        return this.profile;
    }
}
