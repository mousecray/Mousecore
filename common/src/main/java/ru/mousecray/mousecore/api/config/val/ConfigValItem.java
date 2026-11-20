/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.api.config.val;

import ru.mousecray.mousecore.api.config.val.base.ConfigOtherVal;
import ru.mousecray.mousecore.api.customtype.values.minecraft.MinecraftItemType;
import ru.mousecray.mousecore.api.minecraft.MouseItemState;

public interface ConfigValItem extends ConfigOtherVal<MinecraftItemType> {
    MouseItemState getItemState();
    String getPrefix();
    String getName();

    void setItem(String prefix, String name);
    void setItem(MouseItemState state);
}