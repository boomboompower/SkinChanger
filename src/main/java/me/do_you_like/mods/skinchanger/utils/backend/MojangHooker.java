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

package me.do_you_like.mods.skinchanger.utils.backend;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;

import java.util.UUID;
import java.util.regex.Pattern;

import me.do_you_like.mods.skinchanger.SkinChangerMod;
import me.do_you_like.mods.skinchanger.utils.general.BetterJsonObject;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;

import org.apache.commons.io.IOUtils;

/**
 * A simple hooker for skin & cape components from the Mojang API.
 *
 * @author boomboompower
 * @since 3.0.0
 * @version 1.0
 */
public class MojangHooker {
    
    // Prevents unknown urls loading skins
    // This is to stop
    private static final String[] TRUSTED_DOMAINS = {
        ".minecraft.net",
        ".mojang.com"
    };

    private static final Pattern uuidPattern = Pattern.compile("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)");
    
    private static final HashMap<String, String[]> idCaches = new HashMap<>(); // Store the id
    
    private static final HashMap<String, String> responses = new HashMap<>(); // Store responses
    
    private static final HashMap<String, Boolean> slimSkins = new HashMap<>(); // Store skin checks
    
    private static final HashMap<String, BetterJsonObject> idEncryptedTextures = new HashMap<>(); // Store texture array
    
    private static final HashMap<String, ResourceLocation> skins = new HashMap<>(); // Store skin locations
    
    public MojangHooker() {
        // Initalize nice things
    }
    
    /**
     * Gets the id from the players name
     *
     * @param nameIn the name
     * @return the id
     */
    public String getIdFromUsername(String nameIn) {
        if (nameIn == null) {
            return null;
        }

        if (idCaches.containsKey(nameIn)) {
            return idCaches.get(nameIn)[0];
        }
        
        if (nameIn.isEmpty()) {
            idCaches.put("", new String[] {"", ""});

            return "";
        }
    
        BetterJsonObject profile = getProfileFromUsername(nameIn);
    
        if (profile.has("success") && !profile.get("success").getAsBoolean()) {
            idCaches.put(nameIn.toLowerCase(), new String[] {"", ""});

            return "";
        }
    
        if (profile.has("id")) {
            idCaches.put(nameIn.toLowerCase(), new String[] { profile.get("id").getAsString(), profile.get("name").getAsString() });

            return profile.get("id").getAsString();
        }

        idCaches.put(nameIn, new String[] {"", ""});

        return "";
    }

    /**
     * Gets the real name from an input
     *
     * @param nameIn the name
     * @return the real name of the player
     */
    public String getRealNameFromName(String nameIn) {
        if (nameIn == null) {
            return null;
        }

        if (idCaches.containsKey(nameIn)) {
            return idCaches.get(nameIn)[1];
        }

        getIdFromUsername(nameIn);

        if (idCaches.get(nameIn) == null) {
            return null;
        }

        return idCaches.get(nameIn)[1];
    }

    /**
     * Gets the Textures of a player from a username
     *
     * @param name the players name
     * @return null if an error occured or no id is found in the response
     */
    public BetterJsonObject getTexturesFromName(String name) {
        if (name == null || name.isEmpty()) {
            return new BetterJsonObject();
        }
    
        return getTexturesFromId(getIdFromUsername(name));
    }
    
    /**
     * Gets the Textures of a user from an id
     *
     * @param id the id to be used, supports {@link java.util.UUID#toString()}
     * @return the textures JsonObject or an empty JsonObject if an error occurs
     */
    public BetterJsonObject getTexturesFromId(String id) {
        if (id == null || id.isEmpty()) {
            return new BetterJsonObject();
        }
        
        id = id.replace("-", ""); // Remove dashes
        
        return new BetterJsonObject(getUrl("https://sessionserver.mojang.com/session/minecraft/profile/" + id));
    }

