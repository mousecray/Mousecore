/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.api.customtype.type;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.CustomType;

import javax.annotation.Nullable;

@MethodReturnsNonnullByDefault
public interface IValType {
    String getDisplayName();
    ValTypeCategory getCategory();
    @Nullable Class<? extends CustomType> getTypeClass();
}