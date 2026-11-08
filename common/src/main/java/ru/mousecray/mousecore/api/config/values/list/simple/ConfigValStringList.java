/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.config.values.list.simple;

import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.specific.ConfigValType;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.values.base.list.simple.ConfigSimpleListOtherVal;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.values.SimpleListType;
import ru.mousecray.mousecore.api.customtype.values.StringType;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
@FieldsAreNonnullByDefault
public class ConfigValStringList extends ConfigSimpleListOtherVal<StringType> {
    @SafeVarargs
    public ConfigValStringList(
            @Nullable SimpleListType<StringType> defaultValue, @Nullable SimpleListType<StringType> disabledValue,
            @Nullable Constraint<?>[] constraints,
            @Nullable PredefinedValue<SimpleListType<StringType>>... configureValues
    ) {
        super(
                ConfigValType.STRING, StringType.class, createValCreator(),
                defaultValue == null
                        ? SimpleListType.create(StringType.class, createValCreator())
                        : defaultValue,
                disabledValue == null
                        ? SimpleListType.create(StringType.class, createValCreator())
                        : disabledValue,
                constraints, configureValues
        );
    }

    private static Function<String, StringType> createValCreator() {
        return str -> CustomType.parse(StringType.class, str);
    }
}