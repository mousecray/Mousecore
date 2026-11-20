/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.core.config.val.base.list;

import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.base.list.ConfigValSimpleListLogical;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.LogicalType;
import ru.mousecray.mousecore.api.customtype.values.collection.SimpleListType;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
@FieldsAreNonnullByDefault
public class ConfigValSimpleListLogicalImpl<T extends LogicalType<?>> extends ConfigValSimpleListImpl<T> implements ConfigValSimpleListLogical<T> {
    @SafeVarargs
    public ConfigValSimpleListLogicalImpl(
            boolean isConditional, boolean isUnique,
            @Nullable Function<String, T> valCreator,
            SimpleListType<T> defaultValue, SimpleListType<T> disabledValue,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<SimpleListType<T>>... configureValues
    ) {
        super(isConditional, isUnique, valCreator, defaultValue, disabledValue, constraints, configureValues);
    }
}