/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.build.val.base;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.ConfigVal;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.utils.MouseCollections;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.utils.PredefinedValueImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public class AbstractConfigValueBuilder<
        BUILDER extends AbstractConfigChildBuilder, CONFIG_VAL_TYPE extends ConfigVal, VAL_TYPE extends CustomType<?>,
        SELF extends AbstractConfigValueBuilder<BUILDER, CONFIG_VAL_TYPE, VAL_TYPE, SELF>
        > {
    protected final     BUILDER                                        parBuilder;
    protected final     Consumer<CONFIG_VAL_TYPE>                      onComplete;
    protected           VAL_TYPE                                       defaultValue;
    protected           VAL_TYPE                                       disabledValue;
    protected @Nullable ArrayList<PredefinedValue<VAL_TYPE>>           configureValues;
    protected @Nullable ArrayList<Constraint<? extends CustomType<?>>> constraints;

    protected boolean disableDictionaryThreshold   = false;
    protected boolean disableDictionaryStandard    = false;
    protected boolean enableThresholdConfiguration = false;
    protected boolean enableStandardConfiguration  = false;

    protected AbstractConfigValueBuilder(
            BUILDER parBuilder, VAL_TYPE defaultValue, VAL_TYPE disabledValue, Consumer<CONFIG_VAL_TYPE> onComplete
    ) {
        this.parBuilder = parBuilder;
        this.defaultValue = defaultValue;
        this.disabledValue = disabledValue;
        this.onComplete = onComplete;
    }

    public SELF setDefaultValue(VAL_TYPE defaultValue) {
        this.defaultValue = Objects.requireNonNull(defaultValue);
        return self();
    }

    public SELF setDisabledValue(VAL_TYPE disabledValue) {
        this.disabledValue = Objects.requireNonNull(disabledValue);
        return self();
    }

    public SELF addConfigureValue(String name, VAL_TYPE value) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(value);
        if (configureValues == null) configureValues = new ArrayList<>();
        configureValues.add(new PredefinedValueImpl<>(name, value));
        return self();
    }

    public SELF addConstraint(Constraint value) {
        Objects.requireNonNull(value);
        if (constraints == null) constraints = new ArrayList<>();
        constraints.add(value);
        return self();
    }

    @Nullable
    protected ImmutableList<Pair<String, String>> genDictionaryThreshold() {
        return disableDictionaryThreshold ? null
                : parBuilder.hasDictionary()
                ? parBuilder.getDictionary().getDefThresholdForType(defaultValue.getValType()) : null;
    }

    @Nullable
    protected ImmutableList<Triple<String, String, String>> genDictionaryStandard() {
        return disableDictionaryStandard ? null
                : parBuilder.hasDictionary()
                ? parBuilder.getDictionary().getDefStandardForType(defaultValue.getValType()) : null;
    }

    @Nullable
    protected Constraint<? extends CustomType<?>>[] genConstraints() { return MouseCollections.toArray(constraints, Constraint.class); }

    @Nullable
    protected PredefinedValue<VAL_TYPE>[] genConfigure() { return MouseCollections.toArray(configureValues, PredefinedValue.class); }

    @SuppressWarnings("unchecked") protected SELF self() { return (SELF) this; }
}