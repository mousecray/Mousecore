/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.core.config.val;

import net.minecraft.world.effect.MobEffectInstance;
import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.ConfigValEffect;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.values.minecraft.MinecraftEffectType;
import ru.mousecray.mousecore.api.customtype.values.number.TimeType;
import ru.mousecray.mousecore.api.minecraft.MouseEffectState;
import ru.mousecray.mousecore.core.config.val.base.ConfigValMinecraftImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@FieldsAreNonnullByDefault
@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public class ConfigValEffectImpl extends ConfigValMinecraftImpl<MinecraftEffectType> implements ConfigValEffect {
    @SafeVarargs
    public ConfigValEffectImpl(
            MinecraftEffectType defaultValue, MinecraftEffectType disabledValue,
            @Nullable String specificDataType,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<MinecraftEffectType>... configureValues
    ) {
        super(MinecraftEffectType.class, defaultValue, disabledValue, specificDataType, constraints, configureValues);
    }

    @Override public MouseEffectState getEffectState()                                  { return getValue().asEffectState(); }
    @Override public String getPrefix()                                                 { return getValue().getPrefix(); }
    @Override public String getName()                                                   { return getValue().getName(); }

    @Override public MobEffectInstance createInstance(TimeType duration, int amplifier) { return getValue().createInstance(duration, amplifier); }
    @Override public MobEffectInstance createInstance(TimeType duration)                { return getValue().createInstance(duration); }

    @Override public void setEffect(String prefix, String name)                         { setValue(MinecraftEffectType.create(prefix, name)); }
    @Override public void setEffect(MouseEffectState state)                             { setValue(MinecraftEffectType.create(state)); }
}