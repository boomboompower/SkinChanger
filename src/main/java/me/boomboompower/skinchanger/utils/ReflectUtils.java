/*
 *     Copyright (C) 2017 boomboompower
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

package me.boomboompower.skinchanger.utils;

import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A nice reflection class, using a mix of forges ReflectionHandler and some
 * of java's internal reflection implementations whilst remaining sun-free!
 */
public class ReflectUtils {
    
    private static List<String> logs = new ArrayList<>();
    
    public static <T> MethodHandle findMethod(Class<T> clazz, String[] methodNames, Class<?>... methodTypes) {
        final Method method = ReflectionHelper.findMethod(clazz, null, methodNames, methodTypes);
        
        try {
            return MethodHandles.lookup().unreflect(method);
        } catch (Exception e) {
            throw new ReflectionHelper.UnableToFindMethodException(methodNames, e);
        }
    }
    
    public static <T, E> void setPrivateValue(Class<? super T> classToAccess, T instance, E value, String... fieldNames) {
        try {
            ReflectionHelper.setPrivateValue(classToAccess, instance, value,
                ObfuscationReflectionHelper.remapFieldNames(classToAccess.getName(), fieldNames));
        } catch (Throwable e) {
            sendLog("No methods found for arguments: %s !", Arrays.toString(fieldNames));
        }
    }
    
    private static void sendLog(String message, Object... formatting) {
        if (logs.contains(message)) {
            return;
        }
        
        System.out.println(String.format(message, formatting));
        logs.add(message);
    }
}