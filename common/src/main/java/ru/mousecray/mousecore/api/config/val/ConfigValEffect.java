/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.api.config.val;

import net.minecraft.world.effect.MobEffectInstance;
import ru.mousecray.mousecore.api.config.val.base.ConfigOtherVal;
import ru.mousecray.mousecore.api.customtype.values.minecraft.MinecraftEffectType;
import ru.mousecray.mousecore.api.customtype.values.number.TimeType;
import ru.mousecray.mousecore.api.minecraft.MouseEffectState;

public interface ConfigValEffect extends ConfigOtherVal<MinecraftEffectType> {
    MouseEffectState getEffectState();
    String getPrefix();
    String getName();

    MobEffectInstance createInstance(TimeType duration, int amplifier);
    MobEffectInstance createInstance(TimeType duration);

    void setEffect(String prefix, String name);
    void setEffect(MouseEffectState state);
}