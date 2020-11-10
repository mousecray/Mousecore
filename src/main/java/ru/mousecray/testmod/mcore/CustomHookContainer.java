package ru.mousecray.testmod.mcore;

import net.minecraftforge.fml.common.Mod.EventHandler;
import ru.mousecray.mousecore.api.asm.MouseContainer;
import ru.mousecray.mousecore.api.asm.event.MouseLoadEvent;

@MouseContainer
public class CustomHookContainer {

    @EventHandler
    public void onHookLoad(MouseLoadEvent event) {
        event.registerInterfaceCreator("net.minecraft.block.BlockAir", AirInterface.class);
    }
}