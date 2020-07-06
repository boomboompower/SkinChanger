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

package wtf.boomy.mods.skinchanger;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.network.NetworkPlayerInfo;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import wtf.boomy.mods.skinchanger.api.SkinAPI;
import wtf.boomy.mods.skinchanger.api.impl.AshconHooker;
import wtf.boomy.mods.skinchanger.commands.SkinCommand;
import wtf.boomy.mods.skinchanger.configuration.ConfigurationHandler;
import wtf.boomy.mods.skinchanger.cosmetic.CosmeticFactory;
import wtf.boomy.mods.skinchanger.cosmetic.impl.SkinChangerStorage;
import wtf.boomy.mods.skinchanger.utils.backend.CacheRetriever;
import wtf.boomy.mods.skinchanger.utils.game.ChatColor;

import java.io.File;

/**
 * The Official SkinChanger mod
 *
 * @author boombompower
 * @version 3.0.0
 */
@Mod(modid = SkinChangerMod.MOD_ID, version = SkinChangerMod.VERSION, acceptedMinecraftVersions = "[1.8.8,1.8.9]", clientSideOnly = true)
public class SkinChangerMod {
    
    public static final String MOD_ID = "skinchanger";
    public static final String VERSION = "3.0.1";
    
    // Forge will instantiate this
    @Mod.Instance
    private static SkinChangerMod instance;
    
    private ConfigurationHandler configurationHandler;
    private CosmeticFactory cosmeticFactory;
    private CacheRetriever cacheRetriever;
    private SkinAPI skinAPI;
    
    private File modConfigDirectory;
    
    private final SkinChangerStorage skinChangerStorage;
    
    // When forge creates a new instance of this class we need
    // to also build the storage component of the mod.
    public SkinChangerMod() {
        this.skinChangerStorage = new SkinChangerStorage();
    }
    
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ModMetadata data = event.getModMetadata();
        data.description = ChatColor.AQUA + "A client-side mod that allows you to change your skin instantly!";
        data.authorList.add("boomboompower");
        data.version = SkinChangerMod.VERSION;
        
        this.modConfigDirectory = new File(event.getModConfigurationDirectory(), "skinchanger");
        
        this.cacheRetriever = new CacheRetriever(this);
        this.configurationHandler = new ConfigurationHandler(this);
    }
    
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        //MinecraftForge.EVENT_BUS.register(new MainEvents(this));
        ClientCommandHandler.instance.registerCommand(new SkinCommand(this));
    }
    
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        this.configurationHandler.load();
        
        this.cosmeticFactory = new CosmeticFactory(this);
        this.skinAPI = new AshconHooker();
        
        // Hook Resource Reloads
//        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(r -> {
//            System.out.println("Resources reloaded");
//        });
        
        // This forces our patches to be done NOW!
        new NetworkPlayerInfo((GameProfile) null);
    }
    
    /**
     * Returns the storage container containing the players custom skin, cape and skin type options
     *
     * @return the object containing skin & cape data.
     */
    public SkinChangerStorage getStorage() {
        return this.skinChangerStorage;
    }
    
    /**
     * Returns the configuration of the mod. Used to load and store variables.
     *
     * @return the config handler used by the mod.
     */
    public ConfigurationHandler getConfigurationHandler() {
        return configurationHandler;
    }
    
    /**
     * Returns the factory containing all the cosmetic, bulky features of the mod.
     *
     * @return the cosmetic factory.
     */
    public CosmeticFactory getCosmeticFactory() {
        return cosmeticFactory;
    }
    
    /**
     * Returns the object containing all the smart caching objects.
     *
     * Automatically saves and stores files retrieved from websites or Mojang's database to increase load times.
     *
     * @return the caching platform used by the mod.
     */
    public CacheRetriever getCacheRetriever() {
        return cacheRetriever;
    }
    
    /**
     * An instance of the Skin API specifically designed just for retrieving skin profiles from a username/uuid.
     *
     * @return a Skin API wrapper, see {@link SkinAPI}.
     */
    public SkinAPI getSkinAPI() {
        return skinAPI;
    }
    
    /**
     * Returns the location where mod save files, capes and caches are all stored.
     *
     * @return defaults to the forge/skinchanger directory.
     */
    public File getModConfigDirectory() {
        return modConfigDirectory;
    }
    
    /**
     * Getter for the SkinChanger mod instance
     *
     * @return the SkinChanger mod instance.
     */
    public static SkinChangerMod getInstance() {
        return instance;
    }
}
