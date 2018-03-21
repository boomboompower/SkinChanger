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

package me.boomboompower.skinchanger.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import me.boomboompower.skinchanger.SkinChangerMod;

import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import org.apache.commons.io.IOUtils;

public class WebsiteUtils {

    private Minecraft mc = Minecraft.getMinecraft();
    private AtomicInteger threadNumber = new AtomicInteger(0);

    private ExecutorService POOL = Executors.newFixedThreadPool(8, r -> new Thread(r, String.format("WebsiteUtils Thread %s", threadNumber.incrementAndGet())));
    private ScheduledExecutorService RUNNABLE_POOL = Executors.newScheduledThreadPool(2, r -> new Thread(r, "WebsiteUtils Thread " + threadNumber.incrementAndGet()));

    private boolean isRunning = false;
    
    private boolean isDisabled = false;
    
    private LinkedList<String> updateMessage = new LinkedList<>();
    private boolean hasSeenHigherMessage = false;
    private boolean showUpdateSymbol = true;
    private boolean showUpdateHeader = true;
    private boolean higherVersion = false;
    private boolean needsUpdate = false;
    private String updateVersion = "0";

    private ScheduledFuture<?> modVersionChecker;

    private final String modName;
    private final String sessionId;

    private final String BASE_LINK = "https://gist.githubusercontent.com/boomboompower/a0587ab2ce8e7bc4835fdf43f46f06eb/raw";

    public WebsiteUtils(String modName) {
        MinecraftForge.EVENT_BUS.register(this);

        this.modName = modName;
//        this.sessionId = Minecraft.getMinecraft().getSession().getProfile().getId().toString();
        this.sessionId = "boomboompowerisbad";
    }

    public void begin() {
        if (!this.isRunning) {
            this.isRunning = true;
            
            /*
             * All threads run every 5 minutes
             */

            this.modVersionChecker = schedule(() -> {
                // Disable the update checker
                if (this.isDisabled) {
                    return;
                }
                
                // Below is a prime example of unreadable, bad code. This will be rewritten in a later release.
    
                JsonObject object = new JsonParser().parse(rawWithAgent(this.BASE_LINK + "/" + this.sessionId + ".json")).getAsJsonObject();

                if (object.has("success") && !object.get("success").getAsBoolean()) {
                    
                    object = new JsonParser().parse(rawWithAgent(this.BASE_LINK)).getAsJsonObject();
                }
                
                // Test two, this is to test the new grabbed url
                if (object.has("success") && !object.get("success").getAsBoolean()) {
                    // Test if the json has a fallback url
                    if (object.has("url")) {
                        System.out.println("Updater found a fallback url, using it... " + object.get("url").getAsString());
                        object = new JsonParser().parse(rawWithAgent(object.get("url").getAsString())).getAsJsonObject();
                        object.addProperty("generatedFallbackValue", true);
                    } else {
                        // Disabling because a second error occured while grabbing the normal url
                        disableMod();
                        return;
                    }
                }
                
                if (object.has("success") && !object.get("success").getAsBoolean() && object.has("generatedFallbackValue")) {
                    System.out.println("Fallback url failed. Halting the updater");
                    disableMod();
                    return;
                }
                
                // Disables the mod
                if (!object.has("enabled") || !object.get("enabled").getAsBoolean()) {
                    disableMod();
                }
    
                // Tells the mod to use the updater symbol or not
                if (object.has("showupdatesymbol")) {
                    this.showUpdateSymbol = object.get("showupdatesymbol").getAsBoolean();
                }
    
                // Sets the seenhigherversion variable
                if (object.has("seenhigherversion")) {
                    this.hasSeenHigherMessage = object.get("seenhigherversion").getAsBoolean();
                }
    
                if (object.has("updateheader")) {
                    this.showUpdateHeader = object.get("updateheader").getAsBoolean();
                }
    
                int currentVersion = formatVersion(SkinChangerMod.VERSION);
                int latestVersion = object.has("latest-version") ? formatVersion(object.get("latest-version").getAsString()) : -1;
                
                if (currentVersion < latestVersion && latestVersion > 0) {
                    this.needsUpdate = true;
                    this.updateVersion = object.has("latest-version") ? object.get("latest-version").getAsString() : "-1";
        
                    if (object.has("update-message") && object.get("update-message").isJsonArray()) {
                        LinkedList<String> update = new LinkedList<>();
                        JsonArray array = object.get("update-message").getAsJsonArray();
            
                        for (JsonElement element : array) {
                            update.add(element.getAsString());
                        }
            
                        if (!update.isEmpty()) {
                            this.updateMessage = update;
                        }
                    }
                } else if (currentVersion > latestVersion && latestVersion > 0) {
                    this.higherVersion = true;
                } else {
                    this.needsUpdate = false;
                    this.updateVersion = "-1";
                }
            }, 0, 5, TimeUnit.MINUTES);
        } else {
            throw new IllegalStateException("WebsiteUtils is already running!");
        }
    }

