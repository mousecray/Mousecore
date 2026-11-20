/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.api.config.val.container;

import ru.mousecray.mousecore.api.config.val.ConfigVal;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.values.collection.SimpleTableType;
import ru.mousecray.mousecore.api.customtype.values.collection.SimpleTupleType;

import java.util.List;

public interface ConfigValTable extends ConfigVal<SimpleTableType> {
    List<IValType> getSchema();
    List<SimpleTupleType> getRows();

    void setRows(List<SimpleTupleType> rows);
}