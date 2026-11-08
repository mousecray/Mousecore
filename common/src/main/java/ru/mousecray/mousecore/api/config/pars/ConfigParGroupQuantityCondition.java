/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.config.pars;

import org.apache.commons.lang3.tuple.Pair;
import ru.mousecray.mousecore.api.DisplayName;
import ru.mousecray.mousecore.api.VariableValue;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.ConfigParDisabler;
import ru.mousecray.mousecore.api.config.ConfigParGroup;
import ru.mousecray.mousecore.api.config.values.ConfigConditionVal;
import ru.mousecray.mousecore.api.config.values.ConfigValRandomQuantity;
import ru.mousecray.mousecore.api.config.values.base.ConfigListVal;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.ListType;
import ru.mousecray.mousecore.api.customtype.values.RandomQuantityType;
import ru.mousecray.mousecore.api.error.UnsupportedValException;
import ru.mousecray.mousecore.api.utils.MouseCollections;
import ru.mousecray.mousecore.api.utils.MouseReflection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public class ConfigParGroupQuantityCondition<T extends CustomType<?>> extends ConfigParGroup<RandomQuantityType> {
    @Nonnull protected final  DisplayName mainParName;
    @Nonnull protected final  DisplayName conditionParName;
    @Nullable protected final DisplayName listParName;

    public ConfigParGroupQuantityCondition(
            DisplayName name,
            @Nullable ConfigParDisabler disablePar,
            Pair<DisplayName, ConfigValRandomQuantity> mainPar,
            @Nullable String comment,
            Pair<DisplayName, ConfigConditionVal<T>> conditionPar,
            @Nullable Pair<DisplayName, ConfigListVal<?, T, ?>> listPar
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

    private static <T extends CustomType<?>> Pair<DisplayName, ConfigConditionVal<T>> checkConditionAndList(
            Pair<DisplayName, ConfigConditionVal<T>> conditionPar,
            @Nullable Pair<DisplayName, ConfigListVal<?, T, ?>> listPar
    ) {
        if (listPar != null) {
            if (!conditionPar.getValue().getListComponentType().equals(listPar.getValue().getListComponentType())) {
                throw new UnsupportedValException("ConfigConditionVal type '" + conditionPar.getKey().getInternalName() +
                        "' is not equal with type of ConfigListVal '" + listPar.getKey().getInternalName() +
                        "'. ConfigConditionVal type = " + conditionPar.getValue().getListComponentType() +
                        "; ConfigListVal type = " + listPar.getValue().getListComponentType());
            }
            VariableValue<Supplier> var = MouseReflection.invokeMethod(
                    ConfigConditionVal.class, Supplier.class,
                    "getListSupplier", conditionPar.getValue(), null
            );
            if (var.isPresent()) {
                @SuppressWarnings("unchecked") Supplier<ListType<?, ?>> sup = var.getValue();
                if (sup != null && listPar.getValue().getValue().getLogicPipeline().isEqual(sup.get()).asBoolean()) {
                    return conditionPar;
                }
            }
            throw new UnsupportedValException("ConfigConditionVal '" + conditionPar.getKey().getInternalName() +
                    "' doesn't has relation with ConfigListVal '" + listPar.getKey().getInternalName());
        }
        return conditionPar;
    }

    @SuppressWarnings({ "unchecked", "DataFlowIssue" })
    public RandomQuantityType getValue(T conditionTarget) {
        return ((ConfigConditionVal<T>) getConfigVal(conditionParName)).test(conditionTarget)
                ? getMainValue() : RandomQuantityType.NULL;
    }

    @Override public RandomQuantityType getMainValue() { return getConfigVal().getValue(); }
}