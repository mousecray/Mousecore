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
import ru.mousecray.mousecore.api.config.val.ConfigValEnum;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.values.other.EnumType;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.core.config.utils.PredefinedValueImpl;
import ru.mousecray.mousecore.core.config.val.base.ConfigValOtherImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@FieldsAreNonnullByDefault
@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public class ConfigValEnumImpl<E extends Enum<E>> extends ConfigValOtherImpl<EnumType<E>> implements ConfigValEnum<E> {
    private final Class<E> enumClass;

    @SafeVarargs
    public ConfigValEnumImpl(
            Class<E> enumClass,
            EnumType<E> defaultValue, EnumType<E> disabledValue,
            @Nullable String specificDataType,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<EnumType<E>>... configureValues
    ) {
        super(
                defaultValue, disabledValue, specificDataType, constraints,
                genEnumValues(enumClass, configureValues)
        );
        this.enumClass = Objects.requireNonNull(enumClass);
    }

    private static <E extends Enum<E>> PredefinedValue<EnumType<E>>[] genEnumValues(
            Class<E> enumClass,
            @Nullable PredefinedValue<EnumType<E>>[] predefinedValues
    ) {
        List<PredefinedValue<EnumType<E>>> list = new ArrayList<>();
        if (predefinedValues != null) for (PredefinedValue<EnumType<E>> val : predefinedValues) if (val != null) list.add(val);

        for (E constant : enumClass.getEnumConstants()) {
            boolean exists = false;
            for (PredefinedValue<EnumType<E>> pv : list) {
                if (pv.getValue() != null && pv.getValue().getEnum() == constant) {
                    exists = true;
                    break;
                }
            }
            if (!exists) list.add(new PredefinedValueImpl<>(constant.name(), EnumType.create(constant)));
        }
        return list.toArray(new PredefinedValue[0]);
    }

    @Override
    protected VariableValue<EnumType<E>> parseValue(@Nullable String value) {
        try {
            return VariableValue.create(EnumType.parseEnum(enumClass, value));
        } catch (ValueFormatException ignore) { return VariableValue.create(); }
    }

    @Override public E getEnum()             { return getValue().getEnum(); }
    @Override public Class<E> getEnumClass() { return enumClass; }
    @Override public void setEnum(E value)   { setValue(EnumType.create(value)); }
}