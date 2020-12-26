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

package wtf.boomy.mods.skinchanger.locale;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import wtf.boomy.mods.skinchanger.utils.ambiguous.ThreadFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A custom translation system based on JSON files instead of .lang (property files)
 * <br>
 * Each key in the Json file is converted into a string representation. Consider the following json
 * <br>
 * <div>
 *     <code>{
 *        "hello": {
 *            "world": "hey there!"
 *        }
 *    }</code>
 * </div>
 * <br>
 * This will store the key "hello.world" in and replace it with "hey there!" when parsed.
 * <br>
 *     <br>
 * This parser also supports Arrays in JSON, however they are stored
 * in a list instead of a simple string, and cannot be formatted unlike the single-line strings.
 * <br>
 * Consider the following mixed JSON
 * <div>
 * <code>{
 *     "hello": {
 *         "world": "hey there!",
 *         "foo": [
 *              "bar"
 *         ]
 *     }
 * }</code>
 * </div>
 * <br>
 * This will still assign the "hello.world" key to "hey there!", however it will assign
 * "hello.foo" to an unmodifiable String List ({@code List<String>} containing "bar".
 *
 * @author boomboompower
 * @version 1.0
 * @apiNote Remember to call {@link #loadTranslations()} before use or nothing will be translated.
 */
public final class Language {
    
    private static final ThreadFactory threadFactory = new ThreadFactory("SkinChanger - Language");
    private static final Logger logger = LogManager.getLogger("SkinChanger - Language");
    
    // Have things been loaded yet? If this is false nothing will be translated.
    private static boolean translationsLoaded = false;
    
    private static final Map<String, String> oneToOneTranslations = new HashMap<>();
    private static final Map<String, List<String>> multiLineTranslations = new HashMap<>();
    
    // No sub-classes
    private Language() { }
    
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
        
        String translated = internalTranslate(fixedKey, args);
        
        // Debug logging
        if (logger.isTraceEnabled()) {
            logger.trace("Translated " + key  + "(" + fixedKey + ") to " + translated);
        }
        
        if (logger.isWarnEnabled() && translated.equals(fixedKey)) {
            logger.warn("An untranslated key was found: " + fixedKey);
        }
        
        return translated;
    }
    
    /**
     * Values in the JSON which are arrays are stored separately to other primatives.
     *
     * This is useful for lore entries, however the downside is this cannot be formatted.
     *
     * @param key the key of the object
     * @return an array of strings or an array containing the key if none exists
     */
    public static List<String> getMultiLine(String key) {
        if (!translationsLoaded) {
            logger.error("Translations have not been loaded. Please call Language#loadTranslations() at init!");
            
            return Collections.singletonList(key);
        }
        
        // Nice and neat
        return multiLineTranslations.getOrDefault(key, Collections.singletonList(key));
    }
    
    private static String internalTranslate(String key, Object... args) {
        if (!translationsLoaded) {
            logger.error("Translations have not been loaded. Please call Language#loadTranslations() at init!");
            
            return key;
        }
        
        String form = oneToOneTranslations.getOrDefault(key, key);
        
        if (args != null && args.length > 0) {
            try {
                form = String.format(form, args);
            } catch (IllegalFormatException ignored) {
            }
        }
        
        return form;
    }
    
    /**
     * Call me when the mod starts. This will initialize the translation system.
     */
    public static void loadTranslations() {
        if (translationsLoaded) return;
        
        translationsLoaded = true;
        
        threadFactory.runAsync(() -> {
            InputStream stream = Language.class.getResourceAsStream("/lingo/en.json");
            
            if (stream == null) {
                return;
            }
            
            InputStreamReader reader = null;
            JsonElement parsed;
            try {
                reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
                parsed = new JsonParser().parse(reader);
                
                // Parses the parsed JSON. Is this inception?
                interpretIncomingJson("", parsed);
                
                reader.close();
                stream.close();
            } catch (JsonParseException | IOException ex) {
                logger.error("An error occurred while parsing the language file", ex);
            } finally {
                // Basically if an error occurs and the
                // reader doesn't get a chance to close
                // we'll make sure it's closed to prevent
                // a memory leak.
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException ex) {
                        logger.error("Failed to close input reader ", ex);
                    }
                }
                
                // Same as above. If the code terminates we want to make sure we've
                // actually closed our streams to free up the memory we aren't using.
                try {
                    stream.close();
                } catch (IOException ex) {
                    logger.error("Failed to close language input stream: ", ex);
                }
            }
            
            // Close it down. We only need it when initializing the translation system
            // without freezing and potentially crashing the game if a failure occurs.
            threadFactory.destroy();
        });
    }
    
    /**
     * Recursively parses all inputs in a JSON file and adds them to the
     *
     * @param pathIn the path to the element. When calling this method parse an empty string.
     * @param element the element which will be searched. If the element is a primative, its string
     *                representation will be returned. If the element is a JsonArray its Array as a
     *                String list {@code List<String>} will be returned. If it's a JsonObject it
     *                will also be recursively searched for values.
     *
     * @return a {@code String} for a JsonPrimative or a {@code List<String>} for a JsonArray or null when done.
     */
    private static Object interpretIncomingJson(String pathIn, JsonElement element) {
        if (element.isJsonArray()) {
            List<String> lines = new LinkedList<>();
            
            // Convert each line in the JsonArray to a string
            // and add it to the Array
            for (JsonElement child : element.getAsJsonArray()) {
                lines.add(child.getAsString());
            }
            
            // Makes the list unmodifiable
            return Collections.unmodifiableList(lines);
        } else if (element.isJsonNull()) {
            // Peek a boo! This shouldn't
            // really happen but for consistency
            // we'll have a catch for this.
            return "NULL";
        } else if (element.isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
                String chosenPath = pathIn + (pathIn.isEmpty() ? "" : ".") + entry.getKey();
                
                // Either null, a String or a List
                Object value = interpretIncomingJson(chosenPath, entry.getValue());
                
                // No path was found
                if (value == null) {
                    continue;
                }
                
                if (value instanceof String) {
                    registerKeyToValue(chosenPath, (String) value);
                } else if (value instanceof List) {
                    registerArrayKey(chosenPath, (List<String>) value);
                } else {
                    logger.error("Unrecognized interpreted language value " + value);
                }
            }
            
            return null;
        } else {
            return element.getAsString();
        }
    }
    
    /**
     * Registers a single-line value from the language file to a specific key.
     *
     * Does nothing if the key has already been registered to.
     *
     * @param path the path to the key (a path for the JsonObject's separated by '.')
     * @param value the value of the key as a simple string.
     */
    private static void registerKeyToValue(String path, String value) {
        if (oneToOneTranslations.containsKey(path)) return;
        
        oneToOneTranslations.put(path, value);
    }
    
    /**
     * Registers a multi-line value in the language file to a key.
     *
     * @param path the path to the key (a list of JsonObjects separated by .)
     * @param value the value of the key as an array
     */
    private static void registerArrayKey(String path, List<String> value) {
        if (oneToOneTranslations.containsKey(path)) return;
        
        multiLineTranslations.put(path, value);
    }
}
