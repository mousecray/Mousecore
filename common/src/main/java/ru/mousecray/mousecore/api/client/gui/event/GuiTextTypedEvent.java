/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.event;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.mouseproject.api.client.gui.MGuiElement;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class GuiTextTypedEvent<T extends MGuiElement<T>> extends GuiKeyEvent<T> {
    private           int    cursorPos;
    private           int    selectionEnd;
    @Nullable private String oldText;
    @Nullable private String newText;

    void setCursorPos(int cursorPos)          { this.cursorPos = cursorPos; }
    void setSelectionEnd(int selectionEnd)    { this.selectionEnd = selectionEnd; }
    void setOldText(@Nullable String oldText) { this.oldText = oldText; }
    void setNewText(@Nullable String newText) { this.newText = newText; }

    public int getCursorPos()                 { return cursorPos; }
    public int getSelectionEnd()              { return selectionEnd; }
    @Nullable public String getOldText()      { return oldText; }
    @Nullable public String getNewText()      { return newText; }
}