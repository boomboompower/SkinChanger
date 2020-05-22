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

package wtf.boomy.mods.skinchanger.gui.additional;

import wtf.boomy.mods.skinchanger.gui.SkinChangerMenu;
import wtf.boomy.mods.skinchanger.utils.gui.impl.ModernButton;
import wtf.boomy.mods.skinchanger.utils.gui.impl.ModernCheckbox;
import wtf.boomy.mods.skinchanger.utils.gui.impl.ModernHeader;

public class ModOptionsMenu extends SkinChangerMenu {

    private final SkinChangerMenu skinChangerMenu;

    public ModOptionsMenu(SkinChangerMenu skinChangerMenu) {
        this.skinChangerMenu = skinChangerMenu;
    }

    @Override
    protected void onGuiInitExtra() {
        // Call first
        setAsSubMenu(this.skinChangerMenu);

        ModernHeader bored = new ModernHeader(this, 5, 5, "Hello World", 1, false);

        ModernCheckbox firstMeme = new ModernCheckbox(5, 10, 5, 5, true, "Hello world!");
        ModernCheckbox secondMeme = new ModernCheckbox(5, 10, 5, 5, true, "Hello world!");

        bored.addChild(firstMeme);
        bored.addChild(secondMeme);

        registerElement(bored);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        
    }

    @Override
    protected void onButtonPressedExtra(ModernButton button) {

    }
}
