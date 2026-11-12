/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.build.val;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.ConfigLocaleType;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.ConfigValCondition;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.ListType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.customtype.values.ConditionType;
import ru.mousecray.mousecore.api.utils.MouseCollections;
import ru.mousecray.mousecore.api.utils.MouseLogic;
import ru.mousecray.mousecore.api.utils.MouseStrings;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.utils.PredefinedValueImpl;
import ru.mousecray.mousecore.core.config.val.ConfigValConditionImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigConditionBuilder<LIST_COMPONENT_VAL extends CustomType<?>, BUILDER extends AbstractConfigChildBuilder> {
    private final     BUILDER                                                                                        parBuilder;
    private           ConditionType<ListType<?, LIST_COMPONENT_VAL>, LIST_COMPONENT_VAL>                             defaultValue;
    private           ConditionType<ListType<?, LIST_COMPONENT_VAL>, LIST_COMPONENT_VAL>                             disabledValue;
    private           boolean                                                                                        hasDisabledValue;
    @Nullable private ArrayList<PredefinedValue<ConditionType<ListType<?, LIST_COMPONENT_VAL>, LIST_COMPONENT_VAL>>> configureValues;
    @Nullable private ArrayList<Constraint<? extends CustomType<?>>>                                                 constraints;

    private final IValType listComponentType;
    private       String   pathToParList;

    private boolean disableDictionaryThreshold   = false;
    private boolean disableDictionaryUseList     = false;
    private boolean enableThresholdConfiguration = false;

    private final Consumer<ConfigValCondition<LIST_COMPONENT_VAL>> onComplete;

    private ConfigConditionBuilder(
            BUILDER parBuilder, IValType listComponentType,
            Consumer<ConfigValCondition<LIST_COMPONENT_VAL>> onComplete
    ) {
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
        this.listComponentType = listComponentType;
        this.onComplete = onComplete;
    }

    public static <LIST_COMPONENT_VAL extends CustomType<?>, BUILDER extends AbstractConfigChildBuilder>
    ConfigConditionBuilder<LIST_COMPONENT_VAL, BUILDER>
    create(
            BUILDER parBuilder, IValType listComponentType,
            Consumer<ConfigValCondition<LIST_COMPONENT_VAL>> onComplete
    ) {
        return new ConfigConditionBuilder<>(Objects.requireNonNull(parBuilder), listComponentType, onComplete);
    }

    public ConfigConditionBuilder<LIST_COMPONENT_VAL, BUILDER> setDefaultValue(
            ConditionType<ListType<?, LIST_COMPONENT_VAL>, LIST_COMPONENT_VAL> defaultValue
    ) {
        this.defaultValue = Objects.requireNonNull(defaultValue);
        if (!hasDisabledValue) disabledValue = ConditionType.create(MouseLogic.invert(defaultValue.getPredicate()), defaultValue.getDisplayName());
        return this;
    }

    public ConfigConditionBuilder<LIST_COMPONENT_VAL, BUILDER> setDisabledValue(
            ConditionType<ListType<?, LIST_COMPONENT_VAL>, LIST_COMPONENT_VAL> disabledValue
    ) {
        this.disabledValue = Objects.requireNonNull(disabledValue);
        hasDisabledValue = true;
        return this;
    }

    public ConfigConditionBuilder<LIST_COMPONENT_VAL, BUILDER> enableThresholdConfiguration() {
        enableThresholdConfiguration = true;
        return this;
    }

    public ConfigConditionBuilder<LIST_COMPONENT_VAL, BUILDER> disableDictionaryThreshold() {
        disableDictionaryThreshold = true;
        return this;
    }

    public ConfigConditionBuilder<LIST_COMPONENT_VAL, BUILDER> disableDictionaryUseList() {
        disableDictionaryUseList = true;
        return this;
    }

    public ConfigConditionBuilder<LIST_COMPONENT_VAL, BUILDER> addConfigureValue(
            String name, ConditionType<ListType<?, LIST_COMPONENT_VAL>, LIST_COMPONENT_VAL> value
    ) {
        Objects.requireNonNull(name);
        if (configureValues == null) configureValues = new ArrayList<>();
        configureValues.add(new PredefinedValueImpl<>(name, value));
        return this;
    }

    public ConfigConditionBuilder<LIST_COMPONENT_VAL, BUILDER> addConstraint(Constraint value) {
        Objects.requireNonNull(value);
        if (constraints == null) constraints = new ArrayList<>();
        constraints.add(value);
        return this;
    }

    @SuppressWarnings("DataFlowIssue")
    public ConfigConditionBuilder<LIST_COMPONENT_VAL, BUILDER> setListPar(String pathToParList) {
        pathToParList = MouseStrings.trimWith(Objects.requireNonNull(pathToParList), true, '\t');
        if (pathToParList.isEmpty()) throw new IllegalArgumentException("pathToParList cannot be empty");
        if (pathToParList.contains("\t")) throw new IllegalArgumentException("pathToParList cannot contains tabs");
        this.pathToParList = pathToParList;

        return this;
    }

    public BUILDER buildCondition() {
        onComplete.accept(
                new ConfigValConditionImpl<>(
                        listComponentType, null,
                        enableThresholdConfiguration, defaultValue, disabledValue,
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
                        disableDictionaryThreshold ? null
                                : parBuilder.hasDictionary()
                                ? parBuilder.getDictionary().getDefThresholdForType(ValType.SIMPLE_CONDITION) : null,
                        disableDictionaryUseList ? null
                                : parBuilder.hasDictionary()
                                ? parBuilder.getDictionary().getDefUseListForType(ValType.SIMPLE_CONDITION) : null,
                        MouseCollections.toArray(constraints, Constraint.class),
                        MouseCollections.toArray(configureValues, PredefinedValue.class)
                )
        );
        return parBuilder;
    }
}