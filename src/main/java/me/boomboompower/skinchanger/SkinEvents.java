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

package me.boomboompower.skinchanger;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SkinEvents {

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (Minecraft.getMinecraft().currentScreen == null) {
            if (!SkinChanger.skinManager.getSkinName().isEmpty()) {
                SkinChanger.skinManager.updateSkin(Minecraft.getMinecraft().thePlayer);
            }
            if (SkinChanger.capeManager.getResourceLocation() != null) {
                SkinChanger.capeManager.addCape(Minecraft.getMinecraft().thePlayer);
            }
        }
    }
}
