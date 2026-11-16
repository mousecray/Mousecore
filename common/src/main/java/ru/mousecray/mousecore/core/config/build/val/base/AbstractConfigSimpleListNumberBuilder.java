package ru.mousecray.mousecore.core.config.build.val.base;

import ru.mousecray.mousecore.api.config.val.base.list.ConfigValSimpleList;
import ru.mousecray.mousecore.api.customtype.NumberType;
import ru.mousecray.mousecore.api.customtype.range.Range;
import ru.mousecray.mousecore.api.customtype.range.RangeContainer;
import ru.mousecray.mousecore.api.customtype.values.logic.ConditionalValType;
import ru.mousecray.mousecore.api.error.IncorrectOperationException;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class AbstractConfigSimpleListNumberBuilder<
        BUILDER extends AbstractConfigChildBuilder,
        LIST_COMPONENT_TYPE extends NumberType<?>,
        SELF extends AbstractConfigSimpleListNumberBuilder<BUILDER, LIST_COMPONENT_TYPE, SELF>>
        extends AbstractConfigSimpleListBuilder<BUILDER, LIST_COMPONENT_TYPE, SELF> {

    protected final     List<Range<LIST_COMPONENT_TYPE>>    range                 = new ArrayList<>();
    @Nullable protected RangeContainer<LIST_COMPONENT_TYPE> cachedRangeContainer;
    protected           boolean                             isRangeContainerBuilt = false;

    protected AbstractConfigSimpleListNumberBuilder(BUILDER parBuilder, Consumer<ConfigValSimpleList<?>> onComplete) {
        super(parBuilder, onComplete);
    }

    public SELF addValidRange(LIST_COMPONENT_TYPE minValue, LIST_COMPONENT_TYPE maxValue) {
        range.add(Range.of(Objects.requireNonNull(minValue), Objects.requireNonNull(maxValue)));
        isRangeContainerBuilt = false;
        cachedRangeContainer = null;
        return self();
    }

    @Nullable
    protected RangeContainer<LIST_COMPONENT_TYPE> genRangeContainer() {
        if (!isRangeContainerBuilt) {
            cachedRangeContainer = range.isEmpty() ? null : new RangeContainer<>(range);
            isRangeContainerBuilt = true;
        }
        return cachedRangeContainer;
    }

    @Override
    protected void validateEntries(List<ConditionalValType<LIST_COMPONENT_TYPE>> entries, String valueName) {
        RangeContainer<LIST_COMPONENT_TYPE> rangeContainer = genRangeContainer();
        if (rangeContainer != null) {
            for (ConditionalValType<LIST_COMPONENT_TYPE> entry : entries) {
                if (!rangeContainer.isInRange(entry.getValue())) {
                    throw new IncorrectOperationException("ConfigValList " + valueName + " contains element '"
                            + entry.getValue() + "' that violates the valid range: " + rangeContainer);
                }
            }
        }
    }
}