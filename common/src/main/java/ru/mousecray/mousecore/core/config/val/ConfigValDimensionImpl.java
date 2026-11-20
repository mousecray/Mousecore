/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.core.config.val;

import net.minecraft.world.level.Level;
import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.ConfigValDimension;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.values.minecraft.MinecraftDimensionType;
import ru.mousecray.mousecore.api.minecraft.MouseResourceKey;
import ru.mousecray.mousecore.core.config.val.base.ConfigValMinecraftImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@FieldsAreNonnullByDefault
@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public class ConfigValDimensionImpl extends ConfigValMinecraftImpl<MinecraftDimensionType> implements ConfigValDimension {
    @SafeVarargs
    public ConfigValDimensionImpl(
            MinecraftDimensionType defaultValue, MinecraftDimensionType disabledValue,
            @Nullable String specificDataType,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<MinecraftDimensionType>... configureValues
    ) {
        super(MinecraftDimensionType.class, defaultValue, disabledValue, specificDataType, constraints, configureValues);
    }

    @Override public MouseResourceKey<Level> getResourceKey()       { return getValue().asResourceKey(); }
    @Override public String getPrefix()                             { return getValue().getPrefix(); }
    @Override public String getName()                               { return getValue().getName(); }
    @Override public void setDimension(String prefix, String name)  { setValue(MinecraftDimensionType.create(prefix, name)); }
    @Override public void setDimension(MouseResourceKey<Level> key) { setValue(MinecraftDimensionType.create(key)); }
}