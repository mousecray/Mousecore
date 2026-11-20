/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.core.config.build.val.minecraft;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.val.ConfigValParticle;
import ru.mousecray.mousecore.api.customtype.values.minecraft.MinecraftParticleType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.base.AbstractConfigValueBuilder;
import ru.mousecray.mousecore.core.config.val.ConfigValParticleImpl;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Consumer;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigParticleBuilder<BUILDER extends AbstractConfigChildBuilder>
        extends AbstractConfigValueBuilder<BUILDER, ConfigValParticle, MinecraftParticleType, ConfigParticleBuilder<BUILDER>> {

    private ConfigParticleBuilder(BUILDER parBuilder, Consumer<ConfigValParticle> onComplete) {
        super(parBuilder, MinecraftParticleType.POOF, MinecraftParticleType.POOF, onComplete);
    }

    public static <BUILDER extends AbstractConfigChildBuilder> ConfigParticleBuilder<BUILDER> create(
            BUILDER parBuilder, Consumer<ConfigValParticle> onComplete
    ) {
        return new ConfigParticleBuilder<>(Objects.requireNonNull(parBuilder), onComplete);
    }

    public BUILDER buildParticle() {
        onComplete.accept(
                new ConfigValParticleImpl(
                        defaultValue, disabledValue, null,
                        genConstraints(), genConfigure()
                )
        );
        return parBuilder;
    }
}