package ru.mousecray.testmod;

import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.mousecray.testmod.core.proxy.CommonProxy;
import ru.mousecray.testmod.mcore.AirInterface;

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
        Logger logger = LogManager.getLogger("testmod");
        if (Blocks.AIR instanceof AirInterface) {
            logger.log(Level.FATAL, "YEEEEEESSS!!!!!");
            AirInterface air = (AirInterface) Blocks.AIR;
            air.test();
            logger.log(Level.FATAL, "OU MAY GOD!!!!!");
        } else logger.log(Level.FATAL, "NOOOOOOOOO!!!!!");
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