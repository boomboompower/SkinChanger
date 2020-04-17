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

package me.do_you_like.mods.skinchanger.gui.additional;

import lombok.experimental.var;
import me.do_you_like.mods.skinchanger.gui.SkinChangerMenu;
import me.do_you_like.mods.skinchanger.utils.gui.impl.ModernButton;
import me.do_you_like.mods.skinchanger.utils.gui.impl.ModernCheckbox;
import me.do_you_like.mods.skinchanger.utils.gui.impl.ModernHeader;

public class ModOptionsMenu extends SkinChangerMenu {

    private final SkinChangerMenu skinChangerMenu;

    public ModOptionsMenu(SkinChangerMenu skinChangerMenu) {
        this.skinChangerMenu = skinChangerMenu;
    }

    @Override
    protected void onGuiInitExtra() {
        // Call first
        setAsSubMenu(this.skinChangerMenu);

        var bored = new ModernHeader(this, 5, 5, "Hello World", 1, false);

        var firstMeme = new ModernCheckbox(5, 10, 50, 50, true, "Hello world!");
        var secondMeme = new ModernCheckbox(5, 10, 50, 50, true, "Hello world!");

        bored.addChild(firstMeme);
        bored.addChild(secondMeme);

        registerElement(bored);
    }

    @Override
    protected void onButtonPressedExtra(ModernButton button) {

    }

    @Override
    public void setRotation(float rotation) {
        this.rotation = rotation;

        this.skinChangerMenu.setRotation(rotation);
    }
}
