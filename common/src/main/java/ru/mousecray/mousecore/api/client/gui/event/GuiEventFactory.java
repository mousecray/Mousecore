/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.event;

import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.mouseproject.api.client.gui.MGuiElement;
import ru.mousecray.mouseproject.api.client.gui.component.sound.MSoundSourceType;
import ru.mousecray.mouseproject.api.client.gui.misc.MoveDirection;
import ru.mousecray.mouseproject.api.client.gui.misc.ScrollDirection;

@SideOnly(Side.CLIENT)
public class GuiEventFactory {
    public static <T extends MGuiElement<T>> void pushMouseClickEvent(GuiMouseClickEvent<T> event, int x, int y) {
        event.reset();
        event.setMouseX(x);
        event.setMouseY(y);
    }

    public static <T extends MGuiElement<T>> void pushMouseMoveEvent(GuiMouseMoveEvent<T> event, int x, int y, MoveDirection moveDirection) {
        event.reset();
        event.setMouseX(x);
        event.setMouseY(y);
        event.setMoveDirection(moveDirection);
    }

    public static <T extends MGuiElement<T>> void pushMouseScrollEvent(GuiMouseScrollEvent<T> event, int x, int y, ScrollDirection scrollDirection, int scrollAmount) {
        event.reset();
        event.setMouseX(x);
        event.setMouseY(y);
        event.setScrollDirection(scrollDirection);
        event.setScrollAmount(Math.abs(scrollAmount));
    }

    public static <T extends MGuiElement<T>> void pushMouseDragEvent(GuiMouseDragEvent<T> event, int x, int y, MoveDirection moveDirection, int diffX, int diffY, int tickDown) {
        event.reset();
        event.setMouseX(x);
        event.setMouseY(y);
        event.setMoveDirection(moveDirection);
        event.setDiffX(diffX);
        event.setDiffY(diffY);
        event.setTickDown(tickDown);
    }

    public static <T extends MGuiElement<T>> void pushTickEvent(GuiTickEvent<T> event, int x, int y, float partialTick) {
        event.reset();
        event.setMouseX(x);
        event.setMouseY(y);
        event.setPartialTick(partialTick);
    }

    public static <T extends MGuiElement<T>> void pushTextTypedEvent(GuiTextTypedEvent<T> event, int x, int y, int cursorPos, int selectionEnd, String oldText, String newText) {
        event.reset();
        event.setMouseX(x);
        event.setMouseY(y);
        event.setCursorPos(cursorPos);
        event.setSelectionEnd(selectionEnd);
        event.setOldText(oldText);
        event.setNewText(newText);
    }

    public static <T extends MGuiElement<T>> void pushKeyEvent(GuiKeyEvent<T> event, int x, int y, char typedChar, int keyCode) {
        event.reset();
        event.setMouseX(x);
        event.setMouseY(y);
        event.setTypedChar(typedChar);
        event.setKeyCode(keyCode);
    }


    public static <T extends MGuiElement<T>> void pushSoundEvent(GuiSoundEvent<T> event, int x, int y, SoundHandler handler, SoundEvent sound, MSoundSourceType source) {
        event.reset();
        event.setMouseX(x);
        event.setMouseY(y);
        event.setSound(sound);
        event.setSource(source);
        event.setHandler(handler);
    }

    public static <T extends MGuiElement<T>> void pushSliderChangedEvent(GuiSliderChangedEvent<T> event, int x, int y, int oldValue, int newValue) {
        event.reset();
        event.setMouseX(x);
        event.setMouseY(y);
        event.setOldValue(oldValue);
        event.setNewValue(newValue);
    }
}