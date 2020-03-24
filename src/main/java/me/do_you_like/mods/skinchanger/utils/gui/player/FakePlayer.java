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

package me.do_you_like.mods.skinchanger.utils.gui.player;

import com.mojang.authlib.GameProfile;

import java.util.UUID;

import me.do_you_like.mods.skinchanger.SkinChangerMod;
import me.do_you_like.mods.skinchanger.compatability.DefaultPlayerSkin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.StatBase;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

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
     * New constructor, just uses the players current world.
     */
    public FakePlayer() {
        this(Minecraft.getMinecraft().thePlayer.worldObj);
    }

    public FakePlayer(AbstractClientPlayer player) {
        this(Minecraft.getMinecraft().thePlayer.worldObj);

        getPlayerInfo().setLocationSkin(player.getLocationSkin());
        getPlayerInfo().setLocationCape(player.getLocationCape());
        getPlayerInfo().setSkinType(DefaultPlayerSkin.getSkinType(player.getUniqueID()));
    }

    // This is required for comparability.
    public FakePlayer(World world) {
        super(world, FAKE_GAME_PROFILE);
    }

    public FakePlayerInfo getPlayerInfo() {
        return this.playerInfo == null ? this.playerInfo = new FakePlayerInfo(this) : this.playerInfo;
    }

    public boolean hasPlayerInfo() {
        return getPlayerInfo() != null;
    }

    @Override
    public boolean hasSkin() {
        return getPlayerInfo().hasLocationSkin();
    }

    @Override
    public ResourceLocation getLocationSkin() {
        return getPlayerInfo().getLocationSkin();
    }

    @Override
    public boolean hasCape() {
        return getPlayerInfo().hasLocationCape();
    }

    @Override
    public ResourceLocation getLocationCape() {
        return getPlayerInfo().getLocationCape();
    }

    @Override public boolean canCommandSenderUseCommand(int i, String s){ return false; }
    @Override public ChunkCoordinates getCommandSenderPosition() {
        return new ChunkCoordinates(0,0,0);
    }
    @Override public void addChatMessage(IChatComponent mess){}
    @Override public void addChatComponentMessage(IChatComponent mess){}
    @Override public void addStat(StatBase par1StatBase, int par2){}
    @Override public void openGui(Object mod, int modGuiId, World world, int x, int y, int z){}
    @Override public boolean isEntityInvulnerable(){ return true; }
    @Override public boolean canAttackPlayer(EntityPlayer player){ return false; }
    @Override public void onDeath(DamageSource source){ return; }
    @Override public void onUpdate(){ return; }
    @Override public void travelToDimension(int dim){ return; }

    /**
     * Copies resources from one player to another.
     *
     * @param player the player to copy resources from.
     */
    public void copyFrom(AbstractClientPlayer player) {
        getPlayerInfo().setLocationSkin(player.getLocationSkin());
        getPlayerInfo().setLocationCape(player.getLocationCape());
        getPlayerInfo().setSkinType(DefaultPlayerSkin.getSkinType(player.getUniqueID()));
    }
    
    public GameProfile getFakeGameProfile() {
        return FAKE_GAME_PROFILE;
    }
}
