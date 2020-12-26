package wtf.boomy.mods.skinchanger.utils.cache;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import wtf.boomy.mods.skinchanger.SkinChangerMod;
import wtf.boomy.mods.skinchanger.configuration.meta.SaveableClassData;
import wtf.boomy.mods.skinchanger.configuration.meta.SaveableField;
import wtf.boomy.mods.skinchanger.utils.ambiguous.ThreadFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * <p>
 * Internal caching mechanism. Uses 3 factors for caching.
 * </p>
 * <p>
 * The majority of the logic in this worker runs during the startup of the module.
 * The later is just for subsequent startups, and generic retrieval.
 * </p>
 * <p>
 * Supersedes the old "CacheRetriever" class.
 * </p>
 *
 * @author boomboompower
 * @version 1.0
 */
@SaveableClassData(saveName = "cache")
@SuppressWarnings("ResultOfMethodCallIgnored")
public final class InternalCache {
    
    private final Logger logger = LogManager.getLogger("SkinChanger - Cache");
    private final ThreadFactory threadFactory = new ThreadFactory("SkinChanger - Cache");
    
    // 3 hour live time. After that caches will
    // be expired on the next load.
    // TODO make this configurable.
    private final long LIVE_TIME = 1000 * 60 * 60 * 3;
    
    // Stores the cache directory for the mod
    private final File cacheDir;
    
    private final TreeMap<CacheType, File> mappedChildren = new TreeMap<>();
    
    // Stores our config variable
    @SaveableField(customName = "clearCache")
    private boolean invalidateCacheOnLoad = true;
    
    public InternalCache(SkinChangerMod mod) {
        mod.getConfig().addAsSaveable(this);
        
        this.cacheDir = new File(mod.getModConfigDirectory(), "cache");
    }
    
    /**
     * The init function for the cache system.
     *
     * Runs the following procedures once called;
     * 1. Creates the cache directory (and halts if one didn't exist already)
     * 2. Scans for any outdated/invalid cache files in the cache directory and subdirectories.
     * 3. Removes any invalid caches.
     */
    public void init() {
        if (!this.cacheDir.exists()) {
            boolean success = createCacheDir();
            
            if (!success) {
                this.logger.warn("Failed to create the cache directory.");
            }
        } else {
            // Delete all cache files that have expired
            // if the user has selected that setting
            
            if (!this.invalidateCacheOnLoad) {
                return;
            }
            
            this.threadFactory.runAsync(this::deleteInvalidCaches);
        }
    }
    
    public File determineCacheHome(CacheType type) {
        if (this.mappedChildren.containsKey(type)) {
            return this.mappedChildren.get(type);
        }
        
        File file = new File(this.cacheDir, type.name().toLowerCase());
        
        if (!file.exists()) {
            file.mkdirs();
        } else if (file.isFile()) {
            file.delete();
        }
        
        this.mappedChildren.put(type, file);
        
        return file;
    }
    
    public void populateCacheDirectory(File directory, boolean createDummyData) {
        if (directory.isFile() || (!directory.exists() && !directory.mkdirs())) return;
        
        File expiryFile = new File(directory, "expiry.dat");
        File dataFile = new File(directory, "data.png");
        
        boolean calculateHash = true;
        
        if (createDummyData && !dataFile.exists()) {
            try {
                dataFile.createNewFile();
    
                calculateHash = false;
            } catch (IOException ex) {
                this.logger.error("Failed to create a dummy file at " + directory, ex);
            }
        }
    
        FileWriter writer = null;
        BufferedWriter bufferedWriter = null;
        
        try {
            writer = new FileWriter(expiryFile);
            bufferedWriter = new BufferedWriter(writer);
            
            if (calculateHash) {
                writer.append("Hash: ").append(calculateHash(dataFile)).append(System.lineSeparator());
            }
            
            long expiryTime = calculateCurrentEpoch() + LIVE_TIME;
            
            writer.append("Expiry: ").append(String.valueOf(expiryTime)).append(System.lineSeparator());
            writer.append("Cached On: ").append(new Date().toString()).append(System.lineSeparator());
        } catch (IOException | NoSuchAlgorithmException ex) {
            this.logger.error("Failed to write a cache file to " + expiryFile, ex);
        } finally {
            closeSilently(writer);
            closeSilently(bufferedWriter);
        }
    }
    
