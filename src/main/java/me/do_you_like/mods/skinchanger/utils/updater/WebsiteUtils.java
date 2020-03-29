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

package me.do_you_like.mods.skinchanger.utils.updater;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import me.do_you_like.mods.skinchanger.SkinChangerMod;
import me.do_you_like.mods.skinchanger.utils.game.ChatColor;

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

/**
 * WebsiteUtils, an UpdateChecker created by boomboompower, using json loading
 *
 * @author boomboompower
 * @version 3.1
 * 
 * @deprecated to be removed in a future release
 */
@Deprecated
public class WebsiteUtils {

    @Deprecated
    private final Minecraft mc = Minecraft.getMinecraft(); // The Minecraft instance
    @Deprecated
    private final AtomicInteger threadNumber = new AtomicInteger(0); // The current ThreadCount
    
    @Deprecated
    private final ExecutorService POOL = Executors.newFixedThreadPool(8, r -> new Thread(r, String
        .format("WebsiteUtils Thread %s",
            this.threadNumber.incrementAndGet()))); // Async task scheduler
    
    @Deprecated
    private final ScheduledExecutorService RUNNABLE_POOL = Executors.newScheduledThreadPool(2,
        r -> new Thread(r, "WebsiteUtils Thread " + this.threadNumber
            .incrementAndGet())); // Repeating task scheduler
    
    @Deprecated
    private boolean isRunning = false; // Is the checker running?
    @Deprecated
    private boolean isDisabled = false; // Is the mod disabled
    
    @Deprecated
    private LinkedList<String> updateMessage = new LinkedList<>(); // A list of messages to send to the player
    @Deprecated
    private boolean hasSeenHigherMessage = false; // true if the user should be alerted for having a newer release
    @Deprecated
    private boolean showUpdateSymbol = true; // true if a arrow should be shown before every update message
    @Deprecated
    private boolean showUpdateHeader = true; // true if the updater should show "this mod is out of date"
    @Deprecated
    private boolean higherVersion = false; // Is this mod newer than the latest released version?
    @Deprecated
    private boolean needsUpdate = false; // Is this mod an older version than the latest released version?
    @Deprecated
    private String updateVersion = "0"; // The newest version availible to download

    @Deprecated
    private ScheduledFuture<?> modVersionChecker; // The repeating runnable for version checking

    @Deprecated
    private final String modName; // The id of this mod
    @Deprecated
    private final String sessionId; // The uuid of the player
    
    // The base link of the site, this can be changed whenever required
    @Deprecated
    private final String BASE_LINK = "https://gist.githubusercontent.com/boomboompower/a0587ab2ce8e7bc4835fdf43f46f06eb/raw";

    @Deprecated
    public WebsiteUtils(String modName) {
        MinecraftForge.EVENT_BUS.register(this);

        this.modName = modName;
        this.sessionId = Minecraft.getMinecraft().getSession().getProfile().getId().toString();
    }
    
    /**
     * Begins the WebsiteUtils updater service, starts all repeating threads,
     * this can only be used if the service is not already running.
     *
     * @throws IllegalStateException if the service is already running
     */
    @Deprecated
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
    
    /**
     * Stops the WebsiteUtils service. Cancels all running tasks and erases the variables
     *
     * @throws IllegalStateException if the service is not running
     */
    @Deprecated
    public void stop() {
        if (this.isRunning) {
            this.isRunning = false;
            
            this.modVersionChecker.cancel(true);
            this.modVersionChecker = null;
        } else {
            throw new IllegalStateException("WebsiteUtils is not running!");
        }
    }
    
    /**
     * Disables the mod
     */
    @Deprecated
    public void disableMod() {
        this.isDisabled = true;
    }
    
    /**
     * Getter for the isDisabled field
     *
     * @return true if the mod should be disabled
     */
    @Deprecated
    public boolean isDisabled() {
        return this.isDisabled;
    }
    
    /**
     * Getter for the isRunning field
     *
     * @return true if this service is running
     */
    @Deprecated
    public boolean isRunning() {
        return this.isRunning;
    }
    
