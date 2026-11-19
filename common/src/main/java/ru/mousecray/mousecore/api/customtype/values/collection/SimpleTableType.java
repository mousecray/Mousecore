/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype.values.collection;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.TableType;
import ru.mousecray.mousecore.api.customtype.TupleType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.api.log.MouseLogger;
import ru.mousecray.mousecore.api.utils.MouseStrings;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public final class SimpleTableType extends TableType<SimpleTableType> {
    private final List<SimpleTupleType> rows;

    private SimpleTableType(List<IValType> schema, List<SimpleTupleType> rows) {
        super(schema);
        Objects.requireNonNull(rows, "Rows list cannot be null");
        for (int i = 0; i < rows.size(); i++) {
            SimpleTupleType row = Objects.requireNonNull(rows.get(i), "Table row at index " + i + " is null");
            if (!row.getSchema().equals(schema)) {
                throw new ValueFormatException("Table row #" + i + " schema mismatch: expected " + schema + ", got " + row.getSchema());
            }
        }
        this.rows = List.copyOf(rows);
    }

    public static SimpleTableType createEmpty(List<IValType> schema) {
        return new SimpleTableType(schema, Collections.emptyList());
    }

    public static SimpleTableType createEmpty(IValType... schema) {
        return new SimpleTableType(Arrays.asList(schema), Collections.emptyList());
    }

    public static SimpleTableType create(List<IValType> schema, List<SimpleTupleType> rows) {
        return new SimpleTableType(schema, rows);
    }

    public static SimpleTableType create(List<IValType> schema, SimpleTupleType... rows) {
        return new SimpleTableType(schema, Arrays.asList(rows));
    }

    public static SimpleTableType create(SimpleTupleType... rows) {
        Objects.requireNonNull(rows, "Rows array cannot be null");
        if (rows.length == 0) {
            throw new ValueFormatException("Cannot infer table schema from an empty rows array");
        }
        List<IValType> inferredSchema = rows[0].getSchema();
        return new SimpleTableType(inferredSchema, Arrays.asList(rows));
    }

    @Override
    public SimpleTableType createType(List<? extends TupleType<?>> newRows) {
        List<SimpleTupleType> typedRows = new ArrayList<>(newRows.size());
        for (TupleType<?> row : newRows) {
            if (row instanceof SimpleTupleType simpleTuple) typedRows.add(simpleTuple);
            else typedRows.add(SimpleTupleType.create(schema, row.asList()));
        }
        return new SimpleTableType(schema, typedRows);
    }

    public static SimpleTableType fromString(
            List<IValType> schema,
            @Nullable String raw,
            @Nullable MouseLogger logger
    ) {
        Objects.requireNonNull(schema, "Schema cannot be null");
        String clean = MouseStrings.trimWith(raw, true, '\t');
        if (clean == null || clean.isEmpty()) throw new ValueFormatException("Cannot parse empty string as Table");

        if (MouseStrings.isWrappedBy(clean, '[', ']')) clean = MouseStrings.trimWith(clean.substring(1, clean.length() - 1), true, '\t');
        if (clean == null || clean.isEmpty()) return createEmpty(schema);

        List<String> rowStrings;
        if (MouseStrings.indexOfRespectingBrackets(clean, "\n") != -1) rowStrings = MouseStrings.splitRespectingBrackets(clean, '\n');
        else if (clean.startsWith("(") && MouseStrings.indexOfRespectingBrackets(clean, ",") != -1) {
            rowStrings = MouseStrings.splitRespectingBrackets(clean, ',');
        } else if (MouseStrings.indexOfRespectingBrackets(clean, ";") != -1) rowStrings = MouseStrings.splitRespectingBrackets(clean, ';');
        else rowStrings = Collections.singletonList(clean);

        List<SimpleTupleType> parsedRows = new ArrayList<>(rowStrings.size());
        for (String rowStr : rowStrings) {
            String line = MouseStrings.trimWith(rowStr, true, '\t');
            if (line == null || line.isEmpty()) continue;
            if (line.endsWith(",")) line = MouseStrings.trimWith(line.substring(0, line.length() - 1), true, '\t');
            if (line == null || line.isEmpty()) continue;

            parsedRows.add(SimpleTupleType.fromString(schema, line, logger));
        }

        return create(schema, parsedRows);
    }

    public static SimpleTableType fromString(List<IValType> schema, @Nullable String raw) { return fromString(schema, raw, null); }
    public static SimpleTableType fromString(IValType[] schema, @Nullable String raw)     { return fromString(Arrays.asList(schema), raw, null); }

    @Override public int rowCount()                                                       { return rows.size(); }
    @Override public TupleType<?> getRow(int rowIndex)                                    { return rows.get(rowIndex); }
    @Override public List<SimpleTupleType> asRows()                                       { return rows; }
    @Override public IValType getValType()                                                { return ValType.SIMPLE_TABLE; }
}