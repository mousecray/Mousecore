/* ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  ***************************************************************************** */

package ru.mousecray.mousecore.api.customtype.values;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.NumberType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.api.utils.MouseNumbers;
import ru.mousecray.mousecore.api.utils.MouseStrings;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public final class TimeType extends NumberType<Long> {
    public static final long TICKS_PER_SECOND = 20L;
    public static final long TICKS_PER_MINUTE = TICKS_PER_SECOND * 60L;
    public static final long TICKS_PER_HOUR   = TICKS_PER_MINUTE * 60L;
    public static final long TICKS_PER_DAY    = TICKS_PER_HOUR * 24L;

    public static final TimeType ZERO = new TimeType(0L);
    public static final TimeType MIN  = new TimeType(0L);
    public static final TimeType MAX  = new TimeType(Long.MAX_VALUE);

    static {
        storage.put(TimeType.class, str -> {
            str = MouseStrings.trimWith(str, true, '\t');
            if (str == null || str.isEmpty()) throw new ValueFormatException("Empty time string");
            return parse(str);
        });
    }

    private TimeType(long ticks) {
        super(Math.max(0L, ticks));
    }

    public static TimeType ofTicks(long ticks) {
        if (ticks <= 0L) return ZERO;
        if (ticks == Long.MAX_VALUE) return MAX;
        return new TimeType(ticks);
    }

    public static TimeType ofSeconds(double seconds) {
        return ofTicks(Math.round(seconds * TICKS_PER_SECOND));
    }

    public static TimeType ofMinutes(double minutes) {
        return ofTicks(Math.round(minutes * TICKS_PER_MINUTE));
    }

    public static TimeType ofHours(double hours) {
        return ofTicks(Math.round(hours * TICKS_PER_HOUR));
    }

    public static TimeType parse(String raw) {
        String clean = MouseStrings.trimWith(raw, true, '\t');
        if (clean == null || clean.isEmpty()) throw new ValueFormatException("Cannot parse empty time string");

        char   lastChar   = clean.charAt(clean.length() - 1);
        long   multiplier = TICKS_PER_SECOND;
        String numberPart = clean;

        if (Character.isLetter(lastChar)) {
            numberPart = clean.substring(0, clean.length() - 1).trim();
            multiplier = switch (Character.toLowerCase(lastChar)) {
                case 't' -> 1L;
                case 's' -> TICKS_PER_SECOND;
                case 'm' -> TICKS_PER_MINUTE;
                case 'h' -> TICKS_PER_HOUR;
                case 'd' -> TICKS_PER_DAY;
                default -> throw new ValueFormatException("Unknown time unit: '" + lastChar + "' in '" + raw + "'");
            };
        }

        Double num = MouseNumbers.tryParseDouble(numberPart);
        if (num == null) throw new ValueFormatException("Invalid number format in time string: '" + raw + "'");

        return ofTicks(Math.round(num * multiplier));
    }

    public long getTicks()                 { return value; }
    public double getSeconds()             { return (double) value / TICKS_PER_SECOND; }
    public double getMinutes()             { return (double) value / TICKS_PER_MINUTE; }
    public double getHours()               { return (double) value / TICKS_PER_HOUR; }

    @Override public IValType getValType() { return ValType.TIME; }
    @Override public Long asNumber()       { return value; }

    @SuppressWarnings("unchecked")
    @Override
    public TimeType createType(Number value) {
        return ofTicks(value.longValue());
    }

    @Override
    public String toString() {
        if (value == 0L) return "0s";
        if (value % TICKS_PER_DAY == 0L) return (value / TICKS_PER_DAY) + "d";
        if (value % TICKS_PER_HOUR == 0L) return (value / TICKS_PER_HOUR) + "h";
        if (value % TICKS_PER_MINUTE == 0L) return (value / TICKS_PER_MINUTE) + "m";
        if (value % TICKS_PER_SECOND == 0L) return (value / TICKS_PER_SECOND) + "s";
        return value + "t";
    }
}