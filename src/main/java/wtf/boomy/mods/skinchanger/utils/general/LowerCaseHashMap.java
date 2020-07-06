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

import java.util.HashMap;

/**
 * Bullshit hack for a HashMap to convert all keys to lowercase
 */
public class LowerCaseHashMap<K, V> extends HashMap<K, V> {
    
    @Override
    public boolean containsKey(Object key) {
        if (key instanceof String) {
            key = ((String) key).toLowerCase();
        }
    
        return super.containsKey(key);
    }
    
    @Override
    public V put(K key, V value) {
        if (key instanceof String) {
            // noinspection unchecked
            key = (K) ((String) key).toLowerCase();
        }
        
        return super.put(key, value);
    }
    
    @Override
    public boolean remove(Object key, Object value) {
        if (key instanceof String) {
            key = ((String) key).toLowerCase();
        }
        
        return super.remove(key, value);
    }
    
    @Override
    public V remove(Object key) {
        if (key instanceof String) {
            key = ((String) key).toLowerCase();
        }
        
        return super.remove(key);
    }
    
    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        if (key instanceof String) {
            // noinspection unchecked
            key = (K) ((String) key).toLowerCase();
        }
        
        return super.replace(key, oldValue, newValue);
    }
    
    @Override
    public V replace(K key, V value) {
        if (key instanceof String) {
            // noinspection unchecked
            key = (K) ((String) key).toLowerCase();
        }
        
        return super.replace(key, value);
    }
    
    @Override
    public V getOrDefault(Object key, V defaultValue) {
        if (key instanceof String) {
            key = ((String) key).toLowerCase();
        }
        
        return super.getOrDefault(key, defaultValue);
    }
}
