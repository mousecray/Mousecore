/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.api.config.val.base.list.simple;

import ru.mousecray.mousecore.api.config.val.ISupportRange;
import ru.mousecray.mousecore.api.customtype.NumberType;

public interface ConfigSimpleListNumberVal<T extends NumberType<?>> extends ConfigSimpleListVal<T>, ISupportRange<T> { }