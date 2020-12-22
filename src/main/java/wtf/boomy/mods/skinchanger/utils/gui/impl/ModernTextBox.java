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

package wtf.boomy.mods.skinchanger.utils.gui.impl;

import java.awt.Color;

import net.minecraft.client.gui.FontRenderer;

import org.lwjgl.input.Keyboard;
import wtf.boomy.mods.skinchanger.utils.gui.ModernGui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatAllowedCharacters;

/**
 * A modified vanilla class textbox
 */
public class ModernTextBox {
    
    private final int id;
    
    public int x;
    public int y;
    
    public int width;
    public int height;
    
    private String text = "";
    
    private int maxStringLength = 16;
    private int cursorCounter;
    
    private boolean isFocused = false;
    private final boolean isEnabled = true;
    
    private int lineScrollOffset; // The current character index that should be used as start of the rendered text.
    private int cursorPosition;
    
    private int selectionEnd;
    
    private final String noTextMessage;
    
    public ModernTextBox(int componentId, int x, int y, int width, int height) {
        this(componentId, x, y, width, height, "Write Here!");
    }
    
    public ModernTextBox(int componentId, int x, int y, int width, int height, String noTextMessage) {
        this.id = componentId;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.noTextMessage = noTextMessage;
    }
    
    /**
     * Increments the cursor counter
     */
    public void updateCursorCounter() {
        ++this.cursorCounter;
    }
    
    /**
     * Sets the text of the textbox
     */
    public void setText(String text) {
        if (text == null) {
            this.text = "";
            this.setCursorPositionEnd();
            return;
        }
        
        this.setCursorPositionEnd();
    }
    
    /**
     * @return Returns the contents of the textbox
     */
    public String getText() {
        return this.text;
    }
    
    /**
     * @return returns the text between the cursor and selectionEnd
     */
    public String getSelectedText() {
        int i = Math.min(this.cursorPosition, this.selectionEnd);
        int j = Math.max(this.cursorPosition, this.selectionEnd);
        return this.text.substring(i, j);
    }
    
    /**
     * replaces selected text, or inserts text at the position on the cursor
     *
     * @param text the text to write
     */
    public void writeText(String text) {
        String s = "";
        String s1 = ChatAllowedCharacters.filterAllowedCharacters(text);
        int i = Math.min(this.cursorPosition, this.selectionEnd);
        int j = Math.max(this.cursorPosition, this.selectionEnd);
        int k = this.maxStringLength - this.text.length() - (i - j);
        int l;
        
        if (this.text.length() > 0) {
            s = s + this.text.substring(0, i);
        }
        
        if (k < s1.length()) {
            s = s + s1.substring(0, k);
            l = k;
        } else {
            s = s + s1;
            l = s1.length();
        }
        
        if (this.text.length() > 0 && j < this.text.length()) {
            s = s + this.text.substring(j);
        }
        
        this.text = s;
        this.moveCursorBy(i - this.selectionEnd + l);
    }
    
