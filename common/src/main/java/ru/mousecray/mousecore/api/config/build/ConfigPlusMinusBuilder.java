/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.config.build;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.specific.ConfigValType;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.values.ConfigValPlusMinus;
import ru.mousecray.mousecore.api.customtype.values.PlusMinusType;
import ru.mousecray.mousecore.api.utils.MouseCollections;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Objects;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigPlusMinusBuilder<BUILDER extends AbstractConfigParameterBuilder<BUILDER>> {
    private final     BUILDER                                   parBuilder;
    private           PlusMinusType                             defaultValue  = PlusMinusType.TRUE;
    private           PlusMinusType                             disabledValue = PlusMinusType.FALSE;
    private           boolean                                   hasDisabledValue;
    private @Nullable ArrayList<PredefinedValue<PlusMinusType>> configureValues;
    private @Nullable ArrayList<Constraint<?>>                  constraints;
    private           boolean                                   disableAdditionalThreshold;

    private ConfigPlusMinusBuilder(BUILDER parBuilder) { this.parBuilder = parBuilder; }

    static <BUILDER extends AbstractConfigParameterBuilder<BUILDER>> ConfigPlusMinusBuilder<BUILDER> create(BUILDER parBuilder) {
        return new ConfigPlusMinusBuilder<>(Objects.requireNonNull(parBuilder));
    }

    public ConfigPlusMinusBuilder<BUILDER> setDefaultValue(PlusMinusType defaultValue) {
        this.defaultValue = Objects.requireNonNull(defaultValue);
        if (!hasDisabledValue) disabledValue = (PlusMinusType) defaultValue.getLogicPipeline().not();
        return this;
    }

    public ConfigPlusMinusBuilder<BUILDER> setDisabledValue(PlusMinusType disabledValue) {
        this.disabledValue = Objects.requireNonNull(disabledValue);
        hasDisabledValue = true;
        return this;
    }

    public ConfigPlusMinusBuilder<BUILDER> disableAdditionalThreshold() {
        disableAdditionalThreshold = true;
        return this;
    }

    public ConfigPlusMinusBuilder<BUILDER> addConfigureValue(String name, PlusMinusType value) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(value);
        if (configureValues == null) configureValues = new ArrayList<>();
        configureValues.add(new PredefinedValue<>(name, value));
        return this;
    }

    public ConfigPlusMinusBuilder<BUILDER> addConstraint(Constraint value) {
        Objects.requireNonNull(value);
        if (constraints == null) constraints = new ArrayList<>();
        constraints.add(value);
        return this;
    }

    public BUILDER buildPlusMinus() {
        parBuilder.setValue(new ConfigValPlusMinus(
                defaultValue, disabledValue,
                MouseCollections.toArray(constraints, Constraint.class),
                disableAdditionalThreshold ? null
                        : parBuilder.hasDictionary()
                        ? parBuilder.getDictionary().getDefThresholdForType(ConfigValType.LOGICAL) : null,
                MouseCollections.toArray(configureValues, PredefinedValue.class))
        );
        return parBuilder;
    }
}