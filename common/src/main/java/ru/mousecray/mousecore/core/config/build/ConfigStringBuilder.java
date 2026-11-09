/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.build;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.utils.MouseCollections;
import ru.mousecray.mousecore.core.config.utils.ConstraintImpl;
import ru.mousecray.mousecore.core.config.utils.PredefinedValueImpl;
import ru.mousecray.mousecore.core.config.val.ConfigValStringImpl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Objects;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigStringBuilder<BUILDER extends AbstractConfigParameterBuilder<BUILDER>> {
    private final     BUILDER                            parBuilder;
    private           String                             defaultValue;
    private           String                             disabledValue;
    private @Nullable ArrayList<Constraint>              constraints;
    @Nullable private ArrayList<PredefinedValue<String>> configureValues;
    private           boolean                            mayBeNull;

    private ConfigStringBuilder(BUILDER parBuilder) { this.parBuilder = parBuilder; }

    static <BUILDER extends AbstractConfigParameterBuilder<BUILDER>> ConfigStringBuilder<BUILDER> create(BUILDER parBuilder) {
        return new ConfigStringBuilder<>(Objects.requireNonNull(parBuilder));
    }

    public ConfigStringBuilder<BUILDER> setDefaultValue(@Nullable String defaultValue) {
        if (defaultValue == null) defaultValue = "";
        this.defaultValue = defaultValue;
        return this;
    }

    public ConfigStringBuilder<BUILDER> setDisabledValue(@Nullable String disabledValue) {
        if (disabledValue == null) disabledValue = "";
        this.disabledValue = disabledValue;
        return this;
    }

    public ConfigStringBuilder<BUILDER> mayBeNull() {
        mayBeNull = true;
        return this;
    }

    public ConfigStringBuilder<BUILDER> addConfigureValue(@Nonnull String name, String value) {
        Objects.requireNonNull(name);
        if (configureValues == null) configureValues = new ArrayList<>();
        configureValues.add(new PredefinedValueImpl<>(name, value));
        return this;
    }

    public ConfigStringBuilder<BUILDER> addConstraint(Constraint value) {
        Objects.requireNonNull(value);
        if (constraints == null) constraints = new ArrayList<>();
        constraints.add(value);
        return this;
    }

    public BUILDER buildString() {
        parBuilder.setValue(new ConfigValStringImpl(
                defaultValue, disabledValue,
                mayBeNull,
                MouseCollections.toArray(constraints, ConstraintImpl.class),
                MouseCollections.toArray(configureValues, PredefinedValueImpl.class)));
        return parBuilder;
    }
}