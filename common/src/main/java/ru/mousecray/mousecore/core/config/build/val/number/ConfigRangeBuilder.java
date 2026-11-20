package ru.mousecray.mousecore.core.config.build.val.number;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.val.ConfigValRange;
import ru.mousecray.mousecore.api.customtype.range.Range;
import ru.mousecray.mousecore.api.customtype.range.RangeContainer;
import ru.mousecray.mousecore.api.customtype.values.number.DecimalType;
import ru.mousecray.mousecore.api.customtype.values.number.RangeType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.base.AbstractConfigNumberValueBuilder;
import ru.mousecray.mousecore.core.config.val.ConfigValRangeImpl;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigRangeBuilder<BUILDER extends AbstractConfigChildBuilder>
        extends AbstractConfigNumberValueBuilder<BUILDER, ConfigValRange, RangeType, ConfigRangeBuilder<BUILDER>> {

    private final List<Range<DecimalType>> boundaryRanges = new ArrayList<>();
    private final List<Range<DecimalType>> spanRanges     = new ArrayList<>();

    private ConfigRangeBuilder(BUILDER parBuilder, Consumer<ConfigValRange> onComplete) {
        super(parBuilder, RangeType.ZERO, RangeType.ZERO, onComplete);
    }

    public static <BUILDER extends AbstractConfigChildBuilder> ConfigRangeBuilder<BUILDER> create(BUILDER parBuilder, Consumer<ConfigValRange> onComplete) {
        return new ConfigRangeBuilder<>(Objects.requireNonNull(parBuilder), onComplete);
    }

    public ConfigRangeBuilder<BUILDER> addValidBoundary(DecimalType min, DecimalType max) {
        boundaryRanges.add(Range.of(Objects.requireNonNull(min), Objects.requireNonNull(max)));
        return this;
    }

    public ConfigRangeBuilder<BUILDER> addValidBoundary(double min, double max) {
        return addValidBoundary(DecimalType.create(min), DecimalType.create(max));
    }

    public ConfigRangeBuilder<BUILDER> addValidSpan(DecimalType minSpan, DecimalType maxSpan) {
        spanRanges.add(Range.of(Objects.requireNonNull(minSpan), Objects.requireNonNull(maxSpan)));
        return this;
    }

    public ConfigRangeBuilder<BUILDER> addValidSpan(double minSpan, double maxSpan) {
        return addValidSpan(DecimalType.create(minSpan), DecimalType.create(maxSpan));
    }

    public BUILDER buildRange() {
        RangeContainer<DecimalType> boundary = boundaryRanges.isEmpty() ? new RangeContainer<>() : new RangeContainer<>(boundaryRanges);
        RangeContainer<DecimalType> span     = spanRanges.isEmpty() ? new RangeContainer<>() : new RangeContainer<>(spanRanges);

        onComplete.accept(
                new ConfigValRangeImpl(
                        defaultValue, disabledValue,
                        span,
                        boundary,
                        enableThresholdConfiguration,
                        genDictionaryThreshold(),
                        actionIfNotEqualConstraint,
                        genConstraints(), genConfigure()
                )
        );
        return parBuilder;
    }
}