/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.api.config.val;

import ru.mousecray.mousecore.api.config.val.base.ConfigOtherVal;
import ru.mousecray.mousecore.api.customtype.values.minecraft.MinecraftBlockType;
import ru.mousecray.mousecore.api.minecraft.MouseBlockState;

public interface ConfigValBlock extends ConfigOtherVal<MinecraftBlockType> {
    MouseBlockState getBlockState();
    String getPrefix();
    String getName();

    void setBlock(String prefix, String name);
    void setBlock(MouseBlockState state);
}