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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import me.boomboompower.skinchanger.SkinChangerMod;

import net.minecraft.client.Minecraft;

import org.apache.commons.io.IOUtils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class WebsiteUtils {

    private Minecraft mc = Minecraft.getMinecraft();
    private AtomicInteger threadNumber = new AtomicInteger(0);

    private ExecutorService POOL = Executors.newFixedThreadPool(8, r -> new Thread(r, String.format("Thread %s", threadNumber.incrementAndGet())));
    private ScheduledExecutorService RUNNABLE_POOL = Executors.newScheduledThreadPool(2, r -> new Thread(r, "Thread " + threadNumber.incrementAndGet()));

    private boolean isRunning = false;
    private boolean isDisabled;

    private boolean higherVersion = false;
    private boolean needsUpdate = false;
    private String updateVersion = "0";

    private ScheduledFuture<?> modSettingsChecker;
    private ScheduledFuture<?> modVersionChecker;
    private ScheduledFuture<?> modBlacklist;

    public void begin() {
        if (!isRunning) {
            // Checks to see if the mod is enabled, runs every 5 minutes
            modSettingsChecker = schedule(() -> {
                JsonObject statusObject = new JsonParser().parse(rawWithAgent("https://gist.githubusercontent.com/boomboompower/a0587ab2ce8e7bc4835fdf43f46f06eb/raw/skinchanger.json")).getAsJsonObject();
                if (!statusObject.has("enabled") || !statusObject.get("enabled").getAsBoolean()) {
                    disableMod();
                }
            }, 0, 5, TimeUnit.MINUTES);

            // Checks if the mod needs updating, runs every 10 minutes
            modVersionChecker = schedule(() -> {
                JsonObject object = new JsonParser().parse(rawWithAgent("https://gist.githubusercontent.com/boomboompower/03cf19715bb0b11173908016c3313349/raw/update.json")).getAsJsonObject();
                if (object.has("success") && object.get("success").getAsBoolean()) {
                    int currentVersion = formatVersion(SkinChangerMod.VERSION);
                    int latestVersion = object.has("latest-version") ? formatVersion(object.get("latest-version").getAsString()) : -1;
                    if (currentVersion < latestVersion && latestVersion > 0) {
                        needsUpdate = true;
                        updateVersion = object.has("latest-version") ? object.get("latest-version").getAsString() : "-1";
                    } else if (currentVersion > latestVersion && latestVersion > 0) {
                        higherVersion = true;
                    } else {
                        needsUpdate = false;
                        updateVersion = "-1";
                    }
                }
            }, 0, 5, TimeUnit.MINUTES);

            // Checks if the user is blacklisted from using my mods, runs every 5 minutes
            modBlacklist = schedule(() -> {
                JsonObject object = new JsonParser().parse(rawWithAgent("https://gist.githubusercontent.com/boomboompower/c865e13393abdbbc1776671498a6f6f7/raw/" + mc.getSession().getProfile().getId().toString() + ".json")).getAsJsonObject();
                if (!object.has("success")) {
                    disableMod();
                }
            }, 0, 5, TimeUnit.MINUTES);
        } else {
            throw new IllegalStateException("WebsiteUtils is already running!");
        }
    }

    public void stop() {
        if (isRunning) {
            modSettingsChecker.cancel(true);
            modVersionChecker.cancel(true);
            modBlacklist.cancel(true);
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

    private ScheduledFuture<?> schedule(Runnable r, long initialDelay, long delay, TimeUnit unit) {
        return RUNNABLE_POOL.scheduleAtFixedRate(r, initialDelay, delay, unit);
    }

    private String rawWithAgent(String url) {
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
}
