/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype.values;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.OtherType;
import ru.mousecray.mousecore.api.utils.MouseLambdas;
import ru.mousecray.mousecore.api.utils.MouseUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.BiPredicate;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public final class ConditionType<T1, T2> extends OtherType<String> {
    public static <TYPE1, TYPE2> ConditionType<TYPE1, TYPE2> ANY(String displayName) {
        return new ConditionType<>(MouseLambdas::ANY, displayName);
    }

    public static <TYPE1, TYPE2> ConditionType<TYPE1, TYPE2> NONE(String displayName) {
        return new ConditionType<>(MouseLambdas::NONE, displayName);
    }

    @Nonnull private final BiPredicate<T1, T2> predicate;

    private ConditionType(BiPredicate<T1, T2> predicate, String displayName) {
        super(displayName);
        this.predicate = predicate;
    }

    public static <T1, T2> ConditionType<T1, T2> create(BiPredicate<T1, T2> predicate, String displayName) {
        return new ConditionType<>(predicate, displayName);
    }

    public boolean test(@Nullable T1 t1, @Nullable T2 t2) { return predicate.test(t1, t2); }

    public BiPredicate<T1, T2> getPredicate()             { return predicate; }
    public String getDisplayName()                        { return value; }

    @SuppressWarnings("unchecked") @Override
    public ConditionType<T1, T2> createType(Object value) {
        if (value instanceof ConditionType) {
            try {
                ConditionType<T1, T2> val = (ConditionType<T1, T2>) value;
                return create(val.predicate, val.value);
            } catch (ClassCastException ignore) { }
        }
        return NONE(this.value);
    }

    @Override public String toString() { return value; }
    @Override public int hashCode()    { return Objects.hash(predicate, value); }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") @Override
    public boolean equals(Object o) {
        return MouseUtils.Equator
                .<ConditionType<T1, T2>, String>equaling(ConditionType::getDisplayName)
                .thenEqualing(ConditionType::getPredicate)
                .equals(getClass(), this, o);
    }
}
