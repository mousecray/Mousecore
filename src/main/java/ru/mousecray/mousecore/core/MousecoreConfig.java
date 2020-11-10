package ru.mousecray.mousecore.core;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class MousecoreConfig {

    private static final File config = new File("./config/" + MousecoreContainer.ID + ".cfg");
    public static String devPath;

    public static void setupAllValues() {
        Configuration config = new Configuration(MousecoreConfig.config);

        devPath = config.get("standard", "devPath", "..\\build\\classes\\main\\",
                "That path to develop directory. It's may sense only you in developing environment.")
                .getString().replace("\\", "/");

        config.save();
    }
}