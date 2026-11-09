package ru.mousecray.mousecore.core;

import org.apache.logging.log4j.LogManager;
import ru.mousecray.mousecore.api.log.ConsoleColor;
import ru.mousecray.mousecore.api.log.MouseLogger;
import ru.mousecray.mousecore.core.config.MouseConfigImpl;

public final class Mousecore {
    public static final String MOD_ID      = "mousecore";
    public static final String MOD_NAME    = "Mousecore";
    public static final String API_ID      = "mousecore_api";
    public static final String API_VERSION = "1.0";

    private static Mousecore instance;
    public static Mousecore getInstance() { return instance != null ? instance : (instance = new Mousecore()); }

    private final MouseLogger     logger;
    private final MouseConfigImpl config;

    private Mousecore() {
        logger = new MouseLogger(LogManager.getLogger(MOD_ID), "[${value}]", ConsoleColor.PURPLE);
//        TODO: config = new MousecoreConfig(MOD_NAME, logger); see MouseBlockState and MouseItemState
        config = null;
    }

    public MouseLogger getLogger()     { return logger; }
    public MouseConfigImpl getConfig() { return config; }
}