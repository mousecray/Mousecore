/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.api.config.val.base;

import ru.mousecray.mousecore.api.config.val.ConfigVal;
import ru.mousecray.mousecore.api.customtype.LogicalType;

public interface ConfigLogicalVal<T extends LogicalType<?>> extends ConfigVal<T> {
    boolean isTrue();
    boolean isFalse();
    void setTrue();
    void setFalse();
}
