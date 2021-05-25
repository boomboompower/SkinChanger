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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    
    // Stores the current version information
    private Version currentVersion = null;
    
    // Stores the latest update information
    private JsonObject updateInformation = null;
    
    // Am I running a newer version than release?
    private boolean runningNewer = false;
    
    // Stores the matching pattern
    private final Pattern versionPattern = Pattern.compile("(?<version>[0-9]+(\\.[0-9]+)*)");
    
    /**
     * Basic constructor
     *
     * @param a       unmapped variable
     * @param b       unmapped variable
     * @param c       unmapped variable
     * @param handler the handler instance associated with this updater.
     */
    public ApagogeUpdater(File a, String b, String c, ApagogeHandler handler) {
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
        return this.runningNewer;
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
    
    private boolean doesCommitMatch(String commit, JsonObject object) {
        if (!object.has("target_commitish")) {
            return false;
        }
        
        // Check if the tag matches the incoming commit
        return object.get("target_commitish").getAsString().equals(commit);
    }
    
    private String getVersionFromObject(JsonObject element) {
        if (element.has("tag_name")) {
            String tagName = element.get("tag_name").getAsString();
            
            Matcher matcher = this.versionPattern.matcher(tagName);
            
            // No matches found
            if (!matcher.find(0) || matcher.groupCount() == 0) {
                return null;
            }
            
            return matcher.group(0);
        } else if (element.has("name")) {
            String name = element.get("name").getAsString();
            
            Matcher matcher = this.versionPattern.matcher(name);
            
            // No matches found
            if (!matcher.find(0) || matcher.groupCount() == 0) {
                return null;
            }
            
            return matcher.group(0);
        }
        
        return null;
    }
    
    /**
     * Tells the Apagoge updater to run, results may be cached from the first time this is ran and can be wiped with {@link #resetUpdateCache()}
     */
    @Override
    public void run() {
        // Try get the stream for the commit file
        InputStream commitHashStream = ApagogeUpdater.class.getResourceAsStream("/commit.txt");
        
        // This generally means the file did not exist in the JAR. Just stop the updater.
        if (commitHashStream == null) {
            terminate();
            
            return;
        }
        
        try {
            // Get the commit hash from the file and trip any newline or whitespace characters at the end
            String commitHash = IOUtils.toString(commitHashStream, StandardCharsets.UTF_8).trim();
            
            // The URL which lists all the releases. We use the per_page tag to make it show up to 100
            String url = "https://api.github.com/repos/boomboompower/SkinChanger/releases?per_page=100";
            
            // Parses the JSON from the releases URL
            JsonElement element = new JsonParser().parse(IOUtils.toString(new URL(url), StandardCharsets.UTF_8));
            
            // Stores the current version
            this.currentVersion = null;
            
            // If there is only one element then we can't check for newer versions.
            if (element.isJsonObject()) {
                terminate();
                
                return;
            } else if (element.isJsonArray()) {
                // Iterate through each of the versions to try find the version which matches
                // to the release we are currently using. This is better than just using
                // the github build number because we sometimes skip releases.
                for (JsonElement child : element.getAsJsonArray()) {
                    // We only want to look at objects, not nestled arrays
                    if (!child.isJsonObject()) {
                        continue;
                    }
                    
                    // If the commit does not match, then we have a problem here.
                    if (!doesCommitMatch(commitHash, child.getAsJsonObject())) {
                        continue;
                    }
                    
                    // Get the version object, may be null if the version tags aren't found on the object.
                    // Theoretically the above check would filter out these kinds of issues, but it's good
                    // to be safe anyway. This will just use a matcher to retrieve the version.
                    String version = getVersionFromObject(child.getAsJsonObject());
                    
                    // The matcher couldn't find the version in the tag or release name, or neither were found in the
                    // JSON so we should just return here since we can't actually determine the version number.
                    if (version == null) {
                        continue;
                    }
                    
                    // Successfully found the version
                    this.currentVersion = new Version(version);
                    
                    break;
                }
            }
            
            // How many returns do you want? Yes.
            // So basically this checks if the calculated version object
            // is null, which will happen if there are no releases matching the release hash
            if (this.currentVersion == null) {
                terminate();
                
                return;
            }
            
            JsonArray array = element.getAsJsonArray();
            
            // TODO, move away from looping twice since this is O(n^2) and is worse than just O(n)
            for (JsonElement child : array) {
                if (!child.isJsonObject()) {
                    return;
                }
                
                // Store the compared information
                int comparison = getComparison(this.currentVersion, child.getAsJsonObject());
                
                // Check if the object is newer than the current version.
                if (comparison < 0) {
                    // Store the update information
                    this.updateInformation = child.getAsJsonObject();
                    
                    break;
                } else if (comparison > 0) {
                    this.runningNewer = true;
                }
            }
        } catch (IllegalArgumentException | IOException ex) {
            ex.printStackTrace();
            
            terminate();
        } finally {
            try {
                commitHashStream.close();
            } catch (IOException exception) {
                exception.printStackTrace();
                
                terminate();
            }
        }
    }
    
    /**
     * Returns the comparison between the two versions
     *
     * -1 if jsonObject is newer
     * 0 on failure or if they're the same
     * 1 if the current version is newer
     *
     * @param currentVersion the version we're currently using
     * @param jsonObject the object to check update information on
     *
     * @return true if the json object has newer version information
     */
    private int getComparison(Version currentVersion, JsonObject jsonObject) {
        String version = getVersionFromObject(jsonObject);
        
        if (version == null) {
            return 0;
        }
        
        try {
            return currentVersion.compareTo(new Version(version));
        } catch (IllegalArgumentException ignored) {
            return 0;
        }
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
    
    public String getCurrentVersionString() {
        return this.currentVersion == null ? "Unknown" : this.currentVersion.get();
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
            
            version = version.trim();
            
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
