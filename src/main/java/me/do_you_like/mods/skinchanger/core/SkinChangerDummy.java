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

package me.do_you_like.mods.skinchanger.core;

import com.google.common.eventbus.EventBus;

import me.do_you_like.mods.skinchanger.SkinChangerMod;

import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;

/**
 * This is required so forge recognises our mod located in {@link SkinChangerMod} class & loads it properly.
 */
public class SkinChangerDummy extends DummyModContainer {

    public SkinChangerDummy() {
        super(new ModMetadata());

        if (SkinChangerMod.getInstance() == null) {
            System.out.println("Something may be going wrong here...");
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        return true;
    }
}
