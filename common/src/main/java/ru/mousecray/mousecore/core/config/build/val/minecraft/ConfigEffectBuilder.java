/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.core.config.build.val.minecraft;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.val.ConfigValEffect;
import ru.mousecray.mousecore.api.customtype.values.minecraft.MinecraftEffectType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.base.AbstractConfigValueBuilder;
import ru.mousecray.mousecore.core.config.val.ConfigValEffectImpl;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Consumer;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigEffectBuilder<BUILDER extends AbstractConfigChildBuilder>
        extends AbstractConfigValueBuilder<BUILDER, ConfigValEffect, MinecraftEffectType, ConfigEffectBuilder<BUILDER>> {

    private ConfigEffectBuilder(BUILDER parBuilder, Consumer<ConfigValEffect> onComplete) {
        super(parBuilder, MinecraftEffectType.SPEED, MinecraftEffectType.SPEED, onComplete);
    }

    public static <BUILDER extends AbstractConfigChildBuilder> ConfigEffectBuilder<BUILDER> create(
            BUILDER parBuilder, Consumer<ConfigValEffect> onComplete
    ) {
        return new ConfigEffectBuilder<>(Objects.requireNonNull(parBuilder), onComplete);
    }

    public BUILDER buildEffect() {
        onComplete.accept(
                new ConfigValEffectImpl(
                        defaultValue, disabledValue, null,
                        genConstraints(), genConfigure()
                )
        );
        return parBuilder;
    }
}