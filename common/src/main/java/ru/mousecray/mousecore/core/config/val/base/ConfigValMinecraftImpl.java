/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ****************************************************************************/

package ru.mousecray.mousecore.core.config.val.base;

import ru.mousecray.mousecore.api.VariableValue;
import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.OtherType;
import ru.mousecray.mousecore.api.error.ValueFormatException;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@FieldsAreNonnullByDefault
@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class ConfigValMinecraftImpl<T extends OtherType<?>> extends ConfigValOtherImpl<T> {
    protected final Class<T> typeClass;

    @SafeVarargs
    public ConfigValMinecraftImpl(
            Class<T> typeClass,
            T defaultValue, T disabledValue,
            @Nullable String specificDataType,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<T>... configureValues
    ) {
        super(defaultValue, disabledValue, specificDataType, constraints, configureValues);
        this.typeClass = Objects.requireNonNull(typeClass);
    }

    @Override
    protected VariableValue<T> parseValue(@Nullable String value) {
        try {
            return VariableValue.create(CustomType.parse(typeClass, value));
        } catch (ValueFormatException ignore) { return VariableValue.create(); }
    }
}