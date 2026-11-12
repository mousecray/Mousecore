/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.core.config.val;

import ru.mousecray.mousecore.api.VariableValue;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.ConfigValString;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.customtype.values.StringType;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.core.config.val.base.ConfigValOtherImpl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ConfigValStringImpl extends ConfigValOtherImpl<StringType> implements ConfigValString {
    @SafeVarargs
    public ConfigValStringImpl(
            @Nullable StringType defaultValue, @Nullable StringType disabledValue,
            @Nullable String specificDataType,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<StringType>... configureValues
    ) {
        super(
                ValType.SIMPLE_STRING,
                defaultValue == null ? StringType.NULL : defaultValue,
                disabledValue == null ? StringType.NULL : disabledValue,
                specificDataType, constraints, configureValues
        );
    }

    @Override public String getString()                     { return getValue().getValue(); }
    @Override public void setString(@Nullable String value) { setValue(value == null ? StringType.NULL : StringType.create(value)); }

    @Nonnull @Override
    protected VariableValue<StringType> parseValue(@Nullable String value) {
        try {
            return VariableValue.create(CustomType.parse(StringType.class, value));
        } catch (ValueFormatException ignore) { return VariableValue.create(); }
    }

    @Override protected boolean saveDisabledValue() { return true; }
}