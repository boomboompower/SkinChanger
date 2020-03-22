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

/**
 * Interface for a basic ModernDrawable item. Contains universal methods which **ALL** drawables
 * should implement. This is used internally such as in the {@link ModernGui class}
 *
 * @author boomboompower
 * @version 1.0
 * @since 3.0.0
 */
public interface ModernDrawable {

    /**
     * Returns the x position of this drawable
     *
     * @return the x position of the drawable.
     */
    public int getX();

    /**
     * Returns the y location of this drawable (not translated)
     *
     * @return the y position of the drawable.
     */
    public int getY();

    /**
     * Returns the width of this drawable.
     *
     * @return the width of the drawable
     */
    public int getWidth();

    /**
     * Calls the render function for the drawable.
     *
     * @param mouseX the current x position of the mouse.
     * @param mouseY the current y position of the mouse.
     */
    public void render(int mouseX, int mouseY, float yTranslation);

    /**
     * Renders the drawable from a header position. By default this will just call the {@link
     * #render(int, int, float)} method, however some {@link ModernDrawable}'s will react differently
     * to this change.
     *
     * @param xPos the x position of the drawable
     * @param yPos the y position of the drawable
     * @param yTranslation the translation in the y axis
     * @param mouseX the raw x location of the mouse
     * @param mouseY the raw y location of the mouse
     * @param recommendedYOffset the recommended offset this {@link ModernDrawable} should follow (how
     *     far down it should be shifted).
     */
    public default void renderFromHeader(int xPos, int yPos, float yTranslation, int mouseX, int mouseY, int recommendedYOffset) {
        render(mouseX, mouseY, yTranslation);
    }

    /**
     * Should this drawable be drawn? If this is false the header will not call the {@link
     * #render(int, int, float)} method.
     *
     * @return true if {@link #render(int, int, float)} should be called for the drawable.
     */
    public boolean isEnabled();

    /**
     * Tells this drawable to register itself as part of this header. Some drawables will react
     * differently to this change
     *
     * @param parent the header which the drawable should be set under.
     * @return the drawable which has just been set as part of this header
     */
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
