/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.component.texture;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.EnumSet;
import java.util.Set;

@SideOnly(Side.CLIENT)
public enum MGuiTextureScaleType {
    STRETCH(0),
    FILL(1),

    FILL_HORIZONTAL(2),
    FILL_VERTICAL(3),
    STRETCH_HORIZONTAL(4),
    STRETCH_VERTICAL(5),

    SINGLE_HORIZONTAL_LEFT(6),
    SINGLE_HORIZONTAL_CENTER(7),
    SINGLE_HORIZONTAL_RIGHT(8),

    SINGLE_VERTICAL_TOP(9),
    SINGLE_VERTICAL_CENTER(10),
    SINGLE_VERTICAL_BOTTOM(11);

    private final int id;
    MGuiTextureScaleType(int id) { this.id = id; }

    public int getId()           { return id; }

    public Category getCategory() {
        switch (this) {
            case STRETCH:
            case STRETCH_HORIZONTAL:
            case STRETCH_VERTICAL:
                return Category.STRETCH;
            case FILL:
            case FILL_HORIZONTAL:
            case FILL_VERTICAL:
                return Category.FILL;
            case SINGLE_HORIZONTAL_LEFT:
            case SINGLE_HORIZONTAL_CENTER:
            case SINGLE_HORIZONTAL_RIGHT:
            case SINGLE_VERTICAL_TOP:
            case SINGLE_VERTICAL_CENTER:
            case SINGLE_VERTICAL_BOTTOM:
                return Category.SINGLE;
        }
        throw new IllegalStateException("Unknown category for " + this);
    }

    public Set<Axes> getAxes() {
        switch (this) {
            case STRETCH:
            case FILL:
                return EnumSet.of(Axes.HORIZONTAL, Axes.VERTICAL);
            case STRETCH_HORIZONTAL:
            case FILL_HORIZONTAL:
            case SINGLE_HORIZONTAL_LEFT:
            case SINGLE_HORIZONTAL_CENTER:
            case SINGLE_HORIZONTAL_RIGHT:
                return EnumSet.of(Axes.HORIZONTAL);
            case STRETCH_VERTICAL:
            case FILL_VERTICAL:
            case SINGLE_VERTICAL_TOP:
            case SINGLE_VERTICAL_CENTER:
            case SINGLE_VERTICAL_BOTTOM:
                return EnumSet.of(Axes.VERTICAL);
        }
        throw new IllegalStateException("Unknown axes for " + this);
    }

    public enum Category {STRETCH, FILL, SINGLE}

    public enum Axes {HORIZONTAL, VERTICAL}
}