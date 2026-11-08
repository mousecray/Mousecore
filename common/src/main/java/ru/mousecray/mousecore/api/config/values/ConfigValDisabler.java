/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.config.values;

import org.apache.commons.lang3.tuple.Pair;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.customtype.values.PlusMinusType;

import javax.annotation.Nullable;
import java.util.List;

public class ConfigValDisabler extends ConfigValPlusMinus {
    @SafeVarargs
    public ConfigValDisabler(
            PlusMinusType defaultValue, PlusMinusType disabledValue,
            @Nullable Constraint<?>[] constraints,
            @Nullable List<Pair<String, String>> additionalThreshold,
            @Nullable PredefinedValue<PlusMinusType>... configureValues
    ) {
        super(
                defaultValue, disabledValue, constraints,
                additionalThreshold, configureValues
        );
    }

    @Override public boolean isDisabled() { return value.isFalse(); }
    public void setDisabled(boolean val)  { setValue(PlusMinusType.create(!val)); }
}