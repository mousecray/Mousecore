/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.api.config.val;

import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.values.collection.SimpleMapType;

import java.util.Map;

public interface ConfigValMap<K extends CustomType<?>, V extends CustomType<?>> extends ConfigVal<SimpleMapType<K, V>> {
    IValType getKeyType();
    IValType getValueType();
    Map<K, V> getMap();
    void setMap(Map<K, V> map);
}