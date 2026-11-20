/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.api.config.val.other;

import ru.mousecray.mousecore.api.config.val.base.ConfigOtherVal;
import ru.mousecray.mousecore.api.customtype.values.other.EnumType;

public interface ConfigValEnum<E extends Enum<E>> extends ConfigOtherVal<EnumType<E>> {
    E getEnum();
    Class<E> getEnumClass();

    void setEnum(E value);
}