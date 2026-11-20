/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.core.config.build.val.minecraft;

import net.minecraft.sounds.SoundEvents;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.val.minecraft.ConfigValSound;
import ru.mousecray.mousecore.api.customtype.values.minecraft.MinecraftSoundType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.base.AbstractConfigValueBuilder;
import ru.mousecray.mousecore.core.config.val.minecraft.ConfigValSoundImpl;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Consumer;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigSoundBuilder<BUILDER extends AbstractConfigChildBuilder>
        extends AbstractConfigValueBuilder<BUILDER, ConfigValSound, MinecraftSoundType, ConfigSoundBuilder<BUILDER>> {

    private ConfigSoundBuilder(BUILDER parBuilder, MinecraftSoundType defaultSound, Consumer<ConfigValSound> onComplete) {
        super(parBuilder, defaultSound, defaultSound, onComplete);
    }

    public static <BUILDER extends AbstractConfigChildBuilder> ConfigSoundBuilder<BUILDER> create(
            BUILDER parBuilder, MinecraftSoundType defaultSound, Consumer<ConfigValSound> onComplete
    ) {
        return new ConfigSoundBuilder<>(Objects.requireNonNull(parBuilder), Objects.requireNonNull(defaultSound), onComplete);
    }

    public static <BUILDER extends AbstractConfigChildBuilder> ConfigSoundBuilder<BUILDER> create(
            BUILDER parBuilder, Consumer<ConfigValSound> onComplete
    ) {
        return create(parBuilder, MinecraftSoundType.create(SoundEvents.EMPTY), onComplete);
    }

    public BUILDER buildSound() {
        onComplete.accept(
                new ConfigValSoundImpl(
                        defaultValue, disabledValue, null,
                        genConstraints(), genConfigure()
                )
        );
        return parBuilder;
    }
}