    /**
     * A getter for the higher version field, which will be
     * true if this mod is newer than the latest released version
     *
     * @return true if the version running is newer than the latest release
     */
    @Deprecated
    public boolean isRunningNewerVersion() {
        return this.higherVersion;
    }
    
    /**
     * Checks to see if this mod needs an update
     *
     * @return true if this mod version is older than the newest one
     */
    @Deprecated
    public boolean needsUpdate() {
        return this.needsUpdate;
    }
    
    /**
     * Getter for the latest availible version of the mod
     *
     * @return the latest version or -1 if not availible
     */
    @Deprecated
    public String getUpdateVersion() {
        return this.updateVersion;
    }
    
    /**
     * Runs a task async to the main thread
     *
     * @param runnable the runnable to run
     */
    @Deprecated
    public void runAsync(Runnable runnable) {
        this.POOL.execute(runnable);
    }
    
    /**
     * Schedules a repeating task that can be cancelled at any time
     *
     * @param r the runnable to run
     * @param initialDelay the delay for the first time ran
     * @param delay all the other delays
     * @param unit the time duration type for the task to be executed, eg
     * a delay of 50 with {@link TimeUnit#MILLISECONDS} will run the task every
     * 50 milliseconds
     *
     * @return the scheduled task
     */
    @Deprecated
    public ScheduledFuture<?> schedule(Runnable r, long initialDelay, long delay, TimeUnit unit) {
        return this.RUNNABLE_POOL.scheduleAtFixedRate(r, initialDelay, delay, unit);
    }

    // Other things
    
    /**
     * Grabs JSON off a site, will return its own JSON if an error occurs,
     * the format for an error is usually <i>{"success":false,"cause":"exception"}</i>
     *
     * @param url the url to grab the json off
     * @return the json recieved
     */
    @Deprecated
    public String rawWithAgent(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.addRequestProperty("User-Agent", "Mozilla/4.76");
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);
            connection.setDoOutput(true);
            return IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            
            // Generic handling for bad errors, captures the error type and message (if specified)
            JsonObject object = new JsonObject();
            object.addProperty("success", false);
            object.addProperty("cause", "Exception");
            object.addProperty("exception_type", e.getClass().getName());
            if (e.getMessage() != null) {
                object.addProperty("exception_message", e.getMessage());
            }
            return object.toString();
        }
    }
    
    /**
     * Strips all character that are not digits in the version input,
     * this is a quick solution to update checking. Will probably not be
     * used in newer versions
     *
     * @param input the verision input
     * @return an integer for the string, or 0 if empty
     */
    @Deprecated
    private int formatVersion(String input) {
        StringBuilder builder = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (Character.isDigit(c)) {
                builder.append(c);
            }
        }
        return builder.toString().trim().isEmpty() ? 0 : Integer.parseInt(builder.toString().trim());
    }
    
    // Handle message sending
    
    @SubscribeEvent(priority = EventPriority.LOW)
    @Deprecated
    public void onJoin(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        WebsiteUtils utils = this;
        
        if (utils.isDisabled()) return;
        
        if (utils.needsUpdate()) {
            utils.runAsync(() -> {
                sleep();

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
                sleep();

                sendMessage("&9&m-----------------------------------------------");
                sendMessage(" ");
                sendMessage(" &b\u21E8 &aYou are running a newer version of " + this.modName +"!");
                sendMessage(" ");
                sendMessage("&9&m-----------------------------------------------");
            });
        }
    }

    @SuppressWarnings("BusyWait")
    private void sleep() {
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
    }

    /**
     * Sends a message to the player, this supports color codes
     *
     * @param message the message to send
     * @param replacements the arguments used to format the string
     */
    @Deprecated
    private void sendMessage(String message, Object... replacements) {
        if (Minecraft.getMinecraft().thePlayer == null) return; // Safety first! :)
        
        try {
            message = String.format(message, replacements);
        } catch (Exception ignored) { }
        Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(ChatColor.translateAlternateColorCodes('&', message)));
    }
    
    /**
     * Sends a clickable link to the user containing all updating information
     */
    @Deprecated
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
        } catch (Exception ignored) {
        }
    }
}
