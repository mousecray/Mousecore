/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.api.config.par;

import ru.mousecray.mousecore.api.DisplayName;
import ru.mousecray.mousecore.api.config.sect.ConfigSectBase;
import ru.mousecray.mousecore.api.config.val.ConfigVal;
import ru.mousecray.mousecore.api.container.ImmutableDisplayNameMap;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.type.IValType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ConfigParBase<T extends CustomType<?>> extends ConfigSectBase {
    IValType getType();
    int getCapacity();

    boolean isSupportAdditionalValue(ConfigVal<? extends CustomType<?>> val);

    @Nonnull ConfigVal<T> getConfigVal();
    @Nullable ConfigVal<? extends CustomType<?>> getConfigVal(String internalName);
    @Nullable ConfigVal<? extends CustomType<?>> getConfigVal(DisplayName name);
    @Nullable ConfigVal getValByDisplayName(String name);

    @Nonnull ImmutableDisplayNameMap<ConfigVal<?>> getValues();
}
