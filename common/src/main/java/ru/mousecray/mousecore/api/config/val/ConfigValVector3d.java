/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.api.config.val;

import ru.mousecray.mousecore.api.config.val.base.ConfigVector3Val;
import ru.mousecray.mousecore.api.customtype.values.number.DecimalType;
import ru.mousecray.mousecore.api.customtype.values.vec3.BlockPosType;
import ru.mousecray.mousecore.api.customtype.values.vec3.Vector3dType;

public interface ConfigValVector3d extends ConfigVector3Val<DecimalType, Vector3dType> {
    double getDoubleX();
    double getDoubleY();
    double getDoubleZ();

    void setDouble(double x, double y, double z);
    BlockPosType asBlockPos();
}