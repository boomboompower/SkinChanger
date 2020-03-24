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

package me.do_you_like.mods.skinchanger;

import java.io.File;

import me.do_you_like.mods.skinchanger.commands.SkinCommand;
import me.do_you_like.mods.skinchanger.configuration.ConfigurationHandler;
import me.do_you_like.mods.skinchanger.utils.backend.MojangHooker;
import me.do_you_like.mods.skinchanger.utils.game.ChatColor;
import me.do_you_like.mods.skinchanger.utils.backend.CacheRetriever;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = SkinChangerMod.MOD_ID, version = SkinChangerMod.VERSION, acceptedMinecraftVersions = "*", clientSideOnly = true)
public class SkinChangerMod {

    public static final String MOD_ID = "skinchanger";
    public static final String VERSION = "3.0.0";

    @Mod.Instance
    private static SkinChangerMod instance;

    private ConfigurationHandler configurationHandler;
    private CacheRetriever cacheRetriever;
    private MojangHooker mojangHooker;

    private File modConfigDirectory;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ModMetadata data = event.getModMetadata();
        data.description = ChatColor.AQUA + "A client-side mod that allows you to change your skin instantly!";
        data.authorList.add("boomboompower");
        data.version = SkinChangerMod.VERSION;

        this.modConfigDirectory = new File(event.getModConfigurationDirectory(), "skinchanger");

        this.cacheRetriever = new CacheRetriever(this);
        this.configurationHandler = new ConfigurationHandler(this);

        for (int i = 0; i < 20; i++) {
            System.out.println((i * 'X') + " PreInit!");
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        //MinecraftForge.EVENT_BUS.register(new MainEvents(this));
        ClientCommandHandler.instance.registerCommand(new SkinCommand(this));
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        this.configurationHandler.load();

        this.mojangHooker = new MojangHooker();

        for (int i = 0; i < 20; i++) {
            System.out.println((i * 'Z') + " PostInit!");
        }
    }

    public File getModConfigDirectory() {
        return this.modConfigDirectory;
    }

    public ConfigurationHandler getConfigurationHandler() {
        return this.configurationHandler;
    }

    public CacheRetriever getCacheRetriever() {
        return this.cacheRetriever;
    }

    public MojangHooker getMojangHooker() {
        return this.mojangHooker;
    }

    public static SkinChangerMod getInstance() {
        return instance;
    }
}
