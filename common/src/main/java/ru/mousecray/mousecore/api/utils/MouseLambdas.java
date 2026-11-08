/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.utils;

import ru.mousecray.mousecore.api.customtype.CustomType;

public class MouseLambdas {
    public static <T> T THIS(T val)                                           { return val; }

    public static <T> boolean ANY(T ignore)                                   { return true; }
    public static <T, D> boolean ANY(T ignore1, D ignore2)                    { return true; }

    public static <T, D> boolean NONE(T ignore1, D ignore2)                   { return false; }

    @SuppressWarnings("unchecked") public static <T> T TO_STRING(String val)  { return (T) val; }
    @SuppressWarnings("unchecked") public static <T> T TO_BOOLEAN(String val) { return (T) MouseUtils.tryParseBoolean(val); }
    @SuppressWarnings("unchecked") public static <T> T TO_CHAR(String val)    { return (T) MouseUtils.tryParseChar(val); }
    @SuppressWarnings("unchecked") public static <T> T TO_LONG(String val)    { return (T) MouseNumbers.tryParseLong(val); }
    @SuppressWarnings("unchecked") public static <T> T TO_INT(String val)     { return (T) MouseNumbers.tryParseInt(val); }
    @SuppressWarnings("unchecked") public static <T> T TO_SHORT(String val)   { return (T) MouseNumbers.tryParseShort(val); }
    @SuppressWarnings("unchecked") public static <T> T TO_BYTE(String val)    { return (T) MouseNumbers.tryParseByte(val); }
    @SuppressWarnings("unchecked") public static <T> T TO_DOUBLE(String val)  { return (T) MouseNumbers.tryParseDouble(val); }
    @SuppressWarnings("unchecked") public static <T> T TO_FLOAT(String val)   { return (T) MouseNumbers.tryParseFloat(val); }

    public static <T> T TO_CUSTOM(Class<T> clazz, String val) {
        return CustomType.parse(clazz, val);
    }
}