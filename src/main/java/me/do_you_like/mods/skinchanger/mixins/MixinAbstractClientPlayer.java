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

package me.do_you_like.mods.skinchanger.mixins;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public abstract class MixinAbstractClientPlayer extends EntityPlayer {

    private ResourceLocation lastCape;
    private String lastCapeName = null;

    private ResourceLocation lastSkin;
    private String lastSkinName = null;

    public MixinAbstractClientPlayer(World worldIn, GameProfile gameProfileIn) {
        super(worldIn, gameProfileIn);
    }

    /**
     * @author boomboompower
     *
     * @reason Hooking the location of the skin to force set it in the game code
     */
    @Inject(method = "getLocationSkin()Lnet/minecraft/util/ResourceLocation;", at = @At("HEAD"), cancellable = true)
    public void getLocationSkin(CallbackInfoReturnable<ResourceLocation> callback) {

    }

    /**
     * @author boomboompower
     *
     * @reason Hooking the location of the cape to force set it in the game code
     */
    @Inject(method = "getLocationCape()Lnet/minecraft/util/ResourceLocation;", at = @At("HEAD"), cancellable = true)
    public void getLocationCape(CallbackInfoReturnable<ResourceLocation> callback) {
    }

    /**
     * @author boomboompower
     *
     * @reason We're adding a hook to the skin type so the player can change the display type of the skin
     */
    @Inject(method = "getSkinType", at = @At("HEAD"), cancellable = true)
    public void getSkinType(CallbackInfoReturnable<String> callback) {
    }

    @Shadow
    protected abstract NetworkPlayerInfo getPlayerInfo();

}
