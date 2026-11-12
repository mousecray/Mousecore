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
import ru.mousecray.mousecore.api.log.ConsoleColor;
import ru.mousecray.mousecore.api.log.LogPrefixes;
import ru.mousecray.mousecore.core.config.build.MouseConfigBuilder;
import ru.mousecray.mousecore.core.config.build.val.ConfigPlusMinusBuilder;
import ru.mousecray.mousecore.core.config.build.val.ConfigStringBuilder;
import ru.mousecray.mousecore.core.config.build.val.ConfigValueBuilder;
import ru.mousecray.mousecore.core.config.build.val.number.ConfigDecimalBuilder;
import ru.mousecray.mousecore.core.config.build.val.number.ConfigIntegralBuilder;
import ru.mousecray.mousecore.core.config.build.val.number.ConfigPercentBuilder;
import ru.mousecray.mousecore.core.config.build.val.number.ConfigRandomQuantityBuilder;
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

    public ConfigPlusMinusBuilder<ConfigParameterBuilder> createPlusMinus() {
        return ConfigPlusMinusBuilder.create(this, this::setValue);
    }

    public ConfigIntegralBuilder<ConfigParameterBuilder> createInteger() {
        return ConfigIntegralBuilder.create(this, this::setValue);
    }

    public ConfigDecimalBuilder<ConfigParameterBuilder> createDecimal() {
        return ConfigDecimalBuilder.create(this, this::setValue);
    }

    public ConfigRandomQuantityBuilder<ConfigParameterBuilder> createRandomQuantity() { return ConfigRandomQuantityBuilder.create(this, this::setValue); }
    public ConfigPercentBuilder<ConfigParameterBuilder> createPercent()               { return ConfigPercentBuilder.create(this, this::setValue); }
    public ConfigStringBuilder<ConfigParameterBuilder> createString()                 { return ConfigStringBuilder.create(this, this::setValue); }

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