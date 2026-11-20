/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.api.config.val;

import ru.mousecray.mousecore.api.config.val.base.ConfigOtherVal;
import ru.mousecray.mousecore.api.customtype.values.minecraft.MinecraftEnchantmentType;
import ru.mousecray.mousecore.api.minecraft.MouseEnchantmentState;

public interface ConfigValEnchantment extends ConfigOtherVal<MinecraftEnchantmentType> {
    MouseEnchantmentState getEnchantmentState();
    String getPrefix();
    String getName();

    void setEnchantment(String prefix, String name);
    void setEnchantment(MouseEnchantmentState state);
}