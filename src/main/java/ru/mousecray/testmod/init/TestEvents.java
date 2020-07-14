package ru.mousecray.testmod.init;

import net.minecraft.init.Items;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TestEvents {

    @SubscribeEvent
    public void onMapUnderwater(RenderSpecificHandEvent event) {
        if (event.getItemStack().getItem() == Items.FILLED_MAP) {
            event.setCanceled(true);
        }
    }
}