/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.component.lang;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MLocalizedString implements MGuiString {
    private final String   key;
    private final Object[] args;

    public MLocalizedString(String key, Object... args) {
        this.key = key;
        this.args = args;
    }

    @Override
    public String get() {
        return I18n.format(key, args);
    }

    @Override
    public String toString() {
        return get();
    }
}