    public void stop() {
        if (this.isRunning) {
            this.isRunning = false;
            
            this.modVersionChecker.cancel(true);
        } else {
            throw new IllegalStateException("WebsiteUtils is not running!");
        }
    }
    
    public void disableMod() {
        this.isDisabled = true;
    }
    
    public boolean isDisabled() {
        return this.isDisabled;
    }
    
    public boolean isRunning() {
        return this.isRunning;
    }

    public boolean isRunningNewerVersion() {
        return this.higherVersion;
    }

    public boolean needsUpdate() {
        return this.needsUpdate;
    }

    public String getUpdateVersion() {
        return this.updateVersion;
    }
    
    public void runAsync(Runnable runnable) {
        this.POOL.execute(runnable);
    }
    
    public ScheduledFuture<?> schedule(Runnable r, long initialDelay, long delay, TimeUnit unit) {
        return this.RUNNABLE_POOL.scheduleAtFixedRate(r, initialDelay, delay, unit);
    }

    // Other things

    public String rawWithAgent(String url) {
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
            JsonObject object = new JsonObject();
            object.addProperty("success", false);
            object.addProperty("cause", "Exception");
            return object.toString();
        }
    }

    private int formatVersion(String input) {
        StringBuilder builder = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (Character.isDigit(c)) {
                builder.append(c);
            }
        }
        return builder.toString().trim().isEmpty() ? 0 : Integer.valueOf(builder.toString().trim());
    }
    
    // Handle message sending
    
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onJoin(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        WebsiteUtils utils = SkinChangerMod.getInstance().getWebsiteUtils();
        
        if (utils.isDisabled()) return;
        
        if (utils.needsUpdate()) {
            utils.runAsync(() -> {
                try {
                    Thread.sleep(3000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (Minecraft.getMinecraft().thePlayer == null) {
                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                sendMessage("&9&m---------------------------------------------");
                sendMessage(" ");
                if (this.showUpdateHeader) {
                    sendMessage(" %s&eYour version of " + this.modName + " is out of date!", (this.showUpdateSymbol ? "&b\u21E8 " : ""));
                    sendLinkText();
                }
                if (this.updateMessage != null && !this.updateMessage.isEmpty()) {
                    if (this.showUpdateHeader) {
                        sendMessage(" %s", (this.showUpdateSymbol ? "&b\u21E8 " : ""));
                    }
                    for (String s : this.updateMessage) {
                        sendMessage(" %s&e" + s, (this.showUpdateSymbol ? "&b\u21E8 " : ""));
                    }
                }
                sendMessage(" ");
                sendMessage("&9&m---------------------------------------------");
            });
        }
        
        if (!this.hasSeenHigherMessage && utils.isRunningNewerVersion()) {
            this.hasSeenHigherMessage = true;
            utils.runAsync(() -> {
                try {
                    Thread.sleep(3000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (Minecraft.getMinecraft().thePlayer == null) {
                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                sendMessage("&9&m-----------------------------------------------");
                sendMessage(" ");
                sendMessage(" &b\u21E8 &aYou are running a newer version of " + this.modName +"!");
                sendMessage(" ");
                sendMessage("&9&m-----------------------------------------------");
            });
        }
    }
    
    private void sendMessage(String message, Object... replacements) {
        if (Minecraft.getMinecraft().thePlayer == null) return; // Safety first! :)
        
        try {
            message = String.format(message, replacements);
        } catch (Exception ex) { }
        Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(ChatColor.translateAlternateColorCodes('&', message)));
    }
    
    private void sendLinkText() {
        if (Minecraft.getMinecraft().thePlayer == null) return; // Safety first! :)
        
        try {
            ChatComponentText text = new ChatComponentText(ChatColor.translateAlternateColorCodes(String.format(" %s&eYou can download v&6%s&e by ", (this.showUpdateSymbol ? "&b\u21E8 " : ""), this.updateVersion)));
            ChatComponentText url = new ChatComponentText(ChatColor.GREEN + "clicking here");
            
            ChatStyle chatStyle = new ChatStyle();
            chatStyle.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(ChatColor.AQUA + "Click here to open the forum thread!")));
            chatStyle.setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://hypixel.net/threads/1244732/"));
            url.setChatStyle(chatStyle);
            text.appendSibling(url).appendText(ChatColor.YELLOW + "!");
            
            Minecraft.getMinecraft().thePlayer.addChatComponentMessage(text);
        } catch (Exception ex) {
        }
    }
}
