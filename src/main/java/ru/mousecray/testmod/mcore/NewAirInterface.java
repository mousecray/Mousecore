package ru.mousecray.testmod.mcore;

import ru.mousecray.mousecore.api.asm.MouseClass;
import ru.mousecray.mousecore.api.asm.transformer.MouseHookAddInterface;

@MouseClass
public class NewAirInterface extends MouseHookAddInterface<AirInterface> {

    public NewAirInterface() {
        super(CustomHookContainer.HOOKID, "net.minecraft.block.BlockAir", AirInterface.class);
    }
}