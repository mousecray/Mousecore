/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.component.color;

public class MColorBuffer {
    private final float[] COLOR_BUFFER = new float[4];
    MColorBuffer() { }

    public void allocate(float r, float g, float b, float a) {
        COLOR_BUFFER[0] = r;
        COLOR_BUFFER[1] = g;
        COLOR_BUFFER[2] = b;
        COLOR_BUFFER[3] = a;
    }

    public float getRed()   { return COLOR_BUFFER[0]; }
    public float getGreen() { return COLOR_BUFFER[1]; }
    public float getBlue()  { return COLOR_BUFFER[2]; }
    public float getAlpha() { return COLOR_BUFFER[3]; }
}