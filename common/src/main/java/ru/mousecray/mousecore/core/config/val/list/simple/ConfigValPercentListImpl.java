/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.core.config.val.list.simple;

import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.list.simple.ConfigValPercentList;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.range.Range;
import ru.mousecray.mousecore.api.customtype.range.RangeContainer;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.customtype.values.PercentType;
import ru.mousecray.mousecore.api.customtype.values.SimpleListType;
import ru.mousecray.mousecore.core.config.val.base.list.simple.ConfigSimpleListNumberValImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
@FieldsAreNonnullByDefault
public class ConfigValPercentListImpl extends ConfigSimpleListNumberValImpl<PercentType> implements ConfigValPercentList {
    @SafeVarargs
    public ConfigValPercentListImpl(
            @Nullable SimpleListType<PercentType> defaultValue, @Nullable SimpleListType<PercentType> disabledValue,
            @Nullable RangeContainer<PercentType> range,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<SimpleListType<PercentType>>... configureValues
    ) {
        super(
                createValCreator(),
                defaultValue == null
                        ? SimpleListType.create(ValType.PERCENT, createValCreator())
                        : defaultValue,
                disabledValue == null
                        ? SimpleListType.create(ValType.PERCENT, createValCreator())
                        : disabledValue,
                range == null
                        ? new RangeContainer<>(Range.of(PercentType.MIN, PercentType.MAX))
                        : range,
                constraints, configureValues
        );
    }

    private static Function<String, PercentType> createValCreator() {
        return str -> CustomType.parse(PercentType.class, str);
    }
}