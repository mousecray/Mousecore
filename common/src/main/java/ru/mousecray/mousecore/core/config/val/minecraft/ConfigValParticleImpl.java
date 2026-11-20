/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.core.config.val.minecraft;

import net.minecraft.core.particles.ParticleType;
import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.minecraft.ConfigValParticle;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.values.minecraft.MinecraftParticleType;
import ru.mousecray.mousecore.api.minecraft.MouseRegistryEntry;
import ru.mousecray.mousecore.core.config.val.base.ConfigValMinecraftImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@FieldsAreNonnullByDefault
@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public class ConfigValParticleImpl extends ConfigValMinecraftImpl<MinecraftParticleType> implements ConfigValParticle {
    @SafeVarargs
    public ConfigValParticleImpl(
            MinecraftParticleType defaultValue, MinecraftParticleType disabledValue,
            @Nullable String specificDataType,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<MinecraftParticleType>... configureValues
    ) {
        super(MinecraftParticleType.class, defaultValue, disabledValue, specificDataType, constraints, configureValues);
    }

    @Override public MouseRegistryEntry<ParticleType<?>> getRegistryEntry()      { return getValue().asRegistryEntry(); }
    @Override public String getPrefix()                                          { return getValue().getPrefix(); }
    @Override public String getName()                                            { return getValue().getName(); }
    @Override public void setParticle(String prefix, String name)                { setValue(MinecraftParticleType.create(prefix, name)); }
    @Override public void setParticle(MouseRegistryEntry<ParticleType<?>> entry) { setValue(MinecraftParticleType.create(entry)); }
}