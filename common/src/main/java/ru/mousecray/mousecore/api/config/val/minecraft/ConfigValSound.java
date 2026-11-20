/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.api.config.val.minecraft;

import net.minecraft.sounds.SoundEvent;
import ru.mousecray.mousecore.api.config.val.base.ConfigOtherVal;
import ru.mousecray.mousecore.api.customtype.values.minecraft.MinecraftSoundType;
import ru.mousecray.mousecore.api.minecraft.MouseRegistryEntry;

public interface ConfigValSound extends ConfigOtherVal<MinecraftSoundType> {
    MouseRegistryEntry<SoundEvent> getRegistryEntry();
    String getPrefix();
    String getName();

    void setSound(String prefix, String name);
    void setSound(MouseRegistryEntry<SoundEvent> entry);
}