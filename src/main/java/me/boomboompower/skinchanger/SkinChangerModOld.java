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

package me.boomboompower.skinchanger;

import me.boomboompower.skinchanger.utils.models.capes.CapeManager;
import me.boomboompower.skinchanger.config.ConfigLoader;
import me.boomboompower.skinchanger.utils.models.skins.SkinManager;

//@Mod(modid = SkinChangerMod.MOD_ID, version = SkinChangerMod.VERSION, acceptedMinecraftVersions = "*")
public class SkinChangerModOld {

    public static final String MOD_ID = "skinchanger";
    public static final String VERSION = "2.0.0";

    //private boolean renderingEnabled = true;

    //private WebsiteUtils websiteUtils;
    //private ConfigLoader loader;

    //private SkinManager skinManager;
    //private CapeManager capeManager;
    
    //private MojangHooker mojangHooker;

    //@Mod.Instance
    //private static SkinChangerMod instance;
    
    public SkinChangerModOld() {
    
    }

    //@Mod.EventHandler
    //public void preInit(FMLPreInitializationEvent event) {
        //ModMetadata data = event.getModMetadata();
        //data.description = ChatColor.AQUA + "A clientside mod that allows you to change your skin instantly!";
        //data.authorList.add("boomboompower");

        //this.websiteUtils = new WebsiteUtils("SkinChanger");
        //this.loader = new ConfigLoader(event.getSuggestedConfigurationFile());

        //this.skinManager = new SkinManager(this.mojangHooker = new MojangHooker(), Minecraft.getMinecraft().thePlayer, true);
        //this.capeManager = new CapeManager(Minecraft.getMinecraft().thePlayer, true);
    //}

    //@Mod.EventHandler
    //public void init(FMLInitializationEvent event) {
        //MinecraftForge.EVENT_BUS.register(new MainEvents(this));
        //ClientCommandHandler.instance.registerCommand(new MainCommand(this));
    
        //this.websiteUtils.begin();
        //this.loader.load();
    //}

    public SkinManager getSkinManager() {
        return null;
    }

    public CapeManager getCapeManager() {
        return null;
    }

    public ConfigLoader getLoader() {
        return null;
    }
    
    public static SkinChangerModOld getInstance() {
        return null;
    }

    public void setRenderingEnabled(boolean toggledIn) {
    }

    public boolean isRenderingEnabled() {
        return false;
    }
}
