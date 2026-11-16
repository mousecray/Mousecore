/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.utils.MouseUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public abstract class Vector3Type<T extends NumberType<?>, SELF extends Vector3Type<T, SELF>> extends CustomType<SELF> {
    private static final MouseUtils.Equator<Vector3Type<?, ?>> EQUATOR = MouseUtils.Equator
            .<Vector3Type<?, ?>>equaling(Vector3Type::getX)
            .thenEqualing(Vector3Type::getY)
            .thenEqualing(Vector3Type::getZ);

    protected final T x;
    protected final T y;
    protected final T z;

    protected Vector3Type(T x, T y, T z) {
        this.x = Objects.requireNonNull(x);
        this.y = Objects.requireNonNull(y);
        this.z = Objects.requireNonNull(z);
    }

    public T getX()                 { return x; }
    public T getY()                 { return y; }
    public T getZ()                 { return z; }

    public abstract double lengthSq();
    public double length()          { return Math.sqrt(lengthSq()); }

    @Override public int hashCode() { return Objects.hash(x, y, z); }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override public boolean equals(Object obj) { return EQUATOR.test(this, obj); }

    @Override
    public int compareTo(SELF o) {
        int cmp = x.compareTo(o.x);
        if (cmp != 0) return cmp;
        cmp = y.compareTo(o.y);
        if (cmp != 0) return cmp;
        return z.compareTo(o.z);
    }

    @Override public String toString() { return "(" + x + ", " + y + ", " + z + ")"; }
}