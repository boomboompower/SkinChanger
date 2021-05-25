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
package wtf.boomy.mods.skinchanger.utils.uis.components;

import wtf.boomy.mods.modernui.threads.SimpleCallback;
import wtf.boomy.mods.modernui.uis.components.ButtonComponent;
import wtf.boomy.mods.skinchanger.locale.Language;

import java.util.ArrayList;
import java.util.List;

/**
 * A type of ModernButton which supports translations.
 * <br>
 * <br>
 * An implementation of the {@link ButtonComponent} designed to adhere to the specifications in the Translation system.
 */
public class LocaleButtonComponent extends ButtonComponent {
    
    private final String title;
    private final String key;
    
    private String defaultValue;
    
    /**
     * A basic constructor for a LocaleButton. With no callback (for legacy reasons)
     *
     * @param buttonId the id of the button (deprecated). Use callbacks.
     * @param x the x position of the button
     * @param y the y position of the button
     * @param widthIn the width of the button
     * @param heightIn the height of the button
     * @param title the title of the button (to remember what the translation is for)
     * @param key the translation key for the button.
     */
    public LocaleButtonComponent(int buttonId, int x, int y, int widthIn, int heightIn, String title, String key) {
        super(buttonId, x, y, widthIn, heightIn, Language.format(key));
        
        this.title = title;
        this.key = key;
    }
    
    /**
     * A basic constructor for a LocaleButton
     *
     * @param buttonId the id of the button (deprecated). Use callbacks.
     * @param x the x position of the button
     * @param y the y position of the button
     * @param widthIn the width of the button
     * @param heightIn the height of the button
     * @param title the title of the button (to remember what the translation is for)
     * @param key the translation key for the button.
     * @param clicked the event which is called when the button is clicked (supersedes buttonId)
     */
    public LocaleButtonComponent(int buttonId, int x, int y, int widthIn, int heightIn, String title, String key, SimpleCallback<LocaleButtonComponent> clicked) {
        super(buttonId, x, y, widthIn, heightIn, Language.format(key), clicked);
        
        this.title = title;
        this.key = key;
    }
    
    
    /**
     * A basic constructor for a LocaleButton
     *
     * @param buttonId the id of the button (deprecated). Use callbacks.
     * @param x the x position of the button
     * @param y the y position of the button
     * @param widthIn the width of the button
     * @param heightIn the height of the button
     * @param title the title of the button (to remember what the translation is for)
     * @param key the translation key for the button.
     * @param value the value of the button (formatted)
     * @param clicked the event which is called when the button is clicked (supersedes buttonId)
     */
    public LocaleButtonComponent(int buttonId, int x, int y, int widthIn, int heightIn, String title, String key, String value, SimpleCallback<LocaleButtonComponent> clicked) {
        super(buttonId, x, y, widthIn, heightIn, Language.format(key, value), clicked);
        
        this.title = title;
        this.key = key;
    }
    
    /**
     * Locates and attempts to use the lore based on the key
     *
     * @param key the key for the lore.
     * @param defaultVal the default value of the lore, appended to the end of the lore.
     *
     * @return this instance of the button.
     */
    public LocaleButtonComponent interpretLoreKey(String key, String defaultVal) {
        List<String> lines = new ArrayList<>(Language.getMultiLine(key));
        
        lines.add(" ");
        lines.add(Language.format("skinchanger.phrase.default", defaultVal));
        
        // Set the lore to our translation
        setMessageLines(lines);
        
        return this;
    }
    
    /**
     * Updates the title of the button with a value
     *
     * @param valueIn the value of the button
     */
    public void updateTitleWithValue(String valueIn) {
        String translatedString = Language.format(this.key, valueIn);
        
        // Fallback for untranslated keys.
        if (translatedString.equalsIgnoreCase(this.key)) {
            translatedString = this.title + "*: " + valueIn;
        }
        
        setText(translatedString);
    }
    
    public LocaleButtonComponent setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        
        return this;
    }
    
    @Override
    public LocaleButtonComponent setDrawingModern(boolean drawingModern) {
        return (LocaleButtonComponent) super.setDrawingModern(drawingModern);
    }
    
    /**
     * Returns the default value of the button
     *
     * @return a nullable string for the default value
     */
    public String getDefaultValue() {
        return this.defaultValue;
    }
    
    /**
     * Returns the translation key for the button
     *
     * @return a non-null translation key for the button
     */
    public String getKey() {
        return this.key;
    }
}
