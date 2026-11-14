package ru.mousecray.mousecore.core.config.val.base.list;

import ru.mousecray.mousecore.api.VariableValue;
import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.base.list.ConfigValSimpleListNumber;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.NumberType;
import ru.mousecray.mousecore.api.customtype.range.RangeContainer;
import ru.mousecray.mousecore.api.customtype.values.ConditionalValType;
import ru.mousecray.mousecore.api.customtype.values.SimpleListType;
import ru.mousecray.mousecore.api.log.ConsoleColor;
import ru.mousecray.mousecore.api.log.LogPrefixes;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Function;

import static ru.mousecray.mousecore.api.VariableValue.create;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
@FieldsAreNonnullByDefault
public abstract class ConfigValSimpleListNumberImpl<T extends NumberType<?>> extends ConfigValSimpleListImpl<T> implements ConfigValSimpleListNumber<T> {
    private final RangeContainer<T> range;

    @SafeVarargs
    public ConfigValSimpleListNumberImpl(
            boolean isConditional,
            @Nullable Function<String, T> valCreator,
            SimpleListType<T> defaultValue, SimpleListType<T> disabledValue,
            RangeContainer<T> range,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<SimpleListType<T>>... configureValues
    ) {
        super(
                isConditional,
                valCreator,
                checkValue(range, defaultValue, "defaultValue"),
                checkValue(range, disabledValue, "disabledValue"),
                constraints, configureValues
        );
        this.range = Objects.requireNonNull(range);
    }

    private static <T extends NumberType<?>> SimpleListType<T> checkValue(RangeContainer<T> range, SimpleListType<T> value, String valueName) {
        value.forEach(val -> {
            if (!range.isInRange(val.getValue())) {
                throw new IllegalStateException("ConfigValueSimpleListNumber got " + valueName +
                        " that contains listValue that not in valid range");
            }
        });
        return value;
    }

    @Override public boolean isInRange(T other)     { return range.isInRange(other); }
    @Override public RangeContainer<T> getRange()   { return range; }
    @Override public VariableValue<T> getMinValue() { return range.isEmpty() ? create() : create(range.getMinValue()); }
    @Override public VariableValue<T> getMaxValue() { return range.isEmpty() ? create() : create(range.getMaxValue()); }

    @SuppressWarnings("DataFlowIssue")
    @Override
    protected ConditionalValType<T> adaptListValue(ConditionalValType<T> value) {
        T innerNumber = value.getValue();
        if (!isInRange(innerNumber)) {
            if (hasLogger()) {
                getLogger().atWarn()
                        .withPrefix(LogPrefixes.CONFIG)
                        .withStyle(ConsoleColor.YELLOW_BG)
                        .log("ConfigValueSimpleListNumber '{0}' got value that not valid. It was adapted",
                                owner.getFullInternalName());
            }
            return value.createType(range.getNearValue(innerNumber), value.isAnti());
        }
        return value;
    }
}