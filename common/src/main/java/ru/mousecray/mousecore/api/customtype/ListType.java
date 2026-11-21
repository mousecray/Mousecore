/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype;

import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.utils.MouseCollections;
import ru.mousecray.mousecore.api.utils.MouseUtils;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@FieldsAreNonnullByDefault
@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class ListType<VAL extends CustomType<?>, T extends CustomType<?>> extends CustomType<ListType<VAL, T>> {
    private static final MouseUtils.Equator<ListType<?, ?>> EQUATOR = MouseUtils.Equator
            .<ListType<?, ?>>equaling(ListType::getListComponentValType)
            .thenEqualing(ListType::getValCreator)
            .thenEqualing(ListType::getList);

    @Nullable private final Function<String, T> valCreator;
    private final           List<VAL>           list;
    private final           IValType            listComponentType;

    protected ListType(IValType listComponentType, @Nullable Function<String, T> valCreator) {
        this(listComponentType, valCreator, Collections.emptyList());
    }

    protected ListType(IValType listComponentType, @Nullable Function<String, T> valCreator, List<VAL> values) {
        this.valCreator = valCreator;
        this.listComponentType = Objects.requireNonNull(listComponentType);
        Objects.requireNonNull(values);
        if (values.isEmpty()) list = Collections.emptyList();
        else {
            List<VAL> copy = new ArrayList<>(values.size());
            for (VAL v : values) if (v != null) copy.add(v);
            list = Collections.unmodifiableList(copy);
        }
    }

    public IValType getListComponentValType()            { return listComponentType; }

    protected abstract <TYPE extends ListType<VAL, T>> TYPE createType(List<VAL> list);

    protected List<VAL> getList()                        { return list; }
    public List<VAL> asList()                            { return list; }
    @Nullable public Function<String, T> getValCreator() { return valCreator; }

    public int size()                                    { return list.size(); }
    public boolean isEmpty()                             { return list.isEmpty(); }
    public VAL getValue(int index)                       { return list.get(index); }

    public <TYPE extends ListType<VAL, T>> TYPE withAdded(VAL value) {
        Objects.requireNonNull(value);
        List<VAL> next = new ArrayList<>(list.size() + 1);
        next.addAll(list);
        next.add(value);
        return createType(next);
    }

    @SuppressWarnings("unchecked")
    public <TYPE extends ListType<VAL, T>> TYPE withRemoved(@Nullable VAL value) {
        if (value == null || !list.contains(value)) return (TYPE) this;
        List<VAL> next = new ArrayList<>(list);
        next.remove(value);
        return createType(next);
    }

    @SuppressWarnings("unchecked")
    public <TYPE extends ListType<VAL, T>> TYPE withCleared() {
        return list.isEmpty() ? (TYPE) this : createType(Collections.emptyList());
    }

    public boolean containsValue(@Nullable VAL value) { return value != null && list.contains(value); }

    public abstract boolean containsOriginalValue(@Nullable T value);

    public void forEach(Consumer<VAL> func)           { list.forEach(func); }

    @SuppressWarnings("unchecked")
    public <TYPE extends ListType<VAL, T>> TYPE map(Function<VAL, VAL> func) {
        if (list.isEmpty()) return (TYPE) this;
        boolean   changed = false;
        List<VAL> next    = new ArrayList<>(list.size());
        for (VAL elem : list) {
            VAL mapped = func.apply(elem);
            if (mapped != null) {
                if (mapped != elem && !Objects.equals(mapped, elem)) changed = true;
                next.add(mapped);
            } else changed = true;
        }
        return changed ? createType(next) : (TYPE) this;
    }

    @Override
    public String toString() {
        return list.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") @Override
    public boolean equals(Object obj) { return EQUATOR.test(this, obj); }

    @Override public int hashCode() { return Objects.hash(listComponentType, list, valCreator); }

    @SuppressWarnings({ "rawtypes" })
    @Override
    public int compareTo(ListType<VAL, T> o) {
        if (o == this) return 0;

        int result = MouseCollections.compare(list, o.list);

        if (result == 0) {
            if (valCreator == o.valCreator) return 0;
            else if (valCreator == null) return -1;
            else if (o.valCreator == null) return 1;
            else return Integer.compare(valCreator.hashCode(), o.valCreator.hashCode());
        }

        return result;
    }
}