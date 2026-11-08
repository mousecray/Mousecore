/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.container;

import javax.annotation.Nonnull;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

@SuppressWarnings({ "ClassEscapesDefinedScope", "unchecked" })
public final class SoftHashMap<K, V> extends RefHashMap<K, V> {
    public SoftHashMap() { super(4); }

    @Override
    protected @Nonnull <T> Key<T> createKey(@Nonnull T k, @Nonnull HashingStrategy<? super T> strategy, @Nonnull ReferenceQueue<? super T> q) {
        return new SoftKey<>(k, strategy, q);
    }

    private static final class SoftKey<T> extends SoftReference<T> implements Key<T> {
        private final          int                        myHash;
        @Nonnull private final HashingStrategy<? super T> myStrategy;

        private SoftKey(@Nonnull T k, @Nonnull HashingStrategy<? super T> strategy, @Nonnull ReferenceQueue<? super T> q) {
            super(k, q);
            myStrategy = strategy;
            myHash = strategy.hashCode(k);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Key)) return false;
            if (myHash != o.hashCode()) return false;
            T t = get();
            T u = ((Key<T>) o).get();
            if (t == null || u == null) return false;
            return keyEqual(t, u, myStrategy);
        }

        @Override
        public int hashCode() {
            return myHash;
        }

        @Nonnull @Override
        public String toString() {
            return "SoftHashMap.SoftKey(" + get() + ")";
        }
    }
}