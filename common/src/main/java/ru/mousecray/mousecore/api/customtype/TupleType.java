/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.utils.MouseUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public abstract class TupleType<SELF extends TupleType<SELF>> extends CustomType<SELF> {
    private static final MouseUtils.Equator<TupleType<?>> EQUATOR = MouseUtils.Equator
            .<TupleType<?>>equaling(TupleType::getSchema)
            .thenEqualing(TupleType::asList);

    protected final List<IValType> schema;

    protected TupleType(List<IValType> schema) {
        super();
        this.schema = Collections.unmodifiableList(Objects.requireNonNull(schema, "Schema cannot be null"));
    }

    public List<IValType> getSchema() { return schema; }
    public int size()                 { return schema.size(); }
    public boolean isEmpty()          { return schema.isEmpty(); }

    public abstract CustomType<?> get(int index);

    @SuppressWarnings("unchecked")
    public <T extends CustomType<?>> T get(int index, Class<T> clazz) {
        CustomType<?> elem = get(index);
        if (clazz.isInstance(elem)) return (T) elem;
        return elem.getCastPipeline().asValue(clazz);
    }

    public CustomType<?> first()  { return get(0); }
    public CustomType<?> second() { return get(1); }
    public CustomType<?> third()  { return get(2); }

    public abstract List<CustomType<?>> asList();
    public abstract CustomType<?>[] asArray();

    public List<IValType> asSchema() {
        return schema;
    }

    public abstract SELF createType(List<CustomType<?>> elements);

    @Override
    public String toString() {
        return asList().stream()
                .map(Objects::toString)
                .collect(Collectors.joining("; ", "(", ")"));
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override public boolean equals(Object o) { return EQUATOR.test(this, o); }
    @Override public int hashCode() { return Objects.hash(schema, asList()); }

    @Override
    @SuppressWarnings({ "rawtypes" })
    public int compareTo(SELF o) {
        return Comparator
                .<TupleType<?>>comparingInt(TupleType::size)
                .thenComparing((t1, t2) -> {
                    for (int i = 0; i < t1.size(); i++) {
                        CustomType<?> e1 = t1.get(i);
                        CustomType<?> e2 = t2.get(i);
                        if (e1 == e2) continue;

                        try {
                            int cmp = ((Comparable) e1).compareTo(e2);
                            if (cmp != 0) return cmp;
                        } catch (ClassCastException ignore) {
                            int cmp = e1.getClass().getName().compareTo(e2.getClass().getName());
                            if (cmp != 0) return cmp;
                        }
                    }
                    return 0;
                })
                .compare(this, o);
    }
}