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

package wtf.boomy.mods.skinchanger.utils.gui.impl;

import wtf.boomy.mods.skinchanger.language.Language;
import wtf.boomy.mods.skinchanger.cosmetic.options.SimpleCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ModernLocaleButton extends ModernButton {
    
    private final String key;
    
    private String defaultValue;
    
    public ModernLocaleButton(int buttonId, int x, int y, String key, String value) {
        super(buttonId, x, y, Language.format("skinchanger.options." + key, value));
    
        this.key = key;
    }
    
    public ModernLocaleButton(int buttonId, String idName, int x, int y, String key) {
        super(buttonId, idName, x, y, Language.format("skinchanger.options." + key));
    
        this.key = key;
    }
    
    public ModernLocaleButton(int buttonId, String idName, int x, int y, String key, String value) {
        super(buttonId, idName, x, y, Language.format("skinchanger.options." + key, value));
    
        this.key = key;
    }
    
    public ModernLocaleButton(int buttonId, int x, int y, int widthIn, int heightIn, String key) {
        super(buttonId, x, y, widthIn, heightIn, Language.format("skinchanger.options." + key));
    
        this.key = key;
    }
    
    public ModernLocaleButton(int buttonId, int x, int y, int widthIn, int heightIn, String key, String value) {
        super(buttonId, x, y, widthIn, heightIn, Language.format("skinchanger.options." + key, value));
    
        this.key = key;
    }
    
    public ModernLocaleButton(int buttonId, int x, int y, int widthIn, int heightIn, String key, SimpleCallback<ModernLocaleButton> clicked) {
        super(buttonId, x, y, widthIn, heightIn, Language.format("skinchanger.options." + key), clicked);
    
        this.key = key;
    }
    
    public ModernLocaleButton(int buttonId, int x, int y, int widthIn, int heightIn, String key, String value, SimpleCallback<ModernLocaleButton> clicked) {
        super(buttonId, x, y, widthIn, heightIn, Language.format("skinchanger.options." + key, value), clicked);
        
        this.key = key;
    }
    
    public void updateValue(String valueIn) {
        setText(Language.format("skinchanger.options." + this.key, valueIn));
    }
    
    public ModernLocaleButton setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        
        return this;
    }
    
    public String getDefaultValue() {
        return defaultValue;
    }
    
    public String getKey() {
        return key;
    }
    
    public ModernLocaleButton interpretLoreKey(String key, String defaultVal) {
        key = key.toLowerCase();
        
        String s = Language.format("skinchanger.options." + key + ".description");
        
        // Not translated
        if (s.equals(key)) {
            return this;
        }
        
        List<String> lines;
        
        if (s.contains("|")) {
            lines = Arrays.asList(s.split("\\|"));
        } else {
            lines = Collections.singletonList(s);
        }
        
        // Makes it modifiable.
        lines = new ArrayList<>(lines);
        
        lines.add(" ");
        lines.add(Language.format("skinchanger.phrase.default", defaultVal));
        
        // Set the lore to our translation
        setMessageLines(lines);
        
        return this;
    }
}
