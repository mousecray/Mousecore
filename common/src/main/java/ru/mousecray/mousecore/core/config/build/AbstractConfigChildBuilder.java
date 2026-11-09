/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.build;

import ru.mousecray.mousecore.api.DisplayName;
import ru.mousecray.mousecore.api.config.utils.ConfigDictionary;
import ru.mousecray.mousecore.core.config.par.ConfigParDisablerImpl;

import javax.annotation.Nullable;

public abstract class AbstractConfigChildBuilder {
    protected final     MouseConfigBuilder configBuilder;
    protected final     String             path;
    protected final     DisplayName        name;
    protected           boolean            canBeDisabled;
    protected @Nullable String             comment;

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

    protected ConfigDictionary getDictionary()  { return hasDictionary() ? configBuilder.dictionary : null; }
    protected boolean hasDictionary()           { return configBuilder != null && configBuilder.dictionary != null; }
    protected String getFullName()              { return path + name.getInternalName(); }
    void addDisabler(ConfigParDisablerImpl par) { }
}