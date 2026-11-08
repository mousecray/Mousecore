/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.config.values;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import ru.mousecray.mousecore.api.VariableValue;
import ru.mousecray.mousecore.api.config.specific.ConfigValType;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.values.base.ConfigNumberVal;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.range.Range;
import ru.mousecray.mousecore.api.customtype.range.RangeContainer;
import ru.mousecray.mousecore.api.customtype.values.RandomQuantityType;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.api.utils.MouseCollections;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ConfigValRandomQuantity extends ConfigNumberVal<RandomQuantityType> {
    @SafeVarargs
    public ConfigValRandomQuantity(
            RandomQuantityType defaultValue, RandomQuantityType disabledValue,
            @Nullable RangeContainer<RandomQuantityType> range,
            boolean enableThresholdConfig, boolean enableStandardConfig,
            @Nullable List<Pair<String, String>> additionalThreshold,
            @Nullable List<Triple<String, String, String>> additionalStandard,
            @Nullable Function<CustomArithmetic<RandomQuantityType>, RandomQuantityType> actionIfNotEqualConstraint,
            @Nullable Constraint<?>[] constraints,
            @Nullable PredefinedValue<RandomQuantityType>... configureValues
    ) {
        super(
                ConfigValType.PERCENT, defaultValue, disabledValue,
                range == null
                        ? range = new RangeContainer<>(Range.of(RandomQuantityType.MIN, RandomQuantityType.MAX))
                        : range,
                "0-0:0.0%", actionIfNotEqualConstraint, constraints,
                genValues(
                        enableThresholdConfig, enableStandardConfig, defaultValue,
                        range.getMinValue(), range.getMaxValue(), additionalThreshold,
                        additionalStandard, configureValues
                )
        );
    }

    private static PredefinedValue<RandomQuantityType>[] genValues(
            boolean tv, boolean sv, RandomQuantityType def, RandomQuantityType min, RandomQuantityType max,
            @Nullable List<Pair<String, String>> additionalThreshold,
            @Nullable List<Triple<String, String, String>> additionalStandard,
            @Nullable PredefinedValue<RandomQuantityType>... predefinedValues
    ) {
        predefinedValues = MouseCollections.map(
                val -> val.getValue() != null ? val : new PredefinedValue<>(val.getDisplayName(), RandomQuantityType.NULL),
                true, predefinedValues
        );

        ArrayList<PredefinedValue<RandomQuantityType>> list = new ArrayList<>();
        if (tv) {
            list.add(new PredefinedValue<>("+", def));
            list.add(new PredefinedValue<>("-", min));

        }
        if (additionalThreshold != null) {
            for (Pair<String, String> pair : additionalThreshold) {
                list.add(new PredefinedValue<>(pair.getLeft(), def));
                list.add(new PredefinedValue<>(pair.getRight(), min));
            }
        }
        RandomQuantityType medium = RandomQuantityType.calcMedium(min, max);
        RandomQuantityType low    = RandomQuantityType.calcLow(min, max);
        RandomQuantityType high   = RandomQuantityType.calcHigh(min, max);
        if (sv) {
            list.add(new PredefinedValue<>("*", low));
            list.add(new PredefinedValue<>("**", medium));
            list.add(new PredefinedValue<>("***", high));

        }
        if (additionalStandard != null) {
            for (Triple<String, String, String> pair : additionalStandard) {
                list.add(new PredefinedValue<>(pair.getLeft(), low));
                list.add(new PredefinedValue<>(pair.getRight(), medium));
                list.add(new PredefinedValue<>(pair.getRight(), high));
            }
        }
        return list.isEmpty() ? predefinedValues
                : MouseCollections.addAll(PredefinedValue.class, predefinedValues, list);
    }

    @Nonnull @Override
    protected VariableValue<RandomQuantityType> parseValue(@Nullable String value) {
        try {
            return VariableValue.create(CustomType.parse(RandomQuantityType.class, value));
        } catch (ValueFormatException ignore) { return VariableValue.create(); }
    }
}