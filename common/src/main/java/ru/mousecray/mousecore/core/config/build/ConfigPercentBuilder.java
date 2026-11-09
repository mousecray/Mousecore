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
import ru.mousecray.mousecore.core.config.val.ConfigValPercentImpl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigPercentBuilder<BUILDER extends AbstractConfigParameterBuilder<BUILDER>> {
    private final     BUILDER                         parBuilder;
    private           Percent0                        defaultValue;
    private final     List<Range<Percent0>>           range         = new ArrayList<>();
    private           Percent0                        disabledValue = Percent0.MIN_VALUE;
    private           boolean                         hasDisabledValue;
    private @Nullable ArrayList<Constraint>           constraints;
    @Nullable private List<PredefinedValue<Percent0>> configureValues;
    private           boolean                         disableAdditionalThreshold;
    private           boolean                         disableAdditionalStandard;
    private           boolean                         enableThresholdConfiguration;
    private           boolean                         enableStandardConfiguration;

    private ConfigPercentBuilder(BUILDER parBuilder) { this.parBuilder = parBuilder; }

    static <BUILDER extends AbstractConfigParameterBuilder<BUILDER>> ConfigPercentBuilder<BUILDER> create(BUILDER parBuilder) {
        return new ConfigPercentBuilder<>(Objects.requireNonNull(parBuilder));
    }

    public ConfigPercentBuilder<BUILDER> setDefaultValue(Percent0 defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public ConfigPercentBuilder<BUILDER> setDisabledValue(Percent0 disabledValue) {
        this.disabledValue = disabledValue;
        hasDisabledValue = true;
        return this;
    }

    public ConfigPercentBuilder<BUILDER> addValidRange(Percent0 minValue, Percent0 maxValue) {
        range.add(Range.of(Objects.requireNonNull(minValue), Objects.requireNonNull(maxValue)));
        if (!hasDisabledValue) disabledValue = minValue;
        return this;
    }

    public ConfigPercentBuilder<BUILDER> enableThresholdConfiguration() {
        enableThresholdConfiguration = true;
        return this;
    }

    public ConfigPercentBuilder<BUILDER> enableStandardConfiguration() {
        enableStandardConfiguration = true;
        return this;
    }

    public ConfigPercentBuilder<BUILDER> disableAdditionalThreshold() {
        disableAdditionalThreshold = true;
        return this;
    }

    public ConfigPercentBuilder<BUILDER> disableAdditionalStandard() {
        disableAdditionalStandard = true;
        return this;
    }

    public ConfigPercentBuilder<BUILDER> addConfigureValue(@Nonnull String name, Percent0 value) {
        Objects.requireNonNull(name);
        if (configureValues == null) configureValues = new ArrayList<>();
        configureValues.add(new PredefinedValueImpl<>(name, value));
        return this;
    }

    public ConfigPercentBuilder<BUILDER> addConstraint(Constraint value) {
        Objects.requireNonNull(value);
        if (constraints == null) constraints = new ArrayList<>();
        constraints.add(value);
        return this;
    }

    public BUILDER buildPercent() {
        parBuilder.setValue(new ConfigValPercentImpl(
                defaultValue,
                range.isEmpty() ? null : new RangeContainer<>(range),
                disabledValue, enableThresholdConfiguration, enableStandardConfiguration,
                disableAdditionalThreshold ? null :
                        parBuilder.hasDictionary()
                                ? parBuilder.getDictionary().getDefThresholdForType(ConfigValType.PERCENT) : null,
                disableAdditionalStandard ? null :
                        parBuilder.hasDictionary()
                                ? parBuilder.getDictionary().getDefStandardForType(ConfigValType.PERCENT) : null,
                MouseCollections.toArray(constraints, ConstraintImpl.class),
                MouseCollections.toArray(configureValues, PredefinedValueImpl.class)));
        return parBuilder;
    }
}