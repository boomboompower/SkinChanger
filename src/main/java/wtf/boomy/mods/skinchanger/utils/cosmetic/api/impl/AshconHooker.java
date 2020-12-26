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

package wtf.boomy.mods.skinchanger.utils.cosmetic.api.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;

import org.apache.commons.io.FileUtils;

import wtf.boomy.mods.skinchanger.SkinChangerMod;
import wtf.boomy.mods.skinchanger.gui.SkinChangerMenu;
import wtf.boomy.mods.skinchanger.utils.cache.CacheType;
import wtf.boomy.mods.skinchanger.utils.cosmetic.api.SkinAPI;
import wtf.boomy.mods.skinchanger.utils.cosmetic.options.SimpleCallback;
import wtf.boomy.mods.skinchanger.utils.BetterJsonObject;
import wtf.boomy.mods.skinchanger.utils.LowerCaseHashMap;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;

/**
 * See https://github.com/Electroid/mojang-api
 * <p>
 * A website to combine multiple Mojang API requests into one
 *
 * @author boomboompower
 */
public class AshconHooker extends SkinAPI {
    
    // https://api.ashcon.app/mojang/v2/user/[username|uuid]
    
    private final HashMap<String, BetterJsonObject> storedData = new LowerCaseHashMap<>();     // Store raw data from Ashcon
    private final LowerCaseHashMap<String, ResourceLocation> skins = new LowerCaseHashMap<>(); // Store skin locations
    
    public AshconHooker() { }
    
    @Override
    public String getIdFromUsername(String userName) {
        if (userName == null) {
            return "";
        }
        
        BetterJsonObject data = getData(userName);
    
        if (data.has("success") && !(data.get("success").getAsBoolean())) {
            return userName;
        }
    
        return data.has("uuid") ? data.get("uuid").getAsString() : userName;
    }
    
    @Override
    public String getNameFromID(String uuid) {
        if (uuid == null) {
            return "Steve";
        }
    
        BetterJsonObject data = getData(uuid);
    
        if (data.has("success") && !(data.get("success").getAsBoolean())) {
            return "Steve";
        }
    
        return data.has("username") ? data.get("username").getAsString() : "Steve";
    }
    
    @Override
    public String getRealNameFromName(String userName) {
        if (userName == null) {
            return "";
        }
        
        BetterJsonObject data = getData(userName);
        
        if (data == null) {
            data = doAPIRequest(userName);
        }
        
        if (data.has("success") && !(data.get("success").getAsBoolean())) {
            return userName;
        }
        
        return data.has("username") ? data.get("username").getAsString() : userName;
    }
    
    @Override
    public void getSkinFromId(String playerId, SimpleCallback<ResourceLocation> callback) {
        if (playerId == null || playerId.isEmpty()) {
            callback.run(DefaultPlayerSkin.getDefaultSkinLegacy());
            
            return;
        }
    
        if (this.skins.containsKey(playerId)) {
            ResourceLocation loc = this.skins.get(playerId);
        
            // Test if the resource is still loaded
            if (Minecraft.getMinecraft().getTextureManager().getTexture(loc) != null) {
                callback.run(loc);
                
                return;
            } else {
                this.skins.remove(playerId);
            }
        }
    
        BetterJsonObject data = getData(playerId);
    
        if (data.has("success") && !(data.get("success").getAsBoolean())) {
            callback.run(DefaultPlayerSkin.getDefaultSkinLegacy());
            
            return;
        }
        
        if (!data.has("textures") || !data.get("textures").isJsonObject() || !data.get("textures").getAsJsonObject().has("skin")) {
            callback.run(DefaultPlayerSkin.getDefaultSkinLegacy());
    
            return;
        }
        
        // Represents the base64 encodes PNG file for the player skin
        String base64data = data.getChild("textures").getChild("skin").optString("data");
    
        File memes = createCachedBase64(playerId, base64data);
        
        SkinChangerMod.getInstance().getCosmeticFactory().getResourceLoader().loadFileDirectly(memes, CacheType.SKIN, o -> {
            this.skins.put(playerId, o);
            
            callback.run(o);
        });
    }
    
    @Override
    public boolean hasSlimSkin(String userName) {
        BetterJsonObject data = getData(userName);
    
        if (data.has("success") && !(data.get("success").getAsBoolean())) {
            return false;
        }
        
        return data.has("textures") && data.get("textures").isJsonObject() && data.get("textures").getAsJsonObject().get("slim").getAsBoolean();
    }
    
    private BetterJsonObject getData(String nameOrUUID) {
        BetterJsonObject data;
    
        if (!this.storedData.containsKey(nameOrUUID)) {
            data = doAPIRequest(nameOrUUID);
        } else {
            data = this.storedData.get(nameOrUUID);
        }
        
        if (data == null) {
            data = doAPIRequest(nameOrUUID);
        }
        
        return data;
    }
    
    private BetterJsonObject doAPIRequest(String nameOrUUID) {
        // Construct URL
        String url = String.format("https://api.ashcon.app/mojang/v2/user/%s", nameOrUUID);
        
        // Make the json object for it
        BetterJsonObject object = new BetterJsonObject(getUrl(url));
        
        this.storedData.put(nameOrUUID, object);
        
        return object;
    }
    
    public File createCachedBase64(String name, String base64data) {
        try {
            // Represents the decoded base64 content
            byte[] rawData = Base64.getDecoder().decode(base64data);
            
            File cacheType = SkinChangerMod.getInstance().getInternalCache().determineCacheHome(CacheType.SKIN);
            File cacheDir = new File(cacheType, name);
            
            File cacheFile = new File(cacheDir, "data.png");
            
            FileUtils.writeByteArrayToFile(cacheFile, rawData);
    
            SkinChangerMod.getInstance().getInternalCache().populateCacheDirectory(cacheDir, false);
            
            return cacheFile;
        } catch (IllegalArgumentException | IOException ex) {
            ex.printStackTrace();
            
            return null;
        }
    }
}
