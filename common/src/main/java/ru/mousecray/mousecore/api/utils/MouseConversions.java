/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.utils;

import static java.lang.Character.getNumericValue;

public final class MouseConversions {
    public static Boolean charToBoolean(Object c, Boolean n)     { return c != null ? getNumericValue((Character) c) > 0 : n; }
    public static Character charToChar(Object c, Character n)    { return c != null ? (Character) c : n; }
    public static Long charToLong(Object c, Long n)              { return c != null ? (long) getNumericValue((Character) c) : n; }
    public static Integer charToInt(Object c, Integer n)         { return c != null ? getNumericValue((Character) c) : n; }
    public static Short charToShort(Object c, Short n)           { return c != null ? (short) getNumericValue((Character) c) : n; }
    public static Byte charToByte(Object c, Byte n)              { return c != null ? (byte) getNumericValue((Character) c) : n; }
    public static Double charToDouble(Object c, Double n)        { return c != null ? (double) getNumericValue((Character) c) : n; }
    public static Float charToFloat(Object c, Float n)           { return c != null ? (float) getNumericValue((Character) c) : n; }

    public static Boolean charToBoolean(Object c)                { return charToBoolean(c, null); }
    public static Character charToChar(Object c)                 { return charToChar(c, null); }
    public static Long charToLong(Object c)                      { return charToLong(c, null); }
    public static Integer charToInt(Object c)                    { return charToInt(c, null); }
    public static Short charToShort(Object c)                    { return charToShort(c, null); }
    public static Byte charToByte(Object c)                      { return charToByte(c, null); }
    public static Double charToDouble(Object c)                  { return charToDouble(c, null); }
    public static Float charToFloat(Object c)                    { return charToFloat(c, null); }

    public static Boolean booleanToBoolean(Object c, Boolean n)  { return c != null ? (Boolean) c : n; }
    public static Character booleanToChar(Object c, Character n) { return c != null ? ((Boolean) c) ? (char) 1 : (char) 0 : n; }
    public static Long booleanToLong(Object c, Long n)           { return c != null ? ((Boolean) c) ? 1L : 0L : n; }
    public static Integer booleanToInt(Object c, Integer n)      { return c != null ? ((Boolean) c) ? 1 : 0 : n; }
    public static Short booleanToShort(Object c, Short n)        { return c != null ? ((Boolean) c) ? (short) 1 : (short) 0 : n; }
    public static Byte booleanToByte(Object c, Byte n)           { return c != null ? ((Boolean) c) ? (byte) 1 : (byte) 0 : n; }
    public static Double booleanToDouble(Object c, Double n)     { return c != null ? ((Boolean) c) ? 1D : 0D : n; }
    public static Float booleanToFloat(Object c, Float n)        { return c != null ? ((Boolean) c) ? 1F : 0F : n; }

    public static Boolean booleanToBoolean(Object c)             { return booleanToBoolean(c, null); }
    public static Character booleanToChar(Object c)              { return booleanToChar(c, null); }
    public static Long booleanToLong(Object c)                   { return booleanToLong(c, null); }
    public static Integer booleanToInt(Object c)                 { return booleanToInt(c, null); }
    public static Short booleanToShort(Object c)                 { return booleanToShort(c, null); }
    public static Byte booleanToByte(Object c)                   { return booleanToByte(c, null); }
    public static Double booleanToDouble(Object c)               { return booleanToDouble(c, null); }
    public static Float booleanToFloat(Object c)                 { return booleanToFloat(c, null); }

    public static Boolean longToBoolean(Object c, Boolean n)     { return c != null ? (Long) c > 0 : n; }
    public static Character longToChar(Object c, Character n)    { return c != null ? (char) ((Long) c).intValue() : n; }
    public static Long longToLong(Object c, Long n)              { return c != null ? (Long) c : n; }
    public static Integer longToInt(Object c, Integer n)         { return c != null ? ((Long) c).intValue() : n; }
    public static Short longToShort(Object c, Short n)           { return c != null ? ((Long) c).shortValue() : n; }
    public static Byte longToByte(Object c, Byte n)              { return c != null ? ((Long) c).byteValue() : n; }
    public static Double longToDouble(Object c, Double n)        { return c != null ? ((Long) c).doubleValue() : n; }
    public static Float longToFloat(Object c, Float n)           { return c != null ? ((Long) c).floatValue() : n; }

