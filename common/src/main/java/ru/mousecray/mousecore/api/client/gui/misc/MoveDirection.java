/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.misc;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.mouseproject.api.client.gui.event.GuiMouseMoveEvent;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public enum MoveDirection {
    HORIZONTAL_RIGHT,
    HORIZONTAL_LEFT,
    VERTICAL_UP,
    VERTICAL_DOWN,
    DIAGONAL_RIGHT,
    DIAGONAL_LEFT;

    @Nullable
    public static MoveDirection getMoveDirection(int diffX, int diffY) {
        final int THRESHOLD = 1;
        if (Math.abs(diffX) < THRESHOLD && Math.abs(diffY) < THRESHOLD) return null;
        MoveDirection direction = null;
        if (Math.abs(diffX) > Math.abs(diffY)) {
            if (diffX > 0) direction = MoveDirection.HORIZONTAL_RIGHT;
            else if (diffX < 0) direction = MoveDirection.HORIZONTAL_LEFT;
        } else if (Math.abs(diffY) > Math.abs(diffX)) {
            if (diffY > 0) direction = MoveDirection.VERTICAL_DOWN;
            else if (diffY < 0) direction = MoveDirection.VERTICAL_UP;
        } else if (diffX != 0 && diffY != 0) {
            if (diffX > 0) direction = MoveDirection.DIAGONAL_RIGHT;
            else direction = MoveDirection.DIAGONAL_LEFT;
        }
        return direction;
    }

    @Nullable
    public static MoveDirection calculateMoveDirection(int newMouseX, int newMouseY, GuiMouseMoveEvent<?> moveEvent) {
        int diffX = newMouseX - moveEvent.getMouseX();
        int diffY = newMouseY - moveEvent.getMouseY();
        return MoveDirection.getMoveDirection(diffX, diffY);
    }
}