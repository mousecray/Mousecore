/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.build.par.group;

import ru.mousecray.mousecore.api.DisplayName;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.par.ConfigParGroup;
import ru.mousecray.mousecore.api.customtype.CustomType;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class ConfigCustomParGroupBuilder<VAL extends CustomType<?>, BUILDER extends ConfigParameterGroupBuilder<VAL>> {
    public BUILDER parBuilder;

    protected abstract ConfigParGroup<VAL> createGroup(DisplayName groupName);

    public final BUILDER buildGroup() {
        ConfigParGroup<? extends CustomType<?>> val = createGroup(parBuilder.name);
        Objects.requireNonNull(val);
        parBuilder.setGroup(val);
        return parBuilder;
    }
}