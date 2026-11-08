/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.dim;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IGuiVector {
    float x();
    float y();

    IGuiVector withX(float newX);
    IGuiVector withY(float newY);
    IGuiVector withVector(IGuiVector newVector);

    IGuiVector add(IGuiVector other);
    IGuiVector sub(IGuiVector other);
    IGuiVector mul(float scalar);
    IGuiVector div(float scalar);
    IGuiVector mul(IGuiVector other);
    IGuiVector div(IGuiVector other);

    MutableGuiVector toMutable();
    GuiVector toImmutable();

    default float lengthSq() { return x() * x() + y() * y(); }
    default float length()   { return (float) Math.sqrt(lengthSq()); }

    default IGuiVector normalized() {
        float len = length();
        return len == 0f ? this : div(len);
    }

    IGuiVector copy();
}