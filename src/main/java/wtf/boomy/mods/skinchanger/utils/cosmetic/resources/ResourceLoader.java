package wtf.boomy.mods.skinchanger.utils.cosmetic.resources;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import wtf.boomy.mods.skinchanger.SkinChangerMod;
import wtf.boomy.mods.skinchanger.utils.cache.CacheType;
import wtf.boomy.mods.skinchanger.utils.cache.InternalCache;
import wtf.boomy.mods.skinchanger.utils.cosmetic.CosmeticFactory;
import wtf.boomy.mods.skinchanger.utils.cosmetic.options.SimpleCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public final class ResourceLoader {
    
    private final Logger logger = LogManager.getLogger("SkinChanger - ResourceLoader");
    
    private final InternalCache internalCache;
    
    public ResourceLoader(CosmeticFactory cosmeticFactory) {
        this.internalCache = cosmeticFactory.getMod().getInternalCache();
    }
    
    /**
     * Loads a url into the game and caches it into the game.
     *
     * A url example could be: String.format("https://minotar.net/skin/%s", name)
     *
     * @param name      the name for the file (to be cached)
     * @param url       the url for the file (to be retrieved)
     * @param cacheType the cache type, influences if
     * @param callback  the real callback, will be called once the resource is properly loaded into the game
     */
    public void loadIntoGame(String name, String url, CacheType cacheType, SimpleCallback<ResourceLocation> callback) {
        if (cacheType == null || cacheType == CacheType.OTHER) {
            throw new IllegalArgumentException("Can no longer use none.");
        }
        
        File fileCache = new File(this.internalCache.determineCacheHome(cacheType), name);
        File dataFile = new File(fileCache, "data.png");
        
        if (this.internalCache.isValidCache(fileCache)) {
            if (dataFile.exists() && dataFile.isFile()) {
                loadFileDirectly(dataFile, cacheType, callback);
    
                return;
            }
        }
        
        // Force HTTPS on resources.
        if ((url.startsWith("http://") && !url.contains("optifine"))) {
            url = "https://" + url.substring("http://".length());
        }
        
        if (!fileCache.exists() && !fileCache.mkdirs()) {
            this.logger.warn("Unable to make cache file for " + fileCache);
    
            return;
        }
        
        URL constructedURL = constructURL(url);
    
        // Downloads the file at the URL to the path specified.
        if (!download(constructedURL, dataFile)) {
            return;
        }
    
        loadFileDirectly(dataFile, cacheType, callback);
    }
    
    public void loadFileDirectly(File file, CacheType cacheType, SimpleCallback<ResourceLocation> callback) {
        final ImageBuffer buffer = cacheType == CacheType.CAPE ? new CapeBuffer() : cacheType == CacheType.SKIN ? new SkinBuffer() : null;
        
        ResourceLocation location = new ResourceLocation("skinchanger", file.getAbsolutePath());
        
        Minecraft.getMinecraft().addScheduledTask(() -> {
            Minecraft.getMinecraft().renderEngine.loadTexture(location, new LocalFileData(cacheType == CacheType.SKIN ? DefaultPlayerSkin.getDefaultSkinLegacy() : null, file, buffer));
            
            if (callback != null) {
                callback.run(location);
            }
        });
    }
    
    private boolean download(URL source, File destination) {
        if (source == null) return false;
        
        try {
            HttpURLConnection connection = (HttpURLConnection) source.openConnection();
            connection.setRequestProperty("User-Agent", "SkinChanger/" + SkinChangerMod.VERSION);
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            
            // Error
            if (connection.getResponseCode() >= 400 && connection.getResponseCode() < 500) {
                return false;
            }
            
            InputStream input = connection.getInputStream();
            try {
                FileOutputStream output = FileUtils.openOutputStream(destination);
                
                try {
                    IOUtils.copy(input, output);
                    
                    output.close();
                    
                    return true;
                } finally {
                    IOUtils.closeQuietly(output);
                }
            } finally {
                IOUtils.closeQuietly(input);
            }
        } catch (IOException ex) {
            this.logger.error("Failed to download a URL (" + source + ") to (" + destination + ")", ex);
        }
        
        return false;
    }
    
    private URL constructURL(String url) {
        try {
            return new URL(url);
        } catch (IOException ex) {
            this.logger.error("Unable to convert to URL: " + url, ex);
        }
        
        return null;
    }
}
