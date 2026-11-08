/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.config;

import org.apache.commons.lang3.tuple.Pair;
import ru.mousecray.mousecore.api.DisplayName;
import ru.mousecray.mousecore.api.customtype.CustomType;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ConfigPar<T extends CustomType<?>> extends ConfigParBase<T> {
    public ConfigPar(
            DisplayName name, boolean canBeDisabled,
            ConfigVal<T> value, @Nullable String comment
    ) {
        super(name, canBeDisabled, Pair.of(name, value), null, comment);
    }

    @Override public boolean isSupportAdditionalValue(ConfigVal<?> val) { return false; }
}