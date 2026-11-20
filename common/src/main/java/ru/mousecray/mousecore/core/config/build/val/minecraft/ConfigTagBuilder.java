/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.core.config.build.val.minecraft;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.val.minecraft.ConfigValTag;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.values.minecraft.MinecraftTagType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.base.AbstractConfigValueBuilder;
import ru.mousecray.mousecore.core.config.val.minecraft.ConfigValTagImpl;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Consumer;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigTagBuilder<BUILDER extends AbstractConfigChildBuilder>
        extends AbstractConfigValueBuilder<BUILDER, ConfigValTag, MinecraftTagType, ConfigTagBuilder<BUILDER>> {

    private final IValType targetValType;

    private ConfigTagBuilder(BUILDER parBuilder, IValType targetValType, Consumer<ConfigValTag> onComplete) {
        super(
                parBuilder,
                MinecraftTagType.create("minecraft", "empty", targetValType),
                MinecraftTagType.create("minecraft", "empty", targetValType),
                onComplete
        );
        this.targetValType = Objects.requireNonNull(targetValType);
    }

    public static <BUILDER extends AbstractConfigChildBuilder> ConfigTagBuilder<BUILDER> create(
            BUILDER parBuilder, IValType targetValType, Consumer<ConfigValTag> onComplete
    ) {
        return new ConfigTagBuilder<>(Objects.requireNonNull(parBuilder), targetValType, onComplete);
    }

    public BUILDER buildTag() {
        onComplete.accept(
                new ConfigValTagImpl(
                        targetValType, defaultValue, disabledValue, null,
                        genConstraints(), genConfigure()
                )
        );
        return parBuilder;
    }
}