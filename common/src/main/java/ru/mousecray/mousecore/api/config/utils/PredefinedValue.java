/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.api.config.utils;

public interface PredefinedValue<T> {
    String getDisplayName();
    T getValue();

    static <T> PredefinedValue<T> create(String displayName, T value) {
        return ConfigTypeFactory.getInstance().PREDEFINED_VALUE(displayName, value);
    }
}