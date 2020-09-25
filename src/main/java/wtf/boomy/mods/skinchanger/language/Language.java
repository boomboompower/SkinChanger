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

package wtf.boomy.mods.skinchanger.language;

import net.minecraft.client.resources.I18n;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A hook for the inbuilt Locale & I18n classes, letting me smartly translate strings.
 */
public class Language {
    
    private static final Logger logger = LogManager.getLogger("SkinChanger - Language");
    
    /**
     * Formats a string with optional arguments
     *
     * @param key the key to use
     * @param args the arguments to format the key
     * @return a formatted string, or the key on failure.
     */
    public static String format(String key, Object... args) {
        String fixedKey = key.replace(" ", "-");
        fixedKey = fixedKey.toLowerCase();
    
        String translated = I18n.format(fixedKey, args);
        
        if (translated.startsWith("Format error:")) {
            translated = key;
        }
        
        // Debug logging
        if (logger.isTraceEnabled()) {
            logger.trace("Translated " + key  + "(" + fixedKey + ") to " + translated);
        }
        
        return translated;
    }
}
