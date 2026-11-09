/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.core.config.val.base.list.conditional;

import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.IValType;
import ru.mousecray.mousecore.api.config.val.base.list.conditional.ConfigConditionalListOtherVal;
import ru.mousecray.mousecore.api.customtype.OtherType;
import ru.mousecray.mousecore.api.customtype.values.ConditionalListType;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
@FieldsAreNonnullByDefault
public abstract class ConfigConditionalListOtherValImpl<T extends OtherType<?>> extends ConfigConditionalListValImpl<T> implements ConfigConditionalListOtherVal<T> {
    @SafeVarargs
    public ConfigConditionalListOtherValImpl(
            IValType listComponentType, Class<T> listTypeClass,
            @Nullable Function<String, T> valCreator,
            ConditionalListType<T> defaultValue, ConditionalListType<T> disabledValue,
            @Nullable Constraint<? extends ru.mousecray.mousecore.api.customtype.CustomType<?>>[] constraints,
            @Nullable PredefinedValue<ConditionalListType<T>>... configureValues
    ) {
        super(
                listComponentType, listTypeClass, valCreator, defaultValue, disabledValue,
                constraints, configureValues
        );
    }
}