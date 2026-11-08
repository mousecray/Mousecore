/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.misc.cache;

import ru.mousecray.mouseproject.api.client.gui.MGuiElement;
import ru.mousecray.mouseproject.api.client.gui.container.MGuiGridPanel;
import ru.mousecray.mouseproject.api.client.gui.dim.GuiVector;
import ru.mousecray.mouseproject.api.client.gui.dim.layout.AnchorPos;
import ru.mousecray.mouseproject.api.client.gui.dim.layout.GridPos;
import ru.mousecray.mouseproject.api.client.gui.dim.layout.GuiMargin;

import java.util.Objects;

public class GuiGridCacheBuilder<T extends MGuiElement<?>, D extends MGuiGridPanel> extends GuiCacheBuilder<T, D, GuiGridCacheBuilder<T, D>> {
    private GridPos gridPos = GridPos.DEFAULT();

    public GuiGridCacheBuilder<T, D> setGridPos(GridPos gridPos) {
        this.gridPos = Objects.requireNonNull(gridPos);
        return this;
    }

    @Override
    protected void setObjToParent(D parent, T obj, GuiMargin margin, AnchorPos anchor, GuiVector offset) {
        parent.addChild(obj, margin, anchor, offset, gridPos);
    }
}