/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.core.config.build.val.minecraft;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.val.ConfigValItem;
import ru.mousecray.mousecore.api.customtype.values.minecraft.MinecraftItemType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.base.AbstractConfigValueBuilder;
import ru.mousecray.mousecore.core.config.val.ConfigValItemImpl;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Consumer;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigItemBuilder<BUILDER extends AbstractConfigChildBuilder>
        extends AbstractConfigValueBuilder<BUILDER, ConfigValItem, MinecraftItemType, ConfigItemBuilder<BUILDER>> {

    private ConfigItemBuilder(BUILDER parBuilder, Consumer<ConfigValItem> onComplete) {
        super(parBuilder, MinecraftItemType.AIR, MinecraftItemType.AIR, onComplete);
    }

    public static <BUILDER extends AbstractConfigChildBuilder> ConfigItemBuilder<BUILDER> create(
            BUILDER parBuilder, Consumer<ConfigValItem> onComplete
    ) {
        return new ConfigItemBuilder<>(Objects.requireNonNull(parBuilder), onComplete);
    }

    public BUILDER buildItem() {
        onComplete.accept(
                new ConfigValItemImpl(
                        defaultValue, disabledValue, null,
                        genConstraints(), genConfigure()
                )
        );
        return parBuilder;
    }
}