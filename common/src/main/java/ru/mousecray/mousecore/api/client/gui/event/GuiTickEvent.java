/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.event;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.mouseproject.api.client.gui.MGuiElement;

@SideOnly(Side.CLIENT)
public class GuiTickEvent<T extends MGuiElement<T>> extends GuiEvent<T> {
    private float partialTick;

    void setPartialTick(float partialTick) { this.partialTick = partialTick; }
    public float getPartialTick()          { return partialTick; }
}