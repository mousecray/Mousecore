/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.api.config.val.minecraft;

import net.minecraft.world.level.biome.Biome;
import ru.mousecray.mousecore.api.config.val.base.ConfigOtherVal;
import ru.mousecray.mousecore.api.customtype.values.minecraft.MinecraftBiomeType;
import ru.mousecray.mousecore.api.minecraft.MouseResourceKey;

public interface ConfigValBiome extends ConfigOtherVal<MinecraftBiomeType> {
    MouseResourceKey<Biome> getResourceKey();
    String getPrefix();
    String getName();

    void setBiome(String prefix, String name);
    void setBiome(MouseResourceKey<Biome> key);
}