package ru.mousecray.mousecore.core;

import org.apache.logging.log4j.LogManager;
import ru.mousecray.mousecore.api.log.ConsoleColor;
import ru.mousecray.mousecore.api.log.MouseLogger;

public final class Mousecore {
    public static final String      MOD_ID      = "mousecore";
    public static final String      API_ID      = "mousecore_api";
    public static final String      API_VERSION = "1.0";
    public static final MouseLogger LOGGER      = new MouseLogger(LogManager.getLogger(MOD_ID), "[${value}]", ConsoleColor.PURPLE);

    private Mousecore() { throw new IllegalStateException("Cannot create instance of utility class"); }
}