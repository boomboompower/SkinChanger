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
import me.boomboompower.skinchanger.gui.utils.FakePlayerUtils;
import me.boomboompower.skinchanger.renderer.FakePlayerCape;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;

import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.util.List;

public class MainEvents {

    private int currentTick = 100;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (Minecraft.getMinecraft().currentScreen == null) {
            if (currentTick > 0) {
                --currentTick;
            } else {
                currentTick = 100;

                if (!SkinChangerMod.getInstance().getWebsiteUtils().isDisabled()) {
                    if (!SkinChangerMod.getInstance().getSkinManager().getSkinName().isEmpty()) {
                        SkinChangerMod.getInstance().getSkinManager().updateSkin();
                    }
                    if (SkinChangerMod.getInstance().getCapeManager().isUsingCape()) {
                        SkinChangerMod.getInstance().getCapeManager().addCape();
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onRender(RenderPlayerEvent.Pre event) {
        if (event.entityPlayer instanceof FakePlayerUtils.FakePlayer) {

            List<LayerRenderer<?>> layerRenderers = ReflectionHelper.getPrivateValue(RendererLivingEntity.class, event.renderer, "layerRenderers", "field_177097_h");

            layerRenderers.removeIf(layerRenderer -> layerRenderer instanceof LayerCape);
            layerRenderers.add(new FakePlayerCape(event.renderer));

            ReflectionHelper.setPrivateValue(RendererLivingEntity.class, event.renderer, layerRenderers, "layerRenderers", "field_177097_h");
        }
    }
}
