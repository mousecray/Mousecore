/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.api.config.val;

import ru.mousecray.mousecore.api.customtype.CustomType;

@FunctionalInterface
public interface ConfigValueListener<V extends CustomType<?>> {
    void onValueChanged(ConfigVal<V> configVal, V oldValue, V newValue);
}