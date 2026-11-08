/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.component.lang;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.mouseproject.Tags;

@SideOnly(Side.CLIENT)
public interface MGuiString {
    static MGuiString EMPTY() { return simple(""); }

    String get();

    static MGuiString simple(String text) {
        return new MSimpleString(text);
    }

    static MGuiString localized(String key, Object... args) {
        return new MLocalizedString(key, args);
    }

    static MGuiString localizedGuiTag(String key, Object... args) {
        return localized("gui." + Tags.MOD_ID + "." + key, args);
    }
}
