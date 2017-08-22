/*
 *     Copyright (C) 2017 boomboompower
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

package me.boomboompower.skinchanger.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import me.boomboompower.skinchanger.SkinChangerMod;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConfigLoader {

    private File configFile;
    private JsonObject configJson;

    public ConfigLoader(File configFile) {
        this.configFile = configFile;
    }

    public void load() {
        if (exists()) {
            log("Config exists, attempting load...", configFile.getName());
            try {
                FileReader fileReader = new FileReader(configFile);
                BufferedReader reader = new BufferedReader(fileReader);
                StringBuilder builder = new StringBuilder();

                String current;
                while ((current = reader.readLine()) != null) {
                    builder.append(current);
                }
                configJson = new JsonParser().parse(builder.toString()).getAsJsonObject();
            } catch (Exception ex) {
                log("Could not read log properly, saving.", configFile.getName());
                save();
            }
            SkinChangerMod.getInstance().getSkinManager().setSkinName(configJson.has("skinname") ? configJson.get("skinname").getAsString() : null);
            SkinChangerMod.getInstance().getCapeManager().setUsingCape(configJson.has("usingcape") && configJson.get("usingcape").getAsBoolean());
        } else {
            log("Config doesn\'t exist. Saving.", configFile.getName());
            save();
        }
    }

    public void save() {
        configJson = new JsonObject();
        try {
            configFile.createNewFile();
            FileWriter writer = new FileWriter(configFile);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            configJson.addProperty("skinname", SkinChangerMod.getInstance().getSkinManager().getSkinName());
            configJson.addProperty("usingcape", SkinChangerMod.getInstance().getCapeManager().isUsingCape());

            bufferedWriter.write(configJson.toString());
            bufferedWriter.close();
            writer.close();
            log("Saved config.", configFile.getName());
        } catch (Exception ex) {
            log("Could not save.", configFile.getName());
            ex.printStackTrace();
        }
    }

    public boolean exists() {
        return Files.exists(Paths.get(configFile.getPath()));
    }

    public File getConfigFile() {
        return this.configFile;
    }

    protected void log(String message, Object... replace) {
        System.out.println(String.format("[%s] " + message, replace));
    }
}
