/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.core.config.val;

import ru.mousecray.mousecore.api.VariableValue;
import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.ConfigValColor;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.values.other.ColorType;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.core.config.val.base.ConfigValOtherImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@FieldsAreNonnullByDefault
@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public class ConfigValColorImpl extends ConfigValOtherImpl<ColorType> implements ConfigValColor {

    @SafeVarargs
    public ConfigValColorImpl(
            ColorType defaultValue, ColorType disabledValue,
            @Nullable String specificDataType,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<ColorType>... configureValues
    ) {
        super(defaultValue, disabledValue, specificDataType, constraints, configureValues);
    }

    @Override
    protected VariableValue<ColorType> parseValue(@Nullable String value) {
        try {
            return VariableValue.create(CustomType.parse(ColorType.class, value));
        } catch (ValueFormatException ignore) { return VariableValue.create(); }
    }

    @Override public int getArgb()                             { return getValue().asArgb(); }
    @Override public int getRgb()                              { return getValue().asRgb(); }
    @Override public int getAlpha()                            { return getValue().asAlpha(); }
    @Override public int getRed()                              { return getValue().asRed(); }
    @Override public int getGreen()                            { return getValue().asGreen(); }
    @Override public int getBlue()                             { return getValue().asBlue(); }

    @Override public String getHex(boolean includeAlpha)       { return getValue().asHex(includeAlpha); }
    @Override public String getHex()                           { return getValue().asHex(); }

    @Override public void setColor(int argb)                   { setValue(ColorType.create(argb)); }
    @Override public void setColor(int r, int g, int b, int a) { setValue(ColorType.create(r, g, b, a)); }
    @Override public void setColor(int r, int g, int b)        { setValue(ColorType.create(r, g, b)); }
}