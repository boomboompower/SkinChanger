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

package wtf.boomy.mods.skinchanger.utils.general;

/**
 * Tries to normalize player skin types. Even though they are pretty crappy in 1.8.9
 *
 * This was a heavily requested feature in the initial versions of SkinChanger.
 */
public enum PlayerSkinType {

    STEVE("Steve", "default"),
    ALEX("Alex", "slim");

    private final String displayName;
    private final String secretName;

    /**
     * Constructs the enum
     *
     * @param secretName the id used in the RenderManager
     */
    PlayerSkinType(String secretName) {
        this.displayName = name().toLowerCase();
        this.secretName = secretName;
    }

    /**
     * Constructs the enum
     *
     * @param displayName the name the user will see
     * @param secretName the id used in the RenderManager
     */
    PlayerSkinType(String displayName, String secretName) {
        this.displayName = displayName;
        this.secretName = secretName;
    }

    /**
     * Returns the display name which the user will see when selecting this skin type
     *
     * @return the display name of the skin type.
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * Returns the associated {@link net.minecraft.client.renderer.entity.RenderManager} -> skinMap value for this skin type.
     *
     * @return the skinMap value of this skin type.
     */
    public String getSecretName() {
        return this.secretName;
    }

    /**
     * Retrieves the next skin type after this skin.
     *
     * @return the next {@link PlayerSkinType} type after this skin.
     */
    public PlayerSkinType getNextSkin() {
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
     * Retrieves a {@link PlayerSkinType} from a string. If none is found then {@link PlayerSkinType#STEVE} will be returned.
     *
     * @param str the value / name of the type
     * @return a {@link PlayerSkinType} from a value. Or {@link PlayerSkinType#STEVE} if none is found.
     */
    public static PlayerSkinType getTypeFromString(String str) {
        // Invalid input, just return the default
        if (str == null || str.trim().isEmpty()) {
            return STEVE;
        }

        for (PlayerSkinType t : values()) {
            // If the string matches a name, return that value.
            if (t.name().equalsIgnoreCase(str) ||
                t.getDisplayName().equalsIgnoreCase(str) ||
                t.getSecretName().equalsIgnoreCase(str)) {

                return t;
            }
        }

        // None was found, just return the default.
        return STEVE;
    }

    /**
     * Retrieves the next value in this Enum from the current value
     *
     * @param type the current enum
     * @return the next {@link PlayerSkinType} after the inputted type.
     */
    public static PlayerSkinType getNextType(PlayerSkinType type) {
        return type.getNextSkin();
    }
}
