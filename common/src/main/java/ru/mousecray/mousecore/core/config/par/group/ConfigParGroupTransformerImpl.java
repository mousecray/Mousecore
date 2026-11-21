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
import ru.mousecray.mousecore.api.config.par.group.ConfigParGroupTransformer;
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
public class ConfigParGroupTransformerImpl<T extends CustomType<?>, CONTEXT extends CustomType<?>>
        extends ConfigParGroupImpl<T> implements ConfigParGroupTransformer<T, CONTEXT> {

    protected final           DisplayName replacementName;
    protected final           DisplayName conditionParName;
    @Nullable protected final DisplayName listParName;

    public ConfigParGroupTransformerImpl(
            DisplayName name,
            @Nullable ConfigParDisablerImpl disablePar,
            Pair<DisplayName, ? extends ConfigValImpl<T>> replacementPar,
            @Nullable String comment,
            Pair<DisplayName, ConfigValConditionImpl<CONTEXT>> conditionPar,
            @Nullable Pair<DisplayName, ? extends ConfigValListImpl<? extends CustomType<?>, CONTEXT, ? extends ListType<?, CONTEXT>>> listPar
    ) {
        super(name, disablePar, replacementPar, comment, buildValuesMap(conditionPar, listPar));
        replacementName = replacementPar.getKey();
        conditionParName = conditionPar.getKey();
        listParName = listPar != null ? listPar.getKey() : null;
    }

    private static <CONTEXT extends CustomType<?>> Map<DisplayName, ConfigValImpl<? extends CustomType<?>>> buildValuesMap(
            Pair<DisplayName, ConfigValConditionImpl<CONTEXT>> conditionPar,
            @Nullable Pair<DisplayName, ? extends ConfigValListImpl<? extends CustomType<?>, CONTEXT, ? extends ListType<?, CONTEXT>>> listPar
    ) {
        Map<DisplayName, ConfigValImpl<? extends CustomType<?>>> map = new LinkedHashMap<>();
        map.put(conditionPar.getKey(), conditionPar.getValue());
        if (listPar != null) map.put(listPar.getKey(), listPar.getValue());
        return map;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T transform(T original, @Nullable CONTEXT context) {
        Objects.requireNonNull(original, "Original value cannot be null");
        return readLocked(() -> {
            if (isDisabled()) return original;
            ConfigValConditionImpl<CONTEXT> cond = (ConfigValConditionImpl<CONTEXT>) getConfigVal(conditionParName);
            return (cond != null && cond.test(context)) ? getReplacement() : original;
        });
    }

    @Override public T getMainValue()   { return getConfigVal().getValue(); }
    @Override public T getReplacement() { return getMainValue(); }
}