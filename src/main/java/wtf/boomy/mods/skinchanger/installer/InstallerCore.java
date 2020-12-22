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

import wtf.boomy.mods.skinchanger.installer.libs.OSType;
import wtf.boomy.mods.skinchanger.installer.libs.OperatingSystem;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Class to automatically install the mod to the mods folder (for the appropriate mod version)
 *
 * Tries to determine where the Minecraft installation is located, and places itself in the mods
 * directory.
 */
public class InstallerCore {
    
    private static final String MOD_NAME = "SkinChanger";
    
    private static final String BUILT_FOR = "@MC_VERSION@";
    private static final String MOD_VERSION = "@VERSION@";
    
    private InstallerCore() {
    }
    
    public static void main(String[] args) throws URISyntaxException {
        // Cannot run in headless mode since there are no built in display options.
        if (!GraphicsEnvironment.isHeadless()) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
    
            new InstallerCore().run();
        }
    }
    
    private void run() throws URISyntaxException {
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
            if (!mcModDir.mkdirs()) {
                onInstallationFailed("Unable to create the mods directory.");
                
                return;
            }
        }
    
        if (InstallerCore.class.getProtectionDomain() == null || InstallerCore.class.getProtectionDomain().getCodeSource() == null || InstallerCore.class.getProtectionDomain().getCodeSource().getLocation() == null) {
            onInstallationFailed("Your mod file may be corrupt.");
        
            return;
        }
    
        // Use toURI to avoid problems with special characters in the path.
        File currentPath = new File(InstallerCore.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    
        if (!currentPath.exists() || currentPath.isDirectory()) {
        
            onInstallationFailed("You are running the installer in a development environment.");
        
            return;
        }
    
        File installLocation = new File(mcModDir, BUILT_FOR);
    
        try {
            try {
                // Collects a list of files which may be SkinChanger
                List<File> filesToDelete = findModCandidates(installLocation);
            
                if (filesToDelete != null && filesToDelete.size() > 0 && showDeleteMenu(filesToDelete) == 0) {
                    for (File file : filesToDelete) {
                        try {
                            file.delete();
                        } catch (SecurityException exception) {
                            // A failure occurred.
                            // Stop deleting files.
                            exception.printStackTrace();
                            
                            break;
                        }
                    }
                }
            } catch (NullPointerException ex) {
                ex.printStackTrace();
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
        } catch (SecurityException ex) {
            // Had no permission to delete the file.
            ex.printStackTrace();
        }
    
        JOptionPane.showMessageDialog(null, "" +
                MOD_NAME + " (v" + MOD_VERSION + ") has been installed at: \n" +
                " " + installLocation.getAbsolutePath() + "\n" +
                "\n" +
                "From: \n" +
                " " + currentPath.getAbsolutePath() + "\n" +
                "\n" +
                "You may delete this file now!", "Installation Successful", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private int showStartupMenu() {
        String[] lines = new String[] {
                "You are running the " + MOD_NAME + " installer for Minecraft v" + BUILT_FOR,
                "",
                "The installer will do the following: ",
                " \u2022 Detect the default Minecraft install location for your system",
                " \u2022 Remove other versions of " + MOD_NAME + " if possible",
                " \u2022 Place a file called \"" + getModFileName() + ".jar\" in your mods directory",
                "",
                "If the installer fails, you will need to place the mod in your mods folder manually",
                "Do you wish to continue?"
        };
        
        return JOptionPane.showConfirmDialog(null, String.join("\n", lines), MOD_NAME + " Installer", JOptionPane.YES_NO_OPTION);
    }
    
    private  int showDeleteMenu(List<File> files) {
        if (files == null || files.isEmpty()) {
            return -1;
        }
        
        List<String> lines = new ArrayList<>();
        
        lines.add("The installation has nearly completed");
        lines.add("");
        lines.add("The following files have been detected as SkinChanger: ");
        
        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            
            lines.add(" - " + file.getName());
            
            // Too many.
            if (i > 35) {
                lines.add(" And " + (files.size() - i) + " more!");
                break;
            }
        }
        
        lines.add("");
        lines.add("Would you like the installer to delete these files?");
    
        return JOptionPane.showConfirmDialog(null, String.join("\n", lines), MOD_NAME + " Installer", JOptionPane.YES_NO_OPTION);
    }
    
    private String getModFileName() {
        return MOD_NAME + " v" + MOD_VERSION;
    }
    
    private void onInstallationFailed(String additional) {
        String message = "Unable to install " + MOD_NAME + ", please place it in your \".minecraft/mods\" directory manually.";
        
        if (additional.trim().length() > 0) {
            message += "\n\n" + additional;
        }
    
        if (OperatingSystem.getOSType() != OSType.UNKNOWN) {
            message += "\n\nYour Minecraft directory should be here: \n" + OperatingSystem.getMinecraftDirectory(OperatingSystem.getOSType());
        } else {
            message += "\n\nThe installer was unable to find the minecraft directory for your operating system.";
        }
        
        JOptionPane.showMessageDialog(null, message, "Installation Failed", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * This is a very severe way of detecting the mod. It reads every JAR in the mods directory and checks
     * if they have a mcmod.info file. If they do it checks if the file contains the MOD_NAME.
     *
     * @param directory the directory to scan for mods
     * @return a List of files which are probably this mod, or null if none are found.
     */
    private List<File> findModCandidates(File directory) {
        if (directory == null || directory.isFile()) return null;
    
        List<File> files = Arrays.stream(Objects.requireNonNull(directory.listFiles())).collect(Collectors.toList());
        List<File> foundCandidates = new ArrayList<>();
    
        for (File file : files) {
            if (file.isDirectory()) {
                continue;
            }
        
            if (!file.getName().endsWith(".jar")) {
                continue;
            }
        
            ZipFile zipFile = null;
        
            try {
                zipFile = new ZipFile(file);
            
                ZipEntry modInfo = zipFile.getEntry("mcmod.info");
            
                if (modInfo == null) {
                    continue;
                }
    
                InputStreamReader reader = null;
                BufferedReader bufferedReader = null;
                
                try {
                    reader = new InputStreamReader(zipFile.getInputStream(modInfo));
                    bufferedReader = new BufferedReader(reader);
                    
                    String text = bufferedReader.lines().collect(Collectors.joining("\n"));
                    
                    if (text.toLowerCase().contains(MOD_NAME.toLowerCase())) {
                        foundCandidates.add(file);
                    }
    
                    bufferedReader.close();
                    reader.close();
                    zipFile.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } finally {
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                    
                    if (reader != null) {
                        reader.close();
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                if (zipFile != null) {
                    try {
                        zipFile.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        
        return foundCandidates.size() > 0 ? foundCandidates : null;
    }
}
