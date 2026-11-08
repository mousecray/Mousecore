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
import ru.mousecray.mousecore.api.config.values.base.list.conditional.ConfigConditionalListOtherVal;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.values.ConditionalListType;
import ru.mousecray.mousecore.api.customtype.values.StringType;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
@FieldsAreNonnullByDefault
public class ConfigValConditionalStringList extends ConfigConditionalListOtherVal<StringType> {
    @SafeVarargs
    public ConfigValConditionalStringList(
            @Nullable ConditionalListType<StringType> defaultValue, @Nullable ConditionalListType<StringType> disabledValue,
            @Nullable Constraint<?>[] constraints,
            @Nullable PredefinedValue<ConditionalListType<StringType>>... configureValues
    ) {
        super(
                ConfigValType.STRING, StringType.class, createValCreator(),
                defaultValue == null
                        ? ConditionalListType.create(StringType.class, createValCreator())
                        : defaultValue,
                disabledValue == null
                        ? ConditionalListType.create(StringType.class, createValCreator())
                        : disabledValue,
                constraints, configureValues
        );
    }

    private static Function<String, StringType> createValCreator() {
        return str -> CustomType.parse(StringType.class, str);
    }
}