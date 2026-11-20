/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.core.config.val.vec3;

import ru.mousecray.mousecore.api.VariableValue;
import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.vec3.ConfigValVector3d;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.range.RangeContainer;
import ru.mousecray.mousecore.api.customtype.values.number.DecimalType;
import ru.mousecray.mousecore.api.customtype.values.vec3.BlockPosType;
import ru.mousecray.mousecore.api.customtype.values.vec3.Vector3dType;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.core.config.val.base.ConfigValVector3Impl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@FieldsAreNonnullByDefault
@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public class ConfigValVector3dImpl extends ConfigValVector3Impl<DecimalType, Vector3dType> implements ConfigValVector3d {
    @SafeVarargs
    public ConfigValVector3dImpl(
            Vector3dType defaultValue, Vector3dType disabledValue,
            @Nullable RangeContainer<DecimalType> rangeX,
            @Nullable RangeContainer<DecimalType> rangeY,
            @Nullable RangeContainer<DecimalType> rangeZ,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<Vector3dType>... configureValues
    ) {
        super(defaultValue, disabledValue, rangeX, rangeY, rangeZ, "(0.0, 0.0, 0.0)", constraints, configureValues);
    }

    @Override
    protected Vector3dType createVector(DecimalType x, DecimalType y, DecimalType z) {
        return Vector3dType.create(x, y, z);
    }

    @Override
    protected VariableValue<Vector3dType> parseValue(@Nullable String value) {
        try {
            return VariableValue.create(CustomType.parse(Vector3dType.class, value));
        } catch (ValueFormatException ignore) { return VariableValue.create(); }
    }

    @Override public double getDoubleX()                          { return getValue().asDoubleX(); }
    @Override public double getDoubleY()                          { return getValue().asDoubleY(); }
    @Override public double getDoubleZ()                          { return getValue().asDoubleZ(); }
    @Override public void setDouble(double x, double y, double z) { setValue(Vector3dType.create(x, y, z)); }
    @Override public BlockPosType asBlockPos()                    { return getValue().asBlockPos(); }
}