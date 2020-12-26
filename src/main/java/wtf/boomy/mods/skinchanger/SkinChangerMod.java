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
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import wtf.boomy.apagoge.ApagogeHandler;
import wtf.boomy.mods.skinchanger.commands.impl.SkinCommand;
import wtf.boomy.mods.skinchanger.configuration.ConfigurationHandler;
import wtf.boomy.mods.skinchanger.utils.cache.InternalCache;
import wtf.boomy.mods.skinchanger.utils.cosmetic.CosmeticFactory;
import wtf.boomy.mods.skinchanger.utils.cosmetic.impl.SkinChangerStorage;
import wtf.boomy.mods.skinchanger.locale.Language;
import wtf.boomy.mods.skinchanger.utils.ChatColor;

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
    private InternalCache internalCache;
    
    private File modConfigDirectory;
    
    private final SkinChangerStorage skinChangerStorage = new SkinChangerStorage(this);
    private final Logger logger = LogManager.getLogger("SkinChanger - Core");
    private final ApagogeHandler apagogeHandler;
    
    /**
     * A basic constructor for the mod.
     *
     * @throws URISyntaxException an exception thrown when Java is unable to locate the code source.
     */
    public SkinChangerMod() throws URISyntaxException {
        this.apagogeHandler = new ApagogeHandler(new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()), "SkinChanger", SkinChangerMod.VERSION);
    }
    
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ModMetadata data = event.getModMetadata();
        data.description = ChatColor.AQUA + "A client-side mod that allows you to change your skin instantly!";
        data.authorList.add("boomboompower");
        data.version = SkinChangerMod.VERSION;
        
        this.modConfigDirectory = new File(event.getModConfigurationDirectory(), "skinchanger");
        
        this.configurationHandler = new ConfigurationHandler(this);
        this.internalCache = new InternalCache(this);
        
        this.configurationHandler.addAsSaveable(this.configurationHandler);
    }
    
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // Requests for Apagoge to check these files. This is only a request
        // and may be ignored in some implementations of Apagoge.
        this.apagogeHandler.addValidatorClasses(
                SkinChangerMod.class,
                SkinChangerStorage.class,
                SkinCommand.class,
                InternalCache.class,
                ConfigurationHandler.class,
                CosmeticFactory.class,
                Language.class
        );
        
        // Called once apagoge has determined if the build succeeded or not
        // if no instance of Apagoge is available this will be called
        // with a failure code. This can by bypassed by using the internal
        // hook and not the handler. With ApagogeHandler#getUpdater() which
        // will return the internal ApagogeVerifier instance (or null if
        // it has either been destroyed or cannot be found).
        this.apagogeHandler.addCompletionListener((handler, success) -> {
            if (!success) {
                if (handler.getUpdater() == null) {
                    this.logger.error("Apagoge was unable to run, no updater was found.");
                } else {
                    this.logger.error("Apagoge failed. Assuming invalid build.");
                }
            } else {
                this.logger.trace("Apagoge succeeded. This build is official.");
            }
        });
        
        ClientCommandHandler.instance.registerCommand(new SkinCommand(this));
    }
    
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        this.configurationHandler.load();
        this.internalCache.init();
        
        this.cosmeticFactory = new CosmeticFactory(this);
        
        // Load the translation system!
        Language.loadTranslations();
        
        // This forces our patches to be done as the game loads
        new NetworkPlayerInfo((GameProfile) null);
        
        // Configurable, this will only happen if the user explicitly
        // chooses to disable the updater. By default it will run.
        if (this.configurationHandler.shouldRunUpdater()) {
            // Ask to start. If no implementation is found this will do nothing.
            this.apagogeHandler.begin();
        }
    }
    
    @Mod.EventHandler
    public void onSignatureViolation(FMLFingerprintViolationEvent event) {
        this.logger.warn("Signature violation detected. SkinChanger is NOT running an official release.");
        this.logger.warn("This may be a sign the mod has been modified, or a dev build is being ran");
        this.logger.warn("The only official place to get SkinChanger safely is from https://mods.boomy.wtf/");
        this.logger.warn("or from the github page located at https://github.com/boomboompower/SkinChanger/");
        
        // Requests the updater to destroy itself.
        // Depending on the implementation this can be ignored.
        // We don't use the handler case for it, since it also
        // makes the updater instance null.
        if (this.apagogeHandler.getUpdater() != null) this.apagogeHandler.getUpdater().kill();
    }
    
    /**
     * Version independent event registering. 1.7 does not
     * use the same event bus as 1.8 and above.
     *
     * @param target the object to register events under.
     */
    public void registerEvents(Object target) {
        // noinspection ConstantConditions
        if (ForgeVersion.mcVersion.startsWith("1.7")) {
            FMLCommonHandler.instance().bus().register(target);
        } else {
            MinecraftForge.EVENT_BUS.register(target);
        }
    }
    
    /**
     * Version independent event registering. 1.7 does not
     * use the same event bus as 1.8 and above.
     *
     * @param target the object to deregister events under.
     */
    public void unregisterEvents(Object target) {
        // noinspection ConstantConditions
        if (ForgeVersion.mcVersion.startsWith("1.7")) {
            FMLCommonHandler.instance().bus().unregister(target);
        } else {
            MinecraftForge.EVENT_BUS.unregister(target);
        }
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
     * Returns the instance for SkinChanger internal cache. Superseding the original "CacheRetriever"
     * object. Performs most operations at the startup of the game.
     *
     * @return the caching platform used by the mod.
     */
    public InternalCache getInternalCache() {
        return this.internalCache;
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
