package ru.mousecray.mousecore;

import net.fabricmc.api.ModInitializer;
import ru.mousecray.mousecore.api.log.ConsoleColor;
import ru.mousecray.mousecore.api.log.LogPrefixes;
import ru.mousecray.mousecore.core.Mousecore;

public class MousecoreFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Mousecore.LOGGER.atInfo()
                .withPrefix(LogPrefixes.INIT)
                .withStyle(ConsoleColor.GREEN_BG)
                .log("Initialize Fabric library");
    }
}
