/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.core.platform;

import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.platform.ConfigTypeFactory;
import ru.mousecray.mousecore.core.config.utils.ConstraintImpl;
import ru.mousecray.mousecore.core.config.utils.PredefinedValueImpl;

public final class ConfigTypeFactoryImpl extends ConfigTypeFactory {
    public ConfigTypeFactoryImpl()                           { }

    @Override public Constraint.Builder CONSTRAINT_BUILDER() { return new ConstraintImpl.BuilderImpl(); }

    @Override
    public <T> PredefinedValue<T> PREDEFINED_VALUE(String displayName, T value) {
        return new PredefinedValueImpl<>(displayName, value);
    }
}