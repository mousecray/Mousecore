/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.par;

import ru.mousecray.mousecore.api.DisplayName;
import ru.mousecray.mousecore.api.config.par.ConfigParDisabler;
import ru.mousecray.mousecore.api.config.utils.ConfigLocaleType;
import ru.mousecray.mousecore.api.config.utils.ValueParseResult;
import ru.mousecray.mousecore.api.customtype.values.PlusMinusType;
import ru.mousecray.mousecore.core.config.val.ConfigValDisablerImpl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ConfigParDisablerImpl extends ConfigParImpl<PlusMinusType> implements ConfigParDisabler {
    private final boolean useDefaultName;

    public ConfigParDisablerImpl(DisplayName name, boolean useDefaultName, ConfigValDisablerImpl value, @Nullable String comment) {
        super(name, true, value, comment);
        this.useDefaultName = useDefaultName;
    }

    @SuppressWarnings("DataFlowIssue") @Nonnull @Override
    public DisplayName getName() {
        return useDefaultName && hasConfig()
                ? new DisplayName(
                super.getName().getInternalName(),
                getConfig().getDictionary().getLocaleForLocale(ConfigLocaleType.DISABLE_PAR))
                : super.getName();
    }

    protected ValueParseResult setDisabledRaw(@Nullable String value) {
        ConfigValDisablerImpl val = ((ConfigValDisablerImpl) getConfigVal());
        ValueParseResult      res = val.setValueRaw(value);
        isDisabled = val.isDisabled();
        return res;
    }

    @Override
    public void setDisabled(boolean disabled) {
        super.setDisabled(disabled);
        ((ConfigValDisablerImpl) getConfigVal()).setDisabled(disabled);
    }

    @Override public boolean isDisabled()             { return isDisabled || getConfigVal().isDisabled(); }

    @Override public ConfigValDisablerImpl getValue() { return (ConfigValDisablerImpl) getConfigVal(); }

    @Override
    public void reset() {
        super.reset();
        ConfigValDisablerImpl val = (ConfigValDisablerImpl) getConfigVal();
        val.reset();
        isDisabled = val.isDisabled();
        val.reloadCache();
    }

    @Override public void reloadCache() {
        super.reloadCache();
        getConfigVal().reloadCache();
    }
}