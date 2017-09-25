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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import me.boomboompower.skinchanger.SkinChangerMod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import org.apache.commons.io.IOUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class WebsiteUtils {

    private Minecraft mc = Minecraft.getMinecraft();
    private AtomicInteger threadNumber = new AtomicInteger(0);

    private ExecutorService POOL = Executors.newFixedThreadPool(8, r -> new Thread(r, String.format("Thread %s", threadNumber.incrementAndGet())));
    private ScheduledExecutorService RUNNABLE_POOL = Executors.newScheduledThreadPool(2, r -> new Thread(r, "Thread " + threadNumber.incrementAndGet()));

    private boolean isRunning = false;
    private boolean isDisabled = false;

    private boolean hasSeenHigherMessage = false;
    private boolean higherVersion = false;
    private boolean needsUpdate = false;
    private String updateVersion = "0";

    private List<String> helperList = new ArrayList<>();

    private boolean experimental_changeAll = false;
    private boolean experimental_oldGui = false;
    private boolean useExperimental = false;

    private ScheduledFuture<?> experimentalChecker;
    private ScheduledFuture<?> modSettingsChecker;
    private ScheduledFuture<?> modVersionChecker;
    private ScheduledFuture<?> helperChecker;
    private ScheduledFuture<?> modBlacklist;

    private String modName = "";

    private final String SETTINGS_LINK = "https://gist.githubusercontent.com/boomboompower/a0587ab2ce8e7bc4835fdf43f46f06eb/raw/skinchanger.json";
    private final String VERSION_LINK = "https://gist.githubusercontent.com/boomboompower/a0587ab2ce8e7bc4835fdf43f46f06eb/raw/update.json";

    public WebsiteUtils(String modName) {
        MinecraftForge.EVENT_BUS.register(this);

        this.modName = modName;
    }

    public void begin() {
        if (!this.isRunning) {

            /*
             * All threads run every 5 minutes
             */

            this.modSettingsChecker = schedule(() -> {
                JsonObject statusObject = new JsonParser().parse(rawWithAgent(SETTINGS_LINK)).getAsJsonObject();
                if (!statusObject.has("enabled") || !statusObject.get("enabled").getAsBoolean()) {
                    disableMod();
                }
            }, 0, 5, TimeUnit.MINUTES);

            this.modVersionChecker = schedule(() -> {
                JsonObject object = new JsonParser().parse(rawWithAgent(VERSION_LINK)).getAsJsonObject();
                if (object.has("success") && object.get("success").getAsBoolean()) {
                    int currentVersion = formatVersion(SkinChangerMod.VERSION);
                    int latestVersion = object.has("latest-version") ? formatVersion(object.get("latest-version").getAsString()) : -1;
                    if (currentVersion < latestVersion && latestVersion > 0) {
                        this.needsUpdate = true;
                        this.updateVersion = object.has("latest-version") ? object.get("latest-version").getAsString() : "-1";
                    } else if (currentVersion > latestVersion && latestVersion > 0) {
                        this.higherVersion = true;
                    } else {
                        this.needsUpdate = false;
                        this.updateVersion = "-1";
                    }
                }
            }, 0, 5, TimeUnit.MINUTES);

            // Everything below is unneeded

            this.modBlacklist = schedule(() -> {
                JsonObject object = new JsonParser().parse(rawWithAgent("https://gist.githubusercontent.com/boomboompower/c865e13393abdbbc1776671498a6f6f7/raw/" + mc.getSession().getProfile().getId().toString() + ".json")).getAsJsonObject();
                if (!object.has("success")) {
                    disableMod();
                }
            }, 0, 5, TimeUnit.MINUTES);

            this.helperChecker = schedule(() -> {
                this.helperList.clear();
                JsonObject object = new JsonParser().parse(rawWithAgent("https://gist.githubusercontent.com/boomboompower/a0587ab2ce8e7bc4835fdf43f46f06eb/raw/helpers.json")).getAsJsonObject();
                if (object.has("success") && object.get("success").getAsBoolean() && object.has("ids")) {
                    for (JsonElement helper : object.get("ids").getAsJsonArray()) {
                        this.helperList.add(helper.getAsString());
                    }
                }
            }, 0, 5, TimeUnit.MINUTES);

            this.experimentalChecker = schedule(() -> {
                JsonObject object = new JsonParser().parse(rawWithAgent("https://gist.githubusercontent.com/boomboompower/a0587ab2ce8e7bc4835fdf43f46f06eb/raw/experimental.json")).getAsJsonObject();
                if (object.has("success") && object.get("success").getAsBoolean()) {
                    this.useExperimental = object.has("enabled") && object.get("enabled").getAsBoolean();
                    this.experimental_oldGui = object.has("old-gui") && object.get("old-gui").getAsBoolean();
                    this.experimental_changeAll = object.has("change-all") && object.get("change-all").getAsBoolean();
                } else {
                    this.useExperimental = false;
                }
            }, 0, 10, TimeUnit.MINUTES);
        } else {
            throw new IllegalStateException("WebsiteUtils is already running!");
        }
    }

    public void stop() {
        if (this.isRunning) {
            this.experimentalChecker.cancel(true);
            this.modSettingsChecker.cancel(true);
            this.modVersionChecker.cancel(true);
            this.helperChecker.cancel(true);
            this.modBlacklist.cancel(true);
        } else {
            throw new IllegalStateException("WebsiteUtils is not running!");
        }
    }

    public void runAsync(Runnable runnable) {
        this.POOL.execute(runnable);
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

    public List<String> getHelpers() {
        return this.helperList;
    }

    public ScheduledFuture<?> schedule(Runnable r, long initialDelay, long delay, TimeUnit unit) {
        return this.RUNNABLE_POOL.scheduleAtFixedRate(r, initialDelay, delay, unit);
    }

    // Experimental shit
    public boolean isExperimentsEnabled() {
        return this.useExperimental;
    }

    public boolean isChangeAllEnabled() {
        return this.experimental_changeAll;
    }

    public boolean isOldGuiEnabled() {
        return this.experimental_oldGui;
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
                sendMessage(" &b\u26AB &e" + this.modName + " is out of date!");
                sendMessage(" &b\u26AB &eDownload %s from the forum page!", "&6v" + utils.getUpdateVersion() + "&e");
                sendMessage(" ");
                sendMessage("&9&m---------------------------------------------");
            });
        }

        if (!this.hasSeenHigherMessage && utils.isRunningNewerVersion()) {
            this.hasSeenHigherMessage = true;
            utils.runAsync(() -> {
                try {
                    Thread.sleep(1000L);
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
                sendMessage(" &b\u26AB &aYou are running a newer version of " + this.modName +"!");
                sendMessage(" ");
                sendMessage("&9&m-----------------------------------------------");
            });
        }
    }

    private List<EntityOtherPlayerMP> get() {
        List<EntityOtherPlayerMP> ppl = new ArrayList<>();
        if (Minecraft.getMinecraft().theWorld != null && Minecraft.getMinecraft().theWorld.loadedEntityList != null) {
            for (Entity entity : Minecraft.getMinecraft().theWorld.loadedEntityList) {
                if (entity instanceof EntityOtherPlayerMP) {
                    ppl.add((EntityOtherPlayerMP) entity);
                }
            }
        }
        return ppl;
    }

    private ResourceLocation downloadCape(String url, String id) {
        if (url != null && !url.isEmpty()) {
            final ResourceLocation rl = new ResourceLocation("helpers/" + id);

            File file1 = new File(new File("./mods/skinchanger".replace("/", File.separator), "helpers"), id);
            File file2 = new File(file1, id + ".png");

            TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

            IImageBuffer imageBuffer = new IImageBuffer() {
                @Override
                public BufferedImage parseUserSkin(BufferedImage img) {
                    int imageWidth = 64;
                    int imageHeight = 32;
                    int srcWidth = img.getWidth();

                    for (int srcHeight = img.getHeight(); imageWidth < srcWidth || imageHeight < srcHeight; imageHeight *= 2) {
                        imageWidth *= 2;
                    }

                    BufferedImage imgNew = new BufferedImage(imageWidth, imageHeight, 2);
                    Graphics g = imgNew.getGraphics();
                    g.drawImage(img, 0, 0, null);
                    g.dispose();
                    return imgNew;
                }

                @Override
                public void skinAvailable() {
                }
            };
            ThreadDownloadImageData textureCape = new ThreadDownloadImageData(file2, url, null, imageBuffer);
            textureManager.loadTexture(rl, textureCape);

            return rl;
        } else {
            return null;
        }
    }

    private void sendMessage(String message, Object... replacements) {
        if (Minecraft.getMinecraft().thePlayer == null) return; // Safety first! :)

        try {
            message = String.format(message, replacements);
        } catch (Exception ex) { }
        Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(ChatColor.translateAlternateColorCodes('&', message)));
    }
}
