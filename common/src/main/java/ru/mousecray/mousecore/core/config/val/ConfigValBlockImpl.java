/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.core.config.val;

import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.ConfigValBlock;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.values.minecraft.MinecraftBlockType;
import ru.mousecray.mousecore.api.minecraft.MouseBlockState;
import ru.mousecray.mousecore.core.config.val.base.ConfigValMinecraftImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@FieldsAreNonnullByDefault
@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public class ConfigValBlockImpl extends ConfigValMinecraftImpl<MinecraftBlockType> implements ConfigValBlock {
    @SafeVarargs
    public ConfigValBlockImpl(
            MinecraftBlockType defaultValue, MinecraftBlockType disabledValue,
            @Nullable String specificDataType,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<MinecraftBlockType>... configureValues
    ) {
        super(MinecraftBlockType.class, defaultValue, disabledValue, specificDataType, constraints, configureValues);
    }

    @Override public MouseBlockState getBlockState()           { return getValue().asBlockState(); }
    @Override public String getPrefix()                        { return getValue().getPrefix(); }
    @Override public String getName()                          { return getValue().getName(); }
    @Override public void setBlock(String prefix, String name) { setValue(MinecraftBlockType.create(prefix, name)); }
    @Override public void setBlock(MouseBlockState state)      { setValue(MinecraftBlockType.create(state)); }
}