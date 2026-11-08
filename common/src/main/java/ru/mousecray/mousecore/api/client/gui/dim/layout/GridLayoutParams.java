/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.dim.layout;

import ru.mousecray.mouseproject.api.client.gui.dim.GuiVector;

import javax.annotation.Nullable;

public class GridLayoutParams extends GuiLayoutParams {
    public final GridPos gridPos;

    public GridLayoutParams(@Nullable GuiMargin margin, @Nullable AnchorPos anchor, @Nullable GuiVector offset, @Nullable GridPos gridPos) {
        super(margin, anchor, offset);
        this.gridPos = gridPos != null ? gridPos : GridPos.DEFAULT();
    }
}