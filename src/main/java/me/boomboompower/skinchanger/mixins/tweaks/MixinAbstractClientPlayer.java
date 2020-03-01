package me.boomboompower.skinchanger.mixins.tweaks;

import com.mojang.authlib.GameProfile;

import me.boomboompower.skinchanger.SkinChangerMod;
import me.boomboompower.skinchanger.gui.experimental.GuiExperimentalAllPlayers;
import me.boomboompower.skinchanger.mixins.Tweaker;

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
            if (Tweaker.MIXINS_ENABLED && SkinChangerMod.getInstance().isRenderingEnabled() && SkinChangerMod.getInstance().getSkinManager().getShouldUse()) {
                if (SkinChangerMod.getInstance().getSkinManager().getSkinName().equals(this.lastSkinName)) {
                    return this.lastSkin;
                }

                this.lastSkinName = SkinChangerMod.getInstance().getSkinManager().getSkinName();
                this.lastSkin = SkinChangerMod.getInstance().getSkinManager().getSkin(this.lastSkinName);

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
            if (Tweaker.MIXINS_ENABLED && SkinChangerMod.getInstance().getCapeManager().isUsingCape()) {
                if (SkinChangerMod.getInstance().getCapeManager().getOfCapeName().equals(this.lastCapeName)) {
                    return this.lastSkin;
                }

                this.lastCapeName = SkinChangerMod.getInstance().getCapeManager().getOfCapeName();
                this.lastCape = SkinChangerMod.getInstance().getCapeManager().getOfCape(this.lastCapeName);

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
            if (Tweaker.MIXINS_ENABLED && SkinChangerMod.getInstance().isRenderingEnabled()) {
                return SkinChangerMod.getInstance().getSkinManager().getSkinType().getSecretName();
            }
        }

        NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
        return networkplayerinfo == null ? DefaultPlayerSkin.getSkinType(this.getUniqueID()) : networkplayerinfo.getSkinType();
    }

    @Shadow
    protected abstract NetworkPlayerInfo getPlayerInfo();

}
