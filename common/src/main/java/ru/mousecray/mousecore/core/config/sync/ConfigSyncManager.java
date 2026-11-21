/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.core.config.sync;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.ConfigEnvironment;
import ru.mousecray.mousecore.api.log.ConsoleColor;
import ru.mousecray.mousecore.api.log.LogPrefixes;
import ru.mousecray.mousecore.api.platform.PlatformHelper;
import ru.mousecray.mousecore.core.config.MouseConfigImpl;
import ru.mousecray.mousecore.core.config.utils.ConfigEnvironmentController;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigSyncManager {
    private static final              ConcurrentHashMap<String, WeakReference<MouseConfigImpl>> REGISTERED = new ConcurrentHashMap<>();
    @Nullable private static volatile Consumer<ConfigSyncSnapshot>                              serverBroadcastHandler;

    private ConfigSyncManager() { }

    public static void register(MouseConfigImpl config) {
        Objects.requireNonNull(config);
        REGISTERED.put(config.getSyncKey(), new WeakReference<>(config));
    }

    public static void unregister(MouseConfigImpl config) {
        Objects.requireNonNull(config);
        String key = config.getSyncKey();
        REGISTERED.computeIfPresent(key, (k, ref) -> {
            MouseConfigImpl current = ref.get();
            return (current == null || current == config) ? null : ref;
        });
    }

    @Nullable
    public static MouseConfigImpl getRegistered(String syncKey) {
        WeakReference<MouseConfigImpl> ref = REGISTERED.get(Objects.requireNonNull(syncKey));
        if (ref == null) return null;
        MouseConfigImpl config = ref.get();
        if (config == null) REGISTERED.remove(syncKey, ref);
        return config;
    }

    public static List<MouseConfigImpl> getRegisteredConfigs() {
        List<MouseConfigImpl> alive = new ArrayList<>();
        REGISTERED.forEach((key, ref) -> {
            MouseConfigImpl config = ref.get();
            if (config != null) alive.add(config);
            else REGISTERED.remove(key, ref);
        });
        return Collections.unmodifiableList(alive);
    }

    /**
     * Invoked by the platform when a dedicated server starts up.
     * Switches the current config environment to {@link ConfigEnvironment#DEDICATED_SERVER}.
     */
    public static void onDedicatedServerStart() {
        ConfigEnvironmentController.setCurrent(ConfigEnvironment.DEDICATED_SERVER);
    }

    /**
     * Invoked by the platform when the client joins a remote (non-local) server,
     * i.e. when {@code !Minecraft.getInstance().isLocalServer()}.
     * Switches the current config environment to {@link ConfigEnvironment#MULTIPLAYER_CLIENT}.
     */
    public static void onClientJoinMultiplayer() {
        ConfigEnvironmentController.setCurrent(ConfigEnvironment.MULTIPLAYER_CLIENT);
    }

    /**
     * Invoked by the platform when the client disconnects from a server
     * (returning to the main menu).
     *
     * <p>Resets the config environment back to {@link ConfigEnvironment#SINGLEPLAYER}
     * and reverts all applied overrides via {@link #revertAll()}.</p>
     */
    public static void onClientDisconnect() {
        ConfigEnvironmentController.setCurrent(ConfigEnvironment.SINGLEPLAYER);
        revertAll();
    }

    /**
     * Registers a broadcast handler (typically from Fabric/Forge integration code)
     * used to send an updated config snapshot to all connected players.
     *
     * @param handler the handler to register, or {@code null} to clear the current one
     */
    public static void setServerBroadcastHandler(@Nullable Consumer<ConfigSyncSnapshot> handler) {
        serverBroadcastHandler = handler;
    }

    /**
     * Collects a network snapshot for the given config instance.
     *
     * @param config the config to snapshot; must not be {@code null}
     * @return the snapshot produced by {@link MouseConfigImpl#collectNetworkSnapshot()}
     * @throws NullPointerException if {@code config} is {@code null}
     */
    public static ConfigSyncSnapshot collectSnapshot(MouseConfigImpl config) {
        Objects.requireNonNull(config);
        return config.collectNetworkSnapshot();
    }

    /**
     * Collects non-empty network snapshots from all registered (live) configs.
     * Typically called when a player logs in to a server, so the client can
     * receive the current state of every syncable config.
     *
     * @return a list of non-empty snapshots; configs that produce empty snapshots
     * are skipped
     */
    public static List<ConfigSyncSnapshot> collectAllSnapshots() {
        List<ConfigSyncSnapshot> result = new ArrayList<>();
        for (MouseConfigImpl config : getRegisteredConfigs()) {
            ConfigSyncSnapshot snapshot = config.collectNetworkSnapshot();
            if (!snapshot.isEmpty()) result.add(snapshot);
        }
        return result;
    }

    /**
     * Broadcasts the current SYNCED snapshot of the given config to all players,
     * provided that a server is running and a network broadcast handler has been
     * registered. Does nothing otherwise.
     *
     * @param config the config whose snapshot should be broadcast
     */
    public static void broadcastIfServer(MouseConfigImpl config) {
        Consumer<ConfigSyncSnapshot> handler = serverBroadcastHandler;
        if (handler == null) return;
        if (!PlatformHelper.getInstance().isServerRunning()) return;
        ConfigSyncSnapshot snapshot = config.collectNetworkSnapshot();
        if (!snapshot.isEmpty()) handler.accept(snapshot);
    }

    public static boolean applySnapshot(ConfigSyncSnapshot snapshot) {
        Objects.requireNonNull(snapshot);
        MouseConfigImpl config = getRegistered(snapshot.getConfigName());
        if (config == null) return false;
        applySnapshot(config, snapshot);
        return true;
    }

    public static void applySnapshot(MouseConfigImpl config, ConfigSyncSnapshot snapshot) {
        Objects.requireNonNull(config);
        Objects.requireNonNull(snapshot);
        if (ConfigEnvironmentController.getCurrent() != ConfigEnvironment.MULTIPLAYER_CLIENT) {
            if (config.logger != null) {
                config.logger.atWarn()
                        .withPrefix(LogPrefixes.CONFIG)
                        .withStyle(ConsoleColor.YELLOW_BG)
                        .log("Ignored ConfigSyncSnapshot for '{0}' outside MULTIPLAYER_CLIENT environment", config.getName());
            }
            return;
        }
        config.applyNetworkSnapshot(snapshot);
    }

    public static void revertOverrides(MouseConfigImpl config) {
        Objects.requireNonNull(config);
        config.revertNetworkOverrides();
    }

    public static void revertAll() {
        for (MouseConfigImpl config : getRegisteredConfigs()) config.revertNetworkOverrides();
    }
}