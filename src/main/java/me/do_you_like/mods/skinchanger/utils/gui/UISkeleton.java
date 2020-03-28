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

import me.do_you_like.mods.skinchanger.utils.gui.impl.ModernButton;

/**
 * Provides the base methods for a Gui, these methods are designed to be applicable to multiple Minecraft versions.
 * <br>
 * It should be noted that all render methods are ran in sandboxed environments meaning if an error occurs, the game will not crash.
 *
 * @author boomboompower
 * @version 1.0
 * @since 3.0.0
 */
public interface UISkeleton {

    /**
     * Called when the GUI is first opened. Equivalent to initGui
     */
    public void onGuiOpen();

    /**
     * Called when the GUI is closed.
     */
    public default void onGuiClose() {
    }

    /**
     * Called just before the render method is called. Should not be used for rendering. Use for logic methods.
     * <br />
     * This method is sandboxed.
     *
     * @param mouseX the x position of the mouse
     * @param mouseY the y position of the mouse
     */
    public default void preRender(int mouseX, int mouseY) {
    }

    /**
     * Called during the render method. Use this for rendering.
     * <br />
     * This method is sandboxed.
     *
     * @param mouseX the x position of the mouse
     * @param mouseY the y position of the mouse
     * @param partialTicks from timer - how much time has elapsed since the last tick, in ticks, for
     *     use by display rendering routines (range: 0.0 - * 1.0). This field is frozen if the display
     *     is paused to eliminate jitter.
     */
    public void onRender(int mouseX, int mouseY, float partialTicks);

    /**
     * Called after the render method. Use this for any post-processing / post-render logic.
     * <br />
     * This method is sandboxed.
     */
    public default void postRender() {
    }

    /**
     * Called when a {@link ModernButton} is left clicked.
     *
     * @param button the button that was left clicked.
     */
    public default void buttonPressed(ModernButton button) {
    }

    /**
     * Called when a {@link ModernButton} is right clicked.
     *
     * @param button the button that was right clicked.
     */
    public default void rightClicked(ModernButton button) {
    }

    /**
     * Called when the user scrolls up
     */
    public default void onScrollUp() {
    }

    /**
     * Called when the user scrolls down
     */
    public default void onScrollDown() {
    }

    /**
     * Called when a character is typed. Mimics the GuiScreen keyTyped functionality.
     *
     * @param keyCode the keyCode of the character. See {@link org.lwjgl.input.Keyboard} to get codes.
     * @param keyCharacter the character representation of the key. Example: if {@link
     *     org.lwjgl.input.Keyboard#KEY_A} was pressed * then 'a' would be the character that is
     *     returned.
     */
    public default void onKeyTyped(int keyCode, char keyCharacter) {}
}
