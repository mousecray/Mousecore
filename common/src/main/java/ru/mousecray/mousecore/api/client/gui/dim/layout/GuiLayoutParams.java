/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.dim.layout;

import ru.mousecray.mouseproject.api.client.gui.dim.GuiVector;

import javax.annotation.Nullable;

public class GuiLayoutParams {
    public final GuiMargin margin;
    public final AnchorPos anchor;
    public final GuiVector offset;

    public GuiLayoutParams(@Nullable GuiMargin margin, @Nullable AnchorPos anchor, @Nullable GuiVector offset) {
        this.margin = margin != null ? margin : GuiMargin.ZERO();
        this.anchor = anchor != null ? anchor : AnchorPos.TOP_LEFT;
        this.offset = offset != null ? offset : GuiVector.ZERO;
    }
}