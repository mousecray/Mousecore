/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.build.val.base;

import ru.mousecray.mousecore.api.config.val.ConfigVal;
import ru.mousecray.mousecore.api.customtype.NumberType;
import ru.mousecray.mousecore.api.customtype.range.Range;
import ru.mousecray.mousecore.api.customtype.range.RangeContainer;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class AbstractConfigSimpleListNumberBuilder<
        BUILDER extends AbstractConfigChildBuilder,
        CONFIG_VAL_LIST extends ConfigVal,
        LIST_COMPONENT_TYPE extends NumberType<?>,
        SELF extends AbstractConfigSimpleListNumberBuilder<BUILDER, CONFIG_VAL_LIST, LIST_COMPONENT_TYPE, SELF>>
        extends AbstractConfigSimpleListBuilder<BUILDER, CONFIG_VAL_LIST, LIST_COMPONENT_TYPE, SELF> {
    protected final List<Range<LIST_COMPONENT_TYPE>> range = new ArrayList<>();

    protected AbstractConfigSimpleListNumberBuilder(BUILDER parBuilder, Consumer<CONFIG_VAL_LIST> onComplete) {
        super(parBuilder, onComplete);
    }

    public SELF addValidRange(LIST_COMPONENT_TYPE minValue, LIST_COMPONENT_TYPE maxValue) {
        range.add(Range.of(Objects.requireNonNull(minValue), Objects.requireNonNull(maxValue)));
        return self();
    }

    protected RangeContainer<LIST_COMPONENT_TYPE> genRangeContainer() {
        return range.isEmpty() ? null : new RangeContainer<>(range);
    }
}