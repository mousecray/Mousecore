/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.core.config.utils;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.ConfigChangeListener;
import ru.mousecray.mousecore.api.config.MouseConfig;
import ru.mousecray.mousecore.api.config.utils.ConfigSide;
import ru.mousecray.mousecore.api.config.utils.ConfigThreadPolicy;
import ru.mousecray.mousecore.api.config.val.ConfigVal;
import ru.mousecray.mousecore.api.config.val.ConfigValueChange;
import ru.mousecray.mousecore.api.config.val.ConfigValueListener;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.log.ConsoleColor;
import ru.mousecray.mousecore.api.log.LogPrefixes;
import ru.mousecray.mousecore.api.log.MouseLogger;
import ru.mousecray.mousecore.api.platform.PlatformHelper;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigListenerDispatcher {
    private ConfigListenerDispatcher() { }

    public record ValueListenerEntry<V extends CustomType<?>>(
            ConfigThreadPolicy policy,
            ConfigValueListener<V> listener
    ) { }

    public record GlobalListenerEntry(
            ConfigThreadPolicy policy,
            ConfigChangeListener listener
    ) { }

    public record PendingValueNotification<V extends CustomType<?>>(
            ConfigValueChange<V> change,
            List<ValueListenerEntry<V>> listeners
    ) {
        public void invokeSafely(ValueListenerEntry<V> entry, @Nullable MouseLogger logger) {
            try {
                entry.listener().onValueChanged(change.configVal(), change.oldValue(), change.newValue());
            } catch (Exception e) {
                if (logger != null) {
                    ConfigVal<V> val  = change.configVal();
                    String       name = val.getOwner() != null ? val.getOwner().getFullInternalName() : val.toString();
                    logger.atError()
                            .withException(e)
                            .withPrefix(LogPrefixes.CONFIG)
                            .withStyle(ConsoleColor.RED_BG)
                            .log("Error in ConfigValueListener for '{0}'", name);
                }
            }
        }
    }

    public static void dispatch(
            @Nullable MouseConfig config,
            List<PendingValueNotification<?>> notifications,
            List<GlobalListenerEntry> globalListeners,
            @Nullable MouseLogger logger
    ) {
        if (notifications.isEmpty()) return;

        List<Runnable> immediateTasks = new ArrayList<>();
        List<Runnable> clientTasks    = new ArrayList<>();
        List<Runnable> serverTasks    = new ArrayList<>();

        for (PendingValueNotification<?> n : notifications) {
            ConfigSide side = n.change().configVal().getSide();
            for (ValueListenerEntry<?> entry : n.listeners()) {
                ConfigThreadPolicy target = entry.policy() == ConfigThreadPolicy.AUTO
                        ? resolveAuto(side)
                        : entry.policy();
                Runnable task = createInvokeTask(n, entry, logger);
                routeTask(target, task, immediateTasks, clientTasks, serverTasks);
            }
        }

        if (config != null && !globalListeners.isEmpty()) {
            List<ConfigValueChange<?>> allChanges = new ArrayList<>(notifications.size());
            for (PendingValueNotification<?> n : notifications) allChanges.add(n.change());
            List<ConfigValueChange<?>> immutableChanges = Collections.unmodifiableList(allChanges);

            for (GlobalListenerEntry entry : globalListeners) {
                ConfigThreadPolicy target = entry.policy() == ConfigThreadPolicy.AUTO
                        ? resolveAuto(config.getDefaultSide())
                        : entry.policy();
                Runnable task = () -> {
                    try {
                        entry.listener().onConfigChanged(config, immutableChanges);
                    } catch (Exception e) {
                        if (logger != null) {
                            logger.atError()
                                    .withException(e)
                                    .withPrefix(LogPrefixes.CONFIG)
                                    .withStyle(ConsoleColor.RED_BG)
                                    .log("Error in ConfigChangeListener for config '{0}'", config.getName());
                        }
                    }
                };
                routeTask(target, task, immediateTasks, clientTasks, serverTasks);
            }
        }

        if (!immediateTasks.isEmpty()) runBatch(immediateTasks);

        if (!clientTasks.isEmpty()) {
            PlatformHelper platform = PlatformHelper.getInstance();
            if (platform.isClientSide()) {
                if (!platform.executeOnClient(() -> runBatch(clientTasks))) {
                    runBatch(clientTasks);
                }
            }
        }

        if (!serverTasks.isEmpty()) {
            PlatformHelper platform = PlatformHelper.getInstance();
            if (!platform.executeOnServer(() -> runBatch(serverTasks))) {
                if (!platform.isClientSide()) runBatch(serverTasks);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <V extends CustomType<?>> Runnable createInvokeTask(
            PendingValueNotification<?> notification,
            ValueListenerEntry<?> entry,
            @Nullable MouseLogger logger
    ) {
        PendingValueNotification<V> typedNotif = (PendingValueNotification<V>) notification;
        ValueListenerEntry<V>       typedEntry = (ValueListenerEntry<V>) entry;
        return () -> typedNotif.invokeSafely(typedEntry, logger);
    }

    private static void routeTask(
            ConfigThreadPolicy target,
            Runnable task,
            List<Runnable> immediateTasks,
            List<Runnable> clientTasks,
            List<Runnable> serverTasks
    ) {
        switch (target) {
            case IMMEDIATE, AUTO -> immediateTasks.add(task);
            case CLIENT_THREAD -> clientTasks.add(task);
            case SERVER_THREAD -> serverTasks.add(task);
        }
    }

    private static ConfigThreadPolicy resolveAuto(ConfigSide side) {
        return switch (side) {
            case CLIENT -> ConfigThreadPolicy.CLIENT_THREAD;
            case SERVER -> ConfigThreadPolicy.SERVER_THREAD;
            case SYNCED, COMMON -> switch (ConfigEnvironmentController.getCurrent()) {
                case DEDICATED_SERVER -> ConfigThreadPolicy.SERVER_THREAD;
                case MULTIPLAYER_CLIENT -> ConfigThreadPolicy.CLIENT_THREAD;
                case SINGLEPLAYER -> PlatformHelper.getInstance().isClientSide()
                        ? ConfigThreadPolicy.CLIENT_THREAD
                        : ConfigThreadPolicy.SERVER_THREAD;
            };
        };
    }

    private static void runBatch(List<Runnable> tasks) {
        for (Runnable task : tasks) task.run();
    }
}