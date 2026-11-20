/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.api.config.val.minecraft;

import net.minecraft.world.level.Level;
import ru.mousecray.mousecore.api.config.val.base.ConfigOtherVal;
import ru.mousecray.mousecore.api.customtype.values.minecraft.MinecraftDimensionType;
import ru.mousecray.mousecore.api.minecraft.MouseResourceKey;

public interface ConfigValDimension extends ConfigOtherVal<MinecraftDimensionType> {
    MouseResourceKey<Level> getResourceKey();
    String getPrefix();
    String getName();

    void setDimension(String prefix, String name);
    void setDimension(MouseResourceKey<Level> key);
}