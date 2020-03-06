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

package me.do_you_like.mods.skinchanger.methods;

import me.do_you_like.mods.skinchanger.methods.meta.ChangeData;

public interface ChangingMethod {

    /**
     * Called when the player changes their skin
     */
    void onSkinChange(ChangeData data);

    /**
     * Called when the player resets their skin
     */
    void onSkinReset(ChangeData data);

    void onCapeChange(String oldCape, String newCape);

    void onCapeReset();
}
