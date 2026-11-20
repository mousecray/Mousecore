/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.api.config.val;

import ru.mousecray.mousecore.api.config.val.base.ConfigOtherVal;
import ru.mousecray.mousecore.api.customtype.values.minecraft.MinecraftEntityType;
import ru.mousecray.mousecore.api.minecraft.MouseEntityState;

public interface ConfigValEntity extends ConfigOtherVal<MinecraftEntityType> {
    MouseEntityState getEntityState();
    String getPrefix();
    String getName();

    void setEntity(String prefix, String name);
    void setEntity(MouseEntityState state);
}