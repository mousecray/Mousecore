/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.core.config.build.val.container;

import ru.mousecray.mousecore.api.config.val.ConfigValMap;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.values.collection.SimpleMapType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.base.AbstractConfigValueBuilder;
import ru.mousecray.mousecore.core.config.val.ConfigValMapImpl;

import java.util.function.Consumer;

public final class ConfigMapBuilder<K extends CustomType<?>, V extends CustomType<?>, BUILDER extends AbstractConfigChildBuilder> extends AbstractConfigValueBuilder<BUILDER, ConfigValMap<K, V>, SimpleMapType<K, V>, ConfigMapBuilder<K, V, BUILDER>> {
    private final IValType keyType;
    private final IValType valueType;

    private ConfigMapBuilder(BUILDER pb, IValType keyType, IValType valueType, Consumer<ConfigValMap<K, V>> oc) {
        super(pb, SimpleMapType.create(keyType, valueType), SimpleMapType.create(keyType, valueType), oc);
        this.keyType = keyType;
        this.valueType = valueType;
    }

    public static <K extends CustomType<?>, V extends CustomType<?>, BUILDER extends AbstractConfigChildBuilder> ConfigMapBuilder<K, V, BUILDER> create(BUILDER pb, IValType keyType, IValType valueType, Consumer<ConfigValMap<K, V>> oc) {
        return new ConfigMapBuilder<>(pb, keyType, valueType, oc);
    }

    public BUILDER buildMap() {
        onComplete.accept(
                new ConfigValMapImpl<>(
                        keyType, valueType,
                        defaultValue, disabledValue, null,
                        genConstraints(), genConfigure()
                )
        );
        return parBuilder;
    }
}