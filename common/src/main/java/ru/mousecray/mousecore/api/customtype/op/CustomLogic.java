/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype.op;

import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.values.PlusMinusType;

public interface CustomLogic<T extends CustomType<?>> {
    PlusMinusType isLess(CustomType<?> other);
    PlusMinusType isMore(CustomType<?> other);
    PlusMinusType isEqual(CustomType<?> other);
    PlusMinusType isLessOrEqual(CustomType<?> other);
    PlusMinusType isMoreOrEqual(CustomType<?> other);

    T not();
    T and(CustomType<?> other);
    T or(CustomType<?> other);
}