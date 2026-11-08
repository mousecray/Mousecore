/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.component.lang;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MSimpleString implements MGuiString {
    private final String text;

    public MSimpleString(String text) {
        this.text = text == null ? "" : text;
    }

    @Override
    public String get() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }
}
