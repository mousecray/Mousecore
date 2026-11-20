/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.core.config.build.val.minecraft;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.val.ConfigValEnchantment;
import ru.mousecray.mousecore.api.customtype.values.minecraft.MinecraftEnchantmentType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.base.AbstractConfigValueBuilder;
import ru.mousecray.mousecore.core.config.val.ConfigValEnchantmentImpl;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Consumer;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigEnchantmentBuilder<BUILDER extends AbstractConfigChildBuilder>
        extends AbstractConfigValueBuilder<BUILDER, ConfigValEnchantment, MinecraftEnchantmentType, ConfigEnchantmentBuilder<BUILDER>> {

    private ConfigEnchantmentBuilder(BUILDER parBuilder, Consumer<ConfigValEnchantment> onComplete) {
        super(parBuilder, MinecraftEnchantmentType.UNBREAKING, MinecraftEnchantmentType.UNBREAKING, onComplete);
    }

    public static <BUILDER extends AbstractConfigChildBuilder> ConfigEnchantmentBuilder<BUILDER> create(
            BUILDER parBuilder, Consumer<ConfigValEnchantment> onComplete
    ) {
        return new ConfigEnchantmentBuilder<>(Objects.requireNonNull(parBuilder), onComplete);
    }

    public BUILDER buildEnchantment() {
        onComplete.accept(
                new ConfigValEnchantmentImpl(
                        defaultValue, disabledValue, null,
                        genConstraints(), genConfigure()
                )
        );
        return parBuilder;
    }
}