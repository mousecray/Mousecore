/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.api.config.utils;

/**
 * Determines on which thread a configuration change listener should be invoked.
 *
 * <p>The policy is evaluated against a parameter's {@code ConfigSide} and the
 * current {@code ConfigEnvironment}; depending on the chosen policy, the
 * listener may be dispatched to the client main thread, the server main
 * thread, the calling thread, or not invoked at all in certain environments.</p>
 *
 * @see ConfigSide
 * @see ConfigEnvironment
 */
public enum ConfigThreadPolicy {

    /**
     * Automatically selects the target thread based on the parameter's
     * {@code ConfigSide} and the current {@code ConfigEnvironment}.
     */
    AUTO,

    /**
     * Always dispatches on the client main thread (the render thread).
     * Not invoked on a dedicated server.
     */
    CLIENT_THREAD,

    /**
     * Always dispatches on the server main thread.
     * If no local (integrated) server is running on a client, the listener
     * is not invoked.
     */
    SERVER_THREAD,

    /**
     * Invokes the listener immediately on the thread that triggered the change
     * (for example, the config I/O thread, a Netty thread, or a GUI thread).
     */
    IMMEDIATE
}