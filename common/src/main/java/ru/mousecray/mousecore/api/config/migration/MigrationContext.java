/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.api.config.migration;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.function.Function;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public interface MigrationContext {
    int getFromVersion();
    int getToVersion();

    boolean has(String path);
    @Nullable String getRaw(String path);

    void rename(String oldPath, String newPath);
    void transform(String path, Function<String, String> transformer);
    void remove(String path);
    void putRaw(String path, String rawValue);

    Map<String, String> getRawData();
}