/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.api.config;

import ru.mousecray.mousecore.api.config.val.ConfigValueChange;

import java.util.List;

@FunctionalInterface
public interface ConfigChangeListener {
    void onConfigChanged(MouseConfig config, List<ConfigValueChange<?>> changes);
}