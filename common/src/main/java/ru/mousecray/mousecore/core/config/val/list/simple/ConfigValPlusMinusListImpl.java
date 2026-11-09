/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.core.config.val.list.simple;

import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.list.simple.ConfigValPlusMinusList;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.values.PlusMinusType;
import ru.mousecray.mousecore.api.customtype.values.SimpleListType;
import ru.mousecray.mousecore.core.config.val.base.list.simple.ConfigSimpleListLogicalValImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
@FieldsAreNonnullByDefault
public class ConfigValPlusMinusListImpl extends ConfigSimpleListLogicalValImpl<PlusMinusType> implements ConfigValPlusMinusList {
    @SafeVarargs
    public ConfigValPlusMinusListImpl(
            @Nullable SimpleListType<PlusMinusType> defaultValue, @Nullable SimpleListType<PlusMinusType> disabledValue,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<SimpleListType<PlusMinusType>>... configureValues
    ) {
        super(
                PlusMinusType.class, createValCreator(),
                defaultValue == null
                        ? SimpleListType.create(PlusMinusType.class, createValCreator())
                        : defaultValue,
                disabledValue == null
                        ? SimpleListType.create(PlusMinusType.class, createValCreator())
                        : disabledValue,
                constraints, configureValues
        );
    }

    private static Function<String, PlusMinusType> createValCreator() {
        return str -> CustomType.parse(PlusMinusType.class, str);
    }
}