/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.api.config.par;

import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.values.RandomQuantityType;

public interface ConfigParGroupQuantityCondition<T extends CustomType<?>> extends ConfigParGroup<RandomQuantityType> {
    RandomQuantityType getValue(T conditionTarget);
}
