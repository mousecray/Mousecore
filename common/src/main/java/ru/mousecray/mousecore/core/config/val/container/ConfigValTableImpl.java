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
import ru.mousecray.mousecore.api.config.val.container.ConfigValTable;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.values.collection.SimpleTableType;
import ru.mousecray.mousecore.api.customtype.values.collection.SimpleTupleType;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.core.config.val.ConfigValImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@FieldsAreNonnullByDefault
@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public class ConfigValTableImpl extends ConfigValImpl<SimpleTableType> implements ConfigValTable {
    private final List<IValType> schema;

    @SafeVarargs
    public ConfigValTableImpl(
            List<IValType> schema,
            SimpleTableType defaultValue, SimpleTableType disabledValue,
            @Nullable String specificDataType,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<SimpleTableType>... configureValues
    ) {
        super(defaultValue, disabledValue, val -> specificDataType, constraints, configureValues);
        this.schema = schema;
    }

    @Override
    protected VariableValue<SimpleTableType> parseValue(@Nullable String value) {
        try {
            return VariableValue.create(SimpleTableType.fromString(schema, value, hasLogger() ? getLogger() : null));
        } catch (ValueFormatException ignore) { return VariableValue.create(); }
    }

    @Override protected boolean saveDisabledValue()           { return false; }

    @Override public List<IValType> getSchema()               { return schema; }
    @Override public List<SimpleTupleType> getRows()          { return getValue().asRows(); }
    @Override public void setRows(List<SimpleTupleType> rows) { setValue(SimpleTableType.create(schema, rows)); }
}