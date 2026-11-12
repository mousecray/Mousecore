/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype;


import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.op.*;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.type.ValTypeMetaCategory;
import ru.mousecray.mousecore.api.customtype.values.PlusMinusType;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public abstract class CustomType<T extends CustomType<T>> implements Comparable<T> {
    protected static final Map<Class<? extends CustomType<?>>, Function<String, ? extends CustomType<?>>> storage = new HashMap<>();

    @SuppressWarnings("unchecked") @Nullable
    public static <T extends CustomType<?>> T parse(Class<T> clazz, @Nullable String val) {
        Function<String, ? extends CustomType<?>> result = storage.get(clazz);

        if (result == null) {
            /* Load class on try to get parser for static initializers in this class */
            try { Class.forName(clazz.getName(), true, clazz.getClassLoader()); } catch (ClassNotFoundException ignore) { }
            result = storage.get(clazz);
        }

        if (result != null) try { return (T) result.apply(val); } catch (ClassCastException ignore) { }
        return null;
    }

    private transient CustomCast<T>       castPipeline;
    private transient CustomArithmetic<T> arithmeticPipeline;
    private transient CustomLogic<T>      logicPipeline;
    private transient CustomBitwise<T>    bitwisePipeline;

    protected CustomType()                                           { }

    @SuppressWarnings("unchecked") protected Class<T> getTypeClass() { return (Class<T>) getClass(); }
    public abstract IValType getValType();
    public ValTypeMetaCategory getValTypeMetaCategory()              { return ValTypeMetaCategory.UNDEFINED; }
    @SuppressWarnings("unchecked") protected T self()                { return (T) this; }

    public ListType<?, ?> asListType()                               { return getCastPipeline().asValue(ListType.class); }
    public NumberType<?> asNumberType()                              { return getCastPipeline().asValue(NumberType.class); }
    public LogicalType<?> asLogicalType()                            { return getCastPipeline().asValue(LogicalType.class); }
    public OtherType<?> asOtherType()                                { return getCastPipeline().asValue(OtherType.class); }

    public CustomCast<T> getCastPipeline() {
        if (castPipeline == null) {
            castPipeline = new CustomCast<>() {
                @Override
                public <TYPE extends CustomType<?>> TYPE asValue(Class<TYPE> targetClass) { return OperationRegistry.evaluate(self(), targetClass); }

                @Override
                public boolean isSupport(Class<? extends CustomType<?>> targetClass) { return OperationRegistry.canEvaluate(getTypeClass(), targetClass); }
            };
        }
        return castPipeline;
    }

    public CustomArithmetic<T> getArithmeticPipeline() {
        if (arithmeticPipeline == null) {
            arithmeticPipeline = new CustomArithmetic<>() {
                @SuppressWarnings("unchecked") @Override
                public T plus(CustomType<?> other) { return (T) OperationRegistry.evaluate(self(), other, ArithmeticOperator.Binary.PLUS); }
                @SuppressWarnings("unchecked") @Override
                public T minus(CustomType<?> other) { return (T) OperationRegistry.evaluate(self(), other, ArithmeticOperator.Binary.MINUS); }
                @SuppressWarnings("unchecked") @Override
                public T multiply(CustomType<?> other) { return (T) OperationRegistry.evaluate(self(), other, ArithmeticOperator.Binary.MULTIPLY); }
                @SuppressWarnings("unchecked") @Override
                public T divide(CustomType<?> other) { return (T) OperationRegistry.evaluate(self(), other, ArithmeticOperator.Binary.DIVIDE); }
                @SuppressWarnings("unchecked") @Override
                public T modulo(CustomType<?> other) { return (T) OperationRegistry.evaluate(self(), other, ArithmeticOperator.Binary.MODULO); }

                @SuppressWarnings("unchecked") @Override
                public T invert() { return (T) OperationRegistry.evaluate(self(), ArithmeticOperator.Unary.INVERT); }
                @SuppressWarnings("unchecked") @Override
                public T increment() { return (T) OperationRegistry.evaluate(self(), ArithmeticOperator.Unary.INCREMENT); }
                @SuppressWarnings("unchecked") @Override
                public T decrement() { return (T) OperationRegistry.evaluate(self(), ArithmeticOperator.Unary.DECREMENT); }

                @Override
                public boolean isSupport(ArithmeticOperator.Unary op) { return OperationRegistry.canEvaluate(getTypeClass(), op); }
                @Override
                public boolean isSupport(ArithmeticOperator.Binary op, CustomType<?> other) { return isSupport(op, other.getTypeClass()); }
                @Override
                public boolean isSupport(ArithmeticOperator.Binary op, Class<? extends CustomType<?>> otherClass) { return OperationRegistry.canEvaluate(getTypeClass(), otherClass, op); }
            };
        }
        return arithmeticPipeline;
    }

    public CustomLogic<T> getLogicPipeline() {
        if (logicPipeline == null) {
            logicPipeline = new CustomLogic<>() {
                @Override
                public PlusMinusType isLess(CustomType<?> other) { return (PlusMinusType) OperationRegistry.evaluate(self(), other, LogicalOperator.Binary.LESS); }
                @Override
                public PlusMinusType isMore(CustomType<?> other) { return (PlusMinusType) OperationRegistry.evaluate(self(), other, LogicalOperator.Binary.MORE); }
                @Override
                public PlusMinusType isEqual(CustomType<?> other) { return (PlusMinusType) OperationRegistry.evaluate(self(), other, LogicalOperator.Binary.EQUAL); }
                @Override
                public PlusMinusType isLessOrEqual(CustomType<?> other) { return (PlusMinusType) OperationRegistry.evaluate(self(), other, LogicalOperator.Binary.LESS_OR_EQUAL); }
                @Override
                public PlusMinusType isMoreOrEqual(CustomType<?> other) { return (PlusMinusType) OperationRegistry.evaluate(self(), other, LogicalOperator.Binary.MORE_OR_EQUAL); }

                @SuppressWarnings("unchecked") @Override
                public T and(CustomType<?> other) { return (T) OperationRegistry.evaluate(self(), other, LogicalOperator.Binary.AND); }
                @SuppressWarnings("unchecked") @Override
                public T or(CustomType<?> other) { return (T) OperationRegistry.evaluate(self(), other, LogicalOperator.Binary.OR); }
                @SuppressWarnings("unchecked") @Override
                public T not() { return (T) OperationRegistry.evaluate(self(), LogicalOperator.Unary.NOT); }

                @Override
                public boolean isSupport(LogicalOperator.Unary op) { return OperationRegistry.canEvaluate(getTypeClass(), op); }
                @Override
                public boolean isSupport(LogicalOperator.Binary op, CustomType<?> other) { return isSupport(op, other.getTypeClass()); }
                @Override
                public boolean isSupport(LogicalOperator.Binary op, Class<? extends CustomType<?>> otherClass) { return OperationRegistry.canEvaluate(getTypeClass(), otherClass, op); }
            };
        }
        return logicPipeline;
    }

    public CustomBitwise<T> getBitwisePipeline() {
        if (bitwisePipeline == null) {
            bitwisePipeline = new CustomBitwise<>() {
                @SuppressWarnings("unchecked") @Override
                public T and(CustomType<?> other) { return (T) OperationRegistry.evaluate(self(), other, BitwiseOperator.Binary.AND); }
                @SuppressWarnings("unchecked") @Override
                public T or(CustomType<?> other) { return (T) OperationRegistry.evaluate(self(), other, BitwiseOperator.Binary.OR); }
                @SuppressWarnings("unchecked") @Override
                public T xor(CustomType<?> other) { return (T) OperationRegistry.evaluate(self(), other, BitwiseOperator.Binary.XOR); }
                @SuppressWarnings("unchecked") @Override
                public T not() { return (T) OperationRegistry.evaluate(self(), BitwiseOperator.Unary.NOT); }

                @SuppressWarnings("unchecked") @Override
                public T leftShift(int other) { return (T) OperationRegistry.evaluate(self(), other, BitwiseOperator.Shift.LEFT_SHIFT); }

                @SuppressWarnings("unchecked") @Override
                public T rightShift(int other) { return (T) OperationRegistry.evaluate(self(), other, BitwiseOperator.Shift.RIGHT_SHIFT); }

                @SuppressWarnings("unchecked") @Override
                public T uRightShift(int other) { return (T) OperationRegistry.evaluate(self(), other, BitwiseOperator.Shift.U_RIGHT_SHIFT); }

                @Override
                public boolean isSupport(BitwiseOperator.Unary op) { return OperationRegistry.canEvaluate(getTypeClass(), op); }
                @Override
                public boolean isSupport(BitwiseOperator.Binary op, CustomType<?> other) { return isSupport(op, other.getTypeClass()); }
                @Override
                public boolean isSupport(BitwiseOperator.Binary op, Class<? extends CustomType<?>> otherClass) { return OperationRegistry.canEvaluate(getTypeClass(), otherClass, op); }
                @Override
                public boolean isSupport(BitwiseOperator.Shift op) { return OperationRegistry.canEvaluate(getTypeClass(), op); }
            };
        }
        return bitwisePipeline;
    }

    @Override public abstract String toString();
}