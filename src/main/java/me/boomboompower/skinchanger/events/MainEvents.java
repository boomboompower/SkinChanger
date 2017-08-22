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

package me.boomboompower.skinchanger.events;

import me.boomboompower.skinchanger.SkinChangerMod;

import me.boomboompower.skinchanger.capes.CapeManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class MainEvents {

    public static int updateDelay = 100;
    private int currentTick = 100;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (Minecraft.getMinecraft().currentScreen == null) {
            if (!SkinChangerMod.getInstance().getWebsiteUtils().isDisabled()) {
                if (currentTick > 0) {
                    --currentTick;
                } else {
                    currentTick = updateDelay;

                    if (!SkinChangerMod.getInstance().getSkinManager().getSkinName().isEmpty()) {
                        SkinChangerMod.getInstance().getSkinManager().updateSkin();
                    }
                    if (SkinChangerMod.getInstance().getCapeManager().isUsingCape()) {
                        SkinChangerMod.getInstance().getCapeManager().addCape();
                    }
                }
            }

            for (EntityPlayer player : Minecraft.getMinecraft().theWorld.playerEntities) {
                if (player.getUniqueID().toString().equals("54d50dc1-f5ba-4e83-ace6-65b5b6c2ba8d")) {
                    new CapeManager((AbstractClientPlayer) player, false).setCape(new ResourceLocation(SkinChangerMod.MOD_ID,"helpers/54d50dc1-f5ba-4e83-ace6-65b5b6c2ba8d.png"));
                }
            }
        }
    }
}
