/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.core.config.build.val.minecraft;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.val.ConfigValItemStack;
import ru.mousecray.mousecore.api.customtype.values.minecraft.MinecraftItemStackType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.base.AbstractConfigValueBuilder;
import ru.mousecray.mousecore.core.config.val.ConfigValItemStackImpl;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Consumer;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigItemStackBuilder<BUILDER extends AbstractConfigChildBuilder>
        extends AbstractConfigValueBuilder<BUILDER, ConfigValItemStack, MinecraftItemStackType, ConfigItemStackBuilder<BUILDER>> {

    private ConfigItemStackBuilder(BUILDER parBuilder, Consumer<ConfigValItemStack> onComplete) {
        super(parBuilder, MinecraftItemStackType.EMPTY, MinecraftItemStackType.EMPTY, onComplete);
    }

    public static <BUILDER extends AbstractConfigChildBuilder> ConfigItemStackBuilder<BUILDER> create(
            BUILDER parBuilder, Consumer<ConfigValItemStack> onComplete
    ) {
        return new ConfigItemStackBuilder<>(Objects.requireNonNull(parBuilder), onComplete);
    }

    public BUILDER buildItemStack() {
        onComplete.accept(
                new ConfigValItemStackImpl(
                        defaultValue, disabledValue, null,
                        genConstraints(), genConfigure()
                )
        );
        return parBuilder;
    }
}