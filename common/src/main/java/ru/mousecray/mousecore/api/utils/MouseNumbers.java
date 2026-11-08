/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.utils;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public final class MouseNumbers {
    private static final DecimalFormatSymbols formatterSymbols = new DecimalFormatSymbols();
    private static final DecimalFormat        formatterDecimal;
    private static final DecimalFormat        formatterDecimalSimple;
    private static final DecimalFormat        formatterInteger;

    static {
        formatterSymbols.setGroupingSeparator(' ');
        formatterSymbols.setDecimalSeparator('.');
        formatterDecimal = new DecimalFormat("#,##0.00###", formatterSymbols);
        formatterDecimalSimple = new DecimalFormat("#,##0.#####", formatterSymbols);
        formatterInteger = new DecimalFormat("#,##0", formatterSymbols);
    }

    public static short toShortExact(long number) {
        if ((short) number != number) throw new ArithmeticException("short overflow");
        return (short) number;
    }

    public static byte toByteExact(long number) {
        if ((byte) number != number) throw new ArithmeticException("byte overflow");
        return (byte) number;
    }

    public static float toFloatExact(double number) {
        if ((float) number != number) throw new ArithmeticException("float overflow");
        return (float) number;
    }

    public static long floor(double number) {
        long i = (long) number;
        return number < (double) i ? i - 1 : i;
    }

    public static long ceil(double number) {
        long i = (long) number;
        return number > (double) i ? i + 1 : i;
    }

    @Nullable
    public static Long tryParseLong(@Nullable String value, @Nullable Long def) {
        if (value == null) return def;
        try { return Long.parseLong(value); } catch (NumberFormatException ignore) { return def; }
    }

    @Nullable
    public static Integer tryParseInt(@Nullable String value, @Nullable Integer def) {
        if (value == null) return def;
        try { return Integer.parseInt(value); } catch (NumberFormatException ignore) { return def; }
    }

    @Nullable
    public static Short tryParseShort(@Nullable String value, @Nullable Short def) {
        if (value == null) return def;
        try { return Short.parseShort(value); } catch (NumberFormatException ignore) { return def; }
    }

    @Nullable
    public static Byte tryParseByte(@Nullable String value, @Nullable Byte def) {
        if (value == null) return def;
        try { return Byte.parseByte(value); } catch (NumberFormatException ignore) { return def; }
    }

    @Nullable
    public static Double tryParseDouble(@Nullable String value, @Nullable Double def) {
        if (value == null) return def;
        try { return Double.parseDouble(value); } catch (NumberFormatException ignore) { return def; }
    }

    @Nullable
    public static Float tryParseFloat(@Nullable String value, @Nullable Float def) {
        if (value == null) return def;
        try { return Float.parseFloat(value); } catch (NumberFormatException ignore) { return def; }
    }

    @Nullable public static Long tryParseLong(@Nullable String value)     { return tryParseLong(value, null); }
    @Nullable public static Integer tryParseInt(@Nullable String value)   { return tryParseInt(value, null); }
    @Nullable public static Short tryParseShort(@Nullable String value)   { return tryParseShort(value, null); }
    @Nullable public static Byte tryParseByte(@Nullable String value)     { return tryParseByte(value, null); }
    @Nullable public static Double tryParseDouble(@Nullable String value) { return tryParseDouble(value, null); }
    @Nullable public static Float tryParseFloat(@Nullable String value)   { return tryParseFloat(value, null); }

    public static List<Integer> divideIntByMax(int count, int max) {
        List<Integer> list = new ArrayList<>();
        while (count > 0) {
            int curr = Math.min(count, max);
            list.add(curr);
            count -= curr;
        }
        return list;
    }

    public static String formatDecimal(double value, boolean hasPositivePlus, boolean simple) {
        String val = simple ? formatterDecimalSimple.format(value) : formatterDecimal.format(value);
        return hasPositivePlus && value > 0 ? "+" + val : val;
    }

    public static String formatLong(long value, boolean hasPositivePlus) {
        String val = formatterInteger.format(value);
        return hasPositivePlus && value > 0 ? "+" + val : val;
    }

    public static String formatObjectIfNumber(@Nullable Object value, boolean hasPositivePlus, boolean simple) {
        String   defString;
        Class<?> defClass = value != null ? value.getClass() : null;

        if (defClass == Float.class || defClass == Double.class)
            defString = formatDecimal(((Number) value).doubleValue(), hasPositivePlus, simple);
        else if (value instanceof Number) defString = formatLong(((Number) value).longValue(), hasPositivePlus);
        else defString = Objects.toString(value);

        return defString;
    }

    public static String formatObjectIfNumber(@Nullable Object value, boolean hasPositivePlus) {
        return formatObjectIfNumber(value, hasPositivePlus, false);
    }

    public static Number plus(Number val1, Number val2)     { return val1.doubleValue() + val2.doubleValue(); }
    public static Number minus(Number val1, Number val2)    { return val1.doubleValue() - val2.doubleValue(); }
    public static Number divide(Number val1, Number val2)   { return val1.doubleValue() / val2.doubleValue(); }
    public static Number multiply(Number val1, Number val2) { return val1.doubleValue() * val2.doubleValue(); }
    public static Number modulo(Number val1, Number val2)   { return val1.doubleValue() % val2.doubleValue(); }
    public static Number invert(Number val)                 { return -val.doubleValue(); }

    public static Number and(Number val1, Number val2) {
        return Double.longBitsToDouble(
                Double.doubleToRawLongBits(val1.doubleValue()) & Double.doubleToRawLongBits(val2.doubleValue())
        );
    }

    public static Number or(Number val1, Number val2) {
        return Double.longBitsToDouble(
                Double.doubleToRawLongBits(val1.doubleValue()) | Double.doubleToRawLongBits(val2.doubleValue())
        );
    }

    public static Number xor(Number val1, Number val2) {
        return Double.longBitsToDouble(
                Double.doubleToRawLongBits(val1.doubleValue()) ^ Double.doubleToRawLongBits(val2.doubleValue())
        );
    }

    public static Number not(Number val1) {
        return Double.longBitsToDouble(~Double.doubleToRawLongBits(val1.doubleValue()));
    }

    public static Number leftShift(Number val1, int val2) {
        return Double.longBitsToDouble(Double.doubleToRawLongBits(val1.doubleValue()) << val2);
    }

    public static Number rightShift(Number val1, int val2) {
        return Double.longBitsToDouble(Double.doubleToRawLongBits(val1.doubleValue()) >> val2);
    }

    public static Number uRightShift(Number val1, int val2) {
        return Double.longBitsToDouble(Double.doubleToRawLongBits(val1.doubleValue()) >>> val2);
    }

    public static final Boolean   NULL_BOOLEAN = false;
    public static final Character NULL_CHAR    = 0;
    public static final Long      NULL_LONG    = 0L;
    public static final Integer   NULL_INTEGER = 0;
    public static final Short     NULL_SHORT   = 0;
    public static final Byte      NULL_BYTE    = 0;
    public static final Double    NULL_DOUBLE  = 0D;
    public static final Float     NULL_FLOAT   = 0F;
}