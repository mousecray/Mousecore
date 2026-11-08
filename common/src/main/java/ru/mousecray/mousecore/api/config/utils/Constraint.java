/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.config.utils;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.*;
import ru.mousecray.mousecore.api.customtype.range.RangeContainer;
import ru.mousecray.mousecore.api.utils.MouseStrings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class Constraint<T extends CustomType<?>> {
    @Nonnull private final  ConstraintType          type;
    @Nonnull private final  ConstraintConditionType relationType;
    @Nullable private final String                  path;
    @Nonnull private final  CustomValType           customValType;
    @Nullable private final T                       val;
    @Nullable private final RangeContainer<?>       range;

    private Constraint(
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

    public ConstraintType getType()                   { return type; }
    public ConstraintConditionType getConditionType() { return relationType; }
    public boolean hasSpecific()                      { return val != null || range != null; }
    @Nullable public String getPath()                 { return path; }
    public CustomValType getValType()                 { return customValType; }
    @Nullable public T getVal()                       { return val; }
    @Nullable public RangeContainer<?> getRange()     { return range; }

    @Override
    public String toString() {
        return "Constraint{" +
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
        if (!(o instanceof Constraint)) return false;
        Constraint value = (Constraint) o;
        return type == value.type && relationType == value.relationType
                && Objects.equals(path, value.path) && Objects.equals(customValType, value.customValType)
                && Objects.equals(val, value.val) && Objects.equals(range, value.range);
    }

    @Override public int hashCode() { return Objects.hash(type, relationType, path, customValType, val, range); }

    public enum ConstraintType {DISABLED, RELATION}

    public enum ConstraintConditionType {DISABLED, LESS, MORE, EQUAL, NOT_EQUAL, LESS_OR_EQUAL, MORE_OR_EQUAL, IN_RANGE}

    public static final class Builder {
        private ConstraintType type;
        private String         path;

        private Builder()              { }
        public static Builder create() { return new Builder(); }

        public DisabledBuilder setDisabledType(String path) {
            this.path = Objects.requireNonNull(path);
            type = ConstraintType.DISABLED;
            return new DisabledBuilder();
        }

        public RelationBuilder setRelationType(String path) {
            this.path = Objects.requireNonNull(path);
            type = ConstraintType.RELATION;
            return new RelationBuilder();
        }

        public final class DisabledBuilder {
            private DisabledBuilder()                      { }

            public DisabledBuilderLogical setLogicalType() { return new DisabledBuilderLogical(); }
            public DisabledBuilderNumber setNumberType()   { return new DisabledBuilderNumber(); }
            public DisabledBuilderOther setOtherType()     { return new DisabledBuilderOther(); }
        }

        public final class DisabledBuilderLogical {
            private DisabledBuilderLogical() { }

            public <T extends LogicalType<?>> Constraint<T> setConditionDisabled() {
                return new Constraint<>(
                        type, ConstraintConditionType.DISABLED,
                        path, CustomValType.LOGICAL, null, null
                );
            }

            public <T extends LogicalType<?>> Constraint<T> setConditionEqual() {
                return new Constraint<>(
                        type, ConstraintConditionType.EQUAL,
                        path, CustomValType.LOGICAL, null, null
                );
            }

            public <T extends LogicalType<?>> Constraint<T> setConditionNotEqual() {
                return new Constraint<>(
                        type, ConstraintConditionType.NOT_EQUAL,
                        path, CustomValType.LOGICAL, null, null
                );
            }

            public <T extends LogicalType<?>> Constraint<T> setConditionEqual(@Nonnull T val) {
                return new Constraint<>(
                        type, ConstraintConditionType.EQUAL,
                        path, CustomValType.LOGICAL, Objects.requireNonNull(val), null
                );
            }

            public <T extends LogicalType<?>> Constraint<T> setConditionNotEqual(@Nonnull T val) {
                return new Constraint<>(
                        type, ConstraintConditionType.NOT_EQUAL,
                        path, CustomValType.LOGICAL, Objects.requireNonNull(val), null
                );
            }
        }

        public final class DisabledBuilderNumber {
            private DisabledBuilderNumber() { }

            public <T extends NumberType<?>> Constraint<T> setConditionDisabled() {
                return new Constraint<>(
                        type, ConstraintConditionType.DISABLED,
                        path, CustomValType.NUMBER, null, null
                );
            }

            public <T extends NumberType<?>> Constraint<T> setConditionEqual() {
                return new Constraint<>(
                        type, ConstraintConditionType.EQUAL,
                        path, CustomValType.NUMBER, null, null
                );
            }

            public <T extends NumberType<?>> Constraint<T> setConditionNotEqual() {
                return new Constraint<>(
                        type, ConstraintConditionType.NOT_EQUAL,
                        path, CustomValType.NUMBER, null, null
                );
            }

            public <T extends NumberType<?>> Constraint<T> setConditionEqual(@Nonnull T val) {
                return new Constraint<>(
                        type, ConstraintConditionType.EQUAL,
                        path, CustomValType.NUMBER, Objects.requireNonNull(val), null
                );
            }

            public <T extends NumberType<?>> Constraint<T> setConditionNotEqual(@Nonnull T val) {
                return new Constraint<>(
                        type, ConstraintConditionType.NOT_EQUAL,
                        path, CustomValType.NUMBER, Objects.requireNonNull(val), null
                );
            }

            public <T extends NumberType<?>> Constraint<T> setConditionLess() {
                return new Constraint<>(
                        type, ConstraintConditionType.LESS,
                        path, CustomValType.NUMBER, null, null
                );
            }

            public <T extends NumberType<?>> Constraint<T> setConditionLessOrEqual() {
                return new Constraint<>(
                        type, ConstraintConditionType.LESS_OR_EQUAL,
                        path, CustomValType.NUMBER, null, null
                );
            }

            public <T extends NumberType<?>> Constraint<T> setConditionMore() {
                return new Constraint<>(
                        type, ConstraintConditionType.MORE,
                        path, CustomValType.NUMBER, null, null
                );
            }

            public <T extends NumberType<?>> Constraint<T> setConditionMoreOrEqual() {
                return new Constraint<>(
                        type, ConstraintConditionType.MORE_OR_EQUAL,
                        path, CustomValType.NUMBER, null, null
                );
            }

            public <T extends NumberType<?>> Constraint<T> setConditionLess(@Nonnull T val) {
                return new Constraint<>(
                        type, ConstraintConditionType.LESS,
                        path, CustomValType.NUMBER, Objects.requireNonNull(val), null
                );
            }

            public <T extends NumberType<?>> Constraint<T> setConditionLessOrEqual(@Nonnull T val) {
                return new Constraint<>(
                        type, ConstraintConditionType.LESS_OR_EQUAL,
                        path, CustomValType.NUMBER, Objects.requireNonNull(val), null
                );
            }

            public <T extends NumberType<?>> Constraint<T> setConditionMore(@Nonnull T val) {
                return new Constraint<>(
                        type, ConstraintConditionType.MORE,
                        path, CustomValType.NUMBER, Objects.requireNonNull(val), null
                );
            }

            public <T extends NumberType<?>> Constraint<T> setConditionMoreOrEqual(@Nonnull T val) {
                return new Constraint<>(
                        type, ConstraintConditionType.MORE_OR_EQUAL,
                        path, CustomValType.NUMBER, Objects.requireNonNull(val), null
                );
            }

            public <T extends NumberType<?>> Constraint<T> setConditionRange(@Nonnull RangeContainer<T> range) {
                return new Constraint<>(
                        type, ConstraintConditionType.IN_RANGE,
                        path, CustomValType.NUMBER, null, Objects.requireNonNull(range)
                );
            }
        }

        public final class DisabledBuilderOther {
            private DisabledBuilderOther() { }

            public <T extends OtherType<?>> Constraint<T> setConditionDisabled() {
                return new Constraint<>(
                        type, ConstraintConditionType.DISABLED,
                        path, CustomValType.OTHER, null, null
                );
            }

            public <T extends OtherType<?>> Constraint<T> setConditionEqual() {
                return new Constraint<>(
                        type, ConstraintConditionType.EQUAL,
                        path, CustomValType.OTHER, null, null
                );
            }

            public <T extends OtherType<?>> Constraint<T> setConditionNotEqual() {
                return new Constraint<>(
                        type, ConstraintConditionType.NOT_EQUAL,
                        path, CustomValType.OTHER, null, null
                );
            }

            public <T extends OtherType<?>> Constraint<T> setConditionEqual(@Nonnull T val) {
                return new Constraint<>(
                        type, ConstraintConditionType.EQUAL,
                        path, CustomValType.OTHER, Objects.requireNonNull(val), null
                );
            }

            public <T extends OtherType<?>> Constraint<T> setConditionNotEqual(@Nonnull T val) {
                return new Constraint<>(
                        type, ConstraintConditionType.NOT_EQUAL,
                        path, CustomValType.OTHER, Objects.requireNonNull(val), null
                );
            }
        }

        public final class RelationBuilder {
            private RelationBuilder()                      { }

            public RelationBuilderLogical setLogicalType() { return new RelationBuilderLogical(); }
            public RelationBuilderNumber setNumberType()   { return new RelationBuilderNumber(); }
            public RelationBuilderOther setOtherType()     { return new RelationBuilderOther(); }
        }

        public final class RelationBuilderLogical {
            private RelationBuilderLogical() { }

            public <T extends LogicalType<?>> Constraint<T> setConditionEqual() {
                return new Constraint<>(
                        type, ConstraintConditionType.EQUAL,
                        path, CustomValType.LOGICAL, null, null
                );
            }

            public <T extends LogicalType<?>> Constraint<T> setConditionNotEqual() {
                return new Constraint<>(
                        type, ConstraintConditionType.NOT_EQUAL,
                        path, CustomValType.LOGICAL, null, null
                );
            }
        }

        public final class RelationBuilderNumber {
            private RelationBuilderNumber() { }

            public <T extends NumberType<?>> Constraint<T> setConditionEqual() {
                return new Constraint<>(
                        type, ConstraintConditionType.EQUAL,
                        path, CustomValType.NUMBER, null, null
                );
            }

            public <T extends NumberType<?>> Constraint<T> setConditionNotEqual() {
                return new Constraint<>(
                        type, ConstraintConditionType.NOT_EQUAL,
                        path, CustomValType.NUMBER, null, null
                );
            }

            public <T extends NumberType<?>> Constraint<T> setConditionLess() {
                return new Constraint<>(
                        type, ConstraintConditionType.LESS,
                        path, CustomValType.NUMBER, null, null
                );
            }

            public <T extends NumberType<?>> Constraint<T> setConditionLessOrEqual() {
                return new Constraint<>(
                        type, ConstraintConditionType.LESS_OR_EQUAL,
                        path, CustomValType.NUMBER, null, null
                );
            }

            public <T extends NumberType<?>> Constraint<T> setConditionMore() {
                return new Constraint<>(
                        type, ConstraintConditionType.MORE,
                        path, CustomValType.NUMBER, null, null
                );
            }

            public <T extends NumberType<?>> Constraint<T> setConditionMoreOrEqual() {
                return new Constraint<>(
                        type, ConstraintConditionType.MORE_OR_EQUAL,
                        path, CustomValType.NUMBER, null, null
                );
            }
        }

        public final class RelationBuilderOther {
            private RelationBuilderOther() { }

            public <T extends OtherType<?>> Constraint<T> setConditionEqual() {
                return new Constraint<>(
                        type, ConstraintConditionType.EQUAL,
                        path, CustomValType.OTHER, null, null
                );
            }

            public <T extends OtherType<?>> Constraint<T> setConditionNotEqual() {
                return new Constraint<>(
                        type, ConstraintConditionType.NOT_EQUAL,
                        path, CustomValType.OTHER, null, null
                );
            }
        }
    }
}