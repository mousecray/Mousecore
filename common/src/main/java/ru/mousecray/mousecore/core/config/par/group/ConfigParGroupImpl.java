package ru.mousecray.mousecore.core.config.par.group;

import org.apache.commons.lang3.tuple.Pair;
import ru.mousecray.mousecore.api.DisplayName;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.par.ConfigParGroup;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.core.config.MouseConfigImpl;
import ru.mousecray.mousecore.core.config.par.ConfigParBaseImpl;
import ru.mousecray.mousecore.core.config.par.ConfigParDisablerImpl;
import ru.mousecray.mousecore.core.config.val.ConfigValImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public abstract class ConfigParGroupImpl<T extends CustomType<?>> extends ConfigParBaseImpl<T> implements ConfigParGroup<T> {
    @Nullable public final ConfigParDisablerImpl disablePar;

    public ConfigParGroupImpl(
            DisplayName name,
            @Nullable ConfigParDisablerImpl disablePar,
            Pair<DisplayName, ? extends ConfigValImpl<T>> valuePair,
            @Nullable String comment, @Nullable Map<DisplayName, ConfigValImpl<? extends CustomType<?>>> values
    ) {
        this(name, disablePar != null, disablePar, valuePair, comment, values);
    }

    public ConfigParGroupImpl(
            DisplayName name,
            boolean canBeDisabled,
            @Nullable ConfigParDisablerImpl disablePar,
            Pair<DisplayName, ? extends ConfigValImpl<T>> valuePair,
            @Nullable String comment, @Nullable Map<DisplayName, ConfigValImpl<? extends CustomType<?>>> values
    ) {
        super(name, canBeDisabled || disablePar != null, valuePair, values, comment);
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
        if (!canBeDisabled()) throw new UnsupportedOperationException("This ConfigParGroup does not support disabling");
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
        super.reloadCache();
    }

    @SuppressWarnings("DataFlowIssue") @Override
    public void setDisabledStructurally(boolean parentDisabled) {
        isStructurallyDisabled = parentDisabled;
        if (hasDisabler()) {
            disablePar.setDisabledStructurally(parentDisabled);
            isDisabled = disablePar.isDisabled();
        }
    }
}