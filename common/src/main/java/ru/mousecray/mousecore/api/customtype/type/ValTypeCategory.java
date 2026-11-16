/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype.type;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public enum ValTypeCategory {
    LOGICAL("Logical"),
    NUMBER("Number"),
    STRING("String"),
    ENUM("Enum"),
    COLOR("Color"),
    VECTOR("Vector"),
    PATTERN("Pattern"),
    EXPRESSION("Expression"),
    MINECRAFT("Minecraft"),

    CONTAINER("Container"),

    CONDITION("Condition"),
    OTHER("Other"),
    UNDEFINED("Undefined");

    private final String displayName;
    ValTypeCategory(String displayName) { this.displayName = displayName; }
    public String getDisplayName()      { return displayName; }

    public boolean isContainer()        { return this == CONTAINER; }
    public boolean canBeContained()     { return this != CONTAINER && this != CONDITION && this != UNDEFINED; }
    public boolean canBeMapKey()        { return canBeContained() && this != EXPRESSION; }
}