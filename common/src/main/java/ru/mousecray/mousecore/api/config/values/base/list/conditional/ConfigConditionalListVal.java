/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.config.values.base.list.conditional;

import ru.mousecray.mousecore.api.VariableValue;
import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.IValType;
import ru.mousecray.mousecore.api.config.specific.ConfigValType;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.values.base.ConfigListVal;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.values.ConditionalListType;
import ru.mousecray.mousecore.api.customtype.values.ConditionalValType;
import ru.mousecray.mousecore.api.error.ValueFormatException;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
@FieldsAreNonnullByDefault
public abstract class ConfigConditionalListVal<T extends CustomType<?>>
        extends ConfigListVal<ConditionalValType<T>, T, ConditionalListType<T>> {
    @SafeVarargs
    public ConfigConditionalListVal(
            IValType listComponentType, Class<T> listTypeClass,
            @Nullable Function<String, T> valCreator,
            ConditionalListType<T> defaultValue, ConditionalListType<T> disabledValue,
            @Nullable Constraint<?>[] constraints,
            @Nullable PredefinedValue<ConditionalListType<T>>... configureValues
    ) {
        super(
                ConfigValType.LIST, defaultValue, disabledValue, listComponentType,
                listTypeClass, valCreator, constraints, configureValues
        );
    }

    @Override
    protected VariableValue<ConditionalListType<T>> parseValue(@Nullable String value) {
        try {
            return VariableValue.create(
                    ConditionalListType.fromString(
                            listComponentClass, value, valCreator,
                            hasLogger() ? getLogger() : null
                    )
            );
        } catch (ValueFormatException ignore) { return VariableValue.create(); }
    }
}