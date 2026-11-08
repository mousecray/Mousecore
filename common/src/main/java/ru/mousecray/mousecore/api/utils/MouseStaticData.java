/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.utils;

import net.minecraft.resources.ResourceLocation;
import ru.mousecray.mousecore.api.client.gui.dim.GuiVector;
import ru.mousecray.mousecore.core.Mousecore;

public class MouseStaticData {
    public static final ResourceLocation CONTROLS_TEXTURES;
    public static final GuiVector        CONTROLS_TEXTURES_SIZE = GuiVector.of(256);

    static {
        CONTROLS_TEXTURES = ResourceLocation.tryBuild(Mousecore.MOD_ID, "textures/gui/controls.png");
        if (CONTROLS_TEXTURES == null) throw new NullPointerException("Controls texture resource location is null");
    }

    private MouseStaticData() { throw new UnsupportedOperationException("Cannot create utility class"); }
}