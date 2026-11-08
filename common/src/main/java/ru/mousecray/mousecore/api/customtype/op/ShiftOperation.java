/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype.op;

import ru.mousecray.mousecore.api.customtype.CustomType;

@FunctionalInterface
public interface ShiftOperation<T extends CustomType<?>, RES> {
    RES execute(T target, int shiftAmount);
}