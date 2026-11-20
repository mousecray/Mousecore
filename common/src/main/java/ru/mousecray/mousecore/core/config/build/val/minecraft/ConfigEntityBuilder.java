/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.core.config.build.val.minecraft;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.val.minecraft.ConfigValEntity;
import ru.mousecray.mousecore.api.customtype.values.minecraft.MinecraftEntityType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.base.AbstractConfigValueBuilder;
import ru.mousecray.mousecore.core.config.val.minecraft.ConfigValEntityImpl;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Consumer;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigEntityBuilder<BUILDER extends AbstractConfigChildBuilder>
        extends AbstractConfigValueBuilder<BUILDER, ConfigValEntity, MinecraftEntityType, ConfigEntityBuilder<BUILDER>> {

    private ConfigEntityBuilder(BUILDER parBuilder, Consumer<ConfigValEntity> onComplete) {
        super(parBuilder, MinecraftEntityType.SHEEP, MinecraftEntityType.SHEEP, onComplete);
    }

    public static <BUILDER extends AbstractConfigChildBuilder> ConfigEntityBuilder<BUILDER> create(
            BUILDER parBuilder, Consumer<ConfigValEntity> onComplete
    ) {
        return new ConfigEntityBuilder<>(Objects.requireNonNull(parBuilder), onComplete);
    }

    public BUILDER buildEntity() {
        onComplete.accept(
                new ConfigValEntityImpl(
                        defaultValue, disabledValue, null,
                        genConstraints(), genConfigure()
                )
        );
        return parBuilder;
    }
}