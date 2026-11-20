/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.core.config.val.base;

import ru.mousecray.mousecore.api.VariableValue;
import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.ConfigDictionary;
import ru.mousecray.mousecore.api.config.utils.ConfigLocaleType;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.base.ConfigValSimpleList;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.values.collection.SimpleListType;
import ru.mousecray.mousecore.api.customtype.values.logic.ConditionalValType;
import ru.mousecray.mousecore.api.error.ValueFormatException;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
@FieldsAreNonnullByDefault
public abstract class ConfigValSimpleListImpl<T extends CustomType<?>>
        extends ConfigValListImpl<ConditionalValType<T>, T, SimpleListType<T>> implements ConfigValSimpleList<T> {

    protected final boolean isConditional;
    protected final boolean isUnique;

    @SafeVarargs
    public ConfigValSimpleListImpl(
            boolean isConditional, boolean isUnique,
            @Nullable Function<String, T> valCreator,
            SimpleListType<T> defaultValue, SimpleListType<T> disabledValue,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<SimpleListType<T>>... configureValues
    ) {
        super(defaultValue, disabledValue, valCreator, constraints, configureValues);
        this.isConditional = isConditional;
        this.isUnique = isUnique;
    }

    @SafeVarargs
    public ConfigValSimpleListImpl(
            @Nullable Function<String, T> valCreator,
            SimpleListType<T> defaultValue, SimpleListType<T> disabledValue,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<SimpleListType<T>>... configureValues
    ) {
        this(defaultValue.isConditional(), defaultValue.isUnique(), valCreator, defaultValue, disabledValue, constraints, configureValues);
    }

    @Override public boolean isConditional() { return isConditional; }
    @Override public boolean isUnique()      { return isUnique; }

    @Override
    protected VariableValue<SimpleListType<T>> parseValue(@Nullable String value) {
        try {
            return VariableValue.create(
                    SimpleListType.fromString(
                            getListComponentType(),
                            isConditional,
                            isUnique,
                            value,
                            valCreator,
                            hasLogger() ? getLogger() : null
                    )
            );
        } catch (ValueFormatException ignore) { return VariableValue.create(); }
    }

    @Override
    @Nullable
    public String getRules(ConfigDictionary dictionary) {
        String baseRule = dictionary.getRulesForType(getType());
        if (baseRule == null) return null;

        StringBuilder sb = new StringBuilder(baseRule);

        if (isConditional()) sb.append(". ").append(dictionary.getLocaleForLocale(ConfigLocaleType.RULE_MODIFIER_CONDITIONAL));
        if (isUnique()) sb.append(". ").append(dictionary.getLocaleForLocale(ConfigLocaleType.RULE_MODIFIER_UNIQUE));

        return sb.toString();
    }
}