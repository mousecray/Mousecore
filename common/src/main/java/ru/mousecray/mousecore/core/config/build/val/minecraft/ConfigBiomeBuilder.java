/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.core.config.build.val.minecraft;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.val.minecraft.ConfigValBiome;
import ru.mousecray.mousecore.api.customtype.values.minecraft.MinecraftBiomeType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.base.AbstractConfigValueBuilder;
import ru.mousecray.mousecore.core.config.val.minecraft.ConfigValBiomeImpl;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Consumer;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigBiomeBuilder<BUILDER extends AbstractConfigChildBuilder>
        extends AbstractConfigValueBuilder<BUILDER, ConfigValBiome, MinecraftBiomeType, ConfigBiomeBuilder<BUILDER>> {

    private ConfigBiomeBuilder(BUILDER parBuilder, Consumer<ConfigValBiome> onComplete) {
        super(parBuilder, MinecraftBiomeType.THE_VOID, MinecraftBiomeType.THE_VOID, onComplete);
    }

    public static <BUILDER extends AbstractConfigChildBuilder> ConfigBiomeBuilder<BUILDER> create(
            BUILDER parBuilder, Consumer<ConfigValBiome> onComplete
    ) {
        return new ConfigBiomeBuilder<>(Objects.requireNonNull(parBuilder), onComplete);
    }

    public BUILDER buildBiome() {
        onComplete.accept(
                new ConfigValBiomeImpl(
                        defaultValue, disabledValue, null,
                        genConstraints(), genConfigure()
                )
        );
        return parBuilder;
    }
}