/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.misc.cache;

import ru.mousecray.mouseproject.api.client.gui.MGuiElement;
import ru.mousecray.mouseproject.api.client.gui.MGuiPanel;
import ru.mousecray.mouseproject.api.client.gui.dim.GuiVector;
import ru.mousecray.mouseproject.api.client.gui.dim.layout.AnchorPos;
import ru.mousecray.mouseproject.api.client.gui.dim.layout.GuiMargin;

public class GuiSimpleCacheBuilder<T extends MGuiElement<?>, D extends MGuiPanel<?>> extends GuiCacheBuilder<T, D, GuiSimpleCacheBuilder<T, D>> {
    @Override
    protected void setObjToParent(D parent, T obj, GuiMargin margin, AnchorPos anchor, GuiVector offset) {
        parent.addChild(obj, margin, anchor, offset);
    }
}