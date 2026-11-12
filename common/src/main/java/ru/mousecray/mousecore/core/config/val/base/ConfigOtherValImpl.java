/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.core.config.val.base;

import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.base.ConfigOtherVal;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.OtherType;
import ru.mousecray.mousecore.core.config.val.ConfigValImpl;

import javax.annotation.Nullable;

public abstract class ConfigOtherValImpl<T extends OtherType<?>> extends ConfigValImpl<T> implements ConfigOtherVal<T> {
    @SafeVarargs
    public ConfigOtherValImpl(
            T defaultValue, T disabledValue, @Nullable String specificDataType,
            @Nullable Constraint<? extends CustomType<?>>[] constraints, @Nullable PredefinedValue<T>... configureValues
    ) {
        super(defaultValue, disabledValue, val -> specificDataType, constraints, configureValues);
    }

    @Override protected boolean saveDisabledValue() { return false; }
}
