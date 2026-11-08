/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.config.values.base.list.simple;

import ru.mousecray.mousecore.api.VariableValue;
import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.ISupportRange;
import ru.mousecray.mousecore.api.config.IValType;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.customtype.NumberType;
import ru.mousecray.mousecore.api.customtype.range.RangeContainer;
import ru.mousecray.mousecore.api.customtype.values.SimpleListType;
import ru.mousecray.mousecore.api.log.ConsoleColor;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Function;

import static ru.mousecray.mousecore.api.VariableValue.create;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
@FieldsAreNonnullByDefault
public abstract class ConfigSimpleListNumberVal<
        T extends NumberType<?>
        > extends ConfigSimpleListVal<T> implements ISupportRange<T> {
    private final RangeContainer<T> range;
    @SafeVarargs
    public ConfigSimpleListNumberVal(
            IValType listComponentType, Class<T> listTypeClass,
            @Nullable Function<String, T> valCreator,
            SimpleListType<T> defaultValue, SimpleListType<T> disabledValue,
            RangeContainer<T> range,
            @Nullable Constraint<?>[] constraints,
            @Nullable PredefinedValue<SimpleListType<T>>... configureValues
    ) {
        super(
                listComponentType, listTypeClass, valCreator,
                checkValue(range, defaultValue, "defaultValue"),
                checkValue(range, disabledValue, "disabledValue"),
                constraints, configureValues
        );
        this.range = Objects.requireNonNull(range);
    }

    private static <T extends NumberType<?>> SimpleListType<T> checkValue(
            RangeContainer<T> range, SimpleListType<T> value, String valueName
    ) {
        value.forEach(val -> {
            if (!range.isInRange(val)) {
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

    @SuppressWarnings("DataFlowIssue") @Override
    protected T adaptListValue(T value) {
        if (!isInRange(value)) {
            if (hasLogger()) {
                getLogger().atWarn()
                        .withPrefix("Config")
                        .withStyle(ConsoleColor.YELLOW_BG)
                        .log("ConfigValueSimpleListNumber '{0}' got value that not valid. It was adapted",
                                owner.getFullInternalName());
            }
            return range.getNearValue(value);
        }
        return value;
    }
}