/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.api.config.val.container;

import ru.mousecray.mousecore.api.config.val.ConfigVal;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.values.collection.SimpleMatrixType;

import java.util.List;

public interface ConfigValMatrix extends ConfigVal<SimpleMatrixType> {
    List<IValType> getSchema();
    double[][] getAs2DArray();

    void setMatrix(double[][] data);
}