/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.api.config.sect;

import ru.mousecray.mousecore.api.DisplayName;
import ru.mousecray.mousecore.api.config.MouseConfig;
import ru.mousecray.mousecore.api.container.ImmutableDisplayNameMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ConfigSectBase {
    @Nullable ConfigSectBase getChild(String name);
    ImmutableDisplayNameMap<? extends ConfigSectBase> getChildren();
    @Nullable ConfigSectBase getChildByDisplayName(String name);

    @Nonnull String getFullInternalName();

    void setDisabled(boolean disabled);

    boolean hasChildren();
    boolean canBeDisabled();
    boolean isDisabled();
    boolean isSupportChildren();
    <T extends ConfigSectBase> boolean isSupportChild(T child);
    boolean hasChild(String name);
    boolean hasChildByDisplayName(String name);
    boolean isLoaded();

    void reset();

    @Nonnull DisplayName getName();
    @Nullable ConfigSectBase getParent();
    @Nullable String getComment();
    int getDeep();

    @Nullable MouseConfig getConfig();
    boolean hasConfig();
}