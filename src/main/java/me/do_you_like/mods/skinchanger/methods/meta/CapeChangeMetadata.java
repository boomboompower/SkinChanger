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

public class CapeChangeMetadata implements ChangeData {

    private String playerName;
    private ResourceLocation resource;
    private boolean isActive;

    @Override
    public String getName() {
        return null;
    }

    @Override
    public ResourceLocation getResource() {
        return null;
    }

    @Override
    public void setResource(String potentialName) {

    }

    @Override
    public void setResource(ResourceLocation location) {

    }

    @Override
    public boolean isActive() {
        return this.resource == null;
    }

    @Override
    public void setActive(boolean active) {
        if (active && this.resource == null && this.playerName != null) {
            downloadCape(this.playerName);
        }
    }

    private void downloadCape(String name) {

    }
}
