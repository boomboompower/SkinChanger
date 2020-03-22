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

/**
 * An type of Drawable which can be interacted with in a GUI. (Such as buttons, sliders etc)
 *
 * @author boomboompower
 * @since 3.0.0
 * @version 1.0
 */
public interface InteractiveDrawable extends ModernDrawable {

    /**
     * Called when this Drawable is left clicked by the user
     *
     * @param mouseX the x position of the mouse
     * @param mouseY the y position of the mouse
     * @param yTranslation the y translation of the screen
     */
    public void onLeftClick(int mouseX, int mouseY, float yTranslation);

    /**
     * Called when this Drawable is right clicked by the user
     *
     * @param mouseX the x position of the mouse
     * @param mouseY the y position of the mouse
     * @param yTranslation the y translation of the screen
     */
    public default void onRightClick(int mouseX, int mouseY, float yTranslation) {
    }

    /**
     * Called when this Drawable is middle clicked by the user
     *
     * @param mouseX the x position of the mouse
     * @param mouseY the y position of the mouse
     * @param yTranslation the y translation of the screen
     */
    public default void onMiddleClick(int mouseX, int mouseY, float yTranslation) {
    }

    /**
     * Called when the mouse is released off this drawable
     *
     * @param mouseX the x position of the mouse
     * @param mouseY the y position of the mouse
     * @param yTranslation the y translation of the screen
     */
    public default void onMouseReleased(int mouseX, int mouseY, float yTranslation) {
    }

    /**
     * Queries the drawable to determine if the mouse is inside of the drawable (only useful for
     * {@link InteractiveDrawable}
     *
     * @param mouseX the raw x location of the mouse
     * @param mouseY the raw y location of the mouse
     * @param yTranslation the translation in the y axis
     * @return true if the mouse is inside this drawable (such as inside a button).
     */
    public boolean isInside(int mouseX, int mouseY, float yTranslation);
}
