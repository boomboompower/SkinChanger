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

import java.awt.Color;

import lombok.Getter;
import lombok.Setter;

import me.do_you_like.mods.skinchanger.utils.gui.InteractiveDrawable;
import me.do_you_like.mods.skinchanger.utils.gui.ModernGui;

public class ModernCheckbox implements InteractiveDrawable {

    @Getter
    private int x;

    @Getter
    private int y;

    @Getter
    private int width;

    @Getter int height;

    @Getter
    @Setter
    private boolean enabled = true;

    @Getter
    @Setter
    private boolean checked;

    private ModernHeader parentHeader;

    public ModernCheckbox(int x, int y, int width, int height) {
        this(x, y, width, height, false);
    }

    public ModernCheckbox(int x, int y, int width, int height, boolean checked) {
        this.x = x;
        this.y = y;

        this.width = width;
        this.height = height;

        this.checked = checked;
    }

    @Override
    public void render(int mouseX, int mouseY, float yTranslation) {
        float x = this.x;
        float y = this.y;

        float finalX = x + this.width;
        float finalY = y + this.height;

        Color color = Color.WHITE;

        if (!this.enabled) {
            color = color.darker();
        }

        ModernGui.drawRectangleOutlineF(x - 1, y - 1, finalX, finalY, color.getRGB());

        if (this.checked) {
            ModernGui.drawRect((int) x + 1, (int) y + 1, (int) finalX - 1, (int) finalY - 1, color.getRGB());
        }
    }

    @Override
    public void renderFromHeader(int xPos, int yPos, float yTranslation, int mouseX, int mouseY, int recommendedYOffset) {
        float x = xPos + this.x;
        float y = recommendedYOffset + 1;

        float finalX = x + this.width;
        float finalY = y + this.height;

        Color color = Color.WHITE;

        if (!this.enabled) {
            color = color.darker();
        }

        ModernGui.drawRectangleOutlineF(x - 1, y - 1, finalX, finalY, color.getRGB());

        if (this.checked) {
            ModernGui.drawRect((int) x + 1, (int) y + 1, (int) finalX - 1, (int) finalY - 1, color.getRGB());
        }
    }


    @Override
    public InteractiveDrawable setAsPartOfHeader(ModernHeader parent) {
        this.parentHeader = parent;

        return this;
    }

    @Override
    public void onLeftClick(int mouseX, int mouseY, float yTranslation) {
        this.checked = !this.checked;
    }

    public boolean isInside(int mouseX, int mouseY, float yTranslation) {
        //if (!this.visible) {
        //    return false;
       // }

        int xPosition = this.x;
        int yPosition = this.y;

        if (this.parentHeader != null) {
            yPosition = (int) this.parentHeader.getOffsetBetweenDrawables();
            xPosition += this.parentHeader.getX();
        }

        yPosition += yTranslation;

        return mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + this.width && mouseY < yPosition + this.height;
    }

    private float min(float first, float second) {
        return first <= second ? first : second;
    }

    private float max(float first, float second) {
        return first >= second ? first : second;
    }
}
