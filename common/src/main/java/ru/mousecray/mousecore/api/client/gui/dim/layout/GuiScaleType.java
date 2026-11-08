/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.dim.layout;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.EnumSet;
import java.util.Set;

@SideOnly(Side.CLIENT)
public enum GuiScaleType {
    FIXED(0),
    FIXED_HORIZONTAL(1),
    FIXED_VERTICAL(2),
    FLOW(3),
    FLOW_HORIZONTAL(4),
    FLOW_VERTICAL(5),
    PARENT(6),
    ORIGIN_HORIZONTAL(7),  // Масштаб оси X зависит от Y
    ORIGIN_VERTICAL(8),    // Масштаб оси Y зависит от X
    PARENT_HORIZONTAL(9),
    PARENT_VERTICAL(10),
    WRAP(11),
    WRAP_HORIZONTAL(12),
    WRAP_VERTICAL(13);

    private final int id;
    GuiScaleType(int id) { this.id = id; }

    public int getId()   { return id; }

    public Category getCategory() {
        switch (this) {
            case FIXED:
            case FIXED_HORIZONTAL:
            case FIXED_VERTICAL:
                return Category.FIXED;
            case FLOW:
            case FLOW_HORIZONTAL:
            case FLOW_VERTICAL:
                return Category.FLOW;
            case ORIGIN_HORIZONTAL:
            case ORIGIN_VERTICAL:
                return Category.ORIGIN;
            case PARENT:
            case PARENT_HORIZONTAL:
            case PARENT_VERTICAL:
                return Category.PARENT;
            case WRAP:
            case WRAP_HORIZONTAL:
            case WRAP_VERTICAL:
                return Category.WRAP;
        }
        throw new IllegalStateException("Unknown category for " + this);
    }

    public Set<Axes> getAxes() {
        switch (this) {
            case FIXED:
            case FLOW:
            case PARENT:
            case WRAP:
                return EnumSet.of(Axes.HORIZONTAL, Axes.VERTICAL);
            case FIXED_HORIZONTAL:
            case FLOW_HORIZONTAL:
            case ORIGIN_HORIZONTAL:
            case PARENT_HORIZONTAL:
            case WRAP_HORIZONTAL:
                return EnumSet.of(Axes.HORIZONTAL);
            case FIXED_VERTICAL:
            case FLOW_VERTICAL:
            case ORIGIN_VERTICAL:
            case PARENT_VERTICAL:
            case WRAP_VERTICAL:
                return EnumSet.of(Axes.VERTICAL);
        }
        throw new IllegalStateException("Unknown axes for " + this);
    }

    public enum Category {FIXED, FLOW, ORIGIN, PARENT, WRAP}

    public enum Axes {HORIZONTAL, VERTICAL}
}