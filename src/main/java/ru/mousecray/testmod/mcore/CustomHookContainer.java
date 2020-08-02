package ru.mousecray.testmod.mcore;

import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import ru.mousecray.mousecore.api.asm.MouseContainer;
import ru.mousecray.mousecore.api.asm.event.MouseLoadEvent;
import ru.mousecray.mousecore.api.asm.transformer.MouseHookParent;

import javax.annotation.Nonnull;

@MouseContainer(hookid = CustomHookContainer.HOOKID)
public class CustomHookContainer {

    public static final String HOOKID = "testmodhook";

    private static final MouseHookParent AIR = new NewAirInterface();

    @Instance
    public static CustomHookContainer INSTANCE;

    @EventHandler
    public void onHookLoad(@Nonnull MouseLoadEvent event) {
        event.registerHook(AIR);
    }
}