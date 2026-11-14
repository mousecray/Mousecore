/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.build.val.base;

import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.ConfigVal;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.values.SimpleListType;
import ru.mousecray.mousecore.api.utils.MouseCollections;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.utils.PredefinedValueImpl;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

public class AbstractConfigSimpleListBuilder<
        BUILDER extends AbstractConfigChildBuilder,
        CONFIG_VAL_LIST extends ConfigVal,
        LIST_COMPONENT_TYPE extends CustomType<?>,
        SELF extends AbstractConfigSimpleListBuilder<BUILDER, CONFIG_VAL_LIST, LIST_COMPONENT_TYPE, SELF>
        > {
    protected final BUILDER                   parBuilder;
    protected final Consumer<CONFIG_VAL_LIST> onComplete;

    protected           SimpleListType<LIST_COMPONENT_TYPE>                             defaultValue;
    protected           SimpleListType<LIST_COMPONENT_TYPE>                             disabledValue;
    @Nullable protected ArrayList<Constraint<? extends CustomType<?>>>                  constraints;
    @Nullable protected ArrayList<PredefinedValue<SimpleListType<LIST_COMPONENT_TYPE>>> configureValues;
    protected           boolean                                                         conditional;

    protected AbstractConfigSimpleListBuilder(BUILDER parBuilder, Consumer<CONFIG_VAL_LIST> onComplete) {
        this.parBuilder = parBuilder;
        this.onComplete = onComplete;
    }

    public SELF setDefaultValue(SimpleListType<LIST_COMPONENT_TYPE> defaultValue) {
        this.defaultValue = Objects.requireNonNull(defaultValue);
        return self();
    }

    public SELF setDisabledValue(SimpleListType<LIST_COMPONENT_TYPE> disabledValue) {
        this.disabledValue = Objects.requireNonNull(disabledValue);
        return self();
    }

    public SELF addConfigureValue(String name, SimpleListType<LIST_COMPONENT_TYPE> value) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(value);
        if (configureValues == null) configureValues = new ArrayList<>();
        configureValues.add(new PredefinedValueImpl<>(name, value));
        return self();
    }

    public SELF addConstraint(Constraint<?> value) {
        Objects.requireNonNull(value);
        if (constraints == null) constraints = new ArrayList<>();
        constraints.add(value);
        return self();
    }

    public SELF setConditional() {
        conditional = true;
        return self();
    }

    @Nullable
    protected Constraint<? extends CustomType<?>>[] genConstraints() { return MouseCollections.toArray(constraints, Constraint.class); }

    @Nullable
    protected PredefinedValue<SimpleListType<LIST_COMPONENT_TYPE>>[] genConfigure() { return MouseCollections.toArray(configureValues, PredefinedValue.class); }

    @SuppressWarnings("unchecked") protected SELF self() { return (SELF) this; }
}