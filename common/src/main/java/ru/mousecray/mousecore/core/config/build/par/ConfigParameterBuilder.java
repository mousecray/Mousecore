/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.build.par;

import ru.mousecray.mousecore.api.DisplayName;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.sect.ConfigSectBase;
import ru.mousecray.mousecore.api.config.val.ConfigVal;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.values.collection.SimpleMatrixType;
import ru.mousecray.mousecore.api.customtype.values.collection.SimpleTupleType;
import ru.mousecray.mousecore.api.log.ConsoleColor;
import ru.mousecray.mousecore.api.log.LogPrefixes;
import ru.mousecray.mousecore.core.config.build.MouseConfigBuilder;
import ru.mousecray.mousecore.core.config.build.val.ConfigListBuilder;
import ru.mousecray.mousecore.core.config.build.val.ConfigPlusMinusBuilder;
import ru.mousecray.mousecore.core.config.build.val.ConfigStringBuilder;
import ru.mousecray.mousecore.core.config.build.val.ConfigValueBuilder;
import ru.mousecray.mousecore.core.config.build.val.container.*;
import ru.mousecray.mousecore.core.config.build.val.minecraft.*;
import ru.mousecray.mousecore.core.config.build.val.number.*;
import ru.mousecray.mousecore.core.config.build.val.other.ConfigColorBuilder;
import ru.mousecray.mousecore.core.config.build.val.other.ConfigEnumBuilder;
import ru.mousecray.mousecore.core.config.build.val.other.ConfigExpressionBuilder;
import ru.mousecray.mousecore.core.config.build.val.other.ConfigRegexBuilder;
import ru.mousecray.mousecore.core.config.build.val.vec3.ConfigBlockPosBuilder;
import ru.mousecray.mousecore.core.config.build.val.vec3.ConfigVector3dBuilder;
import ru.mousecray.mousecore.core.config.par.ConfigParImpl;
import ru.mousecray.mousecore.core.config.val.ConfigValImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigParameterBuilder extends AbstractConfigChildBuilder {
    private ConfigValImpl value;

    private ConfigParameterBuilder(MouseConfigBuilder configBuilder, String path, DisplayName name) {
        super(configBuilder, path, name);
    }

    public static ConfigParameterBuilder create(MouseConfigBuilder configBuilder, DisplayName name, String path) {
        return new ConfigParameterBuilder(
                Objects.requireNonNull(configBuilder),
                Objects.requireNonNull(path),
                Objects.requireNonNull(name)
        );
    }

    @Override
    public ConfigParameterBuilder setComment(@Nullable String comment) {
        this.comment = comment;
        return this;
    }

    @Override
    public ConfigParameterBuilder setCanBeDisabled() {
        canBeDisabled = true;
        return this;
    }

    public ConfigPlusMinusBuilder<ConfigParameterBuilder> createPlusMinus()           { return ConfigPlusMinusBuilder.create(this, this::setValue); }
    public ConfigIntegralBuilder<ConfigParameterBuilder> createInteger()              { return ConfigIntegralBuilder.create(this, this::setValue); }
    public ConfigDecimalBuilder<ConfigParameterBuilder> createDecimal()               { return ConfigDecimalBuilder.create(this, this::setValue); }
    public ConfigRandomQuantityBuilder<ConfigParameterBuilder> createRandomQuantity() { return ConfigRandomQuantityBuilder.create(this, this::setValue); }
    public ConfigPercentBuilder<ConfigParameterBuilder> createPercent()               { return ConfigPercentBuilder.create(this, this::setValue); }
    public ConfigStringBuilder<ConfigParameterBuilder> createString()                 { return ConfigStringBuilder.create(this, this::setValue); }

    public ConfigListBuilder<ConfigParameterBuilder> createList()                     { return ConfigListBuilder.create(this, this::setValue); }

    public ConfigRangeBuilder<ConfigParameterBuilder> createRange()                   { return ConfigRangeBuilder.create(this, this::setValue); }
    public ConfigTimeBuilder<ConfigParameterBuilder> createTime()                     { return ConfigTimeBuilder.create(this, this::setValue); }
    public ConfigVector3dBuilder<ConfigParameterBuilder> createVector3d()             { return ConfigVector3dBuilder.create(this, this::setValue); }
    public ConfigBlockPosBuilder<ConfigParameterBuilder> createBlockPos()             { return ConfigBlockPosBuilder.create(this, this::setValue); }

    public <E extends Enum<E>> ConfigEnumBuilder<E, ConfigParameterBuilder> createEnum(Class<E> enumClass) {
        return ConfigEnumBuilder.create(this, enumClass, this::setValue);
    }

    public ConfigColorBuilder<ConfigParameterBuilder> createColor()             { return ConfigColorBuilder.create(this, this::setValue); }
    public ConfigRegexBuilder<ConfigParameterBuilder> createRegex()             { return ConfigRegexBuilder.create(this, this::setValue); }
    public ConfigExpressionBuilder<ConfigParameterBuilder> createExpression()   { return ConfigExpressionBuilder.create(this, this::setValue); }

    public ConfigBlockBuilder<ConfigParameterBuilder> createBlock()             { return ConfigBlockBuilder.create(this, this::setValue); }
    public ConfigItemBuilder<ConfigParameterBuilder> createItem()               { return ConfigItemBuilder.create(this, this::setValue); }
    public ConfigItemStackBuilder<ConfigParameterBuilder> createItemStack()     { return ConfigItemStackBuilder.create(this, this::setValue); }
    public ConfigTagBuilder<ConfigParameterBuilder> createTag(IValType target)  { return ConfigTagBuilder.create(this, target, this::setValue); }
    public ConfigEntityBuilder<ConfigParameterBuilder> createEntity()           { return ConfigEntityBuilder.create(this, this::setValue); }
    public ConfigDimensionBuilder<ConfigParameterBuilder> createDimension()     { return ConfigDimensionBuilder.create(this, this::setValue); }
    public ConfigBiomeBuilder<ConfigParameterBuilder> createBiome()             { return ConfigBiomeBuilder.create(this, this::setValue); }
    public ConfigEffectBuilder<ConfigParameterBuilder> createEffect()           { return ConfigEffectBuilder.create(this, this::setValue); }
    public ConfigEnchantmentBuilder<ConfigParameterBuilder> createEnchantment() { return ConfigEnchantmentBuilder.create(this, this::setValue); }
    public ConfigParticleBuilder<ConfigParameterBuilder> createParticle()       { return ConfigParticleBuilder.create(this, this::setValue); }
    public ConfigSoundBuilder<ConfigParameterBuilder> createSound()             { return ConfigSoundBuilder.create(this, this::setValue); }

    public ConfigNBTBuilder<ConfigParameterBuilder> createNBT()                 { return ConfigNBTBuilder.create(this, this::setValue); }

    public ConfigTupleBuilder<ConfigParameterBuilder> createTuple(List<IValType> schema, SimpleTupleType def, SimpleTupleType dis) {
        return ConfigTupleBuilder.create(this, schema, def, dis, this::setValue);
    }

    public <K extends CustomType<?>, V extends CustomType<?>> ConfigMapBuilder<K, V, ConfigParameterBuilder> createMap(IValType keyType, IValType valueType) {
        return ConfigMapBuilder.create(this, keyType, valueType, this::setValue);
    }

    public ConfigTableBuilder<ConfigParameterBuilder> createTable(List<IValType> schema) {
        return ConfigTableBuilder.create(this, schema, this::setValue);
    }

    public ConfigMatrixBuilder<ConfigParameterBuilder> createMatrix(List<IValType> schema, SimpleMatrixType def, SimpleMatrixType dis) {
        return ConfigMatrixBuilder.create(this, schema, def, dis, this::setValue);
    }

    public <T2 extends ConfigValueBuilder<?, ConfigParameterBuilder>> T2 createValue(T2 builder) {
        Objects.requireNonNull(builder);
        builder.parBuilder = this;
        builder.onComplete = this::setValue;
        return builder;
    }

    public void setValue(ConfigVal value) {
        Objects.requireNonNull(value);
        if (this.value != null && configBuilder.logger != null) {
            configBuilder.logger.atWarn()
                    .withPrefix(LogPrefixes.CONFIG)
                    .withStyle(ConsoleColor.YELLOW_BG)
                    .log("ConfigValue '{0}' in ConfigParameter '{1}' was overwritten by ConfigValue '{2}' " +
                            "It ConstraintImpl will be skipped", this.value, getFullName(), value
                    );
        }
        this.value = (ConfigValImpl<?>) value;
    }

    public MouseConfigBuilder buildParameter() {
        if (value == null) {
            throw new IllegalStateException("ConfigParameter '" + getFullName() + "' doesn't contain a value");
        }

        if (path.length() > 1) {
            String parentPath      = path.substring(0, path.length() - 1);
            int    lastDot         = parentPath.lastIndexOf('.');
            String grandParentPath = lastDot >= 0 ? parentPath.substring(0, lastDot + 1) : ".";
            String parentName      = lastDot >= 0 ? parentPath.substring(lastDot + 1) : parentPath;

            List<ConfigSectBase> parentSections = configBuilder.sections.get(grandParentPath);
            if (parentSections != null) {
                for (ConfigSectBase base : parentSections) {
                    if (base.getName().getInternalName().equals(parentName)) {
                        if (base.canBeDisabled() != canBeDisabled) {
                            canBeDisabled = base.canBeDisabled();
                            if (configBuilder.logger != null && !configBuilder.autoDisable) {
                                configBuilder.logger.atWarn()
                                        .withPrefix(LogPrefixes.CONFIG)
                                        .withStyle(ConsoleColor.YELLOW_BG)
                                        .log("Parent section of ConfigParameter '{0}' has 'canBeDisabled={1}'. Parameter updated to match",
                                                getFullName(), canBeDisabled);
                            }
                        }
                        break;
                    }
                }
            }
        }

        configBuilder.putParameter(path, new ConfigParImpl(name, canBeDisabled, value, comment));
        return configBuilder;
    }
}