    /**
     * Deletes the specified number of words starting at the cursor position. Negative numbers will delete words left of
     * the cursor.
     *
     * @param words the words to delete
     */
    public void deleteWords(int words) {
        if (this.text.length() != 0) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                this.deleteFromCursor(this.getNthWordFromCursor(words) - this.cursorPosition);
            }
        }
    }
    
    /**
     * delete the selected text, otherwise deletes characters from either side of the cursor. params: delete num
     */
    public void deleteFromCursor(int num) {
        if (this.text.length() != 0) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                boolean flag = num < 0;
                int i = flag ? this.cursorPosition + num : this.cursorPosition;
                int j = flag ? this.cursorPosition : this.cursorPosition + num;
                
                String newText = "";
                
                if (i >= 0) {
                    newText = this.text.substring(0, i);
                }
                
                if (j < this.text.length()) {
                    newText = newText + this.text.substring(j);
                }
                
                this.text = newText;
                
                if (flag) {
                    moveCursorBy(num);
                }
            }
        }
    }
    
    public int getId() {
        return this.id;
    }
    
    /**
     * see @getNthNextWordFromPos() params: N, position
     */
    public int getNthWordFromCursor(int position) {
        return this.getNthWordFromPos(position, this.getCursorPosition());
    }
    
    /**
     * gets the position of the nth word. N may be negative, then it looks backwards. params: N, position
     */
    public int getNthWordFromPos(int number, int position) {
        return this.getNthWordFromPos(number, position, true);
    }
    
    public int getNthWordFromPos(int number, int position, boolean something) {
        int i = position;
        boolean flag = number < 0;
        int j = Math.abs(number);
        
        for (int k = 0; k < j; ++k) {
            if (!flag) {
                int l = this.text.length();
                i = this.text.indexOf(32, i);
                
                if (i == -1) {
                    i = l;
                } else {
                    while (something && i < l && this.text.charAt(i) == 32) {
                        ++i;
                    }
                }
            } else {
                while (something && i > 0 && this.text.charAt(i - 1) == 32) {
                    --i;
                }
                
                while (i > 0 && this.text.charAt(i - 1) != 32) {
                    --i;
                }
            }
        }
        
        return i;
    }
    
    /**
     * Moves the text cursor by a specified number of characters and clears the selection
     */
    public void moveCursorBy(int moveBy) {
        this.setCursorPosition(this.selectionEnd + moveBy);
    }
    
    /**
     * sets the position of the cursor to the provided index
     */
    public void setCursorPosition(int position) {
        this.cursorPosition = position;
        int i = this.text.length();
        this.cursorPosition = clamp(this.cursorPosition, i);
        this.setSelectionPos(this.cursorPosition);
    }
    
    /**
     * sets the cursors position to the beginning
     */
    public void setCursorPositionZero() {
        this.setCursorPosition(0);
    }
    
    /**
     * sets the cursors position to after the text
     */
    public void setCursorPositionEnd() {
        this.setCursorPosition(this.text.length());
    }
    
    /**
     * Call this method from your GuiScreen to process the keys into the textbox
     */
    public void onKeyTyped(char c, int keyCode) {
        if (!this.isFocused) {
            return;
        }
        
        if (ModernGui.isKeyComboCtrlA(keyCode)) {
            this.setCursorPositionEnd();
            this.setSelectionPos(0);
        } else if (ModernGui.isKeyComboCtrlC(keyCode)) {
            ModernGui.setClipboardString(getSelectedText());
        } else if (ModernGui.isKeyComboCtrlV(keyCode)) {
            if (this.isEnabled) {
                this.writeText(ModernGui.getClipboardString());
            }
            
        } else if (ModernGui.isKeyComboCtrlX(keyCode)) {
            ModernGui.setClipboardString(getSelectedText());
            
            if (this.isEnabled) {
                this.writeText("");
            }
        } else {
            switch (keyCode) {
                case 14:
                    if (ModernGui.isCtrlKeyDown()) {
                        if (this.isEnabled) {
                            deleteWords(-1);
                        }
                    } else if (this.isEnabled) {
                        deleteFromCursor(-1);
                    }
                    
                    return;
                case 199:
                    if (ModernGui.isShiftKeyDown()) {
                        setSelectionPos(0);
                    } else {
                        setCursorPositionZero();
                    }
                    return;
                case 203:
                    if (ModernGui.isShiftKeyDown()) {
                        if (ModernGui.isCtrlKeyDown()) {
                            setSelectionPos(this.getNthWordFromPos(-1, this.getSelectionEnd()));
                        } else {
                            setSelectionPos(this.getSelectionEnd() - 1);
                        }
                    } else if (ModernGui.isCtrlKeyDown()) {
                        setCursorPosition(this.getNthWordFromCursor(-1));
                    } else {
                        moveCursorBy(-1);
                    }
                    
                    return;
                case 205:
                    if (ModernGui.isShiftKeyDown()) {
                        if (ModernGui.isCtrlKeyDown()) {
                            this.setSelectionPos(this.getNthWordFromPos(1, this.getSelectionEnd()));
                        } else {
                            this.setSelectionPos(this.getSelectionEnd() + 1);
                        }
                    } else if (ModernGui.isCtrlKeyDown()) {
                        this.setCursorPosition(this.getNthWordFromCursor(1));
                    } else {
                        this.moveCursorBy(1);
                    }
                    return;
                case 207:
                    if (ModernGui.isShiftKeyDown()) {
                        this.setSelectionPos(this.text.length());
                    } else {
                        this.setCursorPositionEnd();
                    }
                    return;
                case 211:
                    if (ModernGui.isCtrlKeyDown()) {
                        if (this.isEnabled) {
                            this.deleteWords(1);
                        }
                    } else if (this.isEnabled) {
                        this.deleteFromCursor(1);
                    }
                    return;
                default:
                    if (ChatAllowedCharacters.isAllowedCharacter(c)) {
                        if (this.isEnabled) {
                            this.writeText(Character.toString(c));
                        }
                    }
            }
        }
    }
    
    /**
     * Args: x, y, buttonClicked
     */
    public void mouseClicked(int x, int y, int buttonClicked) {
        boolean flag = x >= this.x && x < this.x + this.width && y >= this.y && y < this.y + this.height;
        
        if (this.isEnabled) {
            this.setFocused(flag);
        }
        
        if (this.isFocused && flag && buttonClicked == 0) {
            int i = x - this.x;
            
            i -= 4;
            
            FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
            
            String s = fontRenderer.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
            this.setCursorPosition(fontRenderer.trimStringToWidth(s, i).length() + this.lineScrollOffset);
        }
    }
    
    /**
     * Draws the textbox
     */
    public void drawTextBox() {
        Color backgroundColor;
        
        if (this.isEnabled) {
            backgroundColor = new Color(0, 148, 255, 75);
        } else {
            backgroundColor = new Color(0, 125, 215, 75);
        }
        
        ModernGui.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, backgroundColor.getRGB());
        
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
    
        int enabledColor = 14737632;
        int disabledColor = 7368816;
        
        int textColor = this.isEnabled ? enabledColor : disabledColor;
        int j = this.cursorPosition - this.lineScrollOffset;
        int lengthOfSelection = this.selectionEnd - this.lineScrollOffset;
        String trimmedString = fontRenderer.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
        boolean flag = j >= 0 && j <= trimmedString.length();
        boolean flag1 = this.isFocused && this.cursorCounter / 6 % 2 == 0 && flag;
        int l = this.x + 4;
        int i1 = this.y + (this.height - 8) / 2;
        int j1 = l;
        
        if (lengthOfSelection > trimmedString.length()) {
            lengthOfSelection = trimmedString.length();
        }
        
        if (trimmedString.isEmpty() && !this.isFocused && this.isEnabled) {
            fontRenderer.drawString(this.noTextMessage, ((this.x + (float) this.width / 2) - (float) fontRenderer.getStringWidth(this.noTextMessage) / 2), this.y + (float) this.height / 2 - 4, textColor, false);
            return;
        }
        
        if (trimmedString.length() > 0) {
            String s1 = flag ? trimmedString.substring(0, j) : trimmedString;
            
            j1 = fontRenderer.drawString(s1, (float) l, (float) i1, textColor, false);
        }
        
        boolean drawFlashingLine = this.cursorPosition < this.text.length() || this.text.length() >= getMaxStringLength();
        
        int k1 = j1;
        
        if (!flag) {
            k1 = j > 0 ? l + this.width : l;
        } else if (drawFlashingLine) {
            k1 = j1 - 1;
            --j1;
        }
        
        if (trimmedString.length() > 0 && flag && j < trimmedString.length()) {
            fontRenderer.drawString(trimmedString.substring(j), (float) j1, (float) i1, textColor, false);
        }
        
        if (flag1) {
            if (drawFlashingLine) {
                ModernGui.drawRect(k1, i1 - 1, k1 + 1, i1 + 1 + fontRenderer.FONT_HEIGHT, -3092272);
            } else {
                fontRenderer.drawString("_", (float) k1, (float) i1, textColor, false);
            }
        }
        
        if (lengthOfSelection != j) {
            int widthOfStr = l + fontRenderer.getStringWidth(trimmedString.substring(0, lengthOfSelection));
            
            drawCursorVertical(k1, i1 - 1, widthOfStr - 1, i1 + 1 + fontRenderer.FONT_HEIGHT);
        }
    }
    
    /**
     * draws the vertical line cursor in the textbox
     */
    private void drawCursorVertical(int startX, int endY, int endX, int startY) {
        if (startX < endX) {
            int tempX = startX;
            
            startX = endX;
            endX = tempX;
        }
        
        if (endY < startY) {
            int tempY = endY;
            
            endY = startY;
            startY = tempY;
        }
        
        if (endX > this.x + this.width) {
            endX = this.x + this.width;
        }
        
        if (startX > this.x + this.width) {
            startX = this.x + this.width;
        }
        
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        
        GlStateManager.color(0.0F, 0.0F, 255.0F, 255.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.enableColorLogic();
        GlStateManager.colorLogicOp(5387);
        
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(startX, startY, 0.0D).endVertex();
        worldrenderer.pos(endX, startY, 0.0D).endVertex();
        worldrenderer.pos(endX, endY, 0.0D).endVertex();
        worldrenderer.pos(startX, endY, 0.0D).endVertex();
        
        tessellator.draw();
        GlStateManager.disableColorLogic();
        GlStateManager.enableTexture2D();
    }
    
    public void setMaxStringLength(int length) {
        this.maxStringLength = length;
        
        if (this.text.length() > length) {
            this.text = this.text.substring(0, length);
        }
    }
    
    /**
     * returns the maximum number of character that can be contained in this textbox
     */
    public int getMaxStringLength() {
        return this.maxStringLength;
    }
    
    /**
     * returns the current position of the cursor
     */
    public int getCursorPosition() {
        return this.cursorPosition;
    }
    
    /**
     * Sets focus to this gui element
     */
    public void setFocused(boolean isFocused) {
        if (!isFocused) {
            Keyboard.enableRepeatEvents(false);
        }
        
        if (isFocused && !this.isFocused) {
            Keyboard.enableRepeatEvents(true);
            
            this.cursorCounter = 0;
        }
        this.isFocused = isFocused;
    }
    
    /**
     * the side of the selection that is not the cursor, may be the same as the cursor
     */
    public int getSelectionEnd() {
        return this.selectionEnd;
    }
    
    /**
     * returns the width of the textbox depending on if background drawing is enabled
     */
    public int getWidth() {
        return this.width - 8;
    }
    
    /**
     * Sets the position of the selection anchor (i.e. position the selection was started at)
     */
    public void setSelectionPos(int pos) {
        int i = this.text.length();
        
        if (pos > i) {
            pos = i;
        }
        if (pos < 0) {
            pos = 0;
        }
        
        this.selectionEnd = pos;
        
        FontRenderer renderer = Minecraft.getMinecraft().fontRendererObj;
        
        if (renderer != null) {
            
            if (this.lineScrollOffset > i) {
                this.lineScrollOffset = i;
            }
            
            int j = this.getWidth();
            String s = renderer.trimStringToWidth(this.text.substring(this.lineScrollOffset), j);
            int k = s.length() + this.lineScrollOffset;
            
            if (pos == this.lineScrollOffset) {
                this.lineScrollOffset -= renderer.trimStringToWidth(this.text, j, true).length();
            }
            
            if (pos > k) {
                this.lineScrollOffset += pos - k;
            } else if (pos <= this.lineScrollOffset) {
                this.lineScrollOffset -= this.lineScrollOffset - pos;
            }
            
            this.lineScrollOffset = clamp(this.lineScrollOffset, i);
        }
    }
    
    private int clamp(int input, int max) {
        return input > max ? max : Math.max(input, 0);
    }
}