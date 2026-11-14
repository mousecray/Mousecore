/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.api.config.val.base.list;

import ru.mousecray.mousecore.api.config.val.base.ConfigListVal;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.values.ConditionalValType;
import ru.mousecray.mousecore.api.customtype.values.SimpleListType;

public interface ConfigValSimpleList<T extends CustomType<?>> extends ConfigListVal<ConditionalValType<T>, T, SimpleListType<T>> {
    boolean isConditional();
}