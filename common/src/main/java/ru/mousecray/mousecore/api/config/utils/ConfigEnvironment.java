/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.api.config.utils;

/**
 * Describes the environment in which the current config instance is operating.
 *
 * <p>Used to decide how config changes should be handled in different runtime
 * contexts (single-player, dedicated server, or multiplayer client).</p>
 *
 * @see ConfigThreadPolicy
 */
public enum ConfigEnvironment {

    /**
     * Single-player game, main menu, or local testing.
     */
    SINGLEPLAYER,

    /**
     * Dedicated server (a dedicated server JVM).
     */
    DEDICATED_SERVER,

    /**
     * A client connected to a remote server (multiplayer or LAN client).
     */
    MULTIPLAYER_CLIENT
}