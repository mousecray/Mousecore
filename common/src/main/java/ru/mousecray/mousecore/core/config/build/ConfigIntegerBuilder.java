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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigIntegerBuilder<BUILDER extends AbstractConfigParameterBuilder<BUILDER>> {
    private final     BUILDER                          parBuilder;
    private           long                             defaultValue;
    private final     List<Range<Long>>                range         = new ArrayList<>();
    private           long                             disabledValue = Long.MIN_VALUE;
    private           boolean                          hasDisabledValue;
    private @Nullable ArrayList<Constraint>            constraints;
    @Nullable private ArrayList<PredefinedValue<Long>> configureValues;
    private           boolean                          disableAdditionalThreshold;
    private           boolean                          disableAdditionalStandard;
    private           boolean                          enableThresholdConfiguration;
    private           boolean                          enableStandardConfiguration;

    private ConfigIntegerBuilder(BUILDER parBuilder) { this.parBuilder = parBuilder; }

    static <BUILDER extends AbstractConfigParameterBuilder<BUILDER>> ConfigIntegerBuilder<BUILDER> create(BUILDER parBuilder) {
        return new ConfigIntegerBuilder<>(Objects.requireNonNull(parBuilder));
    }

    public ConfigIntegerBuilder<BUILDER> setDefaultValue(long defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public ConfigIntegerBuilder<BUILDER> setDisabledValue(long disabledValue) {
        this.disabledValue = disabledValue;
        hasDisabledValue = true;
        return this;
    }

    public ConfigIntegerBuilder<BUILDER> addValidRange(long minValue, long maxValue) {
        range.add(Range.of(minValue, maxValue));
        if (!hasDisabledValue) disabledValue = minValue;
        return this;
    }

    public ConfigIntegerBuilder<BUILDER> enableThresholdConfiguration() {
        enableThresholdConfiguration = true;
        return this;
    }

    public ConfigIntegerBuilder<BUILDER> enableStandardConfiguration() {
        enableStandardConfiguration = true;
        return this;
    }

    public ConfigIntegerBuilder<BUILDER> disableAdditionalThreshold() {
        disableAdditionalThreshold = true;
        return this;
    }

    public ConfigIntegerBuilder<BUILDER> disableAdditionalStandard() {
        disableAdditionalStandard = true;
        return this;
    }

    public ConfigIntegerBuilder<BUILDER> addConfigureValue(@Nonnull String name, long value) {
        Objects.requireNonNull(name);
        if (configureValues == null) configureValues = new ArrayList<>();
        configureValues.add(new PredefinedValueImpl<>(name, value));
        return this;
    }

    public ConfigIntegerBuilder<BUILDER> addConstraint(Constraint value) {
        Objects.requireNonNull(value);
        if (constraints == null) constraints = new ArrayList<>();
        constraints.add(value);
        return this;
    }

    public BUILDER buildInteger() {
        parBuilder.setValue(new ConfigValInteger(
                defaultValue,
                range.isEmpty() ? null : new RangeContainer<>(range),
                disabledValue, enableThresholdConfiguration, enableStandardConfiguration,
                disableAdditionalThreshold ? null :
                        parBuilder.hasDictionary()
                                ? parBuilder.getDictionary().getDefThresholdForType(ConfigValType.INTEGER) : null,
                disableAdditionalStandard ? null :
                        parBuilder.hasDictionary()
                                ? parBuilder.getDictionary().getDefStandardForType(ConfigValType.INTEGER) : null,
                MouseCollections.toArray(constraints, ConstraintImpl.class),
                MouseCollections.toArray(configureValues, PredefinedValueImpl.class)));
        return parBuilder;
    }
}