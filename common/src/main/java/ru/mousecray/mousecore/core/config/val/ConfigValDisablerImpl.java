/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.core.config.val;

import org.apache.commons.lang3.tuple.Pair;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.ConfigValDisabler;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.values.PlusMinusType;

import javax.annotation.Nullable;
import java.util.List;

public class ConfigValDisablerImpl extends ConfigValPlusMinusImpl implements ConfigValDisabler {
    @SafeVarargs
    public ConfigValDisablerImpl(
            PlusMinusType defaultValue, PlusMinusType disabledValue,
            @Nullable List<Pair<String, String>> additionalThreshold,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<PlusMinusType>... configureValues
    ) {
        super(
                defaultValue, disabledValue, constraints,
                additionalThreshold, configureValues
        );
    }

    @Override public boolean isDisabled()          { return value.isFalse(); }
    @Override public void setDisabled(boolean val) { setValue(PlusMinusType.create(!val)); }
}