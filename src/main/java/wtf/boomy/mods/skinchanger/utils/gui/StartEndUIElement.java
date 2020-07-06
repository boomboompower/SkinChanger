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

package wtf.boomy.mods.skinchanger.utils.gui;

/**
 * An element which has a predefined rectangular area.
 * <p>
 * Generally used in tandem with {@link InteractiveUIElement}
 */
public interface StartEndUIElement {
    
    /**
     * Returns the x-plane position of the coordinate in the top left of the rectangle
     *
     * @return the x value of the element
     */
    public int getX();
    
    /**
     * Returns the y-plane position of the coordinate in the top left of the rectangle
     *
     * @return the y value of the element
     */
    public int getY();
    
    /**
     * Returns the distance from the initial x-plane position to the final x-plane position
     *
     * @return the width of the element
     */
    public int getWidth();
    
    /**
     * Returns the distance from the initial y-plane position to the final y-plane position
     *
     * @return the height of the element
     */
    public int getHeight();
}
