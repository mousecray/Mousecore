/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.api.config.val.base.list;

import ru.mousecray.mousecore.api.VariableValue;
import ru.mousecray.mousecore.api.customtype.NumberType;
import ru.mousecray.mousecore.api.customtype.range.RangeContainer;

public interface ConfigValSimpleListNumber<T extends NumberType<?>> extends ConfigValSimpleList<T> {
    boolean isInRange(T other);
    RangeContainer<T> getRange();
    VariableValue<T> getMinValue();
    VariableValue<T> getMaxValue();
}