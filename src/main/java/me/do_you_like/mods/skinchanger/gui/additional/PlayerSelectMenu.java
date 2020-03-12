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

package me.do_you_like.mods.skinchanger.gui.additional;

import java.awt.Color;

import lombok.Getter;

import me.do_you_like.mods.skinchanger.SkinChangerMod;
import me.do_you_like.mods.skinchanger.gui.SkinChangerMenu;
import me.do_you_like.mods.skinchanger.utils.backend.CacheRetriever;
import me.do_you_like.mods.skinchanger.utils.backend.MojangHooker;
import me.do_you_like.mods.skinchanger.utils.backend.ThreadFactory;
import me.do_you_like.mods.skinchanger.utils.game.ChatColor;
import me.do_you_like.mods.skinchanger.utils.gui.impl.ModernButton;
import me.do_you_like.mods.skinchanger.utils.gui.impl.ModernTextBox;
import me.do_you_like.mods.skinchanger.utils.installing.InternetConnection;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class PlayerSelectMenu extends SkinChangerMenu {

    private static boolean loading;

    private final MojangHooker mojangHooker;
    private final CacheRetriever cacheRetriever;
    private final ThreadFactory threadFactory;

    private SkinChangerMenu skinChangerMenu;
    private StringSelectionType selectionType;

    private String errorMessage = "";

    public PlayerSelectMenu(SkinChangerMenu menu, StringSelectionType selectionType) {
        this.skinChangerMenu = menu;
        this.selectionType = selectionType;

        this.mojangHooker = SkinChangerMod.getInstance().getMojangHooker();
        this.cacheRetriever = SkinChangerMod.getInstance().getCacheRetriever();
        this.threadFactory = new ThreadFactory(selectionType.name());
    }

    @Override
    protected void onGuiInitExtra() {
        setAsSubMenu(this.skinChangerMenu);

        float boxWidth = 150;
        float boxHeight = 20;

        float xLocation = ((float) this.width / 4) - (boxWidth / 2);
        float yLocation = ((float) this.height / 2) - boxHeight;

        ModernTextBox entryBox = new ModernTextBox(0, (int) xLocation, (int) yLocation, (int) boxWidth, (int) boxHeight);

        this.textList.add(entryBox);

        yLocation += boxHeight + 4;

        ModernButton loadButton = new ModernButton(500, (int) xLocation, (int) yLocation, (int) boxWidth, (int) boxHeight, "Load");

        this.buttonList.add(loadButton);
    }

    @Override
    public void onRender(int mouseX, int mouseY, float partialTicks) {
        super.onRender(mouseX, mouseY, partialTicks);

        drawCenteredString(this.fontRendererObj, this.selectionType.getDisplaySentence(), this.width / 4, this.height / 2 - 40, Color.WHITE.getRGB());

        drawCenteredString(this.fontRendererObj, this.errorMessage, this.width / 2, this.height - 10, Color.WHITE.getRGB());

    }

    @Override
    protected void onButtonPressedExtra(ModernButton button) {
        // Already doing an operation. Just do nothing
        if (loading) {
            return;
        }

        if (Minecraft.getMinecraft().isCallingFromMinecraftThread()) {
            this.threadFactory.runAsync(() -> {
                loading = true;

                onButtonPressedExtra(button);

                loading = false;
            });

            //return;
        }

        String enteredText = this.textList.get(0).getText().trim();

        if (enteredText.isEmpty()) {
            return;
        }

        if (!this.selectionType.isValid(enteredText)) {
            String errorText = "";

            if (this.selectionType.isTypeOfUrl()) {
                this.errorMessage = "The entered value did not start with https:// or http://";
            } else if (this.selectionType.isTypeOfUsername()) {
                this.errorMessage = "The entered value was larger than valid username's";
            }

            this.errorMessage = ChatColor.RED + errorText;

            return;
        }

        if (button.getId() == 500) {
            switch (this.selectionType) {
                case P_USERNAME:
                    if (!InternetConnection.hasInternetConnection()) {
                        this.errorMessage = ChatColor.RED + "Could not connect to the internet. Make sure you have a stable internet connection!";

                        return;
                    }

                    String playerId = this.mojangHooker.getIdFromUsername(enteredText);
                    String userName = this.mojangHooker.getRealNameFromName(enteredText);

                    if (userName == null) {
                        userName = enteredText;
                    }

                    String finalUserName = userName;

                    Minecraft.getMinecraft().addScheduledTask(() -> {
                        ResourceLocation resourceLocation = this.mod.getMojangHooker().getSkinFromId(playerId);

                        boolean hasSlimSkin = this.mod.getMojangHooker().hasSlimSkin(finalUserName);

                        SkinChangerMenu.getFakePlayer().getPlayerInfo().setSkinType(hasSlimSkin ? "slim" : "default");
                        SkinChangerMenu.getFakePlayer().getPlayerInfo().setLocationSkin(resourceLocation);
                    });

                    break;
                case C_USERNAME:
                    String cacheName = "c" + enteredText;

                    // If the file exists in the cache we don't need internet.
                    if (!(this.cacheRetriever.doesCacheExist(cacheName) && !this.cacheRetriever.isCacheExpired(cacheName)) || !InternetConnection.hasInternetConnection()) {
                        this.errorMessage = ChatColor.RED + "Could not connect to the internet. Make sure you have a stable internet connection!";

                        return;
                    }

                    String url = "http://s.optifine.net/capes/" + enteredText + ".png";

                    ResourceLocation cape = this.cacheRetriever.loadIntoGame(cacheName, url);

                    SkinChangerMenu.getFakePlayer().getPlayerInfo().setLocationCape(cape);

                    break;
                case P_URL:
                    String cache = "u" + enteredText;

                    // If the file exists in the cache we don't need internet.
                    if (!(this.cacheRetriever.doesCacheExist(cache) && !this.cacheRetriever.isCacheExpired(cache)) || !InternetConnection.hasInternetConnection()) {
                        this.errorMessage = ChatColor.RED + "Could not connect to the internet. Make sure you have a stable internet connection!";

                        return;
                    }

                    // Skin URL Resource
                    ResourceLocation p_URL_Resource = this.cacheRetriever.loadIntoGame(cache, enteredText);

                    SkinChangerMenu.getFakePlayer().getPlayerInfo().setLocationSkin(p_URL_Resource);
                case C_URL:
                    String cacheC = "1" + enteredText;

                    // If the file exists in the cache we don't need internet.
                    if (!(this.cacheRetriever.doesCacheExist(cacheC) && !this.cacheRetriever.isCacheExpired(cacheC)) || !InternetConnection.hasInternetConnection()) {
                        this.errorMessage = ChatColor.RED + "Could not connect to the internet. Make sure you have a stable internet connection!";

                        return;
                    }

                    // Cape URL Resource
                    ResourceLocation c_URL_Resource = this.cacheRetriever.loadIntoGame(cacheC, enteredText);

                    SkinChangerMenu.getFakePlayer().getPlayerInfo().setLocationCape(c_URL_Resource);

                    break;
                default:
            }
        }
    }

    @Override
    public void handleIncomingInput(String playerName) {

    }

    @Override
    public void setRotation(float rotation) {
        this.rotation = rotation;

        this.skinChangerMenu.setRotation(rotation);
    }

    public enum StringSelectionType {
        P_USERNAME("Enter the username of the player."),
        C_USERNAME("Enter the username of the player."),

        P_URL("Enter the URL of the skin. (https://....)"),
        C_URL("Enter the URL of the cape. (https://....)");

        @Getter
        private String displaySentence;

        StringSelectionType(String displaySentence) {
            this.displaySentence = displaySentence;
        }

        /**
         * Checks the entered string to see if it complies with this Selection's rules.
         *
         * @param input the input string to validate
         * @return true if the string follows the specifications
         */
        public boolean isValid(String input) {
            if (input == null || input.isEmpty()) {
                return false;
            }

            if (isTypeOfUsername()) {
                return input.length() > 2 && input.length() < 16;
            }

            if (isTypeOfUrl()) {
                // In order of preference
                return input.startsWith("https://") || input.startsWith("http://") || input.startsWith("www.");
            }

            return false;
        }

        public boolean isTypeOfUrl() {
            return this == P_URL || this == C_URL;
        }

        public boolean isTypeOfUsername() {
            return this == P_USERNAME || this == C_USERNAME;
        }
    }
}
