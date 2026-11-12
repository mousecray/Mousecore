/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.build.par.group;

import ru.mousecray.mousecore.api.DisplayName;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.par.ConfigParGroup;
import ru.mousecray.mousecore.api.config.sect.ConfigSectBase;
import ru.mousecray.mousecore.api.config.utils.ConfigDictionary;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.log.ConsoleColor;
import ru.mousecray.mousecore.api.log.LogPrefixes;
import ru.mousecray.mousecore.core.config.build.MouseConfigBuilder;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public final class ConfigParameterGroupBuilder<T extends CustomType<?>> {
    public final MouseConfigBuilder configBuilder;
    public final String             path;
    public final DisplayName        name;
    boolean canBeDisabled;
    @Nullable    String         comment;
    public final IValType0      valType;
    public final Class<T>       typeClass;
    private      ConfigParGroup group;

    private ConfigParameterGroupBuilder(
            MouseConfigBuilder configBuilder, String path, DisplayName name,
            IValType0 valType, Class<T> typeClass
    ) {
        this.configBuilder = configBuilder;
        this.path = path;
        this.name = name;
        this.valType = valType;
        this.typeClass = typeClass;
    }

    public static <T extends CustomType<?>> ConfigParameterGroupBuilder<T> create(
            MouseConfigBuilder configBuilder, String path, DisplayName name, IValType0 valType, Class<T> typeClass
    ) {
        return new ConfigParameterGroupBuilder<>(
                Objects.requireNonNull(configBuilder),
                Objects.requireNonNull(path),
                Objects.requireNonNull(name),
                Objects.requireNonNull(valType),
                Objects.requireNonNull(typeClass)
        );
    }

    public ConfigParameterGroupQuantityConditionBuilder<T> createQuantityConditionGroup() {
        return ConfigParameterGroupQuantityConditionBuilder.create(this);
    }

    public <T2 extends ConfigCustomParGroupBuilder<T, ConfigParameterGroupBuilder<T>>> T2 createGroup(T2 builder) {
        Objects.requireNonNull(builder);
        builder.parBuilder = this;
        return builder;
    }

    public void setGroup(ConfigParGroup group) {
        if (this.group != null && configBuilder.logger != null) {
            configBuilder.logger.atWarn()
                    .withPrefix(LogPrefixes.CONFIG)
                    .withStyle(ConsoleColor.YELLOW_BG)
                    .log("Value '{0}' in ConfigParGroupImpl '{1}' was overwritten by ConfigParGroupImpl '{2}'",
                            this.group.getFullInternalName(), getFullName(), group.getFullInternalName());
        }
        this.group = group;
    }

    public MouseConfigBuilder buildParameterGroup() {
        if (group == null) throw new IllegalStateException("ConfigParameterGroup doesn't contain par");
        String               parentPath = path.substring(0, path.length() - 1);
        List<ConfigSectBase> list       = configBuilder.sections.get(parentPath.substring(0, parentPath.lastIndexOf('.') + 1));
        ConfigSectBase       parent     = null;
        for (ConfigSectBase base : list) {
            if (base.getName().getInternalName().equals(parentPath.substring(parentPath.lastIndexOf(".") + 1))) {
                parent = base;
            }
        }
        if (parent != null && parent.canBeDisabled() != canBeDisabled) {
            canBeDisabled = parent.canBeDisabled();
            if (configBuilder.logger != null && !configBuilder.autoDisable) {
                configBuilder.logger.atWarn()
                        .withPrefix(LogPrefixes.CONFIG)
                        .withStyle(ConsoleColor.YELLOW_BG)
                        .log("The Parent section of ConfigParameterGroup '{0}' has flag 'canBeDisabled' " +
                                        "other then the ConfigParameterGroup value. " +
                                        "The value of ConfigParameterGroup has been changed",
                                group.getFullInternalName(), getFullName(), group.getFullInternalName());
            }
        }
        configBuilder.putParameter(path, group);
        return configBuilder;
    }

    public @Nullable ConfigDictionary getDictionary() { return configBuilder.dictionary; }
    public boolean hasDictionary()                    { return configBuilder.dictionary != null; }
    String getFullName()                              { return path + name.getInternalName(); }
}