/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.core.config.utils;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.customtype.*;
import ru.mousecray.mousecore.api.customtype.range.RangeContainer;
import ru.mousecray.mousecore.api.utils.MouseStrings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConstraintImpl<T extends CustomType<?>> implements Constraint<T> {
    @Nonnull private final  ConstraintType          type;
    @Nonnull private final  ConstraintConditionType relationType;
    @Nullable private final String                  path;
    @Nonnull private final  CustomValType           customValType;
    @Nullable private final T                       val;
    @Nullable private final RangeContainer<?>       range;

    private ConstraintImpl(
            ConstraintType type, ConstraintConditionType relationType,
            @Nullable String path, CustomValType customValType, @Nullable T val, @Nullable RangeContainer<?> range
    ) {
        this.type = Objects.requireNonNull(type);
        this.relationType = Objects.requireNonNull(relationType);
        path = MouseStrings.trimWith(path, true, '\t');
        if (path != null) {
            if (path.isEmpty()) throw new IllegalArgumentException("path cannot be empty");
            if (path.contains("\t")) throw new IllegalArgumentException("path cannot contains tabs");
        }
        this.path = path;
        this.customValType = Objects.requireNonNull(customValType);
        this.val = val;
        this.range = range;
    }

    @Override public ConstraintType getType()                   { return type; }
    @Override public ConstraintConditionType getConditionType() { return relationType; }
    @Override public boolean hasSpecific()                      { return val != null || range != null; }
    @Nullable @Override public String getPath()                 { return path; }
    @Override public CustomValType getValType()                 { return customValType; }
    @Nullable @Override public T getVal()                       { return val; }
    @Nullable @Override public RangeContainer<?> getRange()     { return range; }

    @Override
    public String toString() {
        return "ConstraintImpl{" +
                "type=" + type +
                ", relationType=" + relationType +
                ", path='" + path + '\'' +
                ", customValType=" + customValType +
                ", val=" + val +
                ", range=" + range +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConstraintImpl value)) return false;
        return type == value.type && relationType == value.relationType
                && Objects.equals(path, value.path) && Objects.equals(customValType, value.customValType)
                && Objects.equals(val, value.val) && Objects.equals(range, value.range);
    }

    @Override public int hashCode() { return Objects.hash(type, relationType, path, customValType, val, range); }

    public static final class BuilderImpl implements Constraint.Builder {
        private ConstraintType type;
        private String         path;

        BuilderImpl() { }

        @Override public DisabledBuilderImpl setDisabledType(String path) {
            this.path = Objects.requireNonNull(path);
            type = ConstraintType.DISABLED;
            return new DisabledBuilderImpl();
        }

        @Override public RelationBuilderImpl setRelationType(String path) {
            this.path = Objects.requireNonNull(path);
            type = ConstraintType.RELATION;
            return new RelationBuilderImpl();
        }

        public final class DisabledBuilderImpl implements Constraint.Builder.DisabledBuilder {
            private DisabledBuilderImpl() { }

            @Override
            public DisabledBuilderLogicalImpl setLogicalType() { return new DisabledBuilderLogicalImpl(); }
            @Override
            public DisabledBuilderNumberImpl setNumberType() { return new DisabledBuilderNumberImpl(); }
            @Override
            public DisabledBuilderOtherImpl setOtherType() { return new DisabledBuilderOtherImpl(); }
        }

        public final class DisabledBuilderLogicalImpl implements Constraint.Builder.DisabledBuilderLogical {
            private DisabledBuilderLogicalImpl() { }

            @Override public <T extends LogicalType<?>> Constraint<T> setConditionDisabled() {
                return new ConstraintImpl<>(
                        type, ConstraintConditionType.DISABLED,
                        path, CustomValType.LOGICAL, null, null
                );
            }

            @Override public <T extends LogicalType<?>> Constraint<T> setConditionEqual() {
                return new ConstraintImpl<>(
                        type, ConstraintConditionType.EQUAL,
                        path, CustomValType.LOGICAL, null, null
                );
            }

            @Override public <T extends LogicalType<?>> Constraint<T> setConditionNotEqual() {
                return new ConstraintImpl<>(
                        type, ConstraintConditionType.NOT_EQUAL,
                        path, CustomValType.LOGICAL, null, null
                );
            }

            @Override public <T extends LogicalType<?>> Constraint<T> setConditionEqual(T val) {
                return new ConstraintImpl<>(
                        type, ConstraintConditionType.EQUAL,
                        path, CustomValType.LOGICAL, Objects.requireNonNull(val), null
                );
            }

            @Override public <T extends LogicalType<?>> Constraint<T> setConditionNotEqual(T val) {
                return new ConstraintImpl<>(
                        type, ConstraintConditionType.NOT_EQUAL,
                        path, CustomValType.LOGICAL, Objects.requireNonNull(val), null
                );
            }
        }

        public final class DisabledBuilderNumberImpl implements Constraint.Builder.DisabledBuilderNumber {
            private DisabledBuilderNumberImpl() { }

            @Override public <T extends NumberType<?>> Constraint<T> setConditionDisabled() {
                return new ConstraintImpl<>(
                        type, ConstraintConditionType.DISABLED,
                        path, CustomValType.NUMBER, null, null
                );
            }

            @Override public <T extends NumberType<?>> Constraint<T> setConditionEqual() {
                return new ConstraintImpl<>(
                        type, ConstraintConditionType.EQUAL,
                        path, CustomValType.NUMBER, null, null
                );
            }

            @Override public <T extends NumberType<?>> Constraint<T> setConditionNotEqual() {
                return new ConstraintImpl<>(
                        type, ConstraintConditionType.NOT_EQUAL,
                        path, CustomValType.NUMBER, null, null
                );
            }

            @Override public <T extends NumberType<?>> Constraint<T> setConditionEqual(T val) {
                return new ConstraintImpl<>(
                        type, ConstraintConditionType.EQUAL,
                        path, CustomValType.NUMBER, Objects.requireNonNull(val), null
                );
            }

            @Override public <T extends NumberType<?>> Constraint<T> setConditionNotEqual(T val) {
                return new ConstraintImpl<>(
                        type, ConstraintConditionType.NOT_EQUAL,
                        path, CustomValType.NUMBER, Objects.requireNonNull(val), null
                );
            }

            @Override public <T extends NumberType<?>> Constraint<T> setConditionLess() {
                return new ConstraintImpl<>(
                        type, ConstraintConditionType.LESS,
                        path, CustomValType.NUMBER, null, null
                );
            }

            @Override public <T extends NumberType<?>> Constraint<T> setConditionLessOrEqual() {
                return new ConstraintImpl<>(
                        type, ConstraintConditionType.LESS_OR_EQUAL,
                        path, CustomValType.NUMBER, null, null
                );
            }

            @Override public <T extends NumberType<?>> Constraint<T> setConditionMore() {
                return new ConstraintImpl<>(
                        type, ConstraintConditionType.MORE,
                        path, CustomValType.NUMBER, null, null
                );
            }

            @Override public <T extends NumberType<?>> Constraint<T> setConditionMoreOrEqual() {
                return new ConstraintImpl<>(
                        type, ConstraintConditionType.MORE_OR_EQUAL,
                        path, CustomValType.NUMBER, null, null
                );
            }

            @Override public <T extends NumberType<?>> Constraint<T> setConditionLess(T val) {
                return new ConstraintImpl<>(
                        type, ConstraintConditionType.LESS,
                        path, CustomValType.NUMBER, Objects.requireNonNull(val), null
                );
            }

            @Override public <T extends NumberType<?>> Constraint<T> setConditionLessOrEqual(T val) {
                return new ConstraintImpl<>(
                        type, ConstraintConditionType.LESS_OR_EQUAL,
                        path, CustomValType.NUMBER, Objects.requireNonNull(val), null
                );
            }

            @Override public <T extends NumberType<?>> Constraint<T> setConditionMore(T val) {
                return new ConstraintImpl<>(
                        type, ConstraintConditionType.MORE,
                        path, CustomValType.NUMBER, Objects.requireNonNull(val), null
                );
            }

            @Override public <T extends NumberType<?>> Constraint<T> setConditionMoreOrEqual(T val) {
                return new ConstraintImpl<>(
                        type, ConstraintConditionType.MORE_OR_EQUAL,
                        path, CustomValType.NUMBER, Objects.requireNonNull(val), null
                );
            }

            @Override public <T extends NumberType<?>> Constraint<T> setConditionRange(RangeContainer<T> range) {
                return new ConstraintImpl<>(
                        type, ConstraintConditionType.IN_RANGE,
                        path, CustomValType.NUMBER, null, Objects.requireNonNull(range)
                );
            }
        }

        public final class DisabledBuilderOtherImpl implements Constraint.Builder.DisabledBuilderOther {
            private DisabledBuilderOtherImpl() { }

            @Override public <T extends OtherType<?>> Constraint<T> setConditionDisabled() {
                return new ConstraintImpl<>(
                        type, ConstraintConditionType.DISABLED,
                        path, CustomValType.OTHER, null, null
                );
            }

            @Override public <T extends OtherType<?>> Constraint<T> setConditionEqual() {
                return new ConstraintImpl<>(
                        type, ConstraintConditionType.EQUAL,
                        path, CustomValType.OTHER, null, null
                );
            }

            @Override public <T extends OtherType<?>> Constraint<T> setConditionNotEqual() {
                return new ConstraintImpl<>(
                        type, ConstraintConditionType.NOT_EQUAL,
                        path, CustomValType.OTHER, null, null
                );
            }

            @Override public <T extends OtherType<?>> Constraint<T> setConditionEqual(T val) {
                return new ConstraintImpl<>(
                        type, ConstraintConditionType.EQUAL,
                        path, CustomValType.OTHER, Objects.requireNonNull(val), null
                );
            }

            @Override public <T extends OtherType<?>> Constraint<T> setConditionNotEqual(T val) {
                return new ConstraintImpl<>(
                        type, ConstraintConditionType.NOT_EQUAL,
                        path, CustomValType.OTHER, Objects.requireNonNull(val), null
                );
            }
        }

        public final class RelationBuilderImpl implements Constraint.Builder.RelationBuilder {
            private RelationBuilderImpl()                                { }

            @Override public RelationBuilderLogicalImpl setLogicalType() { return new RelationBuilderLogicalImpl(); }
            @Override public RelationBuilderNumberImpl setNumberType()   { return new RelationBuilderNumberImpl(); }
            @Override public RelationBuilderOtherImpl setOtherType()     { return new RelationBuilderOtherImpl(); }
        }

        public final class RelationBuilderLogicalImpl implements Constraint.Builder.RelationBuilderLogical {
            private RelationBuilderLogicalImpl() { }

            @Override public <T extends LogicalType<?>> Constraint<T> setConditionEqual() {
                return new ConstraintImpl<>(
                        type, ConstraintConditionType.EQUAL,
                        path, CustomValType.LOGICAL, null, null
                );
            }

            @Override public <T extends LogicalType<?>> Constraint<T> setConditionNotEqual() {
                return new ConstraintImpl<>(
                        type, ConstraintConditionType.NOT_EQUAL,
                        path, CustomValType.LOGICAL, null, null
                );
            }
        }

        public final class RelationBuilderNumberImpl implements Constraint.Builder.RelationBuilderNumber {
            private RelationBuilderNumberImpl() { }

            @Override public <T extends NumberType<?>> Constraint<T> setConditionEqual() {
                return new ConstraintImpl<>(
                        type, ConstraintConditionType.EQUAL,
                        path, CustomValType.NUMBER, null, null
                );
            }

            @Override public <T extends NumberType<?>> Constraint<T> setConditionNotEqual() {
                return new ConstraintImpl<>(
                        type, ConstraintConditionType.NOT_EQUAL,
                        path, CustomValType.NUMBER, null, null
                );
            }

            @Override public <T extends NumberType<?>> Constraint<T> setConditionLess() {
                return new ConstraintImpl<>(
                        type, ConstraintConditionType.LESS,
                        path, CustomValType.NUMBER, null, null
                );
            }

            @Override public <T extends NumberType<?>> Constraint<T> setConditionLessOrEqual() {
                return new ConstraintImpl<>(
                        type, ConstraintConditionType.LESS_OR_EQUAL,
                        path, CustomValType.NUMBER, null, null
                );
            }

            @Override public <T extends NumberType<?>> Constraint<T> setConditionMore() {
                return new ConstraintImpl<>(
                        type, ConstraintConditionType.MORE,
                        path, CustomValType.NUMBER, null, null
                );
            }

            @Override public <T extends NumberType<?>> Constraint<T> setConditionMoreOrEqual() {
                return new ConstraintImpl<>(
                        type, ConstraintConditionType.MORE_OR_EQUAL,
                        path, CustomValType.NUMBER, null, null
                );
            }
        }

        public final class RelationBuilderOtherImpl implements Constraint.Builder.RelationBuilderOther {
            private RelationBuilderOtherImpl() { }

            @Override public <T extends OtherType<?>> Constraint<T> setConditionEqual() {
                return new ConstraintImpl<>(
                        type, ConstraintConditionType.EQUAL,
                        path, CustomValType.OTHER, null, null
                );
            }

            @Override public <T extends OtherType<?>> Constraint<T> setConditionNotEqual() {
                return new ConstraintImpl<>(
                        type, ConstraintConditionType.NOT_EQUAL,
                        path, CustomValType.OTHER, null, null
                );
            }
        }
    }
}