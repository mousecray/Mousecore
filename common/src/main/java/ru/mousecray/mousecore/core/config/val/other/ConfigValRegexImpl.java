/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.core.config.val.other;

import ru.mousecray.mousecore.api.VariableValue;
import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.other.ConfigValRegex;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.values.other.RegexType;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.core.config.val.base.ConfigValOtherImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.regex.Pattern;

@FieldsAreNonnullByDefault
@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public class ConfigValRegexImpl extends ConfigValOtherImpl<RegexType> implements ConfigValRegex {

    @SafeVarargs
    public ConfigValRegexImpl(
            RegexType defaultValue, RegexType disabledValue,
            @Nullable String specificDataType,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<RegexType>... configureValues
    ) {
        super(defaultValue, disabledValue, specificDataType, constraints, configureValues);
    }

    @Override
    protected VariableValue<RegexType> parseValue(@Nullable String value) {
        try {
            return VariableValue.create(CustomType.parse(RegexType.class, value));
        } catch (ValueFormatException ignore) { return VariableValue.create(); }
    }

    @Override public String getPatternString()                  { return getValue().asString(); }
    @Override public Pattern getPattern()                       { return getValue().asPattern(); }
    @Override public int getFlags()                             { return getValue().getFlags(); }

    @Override public boolean matches(@Nullable String input)    { return getValue().matches(input); }
    @Override public boolean find(@Nullable String input)       { return getValue().find(input); }

    @Override public void setPattern(String pattern)            { setValue(RegexType.create(pattern)); }
    @Override public void setPattern(String pattern, int flags) { setValue(RegexType.create(pattern, flags)); }
}