package me.boomboompower.skinchanger.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;

import org.apache.commons.io.IOUtils;

/**
 * A big boy api designed to interact with the mojang api, created for SkinChanger
 *
 * @author boomboompower
 * @version 1.0
 */
public class MojangHooker {
    
    // Prevents unknown urls loading skins
    private static final String[] TRUSTED_DOMAINS = {
        ".minecraft.net",
        ".mojang.com"
    };
    
    private static final HashMap<String, String> idCaches = new HashMap<>(); // Store the id
    
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
        
        if (nameIn.isEmpty()) {
            return idCaches.put(nameIn, "");
        }
    
        BetterJsonObject profile = getProfileFromUsername(nameIn);
    
        if (profile.has("success") && !profile.get("success").getAsBoolean()) {
            return idCaches.put(nameIn, "");
        }
    
        if (profile.has("id")) {
            return idCaches.put(nameIn, profile.get("id").getAsString());
        }
        return idCaches.put(nameIn, "");
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
    
    private BetterJsonObject getEncryptedTexturesUnsafe(String id) throws UnsupportedEncodingException, IllegalStateException, JsonParseException {
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
                JsonObject decodedObj = new JsonParser().parse(new String(decoded, "UTF-8")).getAsJsonObject();
            
                // We have a match!
                if (decodedObj.has("textures") && decodedObj.has("profileId") && decodedObj.get("profileId").getAsString().equals(texturesIn.get("id").getAsString())) {
                    return idEncryptedTextures.put(id, new BetterJsonObject(decodedObj.get("textures").getAsJsonObject()));
                }
            }
        }
        return idEncryptedTextures.put(id, new BetterJsonObject());
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
        } catch (NullPointerException | JsonParseException | IllegalStateException | UnsupportedEncodingException e) {
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
    private boolean hasSlimSkinUnsafe(String id) throws NullPointerException, UnsupportedEncodingException, IllegalStateException, JsonParseException {
        if (id == null) {
            return false;
        }
        
        if (slimSkins.containsKey(id)) {
            return slimSkins.get(id);
        }
    
        JsonObject realTextures = getEncryptedTexturesUnsafe(id).getData();
    
        // Should never happen
        if (!realTextures.has("SKIN")) {
            return slimSkins.put(id, false);
        }
    
        JsonObject skinData = realTextures.get("SKIN").getAsJsonObject();
    
        if (skinData.has("metadata")) {
            JsonObject metaData = skinData.get("metadata").getAsJsonObject();
        
            return slimSkins.put(id, metaData.has("model") && metaData.get("model").getAsString().equals("slim"));
        }
        
        return slimSkins.put(id, false);
    }
    
    /**
     * The safe version of skin loading, this will return the Steve model if any issues occur
     *
     * @param id the id of the user
     * @return the {@link net.minecraft.util.ResourceLocation} of the given id
     */
    public ResourceLocation getSkinFromId(String id) {
        try {
            return getSkinFromIdUnsafe(id);
        } catch (UnsupportedEncodingException ex) {
            return DefaultPlayerSkin.getDefaultSkinLegacy();
        }
    }
    
    /**
     * The unsafe version of skin loading & caching
     *
     * @param id the id of the profile, to be used in decryption
     * @return the {@link net.minecraft.util.ResourceLocation} of the given id
     *
     * @throws UnsupportedEncodingException if the encrypted resource cannot be decrypted
     */
    private ResourceLocation getSkinFromIdUnsafe(String id) throws UnsupportedEncodingException {
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
                return skins.put(id, DefaultPlayerSkin.getDefaultSkinLegacy());
            }
    
            JsonObject skinData = realTextures.get("SKIN").getAsJsonObject();
            
            if (!skinData.has("url")) {
                return skins.put(id, DefaultPlayerSkin.getDefaultSkinLegacy());
            }
            
            String url = skinData.get("url").getAsString();
            
            if (!isTrustedDomain(url)) {
                // Spoofed payload? Does not use an official Mojang network...
                throw new IllegalArgumentException("Invalid payload, the domain issued was not trusted.");
            }
            
            String segment = getLastSegment(url);
            
            final ResourceLocation location = new ResourceLocation("skinchanger/" + segment);
            
            File directory = new File(new File("./mods/skinchanger".replace("/", File.separator), "skins"), attemptSubstring(segment, 0, 2));
            File fileLocation = new File(directory, segment + ".png");
            
            final IImageBuffer imageBuffer = new ImageBufferDownload();
            ThreadDownloadImageData imageData = new ThreadDownloadImageData(fileLocation, url, DefaultPlayerSkin
                .getDefaultSkinLegacy(), new IImageBuffer() {
                public BufferedImage parseUserSkin(BufferedImage image) {
                    if (imageBuffer != null) {
                        image = imageBuffer.parseUserSkin(image);
                    }
                    return image;
                }
                public void skinAvailable() {
                    if (imageBuffer != null) {
                        imageBuffer.skinAvailable();
                    }
                }
            });
            Minecraft.getMinecraft().renderEngine.loadTexture(location, imageData);
            return skins.put(id, location);
        } else {
            return null;
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
            
            response = IOUtils.toString(connection.getInputStream(), "UTF-8");
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
            return url;
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
            throw new IllegalArgumentException("Invalid URL \'" + url + "\'");
        }
        
        String host = uri.getHost();
    
        for (String domain : TRUSTED_DOMAINS) {
            if (host.endsWith(domain)) {
                return true;
            }
        }
        return false;
    }
}
