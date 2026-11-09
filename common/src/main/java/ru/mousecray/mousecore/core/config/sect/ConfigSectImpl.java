/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

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

    public ConfigSectImpl(DisplayName name)                                             { this(name, (String) null); }
    public ConfigSectImpl(DisplayName name, @Nullable String comment)                   { this(name, null, comment); }
    public ConfigSectImpl(DisplayName name, @Nullable ConfigParDisablerImpl disablePar) { this(name, disablePar, null); }

    public ConfigSectImpl(DisplayName name, @Nullable ConfigParDisablerImpl disablePar, @Nullable String comment) {
        super(name, disablePar != null, comment);
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
        disablePar.setDisabled(disabled);
    }

    @SuppressWarnings("DataFlowIssue") @Override
    public void reset() {
        super.reset();
        if (canBeDisabled()) disablePar.reset();
    }

    @SuppressWarnings("DataFlowIssue") @Override public void reloadCache() {
        super.reloadCache();
        if (canBeDisabled()) disablePar.reloadCache();
    }

    @SuppressWarnings("DataFlowIssue") @Override public boolean isDisabled() { return super.isDisabled() || !disablePar.isDisabled(); }
    @Override public boolean isSupportChildren()                             { return true; }

    @Override
    public <T extends ConfigSectBase> boolean isSupportChild(T child) {
        return child instanceof ConfigSectImpl || child instanceof ConfigParBaseImpl;
    }
}