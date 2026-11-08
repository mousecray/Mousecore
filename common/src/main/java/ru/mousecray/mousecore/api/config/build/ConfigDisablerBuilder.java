/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.config.build;

import ru.mousecray.mousecore.api.DisplayName;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.ConfigParDisabler;
import ru.mousecray.mousecore.api.config.specific.ConfigValType;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.values.ConfigValDisabler;
import ru.mousecray.mousecore.api.utils.MouseCollections;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Objects;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigDisablerBuilder<T extends AbstractConfigChildBuilder> {
    private final          T                                   childBuilder;
    @Nonnull private final DisplayName                         name;
    private final          boolean                             useDefaultName;
    @Nullable private      ArrayList<PredefinedValue<Boolean>> configureValues;
    private                boolean                             disableAdditionalThreshold;
    private                boolean                             enableThresholdConfiguration;

    private ConfigDisablerBuilder(T childBuilder, DisplayName name, boolean useDefaultName) {
        this.childBuilder = childBuilder;
        this.name = name;
        this.useDefaultName = useDefaultName;
    }

    static <T extends AbstractConfigChildBuilder> ConfigDisablerBuilder<T> create(T sectionBuilder, String displayName, boolean useDefaultName) {
        return new ConfigDisablerBuilder<>(
                Objects.requireNonNull(sectionBuilder),
                new DisplayName("disabler",
                        Objects.requireNonNull(displayName)), useDefaultName
        );
    }

    public ConfigDisablerBuilder<T> enableThresholdConfiguration() {
        enableThresholdConfiguration = true;
        return this;
    }

    public ConfigDisablerBuilder<T> disableAdditionalThreshold() {
        disableAdditionalThreshold = true;
        return this;
    }

    public ConfigDisablerBuilder<T> addConfigureValue(@Nonnull String name, boolean value) {
        Objects.requireNonNull(name);
        if (configureValues == null) configureValues = new ArrayList<>();
        configureValues.add(new PredefinedValue<>(name, value));
        return this;
    }

    public T buildDisabler() {
        ConfigParDisabler par = new ConfigParDisabler(
                name, useDefaultName,
                new ConfigValDisabler(
                        enableThresholdConfiguration,
                        disableAdditionalThreshold ? null :
                                childBuilder.configBuilder.dictionary != null
                                        ? childBuilder.configBuilder.dictionary.getDefThresholdForType(ConfigValType.BOOLEAN) : null,
                        MouseCollections.toArray(configureValues, PredefinedValue.class)
                ), null);
        childBuilder.addDisabler(par);
        return childBuilder;
    }
}