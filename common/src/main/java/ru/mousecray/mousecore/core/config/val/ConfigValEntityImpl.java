/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.core.config.val;

import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.ConfigValEntity;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.values.minecraft.MinecraftEntityType;
import ru.mousecray.mousecore.api.minecraft.MouseEntityState;
import ru.mousecray.mousecore.core.config.val.base.ConfigValMinecraftImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@FieldsAreNonnullByDefault
@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public class ConfigValEntityImpl extends ConfigValMinecraftImpl<MinecraftEntityType> implements ConfigValEntity {
    @SafeVarargs
    public ConfigValEntityImpl(
            MinecraftEntityType defaultValue, MinecraftEntityType disabledValue,
            @Nullable String specificDataType,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<MinecraftEntityType>... configureValues
    ) {
        super(MinecraftEntityType.class, defaultValue, disabledValue, specificDataType, constraints, configureValues);
    }

    @Override public MouseEntityState getEntityState()          { return getValue().asEntityState(); }
    @Override public String getPrefix()                         { return getValue().getPrefix(); }
    @Override public String getName()                           { return getValue().getName(); }
    @Override public void setEntity(String prefix, String name) { setValue(MinecraftEntityType.create(prefix, name)); }
    @Override public void setEntity(MouseEntityState state)     { setValue(MinecraftEntityType.create(state)); }
}