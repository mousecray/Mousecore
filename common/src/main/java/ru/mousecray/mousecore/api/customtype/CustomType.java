/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype;


import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.op.*;
import ru.mousecray.mousecore.api.customtype.values.PlusMinusType;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public abstract class CustomType<T extends CustomType<T>> implements Comparable<T> {
    protected static final Map<Class<? extends CustomType<?>>, Function<String, ? extends CustomType<?>>> storage = new HashMap<>();

    @SuppressWarnings("unchecked") @Nullable
    public static <T> T parse(Class<T> clazz, @Nullable String val) {
        Function<String, ? extends CustomType<?>> result = storage.get(clazz);
        if (result != null) {
            try {
                return (T) result.apply(val);
            } catch (ClassCastException ignore) { }
        }
        return null;
    }

    private final     CustomValType       valType;
    private transient CustomCast<T>       castPipeline;
    private transient CustomArithmetic<T> arithmeticPipeline;
    private transient CustomLogic<T>      logicPipeline;
    private transient CustomBitwise<T>    bitwisePipeline;

    protected CustomType(CustomValType valType)                      { this.valType = Objects.requireNonNull(valType); }
    protected CustomType()                                           { valType = CustomValType.UNDEFINED; }

    @SuppressWarnings("unchecked") protected Class<T> getTypeClass() { return (Class<T>) getClass(); }
    public CustomValType getValType()                                { return valType; }
    @SuppressWarnings("unchecked") protected T self()                { return (T) this; }

    public ListType<?, ?> asListType()                               { return getCastPipeline().asValue(ListType.class); }
    public NumberType<?> asNumberType()                              { return getCastPipeline().asValue(NumberType.class); }
    public LogicalType<?> asLogicalType()                            { return getCastPipeline().asValue(LogicalType.class); }
    public OtherType<?> asOtherType()                                { return getCastPipeline().asValue(OtherType.class); }

    public CustomCast<T> getCastPipeline() {
        if (castPipeline == null) {
            castPipeline = new CustomCast<T>() {
                @Override
                public <TYPE extends CustomType<?>> TYPE asValue(Class<TYPE> targetClass) { return OperationRegistry.evaluateCast(self(), targetClass); }
            };
        }
        return castPipeline;
    }

    public CustomArithmetic<T> getArithmeticPipeline() {
        if (arithmeticPipeline == null) {
            arithmeticPipeline = new CustomArithmetic<T>() {
                @SuppressWarnings("unchecked") @Override
                public T plus(CustomType<?> other) { return (T) OperationRegistry.evaluateBinary(self(), other, ArithmeticOperator.Binary.PLUS); }
                @SuppressWarnings("unchecked") @Override
                public T minus(CustomType<?> other) { return (T) OperationRegistry.evaluateBinary(self(), other, ArithmeticOperator.Binary.MINUS); }
                @SuppressWarnings("unchecked") @Override
                public T multiply(CustomType<?> other) { return (T) OperationRegistry.evaluateBinary(self(), other, ArithmeticOperator.Binary.MULTIPLY); }
                @SuppressWarnings("unchecked") @Override
                public T divide(CustomType<?> other) { return (T) OperationRegistry.evaluateBinary(self(), other, ArithmeticOperator.Binary.DIVIDE); }
                @SuppressWarnings("unchecked") @Override
                public T modulo(CustomType<?> other) { return (T) OperationRegistry.evaluateBinary(self(), other, ArithmeticOperator.Binary.MODULO); }

                @SuppressWarnings("unchecked") @Override
                public T invert() { return (T) OperationRegistry.evaluateUnary(self(), ArithmeticOperator.Unary.INVERT); }
                @SuppressWarnings("unchecked") @Override
                public T increment() { return (T) OperationRegistry.evaluateUnary(self(), ArithmeticOperator.Unary.INCREMENT); }
                @SuppressWarnings("unchecked") @Override
                public T decrement() { return (T) OperationRegistry.evaluateUnary(self(), ArithmeticOperator.Unary.DECREMENT); }
            };
        }
        return arithmeticPipeline;
    }

    public CustomLogic<T> getLogicPipeline() {
        if (logicPipeline == null) {
            logicPipeline = new CustomLogic<T>() {
                @Override
                public PlusMinusType isLess(CustomType<?> other) { return (PlusMinusType) OperationRegistry.evaluateBinary(self(), other, LogicalOperator.Binary.LESS); }
                @Override
                public PlusMinusType isMore(CustomType<?> other) { return (PlusMinusType) OperationRegistry.evaluateBinary(self(), other, LogicalOperator.Binary.MORE); }
                @Override
                public PlusMinusType isEqual(CustomType<?> other) { return (PlusMinusType) OperationRegistry.evaluateBinary(self(), other, LogicalOperator.Binary.EQUAL); }
                @Override
                public PlusMinusType isLessOrEqual(CustomType<?> other) { return (PlusMinusType) OperationRegistry.evaluateBinary(self(), other, LogicalOperator.Binary.LESS_OR_EQUAL); }
                @Override
                public PlusMinusType isMoreOrEqual(CustomType<?> other) { return (PlusMinusType) OperationRegistry.evaluateBinary(self(), other, LogicalOperator.Binary.MORE_OR_EQUAL); }

                @SuppressWarnings("unchecked") @Override
                public T and(CustomType<?> other) { return (T) OperationRegistry.evaluateBinary(self(), other, LogicalOperator.Binary.AND); }
                @SuppressWarnings("unchecked") @Override
                public T or(CustomType<?> other) { return (T) OperationRegistry.evaluateBinary(self(), other, LogicalOperator.Binary.OR); }
                @SuppressWarnings("unchecked") @Override
                public T not() { return (T) OperationRegistry.evaluateUnary(self(), LogicalOperator.Unary.NOT); }
            };
        }
        return logicPipeline;
    }

    public CustomBitwise<T> getBitwisePipeline() {
        if (bitwisePipeline == null) {
            bitwisePipeline = new CustomBitwise<T>() {
                @SuppressWarnings("unchecked") @Override
                public T and(CustomType<?> other) { return (T) OperationRegistry.evaluateBinary(self(), other, BitwiseOperator.Binary.AND); }
                @SuppressWarnings("unchecked") @Override
                public T or(CustomType<?> other) { return (T) OperationRegistry.evaluateBinary(self(), other, BitwiseOperator.Binary.OR); }
                @SuppressWarnings("unchecked") @Override
                public T xor(CustomType<?> other) { return (T) OperationRegistry.evaluateBinary(self(), other, BitwiseOperator.Binary.XOR); }
                @SuppressWarnings("unchecked") @Override
                public T not() { return (T) OperationRegistry.evaluateUnary(self(), BitwiseOperator.Unary.NOT); }

                @SuppressWarnings("unchecked") @Override
                public T leftShift(int other) {
                    return (T) OperationRegistry.evaluateShift(self(), other, BitwiseOperator.Shift.LEFT_SHIFT);
                }

                @SuppressWarnings("unchecked") @Override
                public T rightShift(int other) {
                    return (T) OperationRegistry.evaluateShift(self(), other, BitwiseOperator.Shift.RIGHT_SHIFT);
                }

                @SuppressWarnings("unchecked") @Override
                public T uRightShift(int other) {
                    return (T) OperationRegistry.evaluateShift(self(), other, BitwiseOperator.Shift.U_RIGHT_SHIFT);
                }
            };
        }
        return bitwisePipeline;
    }

    @Override public abstract String toString();
}