/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.dim.layout;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiMargin {
    public static GuiMargin ZERO() { return new GuiMargin(0); }

    private float left, top, right, bottom;

    public GuiMargin(float all) { left = top = right = bottom = all; }

    public GuiMargin(float horizontal, float vertical) {
        left = right = horizontal;
        top = bottom = vertical;
    }

    public GuiMargin(float left, float top, float right, float bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public float getLeft()              { return left; }
    public float getTop()               { return top; }
    public float getRight()             { return right; }
    public float getBottom()            { return bottom; }

    public void setLeft(float left)     { this.left = left; }
    public void setTop(float top)       { this.top = top; }
    public void setRight(float right)   { this.right = right; }
    public void setBottom(float bottom) { this.bottom = bottom; }
}