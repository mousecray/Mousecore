/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.api.platform;

import java.util.ServiceLoader;

/**
 * Platform abstraction helper providing information about the current runtime environment
 * and utilities for scheduling work on the client or server main threads.
 *
 * <p>Implementations of this interface are provided per platform (e.g., Fabric, Forge, NeoForge)
 * and bridge the gap between the mod and platform-specific APIs. All methods are intended to be
 * safe to call from any thread unless explicitly stated otherwise.</p>
 */
public abstract class PlatformHelper {
    private static final PlatformHelper INSTANCE = ServiceLoader
            .load(PlatformHelper.class, PlatformHelper.class.getClassLoader())
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(
                    """
                            ===============================================================
                            [Mousecore API] Critical error: PlatformHelper implementation is missing!
                            Neither 'mousecore-fabric' nor 'mousecore-forge' platform service was loaded.
                            Please, verify your mod installation or META-INF/services registration.
                            ==============================================================="""
            ));

    protected PlatformHelper()                 { }
    public static PlatformHelper getInstance() { return INSTANCE; }

    /**
     * Returns the human-readable name of the underlying mod loader / platform
     * (for example, {@code "fabric"}, {@code "forge"}, {@code "neoforge"}).
     *
     * @return the platform name; never {@code null}
     */
    public abstract String getPlatformName();

    /**
     * Checks whether a mod with the given identifier is currently loaded.
     *
     * @param modId the mod identifier to test; must not be {@code null}
     * @return {@code true} if the mod is loaded, {@code false} otherwise
     */
    public abstract boolean isModLoaded(String modId);

    /**
     * Indicates whether the game is currently running in a development environment
     * (for example, launched directly from an IDE or a Gradle run task).
     *
     * @return {@code true} if running in a development environment, {@code false} otherwise
     */
    public abstract boolean isDevelopmentEnvironment();

    /**
     * Returns a short textual description of the current environment
     * ({@code "development"} or {@code "production"}), derived from
     * {@link #isDevelopmentEnvironment()}.
     *
     * @return the environment name; never {@code null}
     */
    public String getEnvironmentName() { return isDevelopmentEnvironment() ? "development" : "production"; }

    /**
     * Indicates whether the current JVM is a physical client
     * (i.e., not a dedicated server).
     *
     * @return {@code true} on a physical client, {@code false} on a dedicated server
     */
    public abstract boolean isClientSide();

    /**
     * Indicates whether a logical server is currently running in this JVM.
     * This is {@code true} both for a dedicated server and for the integrated
     * server used in single-player.
     *
     * @return {@code true} if a logical server is running, {@code false} otherwise
     */
    public abstract boolean isServerRunning();

    /**
     * Indicates whether the calling thread is the main client thread
     * (the render thread).
     *
     * @return {@code true} if called from the client main thread, {@code false} otherwise
     */
    public abstract boolean isOnClientThread();

    /**
     * Indicates whether the calling thread is the main server thread.
     *
     * @return {@code true} if called from the server main thread, {@code false} otherwise
     */
    public abstract boolean isOnServerThread();

    /**
     * Executes the given task on the client main thread.
     *
     * <p>If the calling thread is already the client main thread, the task is
     * executed immediately. Otherwise, the task is scheduled for execution via
     * the client's task queue (e.g., {@code Minecraft.execute}).</p>
     *
     * @param task the task to run on the client main thread; must not be {@code null}
     * @return {@code true} if the task was executed or successfully scheduled;
     * {@code false} if the client is unavailable and the task could not be submitted
     */
    public abstract boolean executeOnClient(Runnable task);

    /**
     * Executes the given task on the server main thread.
     *
     * <p>If the calling thread is already the server main thread, the task is
     * executed immediately. Otherwise, the task is scheduled for execution via
     * the server's task queue (e.g., {@code server.execute}).</p>
     *
     * @param task the task to run on the server main thread; must not be {@code null}
     * @return {@code true} if the task was executed or successfully scheduled;
     * {@code false} if no server is currently running and the task could not be submitted
     */
    public abstract boolean executeOnServer(Runnable task);
}