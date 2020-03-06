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

package me.do_you_like.mods.skinchanger.configuration;

import com.google.gson.JsonElement;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.do_you_like.mods.skinchanger.SkinChangerMod;
import me.do_you_like.mods.skinchanger.configuration.meta.ConfigurationData;
import me.do_you_like.mods.skinchanger.configuration.meta.SaveableClassData;
import me.do_you_like.mods.skinchanger.configuration.meta.SaveableField;
import me.do_you_like.mods.skinchanger.utils.general.BetterJsonObject;


public class ConfigurationHandler {

    @SaveableField(customName = "SuperSecretValue")
    private String specialValue = "HelloWorld";

    private final SkinChangerMod mod;
    private final File configFile;

    private final HashMap<Class<?>, ConfigurationData[]> saveableValues = new HashMap<>();

    public ConfigurationHandler(SkinChangerMod mod) {
        this.mod = mod;

        this.configFile = new File(mod.getModConfigDirectory(), "config.json");
    }

    public void save() {
        if (this.saveableValues.isEmpty()) {
            return;
        }

        BetterJsonObject saveObject = new BetterJsonObject();

        for (Map.Entry<Class<?>, ConfigurationData[]> o : this.saveableValues.entrySet()) {
            Class<?> clazz = o.getKey();

            // Where all our values will be saved to.
            BetterJsonObject classSpecific = new BetterJsonObject();

            // The list of fields in the class containing data.
            ConfigurationData[] dataList = o.getValue();

            // Loop through each data value.
            for (ConfigurationData data : dataList) {
                // Retrieves the value from the field.
                Object value = data.getValue();

                // Converts the field value into JSON.
                JsonElement serializedData = saveObject.getGsonData().toJsonTree(value);

                // Add the JSON to our JSON config.
                saveObject.getData().add(data.getSaveName(), serializedData);
            }

            String clazzSaveName = o.getKey().getName();

            if (clazz.isAnnotationPresent(SaveableClassData.class)) {
                clazzSaveName = clazz.getAnnotation(SaveableClassData.class).saveName();
            }

            // Adds that classes config module to the config file.
            saveObject.add(clazzSaveName, saveObject);
        }

        saveObject.writeToFile(this.configFile);
    }

    public void load() {

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
        }

        this.saveableValues.put(clazz.getClass(), storedData.toArray(new ConfigurationData[0]));

        if (!hasSaveableField) {
            System.out.println("Class \'%s\' was called through ConfigurationHandler#addAsSaveable however no @SaveableField annotations were present on any fields. It is advised to deregister that class as calling this method for no purpose may degrade performance. ");

            return;
        }
    }

    public SkinChangerMod getMod() {
        return this.mod;
    }

    public File getConfigFile() {
        return this.configFile;
    }
}
