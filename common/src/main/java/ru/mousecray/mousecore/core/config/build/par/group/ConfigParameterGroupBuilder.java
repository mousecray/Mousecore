/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.build.par.group;

import ru.mousecray.mousecore.api.DisplayName;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.par.group.ConfigParGroup;
import ru.mousecray.mousecore.api.config.sect.ConfigSectBase;
import ru.mousecray.mousecore.api.config.utils.ConfigLang;
import ru.mousecray.mousecore.api.config.utils.ConfigSide;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.log.ConsoleColor;
import ru.mousecray.mousecore.api.log.LogPrefixes;
import ru.mousecray.mousecore.core.config.build.MouseConfigBuilder;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.struct.ConfigDisablerBuilder;
import ru.mousecray.mousecore.core.config.par.ConfigParDisablerImpl;
import ru.mousecray.mousecore.core.config.sect.ConfigSectBaseImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public final class ConfigParameterGroupBuilder<T extends CustomType<?>> extends AbstractConfigChildBuilder {
    public final IValType              valType;
    public final Class<T>              typeClass;
    private      ConfigParGroup<T>     group;
    private      ConfigParDisablerImpl disabler;

    private ConfigParameterGroupBuilder(
            MouseConfigBuilder configBuilder, String path, DisplayName name,
            IValType valType, Class<T> typeClass
    ) {
        super(configBuilder, path, name);
        this.valType = valType;
        this.typeClass = typeClass;
    }

    public static <T extends CustomType<?>> ConfigParameterGroupBuilder<T> create(
            MouseConfigBuilder configBuilder, String path, DisplayName name, IValType valType, Class<T> typeClass
    ) {
        return new ConfigParameterGroupBuilder<>(
                Objects.requireNonNull(configBuilder),
                Objects.requireNonNull(path),
                Objects.requireNonNull(name),
                Objects.requireNonNull(valType),
                Objects.requireNonNull(typeClass)
        );
    }

    @Override
    public ConfigParameterGroupBuilder<T> setComment(@Nullable String comment) {
        super.setComment(comment);
        return this;
    }

    @Override
    public ConfigParameterGroupBuilder<T> setComment(ConfigLang lang, String comment) {
        super.setComment(lang, comment);
        return this;
    }

    @Override
    public ConfigParameterGroupBuilder<T> setDisplayName(String displayName) {
        super.setDisplayName(displayName);
        return this;
    }

    @Override
    public ConfigParameterGroupBuilder<T> setDisplayName(ConfigLang lang, String name) {
        super.setDisplayName(lang, name);
        return this;
    }

    @Override
    public ConfigParameterGroupBuilder<T> setCanBeDisabled() {
        canBeDisabled = true;
        return this;
    }

    @Override
    public void addDisabler(ConfigParDisablerImpl par) {
        disabler = par;
        canBeDisabled = true;
    }

    @Nullable public ConfigParDisablerImpl getDisabler() { return disabler; }
    @Nullable public String getComment()                 { return comment; }

    public ConfigDisablerBuilder<ConfigParameterGroupBuilder<T>> createDisabler(String displayName) {
        return ConfigDisablerBuilder.create(this, Objects.requireNonNull(displayName), false);
    }

    public ConfigDisablerBuilder<ConfigParameterGroupBuilder<T>> createDisabler() {
        return ConfigDisablerBuilder.create(this, "Disabler", true);
    }

    public <TARGET extends CustomType<?>> ConfigConditionalGroupBuilder<T, TARGET> createConditional(
            IValType targetType, Class<TARGET> targetClass, @Nullable Function<String, TARGET> targetCreator
    ) {
        return new ConfigConditionalGroupBuilder<>(this, targetType, targetClass, targetCreator);
    }

    public <TARGET extends CustomType<?>> ConfigConditionalGroupBuilder<T, TARGET> createConditional(
            IValType targetType, Class<TARGET> targetClass
    ) {
        return createConditional(targetType, targetClass, null);
    }

    public <TARGET extends CustomType<?>> ConfigBranchGroupBuilder<T, TARGET> createBranch(
            IValType targetType, Class<TARGET> targetClass, @Nullable Function<String, TARGET> targetCreator
    ) {
        return new ConfigBranchGroupBuilder<>(this, targetType, targetClass, targetCreator);
    }

    public <TARGET extends CustomType<?>> ConfigBranchGroupBuilder<T, TARGET> createBranch(
            IValType targetType, Class<TARGET> targetClass
    ) {
        return createBranch(targetType, targetClass, null);
    }

    public ConfigPhaseGroupBuilder<T> createPhase() {
        return new ConfigPhaseGroupBuilder<>(this);
    }

    public <CONTEXT extends CustomType<?>> ConfigTransformerGroupBuilder<T, CONTEXT> createTransformer(
            IValType contextType, Class<CONTEXT> contextClass, @Nullable Function<String, CONTEXT> contextCreator
    ) {
        return new ConfigTransformerGroupBuilder<>(this, contextType, contextClass, contextCreator);
    }

    public <CONTEXT extends CustomType<?>> ConfigTransformerGroupBuilder<T, CONTEXT> createTransformer(
            IValType contextType, Class<CONTEXT> contextClass
    ) {
        return createTransformer(contextType, contextClass, null);
    }

    public <T2 extends ConfigCustomParGroupBuilder<T, ConfigParameterGroupBuilder<T>>> T2 createGroup(T2 builder) {
        Objects.requireNonNull(builder);
        builder.parBuilder = this;
        return builder;
    }

    @Override
    public ConfigParameterGroupBuilder<T> setSide(@Nullable ConfigSide side) {
        super.setSide(side);
        return this;
    }

    @Override
    public ConfigParameterGroupBuilder<T> markDeprecated() {
        super.markDeprecated();
        return this;
    }

    @Override
    public ConfigParameterGroupBuilder<T> markUnused() {
        super.markUnused();
        return this;
    }

    @Override
    public ConfigParameterGroupBuilder<T> markExperimental() {
        super.markExperimental();
        return this;
    }

    @SuppressWarnings("unchecked")
    public void setGroup(ConfigParGroup<?> group) {
        if (this.group != null && configBuilder.logger != null) {
            configBuilder.logger.atWarn()
                    .withPrefix(LogPrefixes.CONFIG)
                    .withStyle(ConsoleColor.YELLOW_BG)
                    .log("Group '{0}' in ConfigParameterGroup '{1}' was overwritten by '{2}'",
                            this.group.getFullInternalName(), getFullName(), group.getFullInternalName());
        }
        this.group = (ConfigParGroup<T>) group;
    }

    @Override
    public ConfigParameterGroupBuilder<T> setThreadSafe() {
        super.setThreadSafe();
        return this;
    }

    public MouseConfigBuilder buildParameterGroup() {
        if (group == null) throw new IllegalStateException("ConfigParameterGroup '" + getFullName() + "' doesn't contain a group");

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
                                        .log("Parent section of ConfigParameterGroup '{0}' has 'canBeDisabled={1}'. Group updated to match",
                                                getFullName(), canBeDisabled);
                            }
                        }
                        break;
                    }
                }
            }
        }

        if (group instanceof ConfigSectBaseImpl groupImpl) applyMetadata(groupImpl);
        configBuilder.putParameter(path, group);
        return configBuilder;
    }
}