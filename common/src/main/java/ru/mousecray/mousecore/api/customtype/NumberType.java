/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.utils.MouseNumbers;
import ru.mousecray.mousecore.api.utils.MouseUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Comparator;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public abstract class NumberType<T extends Comparable<T>> extends CustomType<NumberType<?>> {
    protected final T value;

    protected NumberType(T value) {
        super(CustomValType.NUMBER);
        this.value = value;
    }

    public T getValue()                { return value; }

    public abstract Number asNumber();

    public double asDouble()           { return asNumber().doubleValue(); }
    public float asFloat()             { return asNumber().floatValue(); }
    public long asLong()               { return asNumber().longValue(); }
    public int asInt()                 { return asNumber().intValue(); }
    public short asShort()             { return asNumber().shortValue(); }
    public byte asByte()               { return asNumber().byteValue(); }

    @Override public String toString() { return MouseNumbers.formatObjectIfNumber(value, false, true); }

    public abstract <TYPE extends NumberType<?>> TYPE createType(Number value);

    @Override public int hashCode()    { return Objects.hash(value); }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") @Override
    public boolean equals(Object o) {
        return MouseUtils.Equator
                .<NumberType<T>, T>equaling(NumberType::getValue)
                .equals(getClass(), this, o);
    }

    @Override
    public int compareTo(NumberType<?> o) {
        return Comparator
                .<NumberType<?>, Comparable>comparing(NumberType::getValue)
                .compare(this, o);
    }
}