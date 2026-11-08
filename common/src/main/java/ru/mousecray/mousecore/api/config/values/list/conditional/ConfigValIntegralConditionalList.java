/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.config.values.list.conditional;

import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.specific.ConfigValType;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.values.base.list.conditional.ConfigConditionalListNumberVal;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.range.Range;
import ru.mousecray.mousecore.api.customtype.range.RangeContainer;
import ru.mousecray.mousecore.api.customtype.values.ConditionalListType;
import ru.mousecray.mousecore.api.customtype.values.IntegralType;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
@FieldsAreNonnullByDefault
public class ConfigValIntegralConditionalList extends ConfigConditionalListNumberVal<IntegralType> {
    @SafeVarargs
    public ConfigValIntegralConditionalList(
            @Nullable ConditionalListType<IntegralType> defaultValue, @Nullable ConditionalListType<IntegralType> disabledValue,
            @Nullable RangeContainer<IntegralType> range,
            @Nullable Constraint<?>[] constraints,
            @Nullable PredefinedValue<ConditionalListType<IntegralType>>... configureValues
    ) {
        super(
                ConfigValType.INTEGRAL, IntegralType.class, createValCreator(),
                defaultValue == null
                        ? ConditionalListType.create(IntegralType.class, createValCreator())
                        : defaultValue,
                disabledValue == null
                        ? ConditionalListType.create(IntegralType.class, createValCreator())
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