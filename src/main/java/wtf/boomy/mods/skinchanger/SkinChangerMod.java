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
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import wtf.boomy.apagoge.ApagogeHandler;
import wtf.boomy.mods.skinchanger.commands.impl.SkinCommand;
import wtf.boomy.mods.skinchanger.configuration.ConfigurationHandler;
import wtf.boomy.mods.skinchanger.cosmetic.CosmeticFactory;
import wtf.boomy.mods.skinchanger.cosmetic.impl.SkinChangerStorage;
import wtf.boomy.mods.skinchanger.utils.ChatColor;
import wtf.boomy.mods.skinchanger.utils.backend.CacheRetriever;

import java.io.File;
import java.net.URISyntaxException;

/**
 * The Official SkinChanger mod
 *
 * @author boombompower
 * @version 3.0.0
 */
@Mod(modid = SkinChangerMod.MOD_ID, version = SkinChangerMod.VERSION, acceptedMinecraftVersions = "@MC_VERSION@", clientSideOnly = true, certificateFingerprint = "@FINGERPRINT@")
public class SkinChangerMod {
    
    public static final String MOD_ID = "skinchanger";
    public static final String VERSION = "@VERSION@";
    
    // Forge will instantiate this
    @Mod.Instance
    private static SkinChangerMod instance;
    
    private ConfigurationHandler configurationHandler;
    private CosmeticFactory cosmeticFactory;
    private CacheRetriever cacheRetriever;
    
    private File modConfigDirectory;
    
    private final SkinChangerStorage skinChangerStorage;
    private final ApagogeHandler apagogeHandler;
    
    // When forge creates a new instance of this class we need
    // to also build the storage component of the mod.
    public SkinChangerMod() throws URISyntaxException {
        this.skinChangerStorage = new SkinChangerStorage(this);
        this.apagogeHandler = new ApagogeHandler(new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()), "SkinChanger", SkinChangerMod.VERSION);
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
        
        this.configurationHandler.addAsSaveable(this.configurationHandler);
    }
    
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // Just a few classes, make sure they have the correct signature.
        this.apagogeHandler.addValidatorClasses(SkinChangerMod.class,
                SkinChangerStorage.class,
                SkinCommand.class,
                CacheRetriever.class,
                ConfigurationHandler.class,
                CosmeticFactory.class
        );
        
        this.apagogeHandler.addCompletionListener((handler, success) -> {
            if (!success) {
                System.err.println("Apagoge failed.");
            } else {
                System.err.println("Apagoge succeeded");
            }
        });
        
        ClientCommandHandler.instance.registerCommand(new SkinCommand(this));
    }
    
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        this.configurationHandler.load();
        
        this.cosmeticFactory = new CosmeticFactory(this);
        
        // This forces our patches to be done as the game loads
        new NetworkPlayerInfo((GameProfile) null);
    }
    
    @Mod.EventHandler
    public void onSignatureViolation(FMLFingerprintViolationEvent event) {
        System.err.println("Signature violation detected. Killing updater.");
        
        // Deletes updater & all data under it
        this.apagogeHandler.requestKill();
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
    public ConfigurationHandler getConfig() {
        return this.configurationHandler;
    }
    
    /**
     * Returns the factory containing all the cosmetic, bulky features of the mod.
     *
     * @return the cosmetic factory.
     */
    public CosmeticFactory getCosmeticFactory() {
        return this.cosmeticFactory;
    }
    
    /**
     * Returns the object containing all the smart caching objects.
     * <p>
     * Automatically saves and stores files retrieved from websites or Mojang's database to increase load times.
     *
     * @return the caching platform used by the mod.
     */
    public CacheRetriever getCacheRetriever() {
        return this.cacheRetriever;
    }
    
    /**
     * Returns the location where mod save files, capes and caches are all stored.
     *
     * @return defaults to the forge/skinchanger directory.
     */
    public File getModConfigDirectory() {
        return this.modConfigDirectory;
    }
    
    /**
     * Returns the mod updater instance, this may be null if the mod is running
     * in a beta environment (if the file hash cannot be checked)
     *
     * @return the mod updater instance or null if the build is not official.
     */
    public ApagogeHandler getApagogeHandler() {
        return this.apagogeHandler;
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
