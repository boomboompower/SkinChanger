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

package wtf.boomy.mods.skinchanger.utils.gui.faces;

/**
 * An type of {@link ModernUIElement} which can be interacted with in a GUI. (Such as buttons, sliders etc)
 *
 * @author boomboompower
 * @version 1.0
 * @since 3.0.0
 */
public interface InteractiveUIElement extends ModernUIElement {
    
    /**
     * Called when this element is left clicked by the user
     *
     * @param mouseX       the x position of the mouse
     * @param mouseY       the y position of the mouse
     * @param yTranslation the y translation of the screen
     */
    public void onLeftClick(int mouseX, int mouseY, float yTranslation);
    
    /**
     * Called when this element is right clicked by the user
     *
     * @param mouseX       the x position of the mouse
     * @param mouseY       the y position of the mouse
     * @param yTranslation the y translation of the screen
     */
    public default void onRightClick(int mouseX, int mouseY, float yTranslation) {
    }
    
    /**
     * Called when this element is middle clicked by the user
     *
     * @param mouseX       the x position of the mouse
     * @param mouseY       the y position of the mouse
     * @param yTranslation the y translation of the screen
     */
    public default void onMiddleClick(int mouseX, int mouseY, float yTranslation) {
    }
    
    /**
     * Called when the mouse is released off this element
     *
     * @param mouseX       the x position of the mouse
     * @param mouseY       the y position of the mouse
     * @param yTranslation the y translation of the screen
     */
    public default void onMouseReleased(int mouseX, int mouseY, float yTranslation) {
    }
    
    /**
     * Queries the element to determine if the mouse is inside of the element (only useful for
     * {@link InteractiveUIElement}
     *
     * @param mouseX       the raw x location of the mouse
     * @param mouseY       the raw y location of the mouse
     * @param yTranslation the translation in the y axis
     *
     * @return true if the mouse is inside this element (such as inside a button).
     */
    public boolean isInside(int mouseX, int mouseY, float yTranslation);
}
