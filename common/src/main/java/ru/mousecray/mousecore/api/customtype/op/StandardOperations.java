/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype.op;

import ru.mousecray.mousecore.api.customtype.*;
import ru.mousecray.mousecore.api.customtype.values.collection.NBTType;
import ru.mousecray.mousecore.api.customtype.values.collection.SimpleMatrixType;
import ru.mousecray.mousecore.api.customtype.values.logic.ConditionalValType;
import ru.mousecray.mousecore.api.customtype.values.logic.PlusMinusType;
import ru.mousecray.mousecore.api.customtype.values.minecraft.MinecraftBlockType;
import ru.mousecray.mousecore.api.customtype.values.minecraft.MinecraftItemStackType;
import ru.mousecray.mousecore.api.customtype.values.minecraft.MinecraftItemType;
import ru.mousecray.mousecore.api.customtype.values.minecraft.MinecraftTagType;
import ru.mousecray.mousecore.api.customtype.values.number.*;
import ru.mousecray.mousecore.api.customtype.values.other.ColorType;
import ru.mousecray.mousecore.api.customtype.values.other.EnumType;
import ru.mousecray.mousecore.api.customtype.values.other.RegexType;
import ru.mousecray.mousecore.api.customtype.values.other.StringType;
import ru.mousecray.mousecore.api.customtype.values.vec3.BlockPosType;
import ru.mousecray.mousecore.api.customtype.values.vec3.Vector3dType;
import ru.mousecray.mousecore.api.error.UnsupportedValException;
import ru.mousecray.mousecore.api.utils.MouseLogic;
import ru.mousecray.mousecore.api.utils.MouseNumbers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StandardOperations {

    public static void registerDefaults() {
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

        OperationRegistry.registerCast(StringType.class, RangeType.class,
                source -> RangeType.parse(source.asString()));

        OperationRegistry.registerCast(StringType.class, TimeType.class,
                source -> TimeType.parse(source.asString()));

        OperationRegistry.registerCast(StringType.class, ColorType.class,
                source -> ColorType.parse(source.asString()));

        OperationRegistry.registerCast(StringType.class, RegexType.class,
                source -> RegexType.parse(source.asString()));

        OperationRegistry.registerCast(StringType.class, Vector3dType.class,
                source -> Vector3dType.parse(source.asString()));

        OperationRegistry.registerCast(StringType.class, BlockPosType.class,
                source -> BlockPosType.parse(source.asString()));

        OperationRegistry.registerCast(StringType.class, MinecraftItemType.class,
                source -> MinecraftItemType.create(source.toString()));

        OperationRegistry.registerCast(StringType.class, MinecraftBlockType.class,
                source -> MinecraftBlockType.create(source.toString()));

        OperationRegistry.registerCast(NumberType.class, DecimalType.class,
                source -> DecimalType.create(source.asDouble()));

        OperationRegistry.registerCast(NumberType.class, IntegralType.class,
                source -> IntegralType.create(source.asLong()));

        OperationRegistry.registerCast(NumberType.class, PercentType.class,
                source -> PercentType.create(source.asDouble()));

        OperationRegistry.registerCast(NumberType.class, RandomQuantityType.class,
                source -> RandomQuantityType.create(PercentType.create(source.asDouble()), IntegralType.NULL, IntegralType.NULL));

        OperationRegistry.registerCast(Vector3dType.class, BlockPosType.class, Vector3dType::asBlockPos);
        OperationRegistry.registerCast(BlockPosType.class, Vector3dType.class, BlockPosType::asVector3d);

        OperationRegistry.registerCast(ColorType.class, IntegralType.class, c -> IntegralType.create(c.asArgb()));
        OperationRegistry.registerCast(IntegralType.class, ColorType.class, n -> ColorType.create(n.asInt()));

        OperationRegistry.registerCast(TimeType.class, IntegralType.class, t -> IntegralType.create(t.asTicks()));
        OperationRegistry.registerCast(IntegralType.class, TimeType.class, n -> TimeType.create(n.asLong()));

        OperationRegistry.registerCast(EnumType.class, IntegralType.class, e -> IntegralType.create(e.getEnum().ordinal()));

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
        OperationRegistry.registerUnary(NumberType.class, LogicalOperator.Unary.NOT,
                target -> PlusMinusType.create(target.asDouble() <= 0));

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

        OperationRegistry.registerBinary(LogicalType.class, NumberType.class, ArithmeticOperator.Binary.PLUS,
                (left, right) -> left.createType(MouseLogic.or(left.asBoolean(), right.asDouble() > 0)));

        OperationRegistry.registerBinary(NumberType.class, LogicalType.class, ArithmeticOperator.Binary.PLUS,
                (left, right) -> left.createType(left.asDouble() + (right.asBoolean() ? 1.0 : 0.0)));

        OperationRegistry.registerBinary(Vector3dType.class, Vector3dType.class, ArithmeticOperator.Binary.PLUS,
                (l, r) -> Vector3dType.create(l.asDoubleX() + r.asDoubleX(), l.asDoubleY() + r.asDoubleY(), l.asDoubleZ() + r.asDoubleZ()));
        OperationRegistry.registerBinary(Vector3dType.class, Vector3dType.class, ArithmeticOperator.Binary.MINUS,
                (l, r) -> Vector3dType.create(l.asDoubleX() - r.asDoubleX(), l.asDoubleY() - r.asDoubleY(), l.asDoubleZ() - r.asDoubleZ()));
        OperationRegistry.registerBinary(Vector3dType.class, Vector3dType.class, LogicalOperator.Binary.EQUAL,
                (l, r) -> PlusMinusType.create(l.equals(r)));

        OperationRegistry.registerBinary(Vector3dType.class, NumberType.class, ArithmeticOperator.Binary.MULTIPLY,
                (v, n) -> Vector3dType.create(v.asDoubleX() * n.asDouble(), v.asDoubleY() * n.asDouble(), v.asDoubleZ() * n.asDouble()));
        OperationRegistry.registerBinary(NumberType.class, Vector3dType.class, ArithmeticOperator.Binary.MULTIPLY,
                (n, v) -> Vector3dType.create(v.asDoubleX() * n.asDouble(), v.asDoubleY() * n.asDouble(), v.asDoubleZ() * n.asDouble()));
        OperationRegistry.registerBinary(Vector3dType.class, NumberType.class, ArithmeticOperator.Binary.DIVIDE,
                (v, n) -> Vector3dType.create(v.asDoubleX() / n.asDouble(), v.asDoubleY() / n.asDouble(), v.asDoubleZ() / n.asDouble()));

        OperationRegistry.registerBinary(BlockPosType.class, BlockPosType.class, ArithmeticOperator.Binary.PLUS,
                (l, r) -> BlockPosType.create(l.asLongX() + r.asLongX(), l.asLongY() + r.asLongY(), l.asLongZ() + r.asLongZ()));
        OperationRegistry.registerBinary(BlockPosType.class, BlockPosType.class, ArithmeticOperator.Binary.MINUS,
                (l, r) -> BlockPosType.create(l.asLongX() - r.asLongX(), l.asLongY() - r.asLongY(), l.asLongZ() - r.asLongZ()));
        OperationRegistry.registerBinary(BlockPosType.class, BlockPosType.class, LogicalOperator.Binary.EQUAL,
                (l, r) -> PlusMinusType.create(l.equals(r)));

        OperationRegistry.registerBinary(BlockPosType.class, NumberType.class, ArithmeticOperator.Binary.MULTIPLY,
                (p, n) -> BlockPosType.create(
                        MouseNumbers.round(p.asLongX() * n.asDouble()),
                        MouseNumbers.round(p.asLongY() * n.asDouble()),
                        MouseNumbers.round(p.asLongZ() * n.asDouble())
                ));
        OperationRegistry.registerBinary(NumberType.class, BlockPosType.class, ArithmeticOperator.Binary.MULTIPLY,
                (n, p) -> BlockPosType.create(
                        MouseNumbers.round(p.asLongX() * n.asDouble()),
                        MouseNumbers.round(p.asLongY() * n.asDouble()),
                        MouseNumbers.round(p.asLongZ() * n.asDouble())
                ));

        OperationRegistry.registerBinary(BlockPosType.class, Vector3dType.class, ArithmeticOperator.Binary.PLUS,
                (pos, vec) -> Vector3dType.create(pos.asLongX() + vec.asDoubleX(), pos.asLongY() + vec.asDoubleY(), pos.asLongZ() + vec.asDoubleZ()));
        OperationRegistry.registerBinary(Vector3dType.class, BlockPosType.class, ArithmeticOperator.Binary.PLUS,
                (vec, pos) -> Vector3dType.create(vec.asDoubleX() + pos.asLongX(), vec.asDoubleY() + pos.asLongY(), vec.asDoubleZ() + pos.asLongZ()));

        OperationRegistry.registerBinary(BlockPosType.class, Vector3dType.class, ArithmeticOperator.Binary.MINUS,
                (pos, vec) -> Vector3dType.create(pos.asLongX() - vec.asDoubleX(), pos.asLongY() - vec.asDoubleY(), pos.asLongZ() - vec.asDoubleZ()));
        OperationRegistry.registerBinary(Vector3dType.class, BlockPosType.class, ArithmeticOperator.Binary.MINUS,
                (vec, pos) -> Vector3dType.create(vec.asDoubleX() - pos.asLongX(), vec.asDoubleY() - pos.asLongY(), vec.asDoubleZ() - pos.asLongZ()));

        OperationRegistry.registerBinary(Vector3dType.class, BlockPosType.class, LogicalOperator.Binary.EQUAL,
                (v, p) -> PlusMinusType.create(v.asBlockPos().equals(p)));
        OperationRegistry.registerBinary(BlockPosType.class, Vector3dType.class, LogicalOperator.Binary.EQUAL,
                (p, v) -> PlusMinusType.create(p.equals(v.asBlockPos())));

        OperationRegistry.registerUnary(Vector3dType.class, ArithmeticOperator.Unary.INVERT,
                v -> Vector3dType.create(-v.asDoubleX(), -v.asDoubleY(), -v.asDoubleZ()));
        OperationRegistry.registerUnary(BlockPosType.class, ArithmeticOperator.Unary.INVERT,
                p -> BlockPosType.create(-p.asLongX(), -p.asLongY(), -p.asLongZ()));

        OperationRegistry.registerBinary(ColorType.class, ColorType.class, ArithmeticOperator.Binary.PLUS,
                (l, r) -> ColorType.create(
                        l.asRed() + r.asRed(),
                        l.asGreen() + r.asGreen(),
                        l.asBlue() + r.asBlue(),
                        MouseNumbers.clamp(l.asAlpha() + r.asAlpha(), 0, 255)
                ));

        OperationRegistry.registerBinary(ColorType.class, ColorType.class, ArithmeticOperator.Binary.MINUS,
                (l, r) -> ColorType.create(
                        l.asRed() - r.asRed(),
                        l.asGreen() - r.asGreen(),
                        l.asBlue() - r.asBlue(),
                        MouseNumbers.clamp(l.asAlpha() - r.asAlpha(), 0, 255)
                ));

        OperationRegistry.registerBinary(ColorType.class, NumberType.class, ArithmeticOperator.Binary.MULTIPLY,
                (c, n) -> ColorType.create(
                        (int) MouseNumbers.round(c.asRed() * n.asDouble()),
                        (int) MouseNumbers.round(c.asGreen() * n.asDouble()),
                        (int) MouseNumbers.round(c.asBlue() * n.asDouble()),
                        c.asAlpha()
                ));
        OperationRegistry.registerBinary(NumberType.class, ColorType.class, ArithmeticOperator.Binary.MULTIPLY,
                (n, c) -> ColorType.create(
                        (int) MouseNumbers.round(c.asRed() * n.asDouble()),
                        (int) MouseNumbers.round(c.asGreen() * n.asDouble()),
                        (int) MouseNumbers.round(c.asBlue() * n.asDouble()),
                        c.asAlpha()
                ));

        OperationRegistry.registerBinary(ColorType.class, NumberType.class, ArithmeticOperator.Binary.DIVIDE,
                (c, n) -> ColorType.create(
                        (int) MouseNumbers.round(c.asRed() / n.asDouble()),
                        (int) MouseNumbers.round(c.asGreen() / n.asDouble()),
                        (int) MouseNumbers.round(c.asBlue() / n.asDouble()),
                        c.asAlpha()
                ));

        OperationRegistry.registerBinary(ColorType.class, ColorType.class, LogicalOperator.Binary.EQUAL,
                (l, r) -> PlusMinusType.create(l.equals(r)));

        OperationRegistry.registerBinary(RegexType.class, StringType.class, LogicalOperator.Binary.EQUAL,
                (regex, str) -> PlusMinusType.create(regex.matches(str.asString())));
        OperationRegistry.registerBinary(StringType.class, RegexType.class, LogicalOperator.Binary.EQUAL,
                (str, regex) -> PlusMinusType.create(regex.matches(str.asString())));

        OperationRegistry.registerBinary(RegexType.class, RegexType.class, LogicalOperator.Binary.EQUAL,
                (l, r) -> PlusMinusType.create(l.equals(r)));

        OperationRegistry.registerBinary(RangeType.class, NumberType.class, ArithmeticOperator.Binary.PLUS,
                (r, n) -> RangeType.create(
                        r.getMinDouble() + n.asDouble(),
                        r.getMaxDouble() + n.asDouble(),
                        r.isIncludeMin(),
                        r.isIncludeMax()
                ));
        OperationRegistry.registerBinary(RangeType.class, NumberType.class, ArithmeticOperator.Binary.MINUS,
                (r, n) -> RangeType.create(
                        r.getMinDouble() - n.asDouble(),
                        r.getMaxDouble() - n.asDouble(),
                        r.isIncludeMin(),
                        r.isIncludeMax()
                ));

        OperationRegistry.registerBinary(RangeType.class, NumberType.class, ArithmeticOperator.Binary.MULTIPLY,
                (r, n) -> RangeType.create(
                        r.getMinDouble() * n.asDouble(),
                        r.getMaxDouble() * n.asDouble(),
                        r.isIncludeMin(),
                        r.isIncludeMax()
                ));
        OperationRegistry.registerBinary(RangeType.class, NumberType.class, ArithmeticOperator.Binary.DIVIDE,
                (r, n) -> RangeType.create(
                        r.getMinDouble() / n.asDouble(),
                        r.getMaxDouble() / n.asDouble(),
                        r.isIncludeMin(),
                        r.isIncludeMax()
                ));

        OperationRegistry.registerBinary(RangeType.class, RangeType.class, BitwiseOperator.Binary.AND,
                (r1, r2) -> {
                    double min1                                                       = r1.getMinDouble(), min2 = r2.getMinDouble();
                    double                             max1 = r1.getMaxDouble(), max2 = r2.getMaxDouble();
                    double                             newMin                         = Math.max(min1, min2);
                    double                             newMax                         = Math.min(max1, max2);
                    if (newMin > newMax) return RangeType.ZERO;
                    boolean incMin = min1 == newMin ? r1.isIncludeMin() : r2.isIncludeMin();
                    boolean incMax = max1 == newMax ? r1.isIncludeMax() : r2.isIncludeMax();
                    return RangeType.create(newMin, newMax, incMin, incMax);
                });

        OperationRegistry.registerBinary(NumberType.class, RangeType.class, LogicalOperator.Binary.EQUAL,
                (n, r) -> PlusMinusType.create(r.isInRange(n.asDouble())));
        OperationRegistry.registerBinary(RangeType.class, NumberType.class, LogicalOperator.Binary.EQUAL,
                (r, n) -> PlusMinusType.create(r.isInRange(n.asDouble())));

        OperationRegistry.registerBinary(RangeType.class, RangeType.class, LogicalOperator.Binary.EQUAL,
                (l, r) -> PlusMinusType.create(l.equals(r)));

        OperationRegistry.registerBinary(EnumType.class, EnumType.class, LogicalOperator.Binary.EQUAL,
                (l, r) -> PlusMinusType.create(l.equals(r)));
        OperationRegistry.registerBinary(EnumType.class, StringType.class, LogicalOperator.Binary.EQUAL,
                (e, s) -> PlusMinusType.create(e.getEnum().name().equalsIgnoreCase(s.asString())));
        OperationRegistry.registerBinary(StringType.class, EnumType.class, LogicalOperator.Binary.EQUAL,
                (s, e) -> PlusMinusType.create(e.getEnum().name().equalsIgnoreCase(s.asString())));

        OperationRegistry.registerBinary(OtherType.class, OtherType.class, LogicalOperator.Binary.EQUAL,
                (left, right) -> PlusMinusType.create(left.getValue().equals(right.getValue())));

        OperationRegistry.registerBinary(ListType.class, ListType.class, LogicalOperator.Binary.EQUAL,
                (left, right) -> PlusMinusType.create(left.equals(right)));

        for (ArithmeticOperator.Binary op : ArithmeticOperator.Binary.values()) {
            OperationRegistry.registerBinary(ConditionalValType.class, CustomType.class, op, (left, right) ->
                    left.createType((CustomType<?>) OperationRegistry.evaluate(left.getValue(), right, op), left.isAnti()));
        }

        for (BitwiseOperator.Binary op : BitwiseOperator.Binary.values()) {
            OperationRegistry.registerBinary(ConditionalValType.class, CustomType.class, op, (left, right) ->
                    left.createType((CustomType<?>) OperationRegistry.evaluate(left.getValue(), right, op), left.isAnti()));
        }

        OperationRegistry.registerBinary(ConditionalValType.class, CustomType.class, LogicalOperator.Binary.AND, (left, right) ->
                left.createType((CustomType<?>) OperationRegistry.evaluate(left.getValue(), right, LogicalOperator.Binary.AND), left.isAnti()));
        OperationRegistry.registerBinary(ConditionalValType.class, CustomType.class, LogicalOperator.Binary.OR, (left, right) ->
                left.createType((CustomType<?>) OperationRegistry.evaluate(left.getValue(), right, LogicalOperator.Binary.OR), left.isAnti()));

        OperationRegistry.registerBinary(ConditionalValType.class, CustomType.class, LogicalOperator.Binary.LESS, (left, right) ->
                (CustomType<?>) OperationRegistry.evaluate(left.getValue(), right, LogicalOperator.Binary.LESS));
        OperationRegistry.registerBinary(ConditionalValType.class, CustomType.class, LogicalOperator.Binary.MORE, (left, right) ->
                (CustomType<?>) OperationRegistry.evaluate(left.getValue(), right, LogicalOperator.Binary.MORE));
        OperationRegistry.registerBinary(ConditionalValType.class, CustomType.class, LogicalOperator.Binary.EQUAL, (left, right) ->
                (CustomType<?>) OperationRegistry.evaluate(left.getValue(), right, LogicalOperator.Binary.EQUAL));
        OperationRegistry.registerBinary(ConditionalValType.class, CustomType.class, LogicalOperator.Binary.LESS_OR_EQUAL, (left, right) ->
                (CustomType<?>) OperationRegistry.evaluate(left.getValue(), right, LogicalOperator.Binary.LESS_OR_EQUAL));
        OperationRegistry.registerBinary(ConditionalValType.class, CustomType.class, LogicalOperator.Binary.MORE_OR_EQUAL, (left, right) ->
                (CustomType<?>) OperationRegistry.evaluate(left.getValue(), right, LogicalOperator.Binary.MORE_OR_EQUAL));

        for (ArithmeticOperator.Unary op : ArithmeticOperator.Unary.values()) {
            OperationRegistry.registerUnary(ConditionalValType.class, op, target ->
                    ConditionalValType.create(target.getValue(), PlusMinusType.create(!target.isAnti().asBoolean())));
        }

        OperationRegistry.registerUnary(ConditionalValType.class, LogicalOperator.Unary.NOT, target ->
                ConditionalValType.create(target.getValue(), PlusMinusType.create(!target.isAnti().asBoolean())));

        OperationRegistry.registerUnary(ConditionalValType.class, BitwiseOperator.Unary.NOT, target ->
                target.createType((CustomType<?>) OperationRegistry.evaluate(target.getValue(), BitwiseOperator.Unary.NOT), target.isAnti()));

        OperationRegistry.registerBinary(TupleType.class, TupleType.class, LogicalOperator.Binary.EQUAL,
                (l, r) -> PlusMinusType.create(l.equals(r)));

        OperationRegistry.registerCast(TupleType.class, StringType.class,
                t -> StringType.create(t.toString()));

        OperationRegistry.registerBinary(MapType.class, MapType.class, ArithmeticOperator.Binary.PLUS,
                (m1, m2) -> {
                    Map<?, ?> merged = new LinkedHashMap(m1.asMap());
                    merged.putAll(m2.asMap());
                    return m1.createType(merged);
                });

        OperationRegistry.registerBinary(MapType.class, CustomType.class, ArithmeticOperator.Binary.MINUS,
                (m, key) -> {
                    Map<?, ?> copy = new LinkedHashMap(m.asMap());
                    copy.remove(key);
                    return m.createType(copy);
                });

        OperationRegistry.registerBinary(MapType.class, MapType.class, BitwiseOperator.Binary.AND,
                (m1, m2) -> {
                    Map intersection = new LinkedHashMap();
                    m1.asMap().forEach((k, v) -> { if (m2.asMap().containsKey(k)) intersection.put(k, v); });
                    return m1.createType(intersection);
                });

        OperationRegistry.registerBinary(MapType.class, MapType.class, LogicalOperator.Binary.EQUAL,
                (l, r) -> PlusMinusType.create(l.equals(r)));

        OperationRegistry.registerCast(MapType.class, StringType.class,
                m -> StringType.create(m.toString()));

        OperationRegistry.registerBinary(TableType.class, TableType.class, ArithmeticOperator.Binary.PLUS,
                (t1, t2) -> {
                    if (!t1.getSchema().equals(t2.getSchema())) {
                        throw new UnsupportedValException("Cannot merge tables with different schemas: " + t1.getSchema() + " and " + t2.getSchema());
                    }
                    List<?> mergedRows = new ArrayList<>(t1.asRows());
                    mergedRows.addAll(t2.asRows());
                    return t1.createType(mergedRows);
                });

        OperationRegistry.registerBinary(TableType.class, TableType.class, LogicalOperator.Binary.EQUAL,
                (l, r) -> PlusMinusType.create(l.equals(r)));

        OperationRegistry.registerCast(TableType.class, StringType.class,
                t -> StringType.create(t.toString()));

        OperationRegistry.registerBinary(MatrixType.class, MatrixType.class, ArithmeticOperator.Binary.PLUS,
                (m1, m2) -> {
                    if (m1.rowCount() != m2.rowCount() || m1.columnCount() != m2.columnCount()) {
                        throw new UnsupportedValException("Cannot add matrices of different dimensions: "
                                + m1.rowCount() + "x" + m1.columnCount() + " and " + m2.rowCount() + "x" + m2.columnCount());
                    }
                    int        r   = m1.rowCount();
                    int        c   = m1.columnCount();
                    double[][] res = new double[r][c];
                    for (int i = 0; i < r; i++) for (int j = 0; j < c; j++) res[i][j] = m1.getDouble(i, j) + m2.getDouble(i, j);
                    return SimpleMatrixType.create(res);
                });

        OperationRegistry.registerBinary(MatrixType.class, MatrixType.class, ArithmeticOperator.Binary.MINUS,
                (m1, m2) -> {
                    if (m1.rowCount() != m2.rowCount() || m1.columnCount() != m2.columnCount()) {
                        throw new UnsupportedValException("Cannot subtract matrices of different dimensions: "
                                + m1.rowCount() + "x" + m1.columnCount() + " and " + m2.rowCount() + "x" + m2.columnCount());
                    }
                    int        r   = m1.rowCount();
                    int        c   = m1.columnCount();
                    double[][] res = new double[r][c];
                    for (int i = 0; i < r; i++) for (int j = 0; j < c; j++) res[i][j] = m1.getDouble(i, j) - m2.getDouble(i, j);
                    return SimpleMatrixType.create(res);
                });

        OperationRegistry.registerBinary(MatrixType.class, NumberType.class, ArithmeticOperator.Binary.MULTIPLY,
                (m, n) -> {
                    int        r      = m.rowCount();
                    int        c      = m.columnCount();
                    double     scalar = n.asDouble();
                    double[][] res    = new double[r][c];
                    for (int i = 0; i < r; i++) for (int j = 0; j < c; j++) res[i][j] = m.getDouble(i, j) * scalar;
                    return SimpleMatrixType.create(res);
                });

        OperationRegistry.registerBinary(NumberType.class, MatrixType.class, ArithmeticOperator.Binary.MULTIPLY,
                (n, m) -> {
                    int        r      = m.rowCount();
                    int        c      = m.columnCount();
                    double     scalar = n.asDouble();
                    double[][] res    = new double[r][c];
                    for (int i = 0; i < r; i++) for (int j = 0; j < c; j++) res[i][j] = m.getDouble(i, j) * scalar;
                    return SimpleMatrixType.create(res);
                });

        OperationRegistry.registerBinary(MatrixType.class, MatrixType.class, ArithmeticOperator.Binary.MULTIPLY,
                (m1, m2) -> {
                    if (m1.columnCount() != m2.rowCount()) {
                        throw new UnsupportedValException("Cannot multiply matrices: columns of A ("
                                + m1.columnCount() + ") must equal rows of B (" + m2.rowCount() + ")");
                    }
                    int        r    = m1.rowCount();
                    int        c    = m2.columnCount();
                    int        kLen = m1.columnCount();
                    double[][] res  = new double[r][c];

                    for (int i = 0; i < r; i++) {
                        for (int j = 0; j < c; j++) {
                            double sum = 0.0D;
                            for (int k = 0; k < kLen; k++) sum += m1.getDouble(i, k) * m2.getDouble(k, j);
                            res[i][j] = sum;
                        }
                    }
                    return SimpleMatrixType.create(res);
                });

        OperationRegistry.registerBinary(MatrixType.class, MatrixType.class, LogicalOperator.Binary.EQUAL,
                (l, r) -> PlusMinusType.create(l.equals(r)));

        OperationRegistry.registerCast(MatrixType.class, StringType.class,
                m -> StringType.create(m.toString()));

        OperationRegistry.registerBinary(NBTType.class, NBTType.class, ArithmeticOperator.Binary.PLUS,
                NBTType::merge);

        OperationRegistry.registerBinary(NBTType.class, StringType.class, ArithmeticOperator.Binary.MINUS,
                (n, key) -> n.remove(key.asString()));

        OperationRegistry.registerBinary(NBTType.class, NBTType.class, LogicalOperator.Binary.EQUAL,
                (l, r) -> PlusMinusType.create(l.equals(r)));

        OperationRegistry.registerCast(NBTType.class, StringType.class,
                n -> StringType.create(n.asString()));

        OperationRegistry.registerCast(StringType.class, NBTType.class,
                s -> NBTType.parse(s.asString()));

        OperationRegistry.registerBinary(MinecraftItemStackType.class, NumberType.class, ArithmeticOperator.Binary.PLUS,
                (stack, n) -> {
                    long               add = n.asLong();
                    RandomQuantityType q   = stack.getQuantity();
                    RandomQuantityType newQ = RandomQuantityType.create(
                            q.getChance(),
                            IntegralType.create(Math.max(0L, q.getMin().asLong() + add)),
                            IntegralType.create(Math.max(0L, q.getMax().asLong() + add))
                    );
                    return stack.withQuantity(newQ);
                });

        OperationRegistry.registerBinary(MinecraftItemStackType.class, NumberType.class, ArithmeticOperator.Binary.MINUS,
                (stack, n) -> {
                    long               sub = n.asLong();
                    RandomQuantityType q   = stack.getQuantity();
                    RandomQuantityType newQ = RandomQuantityType.create(
                            q.getChance(),
                            IntegralType.create(Math.max(0L, q.getMin().asLong() - sub)),
                            IntegralType.create(Math.max(0L, q.getMax().asLong() - sub))
                    );
                    return stack.withQuantity(newQ);
                });

        OperationRegistry.registerBinary(MinecraftItemStackType.class, NumberType.class, ArithmeticOperator.Binary.MULTIPLY,
                (stack, n) -> {
                    double             scalar = n.asDouble();
                    RandomQuantityType q      = stack.getQuantity();
                    RandomQuantityType newQ = RandomQuantityType.create(
                            q.getChance(),
                            IntegralType.create(Math.max(0L, MouseNumbers.round(q.getMin().asLong() * scalar))),
                            IntegralType.create(Math.max(0L, MouseNumbers.round(q.getMax().asLong() * scalar)))
                    );
                    return stack.withQuantity(newQ);
                });

        OperationRegistry.registerBinary(MinecraftItemStackType.class, NBTType.class, ArithmeticOperator.Binary.PLUS,
                (stack, nbt) -> stack.withNbt(stack.getNbt().merge(nbt)));

        OperationRegistry.registerBinary(MinecraftItemType.class, NBTType.class, ArithmeticOperator.Binary.PLUS,
                (item, nbt) -> MinecraftItemStackType.create(item, 1L).withNbt(nbt));

        OperationRegistry.registerBinary(MinecraftItemType.class, NumberType.class, ArithmeticOperator.Binary.PLUS,
                (item, n) -> MinecraftItemStackType.create(item, n.asLong()));

        OperationRegistry.registerBinary(MinecraftItemType.class, RandomQuantityType.class, ArithmeticOperator.Binary.PLUS,
                MinecraftItemStackType::create);

        OperationRegistry.registerCast(MinecraftItemType.class, MinecraftItemStackType.class,
                MinecraftItemStackType::create);

        OperationRegistry.registerCast(MinecraftBlockType.class, MinecraftItemStackType.class,
                MinecraftItemStackType::create);

        OperationRegistry.registerCast(MinecraftItemStackType.class, StringType.class,
                s -> StringType.create(s.toString()));
        OperationRegistry.registerCast(StringType.class, MinecraftItemStackType.class,
                s -> MinecraftItemStackType.parse(s.asString()));

        OperationRegistry.registerBinary(MinecraftItemStackType.class, MinecraftItemStackType.class, LogicalOperator.Binary.EQUAL,
                (l, r) -> PlusMinusType.create(l.equals(r)));

        OperationRegistry.registerBinary(MinecraftItemStackType.class, MinecraftItemType.class, LogicalOperator.Binary.EQUAL,
                (stack, item) -> PlusMinusType.create(stack.getItem().equals(item)));
        OperationRegistry.registerBinary(MinecraftItemType.class, MinecraftItemStackType.class, LogicalOperator.Binary.EQUAL,
                (item, stack) -> PlusMinusType.create(stack.getItem().equals(item)));

        OperationRegistry.registerBinary(MinecraftTagType.class, MinecraftTagType.class, LogicalOperator.Binary.EQUAL,
                (l, r) -> PlusMinusType.create(l.equals(r)));

        OperationRegistry.registerBinary(MinecraftItemType.class, MinecraftTagType.class, LogicalOperator.Binary.EQUAL,
                (item, tag) -> PlusMinusType.create(tag.contains(item)));
        OperationRegistry.registerBinary(MinecraftTagType.class, MinecraftItemType.class, LogicalOperator.Binary.EQUAL,
                (tag, item) -> PlusMinusType.create(tag.contains(item)));

        OperationRegistry.registerBinary(MinecraftBlockType.class, MinecraftTagType.class, LogicalOperator.Binary.EQUAL,
                (block, tag) -> PlusMinusType.create(tag.contains(block)));
        OperationRegistry.registerBinary(MinecraftTagType.class, MinecraftBlockType.class, LogicalOperator.Binary.EQUAL,
                (tag, block) -> PlusMinusType.create(tag.contains(block)));

        OperationRegistry.registerBinary(MinecraftItemStackType.class, MinecraftTagType.class, LogicalOperator.Binary.EQUAL,
                (stack, tag) -> PlusMinusType.create(tag.contains(stack)));
        OperationRegistry.registerBinary(MinecraftTagType.class, MinecraftItemStackType.class, LogicalOperator.Binary.EQUAL,
                (tag, stack) -> PlusMinusType.create(tag.contains(stack)));

        OperationRegistry.registerCast(MinecraftTagType.class, StringType.class,
                t -> StringType.create(t.toString()));
        OperationRegistry.registerCast(StringType.class, MinecraftTagType.class,
                s -> MinecraftTagType.parse(s.asString()));
    }
}