package ru.mousecray.mousecore.core.config.build.val.list;

import ru.mousecray.mousecore.api.config.val.base.list.ConfigValSimpleList;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.customtype.values.DecimalType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.base.AbstractConfigSimpleListNumberBuilder;
import ru.mousecray.mousecore.core.config.val.list.ConfigValDecimalListImpl;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public final class ConfigDecimalListBuilder<BUILDER extends AbstractConfigChildBuilder>
        extends AbstractConfigSimpleListNumberBuilder<BUILDER, DecimalType, ConfigDecimalListBuilder<BUILDER>> {

    private ConfigDecimalListBuilder(BUILDER parBuilder, Consumer<ConfigValSimpleList<?>> onComplete) {
        super(parBuilder, onComplete);
    }

    public static <BUILDER extends AbstractConfigChildBuilder> ConfigDecimalListBuilder<BUILDER> create(
            BUILDER parBuilder, Consumer<ConfigValSimpleList<?>> onComplete
    ) {
        return new ConfigDecimalListBuilder<>(Objects.requireNonNull(parBuilder), onComplete);
    }

    @Override public IValType getComponentValType()                { return ValType.DECIMAL; }
    @Override public Function<String, DecimalType> getValCreator() { return str -> CustomType.parse(DecimalType.class, str); }

    public BUILDER buildDecimalList() {
        onComplete.accept(
                new ConfigValDecimalListImpl(
                        conditional,
                        resolveValue(rawDefaultEntries, "defaultValue"),
                        resolveValue(rawDisabledEntries, "disabledValue"),
                        genRangeContainer(), genConstraints(), genConfigure()
                )
        );
        return parBuilder;
    }
}