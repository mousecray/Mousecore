/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.core.config.build.val.container;

import ru.mousecray.mousecore.api.config.val.container.ConfigValTable;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.values.collection.SimpleTableType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.base.AbstractConfigValueBuilder;
import ru.mousecray.mousecore.core.config.val.container.ConfigValTableImpl;

import java.util.List;
import java.util.function.Consumer;

public final class ConfigTableBuilder<BUILDER extends AbstractConfigChildBuilder> extends AbstractConfigValueBuilder<BUILDER, ConfigValTable, SimpleTableType, ConfigTableBuilder<BUILDER>> {
    private final List<IValType> schema;

    private ConfigTableBuilder(BUILDER pb, List<IValType> schema, Consumer<ConfigValTable> oc) {
        super(pb, SimpleTableType.createEmpty(schema), SimpleTableType.createEmpty(schema), oc);
        this.schema = schema;
    }

    public static <BUILDER extends AbstractConfigChildBuilder> ConfigTableBuilder<BUILDER> create(BUILDER pb, List<IValType> schema, Consumer<ConfigValTable> oc) {
        return new ConfigTableBuilder<>(pb, schema, oc);
    }

    public BUILDER buildTable() {
        onComplete.accept(
                new ConfigValTableImpl(
                        schema, defaultValue, disabledValue, null,
                        genConstraints(), genConfigure()
                )
        );
        return parBuilder;
    }
}