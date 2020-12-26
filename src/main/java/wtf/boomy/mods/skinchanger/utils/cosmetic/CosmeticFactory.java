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

package wtf.boomy.mods.skinchanger.utils.cosmetic;

import wtf.boomy.mods.skinchanger.SkinChangerMod;
import wtf.boomy.mods.skinchanger.utils.cosmetic.resources.ResourceLoader;
import wtf.boomy.mods.skinchanger.utils.cosmetic.impl.fakeplayer.FakePlayerRender;

/**
 * A class to contain all the cosmetic components of SkinChanger.
 * These are configurable in-game and will likely increase performance if they are disabled.
 *
 * @author boomboompower
 * @since 3.0.0
 */
public class CosmeticFactory {
    
    private FakePlayerRender fakePlayerRender;
    private ResourceLoader resourceLoader;
    
    /**
     * SkinChanger mod instance
     */
    private final SkinChangerMod mod;
    
    public CosmeticFactory(SkinChangerMod mod) {
        this.mod = mod;
    }
    
    /**
     * Returns the FakePlayer rendering class
     *
     * @return the fake player renderer
     */
    public FakePlayerRender getFakePlayerRender() {
        if (this.fakePlayerRender == null) {
            this.fakePlayerRender = new FakePlayerRender(this);
    
            this.mod.registerEvents(this.fakePlayerRender);
        }
        
        return this.fakePlayerRender;
    }
    
    /**
     * Returns a resource loader instance class, or creates
     * a new one if none already exists.
     *
     * @return the resource loader instance.
     */
    public ResourceLoader getResourceLoader() {
        if (this.resourceLoader == null) {
            this.resourceLoader = new ResourceLoader(this);
        }
        
        return resourceLoader;
    }
    
    public SkinChangerMod getMod() {
        return mod;
    }
}
