package ru.mousecray.mousecore.core.config.par;

import ru.mousecray.mousecore.api.DisplayName;
import ru.mousecray.mousecore.api.config.par.ConfigParDisabler;
import ru.mousecray.mousecore.api.config.utils.ConfigLocaleType;
import ru.mousecray.mousecore.api.config.utils.ValueParseResult;
import ru.mousecray.mousecore.api.customtype.values.logic.PlusMinusType;
import ru.mousecray.mousecore.core.config.val.logic.ConfigValDisablerImpl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ConfigParDisablerImpl extends ConfigParImpl<PlusMinusType> implements ConfigParDisabler {
    private final boolean useDefaultName;

    public ConfigParDisablerImpl(DisplayName name, boolean useDefaultName, ConfigValDisablerImpl value, @Nullable String comment) {
        super(name, true, value, comment);
        this.useDefaultName = useDefaultName;
        isDisabled = value.isDisabled();
    }

    @SuppressWarnings("DataFlowIssue") @Nonnull @Override
    public DisplayName getName() {
        return useDefaultName && hasConfig()
                ? new DisplayName(
                super.getName().getInternalName(),
                getConfig().getDictionary().getLocaleForLocale(ConfigLocaleType.DISABLE_PAR))
                : super.getName();
    }

    public ValueParseResult setDisabledRaw(@Nullable String value) {
        ConfigValDisablerImpl val = getValue();
        ValueParseResult      res = val.setValueRaw(value);
        isDisabled = val.isDisabled();
        return res;
    }

    @Override
    public void setDisabled(boolean disabled) {
        ConfigValDisablerImpl val = getValue();
        val.setDisabled(disabled);
        isDisabled = val.isDisabled();
    }

    @Override
    public void setDisabledStructurally(boolean parentDisabled) {
        isStructurallyDisabled = parentDisabled;
        isDisabled = getValue().isDisabled();
    }

    @Override public boolean isDisabled()             { return isDisabled; }
    @Override public ConfigValDisablerImpl getValue() { return (ConfigValDisablerImpl) getConfigVal(); }

    @Override
    public void reset() {
        super.reset();
        ConfigValDisablerImpl val = getValue();
        val.reset();
        val.reloadCache();
        isDisabled = val.isDisabled();
    }

    @Override
    public void reloadCache() {
        getValue().reloadCache();
        isDisabled = getValue().isDisabled();
    }
}