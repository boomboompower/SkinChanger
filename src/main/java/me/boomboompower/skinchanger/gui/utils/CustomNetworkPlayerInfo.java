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

package me.boomboompower.skinchanger.gui.utils;

import com.google.common.base.Objects;
import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldSettings;

public class CustomNetworkPlayerInfo extends NetworkPlayerInfo {

    /** The GameProfile for the player represented by this NetworkPlayerInfo instance */
    private final GameProfile gameProfile;
    private WorldSettings.GameType gameType = WorldSettings.GameType.NOT_SET;

    /** Player response time to server in milliseconds */
    private int responseTime = 0;
    private boolean playerTexturesLoaded = false;

    private ResourceLocation locationSkin = new ResourceLocation("textures/entity/steve.png");
    private ResourceLocation locationCape;

    private String skinType = "default";

    /** When this is non-null, it is displayed instead of the player's real name */
    private IChatComponent displayName;

    public CustomNetworkPlayerInfo(GameProfile gameProfile) {
        super(gameProfile);
        this.gameProfile = gameProfile;
    }

    public CustomNetworkPlayerInfo(S38PacketPlayerListItem.AddPlayerData packet) {
        super(packet);
        this.gameProfile = packet.getProfile();
        this.gameType = packet.getGameMode();
        this.responseTime = packet.getPing();
        this.displayName = packet.getDisplayName();
    }

    /**
     * Returns the GameProfile for the player represented by this NetworkPlayerInfo instance
     */
    public GameProfile getGameProfile()
    {
        return this.gameProfile;
    }

    public WorldSettings.GameType getGameType()
    {
        return this.gameType;
    }

    public int getResponseTime()
    {
        return this.responseTime;
    }

    protected void setGameType(WorldSettings.GameType gameType)
    {
        this.gameType = gameType;
    }

    protected void setResponseTime(int responseTime)
    {
        this.responseTime = responseTime;
    }

    public boolean hasLocationSkin()
    {
        return this.locationSkin != null;
    }

    public String getSkinType() {
        return this.skinType == null ? DefaultPlayerSkin.getSkinType(this.gameProfile.getId()) : this.skinType;
    }

    public ResourceLocation getLocationSkin() {
        if (this.locationSkin == null) {
            this.loadPlayerTextures();
        }
        return Objects.firstNonNull(this.locationSkin, DefaultPlayerSkin.getDefaultSkin(this.gameProfile.getId()));
    }

    public ResourceLocation getLocationCape() {
        if (this.locationCape == null) {
            this.loadPlayerTextures();
        }
        return this.locationCape;
    }

    public void setLocationSkin(ResourceLocation locationSkin) {
        this.locationSkin = locationSkin;
    }

    public void setLocationCape(ResourceLocation locationCape) {
        this.locationCape = locationCape;
    }

    public ScorePlayerTeam getPlayerTeam() {
        return Minecraft.getMinecraft().theWorld.getScoreboard().getPlayersTeam(this.getGameProfile().getName());
    }

    protected void loadPlayerTextures() {
        synchronized (this) {
            if (!this.playerTexturesLoaded) {
                this.playerTexturesLoaded = true;
                Minecraft.getMinecraft().getSkinManager().loadProfileTextures(this.gameProfile, (texture, location, profileTexture) -> {
                    switch (texture) {
                        case SKIN:
                            locationSkin = location;
                            skinType = profileTexture.getMetadata("model");

                            if (skinType == null) {
                                skinType = "default";
                            }
                            break;
                        case CAPE:
                            locationCape = location;
                    }
                }, true);
            }
        }
    }

    public void setDisplayName(IChatComponent displayNameIn) {
        this.displayName = displayNameIn;
    }

    public IChatComponent getDisplayName() {
        return this.displayName;
    }
}