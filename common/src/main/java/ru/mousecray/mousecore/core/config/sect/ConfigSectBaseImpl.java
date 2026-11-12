/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.sect;

import ru.mousecray.mousecore.api.DisplayName;
import ru.mousecray.mousecore.api.config.par.ConfigParBase;
import ru.mousecray.mousecore.api.config.sect.ConfigSectBase;
import ru.mousecray.mousecore.api.container.ImmutableDisplayNameMap;
import ru.mousecray.mousecore.api.log.MouseLogger;
import ru.mousecray.mousecore.api.utils.MouseStrings;
import ru.mousecray.mousecore.core.config.MouseConfigImpl;
import ru.mousecray.mousecore.core.config.par.ConfigParBaseImpl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
public abstract class ConfigSectBaseImpl implements ConfigSectBase {
    @Nonnull protected final  DisplayName                                         name;
    @Nullable protected       ConfigSectBaseImpl                                  parent;
    @Nullable protected final String                                              comment;
    protected                 ImmutableDisplayNameMap.Mutable<ConfigSectBaseImpl> children;
    protected final           boolean                                             canBeDisabled;
    protected                 boolean                                             isDisabled;
    protected                 boolean                                             setByConfig;
    protected                 int                                                 deep;
    protected                 MouseConfigImpl                                     config;

    public ConfigSectBaseImpl(DisplayName name, boolean canBeDisabled, @Nullable String comment) {
        this.name = Objects.requireNonNull(name);
        this.canBeDisabled = canBeDisabled;

        comment = MouseStrings.trimWith(comment, true, '\t');
        if (comment != null && comment.isEmpty()) comment = null;
        this.comment = comment;
    }

    @Override public boolean hasChildren()   { return isSupportChildren() && children != null && !children.isEmpty(); }
    @Override public boolean canBeDisabled() { return canBeDisabled; }

    @Override public boolean isDisabled() {
        if (canBeDisabled()) {
            boolean is = isDisabled;
            if (parent != null && parent.isDisabled()) is = is || parent.isDisabled();
            return is;
        } else return false;
    }

    @Override public <T extends ConfigSectBase> boolean isSupportChild(T child) { return true; }

    private boolean checkChildren() {
        if (!isSupportChildren()) throw new UnsupportedOperationException("This ConfigSection cannot contains children");
        if (children == null) {
            children = new ImmutableDisplayNameMap.Mutable<>();
            return true;
        } else return false;
    }

    @Override public boolean hasChildByDisplayName(String name) {
        Objects.requireNonNull(name);
        if (checkChildren()) return false;
        else return children.containsByDisplayName(name);
    }

    @Nullable @Override public ConfigSectBaseImpl getChildByDisplayName(String name) {
        Objects.requireNonNull(name);
        if (checkChildren()) return null;
        else return children.getByDisplayName(name);
    }

    @Override public boolean hasChild(String name) {
        Objects.requireNonNull(name);
        if (checkChildren()) return false;
        else return children.containsByInternalName(name);
    }

    @Nullable @Override public ConfigSectBaseImpl getChild(String name) {
        Objects.requireNonNull(name);
        if (checkChildren()) return null;
        else return children.getByInternalName(name);
    }

    public void addChild(ConfigSectBase section) {
        ConfigSectBaseImpl sectionImpl = (ConfigSectBaseImpl) section;
        Objects.requireNonNull(section);
        checkChildren();
        if (!isSupportChild(section)) {
            throw new UnsupportedOperationException("This ConfigSection cannot support child '" + section.getName().getInternalName() + "'");
        }
        ImmutableDisplayNameMap.Entry<ConfigSectBaseImpl> prev = children.put(section.getName(), sectionImpl);
        if (prev != null && hasLogger()) {
            assert getLogger() != null;
            getLogger().atDebug().log("ConfigSectBaseImpl '{0}' was overwritten by ConfigSectBaseImpl '{1}'",
                    prev.getValue().getFullInternalName(), section.getName().getInternalName());
        }
        sectionImpl.parent = this;
        sectionImpl.setDeep(deep + 1);
        sectionImpl.setConfig(config);

        if (hasConfig()) {
            assert getConfig() != null;
            if (getConfig().isBuilt()) {
                ArrayList<ConfigSectBase> list = new ArrayList<>();
                list.add(section);
                List<ConfigParBase<? extends ru.mousecray.mousecore.api.customtype.CustomType<?>>> children = config.getChildrenRecursivelyInternal(list)
                        .stream()
                        .filter(val -> val instanceof ConfigParBaseImpl)
                        .map(val -> ((ConfigParBase<? extends ru.mousecray.mousecore.api.customtype.CustomType<?>>) val))
                        .collect(Collectors.toList());
                if (section instanceof ConfigParBaseImpl)
                    children.add(((ConfigParBase<? extends ru.mousecray.mousecore.api.customtype.CustomType<?>>) section));
                synchronized (config.dirtySections) { config.dirtySections.addAll(children); }
            }
        }
    }

