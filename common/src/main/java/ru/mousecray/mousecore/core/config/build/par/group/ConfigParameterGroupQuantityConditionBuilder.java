/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.build.par.group;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.tuple.Pair;
import ru.mousecray.mousecore.api.DisplayName;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.sect.ConfigSectBase;
import ru.mousecray.mousecore.api.config.utils.ConfigLocaleType;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.ConfigVal;
import ru.mousecray.mousecore.api.config.val.ConfigValRandomQuantity;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.values.ConditionType;
import ru.mousecray.mousecore.api.log.ConsoleColor;
import ru.mousecray.mousecore.api.log.LogPrefixes;
import ru.mousecray.mousecore.api.utils.MouseCollections;
import ru.mousecray.mousecore.api.utils.MouseLogic;
import ru.mousecray.mousecore.core.config.build.struct.ConfigDisablerBuilder;
import ru.mousecray.mousecore.core.config.build.val.number.ConfigRandomQuantityBuilder;
import ru.mousecray.mousecore.core.config.par.ConfigParDisablerImpl;
import ru.mousecray.mousecore.core.config.par.group.ConfigParGroupQuantityConditionImpl;
import ru.mousecray.mousecore.core.config.utils.ConstraintImpl;
import ru.mousecray.mousecore.core.config.utils.PredefinedValueImpl;
import ru.mousecray.mousecore.core.config.val.ConfigValConditionImpl;
import ru.mousecray.mousecore.core.config.val.ConfigValRandomQuantityImpl;
import ru.mousecray.mousecore.core.config.val.base.list.ConfigValSimpleListImpl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigParameterGroupQuantityConditionBuilder<T extends CustomType<?>>
        extends AbstractConfigParameterBuilder<ConfigParameterGroupQuantityConditionBuilder<T>> {
    @Nonnull private final ConfigParameterGroupBuilder<T>             parBuilder;
    private                DisplayName                                randomName;
    private                Pair<DisplayName, ConfigValRandomQuantity> randomPar;
    private                Pair<DisplayName, ConfigValSimpleListImpl> listPar;
    private                DisplayName                                listName;
    private                ConfigParDisablerImpl                      disablePar;

    //For conditional
    private boolean                                                        hasCondition;
    private DisplayName                                                    conditionName;
    private String                                                         noneVar;
    private String                                                         anyVar;
    private ImmutableList<Pair<String, String>>                            additionalThreshold;
    private ImmutableList<String>                                          additionalUseList;
    private Constraint[]                                                   constraints;
    private PredefinedValue<ConditionType<ConfigValConditionImpl<T>, T>>[] configureValues;
    private ConditionType<ConfigValConditionImpl<T>, T>                    defaultValue;
    private ConditionType<ConfigValConditionImpl<T>, T>                    disabledValue;
    private boolean                                                        hasDisabledValue;
    private boolean                                                        enableThresholdConfig;

    private ConfigParameterGroupQuantityConditionBuilder(ConfigParameterGroupBuilder<T> parBuilder) {
        super(parBuilder.configBuilder, parBuilder.path, parBuilder.name);
        this.parBuilder = parBuilder;
    }

    public static <T extends CustomType<?>> ConfigParameterGroupQuantityConditionBuilder<T> create(ConfigParameterGroupBuilder<T> parBuilder) {
        return new ConfigParameterGroupQuantityConditionBuilder<>(Objects.requireNonNull(parBuilder));
    }

    public ConfigRandomQuantityBuilder<ConfigParameterGroupQuantityConditionBuilder<T>> createRandomPar(String displayName) {
        randomName = new DisplayName("internal$1", displayName);
        return ConfigRandomQuantityBuilder.create(this);
    }

    public ConfigParameterGroupConditionConditionParBuilder createCondition(String displayName) {
        return new ConfigParameterGroupConditionConditionParBuilder(Objects.requireNonNull(displayName));
    }

    public ConfigListBuilder0<T, ConfigParameterGroupQuantityConditionBuilder<T>> createList(String displayName) {
        listName = new DisplayName("internal$3", displayName);
        return ConfigListBuilder0.create(this, parBuilder.valType, parBuilder.typeClass);
    }

    public ConfigDisablerBuilder<ConfigParameterGroupQuantityConditionBuilder<T>> createDisabler(String displayName) {
        return ConfigDisablerBuilder.create(this, Objects.requireNonNull(displayName), false);
    }

    public ConfigDisablerBuilder<ConfigParameterGroupQuantityConditionBuilder<T>> createDisabler() {
        return ConfigDisablerBuilder.create(this, "Disabler", true);
    }

    @Override
    void addDisabler(ConfigParDisablerImpl disabler) {
        if (disablePar != null && configBuilder.logger != null) {
            configBuilder.logger.atWarn()
                    .withPrefix(LogPrefixes.CONFIG)
                    .withStyle(ConsoleColor.YELLOW_BG)
                    .log("Disabler '{0}' in configSection '{1}.{2}' was overwritten by Disabler '{3}'",
                            disablePar.getName().getInternalName(), path, name.getInternalName(), disabler.getName().getInternalName());
        }
        disablePar = disabler;
    }

    @SuppressWarnings("unchecked") @Override
    protected void setValue(ConfigVal value) {
        boolean logVal = false;
        if (value instanceof ConfigValSimpleListImpl) {
            if (listPar != null) logVal = true;
            listPar = Pair.of(listName, (ConfigValSimpleListImpl) value);
        } else if (value instanceof ConfigValRandomQuantityImpl) {
            if (randomPar != null) logVal = true;
            randomPar = Pair.of(randomName, (ConfigValRandomQuantity) value);
        }
        if (logVal && configBuilder.logger != null) {
            configBuilder.logger.atWarn()
                    .withPrefix(LogPrefixes.CONFIG)
                    .withStyle(ConsoleColor.YELLOW_BG)
                    .log("ConfigValue '{0}' in ConfigParameter '{1}' was overwritten by ConfigValue '{2}'",
                            listPar, getFullName(), value);
        }
    }

    public ConfigParameterGroupBuilder<T> buildConditionGroup() {
        if (randomPar == null) throw new IllegalStateException("ConfigParameterGroupCondition doesn't contain main parameter");
        if (!hasCondition) throw new IllegalStateException("ConfigParameterGroupCondition doesn't contain conditional parameter");

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
                                getFullName());
            }
        }
        parBuilder.setGroup(new ConfigParGroupQuantityConditionImpl<>(
                parBuilder.name, disablePar, randomPar, comment,
                Pair.of(conditionName,
                        new ConfigValConditionImpl<>(
                                parBuilder.valType, parBuilder.typeClass, null, enableThresholdConfig,
                                defaultValue, disabledValue,
                                listPar != null ? listPar.getRight().getListSuppler() : null,
                                anyVar, noneVar,
                                hasDictionary()
                                        ? getDictionary().getLocaleForLocale(ConfigLocaleType.LIST_VARIANT)
                                        : ConfigLocaleType.LIST_VARIANT.getDisplayName(),
                                additionalThreshold, listPar != null ? additionalUseList : null,
                                constraints, configureValues
                        )
                ), listPar
        ));
        return parBuilder;
    }

    public class ConfigParameterGroupConditionConditionParBuilder {
        private @Nullable ArrayList<PredefinedValue<ConditionType<ConfigValConditionImpl<T>, T>>> configureValues;
        private @Nullable ArrayList<Constraint>                                                   constraints;
        private           boolean                                                                 disableAdditionalThreshold;
        private           boolean                                                                 disableAdditionalUseList;

        @SuppressWarnings("DataFlowIssue")
        private ConfigParameterGroupConditionConditionParBuilder(String displayName) {
            conditionName = new DisplayName("internal$2", displayName);
            defaultValue = ConditionType.ANY(
                    parBuilder.hasDictionary()
                            ? parBuilder.getDictionary().getLocaleForLocale(ConfigLocaleType.ANY_VARIANT)
                            : ConfigLocaleType.ANY_VARIANT.getDisplayName()
            );
            disabledValue = ConditionType.NONE(
                    parBuilder.hasDictionary()
                            ? parBuilder.getDictionary().getLocaleForLocale(ConfigLocaleType.NONE_VARIANT)
                            : ConfigLocaleType.NONE_VARIANT.getDisplayName()
            );
        }

        public ConfigParameterGroupConditionConditionParBuilder setDefaultValue(ConditionType<ConfigValConditionImpl<T>, T> defaultValue) {
            ConfigParameterGroupQuantityConditionBuilder.this.defaultValue = Objects.requireNonNull(defaultValue);
            if (!hasDisabledValue) {
                disabledValue = ConditionType.create(
                        MouseLogic.invert(defaultValue.getPredicate()),
                        defaultValue.getDisplayName());
            }
            return this;
        }

        public ConfigParameterGroupConditionConditionParBuilder setDisabledValue(ConditionType<ConfigValConditionImpl<T>, T> disabledValue) {
            ConfigParameterGroupQuantityConditionBuilder.this.disabledValue = Objects.requireNonNull(disabledValue);
            hasDisabledValue = true;
            return this;
        }

        public ConfigParameterGroupConditionConditionParBuilder enableThresholdConfiguration() {
            enableThresholdConfig = true;
            return this;
        }

        public ConfigParameterGroupConditionConditionParBuilder disableAdditionalThreshold() {
            disableAdditionalThreshold = true;
            return this;
        }

        public ConfigParameterGroupConditionConditionParBuilder disableAdditionalUseList() {
            disableAdditionalUseList = true;
            return this;
        }

        public ConfigParameterGroupConditionConditionParBuilder addConfigureValue(String name, ConditionType<ConfigValConditionImpl<T>, T> value) {
            Objects.requireNonNull(name);
            if (configureValues == null) configureValues = new ArrayList<>();
            configureValues.add(new PredefinedValueImpl<>(name, value));
            return this;
        }

        public ConfigParameterGroupConditionConditionParBuilder addConstraint(Constraint value) {
            Objects.requireNonNull(value);
            if (constraints == null) constraints = new ArrayList<>();
            constraints.add(value);
            return this;
        }

        public ConfigParameterGroupQuantityConditionBuilder<T> buildCondition() {
            anyVar = hasDictionary() ? getDictionary().getLocaleForLocale(ConfigLocaleType.ANY_VARIANT)
                    : ConfigLocaleType.ANY_VARIANT.getDisplayName();
            noneVar = hasDictionary() ? getDictionary().getLocaleForLocale(ConfigLocaleType.NONE_VARIANT)
                    : ConfigLocaleType.NONE_VARIANT.getDisplayName();
            additionalThreshold = disableAdditionalThreshold ? null
                    : hasDictionary() ? getDictionary().getDefThresholdForType(ConfigValType.CONDITION) : null;
            additionalUseList = disableAdditionalUseList ? null
                    : hasDictionary() ? getDictionary().getDefUseListForType(ConfigValType.CONDITION) : null;
            ConfigParameterGroupQuantityConditionBuilder.this.constraints = MouseCollections.toArray(
                    constraints, ConstraintImpl.class
            );
            ConfigParameterGroupQuantityConditionBuilder.this.configureValues = MouseCollections.toArray(
                    configureValues, PredefinedValueImpl.class
            );
            hasCondition = true;
            return ConfigParameterGroupQuantityConditionBuilder.this;
        }
    }
}