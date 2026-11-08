/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.config.build;

import ru.mousecray.mousecore.api.DisplayName;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.ConfigParGroup;
import ru.mousecray.mousecore.api.utils.MouseReflection;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class ConfigCustomParGroupBuilder<VAL extends Comparable<VAL>, BUILDER extends ConfigParameterGroupBuilder<VAL>> {
    private BUILDER parBuilder;

    protected abstract ConfigParGroup<VAL> createGroup(DisplayName groupName);

    public final BUILDER buildGroup() {
        ConfigParGroup<?> val = createGroup(parBuilder.name);
        Objects.requireNonNull(val);
        if (!MouseReflection.invokeMethod(
                parBuilder.getClass(), ConfigParGroup.class,
                "setGroup", parBuilder, val, parBuilder.configBuilder.logger
        )) {
            throw new RuntimeException("Config building error");
        }
        return parBuilder;
    }
}