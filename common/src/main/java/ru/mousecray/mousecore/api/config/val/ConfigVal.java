/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.api.config.val;

import ru.mousecray.mousecore.api.config.MouseConfig;
import ru.mousecray.mousecore.api.config.par.ConfigParBase;
import ru.mousecray.mousecore.api.config.utils.ConfigSide;
import ru.mousecray.mousecore.api.config.utils.ConfigState;
import ru.mousecray.mousecore.api.config.utils.ConfigThreadPolicy;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.type.IValType;

import javax.annotation.Nullable;
import java.util.EnumSet;

public interface ConfigVal<V extends CustomType<?>> {
    IValType getType();

    V getValue();
    V getDisabledValue();
    V getDefaultValue();

    @Nullable ConfigParBase<?> getOwner();
    /**
     * Current predefined value if it specified in config
     */
    boolean canBeConfigured();
    /**
     * Relations with other ConfigSectBase's
     */
    boolean canBeConstrained();
    boolean canBeDisabled();

    boolean isDisabled();
    boolean isLoaded();

    void reset();

    boolean hasConfig();
    MouseConfig getConfig();

    ConfigSide getSide();
    EnumSet<ConfigState> getEffectiveStates();
    boolean isFrozen();
    boolean isNew();

    boolean isThreadSafe();

    void addListener(ConfigThreadPolicy policy, ConfigValueListener<V> listener);
    default void addListener(ConfigValueListener<V> listener)       { addListener(ConfigThreadPolicy.AUTO, listener); }
    default void addClientListener(ConfigValueListener<V> listener) { addListener(ConfigThreadPolicy.CLIENT_THREAD, listener); }
    default void addServerListener(ConfigValueListener<V> listener) { addListener(ConfigThreadPolicy.SERVER_THREAD, listener); }
    default void addAsyncListener(ConfigValueListener<V> listener)  { addListener(ConfigThreadPolicy.IMMEDIATE, listener); }
    boolean removeListener(ConfigValueListener<V> listener);
    void clearListeners();
}