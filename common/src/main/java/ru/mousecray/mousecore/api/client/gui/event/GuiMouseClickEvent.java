/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.event;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.mouseproject.api.client.gui.MGuiElement;
import ru.mousecray.mouseproject.api.client.gui.misc.ClickType;

@SideOnly(Side.CLIENT)
public class GuiMouseClickEvent<T extends MGuiElement<T>> extends GuiMouseEvent<T> {
    private final ClickType clickType;

    public GuiMouseClickEvent(ClickType clickType) { this.clickType = clickType; }
    public ClickType getClickType()                { return clickType; }
}