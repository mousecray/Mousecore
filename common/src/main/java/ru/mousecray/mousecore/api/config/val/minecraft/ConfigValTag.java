/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.api.config.val.minecraft;

import net.minecraft.resources.ResourceLocation;
import ru.mousecray.mousecore.api.config.val.base.ConfigOtherVal;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.values.minecraft.*;

import javax.annotation.Nullable;

public interface ConfigValTag extends ConfigOtherVal<MinecraftTagType> {
    ResourceLocation getLocation();
    String getNamespace();
    String getPath();
    IValType getTargetValType();

    boolean contains(@Nullable MinecraftItemType item);
    boolean contains(@Nullable MinecraftBlockType block);
    boolean contains(@Nullable MinecraftItemStackType stack);
    boolean contains(@Nullable MinecraftEntityType entity);

    void setTag(ResourceLocation location);
    void setTag(String namespace, String path);
}