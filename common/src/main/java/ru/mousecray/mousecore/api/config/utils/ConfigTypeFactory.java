/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.api.config.utils;

import java.util.ServiceLoader;

public abstract class ConfigTypeFactory {
    private static final ConfigTypeFactory INSTANCE = ServiceLoader
            .load(ConfigTypeFactory.class, ConfigTypeFactory.class.getClassLoader())
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(
                    """
                            ===============================================================
                            [Mousecore API] Critical error: ConfigTypeFactory implementation is missing!
                            The core mod ('mousecore-common') is not loaded or bootstrap failed.
                            Please, verify your mod installation or contact the author.
                            ==============================================================="""
            ));

    protected ConfigTypeFactory()                 { }
    public static ConfigTypeFactory getInstance() { return INSTANCE; }

    public abstract Constraint.Builder CONSTRAINT_BUILDER();
    public abstract <T> PredefinedValue<T> PREDEFINED_VALUE(String displayName, T value);
}