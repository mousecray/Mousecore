/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.api.config.val;

import net.minecraft.core.particles.ParticleType;
import ru.mousecray.mousecore.api.config.val.base.ConfigOtherVal;
import ru.mousecray.mousecore.api.customtype.values.minecraft.MinecraftParticleType;
import ru.mousecray.mousecore.api.minecraft.MouseRegistryEntry;

public interface ConfigValParticle extends ConfigOtherVal<MinecraftParticleType> {
    MouseRegistryEntry<ParticleType<?>> getRegistryEntry();
    String getPrefix();
    String getName();

    void setParticle(String prefix, String name);
    void setParticle(MouseRegistryEntry<ParticleType<?>> entry);
}