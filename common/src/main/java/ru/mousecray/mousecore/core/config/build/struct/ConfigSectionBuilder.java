package ru.mousecray.mousecore.core.config.build.struct;

import ru.mousecray.mousecore.api.DisplayName;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.ConfigSide;
import ru.mousecray.mousecore.api.log.ConsoleColor;
import ru.mousecray.mousecore.api.log.LogPrefixes;
import ru.mousecray.mousecore.core.config.build.MouseConfigBuilder;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.par.ConfigParDisablerImpl;
import ru.mousecray.mousecore.core.config.sect.ConfigSectImpl;

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

    public static ConfigSectionBuilder create(MouseConfigBuilder configBuilder, DisplayName name, @Nullable String path) {
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

    @Override
    public ConfigSectionBuilder setCanBeDisabled() {
        canBeDisabled = true;
        return this;
    }

    public ConfigDisablerBuilder<ConfigSectionBuilder> createDisabler(String displayName) {
        return ConfigDisablerBuilder.create(this, Objects.requireNonNull(displayName), false);
    }

    public ConfigDisablerBuilder<ConfigSectionBuilder> createDisabler() {
        return ConfigDisablerBuilder.create(this, "Disabler", true);
    }

    @Override
    public void addDisabler(ConfigParDisablerImpl disabler) {
        if (disablePar != null && configBuilder.logger != null) {
            configBuilder.logger.atWarn()
                    .withPrefix(LogPrefixes.CONFIG)
                    .withStyle(ConsoleColor.YELLOW_BG)
                    .log("Disabler '{0}' in ConfigSection '{1}.{2}' was overwritten by Disabler '{3}'",
                            disablePar.getName().getInternalName(), path, name.getInternalName(), disabler.getName().getInternalName());
        }
        disablePar = disabler;
        canBeDisabled = true;
    }

    @Override
    public ConfigSectionBuilder setSide(@Nullable ConfigSide side) {
        super.setSide(side);
        return this;
    }

    @Override
    public ConfigSectionBuilder markDeprecated() {
        super.markDeprecated();
        return this;
    }

    @Override
    public ConfigSectionBuilder markUnused() {
        super.markUnused();
        return this;
    }

    @Override
    public ConfigSectionBuilder markExperimental() {
        super.markExperimental();
        return this;
    }

    @Override
    public ConfigSectionBuilder setThreadSafe() {
        super.setThreadSafe();
        return this;
    }

    public MouseConfigBuilder buildSection() {
        ConfigSectImpl section = new ConfigSectImpl(name, canBeDisabled, disablePar, comment);
        applyMetadata(section);
        configBuilder.putSection(path, section);
        return configBuilder;
    }
}