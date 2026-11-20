/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.core.config.val;

import net.minecraft.sounds.SoundEvent;
import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.ConfigValSound;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.values.minecraft.MinecraftSoundType;
import ru.mousecray.mousecore.api.minecraft.MouseRegistryEntry;
import ru.mousecray.mousecore.core.config.val.base.ConfigValMinecraftImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@FieldsAreNonnullByDefault
@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public class ConfigValSoundImpl extends ConfigValMinecraftImpl<MinecraftSoundType> implements ConfigValSound {
    @SafeVarargs
    public ConfigValSoundImpl(
            MinecraftSoundType defaultValue, MinecraftSoundType disabledValue,
            @Nullable String specificDataType,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<MinecraftSoundType>... configureValues
    ) {
        super(MinecraftSoundType.class, defaultValue, disabledValue, specificDataType, constraints, configureValues);
    }

    @Override public MouseRegistryEntry<SoundEvent> getRegistryEntry()   { return getValue().asRegistryEntry(); }
    @Override public String getPrefix()                                  { return getValue().getPrefix(); }
    @Override public String getName()                                    { return getValue().getName(); }
    @Override public void setSound(String prefix, String name)           { setValue(MinecraftSoundType.create(prefix, name)); }
    @Override public void setSound(MouseRegistryEntry<SoundEvent> entry) { setValue(MinecraftSoundType.create(entry)); }
}