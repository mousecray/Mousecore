package ru.mousecray.mousecore.core.config.val.base.list;

import ru.mousecray.mousecore.api.VariableValue;
import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.base.list.ConfigValSimpleList;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.values.ConditionalValType;
import ru.mousecray.mousecore.api.customtype.values.SimpleListType;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.core.config.val.base.ConfigValListImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
@FieldsAreNonnullByDefault
public abstract class ConfigValSimpleListImpl<T extends CustomType<?>>
        extends ConfigValListImpl<ConditionalValType<T>, T, SimpleListType<T>> implements ConfigValSimpleList<T> {

    protected final boolean isConditional;

    @SafeVarargs
    public ConfigValSimpleListImpl(
            boolean isConditional,
            @Nullable Function<String, T> valCreator,
            SimpleListType<T> defaultValue, SimpleListType<T> disabledValue,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<SimpleListType<T>>... configureValues
    ) {
        super(defaultValue, disabledValue, valCreator, constraints, configureValues);
        this.isConditional = isConditional;
    }

    @SafeVarargs
    public ConfigValSimpleListImpl(
            @Nullable Function<String, T> valCreator,
            SimpleListType<T> defaultValue, SimpleListType<T> disabledValue,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<SimpleListType<T>>... configureValues
    ) {
        this(defaultValue.isConditional(), valCreator, defaultValue, disabledValue, constraints, configureValues);
    }

    @Override public boolean isConditional() { return isConditional; }

    @Override
    protected VariableValue<SimpleListType<T>> parseValue(@Nullable String value) {
        try {
            return VariableValue.create(
                    SimpleListType.fromString(
                            getListComponentType(),
                            isConditional,
                            value,
                            valCreator,
                            hasLogger() ? getLogger() : null
                    )
            );
        } catch (ValueFormatException ignore) { return VariableValue.create(); }
    }
}