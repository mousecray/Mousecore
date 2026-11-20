/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.core.config.build.val.container;

import ru.mousecray.mousecore.api.config.val.ConfigValNBT;
import ru.mousecray.mousecore.api.customtype.values.collection.NBTType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.base.AbstractConfigValueBuilder;
import ru.mousecray.mousecore.core.config.val.ConfigValNBTImpl;

import java.util.function.Consumer;

public final class ConfigNBTBuilder<BUILDER extends AbstractConfigChildBuilder> extends AbstractConfigValueBuilder<BUILDER, ConfigValNBT, NBTType, ConfigNBTBuilder<BUILDER>> {
    private ConfigNBTBuilder(BUILDER pb, Consumer<ConfigValNBT> oc) {
        super(pb, NBTType.EMPTY, NBTType.EMPTY, oc);
    }

    public static <BUILDER extends AbstractConfigChildBuilder> ConfigNBTBuilder<BUILDER> create(BUILDER pb, Consumer<ConfigValNBT> oc) {
        return new ConfigNBTBuilder<>(pb, oc);
    }

    public BUILDER buildNBT() {
        onComplete.accept(
                new ConfigValNBTImpl(
                        defaultValue, disabledValue, null,
                        genConstraints(), genConfigure()
                )
        );
        return parBuilder;
    }
}