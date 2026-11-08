package ru.mousecray.mousecore;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import ru.mousecray.mousecore.api.log.ConsoleColor;
import ru.mousecray.mousecore.api.log.LogPrefixes;
import ru.mousecray.mousecore.core.Mousecore;

@Mod(Mousecore.MOD_ID)
public class MousecoreForge {
    public MousecoreForge(FMLJavaModLoadingContext ignoredContext) {
        Mousecore.getInstance().getLogger().atInfo()
                .withPrefix(LogPrefixes.INIT)
                .withStyle(ConsoleColor.GREEN_BG)
                .log("Initialize Forge library");
    }
}
