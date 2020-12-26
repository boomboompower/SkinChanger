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

package wtf.boomy.mods.skinchanger.gui;

import wtf.boomy.mods.skinchanger.SkinChangerMod;
import wtf.boomy.mods.skinchanger.utils.cache.CacheType;

import java.util.UUID;

/**
 * Tells this class which variant of itself it should use
 */
public enum StringSelectionType {
    P_USERNAME("Enter the username of the player.", 12, CacheType.SKIN),
    C_USERNAME("Enter the username of the player.", 17, CacheType.CAPE),
    
    P_URL("Enter the URL of the skin. (https://....)", 14, CacheType.SKIN),
    C_URL("Enter the URL of the cape. (https://....)", 19, CacheType.CAPE),
    
    P_UUID("Enter the UUID of the player. (ABCD-EFGH-...)", 13, CacheType.SKIN),
    C_UUID("Enter the UUID of the player. (ABCD-EFGH-...)", 18, CacheType.CAPE);
    
    private final CacheType cacheType;
    private final String displaySentence;
    private final int buttonID;
    
    
    // If a UUID has been generated we should store
    // it so we don't have to parse it twice
    private UUID storedUUID;
    
    StringSelectionType(String displaySentence, int buttonID, CacheType cacheType) {
        this.displaySentence = displaySentence;
        
        this.buttonID = buttonID;
        this.cacheType = cacheType;
    }
    
    public int getButtonID() {
        return this.buttonID;
    }
    
    public CacheType getCacheType() {
        return this.cacheType;
    }
    
    /**
     * Checks the entered string to see if it complies with this Selection's rules.
     *
     * @param input the input string to validate
     *
     * @return true if the string follows the specifications
     */
    public boolean isValid(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        
        if (isTypeOfUsername()) {
            return input.length() > 2 && input.length() < 16;
        }
        
        if (isTypeOfUrl()) {
            // In order of preference
            return input.startsWith("https://") || input.startsWith("http://") || input.startsWith("www.");
        }
        
        if (isTypeOfUUID()) {
            // Tiny performance increase
            this.storedUUID = SkinChangerMod.getInstance().getConfig().getSkinAPIType().getAPI().getUUIDFromStrippedString(input);
            
            return this.storedUUID != null;
        }
        
        return false;
    }
    
    /**
     * Is this enum a type of URL?
     *
     * @return true if the enum is of type URL
     */
    public boolean isTypeOfUrl() {
        return this == P_URL || this == C_URL;
    }
    
    /**
     * Is this enum a type of username?
     *
     * @return true if the enum is of type username
     */
    public boolean isTypeOfUsername() {
        return this == P_USERNAME || this == C_USERNAME;
    }
    
    /**
     * Is this enum a type of UUID?
     *
     * @return true if the enum is of type UUID
     */
    public boolean isTypeOfUUID() {
        return this == P_UUID || this == C_UUID;
    }
    
    /**
     * Is this enum for a skin?
     *
     * @return true if the selection is related to skins
     */
    public boolean isTypeOfSkin() {
        return this == P_USERNAME || this == P_UUID || this == P_URL;
    }
    
    /**
     * If a UUID has been generated we should store it so we don't have to parse it twice
     *
     * @return the stored uuid.
     */
    public UUID getStoredUUID() {
        return this.storedUUID;
    }
    
    public String getDisplaySentence() {
        return this.displaySentence;
    }
}
