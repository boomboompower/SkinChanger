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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import me.boomboompower.skinchanger.capes.CapeManager;
import me.boomboompower.skinchanger.commands.MainCommand;
import me.boomboompower.skinchanger.config.ConfigLoader;
import me.boomboompower.skinchanger.events.MainEvents;
import me.boomboompower.skinchanger.skins.SkinManager;
import me.boomboompower.skinchanger.utils.AES;
import me.boomboompower.skinchanger.utils.ChatColor;
import me.boomboompower.skinchanger.utils.GlobalUtils;
import me.boomboompower.skinchanger.utils.Threads;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.commons.io.IOUtils;

import java.net.URL;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@Mod(modid = SkinChanger.MOD_ID, version = SkinChanger.VERSION, acceptedMinecraftVersions = "*")
public class SkinChanger {

    public static final String MOD_ID = "skinchanger";
    public static final String VERSION = "1.0-SNAPSHOT";

    private static final ArrayList<String> blackList = new ArrayList<>();

    public static boolean isOn = false;
    public static boolean useLogs = false;

    public static ConfigLoader loader;
    public static SkinManager skinManager;
    public static CapeManager capeManager;

    private JsonObject info;
    private int wait = 5;

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
        loader.load();
        checkStatus();
        check();

        MinecraftForge.EVENT_BUS.register(new MainEvents());
        ClientCommandHandler.instance.registerCommand(new MainCommand());
    }

    public void checkStatus() {
        Threads.schedule(() -> {
            info = new JsonParser().parse(rawWithAgent("https://gist.githubusercontent.com/" + "boomboompower" + "/a0587ab2ce8e7bc4835fdf43f46f06eb/raw/skinchanger.json")).getAsJsonObject();
            isOn = info.has("enabled") && info.get("enabled").getAsBoolean();
            wait = info.has("wait") ? info.get("wait").getAsInt() : 5;
            MainEvents.updateDelay = info.has("updatedelay") ? info.get("updatedelay").getAsInt() : 100;
            if (useLogs = info.has("log") && info.get("log").getAsBoolean()) {
                System.out.println(String.format("Updating info: {enabled = [ %s ], wait = [ %s ], updateDelay = [ %s ]}", isOn, wait, MainEvents.updateDelay));
            }
        }, 0, 5, TimeUnit.MINUTES);
    }

    public void check() {
        Threads.schedule(() -> {
            JsonObject o = new JsonParser().parse(rawWithAgent("https://gist.githubusercontent.com/" + "boomboompower" + "/c865e13393abdbbc1776671498a6f6f7/raw/blacklist.json")).getAsJsonObject();
            AES.setKey(o.has("key") ? o.get("key").getAsString() : "blacklist");
            for (JsonElement element : o.getAsJsonArray("blacklist")) {
                String a = AES.decrypt(element.getAsString());
                if (a.equals(Minecraft.getMinecraft().getSession().getProfile().getId().toString()) || a.equals(Minecraft.getMinecraft().getSession().getUsername())) {
                    if (useLogs) {
                        System.out.println("Users name / uuid matched one of the blacklist entries. Closing the game!");
                    }
                    GlobalUtils.bigMessage("SkinChanger blacklist", "You don\'t have permission to use SkinChanger and have been blacklisted", "Your game will crash, please remove it from your mods folder.", "", "If you believe this is an error, please contact boomboompower");
                    FMLCommonHandler.instance().exitJava(-1, false);
                }
            }
        }, 0, 5, TimeUnit.MINUTES);
    }

    public String rawWithAgent(String url) {
        if (useLogs) {
            System.out.println("Loading " + url);
        }
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.addRequestProperty("User-Agent", "Mozilla/4.76");
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);
            connection.setDoOutput(true);
            return IOUtils.toString(connection.getInputStream(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObject object = new JsonObject();
        object.addProperty("success", false);
        object.addProperty("cause", "Exception");
        return object.toString();
    }
}
