/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.api.config.utils;

import javax.annotation.Nonnull;
import java.util.Objects;

public enum ConfigEnvironment {
    SINGLEPLAYER,
    DEDICATED_SERVER,
    MULTIPLAYER_CLIENT;

    private static volatile ConfigEnvironment current = SINGLEPLAYER;

    @Nonnull
    public static ConfigEnvironment getCurrent() {
        return current;
    }

    public static void setCurrent(@Nonnull ConfigEnvironment environment) {
        current = Objects.requireNonNull(environment);
    }
}