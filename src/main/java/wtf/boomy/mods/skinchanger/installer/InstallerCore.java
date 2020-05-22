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

package wtf.boomy.mods.skinchanger.installer;

import wtf.boomy.mods.skinchanger.utils.installing.OSType;
import wtf.boomy.mods.skinchanger.utils.installing.OperatingSystem;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class InstallerCore {

    private static final String MOD_NAME = "SkinChanger";

    private static final String BUILT_FOR = "1.8.9"; // TODO Update this across versions.
    private static final String MOD_VERSION = "3.0.0"; // TODO Update this on releases

    public static void main(String[] args) {
        if (!GraphicsEnvironment.isHeadless()) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            int result = showStartupMenu();

            // User cancelled the operation
            if (result == -1) {
                return;
            }

            // User clicked no
            if (result == 1) {
                return;
            }

            OSType osType = OperatingSystem.getOSType();
            File minecraftDirectory = OperatingSystem.getMinecraftDirectory(osType);

            if (osType == OSType.UNKNOWN || minecraftDirectory == null) {
                onInstallationFailed("Your operating system is not supported by the installer.\nPlease place the mod file in your mods folder manually.");

                return;
            }

            if (!minecraftDirectory.exists()) {
                onInstallationFailed("Your operating system was supported\nhowever your minecraft directory did not exist.");

                return;
            }

            File mcModDir = new File(minecraftDirectory, "mods");

            if (!mcModDir.exists()) {
                mcModDir.mkdirs();
            }

            if (InstallerCore.class.getProtectionDomain() == null || InstallerCore.class.getProtectionDomain().getCodeSource() == null || InstallerCore.class.getProtectionDomain().getCodeSource().getLocation() == null) {
                onInstallationFailed("Your mod file may be corrupt.");

                return;
            }

            File currentPath = new File(InstallerCore.class.getProtectionDomain().getCodeSource().getLocation().getPath());

            if (!currentPath.exists() || currentPath.isDirectory()) {
                onInstallationFailed("You are running the installer in a development environment.");

                return;
            }

            File installLocation = new File(mcModDir, BUILT_FOR);

            try {
                // Remove other versions of the mod.
                // If it fails then we should do nothing
                try {
                    for (File file : Objects.requireNonNull(installLocation.listFiles())) {
                        if (file.getName().startsWith(MOD_NAME)) {
                            file.delete();
                        }
                    }
                } catch (NullPointerException ignored) {
                }

                Files.copy(currentPath.toPath(), new File(installLocation, getModFileName() + ".jar").toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                onInstallationFailed("The mod was unable to be written to the installation directory.");

                ex.printStackTrace();

                return;
            } catch (SecurityException ex) {
                onInstallationFailed("The installer did not have permission to copy the mod to the mods directory. \nTry running the jar as an admin.");

                ex.printStackTrace();

                return;
            }

            // Remove the file from the current directory.
            try {
                currentPath.deleteOnExit();
            } catch (SecurityException ignored) {
                // Had no permission to delete the file.
            }

            JOptionPane.showMessageDialog(null, MOD_NAME + " (v" + MOD_VERSION + ") has been installed at: \n " + installLocation.getAbsolutePath() + "\n\nFrom: \n" + currentPath.getAbsolutePath() + "\n\nYou may delete this file now!", "Installation Successful", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private static int showStartupMenu() {
        String[] lines = new String[] {
                "You are running the " + MOD_NAME + " installer for Minecraft v" + BUILT_FOR,
                "",
                "The installer will do the following: ",
                " • Detect the default Minecraft install location for your system",
                " • Remove other versions of " + MOD_NAME + " if possible",
                " • Place a file called \"" + getModFileName() + ".jar\" in your mods directory",
                "",
                "If the installer fails, you will need to place the mod in your mods folder manually",
                "Do you wish to continue?"
        };

        return JOptionPane.showConfirmDialog(null, String.join("\n", lines), MOD_NAME + " Installer", JOptionPane.YES_NO_OPTION);
    }

    private static String getModFileName() {
        return MOD_NAME + " v" + MOD_VERSION;
    }

    private static void onInstallationFailed() {
        onInstallationFailed("");
    }

    private static void onInstallationFailed(String additional) {
        String message = "Unable to install " + MOD_NAME + ", please place it in your \".minecraft/mods\" directory manually.";

        if (OperatingSystem.getOSType() != OSType.UNKNOWN) {
            message += "\n\nIt should be located at: \n" + OperatingSystem.getMinecraftDirectory(OperatingSystem.getOSType());
        } else {
            message += "\n\nThe installer was unable to find the minecraft directory for your operating system.";
        }

        if (additional.trim().length() > 0) {
            message += "\n\n" + additional;
        }

        JOptionPane.showMessageDialog(null, message, "Installation Failed", JOptionPane.ERROR_MESSAGE);
    }
}
