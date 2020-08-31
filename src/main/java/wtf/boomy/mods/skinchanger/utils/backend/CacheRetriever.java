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

package wtf.boomy.mods.skinchanger.utils.backend;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import wtf.boomy.mods.skinchanger.SkinChangerMod;
import wtf.boomy.mods.skinchanger.utils.game.Callback;
import wtf.boomy.mods.skinchanger.utils.resources.CapeBuffer;
import wtf.boomy.mods.skinchanger.utils.resources.LocalFileData;
import wtf.boomy.mods.skinchanger.utils.resources.SkinBuffer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import java.util.HashMap;
import java.util.UUID;

/**
 * A class which handles saving and loading files to the SkinChanger cache to save
 * bandwidth for users and make the mod work offline as well.
 * <p>
 * Cached files generally expire after about a day
 *
 * @author boomboompower
 * @since 3.0.0
 */
public class CacheRetriever {
    
    private static final ResourceLocation undefinedTexture = new ResourceLocation("skinchanger", "light.png");
    
    // Upgrade insecure requests. This will enforce on all urls except optifine
    private static final boolean FORCE_HTTPS = true;
    
    private final HashMap<String, String> cachedValues = new HashMap<>();
    
    private final SkinChangerMod mod;
    
    private final File cacheDirectory;
    
    /**
     * Simple constructor for the mod
     *
     * @param mod the SkinChanger mod instance
     */
    public CacheRetriever(SkinChangerMod mod) {
        this.mod = mod;
        this.cacheDirectory = new File(mod.getModConfigDirectory(), "cache");
        
        genCacheDirectory();
    }
    
    /**
     * Loads a url into the game and caches it into the game.
     *
     * @param name      the name for the file (to be cached)
     * @param url       the url for the file (to be retrieved)
     * @param cacheType the cache type, influences if
     * @param callback  the real callback, will be called once the resource is properly loaded into the game
     */
    // String.format("https://minotar.net/skin/%s", name)
    public void loadIntoGame(String name, String url, CacheType cacheType, Callback<ResourceLocation> callback) {
        if (cacheType == null || cacheType == CacheType.OTHER) {
            throw new IllegalArgumentException("Can no longer use none.");
        }
    
        File fileCache = new File(this.cacheDirectory, name);
//        File expiryFile = new File(fileCache + ".expire", name);
        File dataFile = new File(fileCache, name);
        
        ResourceLocation location = new ResourceLocation("skins/" + getCacheName(name));
        
        final IImageBuffer buffer = cacheType == CacheType.CAPE ? new CapeBuffer() : cacheType == CacheType.SKIN ? new SkinBuffer() : null;
        
        if (dataFile.exists() && dataFile.isFile()) {
            loadFileDirectly(dataFile, cacheType, callback);
            
            return;
        }
        
        // Force HTTPS on resources.
        if (FORCE_HTTPS && (url.startsWith("http://") && !url.contains("optifine"))) {
            url = "https://" + url.substring("http://".length());
        }
        
        if (!fileCache.exists()) {
            if (!fileCache.mkdirs()) {
                System.out.println("Unable to make cache dir");
                
                return;
            }
        }
        
        try {
            download(new URL(url), dataFile);
    
            loadFileDirectly(dataFile, cacheType, callback);
        } catch (IOException | NullPointerException ex) {
            fileCache.delete();
            
            ex.printStackTrace();
        }
    }
    
    public void loadFileDirectly(File file, CacheType cacheType, Callback<ResourceLocation> callback) {
        final IImageBuffer buffer = cacheType == CacheType.CAPE ? new CapeBuffer() : cacheType == CacheType.SKIN ? new SkinBuffer() : null;
        
        ResourceLocation location = new ResourceLocation("skins/" + getCacheName(file.getName()));
        
        Minecraft.getMinecraft().addScheduledTask(() -> {
            Minecraft.getMinecraft().renderEngine.loadTexture(location, new LocalFileData(cacheType == CacheType.SKIN ? DefaultPlayerSkin.getDefaultSkinLegacy() : undefinedTexture, file, buffer));
        
            if (callback != null) {
                callback.run(location);
            }
        });
    }
    
