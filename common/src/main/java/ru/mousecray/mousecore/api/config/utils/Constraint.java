/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.api.config.utils;

import ru.mousecray.mousecore.api.customtype.*;
import ru.mousecray.mousecore.api.customtype.range.RangeContainer;

import javax.annotation.Nullable;

public interface Constraint<T extends CustomType<?>> {
    ConstraintType getType();
    ConstraintConditionType getConditionType();
    boolean hasSpecific();
    @Nullable String getPath();
    CustomValType getValType();
    @Nullable T getVal();
    @Nullable RangeContainer<?> getRange();


    enum ConstraintType {DISABLED, RELATION}

    enum ConstraintConditionType {DISABLED, LESS, MORE, EQUAL, NOT_EQUAL, LESS_OR_EQUAL, MORE_OR_EQUAL, IN_RANGE}

    interface Builder {
        static Builder create() { return ConfigTypeFactory.getInstance().CONSTRAINT_BUILDER(); }

        DisabledBuilder setDisabledType(String path);
        RelationBuilder setRelationType(String path);

        interface DisabledBuilder {
            DisabledBuilderLogical setLogicalType();
            DisabledBuilderNumber setNumberType();
            DisabledBuilderOther setOtherType();
        }

        interface DisabledBuilderLogical {
            <T extends LogicalType<?>> Constraint<T> setConditionDisabled();
            <T extends LogicalType<?>> Constraint<T> setConditionEqual();
            <T extends LogicalType<?>> Constraint<T> setConditionNotEqual();
            <T extends LogicalType<?>> Constraint<T> setConditionEqual(T val);
            <T extends LogicalType<?>> Constraint<T> setConditionNotEqual(T val);
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

        interface DisabledBuilderOther {
            <T extends OtherType<?>> Constraint<T> setConditionDisabled();
            <T extends OtherType<?>> Constraint<T> setConditionEqual();
            <T extends OtherType<?>> Constraint<T> setConditionNotEqual();
            <T extends OtherType<?>> Constraint<T> setConditionEqual(T val);
            <T extends OtherType<?>> Constraint<T> setConditionNotEqual(T val);
        }

        interface RelationBuilder {
            RelationBuilderLogical setLogicalType();
            RelationBuilderNumber setNumberType();
            RelationBuilderOther setOtherType();
        }

        interface RelationBuilderLogical {
            <T extends LogicalType<?>> Constraint<T> setConditionEqual();
            <T extends LogicalType<?>> Constraint<T> setConditionNotEqual();
        }

        interface RelationBuilderNumber {
            <T extends NumberType<?>> Constraint<T> setConditionEqual();
            <T extends NumberType<?>> Constraint<T> setConditionNotEqual();
            <T extends NumberType<?>> Constraint<T> setConditionLess();
            <T extends NumberType<?>> Constraint<T> setConditionLessOrEqual();
            <T extends NumberType<?>> Constraint<T> setConditionMore();
            <T extends NumberType<?>> Constraint<T> setConditionMoreOrEqual();
        }

        interface RelationBuilderOther {
            <T extends OtherType<?>> Constraint<T> setConditionEqual();
            <T extends OtherType<?>> Constraint<T> setConditionNotEqual();
        }
    }
}