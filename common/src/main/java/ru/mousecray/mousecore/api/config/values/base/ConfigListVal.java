/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.config.values.base;

import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.ConfigVal;
import ru.mousecray.mousecore.api.config.IValType;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.ListType;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
@FieldsAreNonnullByDefault
public abstract class ConfigListVal<
        LIST_VAL extends CustomType<?>,
        VAL extends CustomType<?>,
        T extends ListType<LIST_VAL, VAL>
        > extends ConfigVal<T> {
    protected final           IValType              listComponentType;
    protected final           Class<VAL>            listComponentClass;
    @Nullable protected final Function<String, VAL> valCreator;

    @SafeVarargs
    public ConfigListVal(
            IValType type, T defaultValue, T disabledValue, IValType listComponentType,
            Class<VAL> listComponentClass, @Nullable Function<String, VAL> valCreator,
            @Nullable Constraint<?>[] constraints, @Nullable PredefinedValue<T>... configureValues
    ) {
        super(type, defaultValue, disabledValue,
                val -> val.hasConfig()
                        ? val.getConfig().getDictionary().getLocaleForType(listComponentType)
                        : listComponentType.getDisplayName(),
                constraints, configureValues
        );
        this.listComponentType = Objects.requireNonNull(listComponentType);
        this.listComponentClass = Objects.requireNonNull(listComponentClass);
        this.valCreator = valCreator;
    }

    public IValType getListComponentType()                    { return listComponentType; }
    protected Class<VAL> getListComponentClass()              { return listComponentClass; }
    @Nullable protected Function<String, VAL> getValCreator() { return valCreator; }

    protected T getList()                                     { return getValue(); }
    public void forEachList(Consumer<LIST_VAL> func)          { getList().forEach(func); }
    public void clearList()                                   { getList().clear(); }
    public boolean isEmptyList()                              { return getList().isEmpty(); }
    public boolean removeListValue(LIST_VAL value)            { return getList().removeValue(adaptListValue(value)); }
    public boolean addListValue(LIST_VAL value)               { return getList().addValue(adaptListValue(value)); }
    public boolean containsValue(LIST_VAL value)              { return getList().containsValue(value); }
    public boolean containsOriginalValue(VAL value)           { return getList().containsOriginalValue(value); }

    protected void setList(T value)                           { setValue(value); }

    protected LIST_VAL adaptListValue(LIST_VAL value)         { return value; }

    @Override protected T adaptValue(T value)                 { return super.adaptValue(value.map(this::adaptListValue)); }
    @Override protected boolean saveDisabledValue()           { return false; }
}
