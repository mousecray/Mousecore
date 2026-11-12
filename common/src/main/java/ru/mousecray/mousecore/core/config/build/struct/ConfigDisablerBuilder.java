/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.build.struct;

import ru.mousecray.mousecore.api.DisplayName;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.customtype.values.PlusMinusType;
import ru.mousecray.mousecore.api.utils.MouseCollections;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.par.ConfigParDisablerImpl;
import ru.mousecray.mousecore.core.config.utils.PredefinedValueImpl;
import ru.mousecray.mousecore.core.config.val.ConfigValDisablerImpl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Objects;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigDisablerBuilder<BUILDER extends AbstractConfigChildBuilder> {
    private final          BUILDER                                   childBuilder;
    @Nonnull private final DisplayName                               name;
    private final          boolean                                   useDefaultName;
    private @Nullable      ArrayList<Constraint>                     constraints;
    @Nullable private      ArrayList<PredefinedValue<PlusMinusType>> configureValues;
    private                boolean                                   disableAdditionalThreshold;
    private                PlusMinusType                             defaultValue  = PlusMinusType.FALSE;
    private                PlusMinusType                             disabledValue = PlusMinusType.TRUE;

    private ConfigDisablerBuilder(BUILDER childBuilder, DisplayName name, boolean useDefaultName) {
        this.childBuilder = childBuilder;
        this.name = name;
        this.useDefaultName = useDefaultName;
    }

    public static <T extends AbstractConfigChildBuilder> ConfigDisablerBuilder<T> create(T sectionBuilder, String displayName, boolean useDefaultName) {
        return new ConfigDisablerBuilder<>(
                Objects.requireNonNull(sectionBuilder),
                new DisplayName("disabler",
                        Objects.requireNonNull(displayName)), useDefaultName
        );
    }

    public ConfigDisablerBuilder<BUILDER> setDefaultValue(PlusMinusType defaultValue) {
        Objects.requireNonNull(defaultValue);
        this.defaultValue = defaultValue;
        return this;
    }

    public ConfigDisablerBuilder<BUILDER> setDisabledValue(PlusMinusType disabledValue) {
        Objects.requireNonNull(disabledValue);
        this.disabledValue = disabledValue;
        return this;
    }

    public ConfigDisablerBuilder<BUILDER> disableDictionaryThreshold() {
        disableAdditionalThreshold = true;
        return this;
    }

    public ConfigDisablerBuilder<BUILDER> addConfigureValue(String name, PlusMinusType value) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(value);
        if (configureValues == null) configureValues = new ArrayList<>();
        configureValues.add(new PredefinedValueImpl<>(name, value));
        return this;
    }

    public ConfigDisablerBuilder<BUILDER> addConstraint(Constraint value) {
        Objects.requireNonNull(value);
        if (constraints == null) constraints = new ArrayList<>();
        constraints.add(value);
        return this;
    }

    public BUILDER buildDisabler() {
        ConfigParDisablerImpl par = new ConfigParDisablerImpl(
                name, useDefaultName,
                new ConfigValDisablerImpl(
                        defaultValue, disabledValue,
                        disableAdditionalThreshold ? null :
                                childBuilder.configBuilder.dictionary != null
                                        ? childBuilder.configBuilder.dictionary.getDefThresholdForType(ValType.PLUS_MINUS) : null,
                        MouseCollections.toArray(constraints, Constraint.class),
                        MouseCollections.toArray(configureValues, PredefinedValue.class)
                ), null);
        childBuilder.addDisabler(par);
        return childBuilder;
    }
}