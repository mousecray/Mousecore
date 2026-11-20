/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.core.config.val.minecraft;

import net.minecraft.world.level.biome.Biome;
import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.minecraft.ConfigValBiome;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.values.minecraft.MinecraftBiomeType;
import ru.mousecray.mousecore.api.minecraft.MouseResourceKey;
import ru.mousecray.mousecore.core.config.val.base.ConfigValMinecraftImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@FieldsAreNonnullByDefault
@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public class ConfigValBiomeImpl extends ConfigValMinecraftImpl<MinecraftBiomeType> implements ConfigValBiome {
    @SafeVarargs
    public ConfigValBiomeImpl(
            MinecraftBiomeType defaultValue, MinecraftBiomeType disabledValue,
            @Nullable String specificDataType,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<MinecraftBiomeType>... configureValues
    ) {
        super(MinecraftBiomeType.class, defaultValue, disabledValue, specificDataType, constraints, configureValues);
    }

    @Override public MouseResourceKey<Biome> getResourceKey()   { return getValue().asResourceKey(); }
    @Override public String getPrefix()                         { return getValue().getPrefix(); }
    @Override public String getName()                           { return getValue().getName(); }
    @Override public void setBiome(String prefix, String name)  { setValue(MinecraftBiomeType.create(prefix, name)); }
    @Override public void setBiome(MouseResourceKey<Biome> key) { setValue(MinecraftBiomeType.create(key)); }
}