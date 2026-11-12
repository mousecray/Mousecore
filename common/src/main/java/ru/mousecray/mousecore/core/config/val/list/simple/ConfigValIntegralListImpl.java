/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.core.config.val.list.simple;

import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.list.simple.ConfigValIntegralList;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.range.Range;
import ru.mousecray.mousecore.api.customtype.range.RangeContainer;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.customtype.values.IntegralType;
import ru.mousecray.mousecore.api.customtype.values.SimpleListType;
import ru.mousecray.mousecore.core.config.val.base.list.simple.ConfigValSimpleListNumberImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
@FieldsAreNonnullByDefault
public class ConfigValIntegralListImpl extends ConfigValSimpleListNumberImpl<IntegralType> implements ConfigValIntegralList {
    @SafeVarargs
    public ConfigValIntegralListImpl(
            @Nullable SimpleListType<IntegralType> defaultValue, @Nullable SimpleListType<IntegralType> disabledValue,
            @Nullable RangeContainer<IntegralType> range,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<SimpleListType<IntegralType>>... configureValues
    ) {
        super(
                createValCreator(),
                defaultValue == null
                        ? SimpleListType.create(ValType.INTEGRAL, createValCreator())
                        : defaultValue,
                disabledValue == null
                        ? SimpleListType.create(ValType.INTEGRAL, createValCreator())
                        : disabledValue,
                range == null
                        ? new RangeContainer<>(Range.of(IntegralType.MIN, IntegralType.MAX))
                        : range,
                constraints, configureValues
        );
    }

    private static Function<String, IntegralType> createValCreator() {
        return str -> CustomType.parse(IntegralType.class, str);
    }
}