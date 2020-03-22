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

package me.boomboompower.skinchanger.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import me.boomboompower.skinchanger.SkinChangerModOld;
import me.boomboompower.skinchanger.utils.models.skins.PlayerSkinType;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@SuppressWarnings("ALL")
@Deprecated
public class ConfigLoader {

    private File configFile;
    private JsonObject configJson;

    public ConfigLoader(File configFile) {
        this.configFile = configFile;
    }

    public void load() {
        if (exists()) {
            log("Config exists, attempting load...", this.configFile.getName());
            try {
                FileReader fileReader = new FileReader(this.configFile);
                BufferedReader reader = new BufferedReader(fileReader);
                StringBuilder builder = new StringBuilder();

                String current;
                while ((current = reader.readLine()) != null) {
                    builder.append(current);
                }
                this.configJson = new JsonParser().parse(builder.toString()).getAsJsonObject();
            } catch (Exception ex) {
                log("Could not read log properly, saving.", this.configFile.getName());
                save();
            }
            SkinChangerModOld
                .getInstance().getSkinManager().setSkinName(this.configJson.has("skinname") ? this.configJson.get("skinname").getAsString() : null);
            SkinChangerModOld
                .getInstance().getCapeManager().setUsingCape(this.configJson.has("usingcape") && this.configJson.get("usingcape").getAsBoolean());
            SkinChangerModOld.getInstance().getCapeManager().setExperimental(this.configJson.has("experimental") && this.configJson.get("experimental").getAsBoolean());
            SkinChangerModOld.getInstance().setRenderingEnabled(this.configJson.has("rendering") && this.configJson.get("rendering").getAsBoolean());
            if (this.configJson.has("experimental") && this.configJson.get("experimental").getAsBoolean() && this.configJson.has("ofCapeName")) {
                SkinChangerModOld
                    .getInstance().getCapeManager().giveOfCape(this.configJson.get("ofCapeName").getAsString());
            }

            if (this.configJson.has("mixins")) {
                JsonObject mixinSettings = this.configJson.getAsJsonObject("mixins");

                SkinChangerModOld
                    .getInstance().getSkinManager().setSkinType(mixinSettings.has("skinType") ? PlayerSkinType
                    .getTypeFromString(mixinSettings.get("skinType").getAsString()) : PlayerSkinType.STEVE);
            }
        } else {
            log("Config doesn\'t exist. Saving.", this.configFile.getName());
            save();
        }
    }

    public void save() {
        this.configJson = new JsonObject();
        try {
            this.configFile.createNewFile();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(this.configFile));
            this.configJson.addProperty("skinname", SkinChangerModOld.getInstance().getSkinManager().getSkinName());
            this.configJson.addProperty("usingcape", SkinChangerModOld.getInstance().getCapeManager().isUsingCape());
            this.configJson.addProperty("experimental", SkinChangerModOld.getInstance().getCapeManager().isExperimental());
            this.configJson.addProperty("rendering", SkinChangerModOld.getInstance().isRenderingEnabled());

            if (SkinChangerModOld.getInstance().getCapeManager().isExperimental()) {
                this.configJson.addProperty("ofCapeName", SkinChangerModOld.getInstance().getCapeManager().getOfCapeName());
            }

            JsonObject mixinSettings = new JsonObject();
            mixinSettings.addProperty("skinType", SkinChangerModOld.getInstance().getSkinManager().getSkinType().getDisplayName());

            this.configJson.add("mixins", mixinSettings);

            bufferedWriter.write(this.configJson.toString());
            bufferedWriter.close();
            log("Saved config.", this.configFile.getName());
        } catch (Exception ex) {
            log("Could not save.", this.configFile.getName());
            ex.printStackTrace();
        }
    }

    public boolean exists() {
        return Files.exists(Paths.get(this.configFile.getPath()));
    }

    public File getConfigFile() {
        return this.configFile;
    }

    protected void log(String message, Object... replace) {
        System.out.println(String.format("[%s] " + message, replace));
    }
}
