/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.container;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.mouseproject.api.client.gui.MGuiScrollPanel;
import ru.mousecray.mouseproject.api.client.gui.dim.GuiShape;

import javax.annotation.ParametersAreNonnullByDefault;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class MGuiSimpleScrollPanel extends MGuiScrollPanel<MGuiSimpleScrollPanel> {
    public MGuiSimpleScrollPanel(GuiShape shape) { super(shape); }
}