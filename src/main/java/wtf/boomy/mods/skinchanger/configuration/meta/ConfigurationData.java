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

import wtf.boomy.mods.skinchanger.utils.general.Prerequisites;

import java.lang.reflect.Field;

/**
 * Stores the data for fields.
 */
public class ConfigurationData {

    private final Field field;
    private final String saveName;
    private final boolean overwriteOnLoad;

    private Object parent;

    public ConfigurationData(Field fieldIn, String saveName) {
        this(fieldIn, saveName, true);
    }

    public ConfigurationData(Field fieldIn, String saveName, boolean overwriteOnLoad) {
        Prerequisites.notNull(fieldIn);
        Prerequisites.notNull(saveName);

        this.field = fieldIn;
        this.saveName = saveName;
        this.overwriteOnLoad = overwriteOnLoad;
    }

    public Field getField() {
        return this.field;
    }

    public String getSaveName() {
        return this.saveName;
    }

    public boolean isOverwriteOnLoad() {
        return this.overwriteOnLoad;
    }

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

    public void initialize(Object parent) {
        if (this.parent != null || parent == null) {
            return;
        }

        this.parent = parent;
    }
}
