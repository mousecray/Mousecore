/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.core.config.val;

import org.apache.commons.lang3.tuple.Pair;
import ru.mousecray.mousecore.api.VariableValue;
import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.ConfigValRange;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.NumberType;
import ru.mousecray.mousecore.api.customtype.op.CustomArithmetic;
import ru.mousecray.mousecore.api.customtype.range.RangeContainer;
import ru.mousecray.mousecore.api.customtype.values.number.DecimalType;
import ru.mousecray.mousecore.api.customtype.values.number.RangeType;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.api.utils.MouseCollections;
import ru.mousecray.mousecore.api.utils.QuantityDistribution;
import ru.mousecray.mousecore.core.config.utils.PredefinedValueImpl;
import ru.mousecray.mousecore.core.config.val.base.ConfigValNumberImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

@FieldsAreNonnullByDefault
@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public class ConfigValRangeImpl extends ConfigValNumberImpl<RangeType> implements ConfigValRange {
    private final RangeContainer<DecimalType> boundaryRange;

    @SafeVarargs
    public ConfigValRangeImpl(
            RangeType defaultValue, RangeType disabledValue,
            @Nullable RangeContainer<RangeType> spanRange,
            @Nullable RangeContainer<DecimalType> boundaryRange,
            boolean enableThresholdConfig,
            @Nullable List<Pair<String, String>> additionalThreshold,
            @Nullable Function<CustomArithmetic<RangeType>, RangeType> actionIfNotEqualConstraint,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<RangeType>... configureValues
    ) {
        super(
                checkBoundary(boundaryRange, defaultValue, "defaultValue"),
                checkBoundary(boundaryRange, disabledValue, "disabledValue"),
                spanRange == null ? new RangeContainer<>() : spanRange,
                "[0; 0]",
                actionIfNotEqualConstraint != null ? actionIfNotEqualConstraint : arithmetic -> RangeType.ZERO,
                constraints,
                genValues(
                        enableThresholdConfig,
                        defaultValue,
                        spanRange != null && !spanRange.isEmpty() ? spanRange.getMinValue() : RangeType.ZERO,
                        additionalThreshold,
                        configureValues
                )
        );
        this.boundaryRange = boundaryRange == null ? new RangeContainer<>() : boundaryRange;
    }

    private static RangeType checkBoundary(
            @Nullable RangeContainer<DecimalType> boundaryRange, RangeType val, String valueName
    ) {
        if (boundaryRange != null && !boundaryRange.isEmpty()) {
            if (!boundaryRange.isInRange(val.getMin()) || !boundaryRange.isInRange(val.getMax())) {
                throw new IllegalStateException("ConfigValRangeImpl got " + valueName + " whose boundaries are outside valid boundary range");
            }
        }
        return val;
    }

    @Nullable
    private static PredefinedValue<RangeType>[] genValues(
            boolean tv, RangeType def, RangeType min,
            @Nullable List<Pair<String, String>> additionalThreshold,
            @Nullable PredefinedValue<RangeType>... predefinedValues
    ) {
        predefinedValues = MouseCollections.map(
                val -> val.getValue() != null ? val : new PredefinedValueImpl<>(val.getDisplayName(), RangeType.ZERO),
                true, predefinedValues
        );

        List<PredefinedValue<RangeType>> list = new ArrayList<>();
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
        return list.isEmpty() ? predefinedValues
                : MouseCollections.addAll(PredefinedValue.class, predefinedValues, list);
    }

    @Override
    protected RangeType adaptValue(RangeType value) {
        RangeType adapted = super.adaptValue(value);
        if (!boundaryRange.isEmpty()) {
            DecimalType min     = adapted.getMin();
            DecimalType max     = adapted.getMax();
            boolean     changed = false;
            if (!boundaryRange.isInRange(min)) {
                min = boundaryRange.getNearValue(min);
                changed = true;
            }
            if (!boundaryRange.isInRange(max)) {
                max = boundaryRange.getNearValue(max);
                changed = true;
            }
            if (changed) {
                double dMin = Math.min(min.asDouble(), max.asDouble());
                double dMax = Math.max(min.asDouble(), max.asDouble());
                return RangeType.create(dMin, dMax, adapted.isIncludeMin(), adapted.isIncludeMax());
            }
        }
        return adapted;
    }

    @Override
    protected VariableValue<RangeType> parseValue(@Nullable String value) {
        try {
            return VariableValue.create(CustomType.parse(RangeType.class, value));
        } catch (ValueFormatException ignore) { return VariableValue.create(); }
    }

    @Override public DecimalType getMin()                                                { return getValue().getMin(); }
    @Override public DecimalType getMax()                                                { return getValue().getMax(); }
    @Override public double getMinDouble()                                               { return getValue().getMinDouble(); }
    @Override public double getMaxDouble()                                               { return getValue().getMaxDouble(); }
    @Override public long getMinLong()                                                   { return getValue().getMinLong(); }
    @Override public long getMaxLong()                                                   { return getValue().getMaxLong(); }
    @Override public boolean isIncludeMin()                                              { return getValue().isIncludeMin(); }
    @Override public boolean isIncludeMax()                                              { return getValue().isIncludeMax(); }
    @Override public double getSpan()                                                    { return getValue().getSpan(); }
    @Override public RangeContainer<DecimalType> getBoundaryRange()                      { return boundaryRange; }

    @Override public boolean contains(double val)                                        { return getValue().isInRange(val); }
    @Override public boolean contains(Number val)                                        { return getValue().isInRange(val); }
    @Override public boolean contains(NumberType<?> val)                                 { return getValue().isInRange(val); }

    @Override public double rollDouble()                                                 { return getValue().rollDouble(); }
    @Override public double rollDouble(QuantityDistribution distribution)                { return getValue().rollDouble(distribution); }
    @Override public double rollDouble(Random random, QuantityDistribution distribution) { return getValue().rollDouble(random, distribution); }

    @Override public long rollLong()                                                     { return getValue().rollLong(); }
    @Override public long rollLong(QuantityDistribution distribution)                    { return getValue().rollLong(distribution); }
    @Override public long rollLong(Random random, QuantityDistribution distribution)     { return getValue().rollLong(random, distribution); }

    @Override public void setRange(double min, double max)                               { setValue(RangeType.create(min, max)); }
    @Override
    public void setRange(double min, double max, boolean includeMin, boolean includeMax) { setValue(RangeType.create(min, max, includeMin, includeMax)); }
}