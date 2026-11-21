/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.core.config.utils;

import ru.mousecray.mousecore.api.config.utils.ConfigEnvironment;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class ConfigEnvironmentController {
    private static volatile ConfigEnvironment current = ConfigEnvironment.SINGLEPLAYER;

    private ConfigEnvironmentController() { }

    @Nonnull
    public static ConfigEnvironment getCurrent() {
        return current;
    }

    public static void setCurrent(@Nonnull ConfigEnvironment environment) {
        current = Objects.requireNonNull(environment);
    }
}