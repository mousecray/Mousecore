/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.build.par;

import ru.mousecray.mousecore.api.DisplayName;
import ru.mousecray.mousecore.api.config.utils.ConfigDictionary;
import ru.mousecray.mousecore.api.config.utils.ConfigSide;
import ru.mousecray.mousecore.api.config.utils.ConfigState;
import ru.mousecray.mousecore.core.config.build.MouseConfigBuilder;
import ru.mousecray.mousecore.core.config.par.ConfigParDisablerImpl;
import ru.mousecray.mousecore.core.config.sect.ConfigSectBaseImpl;

import javax.annotation.Nullable;
import java.util.EnumSet;

public abstract class AbstractConfigChildBuilder {
    public final        MouseConfigBuilder   configBuilder;
    public final        String               path;
    public final        DisplayName          name;
    protected           boolean              canBeDisabled;
    protected @Nullable String               comment;
    @Nullable protected ConfigSide           side;
    protected final     EnumSet<ConfigState> manualStates = EnumSet.noneOf(ConfigState.class);

    protected boolean threadSafe;

    protected AbstractConfigChildBuilder(MouseConfigBuilder configBuilder, String path, DisplayName name) {
        this.configBuilder = configBuilder;
        this.path = path;
        this.name = name;
    }

    protected AbstractConfigChildBuilder(MouseConfigBuilder configBuilder, DisplayName name) {
        this(configBuilder, null, name);
    }

    public AbstractConfigChildBuilder setComment(@Nullable String comment) {
        this.comment = comment;
        return this;
    }

    public AbstractConfigChildBuilder setCanBeDisabled() {
        canBeDisabled = true;
        return this;
    }

    public ConfigDictionary getDictionary()            { return hasDictionary() ? configBuilder.dictionary : null; }
    public boolean hasDictionary()                     { return configBuilder != null && configBuilder.dictionary != null; }
    protected String getFullName()                     { return path + name.getInternalName(); }
    public void addDisabler(ConfigParDisablerImpl par) { }

    public AbstractConfigChildBuilder setSide(@Nullable ConfigSide side) {
        this.side = side;
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public AbstractConfigChildBuilder markDeprecated() {
        manualStates.add(ConfigState.DEPRECATED);
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public AbstractConfigChildBuilder markUnused() {
        manualStates.add(ConfigState.UNUSED);
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public AbstractConfigChildBuilder markExperimental() {
        manualStates.add(ConfigState.EXPERIMENTAL);
        return this;
    }

    protected void applyMetadata(ConfigSectBaseImpl target) {
        if (side != null) target.setExplicitSide(side);
        if (!manualStates.isEmpty()) target.addManualStates(manualStates);
        if (threadSafe) target.setThreadSafe(true);
    }

    public AbstractConfigChildBuilder setThreadSafe() {
        threadSafe = true;
        return this;
    }
}