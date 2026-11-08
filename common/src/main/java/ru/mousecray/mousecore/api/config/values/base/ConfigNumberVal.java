/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.config.values.base;

import ru.mousecray.mousecore.api.VariableValue;
import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.ConfigVal;
import ru.mousecray.mousecore.api.config.ISupportRange;
import ru.mousecray.mousecore.api.config.IValType;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.customtype.NumberType;
import ru.mousecray.mousecore.api.customtype.range.RangeContainer;
import ru.mousecray.mousecore.api.utils.MouseNumbers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

import static ru.mousecray.mousecore.api.VariableValue.create;

@FieldsAreNonnullByDefault
@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class ConfigNumberVal<
        T extends NumberType<?>
        > extends ConfigVal<T> implements ISupportRange<T> {
    private final RangeContainer<T>                range;
    private final Function<CustomArithmetic<T>, T> actionIfNotEqualConstraint;

    @SafeVarargs
    public ConfigNumberVal(
            IValType type, T defaultValue, T disabledValue, RangeContainer<T> range, @Nullable String specificDataType,
            @Nullable Function<CustomArithmetic<T>, T> actionIfNotEqualConstraint,
            @Nullable Constraint<?>[] constraints, @Nullable PredefinedValue<T>... configureValues
    ) {
        super(
                type,
                checkValue(range, defaultValue, "defaultValue"),
                checkValue(range, disabledValue, "disabledValue"),
                val -> specificDataType, constraints, configureValues);
        this.range = range;
        this.actionIfNotEqualConstraint = actionIfNotEqualConstraint == null
                ? arithmetic -> (T) arithmetic.decrement()
                : actionIfNotEqualConstraint;
    }

    private static <T extends NumberType<?>> T checkValue(
            RangeContainer<T> range, T value, String valueName
    ) {
        if (!range.isInRange(value)) {
            throw new IllegalStateException("ConfigNumberVal got " + valueName + " that not in valid range");
        }
        return value;
    }

    protected T getNumber()                         { return getValue(); }

    public double getDouble()                       { return getNumber().asDouble(); }
    public float getFloat()                         { return MouseNumbers.toFloatExact(getDouble()); }

    public long getLong()                           { return getNumber().asLong(); }
    public int getInt()                             { return Math.toIntExact(getLong()); }
    public short getShort()                         { return MouseNumbers.toShortExact(getLong()); }
    public byte getByte()                           { return MouseNumbers.toByteExact(getLong()); }

    protected void setNumber(@Nullable T value)     { setValue(value); }

    public void setDouble(double value)             { setNumber(getNumber().createType(value)); }
    public void setFloat(float value)               { setNumber(getNumber().createType(value)); }

    public void setLong(long value)                 { setNumber(getNumber().createType(value)); }
    public void setInt(int value)                   { setNumber(getNumber().createType(value)); }
    public void setShort(short value)               { setNumber(getNumber().createType(value)); }
    public void setByte(byte value)                 { setNumber(getNumber().createType(value)); }

    @Override public boolean isInRange(T other)     { return getRange().isInRange(other); }
    @Override public RangeContainer<T> getRange()   { return range; }
    @Override public VariableValue<T> getMinValue() { return getRange().isEmpty() ? create() : create(getRange().getMinValue()); }
    @Override public VariableValue<T> getMaxValue() { return getRange().isEmpty() ? create() : create(getRange().getMaxValue()); }

    @Override
    protected T adaptValue(T value) {
        if (!isInRange(value)) return getRange().getNearValue(value);
        return super.adaptValue(value);
    }

    @SuppressWarnings("unchecked") @Nonnull @Override
    protected T processValueIfNotEqualConstraint(T value) {
        return actionIfNotEqualConstraint.apply((CustomArithmetic<T>) value.getArithmeticPipeline());
    }

    @Override protected boolean saveDisabledValue() { return false; }
}
