/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.misc;

import javax.annotation.Nullable;

public enum ScrollDirection {
    UP,
    DOWN;

    @Nullable
    public static ScrollDirection getScrollDirection(int scrollAmount) {
        return scrollAmount < 0 ? UP : scrollAmount > 0 ? DOWN : null;
    }
}