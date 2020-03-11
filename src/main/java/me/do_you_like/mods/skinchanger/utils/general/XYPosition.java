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

package me.do_you_like.mods.skinchanger.utils.general;

import lombok.Getter;

/**
 * A simple storage class for x & y coordinates.
 * Floating point values are used because they are more precise than regular ints as they take decimals into account.
 */
public class XYPosition {

    @Getter
    private final float x;

    @Getter
    private final float y;

    /**
     * Basic constructor, takes an XYPosition and loads it.
     *
     * @param position the position to steal the values from.
     */
    public XYPosition(XYPosition position) {
        this.x = position.x;
        this.y = position.y;
    }

    /**
     * Constructor for a new XYPosition
     *
     * @param x the x value to be read
     * @param y the y value to be read
     */
    public XYPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * The X value as an int.
     *
     * @return the X value as an int.
     */
    public int getX_Int() {
        return (int) this.x;
    }

    /**
     * The Y value as an int.
     *
     * @return the Y value as an int.
     */
    public int getY_Int() {
        return (int) this.y;
    }

    /**
     * Clones this XYPosition.
     *
     * @return a new XYPosition with the same values.
     */
    public XYPosition copy() {
        // Could use new XYPosition(this), however it's not necessary.

        return new XYPosition(this.x, this.y);
    }
}
