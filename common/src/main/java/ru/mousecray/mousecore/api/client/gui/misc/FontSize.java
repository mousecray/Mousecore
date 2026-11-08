/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.misc;

public enum FontSize {
    NORMAL(1.0f), SMALL(1.0f), LARGE(1.5f), EXTRA_LARGE(2.0f);

    private final float vanillaScale;
    FontSize(float vanillaScale) { this.vanillaScale = vanillaScale; }
    public float getScale()      { return vanillaScale; }
}