/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.core.config.val.list.conditional;

import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.ConfigValType;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.list.conditional.ConfigValDecimalConditionalList;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.range.Range;
import ru.mousecray.mousecore.api.customtype.range.RangeContainer;
import ru.mousecray.mousecore.api.customtype.values.ConditionalListType;
import ru.mousecray.mousecore.api.customtype.values.DecimalType;
import ru.mousecray.mousecore.core.config.val.base.list.conditional.ConfigConditionalListNumberValImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
@FieldsAreNonnullByDefault
public class ConfigValDecimalConditionalListImpl extends ConfigConditionalListNumberValImpl<DecimalType> implements ConfigValDecimalConditionalList {
    @SafeVarargs
    public ConfigValDecimalConditionalListImpl(
            @Nullable ConditionalListType<DecimalType> defaultValue, @Nullable ConditionalListType<DecimalType> disabledValue,
            @Nullable RangeContainer<DecimalType> range,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<ConditionalListType<DecimalType>>... configureValues
    ) {
        super(
                ConfigValType.DECIMAL, DecimalType.class, createValCreator(),
                defaultValue == null
                        ? ConditionalListType.create(DecimalType.class, createValCreator())
                        : defaultValue,
                disabledValue == null
                        ? ConditionalListType.create(DecimalType.class, createValCreator())
                        : disabledValue,
                range == null
                        ? new RangeContainer<>(Range.of(DecimalType.MIN, DecimalType.MAX))
                        : range,
                constraints, configureValues
        );
    }

    private static Function<String, DecimalType> createValCreator() {
        return str -> CustomType.parse(DecimalType.class, str);
    }
}