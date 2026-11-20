/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.core.config.val.minecraft;

import net.minecraft.resources.ResourceLocation;
import ru.mousecray.mousecore.api.VariableValue;
import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.minecraft.ConfigValTag;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.values.minecraft.*;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.core.config.val.base.ConfigValMinecraftImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@FieldsAreNonnullByDefault
@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public class ConfigValTagImpl extends ConfigValMinecraftImpl<MinecraftTagType> implements ConfigValTag {
    private final IValType targetValType;

    @SafeVarargs
    public ConfigValTagImpl(
            IValType targetValType,
            MinecraftTagType defaultValue, MinecraftTagType disabledValue,
            @Nullable String specificDataType,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<MinecraftTagType>... configureValues
    ) {
        super(MinecraftTagType.class, defaultValue, disabledValue, specificDataType, constraints, configureValues);
        this.targetValType = Objects.requireNonNull(targetValType);
    }

    @Override
    protected VariableValue<MinecraftTagType> parseValue(@Nullable String value) {
        try {
            return VariableValue.create(MinecraftTagType.parse(value, targetValType));
        } catch (ValueFormatException ignore) { return VariableValue.create(); }
    }

    @Override public ResourceLocation getLocation()                           { return getValue().asLocation(); }
    @Override public String getNamespace()                                    { return getValue().getNamespace(); }
    @Override public String getPath()                                         { return getValue().getPath(); }
    @Override public IValType getTargetValType()                              { return targetValType; }

    @Override public boolean contains(@Nullable MinecraftItemType item)       { return getValue().contains(item); }
    @Override public boolean contains(@Nullable MinecraftBlockType block)     { return getValue().contains(block); }
    @Override public boolean contains(@Nullable MinecraftItemStackType stack) { return getValue().contains(stack); }
    @Override public boolean contains(@Nullable MinecraftEntityType entity)   { return getValue().contains(entity); }

    @Override public void setTag(ResourceLocation location)                   { setValue(MinecraftTagType.create(location, targetValType)); }
    @Override public void setTag(String namespace, String path)               { setValue(MinecraftTagType.create(namespace, path, targetValType)); }
}