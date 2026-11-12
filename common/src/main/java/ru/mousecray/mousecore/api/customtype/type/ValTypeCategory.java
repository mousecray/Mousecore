/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.api.customtype.type;

import javax.annotation.Nonnull;

public enum ValTypeCategory {
    LOGICAL("Logical"),
    NUMBER("Number"),
    STRING("String"),
    LIST("List"),
    CONDITION("Condition"),
    OTHER("Undefined"),
    UNDEFINED("Undefined");

    private final String displayName;
    ValTypeCategory(String displayName)     { this.displayName = displayName; }
    @Nonnull public String getDisplayName() { return displayName; }
}
