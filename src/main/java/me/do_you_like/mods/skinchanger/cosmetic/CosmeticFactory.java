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

package me.do_you_like.mods.skinchanger.cosmetic;

import lombok.Getter;

import me.do_you_like.mods.skinchanger.SkinChangerMod;
import me.do_you_like.mods.skinchanger.cosmetic.impl.ShaderPatch;

import net.minecraft.util.ResourceLocation;

/**
 * A class to contain all the cosmetic components of SkinChanger.
 * These are configurable in-game and will likely increase performance if they are disabled.
 *
 * @since 3.0.0
 * @author boomboompower
 */
public class CosmeticFactory {

    /** GUI blur when enabling she SkinChanger mod */
    @Getter
    private final ShaderPatch blurShader;

    /** SkinChanger mod instance */
    @Getter
    private final SkinChangerMod mod;

    public CosmeticFactory(SkinChangerMod mod) {
        this.mod = mod;

        this.blurShader = new ShaderPatch(new ResourceLocation("skinchanger", "shaders/post/customblur.json"));
    }
}
