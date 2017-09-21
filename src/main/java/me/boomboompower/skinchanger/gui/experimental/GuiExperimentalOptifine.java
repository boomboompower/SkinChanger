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

package me.boomboompower.skinchanger.gui.experimental;

import me.boomboompower.skinchanger.SkinChangerMod;
import me.boomboompower.skinchanger.capes.CapeManager;
import me.boomboompower.skinchanger.gui.utils.FakePlayerUtils;
import me.boomboompower.skinchanger.gui.utils.ModernButton;
import me.boomboompower.skinchanger.gui.utils.ModernGui;
import me.boomboompower.skinchanger.gui.utils.ModernTextBox;
import me.boomboompower.skinchanger.utils.ChatColor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.UUID;

public class GuiExperimentalOptifine extends ModernGui {

    private FakePlayerUtils.FakePlayer fakePlayer = FakePlayerUtils.getFakePlayer();
    private CapeManager fakePlayerCapeManager = new CapeManager(this.fakePlayer, false);

    private ModernTextBox textField;

    public GuiExperimentalOptifine() {
        SkinChangerMod.getInstance().getSkinManager().updatePlayer(null);
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        this.textList.add(this.textField = new ModernTextBox(0, this.width / 2 - 150, this.height / 2 - 22, 300, 20));

        this.buttonList.add(new ModernButton(0, this.width / 2 - 75, this.height / 2 + 26, 150, 20, "Preview cape"));
        this.buttonList.add(new ModernButton(1, this.width / 2 - 75, this.height / 2 + 50, 150, 20, "Reset cape"));
        this.buttonList.add(new ModernButton(2, this.width / 2 - 75, this.height / 2 + 74, 150, 20, "Confirm cape"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        drawEntityOnScreen(this.width / 2, this.height / 2 - 45, 35, this.width / 2 - mouseX, (this.height / 2 - 90) - mouseY, this.fakePlayer, true);

        if (SkinChangerMod.getInstance().getWebsiteUtils().isDisabled()) {
            drawCenteredString(this.mc.fontRendererObj, ChatColor.RED + "The mod is currently disabled and will not work!", this.width / 2, this.height / 2 + 100, Color.WHITE.getRGB());
        } else {
            drawCenteredString(this.mc.fontRendererObj,ChatColor.WHITE + "Names are case sensitive! Ensure you are using the correct name", this.width / 2, this.height / 2 + 100, Color.WHITE.getRGB());
        }
    }

    @Override
    public void buttonPressed(ModernButton button) {
        if (SkinChangerMod.getInstance().getWebsiteUtils().isDisabled()) {
            sendChatMessage("SkinChangerMod is currently disabled, check back soon!");
            this.mc.displayGuiScreen(null);
            return;
        }

        switch (button.id) {
            case 0:
                if (!this.textField.getText().isEmpty() && this.textField.getText().length() >= 2) {
                    this.fakePlayerCapeManager.addCape(downloadCape(this.textField.getText()));
                }
                break;
            case 1:
                SkinChangerMod.getInstance().getCapeManager().setExperimental(false);
                SkinChangerMod.getInstance().getCapeManager().removeCape();
                this.mc.displayGuiScreen(null);
                break;
            case 2:
                if (!this.textField.getText().isEmpty() && this.textField.getText().length() >= 2) {
                    SkinChangerMod.getInstance().getCapeManager().setExperimental(true);
                    SkinChangerMod.getInstance().getCapeManager().addCape(downloadCape(this.textField.getText()));
                    this.mc.displayGuiScreen(null);
                }
                break;
            default:
                this.mc.displayGuiScreen(null);
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        SkinChangerMod.getInstance().getLoader().save();
    }

    public ResourceLocation downloadCape(String name) {
        if (name != null && !name.isEmpty()) {
            final String url = "http://s.optifine.net/capes/" + name + ".png";
            final String id = UUID.nameUUIDFromBytes(name.getBytes()).toString();

            final ResourceLocation rl = new ResourceLocation("ofcape/" + id);

            File file1 = new File(new File("./mods/skinchanger".replace("/", File.separator), "ofcape"), id);
            File file2 = new File(file1, id + ".png");

            TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

            IImageBuffer imageBuffer = new IImageBuffer() {
                @Override
                public BufferedImage parseUserSkin(BufferedImage img) {
                    int imageWidth = 64;
                    int imageHeight = 32;
                    int srcWidth = img.getWidth();

                    for (int srcHeight = img.getHeight(); imageWidth < srcWidth || imageHeight < srcHeight; imageHeight *= 2) {
                        imageWidth *= 2;
                    }

                    BufferedImage imgNew = new BufferedImage(imageWidth, imageHeight, 2);
                    Graphics g = imgNew.getGraphics();
                    g.drawImage(img, 0, 0, null);
                    g.dispose();
                    return imgNew;
                }

                @Override
                public void skinAvailable() {
                }
            };
            ThreadDownloadImageData textureCape = new ThreadDownloadImageData(file2, url, null, imageBuffer);
            textureManager.loadTexture(rl, textureCape);

            return rl;
        } else {
            System.out.println("L");
            return null;
        }
    }
}
