/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.build;

import ru.mousecray.mousecore.api.DisplayName;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.sect.ConfigSect;
import ru.mousecray.mousecore.api.log.ConsoleColor;
import ru.mousecray.mousecore.core.config.par.ConfigParDisablerImpl;
import ru.mousecray.mousecore.core.config.sect.ConfigSectImpl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigSectionBuilder extends AbstractConfigChildBuilder {
    private ConfigParDisablerImpl disablePar;

    private ConfigSectionBuilder(MouseConfigBuilder configBuilder, @Nullable String path, DisplayName name) {
        super(configBuilder, path, name);
    }

    @Nullable String getPath() { return path; }

    static ConfigSectionBuilder create(MouseConfigBuilder configBuilder, DisplayName name, @Nullable String path) {
        return new ConfigSectionBuilder(
                Objects.requireNonNull(configBuilder), path == null ? "" : path,
                Objects.requireNonNull(name)
        );
    }

    @Override
    public ConfigSectionBuilder setComment(@Nullable String comment) {
        this.comment = comment;
        return this;
    }

    public ConfigDisablerBuilder<ConfigSectionBuilder> createDisabler(@Nonnull String displayName) {
        return ConfigDisablerBuilder.create(this, Objects.requireNonNull(displayName), false);
    }

    public ConfigDisablerBuilder<ConfigSectionBuilder> createDisabler() {
        return ConfigDisablerBuilder.create(this, "Disabler", true);
    }

    @Override
    void addDisabler(ConfigParDisablerImpl disabler) {
        if (disablePar != null && configBuilder.logger != null) {
            configBuilder.logger.atWarn()
                    .withPrefix(LogPrefixes.CONFIG)
                    .withStyle(ConsoleColor.YELLOW_BG)
                    .log("Disabler '{0}' in ConfigSection '{1}.{2}' was overwritten by Disabler '{3}'",
                            disablePar.getName().getInternalName(), path, name.getInternalName(), disabler.getName().getInternalName());
        }
        disablePar = disabler;
    }

    public MouseConfigBuilder buildSection() {
        ConfigSect section = new ConfigSectImpl(name, disablePar, comment);
        configBuilder.putSection(path, section);
        return configBuilder;
    }
}