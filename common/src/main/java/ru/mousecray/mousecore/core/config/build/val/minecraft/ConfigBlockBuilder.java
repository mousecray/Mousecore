/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.core.config.build.val.minecraft;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.val.ConfigValBlock;
import ru.mousecray.mousecore.api.customtype.values.minecraft.MinecraftBlockType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.base.AbstractConfigValueBuilder;
import ru.mousecray.mousecore.core.config.val.ConfigValBlockImpl;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Consumer;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigBlockBuilder<BUILDER extends AbstractConfigChildBuilder>
        extends AbstractConfigValueBuilder<BUILDER, ConfigValBlock, MinecraftBlockType, ConfigBlockBuilder<BUILDER>> {

    private ConfigBlockBuilder(BUILDER parBuilder, Consumer<ConfigValBlock> onComplete) {
        super(parBuilder, MinecraftBlockType.AIR, MinecraftBlockType.AIR, onComplete);
    }

    public static <BUILDER extends AbstractConfigChildBuilder> ConfigBlockBuilder<BUILDER> create(
            BUILDER parBuilder, Consumer<ConfigValBlock> onComplete
    ) {
        return new ConfigBlockBuilder<>(Objects.requireNonNull(parBuilder), onComplete);
    }

    public BUILDER buildBlock() {
        onComplete.accept(
                new ConfigValBlockImpl(
                        defaultValue, disabledValue, null,
                        genConstraints(), genConfigure()
                )
        );
        return parBuilder;
    }
}