/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.core.config.val.base.list.simple;

import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.ConfigValType;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.base.list.simple.ConfigSimpleListLogicalVal;
import ru.mousecray.mousecore.api.customtype.LogicalType;
import ru.mousecray.mousecore.api.customtype.values.SimpleListType;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
@FieldsAreNonnullByDefault
public abstract class ConfigSimpleListLogicalValImpl<T extends LogicalType<?>> extends ConfigSimpleListValImpl<T> implements ConfigSimpleListLogicalVal<T> {
    @SafeVarargs
    public ConfigSimpleListLogicalValImpl(
            Class<T> listTypeClass,
            @Nullable Function<String, T> valCreator,
            SimpleListType<T> defaultValue, SimpleListType<T> disabledValue,
            @Nullable Constraint<? extends ru.mousecray.mousecore.api.customtype.CustomType<?>>[] constraints,
            @Nullable PredefinedValue<SimpleListType<T>>... configureValues
    ) {
        super(
                ConfigValType.LOGICAL, listTypeClass, valCreator, defaultValue, disabledValue,
                constraints, configureValues
        );
    }
}