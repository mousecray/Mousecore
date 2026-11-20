package ru.mousecray.mousecore.core.config.build.val;

import ru.mousecray.mousecore.api.config.val.base.ConfigValSimpleList;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.LogicalType;
import ru.mousecray.mousecore.api.customtype.NumberType;
import ru.mousecray.mousecore.api.customtype.OtherType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.customtype.values.logic.PlusMinusType;
import ru.mousecray.mousecore.api.customtype.values.minecraft.*;
import ru.mousecray.mousecore.api.customtype.values.number.*;
import ru.mousecray.mousecore.api.customtype.values.other.*;
import ru.mousecray.mousecore.api.customtype.values.vec3.BlockPosType;
import ru.mousecray.mousecore.api.customtype.values.vec3.Vector3dType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.list.ConfigLogicalListBuilder;
import ru.mousecray.mousecore.core.config.build.val.list.ConfigNumberListBuilder;
import ru.mousecray.mousecore.core.config.build.val.list.ConfigOtherListBuilder;
import ru.mousecray.mousecore.core.config.build.val.list.ConfigVector3ListBuilder;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public final class ConfigListBuilder<BUILDER extends AbstractConfigChildBuilder> {
    private final BUILDER                          parBuilder;
    private final Consumer<ConfigValSimpleList<?>> onComplete;

    private ConfigListBuilder(BUILDER parBuilder, Consumer<ConfigValSimpleList<?>> onComplete) {
        this.parBuilder = parBuilder;
        this.onComplete = onComplete;
    }

    public static <BUILDER extends AbstractConfigChildBuilder> ConfigListBuilder<BUILDER> create(BUILDER parBuilder, Consumer<ConfigValSimpleList<?>> onComplete) {
        return new ConfigListBuilder<>(Objects.requireNonNull(parBuilder), onComplete);
    }

    public <T extends NumberType<?>> ConfigNumberListBuilder<T, BUILDER> setNumberType(IValType valType, Function<String, T> creator) {
        return ConfigNumberListBuilder.create(parBuilder, valType, creator, onComplete);
    }

    public <T extends LogicalType<?>> ConfigLogicalListBuilder<T, BUILDER> setLogicalType(IValType valType, Function<String, T> creator) {
        return ConfigLogicalListBuilder.create(parBuilder, valType, creator, onComplete);
    }

    public <T extends OtherType<?>> ConfigOtherListBuilder<T, BUILDER> setOtherType(IValType valType, Function<String, T> creator) {
        return ConfigOtherListBuilder.create(parBuilder, valType, creator, onComplete);
    }

    public ConfigLogicalListBuilder<PlusMinusType, BUILDER> setPlusMinusType() {
        return setLogicalType(ValType.PLUS_MINUS, str -> CustomType.parse(PlusMinusType.class, str));
    }

    public ConfigNumberListBuilder<DecimalType, BUILDER> setDecimalType() {
        return setNumberType(ValType.DECIMAL, str -> CustomType.parse(DecimalType.class, str));
    }
    public ConfigNumberListBuilder<IntegralType, BUILDER> setIntegralType() {
        return setNumberType(ValType.INTEGRAL, str -> CustomType.parse(IntegralType.class, str));
    }
    public ConfigNumberListBuilder<PercentType, BUILDER> setPercentType() {
        return setNumberType(ValType.PERCENT, str -> CustomType.parse(PercentType.class, str));
    }
    public ConfigNumberListBuilder<RandomQuantityType, BUILDER> setRandomQuantityType() {
        return setNumberType(ValType.RANDOM_QUANTITY, str -> CustomType.parse(RandomQuantityType.class, str));
    }
    public ConfigNumberListBuilder<RangeType, BUILDER> setRangeType() {
        return setNumberType(ValType.RANGE, str -> CustomType.parse(RangeType.class, str));
    }
    public ConfigNumberListBuilder<TimeType, BUILDER> setTimeType() {
        return setNumberType(ValType.TIME, str -> CustomType.parse(TimeType.class, str));
    }

    public ConfigVector3ListBuilder<DecimalType, Vector3dType, BUILDER> setVector3dType() {
        return ConfigVector3ListBuilder.create(parBuilder, ValType.VECTOR3D, str -> CustomType.parse(Vector3dType.class, str), Vector3dType::create, onComplete);
    }
    public ConfigVector3ListBuilder<IntegralType, BlockPosType, BUILDER> setBlockPosType() {
        return ConfigVector3ListBuilder.create(parBuilder, ValType.BLOCK_POS, str -> CustomType.parse(BlockPosType.class, str), BlockPosType::create, onComplete);
    }

    public ConfigOtherListBuilder<StringType, BUILDER> setStringType() {
        return setOtherType(ValType.SIMPLE_STRING, str -> CustomType.parse(StringType.class, str));
    }
    public ConfigOtherListBuilder<ColorType, BUILDER> setColorType() {
        return setOtherType(ValType.COLOR, str -> CustomType.parse(ColorType.class, str));
    }
    public ConfigOtherListBuilder<RegexType, BUILDER> setRegexType() {
        return setOtherType(ValType.REGEX, str -> CustomType.parse(RegexType.class, str));
    }
    public ConfigOtherListBuilder<ExpressionType, BUILDER> setExpressionType() {
        return setOtherType(ValType.EXPRESSION, str -> CustomType.parse(ExpressionType.class, str));
    }
    public <E extends Enum<E>> ConfigOtherListBuilder<EnumType<E>, BUILDER> setEnumType(Class<E> enumClass) {
        return setOtherType(ValType.ENUM, str -> EnumType.parseEnum(enumClass, str));
    }

    public ConfigOtherListBuilder<MinecraftBlockType, BUILDER> setBlockType() {
        return setOtherType(ValType.MINECRAFT_BLOCK, str -> CustomType.parse(MinecraftBlockType.class, str));
    }
    public ConfigOtherListBuilder<MinecraftItemType, BUILDER> setItemType() {
        return setOtherType(ValType.MINECRAFT_ITEM, str -> CustomType.parse(MinecraftItemType.class, str));
    }
    public ConfigOtherListBuilder<MinecraftItemStackType, BUILDER> setItemStackType() {
        return setOtherType(ValType.MINECRAFT_ITEM_STACK, str -> CustomType.parse(MinecraftItemStackType.class, str));
    }
    public ConfigOtherListBuilder<MinecraftEntityType, BUILDER> setEntityType() {
        return setOtherType(ValType.MINECRAFT_ENTITY, str -> CustomType.parse(MinecraftEntityType.class, str));
    }
    public ConfigOtherListBuilder<MinecraftDimensionType, BUILDER> setDimensionType() {
        return setOtherType(ValType.MINECRAFT_DIMENSION, str -> CustomType.parse(MinecraftDimensionType.class, str));
    }
    public ConfigOtherListBuilder<MinecraftBiomeType, BUILDER> setBiomeType() {
        return setOtherType(ValType.MINECRAFT_BIOME, str -> CustomType.parse(MinecraftBiomeType.class, str));
    }
    public ConfigOtherListBuilder<MinecraftEffectType, BUILDER> setEffectType() {
        return setOtherType(ValType.MINECRAFT_EFFECT, str -> CustomType.parse(MinecraftEffectType.class, str));
    }
    public ConfigOtherListBuilder<MinecraftEnchantmentType, BUILDER> setEnchantmentType() {
        return setOtherType(ValType.MINECRAFT_ENCHANTMENT, str -> CustomType.parse(MinecraftEnchantmentType.class, str));
    }
    public ConfigOtherListBuilder<MinecraftParticleType, BUILDER> setParticleType() {
        return setOtherType(ValType.MINECRAFT_PARTICLE, str -> CustomType.parse(MinecraftParticleType.class, str));
    }
    public ConfigOtherListBuilder<MinecraftSoundType, BUILDER> setSoundType() {
        return setOtherType(ValType.MINECRAFT_SOUND, str -> CustomType.parse(MinecraftSoundType.class, str));
    }
    public ConfigOtherListBuilder<MinecraftTagType, BUILDER> setTagType(IValType targetValType) {
        return setOtherType(ValType.MINECRAFT_TAG, str -> MinecraftTagType.parse(str, targetValType));
    }
}