/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ***************************************************************************** */

package ru.mousecray.mousecore.api.customtype.values.collection;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.MatrixType;
import ru.mousecray.mousecore.api.customtype.TupleType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.customtype.values.number.DecimalType;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.api.log.MouseLogger;
import ru.mousecray.mousecore.api.utils.MouseNumbers;
import ru.mousecray.mousecore.api.utils.MouseStrings;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public final class SimpleMatrixType extends MatrixType<SimpleMatrixType> {
    private final List<SimpleTupleType> rows;

    private SimpleMatrixType(List<IValType> schema, List<SimpleTupleType> rows) {
        super(schema);
        Objects.requireNonNull(rows, "Rows list cannot be null");
        for (int i = 0; i < rows.size(); i++) {
            SimpleTupleType row = Objects.requireNonNull(rows.get(i), "Matrix row at index " + i + " is null");
            if (!row.getSchema().equals(schema)) {
                throw new ValueFormatException("Matrix row #" + i + " schema mismatch: expected " + schema + ", got " + row.getSchema());
            }
        }
        this.rows = List.copyOf(rows);
    }

    public static SimpleMatrixType create(List<IValType> schema, List<SimpleTupleType> rows) { return new SimpleMatrixType(schema, rows); }
    public static SimpleMatrixType create(List<IValType> schema, SimpleTupleType... rows)    { return new SimpleMatrixType(schema, Arrays.asList(rows)); }

    public static SimpleMatrixType create(double[][] data) {
        Objects.requireNonNull(data, "Matrix data array cannot be null");
        if (data.length == 0 || data[0].length == 0) {
            throw new ValueFormatException("Cannot create an empty matrix from a 2D array");
        }
        int            r      = data.length;
        int            c      = data[0].length;
        List<IValType> schema = Collections.nCopies(c, ValType.DECIMAL);

        List<SimpleTupleType> rows = new ArrayList<>(r);
        for (int i = 0; i < r; i++) {
            if (data[i].length != c) {
                throw new ValueFormatException("Matrix row #" + i + " length (" + data[i].length + ") does not match column count (" + c + ")");
            }
            CustomType<?>[] cells = new CustomType<?>[c];
            for (int j = 0; j < c; j++) cells[j] = DecimalType.create(data[i][j]);
            rows.add(SimpleTupleType.create(schema, cells));
        }
        return new SimpleMatrixType(schema, rows);
    }

    public static SimpleMatrixType createIdentity(int size) {
        if (size <= 0) throw new ValueFormatException("Matrix size must be greater than 0");
        double[][] data = new double[size][size];
        for (int i = 0; i < size; i++) data[i][i] = 1.0D;
        return create(data);
    }

    public static SimpleMatrixType createZero(int rows, int cols) {
        if (rows <= 0 || cols <= 0) throw new ValueFormatException("Matrix dimensions must be greater than 0");
        return create(new double[rows][cols]);
    }

    @Override
    public SimpleMatrixType createType(List<? extends TupleType<?>> newRows) {
        List<SimpleTupleType> typed = new ArrayList<>(newRows.size());
        for (TupleType<?> r : newRows) {
            if (r instanceof SimpleTupleType st) typed.add(st);
            else typed.add(SimpleTupleType.create(schema, r.asList()));
        }
        return new SimpleMatrixType(schema, typed);
    }

    @Override
    public SimpleMatrixType transpose() {
        int        r          = rowCount();
        int        c          = columnCount();
        double[][] transposed = new double[c][r];
        for (int i = 0; i < r; i++) for (int j = 0; j < c; j++) transposed[j][i] = getDouble(i, j);
        return create(transposed);
    }

    public static SimpleMatrixType fromString(
            List<IValType> schema,
            @Nullable String raw,
            @Nullable MouseLogger logger
    ) {
        SimpleTableType table = SimpleTableType.fromString(schema, raw, logger);
        return create(schema, table.asRows());
    }

    public static SimpleMatrixType fromString(@Nullable String raw, @Nullable MouseLogger logger) {
        String clean = MouseStrings.trimWith(raw, true, '\t');
        if (clean == null || clean.isEmpty()) throw new ValueFormatException("Cannot parse empty matrix string");

        if (clean.startsWith("[") && clean.endsWith("]")) clean = clean.substring(1, clean.length() - 1);

        String[]       rowLines     = clean.contains("\n") ? clean.split("\n") : clean.split(";");
        List<double[]> dataList     = new ArrayList<>();
        int            expectedCols = -1;

        for (String line : rowLines) {
            String trimmed = MouseStrings.trimWith(line, true, '\t');
            if (trimmed == null || trimmed.isEmpty()) continue;
            if (trimmed.startsWith("(") && trimmed.endsWith(")")) trimmed = trimmed.substring(1, trimmed.length() - 1);

            String[] cellParts = trimmed.split(",");
            if (expectedCols == -1) expectedCols = cellParts.length;
            else if (cellParts.length != expectedCols) {
                throw new ValueFormatException("Matrix row has " + cellParts.length + " columns, expected " + expectedCols);
            }

            double[] rowData = new double[expectedCols];
            for (int j = 0; j < expectedCols; j++) {
                Double val = MouseNumbers.tryParseDouble(MouseStrings.trimWith(cellParts[j], true, '\t'));
                if (val == null) throw new ValueFormatException("Cannot parse matrix cell '" + cellParts[j] + "' as double");
                rowData[j] = val;
            }
            dataList.add(rowData);
        }

        if (dataList.isEmpty()) throw new ValueFormatException("No matrix rows found in: '" + raw + "'");
        return create(dataList.toArray(new double[0][]));
    }

    public static SimpleMatrixType fromString(@Nullable String raw) {
        return fromString(raw, null);
    }

    @Override public int rowCount()                    { return rows.size(); }
    @Override public TupleType<?> getRow(int rowIndex) { return rows.get(rowIndex); }
    @Override public List<SimpleTupleType> asRows()    { return rows; }
    @Override public IValType getValType()             { return ValType.SIMPLE_MATRIX; }
}