    /**
     * Creates a cache directory and data file for the cache
     *
     * @param name the name of the file
     */
    public File generateCacheFiles(String name) {
        File cacheDirectory = getCacheDirForName(name);
        
        if (isCacheExpired(name)) {
            //noinspection ResultOfMethodCallIgnored
            cacheDirectory.delete();
        }
        
        if (!cacheDirectory.exists()) {
            if (!cacheDirectory.mkdir()) {
                System.err.println("Failed to create a cache directory.");
                return null;
            }
        }
        
        File dataFile = new File(cacheDirectory, cacheDirectory.getName() + ".png");
        File cacheFile = new File(cacheDirectory, cacheDirectory.getName() + ".lock");
        
        try {
            if (!dataFile.exists()) {
                if (!dataFile.createNewFile()) {
                    System.err.println("Failed to create a data file.");
                }
            }
            
            if (!cacheFile.exists()) {
                if (!cacheFile.createNewFile()) {
                    System.err.println("Failed to create a cache file.");
                } else {
                    // Write the cache information
                    FileWriter writer = new FileWriter(cacheFile);
                    BufferedWriter bufferedWriter = new BufferedWriter(writer);
                    
                    long expirationTime = System.currentTimeMillis();
                    
                    // Current time + 1 day
                    expirationTime += 24 * 60 * 60 * 1000;
                    
                    // Write the one line.
                    bufferedWriter.write(expirationTime + System.lineSeparator());
                    bufferedWriter.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return dataFile;
    }
    
    /**
     * Gets the cached data file from a name
     *
     * @param name      the name of the file (will be converted to a cache name)
     * @param extension the extension of the file (must start with a .)
     *
     * @return the file for the cache or null if it does not exist.
     */
    public File getCacheFileIfIExists(String name, String extension) {
        if (!extension.startsWith(".")) {
            return null;
        }
        
        if (!doesCacheExist(name) || isCacheExpired(name)) {
            return null;
        }
        
        String cached_name = getCacheName(name);
        File dir = new File(this.cacheDirectory, cached_name);
        File data_file = new File(dir, cached_name + extension);
        
        if (!data_file.exists()) {
            return null;
        }
        
        if (data_file.isDirectory()) {
            if (data_file.delete()) {
                return null;
            }
            
            return null;
        }
        
        return data_file;
    }
    
    /**
     * Returns true if the value inside the cache file is either missing
     *
     * @param name the name of the file to check the cache from.
     *
     * @return true if the cached file should have expired.
     */
    public boolean isCacheExpired(String name) {
        if (name == null) {
            return true;
        }
        
        File fileCache = getCacheDirForName(name);
        
        if (!fileCache.exists()) {
            return true;
        }
        
        File cacheLock = new File(fileCache, fileCache.getName() + ".lock");
        
        if (!cacheLock.exists()) {
            return true;
        }
        
        try {
            FileReader fileReader = new FileReader(cacheLock);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            
            // First line contains the expiration time.
            String line = bufferedReader.readLine();
            
            bufferedReader.close();
            
            long time = Long.parseLong(line);
            
            return System.currentTimeMillis() > time;
        } catch (IOException ex) {
            System.err.println("Unable to read cache file for " + name);
            
            ex.printStackTrace();
            
            return true;
        } catch (NumberFormatException ex) {
            System.err.println("Cache file had an invalid number");
            
            ex.printStackTrace();
            
            return true;
        }
    }
    
    /**
     * Returns true if the cache file for a name exists.
     *
     * @param name the name to check for a cache file.
     *
     * @return true if the cache file exists
     */
    public boolean doesCacheExist(String name) {
        if (!genCacheDirectory()) {
            return false;
        }
        
        File cacheDirectory = getCacheDirForName(name);
        
        if (!cacheDirectory.exists()) {
            return false;
        }
        
        File dataFile = new File(cacheDirectory, cacheDirectory.getName() + ".png");
        File cacheFile = new File(cacheDirectory, cacheDirectory.getName() + ".lock");
        
        return dataFile.exists() && dataFile.length() > 2 && cacheFile.exists();
    }
    
    /**
     * Generates a unique ID from a string which can be used as a cache name.
     *
     * @param name the name to retrieve a cache name from
     *
     * @return a unique ID for the entered name which corresponds to the name's cache directory.
     */
    private String getCacheName(String name) {
        name = name.toLowerCase();
        
        // If the cache file exists in memory, just return the name of it.
        if (this.cachedValues.containsKey(name)) {
            return this.cachedValues.get(name);
        }
        
        UUID id = UUID.nameUUIDFromBytes(name.getBytes());
        
        // Take up to the first 5 characters of the name
        String subStrName = name.substring(0, Math.min(7, name.length()));
        
        // Split the UUID into its four segments 0-1-2-3
        String[] uuidSplit = id.toString().split("-");
        
        // Take the first and second component of the UUID.
        String idFirstComponent = uuidSplit[0] + uuidSplit[1];
        
        // Creates the final name of the cache file.
        String finalCacheName = subStrName + "_" + idFirstComponent;
        
        // Cache the name so this code doesn't run on it again.
        this.cachedValues.put(name, finalCacheName);
        
        return finalCacheName;
    }
    
    /**
     * Retrieves the cache directory from a name/input.
     *
     * @param nameOfFile the input which will be cached
     *
     * @return a directory which corresponds to a names cache file.
     */
    private File getCacheDirForName(String nameOfFile) {
        String cacheName = getCacheName(nameOfFile);
        
        return new File(this.cacheDirectory, cacheName);
    }
    
    /**
     * Attempts to generate a cache directory.
     *
     * @return true if the cache directory already existed.
     */
    private boolean genCacheDirectory() {
        boolean existed = true;
        
        if (!this.cacheDirectory.getParentFile().exists()) {
            existed = false;
            
            if (this.cacheDirectory.getParentFile().mkdirs()) {
                System.out.println("Suggested mod directory created.");
            } else {
                System.err.println("Unable to create the mod directory");
                
                return false;
            }
        }
        
        if (!this.cacheDirectory.exists()) {
            existed = false;
            
            if (this.cacheDirectory.mkdir()) {
                System.out.println("Cache directory created.");
            } else {
                System.err.println("Unable to create cache directory.");
                
                return false;
            }
        }
        
        return existed;
    }
    
    private void download(URL source, File destination) {
        try {
            HttpURLConnection connection = (HttpURLConnection) source.openConnection();
            connection.setRequestProperty("User-Agent", "SkinChanger/" + SkinChangerMod.VERSION);
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            
            // Error
            if (connection.getResponseCode() >= 400 && connection.getResponseCode() < 500) {
                return;
            }
            
            InputStream input = connection.getInputStream();
            try {
                FileOutputStream output = FileUtils.openOutputStream(destination);
                
                try {
                    IOUtils.copy(input, output);
                    
                    output.close();
                } finally {
                    IOUtils.closeQuietly(output);
                }
            } finally {
                IOUtils.closeQuietly(input);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public File createCachedBase64(String name, String base64data) {
        try {
            // Represents the decoded base64 content
            byte[] rawData = Base64.getDecoder().decode(base64data);
            
            File cacheFile = generateCacheFiles(name);
    
            FileUtils.writeByteArrayToFile(cacheFile, rawData);
            
            return cacheFile;
        } catch (IllegalArgumentException | IOException ex) {
            ex.printStackTrace();
            
            return null;
        }
    }
    
    public SkinChangerMod getMod() {
        return this.mod;
    }
    
    public File getCacheDirectory() {
        return this.cacheDirectory;
    }
    
    /**
     * Tells the code how the cached file should be parsed by the mod
     * <p>
     * SKIN will be parsed through {@link SkinBuffer}
     * CAPE will be parsed through {@link CapeBuffer}
     * OTHER will not be parsed through anything.
     */
    public enum CacheType {
        SKIN,
        CAPE,
        OTHER
    }
}