    public void setDeep(int deep) {
        this.deep = deep;
        if (hasChildren()) children.values().forEach(c -> c.setDeep(deep + 1));
    }

    public void setConfig(MouseConfigImpl config) {
        this.config = config;
        if (hasChildren()) children.values().forEach(c -> c.setConfig(config));
    }

    @SuppressWarnings("DataFlowIssue")
    public void removeChild(DisplayName name) {
        Objects.requireNonNull(name);
        if (checkChildren()) {
            ImmutableDisplayNameMap.Entry<ConfigSectBaseImpl> remove = children.remove(name);
            if (remove != null && remove.getValue() != null) {
                ConfigSectBaseImpl value = remove.getValue();
                value.parent = null;
                if (hasConfig() && getConfig().isBuilt()) {
                    ArrayList<ConfigSectBase> list = new ArrayList<>();
                    list.add(value);
                    List<ConfigParBase<? extends ru.mousecray.mousecore.api.customtype.CustomType<?>>> children = config.getChildrenRecursivelyInternal(list)
                            .stream()
                            .filter(val -> val instanceof ConfigParBaseImpl)
                            .map(val -> ((ConfigParBase<? extends ru.mousecray.mousecore.api.customtype.CustomType<?>>) val))
                            .collect(Collectors.toList());
                    if (value instanceof ConfigParBaseImpl)
                        children.add(((ConfigParBase<? extends ru.mousecray.mousecore.api.customtype.CustomType<?>>) value));
                    synchronized (config.dirtySections) { config.dirtySections.removeAll(children); }
                }
            }
        }
    }

    @Override
    public ImmutableDisplayNameMap<ConfigSectBaseImpl> getChildren() {
        checkChildren();
        return children;
    }

    public void write(Consumer<ConfigSectBaseImpl> startAction, Consumer<ConfigSectBaseImpl> endAction) {
        startAction.accept(this);
        if (hasChildren()) children.forEach((key, section) -> section.write(startAction, endAction));
        endAction.accept(this);
    }

    @Nonnull @Override
    public String getFullInternalName() {
        StringBuilder      builder    = new StringBuilder(name.getInternalName());
        ConfigSectBaseImpl parentBase = parent;
        while (parentBase != null) {
            builder.insert(0, parentBase.getName().getInternalName() + ".");
            parentBase = parentBase.parent;
        }
        return builder.toString();
    }

    @Override
    public void reset() {
        if (hasChildren()) {
            children.forEach((key, value) -> {
                value.reset();
                value.reloadCache();
            });
        }
    }

    protected void reloadCache() {
        if (hasChildren()) children.forEach((key, value) -> value.reloadCache());
    }

    @Nonnull @Override public DisplayName getName()                             { return name; }
    @Nullable @Override public ConfigSectBase getParent()                       { return parent; }
    @Nullable @Override public String getComment()                              { return comment; }
    @Override public int getDeep()                                              { return deep; }

    @Nullable @Override public MouseConfigImpl getConfig()                      { return config; }
    @Override public boolean hasConfig()                                        { return config != null; }

    @SuppressWarnings("DataFlowIssue") @Nullable public MouseLogger getLogger() { return getConfig().getLogger(); }
    public boolean hasLogger()                                                  { return hasConfig() && config.getLogger() != null; }
    public void setParent(@Nullable ConfigSectBaseImpl parent)                  { this.parent = parent; }

    @SuppressWarnings("DataFlowIssue") @Override
    public boolean isLoaded() {
        return setByConfig && hasConfig() && getConfig().isLoaded();
    }

    @Override
    public void setDisabled(boolean disabled) {
        if (!canBeDisabled()) throw new UnsupportedOperationException("This ConfigSection does not support disabling");
        isDisabled = disabled;
        if (hasChildren()) children.forEach((key, value) -> value.setDisabled(disabled));
    }
}