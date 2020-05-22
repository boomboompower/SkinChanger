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

package wtf.boomy.mods.skinchanger.gui.additional;

import lombok.Getter;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import wtf.boomy.mods.skinchanger.SkinChangerMod;
import wtf.boomy.mods.skinchanger.gui.SkinChangerMenu;
import wtf.boomy.mods.skinchanger.utils.backend.CacheRetriever;
import wtf.boomy.mods.skinchanger.utils.backend.MojangHooker;
import wtf.boomy.mods.skinchanger.utils.backend.ThreadFactory;
import wtf.boomy.mods.skinchanger.utils.gui.impl.ModernButton;
import wtf.boomy.mods.skinchanger.utils.gui.impl.ModernTextBox;
import wtf.boomy.mods.skinchanger.utils.installing.InternetConnection;

import java.awt.Color;
import java.util.Objects;
import java.util.UUID;

public class PlayerSelectMenu extends SkinChangerMenu {

    private static boolean loading;

    private final MojangHooker mojangHooker;
    private final CacheRetriever cacheRetriever;
    private final ThreadFactory threadFactory;

    private SkinChangerMenu skinChangerMenu;
    private StringSelectionType selectionType;

    private int errorMessageTimer = 0;
    private String lastErrorMessage = null;
    private String errorMessage = "";

    private ModernTextBox textBox;

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

        if (this.selectionType.isTypeOfUrl()) {
            entryBox.setMaxStringLength(520);
        } else if (this.selectionType.isTypeOfUUID()) {
            entryBox.setMaxStringLength(36);
        } else {
            entryBox.setMaxStringLength(16);
        }

        registerElement(entryBox);

        this.textBox = entryBox;

        yLocation += boxHeight + 4;

        ModernButton loadButton = new ModernButton(500, (int) xLocation, (int) yLocation, (int) boxWidth, (int) boxHeight, "Load");

        registerElement(loadButton);
    }

    @Override
    public void onRender(int mouseX, int mouseY, float partialTicks) {
        super.onRender(mouseX, mouseY, partialTicks);

        if (!Objects.equals(this.lastErrorMessage, this.errorMessage)) {
            this.errorMessageTimer = 0;

            this.lastErrorMessage = errorMessage;
        }

        int floatingPosition = cap(this.errorMessageTimer);

        drawCenteredString(this.fontRendererObj, this.selectionType.getDisplaySentence(), this.width / 4, this.height / 2 - 40, Color.WHITE.getRGB());

        drawCenteredString(this.fontRendererObj, this.errorMessage, this.width / 2, this.height - floatingPosition, Color.WHITE.getRGB());

        this.errorMessageTimer++;
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

        // Should never happen.
        if (this.textBox == null) {
            return;
        }

        String enteredText = this.textBox.getText().trim();

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

            this.errorMessage = errorText;

            return;
        }

        if (button.getId() == 500) {
            switch (this.selectionType) {
                case P_USERNAME:
                    if (!InternetConnection.hasInternetConnection()) {
                        this.errorMessage = "Could not connect to the internet. Make sure you have a stable internet connection!";

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

                        this.mod.getCosmeticFactory().getFakePlayerRender().setSkinType(hasSlimSkin ? "slim" : "default");
                        this.mod.getCosmeticFactory().getFakePlayerRender().setSkinLocation(resourceLocation);
                    });

                    break;
                case C_USERNAME:
                    String cacheName = "c" + enteredText;

                    // If the file exists in the cache we don't need internet.
                    if (!InternetConnection.hasInternetConnection()) {
                        this.errorMessage = "Could not connect to the internet. Make sure you have a stable internet connection!";

                        return;
                    }

                    String url = "http://s.optifine.net/capes/" + enteredText + ".png";

                    ResourceLocation cape = this.cacheRetriever.loadIntoGame(cacheName, url, CacheRetriever.CacheType.CAPE);

                    this.mod.getCosmeticFactory().getFakePlayerRender().setCapeLocation(cape);

                    break;
                case P_URL:
                    String cache = "u" + enteredText;

                    // If the file exists in the cache we don't need internet.
                    if (!InternetConnection.hasInternetConnection()) {
                        this.errorMessage = "Could not connect to the internet. Make sure you have a stable internet connection!";

                        return;
                    }

                    // Skin URL Resource
                    ResourceLocation p_URL_Resource = this.cacheRetriever.loadIntoGame(cache, enteredText, CacheRetriever.CacheType.SKIN);

                    this.mod.getCosmeticFactory().getFakePlayerRender().setCapeLocation(p_URL_Resource);
                case C_URL:
                    String cacheC = "1" + enteredText;

                    // If the file exists in the cache we don't need internet.
                    if (!InternetConnection.hasInternetConnection()) {
                        this.errorMessage = "Could not connect to the internet. Make sure you have a stable internet connection!";

                        return;
                    }

                    // Cape URL Resource
                    ResourceLocation c_URL_Resource = this.cacheRetriever.loadIntoGame(cacheC, enteredText, CacheRetriever.CacheType.CAPE);

                    this.mod.getCosmeticFactory().getFakePlayerRender().setCapeLocation(c_URL_Resource);

                    break;
                default:
            }
        }
    }

    public boolean handleIncomingInput(String incomingInput, StringSelectionType selectionType) {
        return false;
    }

    /**
     * Caps this integer between a few magic numbers
     *
     * @param in the number to cap
     * @return a number between 0 and 30.
     */
    private int cap(int in) {
        if (in < 0) {
            return 0;
        }

        // 30 is a magic number
        return Math.max(in, 30);
    }

    /**
     * Extra things
     *
     * @param parentMenu the SkinChanger menu
     * @param type the type which it should be switched to
     */
    public void displayExtra(SkinChangerMenu parentMenu, StringSelectionType type) {
        this.skinChangerMenu = parentMenu;

        this.selectionType = type;

        display();
    }

    /**
     * Tells this class which varient of itself it should use
     */
    public enum StringSelectionType {
        P_USERNAME("Enter the username of the player."),
        C_USERNAME("Enter the username of the player."),

        P_URL("Enter the URL of the skin. (https://....)"),
        C_URL("Enter the URL of the cape. (https://....)"),

        P_UUID("Enter the UUID of the player. (ABCD-EFGH-...)"),
        C_UUID("Enter the UUID of the player. (ABCD-EFGH-...)");

        @Getter
        private final String displaySentence;

        // If a UUID has been generated we should store
        // it so we don't have to parse it twice
        @Getter
        private UUID storedUUID;

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

            if (isTypeOfUUID()) {
                // Tiny performance increase
                this.storedUUID = SkinChangerMod.getInstance().getMojangHooker().getUUIDFromStrippedString(input);

                return this.storedUUID != null;
            }

            return false;
        }

        /**
         * Is this enum a type of URL?
         *
         * @return true if the enum is of type URL
         */
        public boolean isTypeOfUrl() {
            return this == P_URL || this == C_URL;
        }

        /**
         * Is this enum a type of username?
         *
         * @return true if the enum is of type username
         */
        public boolean isTypeOfUsername() {
            return this == P_USERNAME || this == C_USERNAME;
        }

        /**
         * Is this enum a type of UUID?
         *
         * @return true if the enum is of type UUID
         */
        public boolean isTypeOfUUID() {
            return this == P_UUID || this == C_UUID;
        }
    }
}
