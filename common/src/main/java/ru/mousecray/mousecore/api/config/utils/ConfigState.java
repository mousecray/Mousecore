package ru.mousecray.mousecore.api.config.utils;

import ru.mousecray.mousecore.api.config.val.ILocaleType;

import javax.annotation.Nonnull;

public enum ConfigState {
    FROZEN("❄", false, ConfigLocaleType.STATE_FROZEN),
    NEW("\uD83D\uDD25", false, ConfigLocaleType.STATE_NEW),
    DEPRECATED("☁", true, ConfigLocaleType.STATE_DEPRECATED),
    UNUSED("\uD83D\uDCA4", true, ConfigLocaleType.STATE_UNUSED),
    EXPERIMENTAL("⚡", true, ConfigLocaleType.STATE_EXPERIMENTAL);

    private final String      icon;
    private final boolean     manual;
    private final ILocaleType localeType;

    ConfigState(String icon, boolean manual, ILocaleType localeType) {
        this.icon = icon;
        this.manual = manual;
        this.localeType = localeType;
    }

    @Nonnull public String getIcon()            { return icon; }
    public boolean isManual()                   { return manual; }
    @Nonnull public ILocaleType getLocaleType() { return localeType; }
}