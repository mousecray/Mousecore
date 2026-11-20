/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.core.config.val.container.list;

import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.container.list.ConfigValSimpleListVector3;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.NumberType;
import ru.mousecray.mousecore.api.customtype.Vector3Type;
import ru.mousecray.mousecore.api.customtype.range.RangeContainer;
import ru.mousecray.mousecore.api.customtype.values.collection.SimpleListType;
import ru.mousecray.mousecore.api.customtype.values.logic.ConditionalValType;
import ru.mousecray.mousecore.api.log.ConsoleColor;
import ru.mousecray.mousecore.api.log.LogPrefixes;
import ru.mousecray.mousecore.core.config.val.base.ConfigValSimpleListImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
@FieldsAreNonnullByDefault
public class ConfigValSimpleListVector3Impl<N extends NumberType<?>, T extends Vector3Type<N, T>>
        extends ConfigValSimpleListImpl<T> implements ConfigValSimpleListVector3<N, T> {

    private final RangeContainer<N>   rangeX;
    private final RangeContainer<N>   rangeY;
    private final RangeContainer<N>   rangeZ;
    private final VectorFactory<N, T> vectorCreator;

    @SafeVarargs
    public ConfigValSimpleListVector3Impl(
            boolean isConditional, boolean isUnique,
            @Nullable Function<String, T> valCreator, VectorFactory<N, T> vectorCreator,
            SimpleListType<T> defaultValue, SimpleListType<T> disabledValue,
            RangeContainer<N> rangeX, RangeContainer<N> rangeY, RangeContainer<N> rangeZ,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<SimpleListType<T>>... configureValues
    ) {
        super(isConditional, isUnique, valCreator, defaultValue, disabledValue, constraints, configureValues);
        this.rangeX = Objects.requireNonNull(rangeX);
        this.rangeY = Objects.requireNonNull(rangeY);
        this.rangeZ = Objects.requireNonNull(rangeZ);
        this.vectorCreator = Objects.requireNonNull(vectorCreator);
    }

    @Override public RangeContainer<N> getRangeX() { return rangeX; }
    @Override public RangeContainer<N> getRangeY() { return rangeY; }
    @Override public RangeContainer<N> getRangeZ() { return rangeZ; }

    @Override
    public boolean isInRange(T vector) {
        return rangeX.isInRange(vector.getX()) && rangeY.isInRange(vector.getY()) && rangeZ.isInRange(vector.getZ());
    }

    @Override
    protected ConditionalValType<T> adaptListValue(ConditionalValType<T> value) {
        T vector = value.getValue();
        if (!isInRange(vector)) {
            if (hasLogger()) {
                getLogger().atWarn().withPrefix(LogPrefixes.CONFIG).withStyle(ConsoleColor.YELLOW_BG)
                        .log("ConfigValueSimpleListVector3 '{0}' got value that not valid. It was adapted",
                                owner != null ? owner.getFullInternalName() : "unknown");
            }
            N x = rangeX.isInRange(vector.getX()) ? vector.getX() : rangeX.getNearValue(vector.getX());
            N y = rangeY.isInRange(vector.getY()) ? vector.getY() : rangeY.getNearValue(vector.getY());
            N z = rangeZ.isInRange(vector.getZ()) ? vector.getZ() : rangeZ.getNearValue(vector.getZ());
            return value.createType(vectorCreator.create(x, y, z), value.isAnti());
        }
        return value;
    }

    @FunctionalInterface
    public interface VectorFactory<N extends NumberType<?>, T extends Vector3Type<N, T>> {
        T create(N x, N y, N z);
    }
}