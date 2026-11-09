/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.build;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.val.ConfigVal;
import ru.mousecray.mousecore.api.utils.MouseReflection;
import ru.mousecray.mousecore.core.config.val.ConfigValImpl;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class ConfigValueBuilder<BUILDER extends AbstractConfigParameterBuilder<BUILDER>> {
    private BUILDER parBuilder;

    @Nonnull protected abstract ConfigVal<? extends ru.mousecray.mousecore.api.customtype.CustomType<?>> createValue();

    public final BUILDER buildValue() {
        ConfigVal<? extends ru.mousecray.mousecore.api.customtype.CustomType<?>> val = createValue();
        Objects.requireNonNull(val);
        if (!MouseReflection.invokeMethod(
                parBuilder.getClass(), ConfigValImpl.class,
                "setValue", parBuilder, val, parBuilder.configBuilder.logger
        )) {
            throw new RuntimeException("Config building error");
        }
        return parBuilder;
    }
}