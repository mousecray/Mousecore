/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.par;

import org.apache.commons.lang3.tuple.Pair;
import ru.mousecray.mousecore.api.DisplayName;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.par.ConfigParGroup;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.core.config.MouseConfigImpl;
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
        super(name, disablePar != null, valuePair, values, comment);
        this.disablePar = disablePar;
        if (disablePar != null) {
            disablePar.setParent(this);
            disablePar.setDeep(deep + 1);
            setConfig(config);
        }
    }

    @SuppressWarnings("DataFlowIssue") @Override public void setDeep(int deep) {
        super.setDeep(deep);
        if (canBeDisabled()) disablePar.setDeep(deep + 1);
    }

    @SuppressWarnings("DataFlowIssue") @Override public void setConfig(MouseConfigImpl config) {
        super.setConfig(config);
        if (canBeDisabled()) disablePar.setConfig(config);
    }

    @SuppressWarnings("DataFlowIssue") @Override
    public void setDisabled(boolean disabled) {
        super.setDisabled(disabled);
        disablePar.setDisabled(!disabled);
    }

    @SuppressWarnings("DataFlowIssue") @Override
    public void reset() {
        super.reset();
        if (canBeDisabled()) disablePar.reset();
    }

    @SuppressWarnings("DataFlowIssue") @Override
    protected void reloadCache() {
        super.reloadCache();
        if (canBeDisabled()) disablePar.reloadCache();
    }

    @SuppressWarnings("DataFlowIssue") @Override public boolean isDisabled() { return super.isDisabled() || !disablePar.isDisabled(); }

}