/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.utils.MouseUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Comparator;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public abstract class LogicalType<T extends Comparable<T>> extends CustomType<LogicalType<?>> {
    private static final MouseUtils.Equator<LogicalType<?>> EQUATOR = MouseUtils.Equator
            .<LogicalType<?>>equaling(LogicalType::getValue)
            .thenEqualing(LogicalType::getYesVal)
            .thenEqualing(LogicalType::getNoVal);

    private final T yesVal;
    private final T noVal;
    private final T value;

    protected LogicalType(T yesVal, T noVal, boolean isYes) {
        this.yesVal = yesVal;
        this.noVal = noVal;
        value = isYes ? yesVal : noVal;
    }

    protected T getYesVal()            { return yesVal; }
    protected T getNoVal()             { return noVal; }
    protected T getValue()             { return value; }
    public boolean isTrue()            { return value.equals(yesVal); }
    public boolean isFalse()           { return !isTrue(); }
    public boolean asBoolean()         { return isTrue(); }
    public int asInt()                 { return isTrue() ? 1 : 0; }

    @Override public String toString() { return isTrue() ? yesVal.toString() : noVal.toString(); }
    public abstract <TYPE extends LogicalType<?>> TYPE createType(boolean isYes);
    @Override public int hashCode()    { return Objects.hash(yesVal, noVal, value); }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") @Override
    public boolean equals(Object o) { return EQUATOR.test(this, o); }

    @Override
    public int compareTo(LogicalType<?> o) {
        return Comparator
                .<LogicalType<?>, Comparable>comparing(LogicalType::getValue)
                .thenComparing(LogicalType::getYesVal)
                .thenComparing(LogicalType::getNoVal)
                .compare(this, o);
    }
}