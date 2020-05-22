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

import lombok.Getter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.resources.IReloadableResourceManager;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import wtf.boomy.mods.skinchanger.commands.SkinCommand;
import wtf.boomy.mods.skinchanger.configuration.ConfigurationHandler;
import wtf.boomy.mods.skinchanger.cosmetic.CosmeticFactory;
import wtf.boomy.mods.skinchanger.cosmetic.impl.SkinChangerStorage;
import wtf.boomy.mods.skinchanger.utils.backend.CacheRetriever;
import wtf.boomy.mods.skinchanger.utils.backend.MojangHooker;
import wtf.boomy.mods.skinchanger.utils.game.ChatColor;

import java.io.File;

/**
 * The Official SkinChanger mod
 *
 * @author boombompower
 * @version 3.0.0
 */
@Mod(modid = SkinChangerMod.MOD_ID, version = SkinChangerMod.VERSION, acceptedMinecraftVersions = "*", clientSideOnly = true)
public class SkinChangerMod {

    public static final String MOD_ID = "skinchanger";
    public static final String VERSION = "3.0.0";

    // Forge will instantiate this
    @Mod.Instance
    private static SkinChangerMod instance;

    @Getter
    private ConfigurationHandler configurationHandler;

    @Getter
    private CosmeticFactory cosmeticFactory;

    @Getter
    private CacheRetriever cacheRetriever;

    @Getter
    private MojangHooker mojangHooker;

    @Getter
    private File modConfigDirectory;

    private final SkinChangerStorage skinChangerStorage;

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
        this.mojangHooker = new MojangHooker();

        // Hook Resource Reloads
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(r -> {
            System.out.println("Resources reloaded");
        });

        // This forces our patches to be done NOW!
        new NetworkPlayerInfo((GameProfile) null);
    }

    /**
     * Getter for the SkinChanger mod instance
     *
     * @return the SkinChanger mod instance.
     */
    public static SkinChangerMod getInstance() {
        return instance;
    }

    public SkinChangerStorage getStorage() {
        return this.skinChangerStorage;
    }
}
