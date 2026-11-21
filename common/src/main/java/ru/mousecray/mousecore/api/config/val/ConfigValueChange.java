/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.api.config.val;

import ru.mousecray.mousecore.api.customtype.CustomType;

import javax.annotation.Nonnull;
import java.util.Objects;

public record ConfigValueChange<V extends CustomType<?>>(
        @Nonnull ConfigVal<V> configVal,
        @Nonnull V oldValue,
        @Nonnull V newValue
) {
    public ConfigValueChange {
        Objects.requireNonNull(configVal);
        Objects.requireNonNull(oldValue);
        Objects.requireNonNull(newValue);
    }
}