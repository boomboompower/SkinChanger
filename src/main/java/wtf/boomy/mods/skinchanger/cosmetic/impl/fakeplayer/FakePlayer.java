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

import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.stats.StatBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import wtf.boomy.mods.skinchanger.SkinChangerMod;

import java.util.UUID;

/**
 * A class for a Fake Player instance, inspired by Forges' @{@link net.minecraftforge.common.util.FakePlayer FakePlayer}
 *
 * @author boomboompower
 * @version 2.0
 */
public class FakePlayer extends AbstractClientPlayer {
    
    private static final GameProfile FAKE_GAME_PROFILE = new GameProfile(UUID.nameUUIDFromBytes("skinchanger".getBytes()), "SkinChanger v" + SkinChangerMod.VERSION);
    
    private FakePlayerInfo playerInfo;
    
    /**
     * Clones the resources from a user
     *
     * @param player the player to clone
     */
    public FakePlayer(AbstractClientPlayer player) {
        this(player.worldObj);
        
        getPlayerInfo().setLocationSkin(player.getLocationSkin());
        getPlayerInfo().setLocationCape(player.getLocationCape());
        getPlayerInfo().setSkinType(player.getSkinType());
    }
    
    // All entities require a world constructor
    public FakePlayer(World world) {
        super(world, FAKE_GAME_PROFILE);
    }
    
    @Override
    public FakePlayerInfo getPlayerInfo() {
        return this.playerInfo == null ? this.playerInfo = new FakePlayerInfo(this) : this.playerInfo;
    }
    
    @Override
    public void addChatComponentMessage(IChatComponent component) {
    }
    
    @Override
    public void addStat(StatBase stat, int amount) {
    }
    
    @Override
    public void openGui(Object mod, int modGuiId, World world, int x, int y, int z) {
    }
    
    @Override
    public void onUpdate() {
    }
    
    @Override
    public void onDeath(DamageSource source) {
    }
    
    @Override
    public void travelToDimension(int dim) {
    }
    
    @Override
    public boolean isEntityInvulnerable(DamageSource source) {
        return true;
    }
    
    @Override
    public boolean canAttackPlayer(EntityPlayer player) {
        return false;
    }
    
    @Override
    public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
        return false;
    }
    
    @Override
    public boolean hasPlayerInfo() {
        return getPlayerInfo() != null;
    }
    
    @Override
    public boolean isWearing(EnumPlayerModelParts modelParts) {
        return true;
    }
    
    @Override
    public Vec3 getPositionVector() {
        return new Vec3(0, 0, 0);
    }
    
    /**
     * Copies resources from one player to another.
     *
     * @param player the player to copy resources from.
     */
    public void copyFrom(AbstractClientPlayer player) {
        getPlayerInfo().setLocationSkin(player.getLocationSkin());
        getPlayerInfo().setLocationCape(player.getLocationCape());
        getPlayerInfo().setSkinType(player.getSkinType());
    }
    
    /**
     * Copies resources from an external source
     *
     * @param skin     the skin resource
     * @param cape     the cape resource
     * @param skinType the skin type
     */
    public void copyFrom(ResourceLocation skin, ResourceLocation cape, String skinType) {
        getPlayerInfo().setLocationSkin(skin);
        getPlayerInfo().setLocationCape(cape);
        getPlayerInfo().setSkinType((skinType == null || skinType.trim().isEmpty()) ? "default" : skinType);
    }
    
    public GameProfile getFakeGameProfile() {
        return FAKE_GAME_PROFILE;
    }
}
