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

package wtf.boomy.mods.skinchanger.utils.game;

/**
 * Simple callback class, a resolution to api calls not being immediate.
 *
 * @param <T> the type of the value to be retrieved.
 */
public interface Callback<T> {
    
    /**
     * Called once a function has been run, the param t represents the value being returned.
     *
     * @param t the value for the callback to consume.
     */
    public void run(T t);
}
