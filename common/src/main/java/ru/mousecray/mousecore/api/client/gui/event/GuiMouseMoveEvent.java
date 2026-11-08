/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.event;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.mouseproject.api.client.gui.MGuiElement;
import ru.mousecray.mouseproject.api.client.gui.misc.MoveDirection;

@SideOnly(Side.CLIENT)
public class GuiMouseMoveEvent<T extends MGuiElement<T>> extends GuiMouseEvent<T> {
    private MoveDirection moveDirection;

    void setMoveDirection(MoveDirection moveDirection) { this.moveDirection = moveDirection; }
    public MoveDirection getMoveDirection()            { return moveDirection; }
}