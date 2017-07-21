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

import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.stats.StatBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.UUID;

public class FakePlayerUtils {

    public static FakePlayer getFakePlayer() {
        return new FakePlayer(Minecraft.getMinecraft().thePlayer.worldObj);
    }

    public static class FakePlayer extends AbstractClientPlayer {

        private static final GameProfile FAKE_GAME_PROFILE = new GameProfile(UUID.nameUUIDFromBytes("skinchanger".getBytes()), "MyNameIsJeff213");

        private NetworkPlayerInfo playerInfo;

        public FakePlayer(World world) {
            super(world, FAKE_GAME_PROFILE);
        }

        @Override
        protected NetworkPlayerInfo getPlayerInfo() {
            return playerInfo == null ? playerInfo = new NetworkPlayerInfo(FAKE_GAME_PROFILE) : playerInfo;
        }

        @Override
        public boolean hasPlayerInfo() {
            return playerInfo != null;
        }

        @Override
        public boolean isWearing(EnumPlayerModelParts p_175148_1_) {
            return true;
        }

        @Override
        public Vec3 getPositionVector() {
            return new Vec3(0, 0, 0);
        }

        @Override
        public boolean canCommandSenderUseCommand(int i, String s) {
            return false;
        }

        @Override
        public void addChatComponentMessage(IChatComponent chatmessagecomponent) {
        }

        @Override
        public void addStat(StatBase par1StatBase, int par2) {
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
    }
}
