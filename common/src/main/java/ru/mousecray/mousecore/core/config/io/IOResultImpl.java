/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.io;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.io.ConfigIOResultType;
import ru.mousecray.mousecore.api.config.io.IOResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public class IOResultImpl implements IOResult {
    @Nonnull private final  ConfigIOResultType type;
    @Nullable private final Exception          error;

    private IOResultImpl(ConfigIOResultType type, @Nullable Exception error) {
        this.type = type;
        this.error = error;
    }

    public static IOResultImpl createSuccess() {
        return new IOResultImpl(ConfigIOResultType.SUCCESS, null);
    }

    public static IOResultImpl createNotFound() {
        return new IOResultImpl(ConfigIOResultType.NOT_FOUND, null);
    }

    public static IOResultImpl createSkipped() {
        return new IOResultImpl(ConfigIOResultType.SKIPPED, null);
    }

    public static IOResultImpl createError(Exception error) {
        return new IOResultImpl(ConfigIOResultType.ERROR, Objects.requireNonNull(error));
    }

    @Override public ConfigIOResultType getType()   { return type; }
    @Nullable @Override public Exception getError() { return error; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IOResultImpl that)) return false;
        return type == that.type && Objects.equals(error, that.error);
    }

    @Override public int hashCode()    { return Objects.hash(type, error); }
    @Override public String toString() { return "IOResultImpl{type=" + type + ", error=" + error + '}'; }

}
