package me.boomboompower.skinchanger.gui;

import me.boomboompower.skinchanger.gui.utils.ModernGui;

public class MixinsWarningGui extends ModernGui {

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
