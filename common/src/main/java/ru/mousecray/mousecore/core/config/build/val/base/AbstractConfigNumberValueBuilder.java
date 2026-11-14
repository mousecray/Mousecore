/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.build.val.base;

import ru.mousecray.mousecore.api.config.val.ConfigVal;
import ru.mousecray.mousecore.api.customtype.NumberType;
import ru.mousecray.mousecore.api.customtype.op.CustomArithmetic;
import ru.mousecray.mousecore.api.customtype.range.Range;
import ru.mousecray.mousecore.api.customtype.range.RangeContainer;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class AbstractConfigNumberValueBuilder<
        BUILDER extends AbstractConfigChildBuilder,
        CONFIG_VAL_TYPE extends ConfigVal,
        VAL_TYPE extends NumberType<?>,
        SELF extends AbstractConfigNumberValueBuilder<BUILDER, CONFIG_VAL_TYPE, VAL_TYPE, SELF>
        >
        extends AbstractConfigValueBuilder<BUILDER, CONFIG_VAL_TYPE, VAL_TYPE, SELF> {
    protected final List<Range<VAL_TYPE>>                          range = new ArrayList<>();
    protected       Function<CustomArithmetic<VAL_TYPE>, VAL_TYPE> actionIfNotEqualConstraint;

    protected AbstractConfigNumberValueBuilder(BUILDER parBuilder, VAL_TYPE defaultValue, VAL_TYPE disabledValue, Consumer<CONFIG_VAL_TYPE> onComplete) {
        super(parBuilder, defaultValue, disabledValue, onComplete);
    }

    public SELF addValidRange(VAL_TYPE minValue, VAL_TYPE maxValue) {
        range.add(Range.of(Objects.requireNonNull(minValue), Objects.requireNonNull(maxValue)));
        disabledValue = minValue;
        return self();
    }

    public SELF enableThresholdConfiguration() {
        enableThresholdConfiguration = true;
        return self();
    }

    public SELF enableStandardConfiguration() {
        enableStandardConfiguration = true;
        return self();
    }

    public SELF disableDictionaryThreshold() {
        disableDictionaryThreshold = true;
        return self();
    }

    public SELF disableDictionaryStandard() {
        disableDictionaryStandard = true;
        return self();
    }

    public SELF setActionIfNotEqualConstraint(Function<CustomArithmetic<VAL_TYPE>, VAL_TYPE> actionIfNotEqualConstraint) {
        Objects.requireNonNull(actionIfNotEqualConstraint);
        this.actionIfNotEqualConstraint = actionIfNotEqualConstraint;
        return self();
    }

    protected RangeContainer<VAL_TYPE> genRangeContainer(VAL_TYPE min, VAL_TYPE max) {
        return range.isEmpty()
                ? (min == null || max == null
                ? null
                : new RangeContainer<>(
                Range.of(
                        min,
                        max
                )))
                : new RangeContainer<>(range);
    }

    protected RangeContainer<VAL_TYPE> genRangeContainer() { return genRangeContainer(null, null); }
}