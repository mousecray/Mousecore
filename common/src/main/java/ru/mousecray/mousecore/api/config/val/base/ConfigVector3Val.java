/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.api.config.val.base;

import ru.mousecray.mousecore.api.config.val.ConfigVal;
import ru.mousecray.mousecore.api.customtype.NumberType;
import ru.mousecray.mousecore.api.customtype.Vector3Type;
import ru.mousecray.mousecore.api.customtype.range.RangeContainer;

public interface ConfigVector3Val<N extends NumberType<?>, T extends Vector3Type<N, T>> extends ConfigVal<T> {
    N getX();
    N getY();
    N getZ();

    double getLength();
    double getLengthSq();

    RangeContainer<N> getRangeX();
    RangeContainer<N> getRangeY();
    RangeContainer<N> getRangeZ();

    void set(N x, N y, N z);
}