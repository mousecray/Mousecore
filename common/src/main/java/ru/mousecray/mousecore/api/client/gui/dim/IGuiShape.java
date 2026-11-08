/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.dim;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IGuiShape {

    float x();
    float y();
    float width();
    float height();

    default float right()  { return x() + width(); }
    default float bottom() { return y() + height(); }

    IGuiVector pos();
    IGuiVector size();

    default boolean contains(float px, float py) {
        return px >= x() && px < right() && py >= y() && py < bottom();
    }
    default boolean contains(IGuiVector p) { return contains(p.x(), p.y()); }

    default boolean intersects(IGuiShape other) {
        return x() < other.right() && right() > other.x() &&
                y() < other.bottom() && bottom() > other.y();
    }

    default boolean contains(IGuiShape other) {
        return x() <= other.x() && y() <= other.y() &&
                right() >= other.right() && bottom() >= other.bottom();
    }

    default float centerX()                                      { return x() + width() * 0.5f; }
    default float centerY()                                      { return y() + height() * 0.5f; }
    default IGuiVector center()                                  { return GuiVector.of(centerX(), centerY()); }

    default boolean isEmpty()                                    { return width() <= 0f || height() <= 0f; }

    IGuiShape withX(float x);
    IGuiShape withY(float y);
    IGuiShape withWidth(float w);
    IGuiShape withHeight(float h);
    IGuiShape withPos(IGuiVector pos);
    IGuiShape withSize(IGuiVector size);
    IGuiShape withShape(IGuiShape shape);

    MutableGuiShape toMutable();
    GuiShape toImmutable();

    IGuiShape offset(float dx, float dy);
    default IGuiShape offset(IGuiVector v)                       { return offset(v.x(), v.y()); }

    IGuiShape grow(float left, float top, float right, float bottom);
    default IGuiShape grow(float all)                            { return grow(all, all, all, all); }
    default IGuiShape grow(float hor, float ver)                 { return grow(hor, ver, hor, ver); }

    default IGuiShape shrink(float all)                          { return grow(-all); }
    default IGuiShape shrink(float hor, float ver)               { return grow(-hor, -ver); }
    default IGuiShape shrink(float l, float t, float r, float b) { return grow(-l, -t, -r, -b); }

    IGuiShape copy();
}