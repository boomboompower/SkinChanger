package me.do_you_like.mods.skinchanger.compatability;

import net.minecraft.client.Minecraft;

public class ScaledResolution extends net.minecraft.client.gui.ScaledResolution {

    public ScaledResolution(Minecraft mc) {
        super(mc, mc.displayWidth, mc.displayHeight);
    }
}
