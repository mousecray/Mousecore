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
public final class GuiShape implements IGuiShape {
    public static final GuiShape ZERO = new GuiShape(0f, 0f, 0f, 0f);

    @Nonnull private final GuiVector pos, size;

    public GuiShape(float x, float y, float width, float height) {
        this(GuiVector.of(x, y), GuiVector.of(width, height));
    }

    public GuiShape(IGuiVector pos, IGuiVector size) {
        this.pos = pos.toImmutable();
        this.size = size.toImmutable();
    }

    public GuiShape(IGuiShape shape)                      { this(shape.pos(), shape.size()); }

    @Override public float x()                            { return pos.x(); }
    @Override public float y()                            { return pos.y(); }
    @Override public float width()                        { return size.x(); }
    @Override public float height()                       { return size.y(); }

    @Override public GuiVector pos()                      { return pos; }
    @Override public GuiVector size()                     { return size; }

    @Override public MutableGuiShape toMutable()          { return new MutableGuiShape(this); }
    @Override public GuiShape toImmutable()               { return this; }

    @Override public GuiShape withX(float newX)           { return new GuiShape(newX, pos.y(), size.x(), size.y()); }
    @Override public GuiShape withY(float newY)           { return new GuiShape(pos.x(), newY, size.x(), size.y()); }
    @Override public GuiShape withWidth(float w)          { return new GuiShape(pos.x(), pos.y(), w, size.y()); }
    @Override public GuiShape withHeight(float h)         { return new GuiShape(pos.x(), pos.y(), size.x(), h); }
    @Override public GuiShape withPos(IGuiVector p)       { return new GuiShape(p.x(), p.y(), size.x(), size.y()); }
    @Override public GuiShape withSize(IGuiVector s)      { return new GuiShape(pos.x(), pos.y(), s.x(), s.y()); }
    @Override public IGuiShape withShape(IGuiShape shape) { return new GuiShape(this); }
    @Override public GuiShape offset(float dx, float dy)  { return new GuiShape(pos.x() + dx, pos.y() + dy, size.x(), size.y()); }

    @Override
    public GuiShape grow(float left, float top, float right, float bottom) {
        return new GuiShape(pos.x() - left, pos.y() - top, size.x() + left + right, size.y() + top + bottom);
    }

    @Override public GuiShape copy() { return new GuiShape(pos.copy(), size.copy()); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GuiShape)) return false;
        GuiShape guiShape = (GuiShape) o;
        return Objects.equals(pos, guiShape.pos) && Objects.equals(size, guiShape.size);
    }

    @Override public int hashCode()    { return Objects.hash(pos, size); }
    @Override public String toString() { return String.format("Shape[%.1f, %.1f, %.1f×%.1f]", pos.x(), pos.y(), size.x(), size.y()); }
}