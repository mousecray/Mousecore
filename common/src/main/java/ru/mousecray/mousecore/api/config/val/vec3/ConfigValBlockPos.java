/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.api.config.val.vec3;

import ru.mousecray.mousecore.api.config.val.base.ConfigVector3Val;
import ru.mousecray.mousecore.api.customtype.values.number.IntegralType;
import ru.mousecray.mousecore.api.customtype.values.vec3.BlockPosType;
import ru.mousecray.mousecore.api.customtype.values.vec3.Vector3dType;

public interface ConfigValBlockPos extends ConfigVector3Val<IntegralType, BlockPosType> {
    long getLongX();
    long getLongY();
    long getLongZ();

    int getIntX();
    int getIntY();
    int getIntZ();

    void setLong(long x, long y, long z);
    void setInt(int x, int y, int z);
    Vector3dType asVector3d();
}