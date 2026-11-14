/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.val.list;

import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.list.ConfigValDecimalList;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.range.Range;
import ru.mousecray.mousecore.api.customtype.range.RangeContainer;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.customtype.values.DecimalType;
import ru.mousecray.mousecore.api.customtype.values.SimpleListType;
import ru.mousecray.mousecore.core.config.val.base.list.ConfigValSimpleListNumberImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
@FieldsAreNonnullByDefault
public class ConfigValDecimalListImpl extends ConfigValSimpleListNumberImpl<DecimalType> implements ConfigValDecimalList {
    @SafeVarargs
    public ConfigValDecimalListImpl(
            boolean isConditional,
            @Nullable SimpleListType<DecimalType> defaultValue, @Nullable SimpleListType<DecimalType> disabledValue,
            @Nullable RangeContainer<DecimalType> range,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<SimpleListType<DecimalType>>... configureValues
    ) {
        super(
                isConditional, createValCreator(),
                defaultValue == null
                        ? SimpleListType.create(ValType.DECIMAL, isConditional, createValCreator())
                        : defaultValue,
                disabledValue == null
                        ? SimpleListType.create(ValType.DECIMAL, isConditional, createValCreator())
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