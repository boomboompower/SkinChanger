package me.boomboompower.skinchanger.utils.fake;

import com.google.common.base.Objects;
import com.mojang.authlib.GameProfile;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;

/**
 * The NetworkPlayerInfo of the {@link me.boomboompower.skinchanger.utils.fake.FakePlayer}
 *
 * Provids many extra methods not included in the normal NetworkPlayerInfo class
 */
public class FakePlayerInfo extends NetworkPlayerInfo {
    
    private final GameProfile profile;
    
    /** The ResourceLocation for the skin */
    private ResourceLocation locationSkin;
    
    /** The ResourceLocation for the cape */
    private ResourceLocation locationCape;
    
    /** The type of skin model
     *
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
        this.skinType = skinType;
    }
    
    @Override
    protected void loadPlayerTextures() {
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
