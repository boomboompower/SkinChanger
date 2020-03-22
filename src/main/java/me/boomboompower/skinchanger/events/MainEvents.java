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

package me.boomboompower.skinchanger.events;

import java.util.Iterator;

import me.boomboompower.skinchanger.SkinChangerModOld;

import me.do_you_like.mods.skinchanger.utils.gui.player.FakePlayer;
import me.do_you_like.mods.skinchanger.utils.gui.player.FakePlayerCape;
import me.do_you_like.mods.skinchanger.methods.impl.mixins.SkinChangerTweaker;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;

import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.util.List;

@SuppressWarnings("ALL")
@Deprecated
public class MainEvents {

    private final SkinChangerModOld mod;
    
    private int currentTick = 100;
    
    public MainEvents(SkinChangerModOld modIn) {
        this.mod = modIn;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (SkinChangerTweaker.MIXINS_ENABLED) {
            return;
        }

        if (Minecraft.getMinecraft().currentScreen == null) {
            if (this.currentTick > 0) {
                --this.currentTick;
            } else {
                this.currentTick = 100;
    
                if (!this.mod.getSkinManager().getSkinName().isEmpty() && this.mod.isRenderingEnabled()) {
                    this.mod.getSkinManager().updateSkin();
                }
    
                if (this.mod.getCapeManager().isUsingCape()) {
                    if (this.mod.getCapeManager().isExperimental()) {
                        this.mod.getCapeManager().giveOfCape(
                            this.mod.getCapeManager().getOfCapeName());
                    } else {
                        this.mod.getCapeManager().addCape();
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onRender(RenderPlayerEvent.Pre event) {
        if (SkinChangerTweaker.MIXINS_ENABLED) {
            return;
        }

        if (event.entityPlayer instanceof FakePlayer) {
            List<LayerRenderer<?>> layerRenderers = ReflectionHelper.getPrivateValue(RendererLivingEntity.class, event.renderer, "layerRenderers", "field_177097_h");
            
            boolean modified = false;
            boolean hasFakeCape = false;
    
            Iterator<LayerRenderer<?>> iterator = layerRenderers.iterator();
            
            // Wipe out all cape rendering instances, whilst testing for ours
            while (iterator.hasNext()) {
                LayerRenderer<?> layerRenderer = iterator.next();
                
                if (layerRenderer instanceof LayerCape) {
                    modified = true;
                    
                    iterator.remove();
                } else if (layerRenderer instanceof FakePlayerCape) {
                    hasFakeCape = true;
                }
            }
            
            if (!hasFakeCape) {
                modified = true;
                
                layerRenderers.add(new FakePlayerCape(event.renderer));
            }
            
            if (modified) {
                ReflectionHelper.setPrivateValue(RendererLivingEntity.class, event.renderer, layerRenderers, "layerRenderers", "field_177097_h");
            }
        }
    }
}
