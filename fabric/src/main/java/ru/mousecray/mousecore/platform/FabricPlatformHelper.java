/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.platform;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import ru.mousecray.mousecore.api.platform.PlatformHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;

public class FabricPlatformHelper extends PlatformHelper {
    private static volatile MinecraftServer currentServer;

    /*
     * TODO: Вызвать 1 раз в ModInitializer (Fabric):
     * ServerLifecycleEvents.SERVER_STARTING.register(FabricPlatformHelper::setCurrentServer);
     * ServerLifecycleEvents.SERVER_STOPPED.register(s -> FabricPlatformHelper.setCurrentServer(null));
     */
    public static void setCurrentServer(@Nullable MinecraftServer server) { currentServer = server; }

    @Override public String getPlatformName()           { return "Fabric"; }
    @Override public boolean isModLoaded(String modId)  { return FabricLoader.getInstance().isModLoaded(modId); }
    @Override public boolean isDevelopmentEnvironment() { return FabricLoader.getInstance().isDevelopmentEnvironment(); }
    @Override public boolean isClientSide()             { return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT; }
    @Override public boolean isServerRunning()          { return currentServer != null; }
    @Override public boolean isOnClientThread()         { return isClientSide() && FabricClientExecutor.isOnClientThread(); }

    @Override
    public boolean isOnServerThread() {
        MinecraftServer server = currentServer;
        return server != null && server.isSameThread();
    }

    @Override
    public boolean executeOnClient(Runnable task) {
        if (!isClientSide()) return false;
        return FabricClientExecutor.execute(task);
    }

    @Override
    public boolean executeOnServer(Runnable task) {
        MinecraftServer server = currentServer;
        if (server == null) return false;
        if (server.isSameThread()) task.run();
        else server.execute(task);
        return true;
    }

    @Nonnull @Override
    public String getGameLocale() {
        if (isClientSide()) {
            String clientLang = FabricClientExecutor.getLanguage();
            if (clientLang != null) return clientLang;
        }
        return Locale.getDefault().toString().toLowerCase(Locale.ROOT);
    }
}