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

package me.do_you_like.mods.skinchanger.cosmetic.impl;

import java.lang.invoke.MethodHandle;

import me.do_you_like.mods.skinchanger.utils.backend.ReflectionUtils;
import me.do_you_like.mods.skinchanger.utils.general.Prerequisites;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;

/**
 * Applies a Shader to Minecraft using Minecraft's inbuilt shader program.
 *
 * This is part of the cosmetic system.
 *
 * <b>Note:</b> This class should be registered <i>after</i> postInit so the EntityRenderer can be loaded into the game
 *
 * @version 3.0.0
 * @author boomboompower
 */
public class ShaderPatch {

    private static final MethodHandle shaderMethod =
        ReflectionUtils.findMethod(EntityRenderer.class,
            new String[] {
                "loadShader", // searge
                "func_175069_a" // notch
        }, ResourceLocation.class);

    // Stores Minecraft's EntityRenderer class
    private final EntityRenderer entityRenderer = Minecraft.getMinecraft().entityRenderer;

    // Stores the shader we want to load.
    private final ResourceLocation shader;

    /**
     * General constructor, containing shader ResourceLocation
     *
     * There are many in-built ones which include:
     *   - new ResourceLocation("shaders/post/bits.json")
     *   - new ResourceLocation("shaders/post/notch.json")
     *   - new ResourceLocation("shaders/post/spider.json")
     *   - new ResourceLocation("shaders/post/wobble.json")
     *
     * If you are using a custom texture (not from Minecraft) then use:
     *   - new ResourceLocation("modid", "shaders/post/myshader.json")
     *
     * @param location the location of the shader. Cannot be null.
     */
    public ShaderPatch(ResourceLocation location) {
        Prerequisites.notNull(this.entityRenderer, "ShaderPatch should be called after postInit()");
        Prerequisites.notNull(location, "A shader cannot be null.");

        this.shader = location;
    }

    /**
     * Applies the shader to the EntityRenderer class. If the user does
     */
    public void applyShader() {
        if (!canBeUsed()) {
            return;
        }

        Prerequisites.notNull(this.entityRenderer, "ShaderPatch should be called after postInit()");
        Prerequisites.notNull(this.shader, "A shader cannot be null.");

        try {
            // Reflection, first value is the instance, second value is the argument
            shaderMethod.invoke(this.entityRenderer, this.shader);
        } catch (OutOfMemoryError e) {

            // We are already screwed
            OpenGlHelper.shadersSupported = false;
        } catch (Throwable throwable) {
            // Just log the error. This is not good though
            throwable.printStackTrace();
        }
    }

    /**
     * Tells the EntityRenderer class to stop using its shader. Throws an error if someone tried to register
     * this class before Minecraft created its EntityRenderer instance.
     */
    public void killShader() {
        Prerequisites.notNull(this.entityRenderer, "ShaderPatch should be called after postInit()");

        this.entityRenderer.stopUseShader();
    }

    /**
     * Is this cosmetic supported? Note: this is "can" be used, not "should" be used.
     *
     * @return true if this cosmetic can be used.
     */
    public boolean canBeUsed() {
        return OpenGlHelper.shadersSupported;
    }
}
