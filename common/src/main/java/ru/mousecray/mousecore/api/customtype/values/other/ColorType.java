/*******************************************************************************
 *  Copyright © 2026 mousecray
 *  Licensed under the GNU Lesser General Public License, Version 3.0
 ****************************************************************************** */

package ru.mousecray.mousecore.api.customtype.values.other;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.OtherType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.error.UnsupportedValException;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.api.utils.MouseNumbers;
import ru.mousecray.mousecore.api.utils.MouseStrings;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public final class ColorType extends OtherType<Integer> {
    public static final ColorType WHITE       = new ColorType(0xFFFFFFFF);
    public static final ColorType BLACK       = new ColorType(0xFF000000);
    public static final ColorType TRANSPARENT = new ColorType(0x00000000);
    public static final ColorType RED         = new ColorType(0xFFFF0000);
    public static final ColorType GREEN       = new ColorType(0xFF00FF00);
    public static final ColorType BLUE        = new ColorType(0xFF0000FF);

    static {
        storage.put(ColorType.class, str -> {
            str = MouseStrings.trimWith(str, true, '\t');
            if (str == null || str.isEmpty()) throw new ValueFormatException("Empty color string");
            return parse(str);
        });
    }

    private ColorType(int argb)              { super(argb); }

    public static ColorType create(int argb) { return new ColorType(argb); }

    public static ColorType create(int r, int g, int b, int a) {
        int cr = MouseNumbers.clamp(r, 0, 255);
        int cg = MouseNumbers.clamp(g, 0, 255);
        int cb = MouseNumbers.clamp(b, 0, 255);
        int ca = MouseNumbers.clamp(a, 0, 255);
        return new ColorType(((ca & 0xFF) << 24) | ((cr & 0xFF) << 16) | ((cg & 0xFF) << 8) | (cb & 0xFF));
    }

    public static ColorType create(int r, int g, int b) { return create(r, g, b, 255); }

    public static ColorType create(float r, float g, float b, float a) {
        return create(
                (int) MouseNumbers.round(MouseNumbers.clamp(r, 0.0F, 1.0F) * 255.0F),
                (int) MouseNumbers.round(MouseNumbers.clamp(g, 0.0F, 1.0F) * 255.0F),
                (int) MouseNumbers.round(MouseNumbers.clamp(b, 0.0F, 1.0F) * 255.0F),
                (int) MouseNumbers.round(MouseNumbers.clamp(a, 0.0F, 1.0F) * 255.0F)
        );
    }

    public static ColorType create(float r, float g, float b) { return create(r, g, b, 1.0F); }

    public static ColorType parse(@Nullable String raw) {
        String clean = MouseStrings.trimWith(raw, true, '\t');
        if (clean == null || clean.isEmpty()) throw new ValueFormatException("Cannot parse empty color string");

        if (clean.toLowerCase().startsWith("rgb") || clean.startsWith("(")) {
            int openIdx  = clean.indexOf('(');
            int closeIdx = clean.lastIndexOf(')');
            if (openIdx >= 0 && closeIdx > openIdx) {
                String   content = clean.substring(openIdx + 1, closeIdx).trim();
                String[] parts   = content.contains(";") ? content.split(";") : content.split(",");
                if (parts.length == 3 || parts.length == 4) {
                    Integer r = MouseNumbers.tryParseInt(MouseStrings.trimWith(parts[0], true, '\t'));
                    Integer g = MouseNumbers.tryParseInt(MouseStrings.trimWith(parts[1], true, '\t'));
                    Integer b = MouseNumbers.tryParseInt(MouseStrings.trimWith(parts[2], true, '\t'));
                    if (r != null && g != null && b != null) {
                        int a = 255;
                        if (parts.length == 4) {
                            String aStr = MouseStrings.trimWith(parts[3], true, '\t');
                            Double aDbl = MouseNumbers.tryParseDouble(aStr);
                            if (aDbl == null) throw new ValueFormatException("Invalid alpha channel format in: '" + raw + "'");
                            a = aDbl <= 1.0D && aStr.contains(".")
                                    ? (int) MouseNumbers.round(aDbl * 255.0D)
                                    : (int) MouseNumbers.round(aDbl);
                        }
                        return create(r, g, b, a);
                    }
                }
            }
            throw new ValueFormatException("Invalid functional color format: '" + raw + "'");
        }

        String hex = clean;
        if (hex.startsWith("#")) hex = hex.substring(1);
        else if (hex.startsWith("0x") || hex.startsWith("0X")) hex = hex.substring(2);
        hex = hex.trim();

        if (hex.length() == 3) {
            Integer r = MouseNumbers.tryParseInt(hex.substring(0, 1), 16);
            Integer g = MouseNumbers.tryParseInt(hex.substring(1, 2), 16);
            Integer b = MouseNumbers.tryParseInt(hex.substring(2, 3), 16);
            if (r != null && g != null && b != null) return create(r * 17, g * 17, b * 17, 255);
        } else if (hex.length() == 4) {
            Integer r = MouseNumbers.tryParseInt(hex.substring(0, 1), 16);
            Integer g = MouseNumbers.tryParseInt(hex.substring(1, 2), 16);
            Integer b = MouseNumbers.tryParseInt(hex.substring(2, 3), 16);
            Integer a = MouseNumbers.tryParseInt(hex.substring(3, 4), 16);
            if (r != null && g != null && b != null && a != null) return create(r * 17, g * 17, b * 17, a * 17);
        } else if (hex.length() == 6) {
            Long rgb = MouseNumbers.tryParseLong(hex, 16);
            if (rgb != null) return create(rgb.intValue() | 0xFF000000);
        } else if (hex.length() == 8) {
            Long argb = MouseNumbers.tryParseLong(hex, 16);
            if (argb != null) return create(argb.intValue());
        }

        throw new ValueFormatException("Invalid HEX color format: '" + raw + "'");
    }

    public int asArgb()         { return value; }
    public int asRgb()          { return value & 0x00FFFFFF; }
    public int asAlpha()        { return (value >> 24) & 0xFF; }
    public int asRed()          { return (value >> 16) & 0xFF; }
    public int asGreen()        { return (value >> 8) & 0xFF; }
    public int asBlue()         { return value & 0xFF; }

    public float asFloatAlpha() { return (float) asAlpha() / 255.0F; }
    public float asFloatRed()   { return (float) asRed() / 255.0F; }
    public float asFloatGreen() { return (float) asGreen() / 255.0F; }
    public float asFloatBlue()  { return (float) asBlue() / 255.0F; }

    public String asHex(boolean includeAlpha) {
        if (includeAlpha) return String.format("#%08X", value);
        else return String.format("#%06X", asRgb());
    }

    public String asHex()                  { return asHex(asAlpha() < 255); }

    @Override public IValType getValType() { return ValType.COLOR; }

    @SuppressWarnings("unchecked") @Override
    public <TYPE extends OtherType<Integer>> TYPE createType(Object val) {
        if (val instanceof ColorType) return (TYPE) val;
        else if (val instanceof Number) return (TYPE) create(((Number) val).intValue());
        else if (val instanceof String) return (TYPE) parse((String) val);
        throw new UnsupportedValException("Cannot create ColorType from " + val);
    }

    @Override public String toString() { return asHex(); }
}