/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.core.config.val;

import org.apache.commons.lang3.tuple.Pair;
import ru.mousecray.mousecore.api.VariableValue;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.ConfigValPlusMinus;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.values.PlusMinusType;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.api.utils.MouseCollections;
import ru.mousecray.mousecore.core.config.utils.PredefinedValueImpl;
import ru.mousecray.mousecore.core.config.val.base.ConfigValLogicalImpl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ConfigValPlusMinusImpl extends ConfigValLogicalImpl<PlusMinusType> implements ConfigValPlusMinus {
    @SafeVarargs
    public ConfigValPlusMinusImpl(
            PlusMinusType defaultValue, PlusMinusType disabledValue,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable List<Pair<String, String>> additionalThreshold,
            @Nullable PredefinedValue<PlusMinusType>... configureValues
    ) {
        super(
                defaultValue, disabledValue,
                "+/-", constraints,
                genValues(additionalThreshold, configureValues)
        );
    }

    private static PredefinedValue<PlusMinusType>[] genValues(
            @Nullable List<Pair<String, String>> additionalThreshold,
            @Nullable PredefinedValue<PlusMinusType>... predefinedValues
    ) {
        predefinedValues = MouseCollections.map(
                val -> val.getValue() != null ? val : new PredefinedValueImpl<>(val.getDisplayName(), PlusMinusType.NULL),
                true, predefinedValues
        );

        List<PredefinedValue<PlusMinusType>> list = new ArrayList<>();
        if (additionalThreshold != null) {
            for (Pair<String, String> pair : additionalThreshold) {
                list.add(new PredefinedValueImpl<>(pair.getLeft(), PlusMinusType.TRUE));
                list.add(new PredefinedValueImpl<>(pair.getRight(), PlusMinusType.FALSE));
            }
        }
        return list.isEmpty() ? predefinedValues
                : MouseCollections.addAll(PredefinedValue.class, predefinedValues, list);
    }

    @Nonnull @Override
    protected VariableValue<PlusMinusType> parseValue(@Nullable String value) {
        try {
            return VariableValue.create(CustomType.parse(PlusMinusType.class, value));
        } catch (ValueFormatException ignore) { return VariableValue.create(); }
    }

    @Override protected boolean saveRules() { return false; }
}