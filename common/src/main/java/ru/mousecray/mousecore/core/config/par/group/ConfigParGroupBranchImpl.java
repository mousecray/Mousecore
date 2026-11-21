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
import ru.mousecray.mousecore.api.config.par.group.ConfigParGroupBranch;
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

@FieldsAreNonnullByDefault
@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public class ConfigParGroupBranchImpl<VAL extends CustomType<?>, TARGET extends CustomType<?>>
        extends ConfigParGroupImpl<VAL> implements ConfigParGroupBranch<VAL, TARGET> {

    protected final           DisplayName matchValName;
    protected final           DisplayName elseValName;
    protected final           DisplayName conditionParName;
    @Nullable protected final DisplayName listParName;

    public ConfigParGroupBranchImpl(
            DisplayName name,
            @Nullable ConfigParDisablerImpl disablePar,
            Pair<DisplayName, ? extends ConfigValImpl<VAL>> matchVal,
            Pair<DisplayName, ? extends ConfigValImpl<VAL>> elseVal,
            @Nullable String comment,
            Pair<DisplayName, ConfigValConditionImpl<TARGET>> conditionPar,
            @Nullable Pair<DisplayName, ? extends ConfigValListImpl<? extends CustomType<?>, TARGET, ? extends ListType<?, TARGET>>> listPar
    ) {
        super(name, disablePar, matchVal, comment, buildValuesMap(elseVal, conditionPar, listPar));
        matchValName = matchVal.getKey();
        elseValName = elseVal.getKey();
        conditionParName = conditionPar.getKey();
        listParName = listPar != null ? listPar.getKey() : null;
    }

    private static <VAL extends CustomType<?>, TARGET extends CustomType<?>> Map<DisplayName, ConfigValImpl<? extends CustomType<?>>> buildValuesMap(
            Pair<DisplayName, ? extends ConfigValImpl<VAL>> elseVal,
            Pair<DisplayName, ConfigValConditionImpl<TARGET>> conditionPar,
            @Nullable Pair<DisplayName, ? extends ConfigValListImpl<? extends CustomType<?>, TARGET, ? extends ListType<?, TARGET>>> listPar
    ) {
        Map<DisplayName, ConfigValImpl<? extends CustomType<?>>> map = new LinkedHashMap<>();
        map.put(elseVal.getKey(), elseVal.getValue());
        map.put(conditionPar.getKey(), conditionPar.getValue());
        if (listPar != null) map.put(listPar.getKey(), listPar.getValue());
        return map;
    }

    @SuppressWarnings("unchecked")
    @Override
    public VAL resolve(@Nullable TARGET conditionTarget) {
        if (isDisabled()) return getElseValue();
        ConfigValConditionImpl<TARGET> cond = (ConfigValConditionImpl<TARGET>) getConfigVal(conditionParName);
        return (cond != null && cond.test(conditionTarget)) ? getMatchValue() : getElseValue();
    }

    @Override public VAL getMainValue()  { return getConfigVal().getValue(); }
    @Override public VAL getMatchValue() { return getMainValue(); }

    @SuppressWarnings("unchecked")
    @Override public VAL getElseValue() {
        ConfigValImpl<VAL> val = (ConfigValImpl<VAL>) getConfigVal(elseValName);
        return val != null ? val.getValue() : getMainValue();
    }
}