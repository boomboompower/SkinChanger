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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import lombok.Getter;

import me.do_you_like.mods.skinchanger.SkinChangerMod;
import me.do_you_like.mods.skinchanger.compatability.DefaultPlayerSkin;
import me.do_you_like.mods.skinchanger.utils.resources.SkinBuffer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.util.ResourceLocation;

public class CacheRetriever {

    private static final boolean FORCE_HTTPS = true;

    private HashMap<String, String> cachedValues = new HashMap<>();

    @Getter
    private final SkinChangerMod mod;

    @Getter
    private final File cacheDirectory;

    public CacheRetriever(SkinChangerMod mod) {
        this.mod = mod;
        this.cacheDirectory = new File(mod.getModConfigDirectory(), "cache");

        for (int i = 0; i < 50; i++) {
            System.out.println(this.cacheDirectory);
        }

        genCacheDirectory();
    }

    // String.format("https://minotar.net/skin/%s", name)
    public ResourceLocation loadIntoGame(String name, String url) {
        File cacheDirectory = getCacheDirForName(name);

        boolean cacheFileExists = doesCacheExist(name);

        if (isCacheExpired(name)) {
            cacheFileExists = false;

            cacheDirectory.delete();
        }

        ResourceLocation location = new ResourceLocation("skins/" + getCacheName(name));

        File dataFile = new File(cacheDirectory, cacheDirectory.getName() + ".png");

        // Force HTTPS on resources.
        if (FORCE_HTTPS && url.startsWith("http://")) {
            url = "https://" + url.substring("http://".length());
        }

        ThreadDownloadImageData imageData = new ThreadDownloadImageData(dataFile, url, DefaultPlayerSkin.getDefaultSkinLegacy(), new SkinBuffer());

        Minecraft.getMinecraft().addScheduledTask(() -> {
            Minecraft.getMinecraft().renderEngine.loadTexture(location, imageData);
        });

        generateCacheFiles(name);

        return location;
    }

    public void generateCacheFiles(String name) {
        File cacheDirectory = getCacheDirForName(name);

        if (isCacheExpired(name)) {
            cacheDirectory.delete();
        }

        if (!cacheDirectory.exists()) {
            if (!cacheDirectory.mkdir()) {
                System.err.println("Failed to create a cache directory.");
                return;
            }
        }

        File dataFile = new File(cacheDirectory, cacheDirectory.getName() + ".png");
        File cacheFile = new File(cacheDirectory, cacheDirectory.getName() + ".lock");

        try {
            if (!dataFile.exists()) {
                if (!dataFile.createNewFile()) {
                    System.err.println("Failed to create a cache file.");
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
    }

    /**
     * Returns true if the value inside the cache file is either missing
     *
     * @param name the name of the file to check the cache from.
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

            long time = Long.valueOf(line);

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

        return dataFile.exists() && cacheFile.exists();
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
        String subStrName = name.substring(0, Math.min(5, name.length()));

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
}
