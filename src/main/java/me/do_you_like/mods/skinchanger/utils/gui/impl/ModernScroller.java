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

package me.do_you_like.mods.skinchanger.utils.gui.impl;

import com.google.common.collect.Lists;

import java.awt.Color;
import java.util.List;

import lombok.Getter;

import me.do_you_like.mods.skinchanger.utils.gui.InteractiveDrawable;
import me.do_you_like.mods.skinchanger.utils.gui.ModernDrawable;
import me.do_you_like.mods.skinchanger.utils.gui.ModernGui;

/**
 * Similar to a {@link ModernHeader}
 */
public class ModernScroller implements InteractiveDrawable {

    @Getter
    private int x;

    @Getter
    private int y;

    @Getter
    private int width;

    @Getter
    private int height;

    // Each node will be rendered with a height of 20px
    private List<ScrollableNode> nodes = Lists.newArrayList();


    public ModernScroller(int x, int y, int width) {
        this.x = x;
        this.y = y;

        this.width = width;

        // Default height.
        this.height = 10;
    }

    @Override
    public void render(int mouseX, int mouseY, float yTranslation) {
        Color transparentBlue = new Color(255, 255, 255, 120);

        ModernGui.drawRectangleOutlineF(this.x, this.y, this.x + this.width, this.y + this.height, transparentBlue.getRGB());

        for (int i = 0; i < this.nodes.size(); i++) {
            ScrollableNode node = this.nodes.get(i);

            int top = this.y;

            // Padding
            top += 2;

            // Position
            top += (i * 20);

            ModernGui.drawRect(this.x + 2, top, this.x + this.width - 2, top + 30, transparentBlue.getRGB());
        }
    }

    @Override
    public boolean isInside(int mouseX, int mouseY, float yTranslation) {
        return false;
    }

    @Override
    public void onLeftClick(int mouseX, int mouseY, float yTranslation) {

    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public ModernDrawable setAsPartOfHeader(ModernHeader parent) {
        throw new UnsupportedOperationException("ModernScrollbar's cannot be added to a header.");
    }

    public void insertNode(ScrollableNode node) {
        if (node == null) {
            System.err.println("A null node was registered. Ignoring.");
            return;
        } else if (this.nodes.contains(node)) {
            // Don't want duplicates.
            return;
        }

        this.height += 30;

        this.nodes.add(node);
    }

    public class ScrollableNode {

    }
}
