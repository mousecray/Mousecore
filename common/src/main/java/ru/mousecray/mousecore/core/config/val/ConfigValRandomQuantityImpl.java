/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.core.config.val;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import ru.mousecray.mousecore.api.VariableValue;
import ru.mousecray.mousecore.api.config.utils.ConfigValType;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.ConfigValRandomQuantity;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.op.CustomArithmetic;
import ru.mousecray.mousecore.api.customtype.range.Range;
import ru.mousecray.mousecore.api.customtype.range.RangeContainer;
import ru.mousecray.mousecore.api.customtype.values.RandomQuantityType;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.api.utils.MouseCollections;
import ru.mousecray.mousecore.core.config.utils.PredefinedValueImpl;
import ru.mousecray.mousecore.core.config.val.base.ConfigNumberValImpl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ConfigValRandomQuantityImpl extends ConfigNumberValImpl<RandomQuantityType> implements ConfigValRandomQuantity {
    @SafeVarargs
    public ConfigValRandomQuantityImpl(
            RandomQuantityType defaultValue, RandomQuantityType disabledValue,
            @Nullable RangeContainer<RandomQuantityType> range,
            boolean enableThresholdConfig, boolean enableStandardConfig,
            @Nullable List<Pair<String, String>> additionalThreshold,
            @Nullable List<Triple<String, String, String>> additionalStandard,
            @Nullable Function<CustomArithmetic<RandomQuantityType>, RandomQuantityType> actionIfNotEqualConstraint,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<RandomQuantityType>... configureValues
    ) {
        super(
                ConfigValType.RANDOM_QUANTITY, defaultValue, disabledValue,
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
                val -> val.getValue() != null ? val : new PredefinedValueImpl<>(val.getDisplayName(), RandomQuantityType.NULL),
                true, predefinedValues
        );

        ArrayList<PredefinedValue<RandomQuantityType>> list = new ArrayList<>();
        if (tv) {
            list.add(new PredefinedValueImpl<>("+", def));
            list.add(new PredefinedValueImpl<>("-", min));

        }
        if (additionalThreshold != null) {
            for (Pair<String, String> pair : additionalThreshold) {
                list.add(new PredefinedValueImpl<>(pair.getLeft(), def));
                list.add(new PredefinedValueImpl<>(pair.getRight(), min));
            }
        }
        RandomQuantityType medium = RandomQuantityType.calcMedium(min, max);
        RandomQuantityType low    = RandomQuantityType.calcLow(min, max);
        RandomQuantityType high   = RandomQuantityType.calcHigh(min, max);
        if (sv) {
            list.add(new PredefinedValueImpl<>("*", low));
            list.add(new PredefinedValueImpl<>("**", medium));
            list.add(new PredefinedValueImpl<>("***", high));

        }
        if (additionalStandard != null) {
            for (Triple<String, String, String> pair : additionalStandard) {
                list.add(new PredefinedValueImpl<>(pair.getLeft(), low));
                list.add(new PredefinedValueImpl<>(pair.getMiddle(), medium));
                list.add(new PredefinedValueImpl<>(pair.getRight(), high));
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