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
import ru.mousecray.mousecore.api.config.val.ConfigValIntegral;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.op.CustomArithmetic;
import ru.mousecray.mousecore.api.customtype.range.Range;
import ru.mousecray.mousecore.api.customtype.range.RangeContainer;
import ru.mousecray.mousecore.api.customtype.values.IntegralType;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.api.utils.MouseCollections;
import ru.mousecray.mousecore.core.config.utils.PredefinedValueImpl;
import ru.mousecray.mousecore.core.config.val.base.ConfigNumberValImpl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ConfigValIntegralImpl extends ConfigNumberValImpl<IntegralType> implements ConfigValIntegral {
    @SafeVarargs
    public ConfigValIntegralImpl(
            IntegralType defaultValue, IntegralType disabledValue,
            @Nullable RangeContainer<IntegralType> range,
            boolean enableThresholdConfig, boolean enableStandardConfig,
            @Nullable List<Pair<String, String>> additionalThreshold,
            @Nullable List<Triple<String, String, String>> additionalStandard,
            @Nullable Function<CustomArithmetic<IntegralType>, IntegralType> actionIfNotEqualConstraint,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<IntegralType>... configureValues
    ) {
        super(
                ConfigValType.INTEGRAL, defaultValue, disabledValue,
                range == null ? range = new RangeContainer<>(Range.of(IntegralType.MIN, IntegralType.MAX)) : range,
                null, actionIfNotEqualConstraint, constraints,
                genValues(
                        enableThresholdConfig, enableStandardConfig, defaultValue,
                        range.getMinValue(), range.getMaxValue(), additionalThreshold,
                        additionalStandard, configureValues
                )
        );
    }

    private static PredefinedValue<IntegralType>[] genValues(
            boolean tv, boolean sv, IntegralType def, IntegralType min, IntegralType max,
            @Nullable List<Pair<String, String>> additionalThreshold,
            @Nullable List<Triple<String, String, String>> additionalStandard,
            @Nullable PredefinedValue<IntegralType>... predefinedValues
    ) {
        predefinedValues = MouseCollections.map(
                val -> val.getValue() != null ? val : new PredefinedValueImpl<>(val.getDisplayName(), IntegralType.NULL),
                true, predefinedValues
        );

        ArrayList<PredefinedValue<IntegralType>> list = new ArrayList<>();
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
        IntegralType medium = IntegralType.calcMedium(min, max);
        IntegralType low    = IntegralType.calcLow(min, max);
        IntegralType high   = IntegralType.calcHigh(min, max);
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
    protected VariableValue<IntegralType> parseValue(@Nullable String value) {
        try {
            return VariableValue.create(CustomType.parse(IntegralType.class, value));
        } catch (ValueFormatException ignore) { return VariableValue.create(); }
    }

    @Override protected boolean saveRules() { return false; }
}