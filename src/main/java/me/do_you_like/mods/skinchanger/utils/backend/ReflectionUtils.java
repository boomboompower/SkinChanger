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

package me.do_you_like.mods.skinchanger.utils.backend;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

/**
 * A nice reflection class, using a mix of forges ReflectionHandler and some
 * of java's internal reflection implementations whilst remaining sun-free!
 */
public class ReflectionUtils {

    // Stores logs which have already been sent.
    private static final List<String> logs = new ArrayList<>();

    /**
     * Finds a method from a given name. Supports forge mapped names
     *
     * @param clazz the class to search
     * @param methodNames the different method names
     * @param methodTypes the constructor of the method
     *
     * @return the method if found.
     */
    public static <T> MethodHandle findMethod(Class<T> clazz, String[] methodNames, Class<?>... methodTypes) {
        final Method method = ReflectionHelper.findMethod(clazz, null, methodNames, methodTypes);
        
        try {
            return MethodHandles.lookup().unreflect(method);
        } catch (Exception e) {
            throw new ReflectionHelper.UnableToFindMethodException(methodNames, e);
        }
    }

    /**
     * Force sets a private value
     *
     * @param classToAccess the class to access
     * @param instance the instance of the class
     * @param value the value to set the field to
     * @param fieldNames the names of each field
     */
    public static <T, E> void setPrivateValue(Class<? super T> classToAccess, T instance, E value, String... fieldNames) {
        try {
            ReflectionHelper.setPrivateValue(classToAccess, instance, value,
                ObfuscationReflectionHelper.remapFieldNames(classToAccess.getName(), fieldNames));
        } catch (Throwable e) {
            sendLog("[" + classToAccess.getSimpleName() + "] No methods found for arguments: %s !", Arrays.toString(fieldNames));
        }
    }

    /**
     * Just so logs aren't spammed. Display the error once.
     *
     * @param message the message which will be sent
     * @param formatting formatting for the message
     */
    private static void sendLog(String message, Object... formatting) {
        if (logs.contains(message)) {
            return;
        }

        System.out.println(String.format(message, formatting));

        logs.add(message);
    }
}