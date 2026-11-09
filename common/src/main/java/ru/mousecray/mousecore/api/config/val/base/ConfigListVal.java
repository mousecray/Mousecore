/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.api.config.val.base;

import ru.mousecray.mousecore.api.config.val.ConfigVal;
import ru.mousecray.mousecore.api.config.val.IValType;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.ListType;

import java.util.function.Consumer;

public interface ConfigListVal<LIST_VAL extends CustomType<?>, VAL extends CustomType<?>, T extends ListType<LIST_VAL, VAL>> extends ConfigVal<T> {
    IValType getListComponentType();
    void forEachList(Consumer<LIST_VAL> func);
    void clearList();
    boolean isEmptyList();
    boolean removeListValue(LIST_VAL value);
    boolean addListValue(LIST_VAL value);
    boolean containsValue(LIST_VAL value);
    boolean containsOriginalValue(VAL value);
}
