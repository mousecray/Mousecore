/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype.values;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.Vector3Type;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.api.utils.MouseNumbers;
import ru.mousecray.mousecore.api.utils.MouseStrings;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public final class Vector3dType extends Vector3Type<DecimalType, Vector3dType> {
    public static final Vector3dType ZERO = new Vector3dType(DecimalType.NULL, DecimalType.NULL, DecimalType.NULL);

    static {
        storage.put(Vector3dType.class, str -> {
            str = MouseStrings.trimWith(str, true, '\t');
            if (str == null || str.isEmpty()) throw new ValueFormatException("Empty vector string");
            return parse(str);
        });
    }

    private Vector3dType(DecimalType x, DecimalType y, DecimalType z) {
        super(x, y, z);
    }

    public static Vector3dType create(DecimalType x, DecimalType y, DecimalType z) {
        return new Vector3dType(x, y, z);
    }

    public static Vector3dType create(double x, double y, double z) {
        return new Vector3dType(DecimalType.create(x), DecimalType.create(y), DecimalType.create(z));
    }

    public static Vector3dType parse(@Nullable String raw) {
        String clean = MouseStrings.trimWith(raw, true, '\t');
        if (clean == null || clean.isEmpty()) throw new ValueFormatException("Cannot parse empty vector");

        if (clean.startsWith("(") && clean.endsWith(")")) clean = clean.substring(1, clean.length() - 1).trim();

        String[] parts = clean.contains(";") ? clean.split(";") : clean.split(",");
        if (parts.length != 3) throw new ValueFormatException("Vector3d requires 3 coordinates (x, y, z). Got: '" + raw + "'");

        Double x = MouseNumbers.tryParseDouble(MouseStrings.trimWith(parts[0], true, '\t'));
        Double y = MouseNumbers.tryParseDouble(MouseStrings.trimWith(parts[1], true, '\t'));
        Double z = MouseNumbers.tryParseDouble(MouseStrings.trimWith(parts[2], true, '\t'));

        if (x == null || y == null || z == null) throw new ValueFormatException("Invalid coordinate numbers in: '" + raw + "'");

        return create(x, y, z);
    }

    public double asDoubleX() { return x.asDouble(); }
    public double asDoubleY() { return y.asDouble(); }
    public double asDoubleZ() { return z.asDouble(); }

    @Override
    public double lengthSq() {
        double dx = asDoubleX(), dy = asDoubleY(), dz = asDoubleZ();
        return dx * dx + dy * dy + dz * dz;
    }

    public BlockPosType asBlockPos() {
        return BlockPosType.create(
                MouseNumbers.floor(asDoubleX()),
                MouseNumbers.floor(asDoubleY()),
                MouseNumbers.floor(asDoubleZ())
        );
    }

    @Override public IValType getValType() { return ValType.VECTOR3D; }
}