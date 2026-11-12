/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.core.config.val.list.conditional;

import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.list.conditional.ConfigValIntegralConditionalList;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.range.Range;
import ru.mousecray.mousecore.api.customtype.range.RangeContainer;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.customtype.values.ConditionalListType;
import ru.mousecray.mousecore.api.customtype.values.IntegralType;
import ru.mousecray.mousecore.core.config.val.base.list.conditional.ConfigConditionalListNumberValImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
@FieldsAreNonnullByDefault
public class ConfigValIntegralConditionalListImpl extends ConfigConditionalListNumberValImpl<IntegralType> implements ConfigValIntegralConditionalList {
    @SafeVarargs
    public ConfigValIntegralConditionalListImpl(
            @Nullable ConditionalListType<IntegralType> defaultValue, @Nullable ConditionalListType<IntegralType> disabledValue,
            @Nullable RangeContainer<IntegralType> range,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<ConditionalListType<IntegralType>>... configureValues
    ) {
        super(
                createValCreator(),
                defaultValue == null
                        ? ConditionalListType.create(ValType.INTEGRAL, createValCreator())
                        : defaultValue,
                disabledValue == null
                        ? ConditionalListType.create(ValType.INTEGRAL, createValCreator())
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