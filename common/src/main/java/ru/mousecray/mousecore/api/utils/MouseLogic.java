/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.utils;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

public final class MouseLogic {
    public static <T> Predicate<T> invert(Predicate<T> predicate) { return val -> !predicate.test(val); }

    public static <T, D> BiPredicate<T, D> invert(BiPredicate<T, D> predicate) {
        return (val1, val2) -> !predicate.test(val1, val2);
    }

    public static boolean invert(boolean val) { return !val; }

    public static <T> Predicate<T> multiply(Predicate<T> predicate1, Predicate<T> predicate2) {
        return val -> predicate1.test(val) && predicate2.test(val);
    }

    public static <T, D> BiPredicate<T, D> multiply(BiPredicate<T, D> predicate1, BiPredicate<T, D> predicate2) {
        return (val1, val2) -> predicate1.test(val1, val2) && predicate2.test(val1, val2);
    }

    public static boolean multiply(boolean val1, boolean val2) { return val1 && val2; }
    public static boolean and(boolean val1, boolean val2)      { return val1 && val2; }

    public static <T> Predicate<T> plus(Predicate<T> predicate1, Predicate<T> predicate2) {
        return val -> predicate1.test(val) || predicate2.test(val);
    }

    public static <T, D> BiPredicate<T, D> plus(BiPredicate<T, D> predicate1, BiPredicate<T, D> predicate2) {
        return (val1, val2) -> predicate1.test(val1, val2) || predicate2.test(val1, val2);
    }

    public static boolean plus(boolean val1, boolean val2) { return val1 || val2; }
    public static boolean or(boolean val1, boolean val2)   { return val1 || val2; }

    public static <T> Predicate<T> minus(Predicate<T> predicate1, Predicate<T> predicate2) {
        return val -> predicate1.test(val) || !predicate2.test(val);
    }

    public static <T, D> BiPredicate<T, D> minus(BiPredicate<T, D> predicate1, BiPredicate<T, D> predicate2) {
        return (val1, val2) -> predicate1.test(val1, val2) || !predicate2.test(val1, val2);
    }

    public static boolean minus(boolean val1, boolean val2) { return val1 || !val2; }

    public static <T> Predicate<T> divide(Predicate<T> predicate1, Predicate<T> predicate2) {
        return val -> predicate1.test(val) && !predicate2.test(val);
    }

    public static <T, D> BiPredicate<T, D> divide(BiPredicate<T, D> predicate1, BiPredicate<T, D> predicate2) {
        return (val1, val2) -> predicate1.test(val1, val2) && !predicate2.test(val1, val2);
    }

    public static boolean divide(boolean val1, boolean val2) { return val1 && !val2; }

    public static <T> Predicate<T> modulo(Predicate<T> predicate1, Predicate<T> predicate2) {
        return val -> !predicate1.test(val) || predicate2.test(val);
    }

    public static <T, D> BiPredicate<T, D> modulo(BiPredicate<T, D> predicate1, BiPredicate<T, D> predicate2) {
        return (val1, val2) -> !predicate1.test(val1, val2) || predicate2.test(val1, val2);
    }

    public static boolean modulo(boolean val1, boolean val2)        { return !val1 || val2; }

    public static boolean isLess(String val1, String val2)          { return MouseStrings.compare(val1, val2) < 0; }
    public static boolean isLess(Number val1, Number val2)          { return val1.doubleValue() < val2.doubleValue(); }
    public static boolean isLess(boolean val1, boolean val2)        { return !val1 && val2; }
    public static boolean isLess(long val1, long val2)              { return val1 < val2; }
    public static boolean isLess(double val1, double val2)          { return val1 < val2; }

    public static boolean isMore(String val1, String val2)          { return MouseStrings.compare(val1, val2) > 0; }
    public static boolean isMore(Number val1, Number val2)          { return val1.doubleValue() > val2.doubleValue(); }
    public static boolean isMore(boolean val1, boolean val2)        { return val1 && !val2; }
    public static boolean isMore(long val1, long val2)              { return val1 > val2; }
    public static boolean isMore(double val1, double val2)          { return val1 > val2; }

    public static boolean isEqual(String val1, String val2)         { return MouseStrings.compare(val1, val2) == 0; }
    public static boolean isEqual(Number val1, Number val2)         { return val1.doubleValue() == val2.doubleValue(); }
    public static boolean isEqual(boolean val1, boolean val2)       { return val1 == val2; }
    public static boolean isEqual(long val1, long val2)             { return val1 == val2; }
    public static boolean isEqual(double val1, double val2)         { return val1 == val2; }

    public static boolean isMoreOrEqual(String val1, String val2)   { return MouseStrings.compare(val1, val2) >= 0; }
    public static boolean isMoreOrEqual(Number val1, Number val2)   { return val1.doubleValue() >= val2.doubleValue(); }
    public static boolean isMoreOrEqual(boolean val1, boolean val2) { return val1; }
    public static boolean isMoreOrEqual(long val1, long val2)       { return val1 >= val2; }
    public static boolean isMoreOrEqual(double val1, double val2)   { return val1 >= val2; }

    public static boolean isLessOrEqual(String val1, String val2)   { return MouseStrings.compare(val1, val2) <= 0; }
    public static boolean isLessOrEqual(Number val1, Number val2)   { return val1.doubleValue() <= val2.doubleValue(); }
    @SuppressWarnings("unused")
    public static boolean isLessOrEqual(boolean val1, boolean val2) { return val2; }
    public static boolean isLessOrEqual(long val1, long val2)     { return val1 <= val2; }
    public static boolean isLessOrEqual(double val1, double val2) { return val1 <= val2; }
}