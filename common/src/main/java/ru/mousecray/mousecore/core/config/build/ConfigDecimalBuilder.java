/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.build;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.ConfigValType;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.customtype.range.Range;
import ru.mousecray.mousecore.api.customtype.range.RangeContainer;
import ru.mousecray.mousecore.api.utils.MouseCollections;
import ru.mousecray.mousecore.core.config.utils.ConstraintImpl;
import ru.mousecray.mousecore.core.config.utils.PredefinedValueImpl;
import ru.mousecray.mousecore.core.config.val.ConfigValDecimalImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigDecimalBuilder<BUILDER extends AbstractConfigParameterBuilder<BUILDER>> {
    private final     BUILDER                            parBuilder;
    private           double                             defaultValue;
    private final     List<Range<Double>>                range         = new ArrayList<>();
    private           double                             disabledValue = Double.MIN_VALUE;
    private           boolean                            hasDisabledValue;
    private @Nullable ArrayList<Constraint>              constraints;
    @Nullable private ArrayList<PredefinedValue<Double>> configureValues;
    private           boolean                            disableAdditionalThreshold;
    private           boolean                            disableAdditionalStandard;
    private           boolean                            enableThresholdConfiguration;
    private           boolean                            enableStandardConfiguration;

    private ConfigDecimalBuilder(BUILDER parBuilder) { this.parBuilder = parBuilder; }

    static <BUILDER extends AbstractConfigParameterBuilder<BUILDER>> ConfigDecimalBuilder<BUILDER> create(BUILDER parBuilder) {
        return new ConfigDecimalBuilder<>(Objects.requireNonNull(parBuilder));
    }

    public ConfigDecimalBuilder<BUILDER> setDefaultValue(double defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public ConfigDecimalBuilder<BUILDER> setDisabledValue(double disabledValue) {
        this.disabledValue = disabledValue;
        hasDisabledValue = true;
        return this;
    }

    public ConfigDecimalBuilder<BUILDER> addValidRange(double minValue, double maxValue) {
        range.add(Range.of(minValue, maxValue));
        if (!hasDisabledValue) disabledValue = minValue;
        return this;
    }

    public ConfigDecimalBuilder<BUILDER> enableThresholdConfiguration() {
        enableThresholdConfiguration = true;
        return this;
    }

    public ConfigDecimalBuilder<BUILDER> enableStandardConfiguration() {
        enableStandardConfiguration = true;
        return this;
    }

    public ConfigDecimalBuilder<BUILDER> disableAdditionalThreshold() {
        disableAdditionalThreshold = true;
        return this;
    }

    public ConfigDecimalBuilder<BUILDER> disableAdditionalStandard() {
        disableAdditionalStandard = true;
        return this;
    }

    public ConfigDecimalBuilder<BUILDER> addConfigureValue(String name, double value) {
        Objects.requireNonNull(name);
        if (configureValues == null) configureValues = new ArrayList<>();
        configureValues.add(new PredefinedValueImpl<>(name, value));
        return this;
    }

    public ConfigDecimalBuilder<BUILDER> addConstraint(Constraint value) {
        Objects.requireNonNull(value);
        if (constraints == null) constraints = new ArrayList<>();
        constraints.add(value);
        return this;
    }

    public BUILDER buildDecimal() {
        parBuilder.setValue(new ConfigValDecimalImpl(
                defaultValue,
                range.isEmpty() ? null : new RangeContainer<>(range),
                disabledValue, enableThresholdConfiguration, enableStandardConfiguration,
                disableAdditionalThreshold ? null
                        : parBuilder.hasDictionary()
                        ? parBuilder.getDictionary().getDefThresholdForType(ConfigValType.DECIMAL) : null,
                disableAdditionalStandard ? null
                        : parBuilder.hasDictionary()
                        ? parBuilder.getDictionary().getDefStandardForType(ConfigValType.DECIMAL) : null,
                MouseCollections.toArray(constraints, ConstraintImpl.class),
                MouseCollections.toArray(configureValues, PredefinedValueImpl.class)));
        return parBuilder;
    }
}