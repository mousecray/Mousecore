/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.core.config.val.list.simple;

import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.ConfigValType;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.list.simple.ConfigValStringList;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.values.SimpleListType;
import ru.mousecray.mousecore.api.customtype.values.StringType;
import ru.mousecray.mousecore.core.config.val.base.list.simple.ConfigSimpleListOtherValImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
@FieldsAreNonnullByDefault
public class ConfigValStringListImpl extends ConfigSimpleListOtherValImpl<StringType> implements ConfigValStringList {
    @SafeVarargs
    public ConfigValStringListImpl(
            @Nullable SimpleListType<StringType> defaultValue, @Nullable SimpleListType<StringType> disabledValue,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
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