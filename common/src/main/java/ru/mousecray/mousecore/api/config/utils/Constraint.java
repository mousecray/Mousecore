/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.api.config.utils;

import ru.mousecray.mousecore.api.customtype.*;
import ru.mousecray.mousecore.api.customtype.range.RangeContainer;
import ru.mousecray.mousecore.api.customtype.type.ValTypeCategory;
import ru.mousecray.mousecore.api.customtype.values.logic.ConditionType;
import ru.mousecray.mousecore.api.customtype.values.other.*;
import ru.mousecray.mousecore.api.platform.ConfigTypeFactory;

import javax.annotation.Nullable;

public interface Constraint<T extends CustomType<?>> {
    ConstraintType getType();
    ConstraintConditionType getConditionType();
    boolean hasSpecific();
    @Nullable String getPath();
    ValTypeCategory getValTypeCategory();
    @Nullable T getVal();
    @Nullable RangeContainer<?> getRange();

    enum ConstraintType {DISABLED, RELATION}

    enum ConstraintConditionType {DISABLED, LESS, MORE, EQUAL, NOT_EQUAL, LESS_OR_EQUAL, MORE_OR_EQUAL, IN_RANGE}

    interface Builder {
        static Builder create() { return ConfigTypeFactory.getInstance().CONSTRAINT_BUILDER(); }

        DisabledBuilder setDisabledType(String path);
        RelationBuilder setRelationType(String path);

        interface DisabledBuilder {
            DisabledBuilderEquality<LogicalType<?>> setLogicalType();
            DisabledBuilderNumber setNumberType();
            DisabledBuilderEquality<StringType> setStringType();
            DisabledBuilderEquality<EnumType<?>> setEnumType();
            DisabledBuilderEquality<ColorType> setColorType();
            DisabledBuilderEquality<Vector3Type<?, ?>> setVectorType();
            DisabledBuilderEquality<RegexType> setPatternType();
            DisabledBuilderEquality<ExpressionType> setExpressionType();
            DisabledBuilderEquality<OtherType<?>> setMinecraftType();
            DisabledBuilderEquality<CustomType<?>> setContainerType();
            DisabledBuilderEquality<ConditionType<?, ?>> setConditionType();
            DisabledBuilderEquality<OtherType<?>> setOtherType();
        }

        interface DisabledBuilderEquality<BASE extends CustomType<?>> {
            <T extends BASE> Constraint<T> setConditionDisabled();
            <T extends BASE> Constraint<T> setConditionEqual();
            <T extends BASE> Constraint<T> setConditionNotEqual();
            <T extends BASE> Constraint<T> setConditionEqual(T val);
            <T extends BASE> Constraint<T> setConditionNotEqual(T val);
        }

        interface DisabledBuilderNumber {
            <T extends NumberType<?>> Constraint<T> setConditionDisabled();
            <T extends NumberType<?>> Constraint<T> setConditionEqual();
            <T extends NumberType<?>> Constraint<T> setConditionNotEqual();
            <T extends NumberType<?>> Constraint<T> setConditionEqual(T val);
            <T extends NumberType<?>> Constraint<T> setConditionNotEqual(T val);
            <T extends NumberType<?>> Constraint<T> setConditionLess();
            <T extends NumberType<?>> Constraint<T> setConditionLessOrEqual();
            <T extends NumberType<?>> Constraint<T> setConditionMore();
            <T extends NumberType<?>> Constraint<T> setConditionMoreOrEqual();
            <T extends NumberType<?>> Constraint<T> setConditionLess(T val);
            <T extends NumberType<?>> Constraint<T> setConditionLessOrEqual(T val);
            <T extends NumberType<?>> Constraint<T> setConditionMore(T val);
            <T extends NumberType<?>> Constraint<T> setConditionMoreOrEqual(T val);
            <T extends NumberType<?>> Constraint<T> setConditionRange(RangeContainer<T> range);
        }

        interface RelationBuilder {
            RelationBuilderEquality<LogicalType<?>> setLogicalType();
            RelationBuilderNumber setNumberType();
            RelationBuilderEquality<StringType> setStringType();
            RelationBuilderEquality<EnumType<?>> setEnumType();
            RelationBuilderEquality<ColorType> setColorType();
            RelationBuilderEquality<Vector3Type<?, ?>> setVectorType();
            RelationBuilderEquality<RegexType> setPatternType();
            RelationBuilderEquality<ExpressionType> setExpressionType();
            RelationBuilderEquality<OtherType<?>> setMinecraftType();
            RelationBuilderEquality<CustomType<?>> setContainerType();
            RelationBuilderEquality<ConditionType<?, ?>> setConditionType();
            RelationBuilderEquality<OtherType<?>> setOtherType();
        }

        interface RelationBuilderEquality<BASE extends CustomType<?>> {
            <T extends BASE> Constraint<T> setConditionEqual();
            <T extends BASE> Constraint<T> setConditionNotEqual();
        }

        interface RelationBuilderNumber {
            <T extends NumberType<?>> Constraint<T> setConditionEqual();
            <T extends NumberType<?>> Constraint<T> setConditionNotEqual();
            <T extends NumberType<?>> Constraint<T> setConditionLess();
            <T extends NumberType<?>> Constraint<T> setConditionLessOrEqual();
            <T extends NumberType<?>> Constraint<T> setConditionMore();
            <T extends NumberType<?>> Constraint<T> setConditionMoreOrEqual();
        }
    }
}