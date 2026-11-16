/*******************************************************************************
 *  Copyright © 2026 mousecray
 *  Licensed under the GNU Lesser General Public License, Version 3.0
 ****************************************************************************** */

package ru.mousecray.mousecore.api.customtype.values.other;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.OtherType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.error.UnsupportedValException;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.api.utils.MouseStrings;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public class EnumType<E extends Enum<E>> extends OtherType<E> {
    private static final Map<Class<? extends Enum<?>>, Map<String, Enum<?>>> ENUM_CACHE = new ConcurrentHashMap<>();

    protected final Class<E> enumClass;

    protected EnumType(E value) {
        super(Objects.requireNonNull(value));
        enumClass = value.getDeclaringClass();
    }

    public static <E extends Enum<E>> EnumType<E> create(E value) { return new EnumType<>(value); }

    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>> EnumType<E> parseEnum(Class<E> enumClass, @Nullable String raw) {
        Objects.requireNonNull(enumClass);
        raw = MouseStrings.trimWith(raw, true, '\t');
        if (raw == null || raw.isEmpty()) throw new ValueFormatException("Cannot parse empty string as Enum of type " + enumClass.getSimpleName());

        Map<String, Enum<?>> nameMap = ENUM_CACHE.computeIfAbsent(enumClass, clazz -> {
            Map<String, Enum<?>> map = new ConcurrentHashMap<>();
            for (Enum<?> constant : clazz.getEnumConstants()) map.put(constant.name().toUpperCase(), constant);
            return map;
        });

        Enum<?> match = nameMap.get(raw.toUpperCase());
        if (match == null) throw new ValueFormatException("No enum constant " + enumClass.getName() + "." + raw);
        return new EnumType<>((E) match);
    }

    @Override public IValType getValType() { return ValType.ENUM; }
    public E getEnum()                     { return value; }
    public Class<E> getEnumClass()         { return enumClass; }
    public E[] getAllConstants()           { return enumClass.getEnumConstants(); }

    @SuppressWarnings("unchecked")
    @Override
    public <TYPE extends OtherType<E>> TYPE createType(Object val) {
        if (val instanceof EnumType<?>) return (TYPE) val;
        else if (enumClass.isInstance(val)) return (TYPE) create((E) val);
        else if (val instanceof String) return (TYPE) parseEnum(enumClass, (String) val);
        throw new UnsupportedValException("Cannot create EnumType of " + enumClass.getSimpleName() + " from " + val);
    }

    @Override public String toString() { return value.name(); }
}