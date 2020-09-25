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

package wtf.boomy.mods.skinchanger.api;

import com.google.gson.annotations.SerializedName;

import wtf.boomy.mods.skinchanger.api.impl.AshconHooker;
import wtf.boomy.mods.skinchanger.api.impl.MojangHooker;

/**
 * A holder for the different API's provided by the mod
 */
public enum SkinAPIType {
    
    @SerializedName("Mojang")
    MOJANG("Mojang", "Built-in Mojang API, can be slow and requires more bandwidth", new MojangHooker()),
    
    @SerializedName("Ashcon")
    ASHCON("Ashcon", "Ashcon API, faster than the Mojang API and uses less bandwidth", new AshconHooker());
    
    private final String displayName;
    private final SkinAPI api;
    
    SkinAPIType(String displayName, String description, SkinAPI instance) {
        this.displayName = displayName;
        this.api = instance;
    }
    
    /**
     * Retrieves the next value (higher ordinal), or loops
     * back to the enum at position 0 if there is no higher enum available.
     *
     * @return the next {@link SkinAPIType} after this one
     */
    public SkinAPIType nextValue() {
        // At the next index.
        int nextOrdinal = ordinal() + 1;
    
        // Don't overflow.
        if (nextOrdinal > values().length - 1) {
            // Just return the one at the 0th index.
            return values()[0];
        }
    
        // Attempt to retrieve the next index.
        return values()[nextOrdinal];
    }
    
    /**
     * Returns the display name for this given skin api
     *
     * @return the readable name for this api
     */
    public String getDisplayName() {
        return this.displayName;
    }
    
    /**
     * Returns the hooker for this api which requests will be made through
     *
     * @return the api hooker
     */
    public SkinAPI getAPI() {
        return this.api;
    }
    
    /**
     * Returns a value if the param matches either an API's {@link #name()} or {@link #getDisplayName()}
     *
     * @param in the value to search for
     * @return a {@link SkinAPIType} or {@link #ASHCON} if no matches are found
     */
    public static SkinAPIType getFromString(String in) {
        for (SkinAPIType type : values()) {
            if (type.name().equalsIgnoreCase(in) || type.getDisplayName().equalsIgnoreCase(in)) {
                return type;
            }
        }
        
        // Default to Ashcon
        return ASHCON;
    }
}
