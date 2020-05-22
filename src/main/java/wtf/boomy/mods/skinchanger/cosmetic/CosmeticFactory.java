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

package wtf.boomy.mods.skinchanger.cosmetic;

import lombok.Getter;

import wtf.boomy.mods.skinchanger.SkinChangerMod;
import wtf.boomy.mods.skinchanger.cosmetic.impl.fakeplayer.FakePlayerRender;
import wtf.boomy.mods.skinchanger.cosmetic.impl.ShaderPatch;

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

    private FakePlayerRender fakePlayerRender;

    /** SkinChanger mod instance */
    @Getter
    private final SkinChangerMod mod;

    public CosmeticFactory(SkinChangerMod mod) {
        this.mod = mod;

        // Blur shader magic
        this.blurShader = new ShaderPatch(new ResourceLocation("skinchanger", "shaders/post/customblur.json"));
    }

    /**
     * Returns the FakePlayer rendering class
     *
     * @return the fake player renderer
     */
    public FakePlayerRender getFakePlayerRender() {
        if (this.fakePlayerRender == null) {
            this.fakePlayerRender = new FakePlayerRender(this);
        }

        return this.fakePlayerRender;
    }
}