    /**
     * Returns a UUID representation of a UUID which has had its braces removed. Mojang's API
     * removes braces from UUID so this code is designed to unstrip them if possible. If a value
     * being parsed into this method already contains braces then no attempt at conversion will be
     * made. If the input cannot be converted to a valid UUID then null will be returned instead.
     *
     * @param strippedInput the input to be converted to a UUID
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
     * Gets the users profile from a username
     *
     * @param name the players name
     * @return their profile
     */
    public BetterJsonObject getProfileFromUsername(String name) {
        if (name == null || name.isEmpty()) {
            return new BetterJsonObject();
        }
        
        return new BetterJsonObject(getUrl("https://api.mojang.com/users/profiles/minecraft/" + name));
    }
    
    private BetterJsonObject getEncryptedTexturesUnsafe(String id) throws IllegalStateException, JsonParseException {
        if (id == null) {
            return new BetterJsonObject();
        }
        
        if (idEncryptedTextures.containsKey(id)) {
            return idEncryptedTextures.get(id);
        }
        
        BetterJsonObject texturesIn = getTexturesFromId(id);
    
        if (!texturesIn.has("properties") || !texturesIn.get("properties").isJsonArray()) {
            return new BetterJsonObject();
        }
    
        // Properties is a JsonArray
        JsonArray propertyArray = texturesIn.get("properties").getAsJsonArray();
    
        for (JsonElement propertyElement : propertyArray) {
            // This shouldn't actually happen at the time of making this,
            // This has just been added for if they add anything to the api
            if (!propertyElement.isJsonObject()) {
                continue;
            }
        
            // Grab the JsonObject version of the property
            JsonObject property = propertyElement.getAsJsonObject();
        
            // Found the textures property!
            if (property.has("name") && property.get("name").getAsString().equals("textures") && property.has("value")) {
                // We need to decode the Base64 value property
                byte[] decoded = Base64.getDecoder().decode(property.get("value").getAsString());

                JsonObject decodedObj = JsonParser.parseString(new String(decoded, StandardCharsets.UTF_8)).getAsJsonObject();
            
                // We have a match!
                if (decodedObj.has("textures") && decodedObj.has("profileId") && decodedObj.get("profileId").getAsString().equals(texturesIn.get("id").getAsString())) {
                    idEncryptedTextures.put(id, new BetterJsonObject(decodedObj.get("textures").getAsJsonObject()));

                    return idEncryptedTextures.get(id);
                }
            }
        }

        idEncryptedTextures.put(id, new BetterJsonObject());

        return idEncryptedTextures.get(id);
    }

