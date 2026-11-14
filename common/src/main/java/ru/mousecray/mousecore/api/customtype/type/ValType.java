/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.api.customtype.type;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.values.*;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public enum ValType implements IValType {
    PLUS_MINUS(ValTypeCategory.LOGICAL, "Logical", PlusMinusType.class),
    INTEGRAL(ValTypeCategory.NUMBER, "Integral Number", IntegralType.class),
    DECIMAL(ValTypeCategory.NUMBER, "Decimal Number", DecimalType.class),
    PERCENT(ValTypeCategory.NUMBER, "Percent", PercentType.class),
    RANDOM_QUANTITY(ValTypeCategory.NUMBER, "Random Quantity", RandomQuantityType.class),
    SIMPLE_CONDITION(ValTypeCategory.CONDITION, "Condition", ConditionType.class),
    SIMPLE_LIST(ValTypeCategory.LIST, "Simple List", SimpleListType.class),
    SIMPLE_STRING(ValTypeCategory.STRING, "String", StringType.class),
    MINECRAFT_BLOCK(ValTypeCategory.OTHER, "Minecraft Block", MinecraftBlock.class),
    MINECRAFT_ITEM(ValTypeCategory.OTHER, "Minecraft Item", MinecraftItem.class),
    UNDEFINED(ValTypeCategory.UNDEFINED, "Undefined", null);

    private final           String                      displayName;
    private final           ValTypeCategory             category;
    @Nullable private final Class<? extends CustomType> typeClass;

    ValType(ValTypeCategory category, String displayName, @Nullable Class<? extends CustomType> typeClass) {
        this.displayName = displayName;
        this.category = category;
        this.typeClass = typeClass;
    }

    @Override public String getDisplayName()                              { return displayName; }
    @Override public ValTypeCategory getCategory()                        { return category; }
    @Override @Nullable public Class<? extends CustomType> getTypeClass() { return typeClass; }
}
