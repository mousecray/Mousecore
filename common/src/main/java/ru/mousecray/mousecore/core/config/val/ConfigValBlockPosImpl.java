/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.core.config.val;

import ru.mousecray.mousecore.api.VariableValue;
import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.ConfigValBlockPos;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.range.RangeContainer;
import ru.mousecray.mousecore.api.customtype.values.number.IntegralType;
import ru.mousecray.mousecore.api.customtype.values.vec3.BlockPosType;
import ru.mousecray.mousecore.api.customtype.values.vec3.Vector3dType;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.core.config.val.base.ConfigValVector3Impl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@FieldsAreNonnullByDefault
@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public class ConfigValBlockPosImpl extends ConfigValVector3Impl<IntegralType, BlockPosType> implements ConfigValBlockPos {
    @SafeVarargs
    public ConfigValBlockPosImpl(
            BlockPosType defaultValue, BlockPosType disabledValue,
            @Nullable RangeContainer<IntegralType> rangeX,
            @Nullable RangeContainer<IntegralType> rangeY,
            @Nullable RangeContainer<IntegralType> rangeZ,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<BlockPosType>... configureValues
    ) {
        super(defaultValue, disabledValue, rangeX, rangeY, rangeZ, "(0, 0, 0)", constraints, configureValues);
    }

    @Override
    protected BlockPosType createVector(IntegralType x, IntegralType y, IntegralType z) {
        return BlockPosType.create(x, y, z);
    }

    @Override
    protected VariableValue<BlockPosType> parseValue(@Nullable String value) {
        try {
            return VariableValue.create(CustomType.parse(BlockPosType.class, value));
        } catch (ValueFormatException ignore) { return VariableValue.create(); }
    }

    @Override public long getLongX()                      { return getValue().asLongX(); }
    @Override public long getLongY()                      { return getValue().asLongY(); }
    @Override public long getLongZ()                      { return getValue().asLongZ(); }
    @Override public int getIntX()                        { return getValue().asIntX(); }
    @Override public int getIntY()                        { return getValue().asIntY(); }
    @Override public int getIntZ()                        { return getValue().asIntZ(); }
    @Override public void setLong(long x, long y, long z) { setValue(BlockPosType.create(x, y, z)); }
    @Override public void setInt(int x, int y, int z)     { setValue(BlockPosType.create(x, y, z)); }
    @Override public Vector3dType asVector3d()            { return getValue().asVector3d(); }
}