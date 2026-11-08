/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype.values;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.error.UnsupportedValException;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.api.utils.MouseStrings;
import ru.mousecray.mousecore.api.utils.MouseUtils;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@SuppressWarnings({ "unchecked" })
@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public final class ConditionalValType<T extends CustomType<?>> extends CustomType<ConditionalValType<T>> {
    public static <T extends CustomType<?>> ConditionalValType<T> NULL() {
        return new ConditionalValType<>(null, false);
    }

    private final T       value;
    private final boolean anti;

    private ConditionalValType(@Nullable T value, boolean anti) {
        super();
        this.value = checkValue(value);
        this.anti = anti;
    }

    public static <T extends CustomType<?>> ConditionalValType<T> create(T value, PlusMinusType isAnti) {
        return new ConditionalValType<>(Objects.requireNonNull(value), isAnti.asBoolean());
    }

    private static <T extends CustomType<?>> ConditionalValType<T> create(T value, boolean isAnti) {
        return new ConditionalValType<>(Objects.requireNonNull(value), isAnti);
    }

    public static <T extends CustomType<?>> ConditionalValType<T> create(T value) {
        return create(value, PlusMinusType.FALSE);
    }

    @Nullable
    private static <T extends CustomType<?>> T checkValue(@Nullable T value) {
        if (value instanceof ConditionalValType) throw new UnsupportedValException("Enclosing ConditionValTypes is not support");
        return value;
    }

    public PlusMinusType isNull() { return PlusMinusType.create(value == null); }
    public T getValue()           { return value; }
    public PlusMinusType isAnti() { return PlusMinusType.create(anti); }

    public static <T extends CustomType<?>> ConditionalValType<T> fromString(Class<T> valClass, String raw) {
        raw = MouseStrings.trimWith(raw, true, '\t');
        if (raw == null) throw new ValueFormatException();
        if (raw.isEmpty()) return NULL();
        boolean anti = false;
        int     i    = raw.indexOf('!');
        if (i > -1) {
            raw = raw.substring(i);
            anti = true;
        }
        return create(CustomType.parse(valClass, raw), PlusMinusType.create(anti));
    }

    public ConditionalValType<T> createType(CustomType<?> value, PlusMinusType isAnti) {
        return (ConditionalValType<T>) create(this.value.getCastPipeline().asValue(value.getClass()), isAnti.asBoolean());
    }

    private ConditionalValType<T> createType(CustomType<?> value, boolean isAnti) {
        return (ConditionalValType<T>) create(this.value.getCastPipeline().asValue(value.getClass()), isAnti);
    }

    @Override public String toString() { return anti ? '!' + value.toString() : value.toString(); }
    @Override public int hashCode()    { return Objects.hash(super.hashCode(), anti); }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") @Override
    public boolean equals(Object o) {
        return MouseUtils.Equator
                .<ConditionalValType<T>, T>equaling(ConditionalValType::getValue)
                .thenEqualing(ConditionalValType::isAnti)
                .equals(getClass(), this, o);
    }

    @Override
    public int compareTo(ConditionalValType<T> o) {
        int result = ((CustomType) value).compareTo(o.getValue());
        if (result == 0 && anti) result = -1;
        return result;
    }
}