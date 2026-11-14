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
import ru.mousecray.mousecore.api.config.val.list.ConfigValPlusMinusList;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.customtype.values.PlusMinusType;
import ru.mousecray.mousecore.api.customtype.values.SimpleListType;
import ru.mousecray.mousecore.core.config.val.base.list.ConfigValSimpleListLogicalImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
@FieldsAreNonnullByDefault
public class ConfigValPlusMinusListImpl extends ConfigValSimpleListLogicalImpl<PlusMinusType> implements ConfigValPlusMinusList {
    @SafeVarargs
    public ConfigValPlusMinusListImpl(
            boolean isConditional,
            @Nullable SimpleListType<PlusMinusType> defaultValue, @Nullable SimpleListType<PlusMinusType> disabledValue,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<SimpleListType<PlusMinusType>>... configureValues
    ) {
        super(
                isConditional, createValCreator(),
                defaultValue == null
                        ? SimpleListType.create(ValType.PLUS_MINUS, isConditional, createValCreator())
                        : defaultValue,
                disabledValue == null
                        ? SimpleListType.create(ValType.PLUS_MINUS, isConditional, createValCreator())
                        : disabledValue,
                constraints, configureValues
        );
    }

    private static Function<String, PlusMinusType> createValCreator() {
        return str -> CustomType.parse(PlusMinusType.class, str);
    }
}