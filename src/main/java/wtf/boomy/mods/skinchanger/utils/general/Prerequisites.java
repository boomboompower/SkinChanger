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

package wtf.boomy.mods.skinchanger.utils.general;

/**
 * Simple prerequisites for varies values in the code. Used to validate certain code operations
 * and prevent me doing anything which some may consider "too hectic".
 *
 * @author boomboompower
 * @since 3.0.0
 */
public final class Prerequisites {
    
    // Private constructor, no instances allowed
    private Prerequisites(Prerequisites paradox) {
        // To create an instance of this class one must have an instance of this class.
        Prerequisites.notNull(paradox, "Paradox.");
    }
    
    /**
     * Ensures a value is not null. If it is, an error will be thrown.
     *
     * @param value the value to check
     */
    public static void notNull(Object value) {
        notNull(value, "Value was null.");
    }
    
    /**
     * Ensures a value is not null. If it is, an error will be thrown.
     *
     * @param value        the value to check
     * @param errorMessage the message to send
     */
    public static void notNull(Object value, String errorMessage) {
        if (value != null) {
            return;
        }
        
        // Hit em with the fat exception
        throw new IllegalArgumentException(errorMessage);
    }
    
    /**
     * Ensures a value is not null or empty. If it is, an error will be thrown.
     *
     * @param value the value to check
     */
    public static void notNullOrEmpty(String value) {
        notNullOrEmpty(value, "Value was either null or empty.");
    }
    
    /**
     * Ensures a value is not null or empty. If it is, an error will be thrown.
     *
     * @param value        the value to check
     * @param errorMessage the message to send
     */
    public static void notNullOrEmpty(String value, String errorMessage) {
        // If the value is not null and its not empty, return.
        if (value != null && !value.isEmpty()) {
            return;
        }
        
        // Hit em with the fat exception
        throw new IllegalArgumentException(errorMessage);
    }
    
    /**
     * Ensures a value (string) is not empty. If it is, an error will be thrown.
     *
     * @param value the value to check
     */
    public static void notEmpty(String value) {
        notEmpty(value, "Value was empty.");
    }
    
    /**
     * Ensures a value (string) is not empty. If it is, an error will be thrown.
     *
     * @param value        the value to check
     * @param errorMessage the message to send
     */
    public static void notEmpty(String value, String errorMessage) {
        if (value == null || !value.isEmpty()) {
            return;
        }
        
        // Hit em with the fat exception
        throw new IllegalArgumentException(errorMessage);
    }
    
    /**
     * Ensures a condition is met. If the condition is not met then an IllegalArgumentException
     * will be thrown with the default message.
     *
     * @param condition the condition to check, if its false then the error will be thrown
     */
    public static void conditionMet(boolean condition) {
        conditionMet(condition, "Condition was not met.");
    }
    
    /**
     * Ensures a condition is met. If the condition is not met then an IllegalArgumentException
     * will be thrown with an error message.
     *
     * @param condition    the condition to check, if its false then the error will be thrown
     * @param errorMessage the error message to be appended to the Stacktrace
     */
    public static void conditionMet(boolean condition, String errorMessage) {
        if (condition) {
            return;
        }
        
        // Hit em with the fat exception
        throw new IllegalArgumentException(errorMessage);
    }
}
