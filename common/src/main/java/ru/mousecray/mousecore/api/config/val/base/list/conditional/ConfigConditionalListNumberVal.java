/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.api.config.val.base.list.conditional;

import ru.mousecray.mousecore.api.config.val.ISupportRange;
import ru.mousecray.mousecore.api.customtype.NumberType;

public interface ConfigConditionalListNumberVal<T extends NumberType<?>> extends ConfigConditionalListVal<T>, ISupportRange<T> { }