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
import ru.mousecray.mousecore.api.customtype.op.CustomArithmetic;
import ru.mousecray.mousecore.api.customtype.range.Range;
import ru.mousecray.mousecore.api.customtype.range.RangeContainer;
import ru.mousecray.mousecore.api.customtype.values.DecimalType;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.api.utils.MouseCollections;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ConfigValDecimal extends ConfigNumberVal<DecimalType> {
    @SafeVarargs
    public ConfigValDecimal(
            DecimalType defaultValue, DecimalType disabledValue,
            @Nullable RangeContainer<DecimalType> range,
            boolean enableThresholdConfig, boolean enableStandardConfig,
            @Nullable List<Pair<String, String>> additionalThreshold,
            @Nullable List<Triple<String, String, String>> additionalStandard,
            @Nullable Function<CustomArithmetic<DecimalType>, DecimalType> actionIfNotEqualConstraint,
            @Nullable Constraint<?>[] constraints,
            @Nullable PredefinedValue<DecimalType>... configureValues
    ) {
        super(
                ConfigValType.DECIMAL, defaultValue, disabledValue,
                range == null ? range = new RangeContainer<>(Range.of(DecimalType.MIN, DecimalType.MAX)) : range,
                null, actionIfNotEqualConstraint, constraints,
                genValues(
                        enableThresholdConfig, enableStandardConfig, defaultValue,
                        range.getMinValue(), range.getMaxValue(), additionalThreshold,
                        additionalStandard, configureValues
                )
        );
    }

    private static PredefinedValue<DecimalType>[] genValues(
            boolean tv, boolean sv, DecimalType def, DecimalType min, DecimalType max,
            @Nullable List<Pair<String, String>> additionalThreshold,
            @Nullable List<Triple<String, String, String>> additionalStandard,
            @Nullable PredefinedValue<DecimalType>... predefinedValues
    ) {
        predefinedValues = MouseCollections.map(
                val -> val.getValue() != null ? val : new PredefinedValue<>(val.getDisplayName(), DecimalType.NULL),
                true, predefinedValues
        );

        ArrayList<PredefinedValue<DecimalType>> list = new ArrayList<>();
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
        DecimalType medium = DecimalType.calcMedium(min, max);
        DecimalType low    = DecimalType.calcLow(min, max);
        DecimalType high   = DecimalType.calcHigh(min, max);
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
    protected VariableValue<DecimalType> parseValue(@Nullable String value) {
        try {
            return VariableValue.create(CustomType.parse(DecimalType.class, value));
        } catch (ValueFormatException ignore) { return VariableValue.create(); }
    }

    @Override protected boolean saveRules() { return false; }
}