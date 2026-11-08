/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.config.build;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.IValType;
import ru.mousecray.mousecore.api.config.specific.ConfigLocaleType;
import ru.mousecray.mousecore.api.config.specific.ConfigValType;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.values.ConfigConditionVal;
import ru.mousecray.mousecore.api.customtype.values.ConditionType;
import ru.mousecray.mousecore.api.error.UnsupportedValException;
import ru.mousecray.mousecore.api.utils.MouseCollections;
import ru.mousecray.mousecore.api.utils.MouseLogic;
import ru.mousecray.mousecore.api.utils.MouseStrings;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Objects;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigConditionBuilder<
        VAL extends Comparable<VAL>,
        BUILDER extends AbstractConfigParameterBuilder<BUILDER>
        > {
    private final     BUILDER                                                                 parBuilder;
    private           ConditionType<ConfigConditionVal<VAL>, VAL>                             defaultValue;
    private           ConditionType<ConfigConditionVal<VAL>, VAL>                             disabledValue;
    private final     IValType                                                                valType;
    private final     Class<VAL>                                                              valueClass;
    private           boolean                                                                 hasDisabledValue;
    private @Nullable ArrayList<PredefinedValue<ConditionType<ConfigConditionVal<VAL>, VAL>>> configureValues;
    private @Nullable ArrayList<Constraint>                                                   constraints;
    private           boolean                                                                 disableAdditionalThreshold;
    private           boolean                                                                 disableAdditionalUseList;
    private           boolean                                                                 enableThresholdConfig;
    private           String                                                                  pathToParList;

    private ConfigConditionBuilder(BUILDER parBuilder, IValType valType, Class<VAL> valueClass) {
        this.parBuilder = parBuilder;
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
        if (!valType.canBeApplicableTo(Objects.requireNonNull(valueClass))) {
            throw new UnsupportedValException("CustomType is not support " + valueClass.getName());
        }
        this.valType = valType;
        this.valueClass = valueClass;
    }

    static <
            VAL extends Comparable<VAL>,
            BUILDER extends AbstractConfigParameterBuilder<BUILDER>
            > ConfigConditionBuilder<VAL, BUILDER>
    create(BUILDER parBuilder, ICustomType valType, Class<VAL> valueClass) {
        return new ConfigConditionBuilder<>(Objects.requireNonNull(parBuilder), valType, valueClass);
    }

    public ConfigConditionBuilder<VAL, BUILDER> setDefaultValue(ConditionType<ConfigConditionVal<VAL>, VAL> defaultValue) {
        this.defaultValue = Objects.requireNonNull(defaultValue);
        if (!hasDisabledValue) {
            disabledValue = ConditionType.create(
                    MouseLogic.invert(defaultValue.getPredicate()),
                    defaultValue.getDisplayName());
        }
        return this;
    }

    public ConfigConditionBuilder<VAL, BUILDER> setDisabledValue(ConditionType<ConfigConditionVal<VAL>, VAL> disabledValue) {
        this.disabledValue = Objects.requireNonNull(disabledValue);
        hasDisabledValue = true;
        return this;
    }

    public ConfigConditionBuilder<VAL, BUILDER> enableThresholdConfiguration() {
        enableThresholdConfig = true;
        return this;
    }

    public ConfigConditionBuilder<VAL, BUILDER> disableAdditionalThreshold() {
        disableAdditionalThreshold = true;
        return this;
    }

    public ConfigConditionBuilder<VAL, BUILDER> disableAdditionalUseList() {
        disableAdditionalUseList = true;
        return this;
    }

    public ConfigConditionBuilder<VAL, BUILDER> addConfigureValue(String name, ConditionType<ConfigConditionVal<VAL>, VAL> value) {
        Objects.requireNonNull(name);
        if (configureValues == null) configureValues = new ArrayList<>();
        configureValues.add(new PredefinedValue<>(name, value));
        return this;
    }

    public ConfigConditionBuilder<VAL, BUILDER> addConstraint(Constraint value) {
        Objects.requireNonNull(value);
        if (constraints == null) constraints = new ArrayList<>();
        constraints.add(value);
        return this;
    }

    @SuppressWarnings("DataFlowIssue")
    public ConfigConditionBuilder<VAL, BUILDER> setListPar(String pathToParList) {
        pathToParList = MouseStrings.trimWith(Objects.requireNonNull(pathToParList), true, '\t');
        if (pathToParList.isEmpty()) throw new IllegalArgumentException("pathToParList cannot be empty");
        if (pathToParList.contains("\t")) throw new IllegalArgumentException("pathToParList cannot contains tabs");
        this.pathToParList = pathToParList;

        return this;
    }

    public BUILDER buildCondition() {
        parBuilder.setValue(new ConfigConditionVal<>(valType, valueClass, null,
                enableThresholdConfig, defaultValue, disabledValue,
                pathToParList,
                parBuilder.hasDictionary()
                        ? parBuilder.getDictionary().getLocaleForLocale(ConfigLocaleType.ANY_VARIANT)
                        : ConfigLocaleType.ANY_VARIANT.getDisplayName(),
                parBuilder.hasDictionary()
                        ? parBuilder.getDictionary().getLocaleForLocale(ConfigLocaleType.NONE_VARIANT)
                        : ConfigLocaleType.NONE_VARIANT.getDisplayName(),
                parBuilder.hasDictionary()
                        ? parBuilder.getDictionary().getLocaleForLocale(ConfigLocaleType.LIST_VARIANT)
                        : ConfigLocaleType.LIST_VARIANT.getDisplayName(),
                disableAdditionalThreshold ? null
                        : parBuilder.hasDictionary()
                        ? parBuilder.getDictionary().getDefThresholdForType(ConfigValType.CONDITION) : null,
                disableAdditionalUseList ? null
                        : parBuilder.hasDictionary()
                        ? parBuilder.getDictionary().getDefUseListForType(ConfigValType.CONDITION) : null,
                MouseCollections.toArray(constraints, Constraint.class),
                MouseCollections.toArray(configureValues, PredefinedValue.class)));
        return parBuilder;
    }
}