    /**
     * Silent version of {@link #hasSlimSkinUnsafe(String)}
     *
     * @param id the users id
     * @return true if the texture is of the slim model
     */
    public boolean hasSlimSkin(String id) {
        try {
            return hasSlimSkinUnsafe(id);
        } catch (NullPointerException | JsonParseException | IllegalStateException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Is the textures provided a slim skin?
     * "slim" = ALEX
     * "default" = STEVE
     *
     * @param id the users id
     * @return true if the texture is of the slim model
     */
    private boolean hasSlimSkinUnsafe(String id) throws NullPointerException, IllegalStateException, JsonParseException {
        if (id == null) {
            return false;
        }

        if (slimSkins.containsKey(id)) {
            return slimSkins.get(id);
        }

        JsonObject realTextures = getEncryptedTexturesUnsafe(id).getData();

        // Should never happen
        if (!realTextures.has("SKIN")) {
            slimSkins.put(id, false);

            return false;
        }

        JsonObject skinData = realTextures.get("SKIN").getAsJsonObject();

        if (skinData.has("metadata")) {
            JsonObject metaData = skinData.get("metadata").getAsJsonObject();

            slimSkins.put(id, metaData.has("model"));

            return metaData.has("model") && metaData.get("model").getAsString().equals("slim");
        }

        slimSkins.put(id, false);

        return false;
    }

    /**
     * The safe version of skin loading, this will return the Steve model if any issues occur
     *
     * @param id the id of the user (see {@link #getIdFromUsername(String)})
     * @return the {@link ResourceLocation} of the given id
     */
    public ResourceLocation getSkinFromId(String id) {
        try {
            return getSkinFromIdUnsafe(id);
        } catch (IllegalArgumentException ex) {
            if (ex.getMessage() != null) {
                System.err.println(ex.getMessage());
            }

            return DefaultPlayerSkin.getDefaultSkinLegacy();
        }
    }

    /**
     * The unsafe version of skin loading & caching
     *
     * @param id the id of the profile, to be used in decryption
     * @return the {@link ResourceLocation} of the given id
     */
    private ResourceLocation getSkinFromIdUnsafe(String id) {
        if (id != null && !id.isEmpty()) {
            if (skins.containsKey(id)) {
                ResourceLocation loc = skins.get(id);

                // Test if the resource is still loaded
                if (Minecraft.getMinecraft().getTextureManager().getTexture(loc) != null) {
                    return loc;
                } else {
                    skins.remove(id);
                }
            }

            JsonObject realTextures = getEncryptedTexturesUnsafe(id).getData();

            // Should never happen
            if (!realTextures.has("SKIN")) {
                skins.put(id, DefaultPlayerSkin.getDefaultSkinLegacy());

                return DefaultPlayerSkin.getDefaultSkinLegacy();
            }

            JsonObject skinData = realTextures.get("SKIN").getAsJsonObject();

            if (!skinData.has("url")) {
                skins.put(id, DefaultPlayerSkin.getDefaultSkinLegacy());

                return DefaultPlayerSkin.getDefaultSkinLegacy();
            }

            String url = skinData.get("url").getAsString();

            // Ensures minecraft servers are not hacked :)
            if (!isTrustedDomain(url)) {
                // Spoofed payload? Does not use an official Mojang network...
                throw new IllegalArgumentException("Invalid payload, the domain issued was not trusted.");
            }

            ResourceLocation playerSkin = SkinChangerMod.getInstance().getCacheRetriever().loadIntoGame(id, url, null);

            skins.put(id, playerSkin);

            return playerSkin;
        } else {
            return DefaultPlayerSkin.getDefaultSkinLegacy();
        }
    }
    
    /**
     * Gets the text in the given url, may not always be json
     *
     * @param url the url
     * @return the response, may not always return as json
     */
    private String getUrl(String url) {
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
            connection.addRequestProperty("User-Agent", "Mozilla/4.76");
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);
            connection.setDoOutput(true);
            
            response = IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            JsonObject object = new JsonObject();
            object.addProperty("success", false);
            object.addProperty("cause", "Exception");
            response = object.toString();
        }
        
        // Stores the response so their api has minimum queries in one session
        responses.put(url, response);
        
        return response;
    }
    
    /**
     * Grabs the last segment of the domain
     *
     * @param url the domain
     * @return the segment or the url if no spitter is found
     */
    private String getLastSegment(String url) {
        if (url == null) {
            return null;
        }
        
        if (url.contains("/")) {
            // The final part of the domain
            String[] last = url.split("/");
            
            return last[last.length - 1];
        }
        
        return url;
    }
    
    /**
     * Attempts to substring by the given amounts, without errors
     *
     * @param in the string to substring
     * @param first the postion of the start
     * @param last the postion of the end
     * @return the substring version of the string, or the input if an error occurs
     */
    private String attemptSubstring(String in, int first, int last) {
        try {
            return in.substring(first, last);
        } catch (IndexOutOfBoundsException ex) {
            // Oops...
            return in;
        }
    }
    
    /**
     * Is the domain tested part of our whitelist?
     *
     * @param url the url to test
     * @return true if the url is trusted
     * @throws IllegalArgumentException if the url is not valid
     */
    private boolean isTrustedDomain(String url) {
        if (url == null) {
            return false;
        }
        
        URI uri;
        
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL '" + url + "'");
        }

        String host = uri.getHost();
    
        for (String domain : TRUSTED_DOMAINS) {
            if (host.endsWith(domain)) {
                return true;
            }
        }
        return false;
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
