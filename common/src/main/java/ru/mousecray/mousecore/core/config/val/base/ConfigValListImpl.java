/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.core.config.val.base;

import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.base.ConfigListVal;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.ListType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.core.config.val.ConfigValImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
@FieldsAreNonnullByDefault
public abstract class ConfigValListImpl<
        LIST_VAL extends CustomType<?>,
        VAL extends CustomType<?>,
        T extends ListType<LIST_VAL, VAL>
        > extends ConfigValImpl<T> implements ConfigListVal<LIST_VAL, VAL, T> {
    @Nullable protected final Function<String, VAL> valCreator;

    @SafeVarargs
    public ConfigValListImpl(
            T defaultValue, T disabledValue,
            @Nullable Function<String, VAL> valCreator,
            @Nullable Constraint<? extends CustomType<?>>[] constraints, @Nullable PredefinedValue<T>... configureValues
    ) {
        super(defaultValue, disabledValue,
                val -> val.hasConfig()
                        ? val.getConfig().getDictionary().getLocaleForType(defaultValue.getListComponentValType())
                        : defaultValue.getValType().getDisplayName(),
                constraints, configureValues
        );
        this.valCreator = valCreator;
    }

    @Override public IValType getListComponentType()           { return defaultValue.getValType(); }

    @Nullable protected Function<String, VAL> getValCreator()  { return valCreator; }

    public T getList()                                         { return getValue(); }
    @Override public void forEachList(Consumer<LIST_VAL> func) { getList().forEach(func); }
    @Override public void clearList()                          { getList().clear(); }
    @Override public boolean isEmptyList()                     { return getList().isEmpty(); }
    @Override public boolean removeListValue(LIST_VAL value)   { return getList().removeValue(adaptListValue(value)); }
    @Override public boolean addListValue(LIST_VAL value)      { return getList().addValue(adaptListValue(value)); }
    @Override public boolean containsValue(LIST_VAL value)     { return getList().containsValue(value); }
    @Override public boolean containsOriginalValue(VAL value)  { return getList().containsOriginalValue(value); }

    protected void setList(T value)                            { setValue(value); }

    protected LIST_VAL adaptListValue(LIST_VAL value)          { return value; }

    @Override protected T adaptValue(T value)                  { return super.adaptValue(value.map(this::adaptListValue)); }
    @Override protected boolean saveDisabledValue()            { return false; }
}
