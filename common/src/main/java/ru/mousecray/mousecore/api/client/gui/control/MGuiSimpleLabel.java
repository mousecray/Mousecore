/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.control;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.mouseproject.api.client.gui.MGuiLabel;
import ru.mousecray.mouseproject.api.client.gui.component.lang.MGuiString;
import ru.mousecray.mouseproject.api.client.gui.dim.GuiShape;

@SideOnly(Side.CLIENT)
public class MGuiSimpleLabel extends MGuiLabel<MGuiSimpleLabel> {
    public MGuiSimpleLabel(MGuiString text, GuiShape shape) {
        super(shape);
        setGuiString(text);
    }
}