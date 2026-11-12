/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.par.group;

import org.apache.commons.lang3.tuple.Pair;
import ru.mousecray.mousecore.api.DisplayName;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.par.ConfigParGroupQuantityCondition;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.ListType;
import ru.mousecray.mousecore.api.customtype.values.RandomQuantityType;
import ru.mousecray.mousecore.api.error.UnsupportedValException;
import ru.mousecray.mousecore.api.utils.MouseCollections;
import ru.mousecray.mousecore.core.config.par.ConfigParDisablerImpl;
import ru.mousecray.mousecore.core.config.val.ConfigValConditionImpl;
import ru.mousecray.mousecore.core.config.val.ConfigValRandomQuantityImpl;
import ru.mousecray.mousecore.core.config.val.base.ConfigListValImpl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public class ConfigParGroupQuantityConditionImpl<T extends CustomType<?>> extends ConfigParGroupImpl<RandomQuantityType> implements ConfigParGroupQuantityCondition<T> {
    @Nonnull protected final  DisplayName mainParName;
    @Nonnull protected final  DisplayName conditionParName;
    @Nullable protected final DisplayName listParName;

    public ConfigParGroupQuantityConditionImpl(
            DisplayName name,
            @Nullable ConfigParDisablerImpl disablePar,
            Pair<DisplayName, ConfigValRandomQuantityImpl> mainPar,
            @Nullable String comment,
            Pair<DisplayName, ConfigValConditionImpl<T>> conditionPar,
            @Nullable Pair<DisplayName, ConfigListValImpl<? extends CustomType<?>, T, ? extends ListType<?, T>>> listPar
    ) {
        super(
                name, disablePar, mainPar, comment,
                listPar != null ?
                        MouseCollections.createMap(
                                checkConditionAndList(conditionPar, listPar).getKey(), conditionPar.getValue(),
                                listPar.getKey(), listPar.getValue()
                        ) :
                        MouseCollections.createMap(
                                checkConditionAndList(conditionPar, null).getKey(), conditionPar.getValue()
                        )
        );
        mainParName = mainPar.getKey();
        conditionParName = conditionPar.getKey();
        listParName = listPar != null ? listPar.getKey() : null;
    }

    private static <T extends CustomType<?>> Pair<DisplayName, ConfigValConditionImpl<T>> checkConditionAndList(
            Pair<DisplayName, ConfigValConditionImpl<T>> conditionPar,
            @Nullable Pair<DisplayName, ? extends ConfigListValImpl<? extends CustomType<?>, T, ? extends ListType<?, T>>> listPar
    ) {
        if (listPar != null) {
            if (conditionPar.getValue().getListComponentType() != listPar.getValue().getListComponentType()) {
                throw new UnsupportedValException("ConfigValConditionImpl type '" + conditionPar.getKey().getInternalName() +
                        "' is not equal with type of ConfigListValImpl '" + listPar.getKey().getInternalName() +
                        "'. ConfigValConditionImpl type = " + conditionPar.getValue().getListComponentType() +
                        "; ConfigListValImpl type = " + listPar.getValue().getListComponentType());
            }

            Supplier<ListType<?, T>> sup = conditionPar.getValue().getListSupplier();
            if (sup != null && listPar.getValue().getValue().getLogicPipeline().isEqual(sup.get()).asBoolean()) {
                return conditionPar;
            }
            throw new UnsupportedValException("ConfigValConditionImpl '" + conditionPar.getKey().getInternalName() +
                    "' doesn't has relation with ConfigListValImpl '" + listPar.getKey().getInternalName());
        }
        return conditionPar;
    }

    @SuppressWarnings({ "unchecked", "DataFlowIssue" }) @Override public RandomQuantityType getValue(T conditionTarget) {
        return ((ConfigValConditionImpl<T>) getConfigVal(conditionParName)).test(conditionTarget)
                ? getMainValue() : RandomQuantityType.NULL;
    }

    @Override public RandomQuantityType getMainValue() { return getConfigVal().getValue(); }
}