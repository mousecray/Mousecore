/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.config;

import ru.mousecray.mousecore.api.DisplayName;
import ru.mousecray.mousecore.api.container.ImmutableDisplayNameMap;
import ru.mousecray.mousecore.api.log.MouseLogger;
import ru.mousecray.mousecore.api.utils.MouseStrings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
public abstract class ConfigSectBase {
    @Nonnull protected final  DisplayName                                     name;
    @Nullable protected       ConfigSectBase                                  parent;
    @Nullable protected final String                                          comment;
    protected                 ImmutableDisplayNameMap.Mutable<ConfigSectBase> children;
    protected final           boolean                                         canBeDisabled;
    protected                 boolean                                         isDisabled;
    protected                 boolean                                         setByConfig;
    protected                 int                                             deep;
    protected                 MouseConfig                                     config;

    public ConfigSectBase(DisplayName name, boolean canBeDisabled, @Nullable String comment) {
        this.name = Objects.requireNonNull(name);
        this.canBeDisabled = canBeDisabled;

        comment = MouseStrings.trimWith(comment, true, '\t');
        if (comment != null && comment.isEmpty()) comment = null;
        this.comment = comment;
    }

    public boolean hasChildren()   { return isSupportChildren() && children != null && !children.isEmpty(); }
    public boolean canBeDisabled() { return canBeDisabled; }

    public boolean isDisabled() {
        if (canBeDisabled()) {
            boolean is = isDisabled;
            if (parent != null && parent.isDisabled()) is = is || parent.isDisabled();
            return is;
        } else return false;
    }

    public abstract boolean isSupportChildren();
    public boolean isSupportChild(ConfigSectBase child) { return true; }

    private boolean checkChildren() {
        if (!isSupportChildren()) throw new UnsupportedOperationException("This ConfigSection cannot contains children");
        if (children == null) {
            children = new ImmutableDisplayNameMap.Mutable<>();
            return true;
        } else return false;
    }

    public boolean hasChildByDisplayName(String name) {
        Objects.requireNonNull(name);
        if (checkChildren()) return false;
        else return children.containsByDisplayName(name);
    }

    @Nullable
    public ConfigSectBase getChildByDisplayName(String name) {
        Objects.requireNonNull(name);
        if (checkChildren()) return null;
        else return children.getByDisplayName(name);
    }

    public boolean hasChild(String name) {
        Objects.requireNonNull(name);
        if (checkChildren()) return false;
        else return children.containsByInternalName(name);
    }

    @Nullable
    public ConfigSectBase getChild(String name) {
        Objects.requireNonNull(name);
        if (checkChildren()) return null;
        else return children.getByInternalName(name);
    }

    public void addChild(ConfigSectBase section) {
        Objects.requireNonNull(section);
        checkChildren();
        if (!isSupportChild(section)) {
            throw new UnsupportedOperationException("This ConfigSection cannot support child '" + section.getName().getInternalName() + "'");
        }
        ImmutableDisplayNameMap.Entry<ConfigSectBase> prev = children.put(section.getName(), section);
        if (prev != null && hasLogger()) {
            assert getLogger() != null;
            getLogger().atDebug().log("ConfigSectBase '{0}' was overwritten by ConfigSectBase '{1}'",
                    prev.getValue().getFullInternalName(), section.getName().getInternalName());
        }
        section.parent = this;
        section.setDeep(deep + 1);
        section.setConfig(config);

        if (hasConfig()) {
            assert getConfig() != null;
            if (getConfig().isBuilt()) {
                ArrayList<ConfigSectBase> list = new ArrayList<>();
                list.add(section);
                List<ConfigParBase<?>> children = config.getChildrenRecursivelyInternal(list)
                        .stream()
                        .filter(val -> val instanceof ConfigParBase)
                        .map(val -> ((ConfigParBase<?>) val))
                        .collect(Collectors.toList());
                if (section instanceof ConfigParBase) children.add(((ConfigParBase<?>) section));
                synchronized (config.dirtySections) { config.dirtySections.addAll(children); }
            }
        }
    }

    protected void setDeep(int deep) {
        this.deep = deep;
        if (hasChildren()) children.values().forEach(c -> c.setDeep(deep + 1));
    }

    protected void setConfig(MouseConfig config) {
        this.config = config;
        if (hasChildren()) children.values().forEach(c -> c.setConfig(config));
    }

    @SuppressWarnings("DataFlowIssue")
    public void removeChild(DisplayName name) {
        Objects.requireNonNull(name);
        if (checkChildren()) {
            ImmutableDisplayNameMap.Entry<ConfigSectBase> remove = children.remove(name);
            if (remove != null && remove.getValue() != null) {
                ConfigSectBase value = remove.getValue();
                value.parent = null;
                if (hasConfig() && getConfig().isBuilt()) {
                    ArrayList<ConfigSectBase> list = new ArrayList<>();
                    list.add(value);
                    List<ConfigParBase<?>> children = config.getChildrenRecursivelyInternal(list)
                            .stream()
                            .filter(val -> val instanceof ConfigParBase)
                            .map(val -> ((ConfigParBase<?>) val))
                            .collect(Collectors.toList());
                    if (value instanceof ConfigParBase) children.add(((ConfigParBase<?>) value));
                    synchronized (config.dirtySections) { config.dirtySections.removeAll(children); }
                }
            }
        }
    }

    public ImmutableDisplayNameMap<ConfigSectBase> getChildren() {
        checkChildren();
        return children;
    }

    protected void write(Consumer<ConfigSectBase> startAction, Consumer<ConfigSectBase> endAction) {
        startAction.accept(this);
        if (hasChildren()) children.forEach((key, section) -> section.write(startAction, endAction));
        endAction.accept(this);
    }

    @Nonnull
    public String getFullInternalName() {
        StringBuilder  builder    = new StringBuilder(name.getInternalName());
        ConfigSectBase parentBase = parent;
        while (parentBase != null) {
            builder.insert(0, parentBase.getName().getInternalName() + ".");
            parentBase = parentBase.parent;
        }
        return builder.toString();
    }

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

    @Nonnull public DisplayName getName()                                       { return name; }
    @Nullable public ConfigSectBase getParent()                                 { return parent; }
    @Nullable public String getComment()                                        { return comment; }
    public int getDeep()                                                        { return deep; }

    @Nullable public MouseConfig getConfig()                                    { return config; }
    public boolean hasConfig()                                                  { return config != null; }

    @SuppressWarnings("DataFlowIssue") @Nullable public MouseLogger getLogger() { return getConfig().getLogger(); }
    public boolean hasLogger()                                                  { return hasConfig() && config.getLogger() != null; }
    protected void setParent(@Nullable ConfigSectBase parent)                   { this.parent = parent; }

    @SuppressWarnings("DataFlowIssue")
    public boolean isLoaded() {
        return setByConfig && hasConfig() && getConfig().isLoaded();
    }

    public void setDisabled(boolean disabled) {
        if (!canBeDisabled()) throw new UnsupportedOperationException("This ConfigSection does not support disabling");
        isDisabled = disabled;
        if (hasChildren()) children.forEach((key, value) -> value.setDisabled(disabled));
    }
}