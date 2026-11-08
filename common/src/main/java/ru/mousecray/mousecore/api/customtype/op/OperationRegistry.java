/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype.op;

import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.error.UnsupportedValException;
import ru.mousecray.mousecore.api.utils.MouseStrings;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiFunction;
import java.util.function.Function;

public class OperationRegistry {

    private static final List<BinaryRuleRecord<?, ?, ?>> BINARY_RULES = new CopyOnWriteArrayList<>();
    private static final List<UnaryRuleRecord<?, ?>>     UNARY_RULES  = new CopyOnWriteArrayList<>();
    private static final List<ShiftRuleRecord<?, ?>>     SHIFT_RULES  = new CopyOnWriteArrayList<>();
    private static final List<CastRuleRecord<?, ?>>      CAST_RULES   = new CopyOnWriteArrayList<>();

    private static final Map<String, BiFunction<?, ?, ?>>  BINARY_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, Function<?, ?>>       UNARY_CACHE  = new ConcurrentHashMap<>();
    private static final Map<String, ShiftOperation<?, ?>> SHIFT_CACHE  = new ConcurrentHashMap<>();
    private static final Map<String, Function<?, ?>>       CAST_CACHE   = new ConcurrentHashMap<>();

    static {
        StandardOperations.registerDefaults();
    }

    public static <L extends CustomType<?>, R extends CustomType<?>, RES extends CustomType<?>> void registerBinary(
            Class<L> leftType, Class<R> rightType, IBinaryOperator op, BiFunction<L, R, RES> action) {
        BINARY_RULES.add(new BinaryRuleRecord<>(leftType, rightType, op, action));
        BINARY_CACHE.clear();
    }

    public static <T extends CustomType<?>, RES extends CustomType<?>> void registerUnary(
            Class<T> type, IUnaryOperator op, Function<T, RES> action) {
        UNARY_RULES.add(new UnaryRuleRecord<>(type, op, action));
        UNARY_CACHE.clear();
    }

    public static <T extends CustomType<?>, RES extends CustomType<?>> void registerShift(
            Class<T> type, IShiftOperator op, ShiftOperation<T, RES> action) {
        SHIFT_RULES.add(new ShiftRuleRecord<>(type, op, action));
        SHIFT_CACHE.clear();
    }

    public static <S extends CustomType<?>, T extends CustomType<?>> void registerCast(
            Class<S> sourceType, Class<T> targetType, Function<S, T> action) {
        CAST_RULES.add(new CastRuleRecord<>(sourceType, targetType, action));
        CAST_CACHE.clear();
    }

    public static Object evaluateBinary(CustomType<?> left, CustomType<?> right, IBinaryOperator op) {
        if (left == null || right == null) throw new UnsupportedValException("Arguments cannot be null");

        String     cacheKey = left.getClass().getName() + "|" + right.getClass().getName() + "|" + op.toString();
        BiFunction action   = BINARY_CACHE.computeIfAbsent(cacheKey, k -> findBestBinaryMatch(left.getClass(), right.getClass(), op));

        if (action == null) {
            throw new UnsupportedValException(MouseStrings.format("Operation {0} not supported between {1} и {2}",
                    op, left.getClass().getSimpleName(), right.getClass().getSimpleName()));
        }
        return action.apply(left, right);
    }

    public static Object evaluateUnary(CustomType<?> target, IUnaryOperator op) {
        if (target == null) throw new UnsupportedValException("Argument cannot be null");

        String   cacheKey = target.getClass().getName() + "|" + op.toString();
        Function action   = UNARY_CACHE.computeIfAbsent(cacheKey, k -> findBestUnaryMatch(target.getClass(), op));

        if (action == null) {
            throw new UnsupportedValException(MouseStrings.format("Unary operation {0} not supported for {1}",
                    op, target.getClass().getSimpleName()));
        }
        return action.apply(target);
    }

    public static Object evaluateShift(CustomType<?> target, int shiftAmount, IShiftOperator op) {
        if (target == null) throw new UnsupportedValException("Аргумент не может быть null");

        String         cacheKey = target.getClass().getName() + "|" + op.toString();
        ShiftOperation action   = SHIFT_CACHE.computeIfAbsent(cacheKey, k -> findBestShiftMatch(target.getClass(), op));

        if (action == null) {
            throw new UnsupportedValException(String.format("Операция сдвига %s не поддерживается для %s",
                    op, target.getClass().getSimpleName()));
        }
        return action.execute(target, shiftAmount);
    }

    @SuppressWarnings("unchecked")
    public static <T extends CustomType<?>> T evaluateCast(CustomType<?> source, Class<T> targetClass) {
        if (source == null) throw new UnsupportedValException("Cast source cannot be null");
        if (targetClass.isInstance(source)) return (T) source;

        String   cacheKey = source.getClass().getName() + "|CAST_TO|" + targetClass.getName();
        Function action   = CAST_CACHE.computeIfAbsent(cacheKey, k -> findBestCastMatch(source.getClass(), targetClass));

        if (action == null) {
            throw new UnsupportedValException(MouseStrings.format("Cast of {0} to {1} not supported",
                    source.getClass().getSimpleName(), targetClass.getSimpleName()));
        }
        return (T) action.apply(source);
    }

    private static BiFunction<?, ?, ?> findBestBinaryMatch(Class<?> leftCls, Class<?> rightCls, IBinaryOperator op) {
        for (BinaryRuleRecord<?, ?, ?> rule : BINARY_RULES) {
            if (rule.op.equals(op) && rule.leftType.isAssignableFrom(leftCls) && rule.rightType.isAssignableFrom(rightCls)) {
                return rule.action;
            }
        }
        return null;
    }

    private static Function<?, ?> findBestUnaryMatch(Class<?> targetCls, IUnaryOperator op) {
        for (UnaryRuleRecord<?, ?> rule : UNARY_RULES) {
            if (rule.op.equals(op) && rule.type.isAssignableFrom(targetCls)) {
                return rule.action;
            }
        }
        return null;
    }

    private static ShiftOperation<?, ?> findBestShiftMatch(Class<?> targetCls, IShiftOperator op) {
        for (ShiftRuleRecord<?, ?> rule : SHIFT_RULES) {
            if (rule.op.equals(op) && rule.type.isAssignableFrom(targetCls)) {
                return rule.action;
            }
        }
        return null;
    }

    private static Function<?, ?> findBestCastMatch(Class<?> sourceCls, Class<?> targetCls) {
        for (CastRuleRecord<?, ?> rule : CAST_RULES) {
            if (rule.sourceType.isAssignableFrom(sourceCls) && targetCls.isAssignableFrom(rule.targetType)) {
                return rule.action;
            }
        }
        return null;
    }

    private record BinaryRuleRecord<L, R, RES>(
            Class<L> leftType, Class<R> rightType, IBinaryOperator op, BiFunction<L, R, RES> action
    ) { }

    private record UnaryRuleRecord<T, RES>(Class<T> type, IUnaryOperator op, Function<T, RES> action) { }

    private record ShiftRuleRecord<T extends CustomType<?>, RES>(Class<T> type, IShiftOperator op, ShiftOperation<T, RES> action) { }

    private record CastRuleRecord<S, T>(Class<S> sourceType, Class<T> targetType, Function<S, T> action) { }
}