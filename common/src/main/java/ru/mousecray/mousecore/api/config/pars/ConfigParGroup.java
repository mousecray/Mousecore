/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.config.pars;

import org.apache.commons.lang3.tuple.Pair;
import ru.mousecray.mousecore.api.DisplayName;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.*;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.utils.MouseReflection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public abstract class ConfigParGroup<T extends CustomType<?>> extends ConfigParBase<T> {
    @Nullable public final ConfigParDisabler disablePar;

    public ConfigParGroup(
            DisplayName name,
            @Nullable ConfigParDisabler disablePar,
            @Nonnull Pair<DisplayName, ? extends ConfigVal<T>> valuePair,
            @Nullable String comment, @Nullable Map<DisplayName, ConfigVal<?>> values
    ) {
        super(name, disablePar != null, valuePair, values, comment);
        this.disablePar = disablePar;
        if (disablePar != null) {
            MouseReflection.invokeMethod(
                    ConfigSectBase.class, ConfigSectBase.class, "setParent",
                    disablePar, this, hasLogger() ? getLogger() : null
            );
            MouseReflection.invokeMethod(
                    ConfigSectBase.class, int.class, "setDeep",
                    disablePar, deep + 1, hasLogger() ? getLogger() : null
            );
            setConfig(config);
        }
    }

    @Override
    protected void setDeep(int deep) {
        super.setDeep(deep);
        if (canBeDisabled()) {
            MouseReflection.invokeMethod(
                    ConfigSectBase.class, int.class, "setDeep",
                    disablePar, deep + 1, hasLogger() ? getLogger() : null
            );
        }
    }

    @Override
    protected void setConfig(MouseConfig config) {
        super.setConfig(config);
        if (canBeDisabled()) {
            MouseReflection.invokeMethod(
                    ConfigSectBase.class, MouseConfig.class, "setParent",
                    disablePar, config, hasLogger() ? getLogger() : null
            );
        }
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

    public abstract T getMainValue();
}