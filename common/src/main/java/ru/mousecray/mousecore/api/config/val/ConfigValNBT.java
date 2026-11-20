/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.config.val;

import net.minecraft.nbt.CompoundTag;
import ru.mousecray.mousecore.api.customtype.values.collection.NBTType;

public interface ConfigValNBT extends ConfigVal<NBTType> {
    CompoundTag getCompoundTag();
    void setCompoundTag(CompoundTag tag);
}