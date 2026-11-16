/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ****************************************************************************** */

package ru.mousecray.mousecore.api.customtype;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.type.ValTypeCategory;
import ru.mousecray.mousecore.api.error.ValueFormatException;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public abstract class MatrixType<SELF extends MatrixType<SELF>> extends TableType<SELF> {

    protected MatrixType(List<IValType> schema) {
        super(schema);
        for (int i = 0; i < schema.size(); i++) {
            IValType col = schema.get(i);
            if (col.getCategory() != ValTypeCategory.NUMBER) {
                throw new ValueFormatException("Matrix column #" + i + " must have NUMBER category, but got: " + col.getDisplayName());
            }
        }
    }

    public boolean isSquare()                 { return rowCount() == columnCount(); }
    public double getDouble(int row, int col) { return ((NumberType<?>) getCell(row, col)).asDouble(); }

    public double[][] as2DArray() {
        int        r    = rowCount();
        int        c    = columnCount();
        double[][] data = new double[r][c];
        for (int i = 0; i < r; i++) for (int j = 0; j < c; j++) data[i][j] = getDouble(i, j);
        return data;
    }

    public abstract SELF transpose();

    public double determinant() {
        if (!isSquare()) throw new UnsupportedOperationException("Determinant is only defined for square matrices (" + rowCount() + "x" + columnCount() + ")");
        int n = rowCount();
        if (n == 0) return 1.0D;
        if (n == 1) return getDouble(0, 0);
        if (n == 2) return getDouble(0, 0) * getDouble(1, 1) - getDouble(0, 1) * getDouble(1, 0);

        double[][] a   = as2DArray();
        double     det = 1.0D;

        for (int i = 0; i < n; i++) {
            int pivot = i;
            for (int j = i + 1; j < n; j++) if (Math.abs(a[j][i]) > Math.abs(a[pivot][i])) pivot = j;
            if (Math.abs(a[pivot][i]) < 1e-12) return 0.0D;

            if (pivot != i) {
                double[] temp = a[i];
                a[i] = a[pivot];
                a[pivot] = temp;
                det = -det;
            }

            det *= a[i][i];

            for (int j = i + 1; j < n; j++) {
                double factor = a[j][i] / a[i][i];
                for (int k = i; k < n; k++) a[j][k] -= factor * a[i][k];
            }
        }
        return det;
    }
}