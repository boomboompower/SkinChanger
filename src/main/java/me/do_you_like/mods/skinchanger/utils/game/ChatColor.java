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

package me.do_you_like.mods.skinchanger.utils.game;

import java.util.regex.Pattern;

public enum ChatColor {
    
    BLACK('0'),
    DARK_BLUE('1'),
    DARK_GREEN('2'),
    DARK_AQUA('3'),
    DARK_RED('4'),
    DARK_PURPLE('5'),
    GOLD('6'),
    GRAY('7'),
    DARK_GRAY('8'),
    BLUE('9'),
    GREEN('a'),
    AQUA('b'),
    RED('c'),
    LIGHT_PURPLE('d'),
    YELLOW('e'),
    WHITE('f'),
    MAGIC('k', true),
    BOLD('l', true),
    STRIKETHROUGH('m', true),
    UNDERLINE('n', true),
    ITALIC('o', true),
    RESET('r');

    private static final char COLOR_CHAR = '\u00A7';
    private static final String colorString = "0123456789AaBbCcDdEeFfKkLlMmNnOoRr";
    private static final Pattern colorPattern = Pattern.compile("(?i)" + COLOR_CHAR + "[0-9A-FK-OR]");

    private final char code;
    private final boolean isFormat;
    private final String toString;

    ChatColor(char code) {
        this(code, false);
    }

    ChatColor(char code, boolean isFormat) {
        this.code = code;
        this.isFormat = isFormat;
        this.toString = new String(new char[] {COLOR_CHAR, code});
    }

    public char getChar() {
        return code;
    }

    /**
     * This is part of the magic
     *
     * @return the string version of this ChatColor
     */
    @Override
    public String toString() {
        return this.toString;
    }

    /**
     * Returns true if this ChatColor is actually a formatting code
     *
     * @return true if this ChatColor is a formatting code.
     */
    public boolean isFormat() {
        return this.isFormat;
    }

    /**
     * Returns true if this ChatColor changes the color of the chat (not formatting)
     *
     * @return true if this ChatColor is a color
     */
    public boolean isColor() {
        return !this.isFormat && this != RESET;
    }

    /**
     * Removes all colors from a string
     *
     * @param input the input to strip
     *
     * @return a ChatColor stripped string
     */
    public static String stripColor(final String input) {
        if (input == null) {
            return null;
        }

        return colorPattern.matcher(input).replaceAll("");
    }

    /**
     * Replaces all '&' characters with Minecraft's formatting code symbol
     *
     * @param textToTranslate the text which should be altered
     * @return the altered text
     */
    public static String translateAlternateColorCodes(String textToTranslate) {
        return translateAlternateColorCodes('&', textToTranslate);
    }

    /**
     * Replaces all "altColorChar" references with the ChatColor code if the following character
     * is a valid {@link ChatColor} entry.
     *
     * @param altColorChar the character which should be used as a replacement for Minecraft's formatting code.
     * @param textToTranslate the text which should be altered and colored.
     * @return the altered text with all valid ChatColor codes added.
     */
    public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
        char[] chars = textToTranslate.toCharArray();

        for (int i = 0; i < chars.length - 1; i++) {
            // If the character at this position is the COLOR_CHAR and the following character is a valid ChatColor
            // Then we should replace the character at this position to the color char and make the following
            // character lowercase (so it works across some other mc versions).
            if (chars[i] == altColorChar && colorString.indexOf(chars[i + 1]) > -1) {
                chars[i] = ChatColor.COLOR_CHAR;
                chars[i + 1] = Character.toLowerCase(chars[i + 1]);
            }
        }

        // Rebuilds the string from the chars
        return new String(chars);
    }

    /**
     * Removes all formatting codes from the message by formatting it for color codes
     * then stripping the formatted message of all codes. Useful for getting a literal string.
     *
     * @param altColorChat the code which should be translated to the color symbol
     * @param message the message to format then unformat.
     * @return a completely stripped string.
     */
    public static String formatUnformat(char altColorChat, String message) {
        return stripColor(translateAlternateColorCodes(altColorChat, message));
    }
}