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
import ru.mousecray.mousecore.api.customtype.values.RandomQuantityType;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
@FieldsAreNonnullByDefault
public class ConfigValRandomQuantityConditionalList extends ConfigConditionalListNumberVal<RandomQuantityType> {
    @SafeVarargs
    public ConfigValRandomQuantityConditionalList(
            @Nullable ConditionalListType<RandomQuantityType> defaultValue, @Nullable ConditionalListType<RandomQuantityType> disabledValue,
            @Nullable RangeContainer<RandomQuantityType> range,
            @Nullable Constraint<?>[] constraints,
            @Nullable PredefinedValue<ConditionalListType<RandomQuantityType>>... configureValues
    ) {
        super(
                ConfigValType.RANDOM_QUANTITY, RandomQuantityType.class, createValCreator(),
                defaultValue == null
                        ? ConditionalListType.create(RandomQuantityType.class, createValCreator())
                        : defaultValue,
                disabledValue == null
                        ? ConditionalListType.create(RandomQuantityType.class, createValCreator())
                        : disabledValue,
                range == null
                        ? new RangeContainer<>(Range.of(RandomQuantityType.MIN, RandomQuantityType.MAX))
                        : range,
                constraints, configureValues
        );
    }

    private static Function<String, RandomQuantityType> createValCreator() {
        return str -> CustomType.parse(RandomQuantityType.class, str);
    }
}