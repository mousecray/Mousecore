/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.utils.MouseUtils;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Comparator;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public abstract class OtherType<T extends Comparable<T>> extends CustomType<OtherType<?>> {
    protected final T value;

    protected OtherType(T value) {
        super(CustomValType.OTHER);
        this.value = value;
    }

    public T getValue()                { return value; }

    @Override public int hashCode()    { return Objects.hash(value); }
    public abstract <TYPE extends OtherType<T>> TYPE createType(Object value);
    @Override public String toString() { return value.toString(); }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") @Override
    public boolean equals(Object o) {
        return MouseUtils.Equator
                .<OtherType<T>, T>equaling(OtherType::getValue)
                .equals(getClass(), this, o);
    }

    @Override
    public int compareTo(@Nonnull OtherType<?> o) {
        return Comparator
                .<OtherType<?>, Comparable>comparing(OtherType::getValue)
                .compare(this, o);
    }
}