    public boolean isValidCache(File fileCache) {
        if (fileCache == null || !fileCache.exists()) return false;
        
        if (fileCache.isFile()) {
            return !hasFileExpired(fileCache);
        }
    
        File childFile = new File(fileCache, "expiry.dat");
        
        return childFile.exists() && !hasFileExpired(childFile);
    }
    
    public void setInvalidateCacheOnLoad(boolean invalidate) {
        this.invalidateCacheOnLoad = invalidate;
    }
    
    public boolean isInvalidateCacheOnLoad() {
        return invalidateCacheOnLoad;
    }
    
    /**
     * Attempts to create the cache file directory if none exists.
     *
     * Returns false on failure, true on success.
     */
    private boolean createCacheDir() {
        if (this.cacheDir.isFile() && !this.cacheDir.delete()) return false;
        
        return this.cacheDir.mkdirs();
    }
    
    private void deleteInvalidCaches() {
        // This should never happen but in case implementation changes
        // in the future or the user is lightning fast we should prepare
        // ourselves for a very odd scenario.
        if (!this.cacheDir.exists()) return;
        
        // The structure of the cache file should be
        // [Cache Type (Folder)] -> [Cache File Name (Folder)] -> [Cache File + Expiry (File)]
        //
        // So this trip will need to be made in three parts
        // 1. Loop through all folders in cache directory
        // 2. Loop through those subfolders
        // 3. Check all expiry info in the subfolders (maybe name the file the expiration?)
        // 4. Delete the "Cache File Name" folder along with the contents if possible.
        
        File[] cacheTypeFolders = this.cacheDir.listFiles();
        
        // Don't continue if there are no sub folders in the first pass
        if (cacheTypeFolders == null || cacheTypeFolders.length == 0) {
            this.logger.warn("Skipping cache invalidation as there were no cache folders.");
            
            return;
        }
        
        // Stage 1. Loop through all folders in the cache directory
        for (File cacheTypeFile : cacheTypeFolders) {
            // Delete any files which shouldn't be there.
            if (cacheTypeFile.isFile()) {
                cacheTypeFile.delete();
                
                continue;
            }
            
            // Pass it off to the next function.
            invalidateStageTwo(cacheTypeFile);
        }
    }
    
    private void invalidateStageTwo(File cacheDirectory) {
        // Grab an array of the files in this directory
        File[] subFiles = cacheDirectory.listFiles();
        
        // Once more, ensure there are actually files before continuing.
        if (subFiles == null || subFiles.length == 0) return;
        
        // We'll want to loop over our children to determine if the child
        // needs to be deleted or if it's allowed to stay since the cache
        // is not invalid... yet.
        for (File child : subFiles) {
            // This stage and folder should only be handling
            // files, not folders. This is a sign of something
            // either being placed in by the user or an older
            // version of the cache implementation.
            
            // TODO move these files to a "legacy" directory instead of deleting.
            if (child.isFile()) {
                child.delete();
                
                continue;
            }
            
            // Take the children from this file.
            File[] children = child.listFiles();
            
            // A cache folder for a resource is empty. This signals
            // that the resource has been modified and should be
            // discarded appropriately.
            if (children == null || children.length == 0) {
                child.delete();
                
                continue;
            }
            
            // Stores the found expiry file. If this stays
            // null then the child folder will be deleted.
            File expiryFile = null;
            
            for (File file : children) {
                if (file.getName().equals("expiry.dat")) {
                    expiryFile = file;
                    
                    break;
                }
            }
            
            // There was no expiry file. Therefore we should delete the
            // cache folder since it has likely been tampered with.
            if (expiryFile == null) {
                child.delete();
                
                continue;
            }
            
            // Check if the expiry file is telling us the files
            // in this folder are out of date and should be deleted.
            boolean fileExpired = hasFileExpired(expiryFile);
            
            // If we should delete an invalid file then we will
            if (fileExpired) {
                child.delete();
            }
        }
    }
    
