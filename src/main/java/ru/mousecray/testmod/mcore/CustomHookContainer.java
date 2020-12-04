package ru.mousecray.testmod.mcore;

import net.minecraftforge.fml.common.Mod.EventHandler;
import ru.mousecray.mousecore.api.asm.MinecraftClass;
import ru.mousecray.mousecore.api.asm.MouseContainer;
import ru.mousecray.mousecore.api.asm.event.MouseLoadEvent;
import ru.mousecray.mousecore.api.asm.method.utils.MouseMethodBuilder;

@MouseContainer
public class CustomHookContainer {

    @EventHandler
    public void onHookLoad(MouseLoadEvent event) {
        event.registerInterfaceCreator(MinecraftClass.BlockAir, MinecraftClass.fromCanonicalName("ru.mousecray.testmod.mcore.AirInterface"),
                MouseMethodBuilder.create("test")
                        .setVoid()
                        .build());
        event.registerMethodCreator(MinecraftClass.BlockAir,
                MouseMethodBuilder.create("test")
                        .setVoid()
                        .setArguments(MinecraftClass.BlockAnvil)
                        .build());
    }
}