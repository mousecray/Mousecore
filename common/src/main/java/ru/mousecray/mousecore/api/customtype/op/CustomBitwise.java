/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype.op;

import ru.mousecray.mousecore.api.customtype.CustomType;

public interface CustomBitwise<T extends CustomType<?>> {
    T not();
    T and(CustomType<?> other);
    T or(CustomType<?> other);
    T xor(CustomType<?> other);

    T leftShift(int other);
    T rightShift(int other);
    T uRightShift(int other);
}