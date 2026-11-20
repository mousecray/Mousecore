/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.config.val;

import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.values.collection.SimpleTupleType;

import java.util.List;

public interface ConfigValTuple extends ConfigVal<SimpleTupleType> {
    List<IValType> getSchema();
    List<CustomType<?>> getElements();

    void setElements(List<CustomType<?>> elements);
}