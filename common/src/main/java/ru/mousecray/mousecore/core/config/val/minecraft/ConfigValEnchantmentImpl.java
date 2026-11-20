/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.core.config.val.minecraft;

import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.minecraft.ConfigValEnchantment;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.values.minecraft.MinecraftEnchantmentType;
import ru.mousecray.mousecore.api.minecraft.MouseEnchantmentState;
import ru.mousecray.mousecore.core.config.val.base.ConfigValMinecraftImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@FieldsAreNonnullByDefault
@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public class ConfigValEnchantmentImpl extends ConfigValMinecraftImpl<MinecraftEnchantmentType> implements ConfigValEnchantment {
    @SafeVarargs
    public ConfigValEnchantmentImpl(
            MinecraftEnchantmentType defaultValue, MinecraftEnchantmentType disabledValue,
            @Nullable String specificDataType,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<MinecraftEnchantmentType>... configureValues
    ) {
        super(MinecraftEnchantmentType.class, defaultValue, disabledValue, specificDataType, constraints, configureValues);
    }

    @Override public MouseEnchantmentState getEnchantmentState()      { return getValue().asEnchantmentState(); }
    @Override public String getPrefix()                               { return getValue().getPrefix(); }
    @Override public String getName()                                 { return getValue().getName(); }
    @Override public void setEnchantment(String prefix, String name)  { setValue(MinecraftEnchantmentType.create(prefix, name)); }
    @Override public void setEnchantment(MouseEnchantmentState state) { setValue(MinecraftEnchantmentType.create(state)); }
}