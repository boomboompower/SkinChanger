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

package me.do_you_like.mods.skinchanger.methods.impl.mixins;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

// --tweakClass me.do_you_like.mods.skinchanger.methods.impl.mixins.SkinChangerTweaker
public class SkinChangerTweaker implements ITweaker {

    public static boolean MIXINS_ENABLED = false;

    private final File mixinsConfigFile = new File(".", "config\\skinchanger\\mixins.dat");

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {}

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        // We will check the config to see if the user wants to use Mixins

        System.out.println("[SkinChanger] Loading Mixin check...");

        if (!shouldUseMixins()) {
            System.out.println("[SkinChanger] Mixins is DISABLED!");
            return;
        }

        MIXINS_ENABLED = true;

        System.out.println("SkinChanger Tweaker Started!");

        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.skinchanger.json");
        MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
        MixinEnvironment.getDefaultEnvironment().setSide(MixinEnvironment.Side.CLIENT);
    }

    @Override
    public String getLaunchTarget() {
        return "net.minecraft.client.main.Main";
    }

    @Override
    public String[] getLaunchArguments() {
        return new String[0];
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private boolean shouldUseMixins() {
        // If the config doesn't exist, we'll create one
        genConfig();

        try {
            FileReader fileReader = new FileReader(this.mixinsConfigFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            // We only care about the first line.
            String str = bufferedReader.readLine();

            bufferedReader.close();

            switch (str.toLowerCase()) {
                case "true":
                case "yes":
                case "y":
                case "1":
                    return true;
                case "false":
                case "no":
                case "n":
                case "0":
                    return false;
                default:
                    this.mixinsConfigFile.delete();

                    genConfig();

                    return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void genConfig() {
        if (!this.mixinsConfigFile.getParentFile().exists()) {
            if (!this.mixinsConfigFile.getParentFile().mkdirs()) {
                return;
            }
        }

        if (!this.mixinsConfigFile.exists()) {
            try {
                if (!this.mixinsConfigFile.createNewFile()) {
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return;
        }

        try {
            FileWriter fileWriter = new FileWriter(this.mixinsConfigFile);
            BufferedWriter writer = new BufferedWriter(fileWriter);

            writer.write("yes");
            writer.write(System.lineSeparator());

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
