/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.dim.layout;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GridPos {
    public static GridPos DEFAULT() { return new GridPos(0, 0); }

    public final int row;
    public final int col;
    public final int rowSpan;
    public final int colSpan;

    public GridPos(int row, int col) {
        this(row, col, 1, 1);
    }

    public GridPos(int row, int col, int rowSpan, int colSpan) {
        this.row = row;
        this.col = col;
        this.rowSpan = Math.max(1, rowSpan);
        this.colSpan = Math.max(1, colSpan);
    }
}