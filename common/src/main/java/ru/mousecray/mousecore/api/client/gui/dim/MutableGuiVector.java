/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.dim;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class MutableGuiVector implements IGuiVector {
    private float x;
    private float y;

    public MutableGuiVector() { this(0f, 0f); }

    public MutableGuiVector(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public MutableGuiVector(IGuiVector v)         { this(v.x(), v.y()); }

    @Override public float x()                    { return x; }
    @Override public float y()                    { return y; }

    @Override public MutableGuiVector toMutable() { return this; }
    @Override public GuiVector toImmutable()      { return new GuiVector(this); }

    @Override public MutableGuiVector withX(float newX) {
        x = newX;
        return this;
    }

    @Override
    public MutableGuiVector withY(float newY) {
        y = newY;
        return this;
    }

    @Override
    public IGuiVector withVector(IGuiVector newVec) {
        withX(newVec.x());
        withY(newVec.y());
        return this;
    }

    @Override
    public MutableGuiVector add(IGuiVector v) {
        x += v.x();
        y += v.y();
        return this;
    }

    @Override
    public MutableGuiVector sub(IGuiVector v) {
        x -= v.x();
        y -= v.y();
        return this;
    }

    @Override
    public MutableGuiVector mul(float s) {
        x *= s;
        y *= s;
        return this;
    }

    @Override
    public MutableGuiVector div(float s) {
        if (s == 0f) throw new ArithmeticException("Division by zero");
        x /= s;
        y /= s;
        return this;
    }
    @Override public MutableGuiVector mul(IGuiVector v) {
        x *= v.x();
        y *= v.y();
        return this;
    }

    @Override
    public MutableGuiVector div(IGuiVector v) {
        if (v.x() == 0f || v.y() == 0f) throw new ArithmeticException("Division by zero");
        x /= v.x();
        y /= v.y();
        return this;
    }

    public MutableGuiVector add(float dx, float dy) {
        x += dx;
        y += dy;
        return this;
    }
    public MutableGuiVector sub(float dx, float dy) {
        x -= dx;
        y -= dy;
        return this;
    }

    @Override public MutableGuiVector copy() { return new MutableGuiVector(x, y); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IGuiVector)) return false;
        IGuiVector v = (IGuiVector) o;
        return Float.compare(v.x(), x) == 0 && Float.compare(v.y(), y) == 0;
    }

    @Override public int hashCode()    { return Float.floatToIntBits(x) * 31 + Float.floatToIntBits(y); }
    @Override public String toString() { return String.format("MutVec(%.2f, %.2f)", x, y); }
}