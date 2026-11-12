/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.api.customtype.type;

import javax.annotation.Nonnull;

public enum ValTypeMetaCategory {
    CONDITIONAL("Conditional"),
    UNDEFINED("Undefined");

    private final String displayName;
    ValTypeMetaCategory(String displayName) { this.displayName = displayName; }
    @Nonnull public String getDisplayName() { return displayName; }
}
