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

import me.do_you_like.mods.skinchanger.utils.gui.impl.ModernHeader;

public interface ModernDrawable {

    public int getX();

    public int getY();

    public int getWidth();

    /**
     * Calls the render function for the drawable.
     *
     * @param mouseX the current x position of the mouse.
     * @param mouseY the current y position of the mouse.
     */
    public void render(int mouseX, int mouseY);

    public default void renderFromHeader(int xPos, int yPos, int mouseX, int mouseY, int recommendedYOffset) {
        render(mouseX, mouseY);
    }

    /**
     * Should this drawable be drawn? If this is false the header will not call the {@link #render(int, int)} method.
     *
     * @return true if {@link #render(int, int)} should be called for the drawable.
     */
    public boolean isEnabled();

    public ModernDrawable setAsPartOfHeader(ModernHeader parent);

    /**
     * Stops this drawable being translatable
     *
     * @return this translatable
     */
    public default ModernDrawable disableTranslatable() {
        return this;
    }

    /**
     * Should this Drawable be translated (up/down)
     *
     * @return true if it should be translated.
     */
    public default boolean isTranslatable() {
        return true;
    }

    /**
     * Should this drawable be rendered relative to its header (if its part of one)?
     *
     * @return true if the drawable should be moved based on header position.
     */
    public default boolean renderRelativeToHeader() {
        return true;
    }
}
