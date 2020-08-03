package ru.mousecray.testmod.mcore;

import net.minecraftforge.fml.common.Mod.EventHandler;
import ru.mousecray.mousecore.api.asm.MouseContainer;
import ru.mousecray.mousecore.api.asm.event.MouseLoadEvent;
import ru.mousecray.mousecore.api.asm.transformer.MouseHookParent;

@MouseContainer
public class CustomHookContainer {

    private static final MouseHookParent AIR = new NewAirInterface();

    @EventHandler
    public void onHookLoad(MouseLoadEvent event) {
        assert event != null;
        event.registerHook(AIR);
    }
}