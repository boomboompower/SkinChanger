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

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import wtf.boomy.mods.skinchanger.SkinChangerMod;
import wtf.boomy.mods.skinchanger.utils.backend.ThreadFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ClassTransformer implements IClassTransformer {

    private boolean patchSkinMethod = true;
    private boolean patchCapeMethod = true;
    private boolean patchSkinTypeMethod = true;

    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    public ClassTransformer() {
        ThreadFactory threadFactory = new ThreadFactory("SkinChangerTransformer");

        threadFactory.runAsync(() -> {
            try {
                String defaultText = "PatchSkins: yes" + System.lineSeparator() + "PatchCapes: yes" + System.lineSeparator() + "PatchSkinType: yes";
                File file = new File(new File("config", "skinchanger"), "asm.text");

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

                        String[] components = s.split(": ", 1);
                        String id = components[0];
                        String value = components[1];

                        boolean toPatch = value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("1");

                        if (id.equalsIgnoreCase("PatchSkins")) {
                            this.patchSkinMethod = toPatch;
                        }

                        if (id.equalsIgnoreCase("PatchCapes")) {
                            this.patchCapeMethod = toPatch;
                        }

                        if (id.equalsIgnoreCase("PatchSkinType")) {
                            this.patchSkinTypeMethod = toPatch;
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
        if (!this.patchSkinMethod && !this.patchCapeMethod && !this.patchSkinTypeMethod) {
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
        }

        return bytes;
    }

    private void transformNetworkPlayerInfo(boolean isDevEnv, ClassNode clazz, MethodNode method) {
        String methodName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(clazz.name, method.name, method.desc);

        if (this.patchSkinMethod && methodName.equals("getLocationSkin")) {
            System.out.println("Patching getLocationSkin (" + method.name + ")");

            method.instructions.insert(createForName(isDevEnv, "isUsingSkin", "getSkin"));

            System.out.println("Finished patching getLocationSkin (" + method.name + ")");

            SkinChangerMod.getInstance().getStorage().setSkinPatchApplied(true);
        } else if (this.patchCapeMethod && methodName.equals("getLocationCape")) {
            System.out.println("Patching getLocationCape (" + method.name + ")");

            method.instructions.insert(createForName(isDevEnv, "isUsingCape", "getCape"));

            System.out.println("Finished patching getLocationCape (" + method.name + ")");

            SkinChangerMod.getInstance().getStorage().setCapePatchApplied(true);
        } else if (this.patchSkinTypeMethod && methodName.equalsIgnoreCase("getSkinType")) {
            System.out.println("Patching getSkinType (" + method.name + ")");

            method.instructions.insert(createForSkinType(isDevEnv));

            System.out.println("Finished patching getSkinType (" + method.name + ")");

            SkinChangerMod.getInstance().getStorage().setSkinTypePatchApplied(true);
        }
    }

    private InsnList createForName(boolean devEnv, String isUsingX, String getX) {
        String gameProfileField = devEnv ? "gameProfile" : "field_178867_a";

        // Constructs a list of bytecode asm instructions to and adds them to the start of the method
        return constructList(
                // We load the gameProfile from the class
                // and send it to SkinStorage#isUsingSkin
                getModInstance(),
                getStorage(),

                // We load it into the stack
                new VarInsnNode(Opcodes.ALOAD, 0),

                // We call the isUsingX statement in the SkinStorage class, parsing in the GameProfile from the NetworkPlayerInfo file
                new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/network/NetworkPlayerInfo", gameProfileField, "Lcom/mojang/authlib/GameProfile;"),
                invokeVirtual("wtf/boomy/mods/skinchanger/cosmetic/impl/SkinChangerStorage", isUsingX, "(Lcom/mojang/authlib/GameProfile;)Z"),

                // Using the knowledge above, we continue if the isUsingX method returns true
                whenTrue(
                        // We invoke the getX method from the SkinStorage class
                        getModInstance(),
                        getStorage(),
                        invokeVirtual("wtf/boomy/mods/skinchanger/cosmetic/impl/SkinChangerStorage", getX, "()Lnet/minecraft/util/ResourceLocation;"),

                        // Finally, we return the X value they wanted, the method has been successfully injected into.
                        new InsnNode(Opcodes.ARETURN)
                )
        );
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
                invokeVirtual("wtf/boomy/mods/skinchanger/cosmetic/impl/SkinChangerStorage", "getSkinType", "(Lcom/mojang/authlib/GameProfile;)Ljava/lang/String;"),
                new VarInsnNode(Opcodes.ASTORE, 1),
                new VarInsnNode(Opcodes.ALOAD, 1),
                new JumpInsnNode(Opcodes.IFNULL, skip),
                new VarInsnNode(Opcodes.ALOAD, 1),
                new InsnNode(Opcodes.ARETURN),
                skip
        );
    }

    private MethodInsnNode getModInstance() {
        return new MethodInsnNode(Opcodes.INVOKESTATIC, "wtf/boomy/mods/skinchanger/SkinChangerMod", "getInstance", "()Lwtf/boomy/mods/skinchanger/SkinChangerMod;", false);
    }

    private MethodInsnNode getStorage() {
        return invokeVirtual("wtf/boomy/mods/skinchanger/SkinChangerMod", "getStorage", "()Lwtf/boomy/mods/skinchanger/cosmetic/impl/SkinChangerStorage;");
    }

    private MethodInsnNode invokeVirtual(String owner, String name, String desc) {
        return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, owner, name, desc, false);
    }

    private InsnList whenTrue(Object... args) {
        LabelNode label = new LabelNode();

        return constructList(new JumpInsnNode(Opcodes.IFEQ, label), constructList(args), label);
    }

    private InsnList whenLess(Object... args) {
        LabelNode label = new LabelNode();

        return constructList(new JumpInsnNode(Opcodes.IFLE, label), constructList(args), label);
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
     * @param file the file to save the value to
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
}
