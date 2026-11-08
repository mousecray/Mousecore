/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.config;

import ru.mousecray.mousecore.api.DisplayName;
import ru.mousecray.mousecore.api.VariableValue;
import ru.mousecray.mousecore.api.config.pars.ConfigPar;
import ru.mousecray.mousecore.api.config.specific.ConfigLocaleType;
import ru.mousecray.mousecore.api.config.utils.ValueParseResult;
import ru.mousecray.mousecore.api.config.values.ConfigValDisabler;
import ru.mousecray.mousecore.api.customtype.values.PlusMinusType;
import ru.mousecray.mousecore.api.utils.MouseReflection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ConfigParDisabler extends ConfigPar<PlusMinusType> {
    private final boolean useDefaultName;

    public ConfigParDisabler(DisplayName name, boolean useDefaultName, ConfigValDisabler value, @Nullable String comment) {
        super(name, true, value, comment);
        this.useDefaultName = useDefaultName;
    }

    @Nonnull @Override
    public DisplayName getName() {
        return useDefaultName && hasConfig()
                ? new DisplayName(
                super.getName().getInternalName(),
                getConfig().getDictionary().getLocaleForLocale(ConfigLocaleType.DISABLE_PAR))
                : super.getName();
    }

    protected ValueParseResult setDisabledRaw(@Nullable String value) {
        ConfigValDisabler val = ((ConfigValDisabler) getConfigVal());
        VariableValue<ValueParseResult> var = MouseReflection.invokeMethod(
                ConfigVal.class, String.class, ValueParseResult.class, "setValueRaw",
                val, value, hasLogger() ? getLogger() : null
        );
        ValueParseResult res = var.isPresent() ? var.getValue() : ValueParseResult.ERROR;
        isDisabled = val.isDisabled();
        return res;
    }

    @Override
    public void setDisabled(boolean disabled) {
        super.setDisabled(disabled);
        ((ConfigValDisabler) getConfigVal()).setDisabled(disabled);
    }

    @Override public boolean isDisabled() { return isDisabled || getConfigVal().isDisabled(); }

    public ConfigValDisabler getValue()   { return (ConfigValDisabler) getConfigVal(); }

    @Override
    public void reset() {
        super.reset();
        ConfigValDisabler val = (ConfigValDisabler) getConfigVal();
        val.reset();
        isDisabled = val.isDisabled();
        MouseReflection.invokeMethod(
                ConfigVal.class, "reloadCache",
                val, hasLogger() ? getLogger() : null
        );
    }

    @Override
    protected void reloadCache() {
        super.reloadCache();
        MouseReflection.invokeMethod(
                ConfigVal.class, "reloadCache",
                getConfigVal(), hasLogger() ? getLogger() : null
        );
    }
}