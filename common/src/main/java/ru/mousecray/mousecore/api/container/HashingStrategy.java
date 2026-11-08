/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.container;

import javax.annotation.Nullable;
import java.util.Objects;

@SuppressWarnings("unchecked")
public interface HashingStrategy<T> {
    int hashCode(@Nullable T object);

    boolean equals(@Nullable T o1, @Nullable T o2);

    static <T> HashingStrategy<T> canonical() { return (HashingStrategy<T>) CanonicalHashingStrategy.INSTANCE; }
}

final class CanonicalHashingStrategy<T> implements HashingStrategy<T> {
    static final HashingStrategy<?> INSTANCE = new CanonicalHashingStrategy<>();

    @Override public int hashCode(T value)      { return Objects.hashCode(value); }
    @Override public boolean equals(T o1, T o2) { return Objects.equals(o1, o2); }
}