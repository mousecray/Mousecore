/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.core.config.val.base;

import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.base.ConfigVector3Val;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.NumberType;
import ru.mousecray.mousecore.api.customtype.Vector3Type;
import ru.mousecray.mousecore.api.customtype.range.RangeContainer;
import ru.mousecray.mousecore.core.config.val.ConfigValImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@FieldsAreNonnullByDefault
@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class ConfigValVector3Impl<
        N extends NumberType<?>,
        T extends Vector3Type<N, T>
        > extends ConfigValImpl<T> implements ConfigVector3Val<N, T> {
    private final RangeContainer<N> rangeX;
    private final RangeContainer<N> rangeY;
    private final RangeContainer<N> rangeZ;

    @SafeVarargs
    public ConfigValVector3Impl(
            T defaultValue, T disabledValue,
            @Nullable RangeContainer<N> rangeX,
            @Nullable RangeContainer<N> rangeY,
            @Nullable RangeContainer<N> rangeZ,
            @Nullable String specificDataType,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<T>... configureValues
    ) {
        super(
                checkVector(rangeX, rangeY, rangeZ, defaultValue, "defaultValue"),
                checkVector(rangeX, rangeY, rangeZ, disabledValue, "disabledValue"),
                val -> specificDataType, constraints, configureValues
        );
        this.rangeX = rangeX == null ? new RangeContainer<>() : rangeX;
        this.rangeY = rangeY == null ? new RangeContainer<>() : rangeY;
        this.rangeZ = rangeZ == null ? new RangeContainer<>() : rangeZ;
    }

    private static <N extends NumberType<?>, T extends Vector3Type<N, T>> T checkVector(
            @Nullable RangeContainer<N> rangeX,
            @Nullable RangeContainer<N> rangeY,
            @Nullable RangeContainer<N> rangeZ,
            T value, String valueName
    ) {
        if ((rangeX != null && !rangeX.isInRange(value.getX()))
                || (rangeY != null && !rangeY.isInRange(value.getY()))
                || (rangeZ != null && !rangeZ.isInRange(value.getZ()))) {
            throw new IllegalStateException("ConfigValVector3Impl got " + valueName + " that is outside valid coordinate range");
        }
        return value;
    }

    protected abstract T createVector(N x, N y, N z);

    @Override public N getX()                      { return getValue().getX(); }
    @Override public N getY()                      { return getValue().getY(); }
    @Override public N getZ()                      { return getValue().getZ(); }
    @Override public double getLength()            { return getValue().length(); }
    @Override public double getLengthSq()          { return getValue().lengthSq(); }

    @Override public RangeContainer<N> getRangeX() { return rangeX; }
    @Override public RangeContainer<N> getRangeY() { return rangeY; }
    @Override public RangeContainer<N> getRangeZ() { return rangeZ; }

    @Override public void set(N x, N y, N z)       { setValue(createVector(x, y, z)); }

    @Override
    protected T adaptValue(T value) {
        N       x       = value.getX();
        N       y       = value.getY();
        N       z       = value.getZ();
        boolean changed = false;

        if (!rangeX.isInRange(x)) {
            x = rangeX.getNearValue(x);
            changed = true;
        }
        if (!rangeY.isInRange(y)) {
            y = rangeY.getNearValue(y);
            changed = true;
        }
        if (!rangeZ.isInRange(z)) {
            z = rangeZ.getNearValue(z);
            changed = true;
        }

        return changed ? createVector(x, y, z) : super.adaptValue(value);
    }

    @Override protected boolean saveDisabledValue() { return false; }
}