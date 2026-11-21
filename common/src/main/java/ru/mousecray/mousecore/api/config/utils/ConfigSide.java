package ru.mousecray.mousecore.api.config.utils;

import ru.mousecray.mousecore.api.config.val.ILocaleType;

import javax.annotation.Nonnull;

public enum ConfigSide {
    CLIENT(ConfigLocaleType.SIDE_CLIENT),
    SERVER(ConfigLocaleType.SIDE_SERVER),
    SYNCED(ConfigLocaleType.SIDE_SYNCED),
    COMMON(ConfigLocaleType.SIDE_COMMON);

    private final ILocaleType localeType;

    ConfigSide(ILocaleType localeType) {
        this.localeType = localeType;
    }

    @Nonnull public ILocaleType getLocaleType() { return localeType; }
    public boolean isSynced()                   { return this == SYNCED; }

    public boolean isFrozenIn(@Nonnull ConfigEnvironment env) {
        return switch (env) {
            case SINGLEPLAYER -> false;
            case DEDICATED_SERVER -> this == CLIENT;
            case MULTIPLAYER_CLIENT -> this == SERVER || this == SYNCED;
        };
    }
}