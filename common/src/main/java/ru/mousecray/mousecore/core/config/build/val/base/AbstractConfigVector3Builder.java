/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.core.config.build.val.base;

import ru.mousecray.mousecore.api.config.val.ConfigVal;
import ru.mousecray.mousecore.api.customtype.NumberType;
import ru.mousecray.mousecore.api.customtype.Vector3Type;
import ru.mousecray.mousecore.api.customtype.range.Range;
import ru.mousecray.mousecore.api.customtype.range.RangeContainer;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class AbstractConfigVector3Builder<
        BUILDER extends AbstractConfigChildBuilder,
        CONFIG_VAL_TYPE extends ConfigVal,
        N extends NumberType<?>,
        VAL_TYPE extends Vector3Type<N, VAL_TYPE>,
        SELF extends AbstractConfigVector3Builder<BUILDER, CONFIG_VAL_TYPE, N, VAL_TYPE, SELF>
        > extends AbstractConfigValueBuilder<BUILDER, CONFIG_VAL_TYPE, VAL_TYPE, SELF> {

    protected final List<Range<N>> rangeX = new ArrayList<>();
    protected final List<Range<N>> rangeY = new ArrayList<>();
    protected final List<Range<N>> rangeZ = new ArrayList<>();

    protected AbstractConfigVector3Builder(BUILDER parBuilder, VAL_TYPE defaultValue, VAL_TYPE disabledValue, Consumer<CONFIG_VAL_TYPE> onComplete) {
        super(parBuilder, defaultValue, disabledValue, onComplete);
    }

    public SELF addValidRangeX(N min, N max) {
        rangeX.add(Range.of(Objects.requireNonNull(min), Objects.requireNonNull(max)));
        return self();
    }

    public SELF addValidRangeY(N min, N max) {
        rangeY.add(Range.of(Objects.requireNonNull(min), Objects.requireNonNull(max)));
        return self();
    }

    public SELF addValidRangeZ(N min, N max) {
        rangeZ.add(Range.of(Objects.requireNonNull(min), Objects.requireNonNull(max)));
        return self();
    }

    @Nullable protected RangeContainer<N> genRangeContainerX() { return rangeX.isEmpty() ? null : new RangeContainer<>(rangeX); }
    @Nullable protected RangeContainer<N> genRangeContainerY() { return rangeY.isEmpty() ? null : new RangeContainer<>(rangeY); }
    @Nullable protected RangeContainer<N> genRangeContainerZ() { return rangeZ.isEmpty() ? null : new RangeContainer<>(rangeZ); }
}