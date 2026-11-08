/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.dim.layout;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPadding extends GuiMargin {
    public static GuiPadding ZERO()                                     { return new GuiPadding(0); }

    public GuiPadding(float all)                                        { super(all); }
    public GuiPadding(float horizontal, float vertical)                 { super(horizontal, vertical); }
    public GuiPadding(float left, float top, float right, float bottom) { super(left, top, right, bottom); }
}