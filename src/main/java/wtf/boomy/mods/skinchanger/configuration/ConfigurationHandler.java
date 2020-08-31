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

import wtf.boomy.mods.skinchanger.SkinChangerMod;
import wtf.boomy.mods.skinchanger.api.SkinAPIType;
import wtf.boomy.mods.skinchanger.configuration.meta.ConfigurationData;
import wtf.boomy.mods.skinchanger.configuration.meta.SaveableClassData;
import wtf.boomy.mods.skinchanger.configuration.meta.SaveableField;
import wtf.boomy.mods.skinchanger.utils.general.BetterJsonObject;

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

@SaveableClassData(saveName = "config")
public class ConfigurationHandler {
    
    @SaveableField(customName = "animatedRenderer")
    private boolean usingAnimatedPlayer = true;
    
    @SaveableField(customName = "animatedCapeRenderer")
    private boolean usingAnimatedCape = true;
    
    @SaveableField(customName = "willBlurUI")
    private boolean shouldBlurUI = true;
    
    @SaveableField(customName = "apiType")
    private SkinAPIType skinAPIType = SkinAPIType.ASHCON;
    
    // A bloody hack
    private boolean everyoneMe = false;
    
    private final SkinChangerMod mod;
    private final File configFile;
    private final File capesDirectory;
    
    private final HashMap<Class<?>, ConfigurationData[]> saveableValues = new HashMap<>();
    
    public ConfigurationHandler(SkinChangerMod mod) {
        this.mod = mod;
        
        this.configFile = new File(mod.getModConfigDirectory(), "config.json");
        this.capesDirectory = new File(mod.getModConfigDirectory(), "capes");
        
        extractCapesDirectory();
    }
    
    public ConfigurationHandler(SkinChangerMod mod, File configDirectory) {
        this.mod = mod;
        
        this.configFile = new File(configDirectory, "config.json");
        this.capesDirectory = new File(configDirectory, "capes");
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
                return;
            }
    
            JsonElement parsedJson = new JsonParser().parse(json);
            
            if (!parsedJson.isJsonObject()) {
                System.err.println("Unable to load settings, config file corrupt.");
                
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
                    System.err.println("Corrupt config segment " + classSaveName);
                    
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
                                System.err.println("Unable to find appropriate value for " + data.getField().getType());
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
    
    public void addAsSaveable(Object clazz) {
        if (clazz == null) {
            return;
        }
        
        // If the class is already registered, don't add it again.
        if (this.saveableValues.containsKey(clazz.getClass())) {
            return;
        }
        
        Field[] clazzFields = clazz.getClass().getDeclaredFields();
        
        // Don't touch it if it has no fields
        if (clazzFields.length <= 0) {
            return;
        }
        
        List<ConfigurationData> storedData = new ArrayList<>();
        
        // True if a field has been found with a SaveableField attribute.
        boolean hasSaveableField = false;
        
        for (Field f : clazzFields) {
            try {
                // Check if the field is actually saveable
                if (!f.isAnnotationPresent(SaveableField.class)) {
                    continue;
                }
    
                hasSaveableField = true;
    
                SaveableField saveable = f.getAnnotation(SaveableField.class);
    
                String nameToSave = f.getName();
    
                if (!saveable.customName().isEmpty()) {
                    nameToSave = saveable.customName();
                }
    
                // Create an instance of this field.
                ConfigurationData data = new ConfigurationData(f, nameToSave, saveable.shouldOverwriteOnLoad());
    
                data.initialize(clazz);
    
                storedData.add(data);
            } catch (AnnotationFormatError ex) {
                System.err.println("An error occurred whilst reading field " + f.getName() + " in " + clazz.getClass().getSimpleName());
            }
        }
        
        this.saveableValues.put(clazz.getClass(), storedData.toArray(new ConfigurationData[0]));
        
        if (!hasSaveableField) {
            System.out.println("Class '%s' was called through ConfigurationHandler#addAsSaveable however no @SaveableField annotations were present on any fields. It is advised to deregister that class as calling this method for no purpose may degrade performance. ");
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
                        
                        // If for some reason the parent file of this entry doesn't exist, it should be made.
                        // This attempts to make the parent folder.
                        if (!unzippedFile.getParentFile().exists()) {
                            if (!unzippedFile.getParentFile().mkdirs()) {
                                continue;
                            }
                        }
                        
                        // Try make the file we will write to
                        if (!unzippedFile.exists()) {
                            if (!unzippedFile.createNewFile()) {
                                continue;
                            }
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
    
    public void setShouldBlurUI(boolean shouldBlurUI) {
        this.shouldBlurUI = shouldBlurUI;
    }
    
    public void setEveryoneMe(boolean everyoneMe) {
        this.everyoneMe = everyoneMe;
    }
    
    public boolean isUsingAnimatedPlayer() {
        return this.usingAnimatedPlayer;
    }
    
    public boolean isUsingAnimatedCape() {
        return this.usingAnimatedCape;
    }
    
    public boolean shouldBlurUI() {
        return this.shouldBlurUI;
    }
    
    public boolean isEveryoneMe() {
        return this.everyoneMe;
    }
    
    public SkinAPIType getSkinAPIType() {
        return this.skinAPIType;
    }
}
