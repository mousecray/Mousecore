/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.core.config.build.val.list;

import ru.mousecray.mousecore.api.config.val.base.ConfigValSimpleList;
import ru.mousecray.mousecore.api.customtype.NumberType;
import ru.mousecray.mousecore.api.customtype.Vector3Type;
import ru.mousecray.mousecore.api.customtype.range.Range;
import ru.mousecray.mousecore.api.customtype.range.RangeContainer;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.values.collection.SimpleListType;
import ru.mousecray.mousecore.api.customtype.values.logic.ConditionalValType;
import ru.mousecray.mousecore.api.error.IncorrectOperationException;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.base.AbstractConfigSimpleListBuilder;
import ru.mousecray.mousecore.core.config.val.container.list.ConfigValSimpleListVector3Impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public final class ConfigVector3ListBuilder<N extends NumberType<?>, T extends Vector3Type<N, T>, BUILDER extends AbstractConfigChildBuilder>
        extends AbstractConfigSimpleListBuilder<BUILDER, T, ConfigVector3ListBuilder<N, T, BUILDER>> {

    private final IValType                                           valType;
    private final Function<String, T>                                valCreator;
    private final ConfigValSimpleListVector3Impl.VectorFactory<N, T> vectorFactory;

    private final List<Range<N>> rangeX = new ArrayList<>();
    private final List<Range<N>> rangeY = new ArrayList<>();
    private final List<Range<N>> rangeZ = new ArrayList<>();

    private ConfigVector3ListBuilder(BUILDER pb, IValType valType, Function<String, T> valCreator, ConfigValSimpleListVector3Impl.VectorFactory<N, T> vectorFactory, Consumer<ConfigValSimpleList<?>> oc) {
        super(pb, oc);
        this.valType = valType;
        this.valCreator = valCreator;
        this.vectorFactory = vectorFactory;
    }

    public static <N extends NumberType<?>, T extends Vector3Type<N, T>, BUILDER extends AbstractConfigChildBuilder> ConfigVector3ListBuilder<N, T, BUILDER> create(
            BUILDER pb, IValType valType, Function<String, T> valCreator, ConfigValSimpleListVector3Impl.VectorFactory<N, T> vectorFactory, Consumer<ConfigValSimpleList<?>> oc) {
        return new ConfigVector3ListBuilder<>(pb, valType, valCreator, vectorFactory, oc);
    }

    public ConfigVector3ListBuilder<N, T, BUILDER> addValidRangeX(N min, N max) {
        rangeX.add(Range.of(Objects.requireNonNull(min), Objects.requireNonNull(max)));
        return this;
    }
    public ConfigVector3ListBuilder<N, T, BUILDER> addValidRangeY(N min, N max) {
        rangeY.add(Range.of(Objects.requireNonNull(min), Objects.requireNonNull(max)));
        return this;
    }
    public ConfigVector3ListBuilder<N, T, BUILDER> addValidRangeZ(N min, N max) {
        rangeZ.add(Range.of(Objects.requireNonNull(min), Objects.requireNonNull(max)));
        return this;
    }

    @Override public IValType getComponentValType()      { return valType; }
    @Override public Function<String, T> getValCreator() { return valCreator; }

    @Override
    protected void validateEntries(List<ConditionalValType<T>> entries, String valueName) {
        RangeContainer<N> rx = rangeX.isEmpty() ? null : new RangeContainer<>(rangeX);
        RangeContainer<N> ry = rangeY.isEmpty() ? null : new RangeContainer<>(rangeY);
        RangeContainer<N> rz = rangeZ.isEmpty() ? null : new RangeContainer<>(rangeZ);

        for (ConditionalValType<T> entry : entries) {
            T vec = entry.getValue();
            if ((rx != null && !rx.isInRange(vec.getX())) ||
                    (ry != null && !ry.isInRange(vec.getY())) ||
                    (rz != null && !rz.isInRange(vec.getZ()))) {
                throw new IncorrectOperationException("ConfigValList " + valueName + " contains element '"
                        + vec + "' that violates the valid vector range");
            }
        }
    }

    public BUILDER buildVector3List() {
        SimpleListType<T> def = resolveValue(rawDefaultEntries, "defaultValue");
        if (def == null) def = SimpleListType.create(valType, conditional, unique, valCreator, Collections.emptyList());

        SimpleListType<T> dis = resolveValue(rawDisabledEntries, "disabledValue");
        if (dis == null) dis = SimpleListType.create(valType, conditional, unique, valCreator, Collections.emptyList());

        onComplete.accept(new ConfigValSimpleListVector3Impl<>(
                conditional, unique, valCreator, vectorFactory, def, dis,
                new RangeContainer<>(rangeX), new RangeContainer<>(rangeY), new RangeContainer<>(rangeZ),
                genConstraints(), genConfigure()
        ));
        return parBuilder;
    }
}