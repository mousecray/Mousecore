package ru.mousecray.mousecore.core.config.sect;

import ru.mousecray.mousecore.api.DisplayName;
import ru.mousecray.mousecore.api.config.sect.ConfigSect;
import ru.mousecray.mousecore.api.config.sect.ConfigSectBase;
import ru.mousecray.mousecore.core.config.MouseConfigImpl;
import ru.mousecray.mousecore.core.config.par.ConfigParBaseImpl;
import ru.mousecray.mousecore.core.config.par.ConfigParDisablerImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ConfigSectImpl extends ConfigSectBaseImpl implements ConfigSect {
    @Nullable public final ConfigParDisablerImpl disablePar;

    public ConfigSectImpl(DisplayName name)                                             { this(name, false, null, null); }
    public ConfigSectImpl(DisplayName name, @Nullable String comment)                   { this(name, false, null, comment); }
    public ConfigSectImpl(DisplayName name, @Nullable ConfigParDisablerImpl disablePar) { this(name, disablePar != null, disablePar, null); }
    public ConfigSectImpl(DisplayName name, @Nullable ConfigParDisablerImpl disablePar, @Nullable String comment) {
        this(name, disablePar != null, disablePar, comment);
    }

    public ConfigSectImpl(
            DisplayName name, boolean canBeDisabled,
            @Nullable ConfigParDisablerImpl disablePar, @Nullable String comment
    ) {
        super(name, canBeDisabled || disablePar != null, comment);
        this.disablePar = disablePar;
        if (disablePar != null) {
            disablePar.setParent(this);
            disablePar.setDeep(deep + 1);
            disablePar.setConfig(config);
            isDisabled = disablePar.isDisabled();
        }
    }

    public boolean hasDisabler() { return disablePar != null; }

    @SuppressWarnings("DataFlowIssue") @Override public void setDeep(int deep) {
        super.setDeep(deep);
        if (hasDisabler()) disablePar.setDeep(deep + 1);
    }

    @SuppressWarnings("DataFlowIssue") @Override public void setConfig(MouseConfigImpl config) {
        super.setConfig(config);
        if (hasDisabler()) disablePar.setConfig(config);
    }

    @SuppressWarnings("DataFlowIssue") @Override
    public void setDisabled(boolean disabled) {
        if (!canBeDisabled()) throw new UnsupportedOperationException("This ConfigSection does not support disabling");
        if (hasDisabler()) {
            disablePar.setDisabled(disabled);
            isDisabled = disablePar.isDisabled();
        } else {
            isDisabled = disabled;
            if (hasConfig() && config.isBuilt()) config.reloadCache();
            else setDisabledStructurally(isStructurallyDisabled);
        }
    }

    @SuppressWarnings("DataFlowIssue") @Override
    public void reset() {
        super.reset();
        if (hasDisabler()) {
            disablePar.reset();
            isDisabled = disablePar.isDisabled();
        }
    }

    @SuppressWarnings("DataFlowIssue") @Override
    public void reloadCache() {
        if (hasDisabler()) {
            disablePar.reloadCache();
            isDisabled = disablePar.isDisabled();
        }
        boolean effective = isDisabled();
        if (hasChildren()) children.forEach((key, value) -> value.setDisabledStructurally(effective));
        super.reloadCache();
    }

    @SuppressWarnings("DataFlowIssue") @Override
    public void setDisabledStructurally(boolean parentDisabled) {
        isStructurallyDisabled = parentDisabled;
        if (hasDisabler()) {
            disablePar.setDisabledStructurally(parentDisabled);
            isDisabled = disablePar.isDisabled();
        }
        boolean effective = isDisabled();
        if (hasChildren()) children.forEach((key, value) -> value.setDisabledStructurally(effective));
    }

    @Override public boolean isSupportChildren() { return true; }

    @Override
    public <T extends ConfigSectBase> boolean isSupportChild(T child) {
        return child instanceof ConfigSectImpl || child instanceof ConfigParBaseImpl;
    }
}