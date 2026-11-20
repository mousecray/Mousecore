/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.core.config.val.container;

import net.minecraft.nbt.CompoundTag;
import ru.mousecray.mousecore.api.VariableValue;
import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.container.ConfigValNBT;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.values.collection.NBTType;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.core.config.val.ConfigValImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@FieldsAreNonnullByDefault
@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public class ConfigValNBTImpl extends ConfigValImpl<NBTType> implements ConfigValNBT {

    @SafeVarargs
    public ConfigValNBTImpl(
            NBTType defaultValue, NBTType disabledValue,
            @Nullable String specificDataType,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<NBTType>... configureValues
    ) {
        super(defaultValue, disabledValue, val -> specificDataType, constraints, configureValues);
    }

    @Override
    protected VariableValue<NBTType> parseValue(@Nullable String value) {
        try {
            return VariableValue.create(CustomType.parse(NBTType.class, value));
        } catch (ValueFormatException ignore) { return VariableValue.create(); }
    }

    @Override protected boolean saveDisabledValue()       { return false; }

    @Override public CompoundTag getCompoundTag()         { return getValue().asCompoundTag(); }
    @Override public void setCompoundTag(CompoundTag tag) { setValue(NBTType.create(tag)); }
}