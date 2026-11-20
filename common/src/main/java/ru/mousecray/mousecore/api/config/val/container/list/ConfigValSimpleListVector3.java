/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.api.config.val.container.list;

import ru.mousecray.mousecore.api.config.val.base.ConfigValSimpleList;
import ru.mousecray.mousecore.api.customtype.NumberType;
import ru.mousecray.mousecore.api.customtype.Vector3Type;
import ru.mousecray.mousecore.api.customtype.range.RangeContainer;

public interface ConfigValSimpleListVector3<N extends NumberType<?>, T extends Vector3Type<N, T>> extends ConfigValSimpleList<T> {
    RangeContainer<N> getRangeX();
    RangeContainer<N> getRangeY();
    RangeContainer<N> getRangeZ();
    boolean isInRange(T vector);
}