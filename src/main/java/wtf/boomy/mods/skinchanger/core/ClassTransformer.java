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

package wtf.boomy.mods.skinchanger.core;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import wtf.boomy.mods.skinchanger.SkinChangerMod;
import wtf.boomy.mods.skinchanger.utils.ambiguous.ThreadFactory;
import wtf.boomy.mods.skinchanger.utils.cosmetic.impl.SkinChangerStorage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Patcher method for SkinChanger, modifies the game to allow for the Skin and Cape tweaks.
 *
 * @author boomboompower
 */
public class ClassTransformer implements IClassTransformer {
    
    private static final String skinChangerClass = wtf.boomy.mods.skinchanger.SkinChangerMod.class.getName().replace(".", "/");
    private static final String storageClass = wtf.boomy.mods.skinchanger.utils.cosmetic.impl.SkinChangerStorage.class.getName().replace(".", "/");
    
    public static boolean shouldPatchSkinGetter = true;
    public static boolean shouldPatchCapeGetter = true;
    public static boolean shouldPatchSkinType = true;
    public static boolean shouldPatchOptifine = true;
    
    // If capes have already been tweaked on optifine don't bother patching networkplayerinfo
    private boolean tweakedOptifine = false;
    
    // Stores the logger instance
    private Logger logger;
    
    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    public ClassTransformer() {
        ThreadFactory threadFactory = new ThreadFactory("SkinChangerTransformer");
        
        threadFactory.runAsync(() -> {
            try {
                String defaultText = "PatchSkins: yes" + System.lineSeparator() + "PatchCapes: yes" + System.lineSeparator() + "PatchSkinType: yes" + System.lineSeparator() + "PatchOF: yes";
                File file = new File(new File("config", "skinchanger"), "asm.txt");
                
                if (!file.getParentFile().exists()) {
                    writeToFile(file, defaultText);
                    
                    return;
                }
                
                if (!file.exists()) {
                    writeToFile(file, defaultText);
                    
                    return;
                }
                
                FileReader reader = null;
                BufferedReader bufferedReader = null;
                
                try {
                    reader = new FileReader(file);
                    bufferedReader = new BufferedReader(reader);
                    
                    List<String> lines = bufferedReader.lines().collect(Collectors.toList());
                    
                    for (String s : lines) {
                        if (!s.contains(": ")) {
                            continue;
                        }
                        
                        String[] components = s.split(": ", 2);
                        String id = components[0];
                        String value = components[1];
                        
                        boolean toPatch = value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("1");
                        
                        if (id.equalsIgnoreCase("PatchSkins")) {
                            shouldPatchSkinGetter = toPatch;
                        }
                        
                        if (id.equalsIgnoreCase("PatchCapes")) {
                            shouldPatchCapeGetter = toPatch;
                        }
                        
                        if (id.equalsIgnoreCase("PatchSkinType")) {
                            shouldPatchSkinType = toPatch;
                        }
                        
                        if (id.equalsIgnoreCase("PatchOF")) {
                            shouldPatchOptifine = toPatch;
                        }
                    }
                } catch (Exception ex) {
                    writeToFile(file, defaultText);
                } finally {
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                    if (reader != null) {
                        reader.close();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
    
    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (!shouldPatchSkinGetter && !shouldPatchCapeGetter && !shouldPatchSkinType) {
            return bytes;
        }
        
        // In a dev environment, the obfuscated name and remapped name will be the same
        boolean isDevEnv = (name.equals(transformedName));
        
        if (transformedName.equals("net.minecraft.client.network.NetworkPlayerInfo")) {
            ClassReader reader = new ClassReader(bytes);
            ClassNode classNode = new ClassNode();
            reader.accept(classNode, ClassReader.SKIP_FRAMES);
            
            classNode.methods.forEach(m -> transformNetworkPlayerInfo(isDevEnv, classNode, m));
            
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            classNode.accept(writer);
            return writer.toByteArray();
        } else if (transformedName.equals("net.minecraft.client.entity.AbstractClientPlayer")) {
            ClassReader reader = new ClassReader(bytes);
            ClassNode classNode = new ClassNode();
            reader.accept(classNode, ClassReader.SKIP_FRAMES);
    
            classNode.methods.forEach(m -> transformOptifine(isDevEnv, classNode, m));
    
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            classNode.accept(writer);
            return writer.toByteArray();
        }
        
        return bytes;
    }
    
    private void transformOptifine(boolean isDevEnv, ClassNode clazz, MethodNode method) {
        String methodName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(clazz.name, method.name, method.desc);
        
        if (methodName.equals("getLocationCape") || methodName.equals("func_178861_h") || methodName.equals("k")) {
            getLogger().info("Patching Optifine capes (" + method.name + ")");
            
            method.instructions.insert(createForOptifine(isDevEnv, "getPlayerCape"));
            
            getLogger().info("Finished patching Optifine capes (" + method.name + ")");
            
            this.tweakedOptifine = true;
        }
    }
    
    private void transformNetworkPlayerInfo(boolean isDevEnv, ClassNode clazz, MethodNode method) {
        if (method.name == null || clazz.name == null) return;
        
        String methodName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(clazz.name, method.name, method.desc);
        
        if (shouldPatchSkinGetter && methodName.equals((isDevEnv ? "getLocationSkin" : "func_178837_g"))) {
            getLogger().info("Patching getLocationSkin (" + method.name + ")");
            
            method.instructions.insert(createForResource(isDevEnv, "getPlayerSkin"));
            
            getLogger().info("Finished patching getLocationSkin (" + method.name + ")");
    
            SkinChangerStorage.getInstance().activateSkinPatch();
        } else if (shouldPatchCapeGetter && methodName.equals((isDevEnv ? "getLocationCape" : "func_178861_h"))) {
            if (this.tweakedOptifine) {
                getLogger().info("Skipping getLocationCape patch (" + method.name + ") since optifine has already been patched!");
            } else {
                getLogger().info("Patching getLocationCape (" + method.name + ")");
    
                method.instructions.insert(createForResource(isDevEnv, "getPlayerCape"));
    
                getLogger().info("Finished patching getLocationCape (" + method.name + ")");
            }
            
            SkinChangerStorage.getInstance().activateCapePatch();
        } else if (shouldPatchSkinType && methodName.equals((isDevEnv ? "getSkinType" : "func_178851_f"))) {
            getLogger().info("Patching getSkinType (" + method.name + ")");
            
            method.instructions.insert(createForSkinType(isDevEnv));
            
            getLogger().info("Finished patching getSkinType (" + method.name + ")");
            
            SkinChangerStorage.getInstance().activateCapeTypePatch();
        }
    }
    
    private InsnList createForSkinType(boolean devEnv) {
        String gameProfileField = devEnv ? "gameProfile" : "field_178867_a";
        
        LabelNode skip = new LabelNode();
        
        // Constructs a list of bytecode asm instructions to and adds them to the start of the method
        return constructList(
                getModInstance(), // INVOKESTATIC
                getStorage(), // INVOKEVIRTUAL
                new VarInsnNode(Opcodes.ALOAD, 0),
                new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/network/NetworkPlayerInfo", gameProfileField, "Lcom/mojang/authlib/GameProfile;"),
                invokeVirtual(storageClass, "getSkinType", "(Lcom/mojang/authlib/GameProfile;)Ljava/lang/String;"),
                new VarInsnNode(Opcodes.ASTORE, 1),
                new VarInsnNode(Opcodes.ALOAD, 1),
                new JumpInsnNode(Opcodes.IFNULL, skip),
                new VarInsnNode(Opcodes.ALOAD, 1),
                new InsnNode(Opcodes.ARETURN),
                skip
        );
    }
    
    private InsnList createForResource(boolean devEnv, String methodName) {
        String gameProfileField = devEnv ? "gameProfile" : "field_178867_a";
        
        LabelNode skip = new LabelNode();
        
        // Constructs a list of bytecode asm instructions to and adds them to the start of the method
        return constructList(
                getModInstance(), // INVOKESTATIC
                getStorage(), // INVOKEVIRTUAL
                new VarInsnNode(Opcodes.ALOAD, 0),
                new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/network/NetworkPlayerInfo", gameProfileField, "Lcom/mojang/authlib/GameProfile;"),
                invokeVirtual(storageClass, methodName, "(Lcom/mojang/authlib/GameProfile;)Lnet/minecraft/util/ResourceLocation;"),
                new VarInsnNode(Opcodes.ASTORE, 1),
                new VarInsnNode(Opcodes.ALOAD, 1),
                new JumpInsnNode(Opcodes.IFNULL, skip),
                new VarInsnNode(Opcodes.ALOAD, 1),
                new InsnNode(Opcodes.ARETURN),
                skip
        );
    }
    
    private InsnList createForOptifine(boolean devEnv, String methodName) {
        String gameProfileMethod = devEnv ? "getGameProfile" : "func_146103_bH";
    
        LabelNode skip = new LabelNode();
    
        // Constructs a list of bytecode asm instructions to and adds them to the start of the method
        return constructList(
                getModInstance(), // INVOKESTATIC
                getStorage(), // INVOKEVIRTUAL
                new VarInsnNode(Opcodes.ALOAD, 0),
                new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/entity/AbstractClientPlayer", gameProfileMethod, "()Lcom/mojang/authlib/GameProfile;", false),
                invokeVirtual(storageClass, methodName, "(Lcom/mojang/authlib/GameProfile;)Lnet/minecraft/util/ResourceLocation;"),
                new VarInsnNode(Opcodes.ASTORE, 1),
                new VarInsnNode(Opcodes.ALOAD, 1),
                new JumpInsnNode(Opcodes.IFNULL, skip),
                new VarInsnNode(Opcodes.ALOAD, 1),
                new InsnNode(Opcodes.ARETURN),
                skip
        );
    }
    
    private MethodInsnNode getModInstance() {
        return new MethodInsnNode(Opcodes.INVOKESTATIC, skinChangerClass, "getInstance", "()L" + skinChangerClass + ";", false);
    }
    
    private MethodInsnNode getStorage() {
        return invokeVirtual(skinChangerClass, "getStorage", "()L" + storageClass + ";");
    }
    
    private MethodInsnNode invokeVirtual(String owner, String name, String desc) {
        return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, owner, name, desc, false);
    }
    
    private InsnList constructList(Object... args) {
        InsnList list = new InsnList();
        for (Object arg : args) {
            if (arg instanceof AbstractInsnNode) {
                list.add((AbstractInsnNode) arg);
            } else if (arg instanceof InsnList) {
                list.add((InsnList) arg);
            }
        }
        return list;
    }
    
    /**
     * This must run independently, Gson is not loaded when this is called
     * and will crash the game if it's used. Therefore we need our own method.
     *
     * @param file  the file to save the value to
     * @param value the value to write to the file.
     */
    @SuppressWarnings({"ResultOfMethodCallIgnored", "DuplicatedCode"})
    public void writeToFile(File file, String value) {
        if (file == null || (file.exists() && file.isDirectory())) {
            // Do nothing if future issues may occur
            return;
        }
        
        try {
            if (!file.exists()) {
                File parent = file.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                file.createNewFile();
            }
            
            FileWriter writer = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(value);
            bufferedWriter.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private Logger getLogger() {
        if (this.logger == null) {
            this.logger = LogManager.getLogger("SkinChanger - CoreMod");
        }
        
        return this.logger;
    }
}
