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

package me.do_you_like.mods.skinchanger.utils.gui;

import me.do_you_like.mods.skinchanger.utils.gui.impl.ModernButton;

public interface UISkeleton {

    void onGuiOpen();

    default void onGuiClose() {
    }

    default void preRender() {
    }

    void onRender(int mouseX, int mouseY, float partialTicks);

    default void postRender() {
    }

    default void buttonPressed(ModernButton button) {
    }

    default void rightClicked(ModernButton button) {
    }

    default void onKeyTyped(int keyCode, char keyCharacter) {
    }
}
