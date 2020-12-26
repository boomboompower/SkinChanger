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

package wtf.boomy.apagoge.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;
import wtf.boomy.apagoge.ApagogeHandler;
import wtf.boomy.apagoge.ApagogeVerifier;
import wtf.boomy.apagoge.CompletionListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * A Github based updater. Only implements the run functionality as per
 * the Apagoge specification.
 */
public class ApagogeUpdater implements ApagogeVerifier {
    
    // Stores a list of the completion listeners to be notified
    // once the updater has finished
    private final List<CompletionListener> completionListeners = new ArrayList<>();
    
    // Stores the handler
    private final ApagogeHandler handler;
    
    // Stores the latest update information
    private JsonObject updateInformation = null;
    
    /**
     * Basic constructor
     *
     * @param a       unmapped variable
     * @param b       unmapped variable
     * @param c       unmapped variable
     * @param handler the handler instance associated with this updater.
     */
    protected ApagogeUpdater(File a, String b, String c, ApagogeHandler handler) {
        this.handler = handler;
    }
    
    /**
     * Returns the build-type of this program.
     *
     * @return Returns 1 if this binary has been signed with a valid signature and the file has been verified by the updater
     * Returns 0 if the file has not been verified, but has the correct signature
     * Returns -1 if the file has an incorrect signature.
     */
    @Override
    public int getBuildType() {
        return -1;
    }
    
    /**
     * Returns the SHA-256 hash of this verified build, will return null if the {@link #getBuildType()} is -1;
     *
     * @return the SHA-256 hash of this build, or null.
     */
    @Override
    public String getVerifiedHash() {
        return null;
    }
    
    /**
     * Returns the data for the newest version if there is one, or null if this version is up to date or {@link #isRunningNewerVersion()} is true
     *
     * @return JSON data for the newest version or null
     */
    @Override
    public JsonObject getNewestVersion() {
        return this.updateInformation;
    }
    
    /**
     * True if the version being run is newer than the latest version, false if it's older or is the latest release.
     *
     * @return See above
     */
    @Override
    public boolean isRunningNewerVersion() {
        return false;
    }
    
    /**
     * Adds a callback method, will notify once the verifier is done
     *
     * @param listener the listener to verify, see {@link CompletionListener}
     */
    @Override
    public void addCompletionListener(CompletionListener listener) {
        if (this.completionListeners.contains(listener)) {
            return;
        }
        
        this.completionListeners.add(listener);
    }
    
    /**
     * Adds a list of classes to validate against. Depending on the implementation this may do nothing.
     *
     * @param classArray an array of classes to check for validation
     */
    @Override
    public void addValidatorClasses(Class<?>[] classArray) {
    
    }
    
    /**
     * Invalidates all JSON cached data, depending on the implementation this may be handled differently
     */
    @Override
    public void resetUpdateCache() {
    
    }
    
    /**
     * Tells the Apagoge updater to run, results may be cached from the first time this is ran and can be wiped with {@link #resetUpdateCache()}
     */
    @Override
    public void run() {
        InputStream actionsBuild = ApagogeUpdater.class.getResourceAsStream("/GHCI.txt");
        
        if (actionsBuild == null) {
            terminate();
            
            return;
        }
        
        try {
            Version buildVersion = new Version("3.0." + IOUtils.toString(actionsBuild, StandardCharsets.UTF_8));
            
            String url = "https://api.github.com/repos/boomboompower/SkinChanger/releases";
            
            JsonElement element = new JsonParser().parse(IOUtils.toString(new URL(url), StandardCharsets.UTF_8));
            
            if (element.isJsonObject() && isNewerVersion(buildVersion, element.getAsJsonObject())) {
                this.updateInformation = element.getAsJsonObject();
                
                return;
            }
            
            JsonArray array = element.getAsJsonArray();
            
            for (JsonElement child : array) {
                if (child.isJsonObject() && isNewerVersion(buildVersion, child.getAsJsonObject())) {
                    this.updateInformation = child.getAsJsonObject();
                    
                    break;
                }
            }
        } catch (IOException exception) {
            exception.printStackTrace();
            
            terminate();
        } finally {
            try {
                actionsBuild.close();
            } catch (IOException exception) {
                exception.printStackTrace();
                
                terminate();
            }
        }
    }
    
    private boolean isNewerVersion(Version currentVersion, JsonObject jsonObject) {
        if (jsonObject.has("tag_name")) {
            String tag_name = jsonObject.get("tag_name").getAsString();
            
            // For variations
            if (tag_name.contains("-")) {
                tag_name = tag_name.split("-")[0];
            }
            
            try {
                Version foundVersion = new Version(tag_name);
                
                return currentVersion.compareTo(foundVersion) < 1;
            } catch (IllegalArgumentException ignored) {
                return false;
            }
        }
        
        return false;
    }
    
    /**
     * Always return false. We can never verify a public build.
     */
    private void terminate() {
        this.completionListeners.forEach(listener -> listener.onFinish(this.handler, false));
    }
    
    /**
     * Terminates this updater, once this is called the following will commence
     * <p>
     * - Build type will be -1
     * - Verified hash will be removed
     * - Running new version will be set to false
     * - New version JSON data will be erased
     * - Run will no longer work
     * - Validator classes will be cleared
     * <p>
     * This method effectively ends the Updater until the process is restarted or a new Updater is created.
     */
    @Override
    public void kill() {
    
    }
    
    /**
     * Source code from https://stackoverflow.com/a/11024200
     * <p>
     * by alex (https://stackoverflow.com/users/1445568/alex)
     */
    private static class Version implements Comparable<Version> {
        
        private final String version;
        
        public final String get() {
            return this.version;
        }
        
        public Version(String version) {
            if (version == null) throw new IllegalArgumentException("Version can not be null");
            if (!version.matches("[0-9]+(\\.[0-9]+)*")) throw new IllegalArgumentException("Invalid version format");
            
            this.version = version;
        }
        
        @Override
        public int compareTo(Version that) {
            if (that == null) return 1;
            
            String[] thisParts = this.get().split("\\.");
            String[] thatParts = that.get().split("\\.");
            
            int length = Math.max(thisParts.length, thatParts.length);
            
            for (int i = 0; i < length; i++) {
                int thisPart = i < thisParts.length ? Integer.parseInt(thisParts[i]) : 0;
                int thatPart = i < thatParts.length ? Integer.parseInt(thatParts[i]) : 0;
                
                if (thisPart < thatPart) return -1;
                if (thisPart > thatPart) return 1;
            }
            return 0;
        }
        
        @Override
        public boolean equals(Object that) {
            if (this == that) {
                return true;
            }
            if (that == null) {
                return false;
            }
            if (this.getClass() != that.getClass()) {
                return false;
            }
            return this.compareTo((Version) that) == 0;
        }
        
    }
}
