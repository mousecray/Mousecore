/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.core.config.val;

import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.ConfigValItem;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.values.minecraft.MinecraftItemType;
import ru.mousecray.mousecore.api.minecraft.MouseItemState;
import ru.mousecray.mousecore.core.config.val.base.ConfigValMinecraftImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@FieldsAreNonnullByDefault
@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public class ConfigValItemImpl extends ConfigValMinecraftImpl<MinecraftItemType> implements ConfigValItem {
    @SafeVarargs
    public ConfigValItemImpl(
            MinecraftItemType defaultValue, MinecraftItemType disabledValue,
            @Nullable String specificDataType,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<MinecraftItemType>... configureValues
    ) {
        super(MinecraftItemType.class, defaultValue, disabledValue, specificDataType, constraints, configureValues);
    }

    @Override public MouseItemState getItemState()            { return getValue().asItemState(); }
    @Override public String getPrefix()                       { return getValue().getPrefix(); }
    @Override public String getName()                         { return getValue().getName(); }
    @Override public void setItem(String prefix, String name) { setValue(MinecraftItemType.create(prefix, name)); }
    @Override public void setItem(MouseItemState state)       { setValue(MinecraftItemType.create(state)); }
}