/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.component.state;

public enum MGuiElementState {
    HOVERED,
    PRESSED,
    FOCUSED,
    SELECTED,
    FAIL,
    DISABLED,
    HIDDEN;

    public final int mask = 1 << ordinal();
}