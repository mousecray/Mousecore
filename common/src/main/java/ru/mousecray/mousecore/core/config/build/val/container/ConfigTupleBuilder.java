/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.core.config.build.val.container;

import ru.mousecray.mousecore.api.config.val.ConfigValTuple;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.values.collection.SimpleTupleType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.base.AbstractConfigValueBuilder;
import ru.mousecray.mousecore.core.config.val.ConfigValTupleImpl;

import java.util.List;
import java.util.function.Consumer;

public final class ConfigTupleBuilder<BUILDER extends AbstractConfigChildBuilder> extends AbstractConfigValueBuilder<BUILDER, ConfigValTuple, SimpleTupleType, ConfigTupleBuilder<BUILDER>> {
    private final List<IValType> schema;

    private ConfigTupleBuilder(BUILDER pb, List<IValType> schema, SimpleTupleType def, SimpleTupleType dis, Consumer<ConfigValTuple> oc) {
        super(pb, def, dis, oc);
        this.schema = schema;
    }

    public static <BUILDER extends AbstractConfigChildBuilder> ConfigTupleBuilder<BUILDER> create(BUILDER pb, List<IValType> schema, SimpleTupleType def, SimpleTupleType dis, Consumer<ConfigValTuple> oc) {
        return new ConfigTupleBuilder<>(pb, schema, def, dis, oc);
    }

    public BUILDER buildTuple() {
        onComplete.accept(
                new ConfigValTupleImpl(
                        schema, defaultValue, disabledValue, null,
                        genConstraints(), genConfigure()
                )
        );
        return parBuilder;
    }
}