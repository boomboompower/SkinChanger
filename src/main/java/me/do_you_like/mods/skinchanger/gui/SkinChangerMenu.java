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

package me.do_you_like.mods.skinchanger.gui;

import java.awt.Color;

import me.do_you_like.mods.skinchanger.utils.gui.impl.ModernButton;
import me.do_you_like.mods.skinchanger.utils.gui.impl.ModernGui;
import me.do_you_like.mods.skinchanger.utils.gui.impl.ModernHeader;
import me.do_you_like.mods.skinchanger.utils.gui.impl.ModernSlider;

public class SkinChangerMenu extends ModernGui {

    // ModernHeader hack
    //
    // For the middle of the left half of the screen do:
    // xPosition = (this.width / 2) / scale
    //
    // For the middle of the right half of the screen do:
    // xPosition = (this.width / 2) * scale

    @Override
    public void onGuiOpen() {
        ModernHeader title = new ModernHeader((int) ((this.width / 2) / 1.5F), 12, "SkinChanger", 1.5F, false);

        title.setDrawCentered(true);

        // ----------------------------------

        ModernHeader skinSettings = new ModernHeader(95, 165, "Skin Settings", 1.20F, true, Color.RED);

        skinSettings.setOffsetBetweenDrawables(32F);

        skinSettings.getSubDrawables().add(new ModernButton(12, 5, 20, "Load from Player").setAsPartOfHeader(skinSettings));
        skinSettings.getSubDrawables().add(new ModernButton(13, 5, 20, "Load from URL").setAsPartOfHeader(skinSettings));
        skinSettings.getSubDrawables().add(new ModernButton(14, 5, 20, "Load from File").setAsPartOfHeader(skinSettings));
        skinSettings.getSubDrawables().add(new ModernButton(15, 5, 20, "Reset Skin").setAsPartOfHeader(skinSettings));

        // ----------------------------------

        ModernHeader capeSettings = new ModernHeader(95, 585, "Cape Settings", 1.25F, true, Color.GREEN);

        capeSettings.getSubDrawables().add(new ModernButton(12, 5, 20, "Load from Player").setAsPartOfHeader(capeSettings));
        capeSettings.getSubDrawables().add(new ModernButton(13, 5, 20, "Load from URL").setAsPartOfHeader(capeSettings));
        capeSettings.getSubDrawables().add(new ModernButton(14, 5, 20, "Load from File").setAsPartOfHeader(capeSettings));
        capeSettings.getSubDrawables().add(new ModernButton(15, 5, 20, "Reset Skin").setAsPartOfHeader(capeSettings));

        // ----------------------------------

        ModernHeader recentSkins = new ModernHeader(525, 165, "Recent Skins", 1.3F, true, Color.YELLOW);

        // ----------------------------------

        ModernHeader recentCapes = new ModernHeader(525, 585, "Recent Capes", 1.33F, true, Color.LIGHT_GRAY);

        // ----------------------------------

        this.headerList.add(title);
        this.headerList.add(skinSettings);
        this.headerList.add(capeSettings);
        this.headerList.add(recentSkins);
        this.headerList.add(recentCapes);

        this.sliderList.add(new ModernSlider(5, this.width / 2 - 100, this.height / 2 + 74, 200, 20, "Scale: ", 1.0F, 200.0F, 100.0F) {
            @Override
            public void onSliderUpdate() {
                System.out.println(getValue() / 100);

                for (ModernHeader header : SkinChangerMenu.this.headerList) {
                    if (header == title) {
                        continue;
                    }

                    header.setScaleSize((float) (getValue() / 100.0D));
                }
            }
        });

        this.sliderList.add(new ModernSlider(6, this.width / 2 - 100, this.height / 2 + 98, 200, 20, "Offset: ", 1.0F, 100.0F, 50.0F) {
            @Override
            public void onSliderUpdate() {
                skinSettings.getWidth();

                //skinSettings.setOffsetBetweenDrawables((float) (getValue() / 1.0D));
            }
        });
    }

    @Override
    public void onGuiClose() {
        this.headerList.clear();
    }

    @Override
    public void preRender() {
    }

    @Override
    public void onRender(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        drawCenteredString(this.fontRendererObj, "by boomboompower", this.width / 2, 16, Color.CYAN.getRGB());
    }

    @Override
    public void postRender() {

    }

    @Override
    public void buttonPressed(ModernButton button) {

    }

    @Override
    public void onKeyTyped(int keyCode, char keyCharacter) {

    }


}
