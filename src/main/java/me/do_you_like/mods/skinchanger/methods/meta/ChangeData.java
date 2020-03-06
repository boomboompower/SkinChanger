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

package me.do_you_like.mods.skinchanger.methods.meta;

import net.minecraft.util.ResourceLocation;

/**
 * Stores all data related to a texture in the game.
 */
public interface ChangeData {

    /**
     * The name of the holder of this skin
     *
     * @return the name of the holder of this resource.
     */
    String getName();

    /**
     * The minecraft location of this resource.
     *
     * @return the location of the resource.
     */
    ResourceLocation getResource();

    /**
     * Sets the resource from a given name.
     *
     * @param potentialName the name to set the resource to.
     */
    void setResource(String potentialName);

    /**
     * Sets the resource from a location in Minecraft. Doing this will cause {@link #getName()} to be null.
     *
     * @param location the location of the resource.
     */
    void setResource(ResourceLocation location);

    /**
     * True if this resource is going to be used in the game code
     *
     * @return true if this resource is being actively used
     */
    boolean isActive();

    /**
     * Sets the resources active state, if this is set to false then the resource should not be used
     *
     * @param active the active state of the resource / this data
     */
    void setActive(boolean active);
}
