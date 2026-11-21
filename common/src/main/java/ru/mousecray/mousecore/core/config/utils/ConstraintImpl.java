/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.core.config.utils;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.customtype.*;
import ru.mousecray.mousecore.api.customtype.range.RangeContainer;
import ru.mousecray.mousecore.api.customtype.type.ValTypeCategory;
import ru.mousecray.mousecore.api.customtype.values.logic.ConditionType;
import ru.mousecray.mousecore.api.customtype.values.other.*;
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
    @Nonnull private final  ValTypeCategory         valTypeCategory;
    @Nullable private final T                       val;
    @Nullable private final RangeContainer<?>       range;

    private ConstraintImpl(
            ConstraintType type, ConstraintConditionType relationType,
            @Nullable String path, ValTypeCategory valTypeCategory, @Nullable T val, @Nullable RangeContainer<?> range
    ) {
        this.type = Objects.requireNonNull(type);
        this.relationType = Objects.requireNonNull(relationType);
        path = MouseStrings.trimWith(path, true, '\t');
        if (path != null) {
            if (path.isEmpty()) throw new IllegalArgumentException("path cannot be empty");
            if (path.contains("\t")) throw new IllegalArgumentException("path cannot contains tabs");
        }
        this.path = path;
        this.valTypeCategory = Objects.requireNonNull(valTypeCategory);
        if (val != null && val.getValType().getCategory() != valTypeCategory) {
            throw new IllegalArgumentException("Constraint value category mismatch: expected " + valTypeCategory
                    + ", got " + val.getValType().getCategory());
        }
        this.val = val;
        this.range = range;
    }

    @Override public ConstraintType getType()                   { return type; }
    @Override public ConstraintConditionType getConditionType() { return relationType; }
    @Override public boolean hasSpecific()                      { return val != null || range != null; }
    @Nullable @Override public String getPath()                 { return path; }
    @Override public ValTypeCategory getValTypeCategory()       { return valTypeCategory; }
    @Nullable @Override public T getVal()                       { return val; }
    @Nullable @Override public RangeContainer<?> getRange()     { return range; }

    @Override
    public String toString() {
        return "ConstraintImpl{" +
                "type=" + type +
                ", relationType=" + relationType +
                ", path='" + path + '\'' +
                ", valType=" + valTypeCategory +
                ", val=" + val +
                ", range=" + range +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConstraintImpl<?> value)) return false;
        return type == value.type && relationType == value.relationType
                && Objects.equals(path, value.path) && Objects.equals(valTypeCategory, value.valTypeCategory)
                && Objects.equals(val, value.val) && Objects.equals(range, value.range);
    }

    @Override public int hashCode() { return Objects.hash(type, relationType, path, valTypeCategory, val, range); }

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
            private DisabledBuilderImpl()                                                    { }

            @Override
            public DisabledBuilderEquality<LogicalType<?>> setLogicalType()                  { return new DisabledBuilderEqualityImpl<>(ValTypeCategory.LOGICAL); }
            @Override public DisabledBuilderNumberImpl setNumberType()                       { return new DisabledBuilderNumberImpl(); }
            @Override
            public DisabledBuilderEquality<StringType> setStringType()                       { return new DisabledBuilderEqualityImpl<>(ValTypeCategory.STRING); }
            @Override public DisabledBuilderEquality<EnumType<?>> setEnumType()              { return new DisabledBuilderEqualityImpl<>(ValTypeCategory.ENUM); }
            @Override
            public DisabledBuilderEquality<ColorType> setColorType()                         { return new DisabledBuilderEqualityImpl<>(ValTypeCategory.COLOR); }
            @Override
            public DisabledBuilderEquality<Vector3Type<?, ?>> setVectorType()                { return new DisabledBuilderEqualityImpl<>(ValTypeCategory.VECTOR); }
            @Override
            public DisabledBuilderEquality<RegexType> setPatternType()                       { return new DisabledBuilderEqualityImpl<>(ValTypeCategory.PATTERN); }
            @Override
            public DisabledBuilderEquality<ExpressionType> setExpressionType()               { return new DisabledBuilderEqualityImpl<>(ValTypeCategory.EXPRESSION); }
            @Override
            public DisabledBuilderEquality<OtherType<?>> setMinecraftType()                  { return new DisabledBuilderEqualityImpl<>(ValTypeCategory.MINECRAFT); }
            @Override
            public DisabledBuilderEquality<CustomType<?>> setContainerType()                 { return new DisabledBuilderEqualityImpl<>(ValTypeCategory.CONTAINER); }
            @Override
            public DisabledBuilderEquality<ConditionType<?, ?>> setConditionType()           { return new DisabledBuilderEqualityImpl<>(ValTypeCategory.CONDITION); }
            @Override
            public DisabledBuilderEquality<OtherType<?>> setOtherType()                      { return new DisabledBuilderEqualityImpl<>(ValTypeCategory.OTHER); }
        }

        public final class DisabledBuilderEqualityImpl<BASE extends CustomType<?>> implements Constraint.Builder.DisabledBuilderEquality<BASE> {
            private final ValTypeCategory category;

            private DisabledBuilderEqualityImpl(ValTypeCategory category) {
                this.category = category;
            }

            @Override public <T extends BASE> Constraint<T> setConditionDisabled() {
                return new ConstraintImpl<>(type, ConstraintConditionType.DISABLED, path, category, null, null);
            }

            @Override public <T extends BASE> Constraint<T> setConditionEqual() {
                return new ConstraintImpl<>(type, ConstraintConditionType.EQUAL, path, category, null, null);
            }

            @Override public <T extends BASE> Constraint<T> setConditionNotEqual() {
                return new ConstraintImpl<>(type, ConstraintConditionType.NOT_EQUAL, path, category, null, null);
            }

            @Override public <T extends BASE> Constraint<T> setConditionEqual(T val) {
                return new ConstraintImpl<>(type, ConstraintConditionType.EQUAL, path, category, Objects.requireNonNull(val), null);
            }

            @Override public <T extends BASE> Constraint<T> setConditionNotEqual(T val) {
                return new ConstraintImpl<>(type, ConstraintConditionType.NOT_EQUAL, path, category, Objects.requireNonNull(val), null);
            }
        }

        public final class DisabledBuilderNumberImpl implements Constraint.Builder.DisabledBuilderNumber {
            private DisabledBuilderNumberImpl() { }

            @Override public <T extends NumberType<?>> Constraint<T> setConditionDisabled() {
                return new ConstraintImpl<>(type, ConstraintConditionType.DISABLED, path, ValTypeCategory.NUMBER, null, null);
            }

            @Override public <T extends NumberType<?>> Constraint<T> setConditionEqual() {
                return new ConstraintImpl<>(type, ConstraintConditionType.EQUAL, path, ValTypeCategory.NUMBER, null, null);
            }

            @Override public <T extends NumberType<?>> Constraint<T> setConditionNotEqual() {
                return new ConstraintImpl<>(type, ConstraintConditionType.NOT_EQUAL, path, ValTypeCategory.NUMBER, null, null);
            }

            @Override public <T extends NumberType<?>> Constraint<T> setConditionEqual(T val) {
                return new ConstraintImpl<>(type, ConstraintConditionType.EQUAL, path, ValTypeCategory.NUMBER, Objects.requireNonNull(val), null);
            }

            @Override public <T extends NumberType<?>> Constraint<T> setConditionNotEqual(T val) {
                return new ConstraintImpl<>(type, ConstraintConditionType.NOT_EQUAL, path, ValTypeCategory.NUMBER, Objects.requireNonNull(val), null);
            }

            @Override public <T extends NumberType<?>> Constraint<T> setConditionLess() {
                return new ConstraintImpl<>(type, ConstraintConditionType.LESS, path, ValTypeCategory.NUMBER, null, null);
            }

            @Override public <T extends NumberType<?>> Constraint<T> setConditionLessOrEqual() {
                return new ConstraintImpl<>(type, ConstraintConditionType.LESS_OR_EQUAL, path, ValTypeCategory.NUMBER, null, null);
            }

            @Override public <T extends NumberType<?>> Constraint<T> setConditionMore() {
                return new ConstraintImpl<>(type, ConstraintConditionType.MORE, path, ValTypeCategory.NUMBER, null, null);
            }

            @Override public <T extends NumberType<?>> Constraint<T> setConditionMoreOrEqual() {
                return new ConstraintImpl<>(type, ConstraintConditionType.MORE_OR_EQUAL, path, ValTypeCategory.NUMBER, null, null);
            }

            @Override public <T extends NumberType<?>> Constraint<T> setConditionLess(T val) {
                return new ConstraintImpl<>(type, ConstraintConditionType.LESS, path, ValTypeCategory.NUMBER, Objects.requireNonNull(val), null);
            }

            @Override public <T extends NumberType<?>> Constraint<T> setConditionLessOrEqual(T val) {
                return new ConstraintImpl<>(type, ConstraintConditionType.LESS_OR_EQUAL, path, ValTypeCategory.NUMBER, Objects.requireNonNull(val), null);
            }

            @Override public <T extends NumberType<?>> Constraint<T> setConditionMore(T val) {
                return new ConstraintImpl<>(type, ConstraintConditionType.MORE, path, ValTypeCategory.NUMBER, Objects.requireNonNull(val), null);
            }

            @Override public <T extends NumberType<?>> Constraint<T> setConditionMoreOrEqual(T val) {
                return new ConstraintImpl<>(type, ConstraintConditionType.MORE_OR_EQUAL, path, ValTypeCategory.NUMBER, Objects.requireNonNull(val), null);
            }

            @Override public <T extends NumberType<?>> Constraint<T> setConditionRange(RangeContainer<T> range) {
                return new ConstraintImpl<>(type, ConstraintConditionType.IN_RANGE, path, ValTypeCategory.NUMBER, null, Objects.requireNonNull(range));
            }
        }

        public final class RelationBuilderImpl implements Constraint.Builder.RelationBuilder {
            private RelationBuilderImpl()                                                    { }

            @Override
            public RelationBuilderEquality<LogicalType<?>> setLogicalType()                  { return new RelationBuilderEqualityImpl<>(ValTypeCategory.LOGICAL); }
            @Override public RelationBuilderNumberImpl setNumberType()                       { return new RelationBuilderNumberImpl(); }
            @Override
            public RelationBuilderEquality<StringType> setStringType()                       { return new RelationBuilderEqualityImpl<>(ValTypeCategory.STRING); }
            @Override public RelationBuilderEquality<EnumType<?>> setEnumType()              { return new RelationBuilderEqualityImpl<>(ValTypeCategory.ENUM); }
            @Override
            public RelationBuilderEquality<ColorType> setColorType()                         { return new RelationBuilderEqualityImpl<>(ValTypeCategory.COLOR); }
            @Override
            public RelationBuilderEquality<Vector3Type<?, ?>> setVectorType()                { return new RelationBuilderEqualityImpl<>(ValTypeCategory.VECTOR); }
            @Override
            public RelationBuilderEquality<RegexType> setPatternType()                       { return new RelationBuilderEqualityImpl<>(ValTypeCategory.PATTERN); }
            @Override
            public RelationBuilderEquality<ExpressionType> setExpressionType()               { return new RelationBuilderEqualityImpl<>(ValTypeCategory.EXPRESSION); }
            @Override
            public RelationBuilderEquality<OtherType<?>> setMinecraftType()                  { return new RelationBuilderEqualityImpl<>(ValTypeCategory.MINECRAFT); }
            @Override
            public RelationBuilderEquality<CustomType<?>> setContainerType()                 { return new RelationBuilderEqualityImpl<>(ValTypeCategory.CONTAINER); }
            @Override
            public RelationBuilderEquality<ConditionType<?, ?>> setConditionType()           { return new RelationBuilderEqualityImpl<>(ValTypeCategory.CONDITION); }
            @Override
            public RelationBuilderEquality<OtherType<?>> setOtherType()                      { return new RelationBuilderEqualityImpl<>(ValTypeCategory.OTHER); }
        }

        public final class RelationBuilderEqualityImpl<BASE extends CustomType<?>> implements Constraint.Builder.RelationBuilderEquality<BASE> {
            private final ValTypeCategory category;

            private RelationBuilderEqualityImpl(ValTypeCategory category) {
                this.category = category;
            }

            @Override public <T extends BASE> Constraint<T> setConditionEqual() {
                return new ConstraintImpl<>(type, ConstraintConditionType.EQUAL, path, category, null, null);
            }

            @Override public <T extends BASE> Constraint<T> setConditionNotEqual() {
                return new ConstraintImpl<>(type, ConstraintConditionType.NOT_EQUAL, path, category, null, null);
            }
        }

        public final class RelationBuilderNumberImpl implements Constraint.Builder.RelationBuilderNumber {
            private RelationBuilderNumberImpl() { }

            @Override public <T extends NumberType<?>> Constraint<T> setConditionEqual() {
                return new ConstraintImpl<>(type, ConstraintConditionType.EQUAL, path, ValTypeCategory.NUMBER, null, null);
            }

            @Override public <T extends NumberType<?>> Constraint<T> setConditionNotEqual() {
                return new ConstraintImpl<>(type, ConstraintConditionType.NOT_EQUAL, path, ValTypeCategory.NUMBER, null, null);
            }

            @Override public <T extends NumberType<?>> Constraint<T> setConditionLess() {
                return new ConstraintImpl<>(type, ConstraintConditionType.LESS, path, ValTypeCategory.NUMBER, null, null);
            }

            @Override public <T extends NumberType<?>> Constraint<T> setConditionLessOrEqual() {
                return new ConstraintImpl<>(type, ConstraintConditionType.LESS_OR_EQUAL, path, ValTypeCategory.NUMBER, null, null);
            }

            @Override public <T extends NumberType<?>> Constraint<T> setConditionMore() {
                return new ConstraintImpl<>(type, ConstraintConditionType.MORE, path, ValTypeCategory.NUMBER, null, null);
            }

            @Override public <T extends NumberType<?>> Constraint<T> setConditionMoreOrEqual() {
                return new ConstraintImpl<>(type, ConstraintConditionType.MORE_OR_EQUAL, path, ValTypeCategory.NUMBER, null, null);
            }
        }
    }
}