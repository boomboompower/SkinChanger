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

package me.boomboompower.skinchanger.mixins.tweaks;

import com.mojang.authlib.GameProfile;

import me.boomboompower.skinchanger.SkinChangerModOld;
import me.boomboompower.skinchanger.gui.experimental.GuiExperimentalAllPlayers;

import me.do_you_like.mods.skinchanger.methods.impl.mixins.SkinChangerTweaker;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

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
    @Overwrite
    public ResourceLocation getLocationSkin()
    {
        if (getEntityId() == Minecraft.getMinecraft().thePlayer.getEntityId()) {
            if (SkinChangerTweaker.MIXINS_ENABLED && SkinChangerModOld.getInstance().isRenderingEnabled() && SkinChangerModOld
                .getInstance().getSkinManager().getShouldUse()) {
                if (SkinChangerModOld.getInstance().getSkinManager().getSkinName().equals(this.lastSkinName)) {
                    return this.lastSkin;
                }

                this.lastSkinName = SkinChangerModOld.getInstance().getSkinManager().getSkinName();
                this.lastSkin = SkinChangerModOld.getInstance().getSkinManager().getSkin(this.lastSkinName);

                return this.lastSkin;
            }
        }

        NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
        return networkplayerinfo == null ? DefaultPlayerSkin.getDefaultSkin(this.getUniqueID()) : networkplayerinfo.getLocationSkin();
    }

    /**
     * @author boomboompower
     *
     * @reason Hooking the location of the cape to force set it in the game code
     */
    @Overwrite
    public ResourceLocation getLocationCape()
    {
        if (getEntityId() == Minecraft.getMinecraft().thePlayer.getEntityId()) {
            if (SkinChangerTweaker.MIXINS_ENABLED && SkinChangerModOld.getInstance().getCapeManager().isUsingCape()) {
                if (SkinChangerModOld.getInstance().getCapeManager().getOfCapeName().equals(this.lastCapeName)) {
                    return this.lastSkin;
                }

                this.lastCapeName = SkinChangerModOld.getInstance().getCapeManager().getOfCapeName();
                this.lastCape = SkinChangerModOld.getInstance().getCapeManager().getOfCape(this.lastCapeName);

                return this.lastCape;
            }
        } else if (GuiExperimentalAllPlayers.forcedAllSkins != null) {
            return GuiExperimentalAllPlayers.forcedAllSkins;
        }

        NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
        return networkplayerinfo == null ? null : networkplayerinfo.getLocationCape();
    }

    /**
     * @author boomboompower
     *
     * @reason We're adding a hook to the skin type so the player can change the display type of the skin
     */
    @Overwrite
    public String getSkinType()
    {
        if (getEntityId() == Minecraft.getMinecraft().thePlayer.getEntityId()) {
            if (SkinChangerTweaker.MIXINS_ENABLED && SkinChangerModOld.getInstance().isRenderingEnabled()) {
                return SkinChangerModOld.getInstance().getSkinManager().getSkinType().getSecretName();
            }
        }

        NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
        return networkplayerinfo == null ? DefaultPlayerSkin.getSkinType(this.getUniqueID()) : networkplayerinfo.getSkinType();
    }

    @Shadow
    protected abstract NetworkPlayerInfo getPlayerInfo();

}
