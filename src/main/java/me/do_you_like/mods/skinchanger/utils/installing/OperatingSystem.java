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

package me.do_you_like.mods.skinchanger.utils.installing;

import java.io.File;

public class OperatingSystem {

    private static OSType osType;

    static {
        String osName = System.getProperty("os.name");

        if (osName.startsWith("Windows")) {
            osType = OSType.WINDOWS;
        } else if (osName.startsWith("Mac") || osName.startsWith("Darwin")) {
            osType = OSType.MAC;
        } else if (osName.startsWith("Linux")) {
            osType = OSType.LINUX;
        } else {
            osType = OSType.UNKNOWN;
        }
    }

    private OperatingSystem() {
    }

    public static OSType getOSType() {
        if (osType != null) {
            return osType;
        }

        String osName = System.getProperty("os.name");

        if (osName.startsWith("Windows")) {
            osType = OSType.WINDOWS;
        } else if (osName.startsWith("Mac") || osName.startsWith("Darwin")) {
            osType = OSType.MAC;
        } else if (osName.startsWith("Linux")) {
            osType = OSType.LINUX;
        } else {
            osType = OSType.UNKNOWN;
        }

        return osType;
    }

    public static File getMinecraftDirectory(OSType system) {
        String path;

        switch (system) {
            case WINDOWS:
                path = "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\.minecraft\\";
                break;
            case LINUX:
            case MAC:
                path = system.getNormalDirectory();
                break;
            default:
                path = null;
        }

        if (path == null) {
            return null;
        }

        return new File(path);
    }
}