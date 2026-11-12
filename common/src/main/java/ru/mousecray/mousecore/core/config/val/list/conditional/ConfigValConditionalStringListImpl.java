/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.core.config.val.list.conditional;

import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.list.conditional.ConfigValConditionalStringList;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.customtype.values.ConditionalListType;
import ru.mousecray.mousecore.api.customtype.values.StringType;
import ru.mousecray.mousecore.core.config.val.base.list.conditional.ConfigValConditionalListOtherImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
@FieldsAreNonnullByDefault
public class ConfigValConditionalStringListImpl extends ConfigValConditionalListOtherImpl<StringType> implements ConfigValConditionalStringList {
    @SafeVarargs
    public ConfigValConditionalStringListImpl(
            @Nullable ConditionalListType<StringType> defaultValue, @Nullable ConditionalListType<StringType> disabledValue,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<ConditionalListType<StringType>>... configureValues
    ) {
        super(
                createValCreator(),
                defaultValue == null
                        ? ConditionalListType.create(ValType.SIMPLE_STRING, createValCreator())
                        : defaultValue,
                disabledValue == null
                        ? ConditionalListType.create(ValType.SIMPLE_STRING, createValCreator())
                        : disabledValue,
                constraints, configureValues
        );
    }

    private static Function<String, StringType> createValCreator() {
        return str -> CustomType.parse(StringType.class, str);
    }
}