/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.core.config.val.base;

import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.base.ConfigLogicalVal;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.LogicalType;
import ru.mousecray.mousecore.core.config.val.ConfigValImpl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class ConfigLogicalValImpl<T extends LogicalType<?>> extends ConfigValImpl<T> implements ConfigLogicalVal<T> {
    @SafeVarargs
    public ConfigLogicalValImpl(
            T defaultValue, T disabledValue, @Nullable String specificDataType,
            @Nullable Constraint<? extends CustomType<?>>[] constraints, @Nullable PredefinedValue<T>... configureValues
    ) {
        super(defaultValue, disabledValue, val -> specificDataType, constraints, configureValues);
    }

    protected T getLogical()           { return getValue(); }
    @Override public boolean isTrue()  { return getLogical().isTrue(); }
    @Override public boolean isFalse() { return getLogical().isFalse(); }

    protected void setLogical(T value) { setValue(value); }
    @Override public void setTrue()    { setLogical(getLogical().createType(true)); }
    @Override public void setFalse()   { setLogical(getLogical().createType(false)); }

    @SuppressWarnings("unchecked") @Nonnull @Override
    protected T processValueIfNotEqualConstraint(@Nonnull T value) {
        return (T) value.getLogicPipeline().not();
    }

    @Override protected boolean saveDisabledValue() { return false; }
}
