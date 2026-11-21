/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

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
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<T>... configureValues
    ) {
        super(defaultValue, disabledValue,
                val -> val.hasConfig()
                        ? val.getConfig().getDictionary().getLocaleForType(defaultValue.getListComponentValType())
                        : defaultValue.getListComponentValType().getDisplayName(),
                constraints, configureValues
        );
        this.valCreator = valCreator;
    }

    @Override public IValType getListComponentType()           { return defaultValue.getListComponentValType(); }

    @Nullable protected Function<String, VAL> getValCreator()  { return valCreator; }

    @Override public T getList()                               { return getValue(); }
    @Override public void setList(T value)                     { setValue(value); }
    @Override public int size()                                { return getList().size(); }
    @Override public void forEachList(Consumer<LIST_VAL> func) { getList().forEach(func); }
    @Override public boolean isEmptyList()                     { return getList().isEmpty(); }
    @Override public boolean containsValue(LIST_VAL value)     { return getList().containsValue(value); }
    @Override public boolean containsOriginalValue(VAL value)  { return getList().containsOriginalValue(value); }

    @Override
    public void clearList() {
        writeLocked(() -> {
            T current = getList();
            if (!current.isEmpty()) setList(current.withCleared());
        });
    }

    @Override
    public boolean addListValue(LIST_VAL value) {
        return writeLockedResult(() -> {
            T current = getList();
            T updated = current.withAdded(adaptListValue(value));
            if (updated == current || updated.equals(current)) return false;
            setList(updated);
            return true;
        });
    }

    @Override
    public boolean removeListValue(LIST_VAL value) {
        return writeLockedResult(() -> {
            T current = getList();
            T updated = current.withRemoved(adaptListValue(value));
            if (updated == current || updated.equals(current)) return false;
            setList(updated);
            return true;
        });
    }

    protected LIST_VAL adaptListValue(LIST_VAL value) { return value; }

    @Override protected T adaptValue(T value)         { return super.adaptValue(value.map(this::adaptListValue)); }
    @Override protected boolean saveDisabledValue()   { return false; }
}