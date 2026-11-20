package ru.mousecray.mousecore.core.config.val;

import org.apache.commons.lang3.tuple.Pair;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.ConfigValDisabler;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.values.logic.PlusMinusType;

import javax.annotation.Nullable;
import java.util.List;

public class ConfigValDisablerImpl extends ConfigValPlusMinusImpl implements ConfigValDisabler {
    @SafeVarargs
    public ConfigValDisablerImpl(
            PlusMinusType defaultValue, PlusMinusType disabledValue,
            @Nullable List<Pair<String, String>> additionalThreshold,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<PlusMinusType>... configureValues
    ) {
        super(
                defaultValue, disabledValue, constraints,
                additionalThreshold, configureValues
        );
    }

    @Override
    protected boolean isValueDisabled() {
        return (owner != null && owner.isStructurallyDisabled()) || constrainedDisabled;
    }

    @Override public boolean isDisabled()          { return getValue().isTrue(); }
    @Override public void setDisabled(boolean val) { setValue(PlusMinusType.create(val)); }
}