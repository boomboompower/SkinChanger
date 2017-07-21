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

import me.boomboompower.skinchanger.SkinChanger;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class SkinEvents {

    private int currentTick = 100;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (Minecraft.getMinecraft().currentScreen == null && SkinChanger.isOn) {
            if (currentTick > 0) {
                --currentTick;
            } else {
                currentTick = 100;

                if (!SkinChanger.skinManager.getSkinName().isEmpty()) {
                    SkinChanger.skinManager.updateSkin();
                }
                if (SkinChanger.capeManager.isUsingCape()) {
                    SkinChanger.capeManager.addCape();
                }
            }
        }
    }
}
