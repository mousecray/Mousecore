/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.core.config.build.val.container;

import ru.mousecray.mousecore.api.config.val.container.ConfigValMatrix;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.values.collection.SimpleMatrixType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.base.AbstractConfigValueBuilder;
import ru.mousecray.mousecore.core.config.val.container.ConfigValMatrixImpl;

import java.util.List;
import java.util.function.Consumer;

public final class ConfigMatrixBuilder<BUILDER extends AbstractConfigChildBuilder> extends AbstractConfigValueBuilder<BUILDER, ConfigValMatrix, SimpleMatrixType, ConfigMatrixBuilder<BUILDER>> {
    private final List<IValType> schema;

    private ConfigMatrixBuilder(BUILDER pb, List<IValType> schema, SimpleMatrixType def, SimpleMatrixType dis, Consumer<ConfigValMatrix> oc) {
        super(pb, def, dis, oc);
        this.schema = schema;
    }

    public static <BUILDER extends AbstractConfigChildBuilder> ConfigMatrixBuilder<BUILDER> create(BUILDER pb, List<IValType> schema, SimpleMatrixType def, SimpleMatrixType dis, Consumer<ConfigValMatrix> oc) {
        return new ConfigMatrixBuilder<>(pb, schema, def, dis, oc);
    }

    public BUILDER buildMatrix() {
        onComplete.accept(
                new ConfigValMatrixImpl(
                        schema, defaultValue, disabledValue, null,
                        genConstraints(), genConfigure()
                )
        );
        return parBuilder;
    }
}