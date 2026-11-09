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
import ru.mousecray.mousecore.core.config.val.ConfigValRandomQuantityImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigRandomQuantityBuilder<BUILDER extends AbstractConfigParameterBuilder<BUILDER>> {
    private final BUILDER parBuilder;

    private           RandomQuantity0                             defaultValue;
    private final     List<Range<RandomQuantity0>>                range         = new ArrayList<>();
    private           RandomQuantity0                             disabledValue = RandomQuantity0.MIN_VALUE;
    private           boolean                                     hasDisabledValue;
    private @Nullable ArrayList<Constraint>                       constraints;
    @Nullable private ArrayList<PredefinedValue<RandomQuantity0>> configureValues;
    private           boolean                                     disableAdditionalThreshold;
    private           boolean                                     disableAdditionalStandard;
    private           boolean                                     enableThresholdConfiguration;
    private           boolean                                     enableStandardConfiguration;

    private ConfigRandomQuantityBuilder(BUILDER parBuilder) { this.parBuilder = parBuilder; }

    static <
            BUILDER extends AbstractConfigParameterBuilder<BUILDER>
            > ConfigRandomQuantityBuilder<BUILDER> create(BUILDER parBuilder) {
        return new ConfigRandomQuantityBuilder<>(Objects.requireNonNull(parBuilder));
    }

    public ConfigRandomQuantityBuilder<BUILDER> setDefaultValue(RandomQuantity0 defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public ConfigRandomQuantityBuilder<BUILDER> setDisabledValue(RandomQuantity0 disabledValue) {
        this.disabledValue = disabledValue;
        hasDisabledValue = true;
        return this;
    }

    public ConfigRandomQuantityBuilder<BUILDER> addValidRange(RandomQuantity0 minValue, RandomQuantity0 maxValue) {
        range.add(Range.of(Objects.requireNonNull(minValue), Objects.requireNonNull(maxValue)));
        if (!hasDisabledValue) disabledValue = minValue;
        return this;
    }

    public ConfigRandomQuantityBuilder<BUILDER> enableThresholdConfiguration() {
        enableThresholdConfiguration = true;
        return this;
    }

    public ConfigRandomQuantityBuilder<BUILDER> enableStandardConfiguration() {
        enableStandardConfiguration = true;
        return this;
    }

    public ConfigRandomQuantityBuilder<BUILDER> disableAdditionalThreshold() {
        disableAdditionalThreshold = true;
        return this;
    }

    public ConfigRandomQuantityBuilder<BUILDER> disableAdditionalStandard() {
        disableAdditionalStandard = true;
        return this;
    }

    public ConfigRandomQuantityBuilder<BUILDER> addConfigureValue(String name, RandomQuantity0 value) {
        Objects.requireNonNull(name);
        if (configureValues == null) configureValues = new ArrayList<>();
        configureValues.add(new PredefinedValueImpl<>(name, value));
        return this;
    }

    public ConfigRandomQuantityBuilder<BUILDER> addConstraint(Constraint value) {
        Objects.requireNonNull(value);
        if (constraints == null) constraints = new ArrayList<>();
        constraints.add(value);
        return this;
    }

    public BUILDER buildRandomQuantity() {
        parBuilder.setValue(new ConfigValRandomQuantityImpl(
                defaultValue,
                range.isEmpty()
                        ? new RangeContainer<>(
                        Range.of(
                                RandomQuantity0.MIN_VALUE,
                                RandomQuantity0.create(Percent0.MAX_VALUE, 1000, 1000)
                        ))
                        : new RangeContainer<>(range),
                disabledValue, enableThresholdConfiguration, enableStandardConfiguration,
                disableAdditionalThreshold ? null :
                        parBuilder.hasDictionary()
                                ? parBuilder.getDictionary().getDefThresholdForType(ConfigValType.RANDOM_QUANTITY) : null,
                disableAdditionalStandard ? null :
                        parBuilder.hasDictionary()
                                ? parBuilder.getDictionary().getDefStandardForType(ConfigValType.RANDOM_QUANTITY) : null,
                MouseCollections.toArray(constraints, ConstraintImpl.class),
                MouseCollections.toArray(configureValues, PredefinedValueImpl.class)));
        return parBuilder;
    }
}