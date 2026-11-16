/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.utils.MouseUtils;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public abstract class MapType<K extends CustomType<?>, V extends CustomType<?>, SELF extends MapType<K, V, SELF>> extends CustomType<SELF> {
    private static final MouseUtils.Equator<MapType<?, ?, ?>> EQUATOR = MouseUtils.Equator
            .<MapType<?, ?, ?>>equaling(MapType::getKeyType)
            .thenEqualing(MapType::getValueType)
            .thenEqualing(MapType::asMap);

    protected final IValType keyType;
    protected final IValType valueType;

    protected MapType(IValType keyType, IValType valueType) {
        super();
        this.keyType = Objects.requireNonNull(keyType, "KeyType cannot be null");
        this.valueType = Objects.requireNonNull(valueType, "ValueType cannot be null");
    }

    public IValType getKeyType()   { return keyType; }
    public IValType getValueType() { return valueType; }

    public abstract int size();
    public boolean isEmpty()       { return size() == 0; }

    public abstract boolean containsKey(K key);
    public abstract boolean containsValue(V value);

    @Nullable
    public abstract V get(K key);

    public V getOrDefault(K key, V defaultValue) {
        V val = get(key);
        return val != null ? val : defaultValue;
    }

    public abstract Map<K, V> asMap();
    public Set<K> asKeySet()                 { return asMap().keySet(); }
    public Collection<V> asValues()          { return asMap().values(); }
    public Set<Map.Entry<K, V>> asEntrySet() { return asMap().entrySet(); }

    public abstract SELF createType(Map<K, V> map);

    @Override
    public String toString() {
        return asMap().entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", ", "{", "}"));
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override public boolean equals(Object o) { return EQUATOR.test(this, o); }
    @Override public int hashCode() { return Objects.hash(keyType, valueType, asMap()); }

    @Override
    @SuppressWarnings({ "rawtypes" })
    public int compareTo(SELF o) {
        return Comparator
                .<MapType<?, ?, ?>>comparingInt(MapType::size)
                .thenComparing((m1, m2) -> {
                    List<Comparable> k1 = new ArrayList<>((Collection) m1.asMap().keySet());
                    List<Comparable> k2 = new ArrayList<>((Collection) m2.asMap().keySet());
                    Collections.sort(k1);
                    Collections.sort(k2);

                    for (int i = 0; i < k1.size(); i++) {
                        Comparable key1 = k1.get(i);
                        Comparable key2 = k2.get(i);
                        int        cmp  = key1.compareTo(key2);
                        if (cmp != 0) return cmp;

                        CustomType<?> v1 = m1.asMap().get(key1);
                        CustomType<?> v2 = m2.asMap().get(key2);
                        if (v1 == v2) continue;
                        if (v1 == null) return -1;
                        if (v2 == null) return 1;

                        try {
                            int vcmp = ((Comparable) v1).compareTo(v2);
                            if (vcmp != 0) return vcmp;
                        } catch (ClassCastException ignore) {
                            int vcmp = v1.getClass().getName().compareTo(v2.getClass().getName());
                            if (vcmp != 0) return vcmp;
                        }
                    }
                    return 0;
                })
                .compare(this, o);
    }
}