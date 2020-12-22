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

package wtf.boomy.mods.skinchanger.configuration.meta;

import java.lang.reflect.Field;

/**
 * Stores the data for a serializable field, allows for an OOP approach to retrieving data from a field.
 *
 * @author boomboompower
 */
public class ConfigurationData {
    
    private final Field field;
    private final String saveName;
    private final boolean overwriteOnLoad;
    
    private Object parent; // null for static fields
    
    /**
     * Constructor for a serializable field
     *
     * @param fieldIn         the field to load data from
     * @param saveName        the name of the field as it appears in the json
     * @param overwriteOnLoad true if the data of the field should be overwritten as soon as it is loaded.
     */
    public ConfigurationData(Field fieldIn, String saveName, boolean overwriteOnLoad) {
        this.field = fieldIn;
        this.saveName = saveName;
        this.overwriteOnLoad = overwriteOnLoad;
    }
    
    /**
     * Returns the actual field instance which we will modify
     *
     * @return the field instance of the value we are saving/loading to/from memory.
     */
    public Field getField() {
        return this.field;
    }
    
    /**
     * Returns the name of the field as it appears in the json
     *
     * @return the name of the field in the JSON, e.g "varXYZ" can be called "xyz" in json.
     */
    public String getSaveName() {
        return this.saveName;
    }
    
    /**
     * Should this field be immediately updated as soon as it is loaded?
     *
     * @return true if the field should be updated as soon as it is loaded
     */
    public boolean isOverwriteOnLoad() {
        return this.overwriteOnLoad;
    }
    
    /**
     * Retrieves the value from the field using the instance provided in the constructor.
     * In the event of an error this method will simply return null and print an error.
     *
     * @return the value of the field or null on error.
     */
    public Object getValue() {
        boolean accessible = this.field.isAccessible();
        
        try {
            boolean modified = false;
            
            if (!accessible) {
                modified = true;
                
                this.field.setAccessible(true);
            }
            
            Object value = this.field.get(this.parent);
            
            if (modified) {
                this.field.setAccessible(false);
            }
            
            return value;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Sets the value of this field, if an error occurs an exception will be silently thrown
     *
     * @param value the value to set the field to
     */
    public void setValue(Object value) {
        boolean accessible = this.field.isAccessible();
        
        try {
            if (!accessible) {
                this.field.setAccessible(true);
            }
            
            this.field.set(this.parent, value);
            
            if (accessible != this.field.isAccessible()) {
                this.field.setAccessible(accessible);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Initializes the field with a parent object (this is the class instance containing the field).
     * <br />
     * This method will be ignored if the field has already been initialized.
     *
     * @param parent the parent of the field
     */
    public void initialize(Object parent) {
        if (this.parent != null || parent == null) {
            return;
        }
        
        this.parent = parent;
    }
}
