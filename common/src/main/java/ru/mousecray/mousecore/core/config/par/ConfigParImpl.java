/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.par;

import org.apache.commons.lang3.tuple.Pair;
import ru.mousecray.mousecore.api.DisplayName;
import ru.mousecray.mousecore.api.config.par.ConfigPar;
import ru.mousecray.mousecore.api.config.val.ConfigVal;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.core.config.val.ConfigValImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ConfigParImpl<T extends CustomType<?>> extends ConfigParBaseImpl<T> implements ConfigPar<T> {
    public ConfigParImpl(
            DisplayName name, boolean canBeDisabled,
            ConfigValImpl<T> value, @Nullable String comment
    ) {
        super(name, canBeDisabled, Pair.of(name, value), null, comment);
    }

    @Override public boolean isSupportAdditionalValue(ConfigVal<? extends CustomType<?>> val) { return false; }
}