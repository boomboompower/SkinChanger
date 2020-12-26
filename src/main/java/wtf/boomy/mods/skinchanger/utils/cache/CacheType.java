package wtf.boomy.mods.skinchanger.utils.cache;

import wtf.boomy.mods.skinchanger.utils.cosmetic.resources.CapeBuffer;
import wtf.boomy.mods.skinchanger.utils.cosmetic.resources.SkinBuffer;

/**
 * Tells the code how the cached file should be parsed by the mod
 * <p>
 * SKIN will be parsed through {@link SkinBuffer}
 * CAPE will be parsed through {@link CapeBuffer}
 * OTHER will not be parsed through anything.
 */
public enum CacheType {
    SKIN,
    CAPE,
    URL,
    OTHER
}
