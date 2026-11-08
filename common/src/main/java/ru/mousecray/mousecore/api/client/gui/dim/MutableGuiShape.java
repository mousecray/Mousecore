/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.dim;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Objects;

@SideOnly(Side.CLIENT)
public final class MutableGuiShape implements IGuiShape {
    @Nonnull private final MutableGuiVector pos, size;

    public MutableGuiShape() { this(0f, 0f, 0f, 0f); }

    public MutableGuiShape(float x, float y, float width, float height) {
        this(new MutableGuiVector(x, y), new MutableGuiVector(width, height));
    }

    public MutableGuiShape(IGuiShape shape) { this(shape.pos(), shape.size()); }

    public MutableGuiShape(IGuiVector pos, IGuiVector size) {
        this.pos = pos.toMutable();
        this.size = size.toMutable();
    }

    @Override public float x()                   { return pos.x(); }
    @Override public float y()                   { return pos.y(); }
    @Override public float width()               { return size.x(); }
    @Override public float height()              { return size.y(); }

    @Override public MutableGuiShape toMutable() { return this; }
    @Override public GuiShape toImmutable()      { return new GuiShape(this); }

    @Override public MutableGuiVector pos()      { return pos; }
    @Override public MutableGuiVector size()     { return size; }

    @Override
    public MutableGuiShape withX(float x) {
        pos.withX(x);
        return this;
    }

    @Override
    public MutableGuiShape withY(float y) {
        pos.withY(y);
        return this;
    }

    @Override
    public MutableGuiShape withWidth(float w) {
        size.withX(w);
        return this;
    }

    @Override
    public MutableGuiShape withHeight(float h) {
        size.withY(h);
        return this;
    }

    @Override
    public MutableGuiShape withPos(IGuiVector p) {
        pos.withVector(p);
        return this;
    }

    @Override
    public MutableGuiShape withSize(IGuiVector s) {
        size.withVector(s);
        return this;
    }
    @Override
    public MutableGuiShape withShape(IGuiShape sh) {
        withPos(sh.pos());
        withSize(sh.size());
        return this;
    }

    @Override
    public MutableGuiShape offset(float dx, float dy) {
        pos.add(dx, dy);
        return this;
    }

    @Override
    public MutableGuiShape grow(float left, float top, float right, float bottom) {
        pos.add(-left, -top);
        size.add(left + right, top + bottom);
        return this;
    }

    @Override public MutableGuiShape copy() { return new MutableGuiShape(pos.copy(), size.copy()); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MutableGuiShape)) return false;
        MutableGuiShape that = (MutableGuiShape) o;
        return Objects.equals(pos, that.pos) && Objects.equals(size, that.size);
    }

    @Override public int hashCode() { return Objects.hash(pos, size); }

    @Override
    public String toString() {
        return String.format("MutShape[%.1f, %.1f, %.1f×%.1f]", pos.x(), pos.y(), size.x(), size.y());
    }
}