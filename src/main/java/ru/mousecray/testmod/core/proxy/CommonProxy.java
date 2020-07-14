package ru.mousecray.testmod.core.proxy;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.mousecray.testmod.TestMod;
import ru.mousecray.testmod.TestUtils;
import ru.mousecray.testmod.core.ClassFieldSource;
import ru.mousecray.testmod.init.TestBlocks;
import ru.mousecray.testmod.init.TestEvents;
import ru.mousecray.testmod.init.TestItems;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

public class CommonProxy {
    protected List<Item> itemsToRegister = new LinkedList<>();
    protected List<Class<? extends TileEntity>> tilesToRegister = new LinkedList<>();
    protected List<Block> blocksToRegister = new LinkedList<>();

    public void preInit(FMLPreInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new TestEvents());

        // Registration Blocks
        new ClassFieldSource<Block>(TestBlocks.class).getAllFields().forEach(this::registerBlock);

        // Registration Items
        new ClassFieldSource<Item>(TestItems.class).getAllFields().forEach(this::registerItem);
    }

    public void init(FMLInitializationEvent event) {

    }

    public void postInit(FMLPostInitializationEvent event) {
        Logger logger = LogManager.getLogger("tbmine");
        Block block = Blocks.AIR;
        if (Blocks.AIR instanceof IClassTransformer) logger.log(Level.FATAL, "YEEEEEESSS!!!!!");
        else logger.log(Level.FATAL, "NOOOOOOOOO!!!!!");
    }

    private void registerBlock(Block block) {
        String name = TestUtils.getName(block);
        block.setRegistryName(name);
        block.setUnlocalizedName(name);

        if (block instanceof ITileEntityProvider) {
            Class<? extends TileEntity> tile = ((ITileEntityProvider) block).createNewTileEntity(null, 0).getClass();
            registerTile(tile);
        }

        blocksToRegister.add(block);
        registerItem(new ItemBlock(block), block.getRegistryName().toString());
    }

    private void registerTile(Class<? extends TileEntity> tile) {
        tilesToRegister.add(tile);
    }

    private void registerItem(@Nonnull Item item, String name) {
        item.setRegistryName(name);
        item.setUnlocalizedName(name);

        itemsToRegister.add(item);
    }

    private void registerItem(Item item) {
        registerItem(item, TestUtils.getName(item));
    }

    @SubscribeEvent
    public void registerBlocks(@Nonnull RegistryEvent.Register<Block> e) {
        blocksToRegister.forEach(e.getRegistry()::register);
        tilesToRegister.forEach(
                tile -> GameRegistry.registerTileEntity(tile, new ResourceLocation(TestMod.ID, tile.getSimpleName())));
    }

    @SubscribeEvent
    public void registerItems(@Nonnull RegistryEvent.Register<Item> e) {
        itemsToRegister.forEach(e.getRegistry()::register);
    }
}