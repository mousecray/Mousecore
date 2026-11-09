/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.core.config.val.base.list.conditional;

import ru.mousecray.mousecore.api.VariableValue;
import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.ConfigValType;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.IValType;
import ru.mousecray.mousecore.api.config.val.base.list.conditional.ConfigConditionalListVal;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.values.ConditionalListType;
import ru.mousecray.mousecore.api.customtype.values.ConditionalValType;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.core.config.val.base.ConfigListValImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
@FieldsAreNonnullByDefault
public abstract class ConfigConditionalListValImpl<T extends CustomType<?>>
        extends ConfigListValImpl<ConditionalValType<T>, T, ConditionalListType<T>> implements ConfigConditionalListVal<T> {
    @SafeVarargs
    public ConfigConditionalListValImpl(
            IValType listComponentType, Class<T> listTypeClass,
            @Nullable Function<String, T> valCreator,
            ConditionalListType<T> defaultValue, ConditionalListType<T> disabledValue,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
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