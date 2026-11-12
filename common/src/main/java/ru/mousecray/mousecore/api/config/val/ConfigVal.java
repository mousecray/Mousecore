/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.api.config.val;

import ru.mousecray.mousecore.api.config.MouseConfig;
import ru.mousecray.mousecore.api.config.par.ConfigParBase;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.type.IValType;

import javax.annotation.Nullable;

public interface ConfigVal<V extends CustomType<?>> {
    IValType getType();

    V getValue();
    V getDisabledValue();
    V getDefaultValue();

    @Nullable ConfigParBase<?> getOwner();
    /**
     * Current predefined value if it specified in config
     */
    boolean canBeConfigured();
    /**
     * Relations with other ConfigSectBase's
     */
    boolean canBeConstrained();
    boolean canBeDisabled();

    boolean isDisabled();
    boolean isLoaded();

    void reset();

    boolean hasConfig();
    MouseConfig getConfig();
}