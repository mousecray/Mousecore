package ru.mousecray.testmod;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import ru.mousecray.testmod.core.proxy.CommonProxy;

@Mod(modid = TestMod.ID, version = TestMod.VERSION, name = TestMod.NAME)
public class TestMod {
    public static final String ID = "testmod";
    public static final String VERSION = "1.0";
    public static final String NAME = "Test mod";
    public static final String SERVER = "ru.mousecray.testmod.core.proxy.CommonProxy";
    public static final String CLIENT = "ru.mousecray.testmod.core.proxy.ClientProxy";

    @Mod.Instance(ID)
    public static TestMod instance;

    @SidedProxy(clientSide = CLIENT, serverSide = SERVER)
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
}