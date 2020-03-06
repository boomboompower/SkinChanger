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

package me.boomboompower.skinchanger.utils.fake;

import com.mojang.authlib.GameProfile;

import java.util.UUID;

import me.boomboompower.skinchanger.SkinChangerModOld;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.stats.StatBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

/**
 * A class for a Fake Player instance, inspired by Forges' @{@link net.minecraftforge.common.util.FakePlayer FakePlayer}
 *
 * @author boomboompower
 * @version 2.0
 */
public class FakePlayer extends AbstractClientPlayer {
    
    private static final GameProfile FAKE_GAME_PROFILE = new GameProfile(UUID.nameUUIDFromBytes("skinchanger".getBytes()), "SkinChanger v" + SkinChangerModOld.VERSION);
    
    private FakePlayerInfo playerInfo;
    
    /**
     * New constuctor, just uses the users current world.
     */
    public FakePlayer() {
        this(Minecraft.getMinecraft().thePlayer.worldObj);
    }
    
    public FakePlayer(World world) {
        super(world, FAKE_GAME_PROFILE);
    }
    
    @Override
    public FakePlayerInfo getPlayerInfo() {
        return this.playerInfo == null ? this.playerInfo = new FakePlayerInfo(this) : this.playerInfo;
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
    
    @Override
    public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
        return false;
    }
    
    @Override
    public void addChatComponentMessage(IChatComponent chatmessagecomponent) {
    }
    
    @Override
    public void addStat(StatBase stat, int amount) {
    }
    
    @Override
    public void openGui(Object mod, int modGuiId, World world, int x, int y, int z) {
    }
    
    @Override
    public boolean isEntityInvulnerable(DamageSource source){
        return true;
    }
    
    @Override
    public boolean canAttackPlayer(EntityPlayer player){
        return false;
    }
    
    @Override
    public void onDeath(DamageSource source) {
    }
    
    @Override
    public void onUpdate() {
    }
    
    @Override
    public void travelToDimension(int dim) {
    }
    
    public GameProfile getFakeGameProfile() {
        return FAKE_GAME_PROFILE;
    }
}
