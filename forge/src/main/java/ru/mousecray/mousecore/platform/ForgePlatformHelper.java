/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.platform;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.server.ServerLifecycleHooks;
import ru.mousecray.mousecore.api.platform.PlatformHelper;

public class ForgePlatformHelper extends PlatformHelper {
    @Override public String getPlatformName()           { return "Forge"; }
    @Override public boolean isModLoaded(String modId)  { return ModList.get().isLoaded(modId); }
    @Override public boolean isDevelopmentEnvironment() { return !FMLLoader.isProduction(); }
    @Override public boolean isClientSide()             { return FMLEnvironment.dist == Dist.CLIENT; }
    @Override public boolean isServerRunning()          { return ServerLifecycleHooks.getCurrentServer() != null; }
    @Override public boolean isOnClientThread()         { return isClientSide() && ForgeClientExecutor.isOnClientThread(); }

    @Override
    public boolean isOnServerThread() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        return server != null && server.isSameThread();
    }

    @Override
    public boolean executeOnClient(Runnable task) {
        if (!isClientSide()) return false;
        return ForgeClientExecutor.execute(task);
    }

    @Override
    public boolean executeOnServer(Runnable task) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return false;
        if (server.isSameThread()) task.run();
        else server.execute(task);
        return true;
    }
}
