/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.build.val.list;

import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.list.simple.ConfigValDecimalList;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.range.Range;
import ru.mousecray.mousecore.api.customtype.range.RangeContainer;
import ru.mousecray.mousecore.api.customtype.values.DecimalType;
import ru.mousecray.mousecore.api.customtype.values.SimpleListType;
import ru.mousecray.mousecore.api.utils.MouseCollections;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.utils.PredefinedValueImpl;
import ru.mousecray.mousecore.core.config.val.list.simple.ConfigValDecimalListImpl;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class ConfigDecimalListBuilder<BUILDER extends AbstractConfigChildBuilder> {
    protected final BUILDER                        parBuilder;
    private final   Consumer<ConfigValDecimalList> onComplete;

    protected           SimpleListType<DecimalType>                             defaultValue;
    protected           SimpleListType<DecimalType>                             disabledValue;
    protected final     List<Range<DecimalType>>                                range = new ArrayList<>();
    @Nullable protected ArrayList<Constraint<? extends CustomType<?>>>          constraints;
    @Nullable protected ArrayList<PredefinedValue<SimpleListType<DecimalType>>> configureValues;

    protected ConfigDecimalListBuilder(BUILDER parBuilder, Consumer<ConfigValDecimalList> onComplete) {
        this.parBuilder = parBuilder;
        this.onComplete = onComplete;
    }

    public static <BUILDER extends AbstractConfigChildBuilder> ConfigDecimalListBuilder<BUILDER> create(BUILDER parBuilder, Consumer<ConfigValDecimalList> onComplete) {
        return new ConfigDecimalListBuilder<>(Objects.requireNonNull(parBuilder), onComplete);
    }

    public ConfigDecimalListBuilder<BUILDER> setDefaultValue(SimpleListType<DecimalType> defaultValue) {
        this.defaultValue = Objects.requireNonNull(defaultValue);
        return this;
    }

    public ConfigDecimalListBuilder<BUILDER> setDisabledValue(SimpleListType<DecimalType> disabledValue) {
        this.disabledValue = Objects.requireNonNull(disabledValue);
        return this;
    }

    public ConfigDecimalListBuilder<BUILDER> addConfigureValue(String name, SimpleListType<DecimalType> value) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(value);
        if (configureValues == null) configureValues = new ArrayList<>();
        configureValues.add(new PredefinedValueImpl<>(name, value));
        return this;
    }

    public ConfigDecimalListBuilder<BUILDER> addConstraint(Constraint<?> value) {
        Objects.requireNonNull(value);
        if (constraints == null) constraints = new ArrayList<>();
        constraints.add(value);
        return this;
    }

    public ConfigDecimalListBuilder<BUILDER> addValidRange(DecimalType minValue, DecimalType maxValue) {
        range.add(Range.of(Objects.requireNonNull(minValue), Objects.requireNonNull(maxValue)));
        return this;
    }

    public BUILDER buildDecimal() {
        onComplete.accept(
                new ConfigValDecimalListImpl(
                        defaultValue, disabledValue,
                        range.isEmpty()
                                ? null
                                : new RangeContainer<>(range),
                        MouseCollections.toArray(constraints, Constraint.class),
                        MouseCollections.toArray(configureValues, PredefinedValue.class)
                )
        );
        return parBuilder;
    }
}