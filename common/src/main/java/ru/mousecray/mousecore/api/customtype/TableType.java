/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.utils.MouseUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public abstract class TableType<SELF extends TableType<SELF>> extends CustomType<SELF> {
    private static final MouseUtils.Equator<TableType<?>> EQUATOR = MouseUtils.Equator
            .<TableType<?>>equaling(TableType::getSchema)
            .thenEqualing(TableType::asRows);

    protected final List<IValType> schema;

    protected TableType(List<IValType> schema) {
        super();
        this.schema = Collections.unmodifiableList(Objects.requireNonNull(schema, "Schema cannot be null"));
    }

    public List<IValType> getSchema()                                                      { return schema; }
    public int columnCount()                                                               { return schema.size(); }
    public abstract int rowCount();
    public boolean isEmpty()                                                               { return rowCount() == 0; }
    public abstract TupleType<?> getRow(int rowIndex);
    public CustomType<?> getCell(int rowIndex, int colIndex)                               { return getRow(rowIndex).get(colIndex); }
    public <T extends CustomType<?>> T getCell(int rowIndex, int colIndex, Class<T> clazz) { return getRow(rowIndex).get(colIndex, clazz); }

    public abstract List<? extends TupleType<?>> asRows();

    public List<CustomType<?>> asColumn(int colIndex) {
        if (colIndex < 0 || colIndex >= columnCount()) {
            throw new IndexOutOfBoundsException("Column index " + colIndex + " out of bounds for schema size " + columnCount());
        }
        List<CustomType<?>> col = new ArrayList<>(rowCount());
        for (int i = 0; i < rowCount(); i++) {
            col.add(getCell(i, colIndex));
        }
        return Collections.unmodifiableList(col);
    }

    public abstract SELF createType(List<? extends TupleType<?>> rows);

    @Override
    public String toString() {
        return asRows().stream()
                .map(Objects::toString)
                .collect(Collectors.joining(", ", "[", "]"));
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override public boolean equals(Object o) { return EQUATOR.test(this, o); }
    @Override public int hashCode() { return Objects.hash(schema, asRows()); }

    @Override
    @SuppressWarnings({ "rawtypes" })
    public int compareTo(SELF o) {
        return Comparator
                .<TableType<?>>comparingInt(TableType::rowCount)
                .thenComparingInt(TableType::columnCount)
                .thenComparing((t1, t2) -> {
                    for (int i = 0; i < t1.rowCount(); i++) {
                        TupleType<?> r1 = t1.getRow(i);
                        TupleType<?> r2 = t2.getRow(i);
                        if (r1 == r2) continue;

                        int cmp = ((Comparable) r1).compareTo(r2);
                        if (cmp != 0) return cmp;
                    }
                    return 0;
                })
                .compare(this, o);
    }
}