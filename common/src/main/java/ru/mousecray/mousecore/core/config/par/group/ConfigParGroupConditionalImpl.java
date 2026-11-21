/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.core.config.par.group;

import org.apache.commons.lang3.tuple.Pair;
import ru.mousecray.mousecore.api.DisplayName;
import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.par.group.ConfigParGroupConditional;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.ListType;
import ru.mousecray.mousecore.core.config.par.ConfigParDisablerImpl;
import ru.mousecray.mousecore.core.config.val.ConfigValImpl;
import ru.mousecray.mousecore.core.config.val.base.ConfigValListImpl;
import ru.mousecray.mousecore.core.config.val.logic.ConfigValConditionImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@FieldsAreNonnullByDefault
@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public class ConfigParGroupConditionalImpl<VAL extends CustomType<?>, TARGET extends CustomType<?>>
        extends ConfigParGroupImpl<VAL> implements ConfigParGroupConditional<VAL, TARGET> {

    protected final           DisplayName mainParName;
    protected final           DisplayName conditionParName;
    @Nullable protected final DisplayName listParName;
    protected final           VAL         fallbackValue;

    public ConfigParGroupConditionalImpl(
            DisplayName name,
            @Nullable ConfigParDisablerImpl disablePar,
            Pair<DisplayName, ? extends ConfigValImpl<VAL>> mainPar,
            VAL fallbackValue,
            @Nullable String comment,
            Pair<DisplayName, ConfigValConditionImpl<TARGET>> conditionPar,
            @Nullable Pair<DisplayName, ? extends ConfigValListImpl<? extends CustomType<?>, TARGET, ? extends ListType<?, TARGET>>> listPar
    ) {
        super(name, disablePar, mainPar, comment, buildValuesMap(conditionPar, listPar));
        mainParName = mainPar.getKey();
        this.fallbackValue = Objects.requireNonNull(fallbackValue);
        conditionParName = conditionPar.getKey();
        listParName = listPar != null ? listPar.getKey() : null;
    }

    private static <TARGET extends CustomType<?>> Map<DisplayName, ConfigValImpl<? extends CustomType<?>>> buildValuesMap(
            Pair<DisplayName, ConfigValConditionImpl<TARGET>> conditionPar,
            @Nullable Pair<DisplayName, ? extends ConfigValListImpl<? extends CustomType<?>, TARGET, ? extends ListType<?, TARGET>>> listPar
    ) {
        Map<DisplayName, ConfigValImpl<? extends CustomType<?>>> map = new LinkedHashMap<>();
        map.put(conditionPar.getKey(), conditionPar.getValue());
        if (listPar != null) map.put(listPar.getKey(), listPar.getValue());
        return map;
    }

    @SuppressWarnings("unchecked")
    @Override
    public VAL getValue(@Nullable TARGET conditionTarget) {
        if (isDisabled()) return fallbackValue;
        ConfigValConditionImpl<TARGET> cond = (ConfigValConditionImpl<TARGET>) getConfigVal(conditionParName);
        return (cond != null && cond.test(conditionTarget)) ? getMainValue() : fallbackValue;
    }

    @Override public VAL getMainValue()     { return getConfigVal().getValue(); }
    @Override public VAL getFallbackValue() { return fallbackValue; }
}