    public static Boolean longToBoolean(Object c)                { return longToBoolean(c, null); }
    public static Character longToChar(Object c)                 { return longToChar(c, null); }
    public static Long longToLong(Object c)                      { return longToLong(c, null); }
    public static Integer longToInt(Object c)                    { return longToInt(c, null); }
    public static Short longToShort(Object c)                    { return longToShort(c, null); }
    public static Byte longToByte(Object c)                      { return longToByte(c, null); }
    public static Double longToDouble(Object c)                  { return longToDouble(c, null); }
    public static Float longToFloat(Object c)                    { return longToFloat(c, null); }

    public static Boolean intToBoolean(Object c, Boolean n)      { return c != null ? (Integer) c > 0 : n; }
    public static Character intToChar(Object c, Character n)     { return c != null ? (char) ((Integer) c).intValue() : n; }
    public static Long intToLong(Object c, Long n)               { return c != null ? ((Integer) c).longValue() : n; }
    public static Integer intToInt(Object c, Integer n)          { return c != null ? (Integer) c : n; }
    public static Short intToShort(Object c, Short n)            { return c != null ? ((Integer) c).shortValue() : n; }
    public static Byte intToByte(Object c, Byte n)               { return c != null ? ((Integer) c).byteValue() : n; }
    public static Double intToDouble(Object c, Double n)         { return c != null ? ((Integer) c).doubleValue() : n; }
    public static Float intToFloat(Object c, Float n)            { return c != null ? ((Integer) c).floatValue() : n; }

    public static Boolean intToBoolean(Object c)                 { return intToBoolean(c, null); }
    public static Character intToChar(Object c)                  { return intToChar(c, null); }
    public static Long intToLong(Object c)                       { return intToLong(c, null); }
    public static Integer intToInt(Object c)                     { return intToInt(c, null); }
    public static Short intToShort(Object c)                     { return intToShort(c, null); }
    public static Byte intToByte(Object c)                       { return intToByte(c, null); }
    public static Double intToDouble(Object c)                   { return intToDouble(c, null); }
    public static Float intToFloat(Object c)                     { return intToFloat(c, null); }

    public static Boolean shortToBoolean(Object c, Boolean n)    { return c != null ? (Short) c > 0 : n; }
    public static Character shortToChar(Object c, Character n)   { return c != null ? (char) ((Short) c).intValue() : n; }
    public static Long shortToLong(Object c, Long n)             { return c != null ? ((Short) c).longValue() : n; }
    public static Integer shortToInt(Object c, Integer n)        { return c != null ? ((Short) c).intValue() : n; }
    public static Short shortToShort(Object c, Short n)          { return c != null ? (Short) c : n; }
    public static Byte shortToByte(Object c, Byte n)             { return c != null ? ((Short) c).byteValue() : n; }
    public static Double shortToDouble(Object c, Double n)       { return c != null ? ((Short) c).doubleValue() : n; }
    public static Float shortToFloat(Object c, Float n)          { return c != null ? ((Short) c).floatValue() : n; }

    public static Boolean shortToBoolean(Object c)               { return shortToBoolean(c, null); }
    public static Character shortToChar(Object c)                { return shortToChar(c, null); }
    public static Long shortToLong(Object c)                     { return shortToLong(c, null); }
    public static Integer shortToInt(Object c)                   { return shortToInt(c, null); }
    public static Short shortToShort(Object c)                   { return shortToShort(c, null); }
    public static Byte shortToByte(Object c)                     { return shortToByte(c, null); }
    public static Double shortToDouble(Object c)                 { return shortToDouble(c, null); }
    public static Float shortToFloat(Object c)                   { return shortToFloat(c, null); }

    public static Boolean byteToBoolean(Object c, Boolean n)     { return c != null ? ((Byte) c) > 0 : n; }
    public static Character byteToChar(Object c, Character n)    { return c != null ? (char) ((Byte) c).intValue() : n; }
    public static Long byteToLong(Object c, Long n)              { return c != null ? ((Byte) c).longValue() : n; }
    public static Integer byteToInt(Object c, Integer n)         { return c != null ? ((Byte) c).intValue() : n; }
    public static Short byteToShort(Object c, Short n)           { return c != null ? ((Byte) c).shortValue() : n; }
    public static Byte byteToByte(Object c, Byte n)              { return c != null ? (Byte) c : n; }
    public static Double byteToDouble(Object c, Double n)        { return c != null ? ((Byte) c).doubleValue() : n; }
    public static Float byteToFloat(Object c, Float n)           { return c != null ? ((Byte) c).floatValue() : n; }

