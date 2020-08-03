package ru.mousecray.testmod.mcore;

import ru.mousecray.mousecore.api.asm.transformer.MouseHookAddInterface;

public class NewAirInterface extends MouseHookAddInterface<AirInterface> {

    public NewAirInterface() {
        super("net.minecraft.block.BlockAir", AirInterface.class);
    }
}