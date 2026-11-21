/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.platform;

import net.minecraft.client.Minecraft;

final class FabricClientExecutor {
    @SuppressWarnings("ConstantValue")
    static boolean isOnClientThread() {
        Minecraft mc = Minecraft.getInstance();
        return mc != null && mc.isSameThread();
    }

    @SuppressWarnings("ConstantValue")
    static boolean execute(Runnable task) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) return false;
        if (mc.isSameThread()) task.run();
        else mc.execute(task);
        return true;
    }
}