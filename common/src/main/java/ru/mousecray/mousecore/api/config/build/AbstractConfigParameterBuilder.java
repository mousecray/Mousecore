/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.config.build;

import ru.mousecray.mousecore.api.DisplayName;
import ru.mousecray.mousecore.api.config.ConfigVal;

import javax.annotation.Nullable;

public abstract class AbstractConfigParameterBuilder<
        BUILDER extends AbstractConfigParameterBuilder<BUILDER>
        > extends AbstractConfigChildBuilder {

    protected AbstractConfigParameterBuilder(MouseConfigBuilder configBuilder, String path, DisplayName name) {
        super(configBuilder, path, name);
    }

    protected AbstractConfigParameterBuilder(MouseConfigBuilder configBuilder, DisplayName name) {
        super(configBuilder, null, name);
    }

    @SuppressWarnings("unchecked") @Override
    public BUILDER setComment(@Nullable String comment) {
        this.comment = comment;
        return (BUILDER) this;
    }

    @SuppressWarnings("unchecked") @Override
    public BUILDER setCanBeDisabled() {
        canBeDisabled = true;
        return (BUILDER) this;
    }

    protected abstract void setValue(ConfigVal val);
}