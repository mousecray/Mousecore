/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.core.config.val.list.simple;

import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.list.simple.ConfigValRandomQuantityList;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.range.Range;
import ru.mousecray.mousecore.api.customtype.range.RangeContainer;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.customtype.values.RandomQuantityType;
import ru.mousecray.mousecore.api.customtype.values.SimpleListType;
import ru.mousecray.mousecore.core.config.val.base.list.simple.ConfigSimpleListNumberValImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
@FieldsAreNonnullByDefault
public class ConfigValRandomQuantityListImpl extends ConfigSimpleListNumberValImpl<RandomQuantityType> implements ConfigValRandomQuantityList {
    @SafeVarargs
    public ConfigValRandomQuantityListImpl(
            @Nullable SimpleListType<RandomQuantityType> defaultValue, @Nullable SimpleListType<RandomQuantityType> disabledValue,
            @Nullable RangeContainer<RandomQuantityType> range,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<SimpleListType<RandomQuantityType>>... configureValues
    ) {
        super(
                createValCreator(),
                defaultValue == null
                        ? SimpleListType.create(ValType.RANDOM_QUANTITY, createValCreator())
                        : defaultValue,
                disabledValue == null
                        ? SimpleListType.create(ValType.RANDOM_QUANTITY, createValCreator())
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