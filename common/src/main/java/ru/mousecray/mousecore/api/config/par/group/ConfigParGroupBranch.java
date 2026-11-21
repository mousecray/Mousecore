/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.api.config.par.group;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.CustomType;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public interface ConfigParGroupBranch<VAL extends CustomType<?>, TARGET extends CustomType<?>> extends ConfigParGroup<VAL> {
    VAL resolve(@Nullable TARGET conditionTarget);
    VAL getMatchValue();
    VAL getElseValue();
}