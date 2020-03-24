package me.do_you_like.mods.skinchanger.compatability;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

public class KeyCompatibility {

    /** Returns true if either windows ctrl key is down or if either mac meta key is down */
    public static boolean isCtrlKeyDown() {
        return Minecraft.isRunningOnMac
            ? Keyboard.isKeyDown(219) || Keyboard.isKeyDown(220)
            : Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157);
    }

    /** Returns true if either shift key is down */
    public static boolean isShiftKeyDown() {
        return Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
    }

    /** Returns true if either alt key is down */
    public static boolean isAltKeyDown() {
        return Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184);
    }

    public static boolean isKeyComboCtrlX(int keyID) {
        return keyID == 45 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    public static boolean isKeyComboCtrlV(int keyID) {
        return keyID == 47 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    public static boolean isKeyComboCtrlC(int keyID) {
        return keyID == 46 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    public static boolean isKeyComboCtrlA(int keyID) {
        return keyID == 30 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }
}
