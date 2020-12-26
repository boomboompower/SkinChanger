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

package wtf.boomy.mods.skinchanger.configuration;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import wtf.boomy.mods.skinchanger.SkinChangerMod;
import wtf.boomy.mods.skinchanger.configuration.meta.ConfigurationData;
import wtf.boomy.mods.skinchanger.configuration.meta.SaveableClassData;
import wtf.boomy.mods.skinchanger.configuration.meta.SaveableField;
import wtf.boomy.mods.skinchanger.utils.BetterJsonObject;
import wtf.boomy.mods.skinchanger.utils.cosmetic.api.SkinAPIType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * A reflection based handler for saving and loading from files to fields
 *
 * Saved fields are stored with their respective classes, see the following:
 * <code>
 * {
 *     "class": {
 *         "field1": "value",
 *         ...
 *     },
 *     "class2": {
 *         "myfield": "value",
 *         ...
 *     }
 * }
 * </code>
 *
 * @since 3.0
 * @author boomboompower
 */
@SaveableClassData(saveName = "config")
public class ConfigurationHandler {
    
    private final Logger logger = LogManager.getLogger("SkinChanger - Config");
    
    @SaveableField(customName = "animatedRenderer")
    private boolean usingAnimatedPlayer = true;
    
    @SaveableField(customName = "animatedCapeRenderer")
    private boolean usingAnimatedCape = true;
    
    @SaveableField(customName = "animatedLighting")
    private boolean usingLighting = false;
    
    @SaveableField(customName = "apiType")
    private SkinAPIType skinAPIType = SkinAPIType.ASHCON;
    
    @SaveableField(customName = "modEnabled")
    private boolean modEnabled = true;
    
    @SaveableField(customName = "animationSpeed")
    private float animationSpeed = 1;
    
    @SaveableField(customName = "oldButtons")
    private boolean oldButtons = false;
    
    @SaveableField(customName = "updateCheck")
    private boolean runUpdater = true;
    
    // Oh god don't let this be saveable
    private boolean everyoneMe = false;
    
    private final SkinChangerMod mod;
    private final File configFile;
    private final File capesDirectory;
    
    // Stores all possible saved values
    private final HashMap<Class<?>, ConfigurationData[]> saveableValues = new HashMap<>();
    
    /**
     * A constructor for a configurable class based on a mod
     *
     * The config directory will be populated from the mod
     *
     * @param mod the mod for the configuration
     */
    public ConfigurationHandler(SkinChangerMod mod) {
        this(mod, mod.getModConfigDirectory());
    }
    
    /**
     * A constructor for a configurable class based on a mod
     *
     * @param mod the mod for the configuration
     * @param configDirectory the main directory where files will be stored
     */
    public ConfigurationHandler(SkinChangerMod mod, File configDirectory) {
        this.mod = mod;
        
        this.configFile = new File(configDirectory, "config.json");
        this.capesDirectory = new File(configDirectory, "capes");
    
        extractCapesDirectory();
    }
    
    public void save() {
        if (this.saveableValues.isEmpty()) {
            return;
        }
        
        BetterJsonObject saveObject = new BetterJsonObject();
        
        for (Map.Entry<Class<?>, ConfigurationData[]> entry : this.saveableValues.entrySet()) {
            Class<?> clazz = entry.getKey();
            
            BetterJsonObject object = new BetterJsonObject();
            
            for (ConfigurationData data : entry.getValue()) {
                Object value = data.getValue();
                
                String serialized = saveObject.getGsonData().toJson(value);
                
                if (value.getClass().isEnum()) {
                    serialized = serialized.replace("\"", "");
                }
                
                object.addProperty(data.getSaveName(), serialized);
            }
            
            String classSaveName = clazz.getSimpleName();
            
            if (clazz.isAnnotationPresent(SaveableClassData.class)) {
                String altName = clazz.getAnnotation(SaveableClassData.class).saveName();
                
                if (altName.trim().length() > 0) {
                    classSaveName = altName;
                }
            }
            
            saveObject.add(classSaveName, object);
        }
        
        saveObject.writeToFile(this.configFile);
    }
    
