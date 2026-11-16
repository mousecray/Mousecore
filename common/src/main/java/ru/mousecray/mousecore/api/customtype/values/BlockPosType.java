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
public final class BlockPosType extends Vector3Type<IntegralType, BlockPosType> {
    public static final BlockPosType ZERO = new BlockPosType(IntegralType.NULL, IntegralType.NULL, IntegralType.NULL);

    private static final int  PACKED_X_LENGTH = 26;
    private static final int  PACKED_Z_LENGTH = 26;
    private static final int  PACKED_Y_LENGTH = 64 - PACKED_X_LENGTH - PACKED_Z_LENGTH;
    private static final long PACKED_X_MASK   = (1L << PACKED_X_LENGTH) - 1L;
    private static final long PACKED_Y_MASK   = (1L << PACKED_Y_LENGTH) - 1L;
    private static final long PACKED_Z_MASK   = (1L << PACKED_Z_LENGTH) - 1L;
    private static final int  PACKED_Y_SHIFT  = 0;
    private static final int  PACKED_Z_SHIFT  = PACKED_Y_LENGTH;
    private static final int  PACKED_X_SHIFT  = PACKED_Y_LENGTH + PACKED_Z_LENGTH;

    static {
        storage.put(BlockPosType.class, str -> {
            str = MouseStrings.trimWith(str, true, '\t');
            if (str == null || str.isEmpty()) throw new ValueFormatException("Empty block pos string");
            return parse(str);
        });
    }

    private BlockPosType(IntegralType x, IntegralType y, IntegralType z)              { super(x, y, z); }

    public static BlockPosType create(IntegralType x, IntegralType y, IntegralType z) { return new BlockPosType(x, y, z); }

    public static BlockPosType create(long x, long y, long z) {
        return new BlockPosType(IntegralType.create(x), IntegralType.create(y), IntegralType.create(z));
    }

    public static BlockPosType parse(@Nullable String raw) {
        String clean = MouseStrings.trimWith(raw, true, '\t');
        if (clean == null || clean.isEmpty()) throw new ValueFormatException("Cannot parse empty BlockPos");

        if (clean.startsWith("(") && clean.endsWith(")")) clean = clean.substring(1, clean.length() - 1).trim();

        String[] parts = clean.contains(";") ? clean.split(";") : clean.split(",");
        if (parts.length != 3) throw new ValueFormatException("BlockPos requires 3 integer coordinates (x, y, z). Got: '" + raw + "'");

        Long x = MouseNumbers.tryParseLong(MouseStrings.trimWith(parts[0], true, '\t'));
        Long y = MouseNumbers.tryParseLong(MouseStrings.trimWith(parts[1], true, '\t'));
        Long z = MouseNumbers.tryParseLong(MouseStrings.trimWith(parts[2], true, '\t'));

        if (x == null || y == null || z == null) throw new ValueFormatException("Invalid integer coordinates in: '" + raw + "'");

        return create(x, y, z);
    }

    public long asLongX() { return x.asLong(); }
    public long asLongY() { return y.asLong(); }
    public long asLongZ() { return z.asLong(); }

    public int asIntX()   { return x.asInt(); }
    public int asIntY()   { return y.asInt(); }
    public int asIntZ()   { return z.asInt(); }

    @Override
    public double lengthSq() {
        long dx = asLongX(), dy = asLongY(), dz = asLongZ();
        return (double) (dx * dx + dy * dy + dz * dz);
    }

    public BlockPosType above()         { return create(asLongX(), asLongY() + 1L, asLongZ()); }
    public BlockPosType above(int dist) { return create(asLongX(), asLongY() + dist, asLongZ()); }
    public BlockPosType below()         { return create(asLongX(), asLongY() - 1L, asLongZ()); }
    public BlockPosType below(int dist) { return create(asLongX(), asLongY() - dist, asLongZ()); }
    public BlockPosType north()         { return create(asLongX(), asLongY(), asLongZ() - 1L); }
    public BlockPosType south()         { return create(asLongX(), asLongY(), asLongZ() + 1L); }
    public BlockPosType west()          { return create(asLongX() - 1L, asLongY(), asLongZ()); }
    public BlockPosType east()          { return create(asLongX() + 1L, asLongY(), asLongZ()); }

    public long asLong() {
        return ((asLongX() & PACKED_X_MASK) << PACKED_X_SHIFT)
                | ((asLongY() & PACKED_Y_MASK) << PACKED_Y_SHIFT)
                | ((asLongZ() & PACKED_Z_MASK) << PACKED_Z_SHIFT);
    }

    public static BlockPosType createFromLong(long packed) {
        long x = (packed << (64 - PACKED_X_SHIFT - PACKED_X_LENGTH)) >> (64 - PACKED_X_LENGTH);
        long y = (packed << (64 - PACKED_Y_SHIFT - PACKED_Y_LENGTH)) >> (64 - PACKED_Y_LENGTH);
        long z = (packed << (64 - PACKED_Z_SHIFT - PACKED_Z_LENGTH)) >> (64 - PACKED_Z_LENGTH);
        return create(x, y, z);
    }

    public Vector3dType asVector3d()       { return Vector3dType.create(asLongX() + 0.5D, asLongY() + 0.5D, asLongZ() + 0.5D); }

    @Override public IValType getValType() { return ValType.BLOCK_POS; }
}