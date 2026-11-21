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
public interface ConfigParGroupTransformer<T extends CustomType<?>, CONTEXT extends CustomType<?>> extends ConfigParGroup<T> {
    T transform(T original, @Nullable CONTEXT context);
    T getReplacement();
}