    /**
     * Loads the serialized values from the config file into
     * the respective fields for which they are for.
     *
     * Only loads the fields into mapped fields which have already
     * been registered through {@link #addAsSaveable(Object)}
     *
     * This method will do nothing if the config file doesn't exist
     * or if there are no fields loaded into the handler.
     */
    public void load() {
        if (this.saveableValues.isEmpty()) {
            return;
        }
    
        if (!this.configFile.exists()) {
            return;
        }
        
        try {
            FileReader fileReader = new FileReader(this.configFile);
            BufferedReader reader = new BufferedReader(fileReader);
            
            List<String> lines = reader.lines().collect(Collectors.toList());
            
            if (lines.isEmpty()) {
                return;
            }
            
            String json = String.join("\n", lines);
            
            if (json.trim().isEmpty()) {
                this.logger.warn("The config file was empty. Skipping...");
                
                return;
            }
    
            JsonElement parsedJson = new JsonParser().parse(json);
            
            if (!parsedJson.isJsonObject()) {
                this.logger.error("Unable to load settings, config format incorrect.");
                
                return;
            }
            
            BetterJsonObject config = new BetterJsonObject(parsedJson.getAsJsonObject());
            
            for (Map.Entry<Class<?>, ConfigurationData[]> entry : this.saveableValues.entrySet()) {
                Class<?> clazz = entry.getKey();
                
                String classSaveName = clazz.getSimpleName();
    
                if (clazz.isAnnotationPresent(SaveableClassData.class)) {
                    String altName = clazz.getAnnotation(SaveableClassData.class).saveName();
        
                    if (altName.trim().length() > 0) {
                        classSaveName = altName;
                    }
                }
                
                if (!config.has(classSaveName)) {
                    continue;
                }
                
                JsonElement metaElement = config.get(classSaveName);
                
                if (!metaElement.isJsonObject()) {
                    this.logger.error("Config segment " + classSaveName + " was not the correct type. Expected JSON_OBJECT but got "+ metaElement.getClass());
                    
                    continue;
                }
                
                JsonObject meta = metaElement.getAsJsonObject();
                
                for (ConfigurationData data : entry.getValue()) {
                    if (!meta.has(data.getSaveName())) {
                        continue;
                    }
                    
                    try {
                        // Gets the data for this value
                        JsonElement element = meta.get(data.getSaveName());
                        
                        if (data.getField().getType().isEnum()) {
                            boolean found = false;
                            
                            for (Object o : data.getField().getType().getEnumConstants()) {
                                if (o.toString().equalsIgnoreCase(element.getAsString())) {
                                    data.setValue(o);
                                    
                                    found = true;
                                    
                                    break;
                                }
                            }
                            
                            if (!found) {
                                this.logger.warn("Unable to find appropriate value for " + data.getField().getType());
                            }
                        } else {
                            Object deserialize = config.getGsonData().fromJson(element, data.getField().getType());
    
                            data.setValue(deserialize);
                        }
                    } catch (JsonSyntaxException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } catch (IOException | JsonSyntaxException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Registers an instance to the configuration handler.
     *
     * Any fields in this class with the {@link SaveableField} will have their values read during
     * the saving process (when {@link #save()} is called) and set during the loading process (when
     * {@link #load()} is called.
     *
     * Field names in the config can be customized by changing the {@link SaveableField#customName()}
     * attribute, and class names can be changed with the {@link SaveableClassData} attribute.
     *
     * @param instance the class instance
     */
    public void addAsSaveable(Object instance) {
        if (instance == null) {
            return;
        }
        
        // If the class is already registered, don't add it again.
        if (this.saveableValues.containsKey(instance.getClass())) {
            return;
        }
        
        // Lists the declared fields in the class.
        Field[] clazzFields = instance.getClass().getDeclaredFields();
        
        // Don't touch it if it has no fields
        if (clazzFields.length <= 0) {
            return;
        }
        
        List<ConfigurationData> storedData = new ArrayList<>();
        
        // True if a field has been found with a SaveableField attribute.
        boolean hasSaveableField = false;
        
        for (Field field : clazzFields) {
            try {
                // Skip to the next field if this field
                // hasn't specified the SaveableField annotation
                if (!field.isAnnotationPresent(SaveableField.class)) {
                    continue;
                }
                
                // Specify that this class actually has
                // fields which can be saved
                hasSaveableField = true;
    
                // Collect the saved field information
                SaveableField saveable = field.getAnnotation(SaveableField.class);
    
                // Choose the default config name as the field's name
                String nameToSave = field.getName();
    
                // If the annotation specifies a custom name,
                // use that instead. (Will still work will obfuscation).
                if (!saveable.customName().isEmpty()) {
                    nameToSave = saveable.customName();
                }
    
                // Create a configuration instance of this field
                ConfigurationData data = new ConfigurationData(field, nameToSave, saveable.shouldOverwriteOnLoad());
    
                // Initialize the instance with the fields instance (for later saving)
                data.initialize(instance);
    
                // Add the field to the stored data list to be saved later.
                storedData.add(data);
            } catch (AnnotationFormatError ex) {
                this.logger.error("An error occurred whilst reading field " + field.getName() + " in " + instance.getClass().getSimpleName(), ex);
            }
        }
        
        this.saveableValues.put(instance.getClass(), storedData.toArray(new ConfigurationData[0]));
        
        if (!hasSaveableField) {
            this.logger.warn("Class '%s' was called through ConfigurationHandler#addAsSaveable however no @SaveableField annotations were present on any fields.");
        }
    }
    
    /**
     * Extracts the {@code capes.zip} file from the mod resources so the user does not need to
     * download capes to use them (as they will already be on the computer).
     * <p>
     * This task will not be run if the capes directory already exists, or cannot be created.
     */
    public void extractCapesDirectory() {
        // If the capes location exists and is a file we will delete the file
        // if we cannot delete the file we will not attempt extraction since
        // files can only be placed in a folder, not in another file (☞ﾟヮﾟ)☞
        if (this.capesDirectory.exists() && this.capesDirectory.isFile()) {
            if (!this.capesDirectory.delete()) {
                return;
            }
        }
        
        // Something has already been extracted so we will terminate
        // instead of trying to overwrite the existing files.
        if (this.capesDirectory.exists()) {
            return;
        }
        
        // Tries to make the capes directory, breaks on a paradox
        // if this fails the extraction is cancelled because it means
        // the file will not be able to be extracted to an existing folder.
        if (!this.capesDirectory.mkdirs()) {
            return;
        }
        
        try {
            // Attempt to load the capes resource
            InputStream content = getClass().getResourceAsStream("/capes.zip");
            
            // It's only around 34.6kb, but if the mod is distributed without it we can ignore the extraction.
            if (content == null) {
                return;
            }
            
            String capesDirectoryCanonicalPath = this.capesDirectory.getCanonicalPath() + File.separator;
            
            // Increases the performance, usually completes in ~32ms, 2048 is quicker but uses more memory.
            byte[] buffer = new byte[512];
            
            ZipInputStream zipFile = new ZipInputStream(content);
            ZipEntry entry;
            
            // Iterates through the contents of the zip, if no more entries exist then the loop will end
            while ((entry = zipFile.getNextEntry()) != null) {
                // If the entry is a directory it will be skipped.
                if (!entry.isDirectory()) {
                    FileOutputStream output = null;
                    
                    try {
                        // Creates a file relative to the path in the zip file
                        File unzippedFile = new File(this.capesDirectory, entry.getName());
                        
                        // Fixes the "Zip Slip" vulnerability caused by directory traversal in zips
                        // See https://snyk.io/research/zip-slip-vulnerability for more info
                        if (!unzippedFile.getCanonicalPath().startsWith(capesDirectoryCanonicalPath)) {
                            this.logger.trace("Skipping suspicious zip entry: " + entry.getName());
                            
                            continue;
                        }
                        
                        // Creates the parent directory for the zip if it doesn't already exist
                        // or skips this entry if the parent folder cannot be created.
                        if (!unzippedFile.getParentFile().exists() && !unzippedFile.getParentFile().mkdirs()) {
                            continue;
                        }
                        
                        // Checks if the unzipped file already exists, if it doesn't
                        // we'll attempt to create it. If this fails we'll skip to the
                        // next entry in the zip file.
                        if (!unzippedFile.exists() && !unzippedFile.createNewFile()) {
                            continue;
                        }
                        
                        // Creates an output stream out of the existing file.
                        output = new FileOutputStream(unzippedFile);
                        
                        int len;
                        
                        // Reads the bytes from the zip entry using the buffer for the specified length.
                        while ((len = zipFile.read(buffer)) > 0) {
                            output.write(buffer, 0, len);
                        }
                    } finally {
                        // Closes the output file if possible.
                        if (output != null) {
                            output.close();
                        }
                    }
                }
                
                // Releases the entry once we are done with it.
                zipFile.closeEntry();
            }
            
            // Releases the zip once we are done with it
            zipFile.close();
            
            // Releases the resource once we are done. Saves memory.
            content.close();
        } catch (IOException ex) {
            // Any errors shouldn't crash the game, this is cosmetic after all.
            ex.printStackTrace();
        }
    }
    
    public SkinChangerMod getMod() {
        return this.mod;
    }
    
    public File getConfigFile() {
        return this.configFile;
    }
    
    public File getCapesDirectory() {
        return this.capesDirectory;
    }
    
    public void setUsingAnimatedPlayer(boolean usingAnimatedPlayer) {
        this.usingAnimatedPlayer = usingAnimatedPlayer;
    }
    
    public void setUsingAnimatedCape(boolean usingAnimatedCape) {
        this.usingAnimatedCape = usingAnimatedCape;
    }
    
    public void setSkinAPIType(SkinAPIType skinAPIType) {
        this.skinAPIType = skinAPIType;
    }
    
    public void setEveryoneMe(boolean everyoneMe) {
        this.everyoneMe = everyoneMe;
    }
    
    public void setModEnabled(boolean modEnabled) {
        this.modEnabled = modEnabled;
    }
    
    public void setUsingLighting(boolean usingLighting) {
        this.usingLighting = usingLighting;
    }
    
    public void setOldButtons(boolean oldButtons) {
        this.oldButtons = oldButtons;
    }
    
    public void setRunUpdater(boolean runUpdater) {
        this.runUpdater = runUpdater;
    }
    
    public void setAnimationSpeed(float animationSpeed) {
        this.animationSpeed = animationSpeed;
    }
    
    public boolean isUsingAnimatedPlayer() {
        return this.usingAnimatedPlayer;
    }
    
    public boolean isUsingAnimatedCape() {
        return this.usingAnimatedCape;
    }
    
    public boolean isUsingLighting() {
        return this.usingLighting;
    }
    
    public boolean isOldButtons() {
        return this.oldButtons;
    }
    
    public boolean isEveryoneMe() {
        return this.everyoneMe;
    }
    
    public boolean isModEnabled() {
        return this.modEnabled;
    }
    
    public boolean shouldRunUpdater() {
        return this.runUpdater;
    }
    
    public float getAnimationSpeed() {
        return animationSpeed;
    }
    
    public SkinAPIType getSkinAPIType() {
        return this.skinAPIType;
    }
}
