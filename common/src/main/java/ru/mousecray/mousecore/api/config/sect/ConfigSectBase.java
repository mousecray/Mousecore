/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.api.config.sect;

import ru.mousecray.mousecore.api.DisplayName;
import ru.mousecray.mousecore.api.config.MouseConfig;
import ru.mousecray.mousecore.api.config.utils.ConfigLang;
import ru.mousecray.mousecore.api.config.utils.ConfigSide;
import ru.mousecray.mousecore.api.config.utils.ConfigState;
import ru.mousecray.mousecore.api.container.ImmutableDisplayNameMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;

public interface ConfigSectBase {
    @Nullable ConfigSectBase getChild(String name);
    ImmutableDisplayNameMap<? extends ConfigSectBase> getChildren();
    @Nullable ConfigSectBase getChildByDisplayName(String name);

    @Nonnull String getFullInternalName();

    void setDisabled(boolean disabled);

    @Nonnull ConfigSide getSide();
    @Nonnull EnumSet<ConfigState> getStates();
    boolean hasState(ConfigState state);
    boolean isFrozen();
    boolean isNew();

    boolean isThreadSafe();

    boolean hasChildren();
    boolean canBeDisabled();
    boolean isDisabled();
    boolean isSupportChildren();
    boolean hasChild(String name);
    boolean hasChildByDisplayName(String name);
    boolean matchesName(String inputName);
    boolean isLoaded();

    void reset();

    @Nonnull DisplayName getName();
    @Nonnull DisplayName getName(ConfigLang lang);
    @Nullable ConfigSectBase getParent();
    @Nullable String getComment();
    @Nullable String getComment(ConfigLang lang);
    int getDeep();

    @Nullable MouseConfig getConfig();
    boolean hasConfig();
}