/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.core.config.val;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import ru.mousecray.mousecore.api.VariableValue;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.ConfigValDecimal;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.op.CustomArithmetic;
import ru.mousecray.mousecore.api.customtype.range.Range;
import ru.mousecray.mousecore.api.customtype.range.RangeContainer;
import ru.mousecray.mousecore.api.customtype.values.DecimalType;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.api.utils.MouseCollections;
import ru.mousecray.mousecore.core.config.utils.PredefinedValueImpl;
import ru.mousecray.mousecore.core.config.val.base.ConfigValNumberImpl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ConfigValDecimalImpl extends ConfigValNumberImpl<DecimalType> implements ConfigValDecimal {
    @SafeVarargs
    public ConfigValDecimalImpl(
            DecimalType defaultValue, DecimalType disabledValue,
            @Nullable RangeContainer<DecimalType> range,
            boolean enableThresholdConfig, boolean enableStandardConfig,
            @Nullable List<Pair<String, String>> additionalThreshold,
            @Nullable List<Triple<String, String, String>> additionalStandard,
            @Nullable Function<CustomArithmetic<DecimalType>, DecimalType> actionIfNotEqualConstraint,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<DecimalType>... configureValues
    ) {
        super(
                defaultValue, disabledValue,
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
                val -> val.getValue() != null ? val : new PredefinedValueImpl<>(val.getDisplayName(), DecimalType.NULL),
                true, predefinedValues
        );

        ArrayList<PredefinedValue<DecimalType>> list = new ArrayList<>();
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
        DecimalType medium = DecimalType.calcMedium(min, max);
        DecimalType low    = DecimalType.calcLow(min, max);
        DecimalType high   = DecimalType.calcHigh(min, max);
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
    protected VariableValue<DecimalType> parseValue(@Nullable String value) {
        try {
            return VariableValue.create(CustomType.parse(DecimalType.class, value));
        } catch (ValueFormatException ignore) { return VariableValue.create(); }
    }

    @Override protected boolean saveRules() { return false; }
}