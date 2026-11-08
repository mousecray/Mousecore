/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype;

import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.utils.MouseCollections;
import ru.mousecray.mousecore.api.utils.MouseUtils;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@FieldsAreNonnullByDefault
@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class ListType<VAL extends CustomType<?>, T extends CustomType<?>> extends CustomType<ListType<VAL, T>> {
    private final           Class<T>            valClass;
    @Nullable private final Function<String, T> valCreator;
    private final           List<VAL>           list = new ArrayList<>();

    protected ListType(Class<T> valClass, @Nullable Function<String, T> valCreator) {
        super(CustomValType.LIST);
        this.valClass = valClass;
        this.valCreator = valCreator;
    }

    protected ListType(Class<T> valClass, @Nullable Function<String, T> valCreator, List<VAL> values) {
        this(Objects.requireNonNull(valClass), valCreator);
        list.addAll(Objects.requireNonNull(values));
    }

    protected abstract <TYPE extends ListType<VAL, T>> TYPE createType(List<VAL> list);

    protected List<VAL> getList()                        { return list; }
    public Class<T> getValClass()                        { return valClass; }
    @Nullable public Function<String, T> getValCreator() { return valCreator; }

    public boolean addValue(VAL value)                   { return list.add(value); }
    public boolean removeValue(VAL value)                { return list.remove(value); }
    public void clear()                                  { list.clear(); }
    public boolean isEmpty()                             { return list.isEmpty(); }
    public VAL getValue(int index)                       { return list.get(index); }

    public boolean containsValue(@Nullable VAL value)    { return list.contains(value); }

    public abstract boolean containsOriginalValue(@Nullable T value);

    public void forEach(Consumer<VAL> func)              { list.forEach(func); }

    public <TYPE extends ListType<VAL, T>> TYPE map(Function<VAL, VAL> func) {
        return createType(MouseCollections.map(func, false, list));
    }

    @Override
    public String toString() {
        return list.stream()
                .map(v -> v.toString())
                .collect(Collectors.joining(", "));
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") @Override
    public boolean equals(Object obj) {
        return MouseUtils.Equator
                .<ListType<VAL, T>, Class<T>>equaling(ListType::getValClass)
                .thenEqualing(ListType::getValCreator)
                .thenEqualing(ListType::getList)
                .equals(getClass(), this, obj);
    }

    @Override public int hashCode() { return Objects.hash(valClass, list, valCreator); }

    @Override
    public int compareTo(ListType<VAL, T> o) {
        if (o == this) return 0;

        int result = Integer.compare(list.size(), o.list.size());

        if (result == 0) {
            List<Comparable> l1 = new ArrayList<>(list);
            Collections.sort(l1);
            List<Comparable> l2 = new ArrayList<>(o.list);
            Collections.sort(l2);

            ListIterator<Comparable> e1 = l1.listIterator();
            ListIterator<Comparable> e2 = l2.listIterator();
            while (e1.hasNext() && e2.hasNext()) {
                Comparable o1 = e1.next(), o2 = e2.next();
                if (o1 != null && o2 != null) {
                    result = o1.compareTo(o2);
                    if (result != 0) return result;
                }
            }
        }

        if (result == 0) {
            if (valCreator == o.valCreator) return 0;
            else if (valCreator == null) return -1;
            else if (o.valCreator == null) return 1;
            else return Integer.compare(valCreator.hashCode(), o.valCreator.hashCode());
        }

        return result;
    }
}