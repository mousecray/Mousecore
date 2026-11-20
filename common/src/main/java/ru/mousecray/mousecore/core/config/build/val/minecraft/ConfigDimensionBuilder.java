/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.core.config.build.val.minecraft;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.val.minecraft.ConfigValDimension;
import ru.mousecray.mousecore.api.customtype.values.minecraft.MinecraftDimensionType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.base.AbstractConfigValueBuilder;
import ru.mousecray.mousecore.core.config.val.minecraft.ConfigValDimensionImpl;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Consumer;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigDimensionBuilder<BUILDER extends AbstractConfigChildBuilder>
        extends AbstractConfigValueBuilder<BUILDER, ConfigValDimension, MinecraftDimensionType, ConfigDimensionBuilder<BUILDER>> {

    private ConfigDimensionBuilder(BUILDER parBuilder, Consumer<ConfigValDimension> onComplete) {
        super(parBuilder, MinecraftDimensionType.OVERWORLD, MinecraftDimensionType.OVERWORLD, onComplete);
    }

    public static <BUILDER extends AbstractConfigChildBuilder> ConfigDimensionBuilder<BUILDER> create(
            BUILDER parBuilder, Consumer<ConfigValDimension> onComplete
    ) {
        return new ConfigDimensionBuilder<>(Objects.requireNonNull(parBuilder), onComplete);
    }

    public BUILDER buildDimension() {
        onComplete.accept(
                new ConfigValDimensionImpl(
                        defaultValue, disabledValue, null,
                        genConstraints(), genConfigure()
                )
        );
        return parBuilder;
    }
}