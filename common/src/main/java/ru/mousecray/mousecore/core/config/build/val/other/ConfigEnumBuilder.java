/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.core.config.build.val.other;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.val.ConfigValEnum;
import ru.mousecray.mousecore.api.customtype.values.other.EnumType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.base.AbstractConfigValueBuilder;
import ru.mousecray.mousecore.core.config.val.ConfigValEnumImpl;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Consumer;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigEnumBuilder<E extends Enum<E>, BUILDER extends AbstractConfigChildBuilder>
        extends AbstractConfigValueBuilder<BUILDER, ConfigValEnum<E>, EnumType<E>, ConfigEnumBuilder<E, BUILDER>> {

    private final Class<E> enumClass;

    private ConfigEnumBuilder(BUILDER parBuilder, Class<E> enumClass, Consumer<ConfigValEnum<E>> onComplete) {
        super(parBuilder, EnumType.create(enumClass.getEnumConstants()[0]), EnumType.create(enumClass.getEnumConstants()[0]), onComplete);
        this.enumClass = enumClass;
    }

    public static <E extends Enum<E>, BUILDER extends AbstractConfigChildBuilder> ConfigEnumBuilder<E, BUILDER> create(
            BUILDER parBuilder, Class<E> enumClass, Consumer<ConfigValEnum<E>> onComplete
    ) {
        return new ConfigEnumBuilder<>(Objects.requireNonNull(parBuilder), Objects.requireNonNull(enumClass), onComplete);
    }

    public BUILDER buildEnum() {
        onComplete.accept(
                new ConfigValEnumImpl<>(
                        enumClass, defaultValue, disabledValue, null,
                        genConstraints(), genConfigure()
                )
        );
        return parBuilder;
    }
}