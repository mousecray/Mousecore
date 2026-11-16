/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype.values;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.NumberType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.api.utils.MouseNumbers;
import ru.mousecray.mousecore.api.utils.MouseStrings;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public final class TimeType extends NumberType<Long> {
    public enum Unit {
        TICKS("t", 1L),
        SECONDS("s", 20L),
        MINUTES("m", 20L * 60L),
        HOURS("h", 20L * 60L * 60L),
        DAYS("d", 20L * 60L * 60L * 24L);

        private final String suffix;
        private final long   ticksMultiplier;

        Unit(String suffix, long ticksMultiplier) {
            this.suffix = suffix;
            this.ticksMultiplier = ticksMultiplier;
        }

        public String getSuffix()        { return suffix; }
        public long getTicksMultiplier() { return ticksMultiplier; }

        @Nullable
        public static Unit fromSuffix(String suffix) {
            for (Unit unit : values()) if (unit.suffix.equalsIgnoreCase(suffix)) return unit;
            return null;
        }

        @Nullable public static Unit fromChar(char c) { return fromSuffix(String.valueOf(c)); }
    }

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

    @SuppressWarnings("ManualMinMaxCalculation") private TimeType(long ticks) { super(ticks < 0L ? 0L : ticks); }

    public static TimeType create(long ticks) {
        if (ticks <= 0L) return ZERO;
        if (ticks == Long.MAX_VALUE) return MAX;
        return new TimeType(ticks);
    }

    public static TimeType create(double value, Unit unit) {
        return create(MouseNumbers.round(value * unit.getTicksMultiplier()));
    }

    public static TimeType create(long value, Unit unit) {
        return create(Math.multiplyExact(value, unit.getTicksMultiplier()));
    }

    public static TimeType parse(String raw) {
        String clean = MouseStrings.trimWith(raw, true, '\t');
        if (clean == null || clean.isEmpty()) throw new ValueFormatException("Cannot parse empty time string");

        char   lastChar   = clean.charAt(clean.length() - 1);
        Unit   unit       = Unit.SECONDS;
        String numberPart = clean;

        if (Character.isLetter(lastChar)) {
            unit = Unit.fromChar(lastChar);
            if (unit == null) throw new ValueFormatException("Unknown time unit: '" + lastChar + "' in '" + raw + "'");
            numberPart = clean.substring(0, clean.length() - 1).trim();
        }

        Double num = MouseNumbers.tryParseDouble(numberPart);
        if (num == null) throw new ValueFormatException("Invalid number format in time string: '" + raw + "'");

        return create(num, unit);
    }

    public long asTicks()                  { return value; }
    public double asSeconds()              { return (double) value / Unit.SECONDS.getTicksMultiplier(); }
    public double asMinutes()              { return (double) value / Unit.MINUTES.getTicksMultiplier(); }
    public double asHours()                { return (double) value / Unit.HOURS.getTicksMultiplier(); }
    public double asDays()                 { return (double) value / Unit.DAYS.getTicksMultiplier(); }

    @Override public IValType getValType() { return ValType.TIME; }
    @Override public Long asNumber()       { return value; }

    @SuppressWarnings("unchecked") @Override
    public TimeType createType(Number value) { return create(value.longValue()); }

    @Override
    public String toString() {
        if (value == 0L) return "0s";
        if (value % Unit.DAYS.getTicksMultiplier() == 0L) return (value / Unit.DAYS.getTicksMultiplier()) + "d";
        if (value % Unit.HOURS.getTicksMultiplier() == 0L) return (value / Unit.HOURS.getTicksMultiplier()) + "h";
        if (value % Unit.MINUTES.getTicksMultiplier() == 0L) return (value / Unit.MINUTES.getTicksMultiplier()) + "m";
        if (value % Unit.SECONDS.getTicksMultiplier() == 0L) return (value / Unit.SECONDS.getTicksMultiplier()) + "s";
        return value + "t";
    }
}