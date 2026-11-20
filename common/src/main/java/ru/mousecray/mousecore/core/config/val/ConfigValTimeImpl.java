/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.core.config.val;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import ru.mousecray.mousecore.api.VariableValue;
import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.ConfigValTime;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.op.CustomArithmetic;
import ru.mousecray.mousecore.api.customtype.range.Range;
import ru.mousecray.mousecore.api.customtype.range.RangeContainer;
import ru.mousecray.mousecore.api.customtype.values.number.TimeType;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.api.utils.MouseCollections;
import ru.mousecray.mousecore.core.config.utils.PredefinedValueImpl;
import ru.mousecray.mousecore.core.config.val.base.ConfigValNumberImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@FieldsAreNonnullByDefault
@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public class ConfigValTimeImpl extends ConfigValNumberImpl<TimeType> implements ConfigValTime {
    @SafeVarargs
    public ConfigValTimeImpl(
            TimeType defaultValue, TimeType disabledValue,
            @Nullable RangeContainer<TimeType> range,
            boolean enableThresholdConfig, boolean enableStandardConfig,
            @Nullable List<Pair<String, String>> additionalThreshold,
            @Nullable List<Triple<String, String, String>> additionalStandard,
            @Nullable Function<CustomArithmetic<TimeType>, TimeType> actionIfNotEqualConstraint,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<TimeType>... configureValues
    ) {
        super(
                defaultValue, disabledValue,
                range == null ? range = new RangeContainer<>(Range.of(TimeType.MIN, TimeType.MAX)) : range,
                "0t/s/m/h/d", actionIfNotEqualConstraint, constraints,
                genValues(
                        enableThresholdConfig, enableStandardConfig, defaultValue,
                        range.getMinValue(), range.getMaxValue(), additionalThreshold,
                        additionalStandard, configureValues
                )
        );
    }

    @Nullable
    private static PredefinedValue<TimeType>[] genValues(
            boolean tv, boolean sv, TimeType def, TimeType min, TimeType max,
            @Nullable List<Pair<String, String>> additionalThreshold,
            @Nullable List<Triple<String, String, String>> additionalStandard,
            @Nullable PredefinedValue<TimeType>... predefinedValues
    ) {
        predefinedValues = MouseCollections.map(
                val -> val.getValue() != null ? val : new PredefinedValueImpl<>(val.getDisplayName(), TimeType.ZERO),
                true, predefinedValues
        );

        ArrayList<PredefinedValue<TimeType>> list = new ArrayList<>();
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

        long     minT   = min.asTicks();
        long     maxT   = max.asTicks();
        TimeType medium = TimeType.create((long) (minT * 0.5D + maxT * 0.5D));
        TimeType low    = TimeType.create((long) (minT * 0.75D + maxT * 0.25D));
        TimeType high   = TimeType.create((long) (minT * 0.25D + maxT * 0.75D));
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

    @Override
    protected VariableValue<TimeType> parseValue(@Nullable String value) {
        try {
            return VariableValue.create(CustomType.parse(TimeType.class, value));
        } catch (ValueFormatException ignore) { return VariableValue.create(); }
    }

    @Override public long getTicks()                                { return getValue().asTicks(); }
    @Override public double getSeconds()                            { return getValue().asSeconds(); }
    @Override public double getMinutes()                            { return getValue().asMinutes(); }
    @Override public double getHours()                              { return getValue().asHours(); }
    @Override public double getDays()                               { return getValue().asDays(); }

    @Override public void setTicks(long ticks)                      { setValue(TimeType.create(ticks)); }
    @Override public void setTime(double value, TimeType.Unit unit) { setValue(TimeType.create(value, unit)); }
    @Override public void setTime(long value, TimeType.Unit unit)   { setValue(TimeType.create(value, unit)); }
}