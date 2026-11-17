/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype.type;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.values.collection.*;
import ru.mousecray.mousecore.api.customtype.values.logic.ConditionType;
import ru.mousecray.mousecore.api.customtype.values.logic.PlusMinusType;
import ru.mousecray.mousecore.api.customtype.values.minecraft.MinecraftBlockType;
import ru.mousecray.mousecore.api.customtype.values.minecraft.MinecraftItemStackType;
import ru.mousecray.mousecore.api.customtype.values.minecraft.MinecraftItemType;
import ru.mousecray.mousecore.api.customtype.values.minecraft.MinecraftTagType;
import ru.mousecray.mousecore.api.customtype.values.number.*;
import ru.mousecray.mousecore.api.customtype.values.other.ColorType;
import ru.mousecray.mousecore.api.customtype.values.other.EnumType;
import ru.mousecray.mousecore.api.customtype.values.other.RegexType;
import ru.mousecray.mousecore.api.customtype.values.other.StringType;
import ru.mousecray.mousecore.api.customtype.values.vec3.BlockPosType;
import ru.mousecray.mousecore.api.customtype.values.vec3.Vector3dType;

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
    RANGE(ValTypeCategory.NUMBER, "Range", RangeType.class),
    TIME(ValTypeCategory.NUMBER, "Time", TimeType.class),

    VECTOR3D(ValTypeCategory.VECTOR, "Vector 3D", Vector3dType.class),
    BLOCK_POS(ValTypeCategory.VECTOR, "Block Position", BlockPosType.class),

    SIMPLE_STRING(ValTypeCategory.STRING, "String", StringType.class),
    ENUM(ValTypeCategory.ENUM, "Enum", EnumType.class),
    COLOR(ValTypeCategory.COLOR, "Color", ColorType.class),
    REGEX(ValTypeCategory.PATTERN, "Regex", RegexType.class),
    EXPRESSION(ValTypeCategory.EXPRESSION, "Expression", ExpressionType.class),

    SIMPLE_LIST(ValTypeCategory.CONTAINER, "List", SimpleListType.class),
    SIMPLE_TUPLE(ValTypeCategory.CONTAINER, "Tuple", SimpleTupleType.class),
    SIMPLE_MAP(ValTypeCategory.CONTAINER, "Map", SimpleMapType.class),
    SIMPLE_TABLE(ValTypeCategory.CONTAINER, "Table", SimpleTableType.class),
    SIMPLE_MATRIX(ValTypeCategory.CONTAINER, "Matrix", SimpleMatrixType.class),
    NBT(ValTypeCategory.CONTAINER, "Nbt", NBTType.class),

    SIMPLE_CONDITION(ValTypeCategory.CONDITION, "Condition", ConditionType.class),

    MINECRAFT_BLOCK(ValTypeCategory.MINECRAFT, "Minecraft Block", MinecraftBlockType.class),
    MINECRAFT_ITEM(ValTypeCategory.MINECRAFT, "Minecraft Item", MinecraftItemType.class),
    MINECRAFT_ITEM_STACK(ValTypeCategory.MINECRAFT, "Minecraft Item Stack", MinecraftItemStackType.class),
    MINECRAFT_TAG(ValTypeCategory.MINECRAFT, "Minecraft Tag", MinecraftTagType.class),
    MINECRAFT_ENTITY(ValTypeCategory.MINECRAFT, "Minecraft Entity", MinecraftEntityType.class),
    MINECRAFT_DIMENSION(ValTypeCategory.MINECRAFT, "Minecraft Dimension", MinecraftDimensionType.class),
    MINECRAFT_BIOME(ValTypeCategory.MINECRAFT, "Minecraft Biome", MinecraftBiomeType.class),
    MINECRAFT_EFFECT(ValTypeCategory.MINECRAFT, "Minecraft Effect", MinecraftEffectType.class),
    MINECRAFT_ENCHANTMENT(ValTypeCategory.MINECRAFT, "Minecraft Enchantment", MinecraftEnchantmentType.class),
    MINECRAFT_PARTICLE(ValTypeCategory.MINECRAFT, "Minecraft Particle", MinecraftParticleType.class),
    MINECRAFT_SOUND(ValTypeCategory.MINECRAFT, "Minecraft Sound", MinecraftSoundType.class),

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