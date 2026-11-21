/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.api.config.utils;

import ru.mousecray.mousecore.api.config.val.ILocaleType;

import javax.annotation.Nonnull;
import java.util.Objects;

public enum ConfigLocaleType implements ILocaleType {
    CONSTRAINTS("Constraints"),
    DEFAULT("Default"),
    DISABLED("Disabled"),
    DISABLED_STATE("Disabled"),
    DISABLE_PAR("Disabled"),
    PREDEFINED("Predefined"),
    RANGE("Valid Range"),
    RULES("Rules"),
    TYPE("Type"),
    VARIANTS("Variants"),
    ANY_VARIANT("Any"),
    NONE_VARIANT("None"),
    LIST_VARIANT("Use List"),
    RULE_MODIFIER_CONDITIONAL("[!] before value - opposite"),
    RULE_MODIFIER_UNIQUE("Duplicates are not allowed"),
    GROUP_RULE("Rule"),
    GROUP_FILTER_LIST("Filter List"),
    GROUP_VALUE("Value"),
    GROUP_RANGE("Range"),
    GROUP_MATCH_VALUE("Match Value"),
    GROUP_ELSE_VALUE("Else Value"),
    GROUP_REPLACEMENT("Replacement"),
    GROUP_FALLBACK("Fallback");

    private final String displayName;
    ConfigLocaleType(@Nonnull String displayName)     { this.displayName = Objects.requireNonNull(displayName); }
    @Nonnull @Override public String getDisplayName() { return displayName; }
}
