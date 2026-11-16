/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype.values.collection;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.MapType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.api.log.ConsoleColor;
import ru.mousecray.mousecore.api.log.LogPrefixes;
import ru.mousecray.mousecore.api.log.MouseLogger;
import ru.mousecray.mousecore.api.utils.MouseStrings;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public final class SimpleMapType<K extends CustomType<?>, V extends CustomType<?>> extends MapType<K, V, SimpleMapType<K, V>> {
    private final Map<K, V> map;

    private SimpleMapType(IValType keyType, IValType valueType, Map<K, V> map) {
        super(keyType, valueType);
        Objects.requireNonNull(map, "Map content cannot be null");
        for (Map.Entry<K, V> entry : map.entrySet()) {
            K key = Objects.requireNonNull(entry.getKey(), "Map key cannot be null");
            V val = Objects.requireNonNull(entry.getValue(), "Map value cannot be null");
            if (keyType != ValType.UNDEFINED && key.getValType() != keyType) {
                throw new ValueFormatException("Map key type mismatch: expected " + keyType.getDisplayName() + ", got " + key.getValType().getDisplayName());
            }
            if (valueType != ValType.UNDEFINED && val.getValType() != valueType) {
                throw new ValueFormatException("Map value type mismatch: expected " + valueType.getDisplayName() + ", got " + val.getValType().getDisplayName());
            }
        }
        this.map = Collections.unmodifiableMap(new LinkedHashMap<>(map));
    }

    public static <K extends CustomType<?>, V extends CustomType<?>> SimpleMapType<K, V> create(IValType keyType, IValType valueType) {
        return new SimpleMapType<>(keyType, valueType, Collections.emptyMap());
    }

    public static <K extends CustomType<?>, V extends CustomType<?>> SimpleMapType<K, V> create(IValType keyType, IValType valueType, Map<K, V> map) {
        return new SimpleMapType<>(keyType, valueType, map);
    }

    public static <K extends CustomType<?>, V extends CustomType<?>> SimpleMapType<K, V> create(IValType keyType, IValType valueType, K key, V value) {
        Map<K, V> single = new LinkedHashMap<>();
        single.put(key, value);
        return new SimpleMapType<>(keyType, valueType, single);
    }

    @Override public SimpleMapType<K, V> createType(Map<K, V> map) { return new SimpleMapType<>(keyType, valueType, map); }

    @SuppressWarnings("unchecked")
    public static <K extends CustomType<?>, V extends CustomType<?>> SimpleMapType<K, V> fromString(
            IValType keyType,
            IValType valueType,
            @Nullable String raw,
            @Nullable MouseLogger logger
    ) {
        Objects.requireNonNull(keyType, "KeyType cannot be null");
        Objects.requireNonNull(valueType, "ValueType cannot be null");
        String clean = MouseStrings.trimWith(raw, true, '\t');
        if (clean == null || clean.isEmpty()) {
            throw new ValueFormatException("Cannot parse empty string as Map");
        }

        if (clean.startsWith("{") && clean.endsWith("}")) {
            clean = clean.substring(1, clean.length() - 1);
        }

        Class<? extends CustomType> keyClass = Objects.requireNonNull(keyType.getTypeClass(), "Key class is undefined for " + keyType.getDisplayName());
        Class<? extends CustomType> valClass = Objects.requireNonNull(valueType.getTypeClass(), "Value class is undefined for " + valueType.getDisplayName());

        Map<K, V> parsedMap = new LinkedHashMap<>();
        String    delimiter = clean.contains("\n") ? "\n" : ",";
        String[]  pairs     = clean.split(delimiter);

        for (String pairStr : pairs) {
            String line = MouseStrings.trimWith(pairStr, true, '\t');
            if (line == null || line.isEmpty()) continue;

            String   sep = line.contains(":") ? ":" : (line.contains("->") ? "->" : "=");
            String[] kv  = line.split(sep, 2);
            if (kv.length != 2) {
                String msg = "Invalid map pair format (missing ':' or '->'): '" + line + "'";
                if (logger != null) {
                    logger.atWarn()
                            .withPrefix(LogPrefixes.CUSTOMTYPE)
                            .withStyle(ConsoleColor.YELLOW_BG)
                            .log(msg);
                }
                throw new ValueFormatException(msg);
            }

            String keyRaw = MouseStrings.trimWith(kv[0], true, '\t');
            String valRaw = MouseStrings.trimWith(kv[1], true, '\t');

            try {
                K parsedKey = (K) CustomType.parse(keyClass, keyRaw);
                V parsedVal = (V) CustomType.parse(valClass, valRaw);

                if (parsedKey == null) throw new ValueFormatException("Parsed key is null for '" + keyRaw + "'");
                if (parsedVal == null) throw new ValueFormatException("Parsed value is null for '" + valRaw + "'");

                parsedMap.put(parsedKey, parsedVal);
            } catch (Exception e) {
                String msg = "Failed to parse map entry ['" + keyRaw + "' -> '" + valRaw + "']: " + e.getMessage();
                if (logger != null) {
                    logger.atWarn()
                            .withPrefix(LogPrefixes.CUSTOMTYPE)
                            .withStyle(ConsoleColor.YELLOW_BG)
                            .log(msg);
                }
                throw new ValueFormatException(msg, e);
            }
        }

        return create(keyType, valueType, parsedMap);
    }

    public static <K extends CustomType<?>, V extends CustomType<?>> SimpleMapType<K, V> fromString(
            IValType keyType,
            IValType valueType,
            @Nullable String raw
    ) {
        return fromString(keyType, valueType, raw, null);
    }

    @Override public int size()                   { return map.size(); }
    @Override public boolean containsKey(K key)   { return map.containsKey(key); }
    @Override public boolean containsValue(V val) { return map.containsValue(val); }
    @Nullable @Override public V get(K key)       { return map.get(key); }
    @Override public Map<K, V> asMap()            { return map; }
    @Override public IValType getValType()        { return ValType.SIMPLE_MAP; }
}