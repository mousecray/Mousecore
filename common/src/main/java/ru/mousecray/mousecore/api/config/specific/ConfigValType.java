/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.config.specific;

import ru.mousecray.mousecore.api.config.IValType;
import ru.mousecray.mousecore.api.customtype.CustomValType;

import javax.annotation.Nonnull;

public enum ConfigValType implements IValType {
    LOGICAL("Logical", CustomValType.LOGICAL),
    INTEGRAL("Integral Number", CustomValType.NUMBER),
    DECIMAL("Decimal Number", CustomValType.NUMBER),
    STRING("String", CustomValType.OTHER),
    PERCENT("PercentType", CustomValType.NUMBER),
    RANDOM_QUANTITY("Random Quantity", CustomValType.NUMBER),
    CONDITION("Condition", CustomValType.OTHER),
    LIST("List", CustomValType.LIST),
    CONDITIONAL_LIST("Conditional List", CustomValType.LIST),
    UNDEFINED("Undefined");

    private final String        displayName;
    private final CustomValType valType;

    ConfigValType(String displayName, CustomValType valType) {
        this.displayName = displayName;
        this.valType = valType;
    }

    ConfigValType(String displayName)                    { this(displayName, CustomValType.UNDEFINED); }

    @Nonnull @Override public String getDisplayName()    { return displayName; }
    @Nonnull @Override public CustomValType getValType() { return valType; }
}