    private boolean hasFileExpired(File file) {
        FileReader reader = null;
        BufferedReader bufferedReader = null;
        
        try {
            // Reads the file into memory
            reader = new FileReader(file);
            bufferedReader = new BufferedReader(reader);
            
            // Uses Java 8 streams to collect each line in the file
            // and store them in a list of easier iteration.
            List<String> lines = bufferedReader.lines().collect(Collectors.toList());
            
            // Stores the full lines found which contains
            // the information about the expiry. If this remains null
            // then the file will be treated as invalid and the cache
            // directory will be deleted.
            String expiryLine = null;
            
            // Check each line and try find one that starts with "Expiry: "
            for (String line : lines) {
                // Ignore empty lines or some lines that start with
                // some common comment characters ("//" and "#")
                if (line.isEmpty() || line.startsWith("//") || line.startsWith("#")) continue;
                
                // Found a candidate. Break from this loop
                if (line.startsWith("Expiry: ")) {
                    // Remember to assign the line data or the
                    // logic so far will have been for nothing.
                    expiryLine = line;
                    
                    break;
                }
            }
            
            // Unable to find the line telling
            // us when the file is meant to actually
            // expire. Assume this file has been modified
            // incorrectly and therefore invalidate it.
            if (expiryLine == null) {
                return true;
            }
            
            // We can trim the fat off the line since
            // our function does not care about the
            // prefix on the line. It only checks the time.
            expiryLine = expiryLine.substring(8, expiryLine.length());
            
            return hasLineExpired(expiryLine);
        } catch (IOException ex) {
            this.logger.error("Failed to check expiry for file " + file + ". It will be deleted.", ex);
        } finally {
            // Cleans up the readers and prevents
            closeSilently(reader);
            closeSilently(bufferedReader);
        }
        
        // Default to an invalid file.
        return true;
    }
    
    private boolean hasLineExpired(String line) {
        // null should never be passed through this function.
        // once again for the sake of preventing silly things,
        // we will have a clause for this bizarre behaviour.
        if (line == null) {
            return true;
        }
        
        // Removes trailing white-spaces from the string.
        line = line.trim();
        
        // Empty lines are considered to be invalid
        if (line.isEmpty()) {
            return true;
        }
        
        // Stores the parsed time as per the line
        long parsedTime = 0L;
        
        try {
            // Uses an internal passing method to try
            // and convert the line into a valid long.
            parsedTime = Long.parseLong(line);
        } catch (NumberFormatException ex) {
            this.logger.trace("Failed to parse line " + line, ex);
            
            return true;
        }
        
        // If the parsed time is less than 0
        // the cache file is probably invalid.
        if (parsedTime < 0) {
            return true;
        }
        
        // If the calculated current time is
        // after the parsed time in the expiry
        // file we should indicate that it's time
        // for the file to be deleted.
        return calculateCurrentEpoch() > parsedTime;
    }
    
    /**
     * Calculates the hashed sha256 value of a file based on its contents.
     *
     * @param file the file to calculate the checksum for.
     * @return the uppercase hash of the file.
     *
     * @throws IOException if the file cannot be read
     * @throws NoSuchAlgorithmException if the sha256 algorithm cannot be found on the system.
     */
    private String calculateHash(File file) throws IOException, NoSuchAlgorithmException {
        byte[] data = Files.readAllBytes(file.toPath());
        
        // Convert bytes to SHA-256
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] result = messageDigest.digest(data);
        
        StringBuilder sb = new StringBuilder();
        
        for (byte b : result) {
            sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        
        // Construct hash using bytes
        return sb.toString().toUpperCase();
    }
    
    /**
     * Silently closes a closable object.
     *
     * If an error occurs or the input is null,
     * the error will be traced, but will not kill the program.
     */
    private void closeSilently(Closeable object) {
        // Skip all null objects
        if (object == null) return;
        
        try {
            // Close the stream. Errors will be traced.
            object.close();
        } catch (IOException ex) {
            this.logger.trace("Failed to close a stream for " + object, ex);
        }
    }
    
    /**
     * Internal epoch to be slightly festive.
     * A surprise to be sure and a welcome one.
     *
     * 25th Dec, 2020 12:00:00 AM, GMT +11:00
     */
    private long calculateCurrentEpoch() {
        return System.currentTimeMillis() - 1608814800000L;
    }
}
