/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.api.config.val.base.list.conditional;

import ru.mousecray.mousecore.api.config.val.base.ConfigListVal;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.values.ConditionalListType;
import ru.mousecray.mousecore.api.customtype.values.ConditionalValType;

public interface ConfigConditionalListVal<T extends CustomType<?>> extends ConfigListVal<ConditionalValType<T>, T, ConditionalListType<T>> { }