    public static Boolean byteToBoolean(Object c)                { return byteToBoolean(c, null); }
    public static Character byteToChar(Object c)                 { return byteToChar(c, null); }
    public static Long byteToLong(Object c)                      { return byteToLong(c, null); }
    public static Integer byteToInt(Object c)                    { return byteToInt(c, null); }
    public static Short byteToShort(Object c)                    { return byteToShort(c, null); }
    public static Byte byteToByte(Object c)                      { return byteToByte(c, null); }
    public static Double byteToDouble(Object c)                  { return byteToDouble(c, null); }
    public static Float byteToFloat(Object c)                    { return byteToFloat(c, null); }

    public static Boolean doubleToBoolean(Object c, Boolean n)   { return c != null ? ((Double) c) > 0 : n; }
    public static Character doubleToChar(Object c, Character n)  { return c != null ? (char) ((Double) c).intValue() : n; }
    public static Long doubleToLong(Object c, Long n)            { return c != null ? ((Double) c).longValue() : n; }
    public static Integer doubleToInt(Object c, Integer n)       { return c != null ? ((Double) c).intValue() : n; }
    public static Short doubleToShort(Object c, Short n)         { return c != null ? ((Double) c).shortValue() : n; }
    public static Byte doubleToByte(Object c, Byte n)            { return c != null ? ((Double) c).byteValue() : n; }
    public static Double doubleToDouble(Object c, Double n)      { return c != null ? (Double) c : n; }
    public static Float doubleToFloat(Object c, Float n)         { return c != null ? ((Double) c).floatValue() : n; }

    public static Boolean doubleToBoolean(Object c)              { return doubleToBoolean(c, null); }
    public static Character doubleToChar(Object c)               { return doubleToChar(c, null); }
    public static Long doubleToLong(Object c)                    { return doubleToLong(c, null); }
    public static Integer doubleToInt(Object c)                  { return doubleToInt(c, null); }
    public static Short doubleToShort(Object c)                  { return doubleToShort(c, null); }
    public static Byte doubleToByte(Object c)                    { return doubleToByte(c, null); }
    public static Double doubleToDouble(Object c)                { return doubleToDouble(c, null); }
    public static Float doubleToFloat(Object c)                  { return doubleToFloat(c, null); }

    public static Boolean floatToBoolean(Object c, Boolean n)    { return c != null ? ((Float) c) > 0 : n; }
    public static Character floatToChar(Object c, Character n)   { return c != null ? (char) ((Float) c).intValue() : n; }
    public static Long floatToLong(Object c, Long n)             { return c != null ? ((Float) c).longValue() : n; }
    public static Integer floatToInt(Object c, Integer n)        { return c != null ? ((Float) c).intValue() : n; }
    public static Short floatToShort(Object c, Short n)          { return c != null ? ((Float) c).shortValue() : n; }
    public static Byte floatToByte(Object c, Byte n)             { return c != null ? ((Float) c).byteValue() : n; }
    public static Double floatToDouble(Object c, Double n)       { return c != null ? ((Float) c).doubleValue() : n; }
    public static Float floatToFloat(Object c, Float n)          { return c != null ? (Float) c : n; }

    public static Boolean floatToBoolean(Object c)               { return floatToBoolean(c, null); }
    public static Character floatToChar(Object c)                { return floatToChar(c, null); }
    public static Long floatToLong(Object c)                     { return floatToLong(c, null); }
    public static Integer floatToInt(Object c)                   { return floatToInt(c, null); }
    public static Short floatToShort(Object c)                   { return floatToShort(c, null); }
    public static Byte floatToByte(Object c)                     { return floatToByte(c, null); }
    public static Double floatToDouble(Object c)                 { return floatToDouble(c, null); }
    public static Float floatToFloat(Object c)                   { return floatToFloat(c, null); }

    @SuppressWarnings("unchecked")
    public static <T> T anyToAny(Object o, Object ifNullVal) {
        if (o == null) o = ifNullVal;
        return (T) o;
    }

    @SuppressWarnings("unchecked")
    public static <T> T anyToAny(Object o) {
        return (T) o;
    }
}