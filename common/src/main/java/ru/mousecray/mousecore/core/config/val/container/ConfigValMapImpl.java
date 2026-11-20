/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.core.config.val.container;

import ru.mousecray.mousecore.api.VariableValue;
import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.container.ConfigValMap;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.values.collection.SimpleMapType;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.core.config.val.ConfigValImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@FieldsAreNonnullByDefault
@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public class ConfigValMapImpl<K extends CustomType<?>, V extends CustomType<?>> extends ConfigValImpl<SimpleMapType<K, V>> implements ConfigValMap<K, V> {
    private final IValType keyType;
    private final IValType valueType;

    @SafeVarargs
    public ConfigValMapImpl(
            IValType keyType, IValType valueType,
            SimpleMapType<K, V> defaultValue, SimpleMapType<K, V> disabledValue,
            @Nullable String specificDataType,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<SimpleMapType<K, V>>... configureValues
    ) {
        super(defaultValue, disabledValue, val -> specificDataType, constraints, configureValues);
        this.keyType = keyType;
        this.valueType = valueType;
    }

    @Override
    protected VariableValue<SimpleMapType<K, V>> parseValue(@Nullable String value) {
        try {
            return VariableValue.create(SimpleMapType.fromString(keyType, valueType, value, hasLogger() ? getLogger() : null));
        } catch (ValueFormatException ignore) { return VariableValue.create(); }
    }

    @Override protected boolean saveDisabledValue() { return false; }

    @Override public IValType getKeyType()          { return keyType; }
    @Override public IValType getValueType()        { return valueType; }
    @Override public Map<K, V> getMap()             { return getValue().asMap(); }
    @Override public void setMap(Map<K, V> map)     { setValue(SimpleMapType.create(keyType, valueType, map)); }
}