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

package wtf.boomy.mods.skinchanger.api;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

import wtf.boomy.mods.skinchanger.cosmetic.options.SimpleCallback;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Skin API, provides various implementations for skins from player names/UUIDs
 *
 * @author boomboompower
 */
public abstract class SkinAPI {
    
    private static final Pattern uuidPattern = Pattern.compile("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)");
    
    private static final HashMap<String, String> responses = new HashMap<>(); // Store responses
    
    public SkinAPI() { }
    
    /**
     * Gets the id from a userName
     *
     * @param userName the username of the user
     *
     * @return the Mojang ID of the user (stripped UUID)
     */
    public abstract String getIdFromUsername(String userName);
    
    /**
     * Retrieves the username of a player from a UUID
     *
     * @param uuid the uuid of the player
     * @return the uuid, may be null.
     */
    public abstract String getNameFromID(String uuid);
    
    /**
     * Gets the real username of a user from the Mojang API, e.g "NoTcH" will return "notch"
     *
     * @param userName the username of the user
     *
     * @return the real name of the player (as stored by Mojang)
     */
    public abstract String getRealNameFromName(String userName);
    
    /**
     * Returns a ResourceLocation from a uuid, this will return the Steve model if any issues occur
     *
     * @param playerId the id of the user (see {@link #getIdFromUsername(String)})
     * @param callback the {@link ResourceLocation} of the given player
     */
    public abstract void getSkinFromId(String playerId, SimpleCallback<ResourceLocation> callback);
    
    /**
     * Is the textures provided a slim skin?
     * "slim" = ALEX
     * "default" = STEVE
     *
     * @param userId the users id
     *
     * @return true if the texture is of the slim model
     */
    public abstract boolean hasSlimSkin(String userId);
    
    /**
     * Gets the text in the given url, may not always be json
     *
     * @param url the url
     *
     * @return the response, may not always return as json
     */
    protected final String getUrl(String url) {
        if (org.lwjgl.input.Keyboard.isKeyDown(org.lwjgl.input.Keyboard.KEY_L)) {
            responses.clear();
        }
        
        if (responses.containsKey(url)) {
            return responses.get(url);
        }
        
        // Store the response
        String response;
        
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url.replace(" ", "%20")).openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.addRequestProperty("User-Agent", "Mozilla/5.0 (compatible; SkinChanger; @VERSION@) Chrome/83.0.4103.116");
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);
            connection.setDoOutput(true);
            
            response = IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            JsonObject object = new JsonObject();
            object.addProperty("success", false);
            object.addProperty("cause", "Exception");
            object.addProperty("message", e.getMessage() != null ? "" : e.getMessage());
            response = object.toString();
        }
        
        // Stores the response so their api has minimum queries in one session
        responses.put(url, response);
        
        return response;
    }
    
    /**
     * Returns a UUID representation of a UUID which has had its braces removed. Mojang's API
     * removes braces from UUID so this code is designed to unstrip them if possible. If a value
     * being parsed into this method already contains braces then no attempt at conversion will be
     * made. If the input cannot be converted to a valid UUID then null will be returned instead.
     *
     * @param strippedInput the input to be converted to a UUID
     *
     * @return a UUID representation of a String if possible or null
     */
    public UUID getUUIDFromStrippedString(String strippedInput) {
        if (strippedInput == null || strippedInput.trim().isEmpty()) {
            return null;
        }
        
        if (strippedInput.contains("-")) {
            return tryParseUUID(strippedInput);
        }
        
        // https://stackoverflow.com/a/19399768/12697448
        // Pattern#compile improves performance.
        String bracedString = uuidPattern.matcher(strippedInput).replaceFirst("$1-$2-$3-$4-$5");
        
        return tryParseUUID(bracedString);
    }
    
    /**
     * A safe way of parsing a UUID
     *
     * @param input the input to parse, should be in hyphen format
     *
     * @return a UUID if valid or null
     */
    private UUID tryParseUUID(String input) {
        try {
            return UUID.fromString(input);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
