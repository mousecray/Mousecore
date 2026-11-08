/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype.op;

import ru.mousecray.mousecore.api.customtype.*;
import ru.mousecray.mousecore.api.customtype.values.*;
import ru.mousecray.mousecore.api.utils.MouseLogic;
import ru.mousecray.mousecore.api.utils.MouseNumbers;

public class StandardOperations {

    @SuppressWarnings("unchecked") public static void registerDefaults() {
        OperationRegistry.registerCast(LogicalType.class, NumberType.class,
                source -> IntegralType.create(source.asBoolean() ? 1L : 0L));

        OperationRegistry.registerCast(NumberType.class, LogicalType.class,
                source -> PlusMinusType.create(source.asDouble() > 0));

        OperationRegistry.registerCast(CustomType.class, StringType.class,
                source -> StringType.create(source.toString()));

        OperationRegistry.registerCast(StringType.class, DecimalType.class,
                source -> CustomType.parse(DecimalType.class, source.toString()));

        OperationRegistry.registerCast(StringType.class, IntegralType.class,
                source -> CustomType.parse(IntegralType.class, source.toString()));

        OperationRegistry.registerCast(StringType.class, PercentType.class,
                source -> CustomType.parse(PercentType.class, source.toString()));

        OperationRegistry.registerCast(StringType.class, RandomQuantityType.class,
                source -> CustomType.parse(RandomQuantityType.class, source.toString()));

        OperationRegistry.registerCast(StringType.class, PlusMinusType.class,
                source -> CustomType.parse(PlusMinusType.class, source.toString()));

        OperationRegistry.registerCast(StringType.class, MinecraftItem.class,
                source -> MinecraftItem.create(source.toString()));

        OperationRegistry.registerCast(StringType.class, MinecraftBlock.class,
                source -> MinecraftBlock.create(source.toString()));

        OperationRegistry.registerCast(NumberType.class, DecimalType.class,
                source -> DecimalType.create(source.asDouble()));

        OperationRegistry.registerCast(NumberType.class, IntegralType.class,
                source -> IntegralType.create(source.asLong()));

        OperationRegistry.registerCast(NumberType.class, PercentType.class,
                source -> PercentType.create(source.asDouble()));

        OperationRegistry.registerCast(NumberType.class, RandomQuantityType.class,
                source -> RandomQuantityType.create(PercentType.create(source.asDouble()), IntegralType.NULL, IntegralType.NULL));

        OperationRegistry.registerBinary(NumberType.class, NumberType.class, ArithmeticOperator.Binary.PLUS,
                (left, right) -> left.createType(MouseNumbers.plus(left.asNumber(), right.asNumber())));
        OperationRegistry.registerBinary(NumberType.class, NumberType.class, ArithmeticOperator.Binary.MINUS,
                (left, right) -> left.createType(MouseNumbers.minus(left.asNumber(), right.asNumber())));
        OperationRegistry.registerBinary(NumberType.class, NumberType.class, ArithmeticOperator.Binary.MULTIPLY,
                (left, right) -> left.createType(MouseNumbers.multiply(left.asNumber(), right.asNumber())));
        OperationRegistry.registerBinary(NumberType.class, NumberType.class, ArithmeticOperator.Binary.DIVIDE,
                (left, right) -> left.createType(MouseNumbers.divide(left.asNumber(), right.asNumber())));
        OperationRegistry.registerBinary(NumberType.class, NumberType.class, ArithmeticOperator.Binary.MODULO,
                (left, right) -> left.createType(MouseNumbers.modulo(left.asNumber(), right.asNumber())));

        OperationRegistry.registerBinary(NumberType.class, NumberType.class, LogicalOperator.Binary.LESS,
                (left, right) -> PlusMinusType.create(MouseLogic.isLess(left.asNumber(), right.asNumber())));
        OperationRegistry.registerBinary(NumberType.class, NumberType.class, LogicalOperator.Binary.MORE,
                (left, right) -> PlusMinusType.create(MouseLogic.isMore(left.asNumber(), right.asNumber())));
        OperationRegistry.registerBinary(NumberType.class, NumberType.class, LogicalOperator.Binary.EQUAL,
                (left, right) -> PlusMinusType.create(MouseLogic.isEqual(left.asNumber(), right.asNumber())));
        OperationRegistry.registerBinary(NumberType.class, NumberType.class, LogicalOperator.Binary.LESS_OR_EQUAL,
                (left, right) -> PlusMinusType.create(MouseLogic.isLessOrEqual(left.asNumber(), right.asNumber())));
        OperationRegistry.registerBinary(NumberType.class, NumberType.class, LogicalOperator.Binary.MORE_OR_EQUAL,
                (left, right) -> PlusMinusType.create(MouseLogic.isMoreOrEqual(left.asNumber(), right.asNumber())));

        OperationRegistry.registerBinary(NumberType.class, NumberType.class, BitwiseOperator.Binary.AND,
                (left, right) -> left.createType(MouseNumbers.and(left.asNumber(), right.asNumber())));
        OperationRegistry.registerBinary(NumberType.class, NumberType.class, BitwiseOperator.Binary.OR,
                (left, right) -> left.createType(MouseNumbers.or(left.asNumber(), right.asNumber())));
        OperationRegistry.registerBinary(NumberType.class, NumberType.class, BitwiseOperator.Binary.XOR,
                (left, right) -> left.createType(MouseNumbers.xor(left.asNumber(), right.asNumber())));
        OperationRegistry.registerShift(NumberType.class, BitwiseOperator.Shift.LEFT_SHIFT,
                (target, shiftAmount) -> target.createType(MouseNumbers.leftShift(target.asNumber(), shiftAmount)));
        OperationRegistry.registerShift(NumberType.class, BitwiseOperator.Shift.RIGHT_SHIFT,
                (target, shiftAmount) -> target.createType(MouseNumbers.rightShift(target.asNumber(), shiftAmount)));
        OperationRegistry.registerShift(NumberType.class, BitwiseOperator.Shift.U_RIGHT_SHIFT,
                (target, shiftAmount) -> target.createType(MouseNumbers.uRightShift(target.asNumber(), shiftAmount)));

        OperationRegistry.registerUnary(NumberType.class, ArithmeticOperator.Unary.INVERT,
                target -> target.createType(MouseNumbers.invert(target.asNumber())));
        OperationRegistry.registerUnary(NumberType.class, ArithmeticOperator.Unary.INCREMENT,
                target -> target.createType(target.asDouble() + 1));
        OperationRegistry.registerUnary(NumberType.class, ArithmeticOperator.Unary.DECREMENT,
                target -> target.createType(target.asDouble() - 1));
        OperationRegistry.registerUnary(NumberType.class, BitwiseOperator.Unary.NOT,
                target -> target.createType(MouseNumbers.not(target.asNumber())));

        OperationRegistry.registerBinary(LogicalType.class, LogicalType.class, LogicalOperator.Binary.AND,
                (left, right) -> left.createType(MouseLogic.and(left.asBoolean(), right.asBoolean())));
        OperationRegistry.registerBinary(LogicalType.class, LogicalType.class, LogicalOperator.Binary.OR,
                (left, right) -> left.createType(MouseLogic.or(left.asBoolean(), right.asBoolean())));

        OperationRegistry.registerBinary(LogicalType.class, LogicalType.class, LogicalOperator.Binary.LESS,
                (left, right) -> PlusMinusType.create(MouseLogic.isLess(left.asBoolean(), right.asBoolean())));
        OperationRegistry.registerBinary(LogicalType.class, LogicalType.class, LogicalOperator.Binary.MORE,
                (left, right) -> PlusMinusType.create(MouseLogic.isMore(left.asBoolean(), right.asBoolean())));
        OperationRegistry.registerBinary(LogicalType.class, LogicalType.class, LogicalOperator.Binary.EQUAL,
                (left, right) -> PlusMinusType.create(MouseLogic.isEqual(left.asBoolean(), right.asBoolean())));
        OperationRegistry.registerBinary(LogicalType.class, LogicalType.class, LogicalOperator.Binary.LESS_OR_EQUAL,
                (left, right) -> PlusMinusType.create(MouseLogic.isLessOrEqual(left.asBoolean(), right.asBoolean())));
        OperationRegistry.registerBinary(LogicalType.class, LogicalType.class, LogicalOperator.Binary.MORE_OR_EQUAL,
                (left, right) -> PlusMinusType.create(MouseLogic.isMoreOrEqual(left.asBoolean(), right.asBoolean())));

        OperationRegistry.registerBinary(LogicalType.class, LogicalType.class, ArithmeticOperator.Binary.PLUS,
                (left, right) -> left.createType(MouseLogic.plus(left.asBoolean(), right.asBoolean())));
        OperationRegistry.registerBinary(LogicalType.class, LogicalType.class, ArithmeticOperator.Binary.MINUS,
                (left, right) -> left.createType(MouseLogic.minus(left.asBoolean(), right.asBoolean())));
        OperationRegistry.registerBinary(LogicalType.class, LogicalType.class, ArithmeticOperator.Binary.MULTIPLY,
                (left, right) -> left.createType(MouseLogic.multiply(left.asBoolean(), right.asBoolean())));
        OperationRegistry.registerBinary(LogicalType.class, LogicalType.class, ArithmeticOperator.Binary.DIVIDE,
                (left, right) -> left.createType(MouseLogic.divide(left.asBoolean(), right.asBoolean())));
        OperationRegistry.registerBinary(LogicalType.class, LogicalType.class, ArithmeticOperator.Binary.MODULO,
                (left, right) -> left.createType(MouseLogic.modulo(left.asBoolean(), right.asBoolean())));

        OperationRegistry.registerUnary(LogicalType.class, LogicalOperator.Unary.NOT,
                target -> target.createType(MouseLogic.invert(target.asBoolean())));
        OperationRegistry.registerUnary(LogicalType.class, ArithmeticOperator.Unary.INVERT,
                target -> target.createType(MouseLogic.invert(target.asBoolean())));
        OperationRegistry.registerUnary(LogicalType.class, ArithmeticOperator.Unary.INCREMENT,
                target -> target.createType(MouseLogic.invert(target.asBoolean())));
        OperationRegistry.registerUnary(LogicalType.class, ArithmeticOperator.Unary.DECREMENT,
                target -> target.createType(MouseLogic.invert(target.asBoolean())));


        OperationRegistry.registerBinary(OtherType.class, OtherType.class, LogicalOperator.Binary.EQUAL,
                (left, right) -> PlusMinusType.create(left.getValue().equals(right.getValue())));

        OperationRegistry.registerBinary(LogicalType.class, NumberType.class, ArithmeticOperator.Binary.PLUS,
                (left, right) -> left.createType(MouseLogic.or(left.asBoolean(), right.asDouble() > 0)));

        OperationRegistry.registerBinary(NumberType.class, LogicalType.class, ArithmeticOperator.Binary.PLUS,
                (left, right) -> left.createType(left.asDouble() + (right.asBoolean() ? 1.0 : 0.0)));

        OperationRegistry.registerUnary(NumberType.class, LogicalOperator.Unary.NOT,
                target -> PlusMinusType.create(target.asDouble() <= 0));

        for (ArithmeticOperator.Binary op : ArithmeticOperator.Binary.values()) {
            OperationRegistry.registerBinary(ConditionalValType.class, CustomType.class, op, (left, right) ->
                    left.createType((CustomType<?>) OperationRegistry.evaluateBinary(left.getValue(), right, op), left.isAnti()));
        }

        for (BitwiseOperator.Binary op : BitwiseOperator.Binary.values()) {
            OperationRegistry.registerBinary(ConditionalValType.class, CustomType.class, op, (left, right) ->
                    left.createType((CustomType<?>) OperationRegistry.evaluateBinary(left.getValue(), right, op), left.isAnti()));
        }

        OperationRegistry.registerBinary(ConditionalValType.class, CustomType.class, LogicalOperator.Binary.AND, (left, right) ->
                left.createType((CustomType<?>) OperationRegistry.evaluateBinary(left.getValue(), right, LogicalOperator.Binary.AND), left.isAnti()));
        OperationRegistry.registerBinary(ConditionalValType.class, CustomType.class, LogicalOperator.Binary.OR, (left, right) ->
                left.createType((CustomType<?>) OperationRegistry.evaluateBinary(left.getValue(), right, LogicalOperator.Binary.OR), left.isAnti()));

        OperationRegistry.registerBinary(ConditionalValType.class, CustomType.class, LogicalOperator.Binary.LESS, (left, right) ->
                (CustomType<?>) OperationRegistry.evaluateBinary(left.getValue(), right, LogicalOperator.Binary.LESS));
        OperationRegistry.registerBinary(ConditionalValType.class, CustomType.class, LogicalOperator.Binary.MORE, (left, right) ->
                (CustomType<?>) OperationRegistry.evaluateBinary(left.getValue(), right, LogicalOperator.Binary.MORE));
        OperationRegistry.registerBinary(ConditionalValType.class, CustomType.class, LogicalOperator.Binary.EQUAL, (left, right) ->
                (CustomType<?>) OperationRegistry.evaluateBinary(left.getValue(), right, LogicalOperator.Binary.EQUAL));
        OperationRegistry.registerBinary(ConditionalValType.class, CustomType.class, LogicalOperator.Binary.LESS_OR_EQUAL, (left, right) ->
                (CustomType<?>) OperationRegistry.evaluateBinary(left.getValue(), right, LogicalOperator.Binary.LESS_OR_EQUAL));
        OperationRegistry.registerBinary(ConditionalValType.class, CustomType.class, LogicalOperator.Binary.MORE_OR_EQUAL, (left, right) ->
                (CustomType<?>) OperationRegistry.evaluateBinary(left.getValue(), right, LogicalOperator.Binary.MORE_OR_EQUAL));

        for (ArithmeticOperator.Unary op : ArithmeticOperator.Unary.values()) {
            OperationRegistry.registerUnary(ConditionalValType.class, op, target ->
                    ConditionalValType.create(target.getValue(), PlusMinusType.create(!target.isAnti().asBoolean())));
        }

        OperationRegistry.registerUnary(ConditionalValType.class, LogicalOperator.Unary.NOT, target ->
                ConditionalValType.create(target.getValue(), PlusMinusType.create(!target.isAnti().asBoolean())));

        OperationRegistry.registerUnary(ConditionalValType.class, BitwiseOperator.Unary.NOT, target ->
                target.createType((CustomType<?>) OperationRegistry.evaluateUnary(target.getValue(), BitwiseOperator.Unary.NOT), target.isAnti()));

        OperationRegistry.registerBinary(ListType.class, ListType.class, LogicalOperator.Binary.EQUAL,
                (left, right) -> PlusMinusType.create(left.equals(right)));
    }
}