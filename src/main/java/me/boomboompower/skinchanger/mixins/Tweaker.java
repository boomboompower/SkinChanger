package me.boomboompower.skinchanger.mixins;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.io.File;
import java.util.List;

public class Tweaker implements ITweaker {

    public static boolean MIXINS_ENABLED = false;

    private final File mixinsConfigFile = new File(".", "mods\\skinchanger\\mixins.dat");

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {}

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        // We will check the config to see if the user wants to use Mixins

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

            writer.write("no");
            writer.write(System.lineSeparator());

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
