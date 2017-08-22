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

package me.boomboompower.skinchanger;

import me.boomboompower.skinchanger.capes.CapeManager;
import me.boomboompower.skinchanger.commands.MainCommand;
import me.boomboompower.skinchanger.config.ConfigLoader;
import me.boomboompower.skinchanger.events.MainEvents;
import me.boomboompower.skinchanger.skins.SkinManager;
import me.boomboompower.skinchanger.utils.ChatColor;
import me.boomboompower.skinchanger.utils.WebsiteUtils;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = SkinChangerMod.MOD_ID, version = SkinChangerMod.VERSION, acceptedMinecraftVersions = "*")
public class SkinChangerMod {

    public static final String MOD_ID = "skinchanger";
    public static final String VERSION = "1.0-SNAPSHOT";

    private WebsiteUtils websiteUtils;
    private ConfigLoader loader;

    private SkinManager skinManager;
    private CapeManager capeManager;

    @Mod.Instance
    private static SkinChangerMod instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ModMetadata data = event.getModMetadata();
        data.description = ChatColor.AQUA + "An easy way to change your minecraft skin! (Clientside)";
        data.authorList.add("boomboompower");

        loader = new ConfigLoader(event.getSuggestedConfigurationFile());

        skinManager = new SkinManager(Minecraft.getMinecraft().thePlayer, true);
        capeManager = new CapeManager(Minecraft.getMinecraft().thePlayer, true);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new MainEvents());
        ClientCommandHandler.instance.registerCommand(new MainCommand());

        Minecraft.getMinecraft().addScheduledTask(() -> {
            websiteUtils.begin();
            loader.load();
        });
    }

    public SkinManager getSkinManager() {
        return this.skinManager;
    }

    public CapeManager getCapeManager() {
        return this.capeManager;
    }

    public ConfigLoader getLoader() {
        return this.loader;
    }

    public WebsiteUtils getWebsiteUtils() {
        return this.websiteUtils;
    }

    public static SkinChangerMod getInstance() {
        return instance;
    }
}
